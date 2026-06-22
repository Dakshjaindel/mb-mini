package com.example.mbminicart.Services;


import com.example.mbmini.Entities.Catalog;
import com.example.mbmini.Services.JPAService;
import com.example.mbminicart.Entities.*;
import com.example.mbminicart.Repos.*;
import com.example.mbminicustomer.ConfigsRepo.NetCreditRepo;
import com.example.mbminicustomer.Entities.CustomerNetCredit;
import com.example.mbminicustomer.Services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class BasketService {

    @Autowired
    private LogRepo logRepo;

    @Autowired
    private BasketRepo basketRepo;

    @Autowired
    private ItemRepo itemRepo;

    @Autowired
    private com.example.mbminicustomer.ConfigsRepo.CreditRepo creditRepo;

    @Autowired
    private NetCreditRepo netCreditRepo;

    @Autowired
    private JPAService catalogService;

    @Autowired
    private CustomerService customerService;




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

}
