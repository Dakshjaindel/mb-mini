package com.example.mbminicart.Services;


import com.example.mbmini.Entities.Catalog;
import com.example.mbmini.Services.CatalogService;
import com.example.mbminicart.Entities.*;
import com.example.mbminicart.Repos.*;
import com.example.mbminicustomer.ConfigsRepo.CustomerRepo;
import com.example.mbminicustomer.ConfigsRepo.NetCreditRepo;
import com.example.mbminicustomer.Entities.CustomerNetCredit;
import com.example.mbminiframework.ORS.DTOs.MatrixServiceRequestDTO;
import com.example.mbminiframework.ORS.RouteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Service
public class BasketService {

    @Autowired
    private RouteService routeService;

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private LogRepo logRepo;

    @Autowired
    private BasketRepo basketRepo;

    @Value("${location.hub.lat}")
    private Double hubLat;

    @Value("${location.hub.long}")
    private Double hubLong;


    @Autowired
    private com.example.mbminicustomer.ConfigsRepo.CreditRepo creditRepo;

    @Autowired
    private NetCreditRepo netCreditRepo;

    @Autowired
    private CatalogService catalogService;


    public String itemUpdateTopic="basketDeplete";

    public String itemUpdateGroup="cartGroup";

    @Autowired
    private ItemRepo itemRepo;





    public String newBasket(Long userId, Date date,Integer flag){
        Basket basket = new Basket(userId,date,flag,0);

        basketRepo.save(basket);

        CustomerNetCredit customerNetCredit=new CustomerNetCredit(userId,BigDecimal.ZERO,flag);
        netCreditRepo.save(customerNetCredit);
        logRepo.save(new Log("new basket made with basketID "+ basket.getId()+"new CreditWallet made with walletId "+ customerNetCredit.getId()));
        return "new basket made with basketID "+ basket.getId();
    }

    public String BasketAdd(Long userId, Long productId, Integer quantity){

        Basket basket = basketRepo.findByUserId(userId)
                .orElseGet(() -> {
                    Basket newBasket = new Basket(userId, new Date(), 1, 0);
                    basketRepo.save(newBasket);
                    return newBasket;
                });
        Long basketId = basket.getId();

        Catalog catalog= catalogService.Get(productId);

        Integer totalCatalogInDemand= itemRepo.CatalogDemand(productId);
        if (quantity+totalCatalogInDemand>catalog.getQuantity()){
            throw new RuntimeException("Not enough Quantity in stock");
        }

        Long customerId= basket.getUserId();
        CustomerNetCredit netCredit = netCreditRepo.getCustomerNetCreditByCustomerId(customerId);
        BigDecimal totalCostOfWallet = itemRepo.sumWalletCostByBasketId(basketId);
        BigDecimal walletAmount =netCredit.getWalletCredit();
        BigDecimal productPrice = catalog.getPrice().multiply(BigDecimal.valueOf(quantity));
        if (totalCostOfWallet.add(productPrice).compareTo(walletAmount)>0){
            throw new RuntimeException(" Not enough wallet amount for the product/ quantity. Please recharge");
        }
        boolean itemExists= itemRepo.existsBasketItemsByBasketIdAndProductId(basketId,productId);
        if (itemExists){
            BasketItem item= itemRepo.findByBasketIdAndProductId(basketId,productId);
            if (quantity==0){
                basket.setQuantity(basket.getQuantity()-item.getQuantity());
                if (basket.getQuantity()==0){
                    basket.setFlag(0);
                }
                item.setQuantity(quantity);
                item.setFlag(0);
                itemRepo.save(item);
                basketRepo.save(basket);
                logRepo.save(new Log("Item removed from basket with basketId"+basket.getId()));
                return "Item removed from basket";
            }
            basket.setQuantity(basket.getQuantity()-(item.getQuantity()-quantity));
            item.setQuantity(quantity);
            basket.setFlag(1);
            basketRepo.save(basket);
            itemRepo.save(item);
            logRepo.save(new Log("Item quantity updated for itemId "+ item.getId()+" in the basket with basketId "+ basket.getId()));
            return "Item quantity updated to "+quantity;
        }
        else {
            BasketItem item =new BasketItem(basketId,productId, quantity,1);
            itemRepo.save(item);
            basket.setQuantity(basket.getQuantity()+quantity);
            basket.setFlag(1);
            basketRepo.save(basket);
            logRepo.save(new Log("Item added to basketId "+ basket.getId()+" with Id "+item.getId()));
            return  "new Item added to basket.";
        }



    }


    @Transactional
    public String customerAddCredit(Long customerId, BigDecimal creditAmount,String type,Integer flag ){
        com.example.mbminicustomer.Entities.Credits credits=new com.example.mbminicustomer.Entities.Credits(customerId,creditAmount,type,flag);
        creditRepo.save(credits);
        CustomerNetCredit netCredit= netCreditRepo.getCustomerNetCreditByCustomerId(customerId);
        if (netCredit == null) {
            throw new RuntimeException("No wallet found for customerId: " + customerId);
        }
        BigDecimal newCredit=netCredit.getWalletCredit().add(creditAmount);
        netCredit.setWalletCredit(newCredit);
        netCreditRepo.save(netCredit);
        logRepo.save(new Log("Credit added to userId " + customerId + "'s wallet, of value "+ creditAmount.toString()));
        return "Credit added to userId " + customerId + "'s wallet, and added to net credit";
    }

    public String finalizeBasket(Long basketId){
        Basket basket= basketRepo.getBasketById(basketId);
        if (basket.getFlag() != null && basket.getFlag() == 2){
            throw new RuntimeException("Basket already finalized");
        }
        if (basket.getQuantity() == null || basket.getQuantity() <= 0){
            throw new RuntimeException("Basket is empty");
        }

        List<BasketItem> items = itemRepo.findByBasketIdAndFlag(basketId, 1);
        if (items.isEmpty()){
            throw new RuntimeException("No active items in basket");
        }

        Long customerId = basket.getUserId();
        CustomerNetCredit netCredit = netCreditRepo.getCustomerNetCreditByCustomerId(customerId);
        BigDecimal total = BigDecimal.ZERO;

        for (BasketItem item : items){
            Catalog catalog = catalogService.Get(item.getProductId());
            total = total.add(catalog.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        if (netCredit.getWalletCredit().compareTo(total) < 0){
            throw new RuntimeException("Insufficient wallet balance to finalize order");
        }

        for (BasketItem item : items){
            Catalog catalog = catalogService.Get(item.getProductId());
            if (catalog.getQuantity() < item.getQuantity()){
                throw new RuntimeException("Product " + catalog.getProductName() + " is out of stock");
            }
            catalogService.Update(
                    catalog.getId(),
                    null,
                    catalog.getQuantity() - item.getQuantity(),
                    null,
                    null
            );
        }

        netCredit.setWalletCredit(netCredit.getWalletCredit().subtract(total));
        netCreditRepo.save(netCredit);
        basket.setFlag(2);
        basketRepo.save(basket);

        for (BasketItem item : items){
            item.setFlag(2);
            itemRepo.save(item);
        }

        logRepo.save(new Log("Order finalized for basketId " + basketId + " total " + total));
        return "Order finalized successfully. Total charged: " + total;
    }

    public String itemQuantityUpdate(com.example.mbminiframework.Entity.CatalogQuantityUpdateDTO payload) {

        if (payload == null) {
            return "No event found in Kafka, nothing to process ";
        }
        Long productId = payload.getProductId();
        Integer newQty = payload.getQuantity();
        List<BasketItem> items=itemRepo.findAllByProductIdAndFlagOrderByCreatedAt(productId,1);
        Integer sumOfItemDemand=itemRepo.findAllByProductIdAndFlagOrderByCreatedAt(productId,1).stream().map(BasketItem::getQuantity).mapToInt(Integer::intValue).sum();

        for (BasketItem item : items){
            if (sumOfItemDemand<=newQty){
                break;
            }
            sumOfItemDemand=sumOfItemDemand-item.getQuantity();
            item.setFlag(0);
            itemRepo.save(item);
        }
        return "Baskets updated for new quantity";

    }

    public List<List<Double>> optimalRoute(Date currDate){
        List<Basket> baskets=basketRepo.findByDateAndFlag(currDate,1);
        log.info("Baksets{}; ",baskets.size());
        List<Long> userIds= baskets.stream().map(Basket::getUserId).toList();
        log.info("UserIds: {}", userIds);
        List<Double> hub=List.of(hubLong,hubLat);
        List<List<Double>> positions= new java.util.ArrayList<>(customerRepo.findAllByIdIn(userIds).stream().map(customer -> List.of(customer.getLongitude(), customer.getLatitude())).toList());
        log.info("Customer positions before hub: {}", positions);
        positions.add(0,hub);
        log.info("Final positions: {}", positions);

        int totalSize= positions.size();
        List<List<Double>> globalDistanceMatrix=new ArrayList<>(totalSize);



        MatrixServiceRequestDTO input=new MatrixServiceRequestDTO(positions,IntStream.range(0, positions.size()).boxed().toList(), IntStream.range(0, positions.size()).boxed().toList(),List.of("distance"));
        List<List<Double>> res= routeService.vrpSolve(input,positions);
        log.info("returning result:{} ", res);
        return res;

    }

    public List<List<Double>> optimalRoute2(Date currDate) {
        List<Basket> baskets = basketRepo.findByDateAndFlag(currDate, 1);
        log.info("Baskets size: {}", baskets.size());

        List<Long> userIds = baskets.stream().map(Basket::getUserId).toList();
        List<Double> hub = List.of(hubLong, hubLat);

        List<List<Double>> positions = new ArrayList<>(
                customerRepo.findAllByIdIn(userIds).stream()
                        .map(customer -> List.of(customer.getLongitude(), customer.getLatitude()))
                        .toList()
        );
        positions.add(0, hub); // Depot/Hub is at index 0

        int totalSize = positions.size();
        log.info("Total positions (Hub + Customers): {}", totalSize);

        // 1. Initialize a full-sized 2D array matching your final matrix size
        Double[][] masterMatrix = new Double[totalSize][totalSize];

        // 2. Define your chunk threshold safely under your 58-element limit
        final int LIMIT = 50;

        // 3. Loop through rows (origins) and columns (destinations) in chunks
        for (int rowStart = 0; rowStart < totalSize; rowStart += LIMIT) {
            int rowEnd = Math.min(rowStart + LIMIT, totalSize);
            List<Integer> originsIndices = IntStream.range(rowStart, rowEnd).boxed().toList();

            for (int colStart = 0; colStart < totalSize; colStart += LIMIT) {
                int colEnd = Math.min(colStart + LIMIT, totalSize);
                List<Integer> destinationsIndices = IntStream.range(colStart, colEnd).boxed().toList();

                log.info("Fetching sub-matrix chunk: Origins {}-{} | Destinations {}-{}",
                        rowStart, rowEnd - 1, colStart, colEnd - 1);

                // Construct the request DTO targeting ONLY this specific sub-grid quadrant
                MatrixServiceRequestDTO chunkRequest = new MatrixServiceRequestDTO(
                        positions,
                        originsIndices,
                        destinationsIndices,
                        List.of("distance")
                );

                // Fetch the small sub-matrix block from ORS
                List<List<Double>> subMatrixResponse = routeService.fetchMatrixBlock(chunkRequest);

                // 4. Stitch the block response into its exact position inside the master grid
                for (int i = 0; i < originsIndices.size(); i++) {
                    int globalRow = originsIndices.get(i);
                    List<Double> responseRow = subMatrixResponse.get(i);

                    for (int j = 0; j < destinationsIndices.size(); j++) {
                        int globalCol = destinationsIndices.get(j);
                        Double val = responseRow.get(j);

                        // Handle null values to safeguard the VRP math solvers
                        masterMatrix[globalRow][globalCol] = (val == null) ? 0.0 : val;
                    }
                }
            }
        }

        // 5. Send the complete assembled 2D array directly to the solver
        List<List<Double>> res = routeService.vrpSolve2(masterMatrix, positions);
        log.info("Returning final optimized results: {}", res);
        return res;
    }





}
