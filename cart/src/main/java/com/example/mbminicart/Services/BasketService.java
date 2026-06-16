package com.example.mbminicart.Services;


import com.example.mbmini.Entities.Catalog;
import com.example.mbmini.Services.JPAService;
import com.example.mbminicart.Configs.CatalogClient;
import com.example.mbminicart.Configs.CustomerClient;
import com.example.mbminicart.Entities.*;
import com.example.mbminicart.Repos.*;
import com.example.mbminicustomer.Services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.Date;

@Service
public class BasketService {

    private final LogRepo logRepo;
    private final BasketRepo basketRepo;
    private final ItemRepo itemRepo;
    private final CreditRepo creditRepo;
    private final NetCreditRepo netCreditRepo;

    private final JPAService catalogService;

    private final CustomerService customerService;

    public BasketService(BasketRepo repository, BasketRepo basketRepo, ItemRepo itemRepo, CatalogClient catalogClient, CustomerClient customerClient, LogRepo logRepo, CreditRepo creditRepo, NetCreditRepo netCreditRepo, JPAService catalogService, CustomerService customerService) {
        this.basketRepo = basketRepo;
        this.itemRepo = itemRepo;
        this.logRepo = logRepo;
        this.creditRepo = creditRepo;
        this.netCreditRepo = netCreditRepo;
        this.catalogService = catalogService;
        this.customerService = customerService;
    }

    public String newBasket(Long userId, Date date,Integer flag){
        Basket basket = new Basket(userId,date,flag,0);

        basketRepo.save(basket);

        CustomerNetCredit customerNetCredit=new CustomerNetCredit(userId,BigDecimal.ZERO,flag);
        netCreditRepo.save(customerNetCredit);
        logRepo.save(new Log("new basket made with basketID "+ basket.getId()+"new CreditWallet made with walletId "+ customerNetCredit.getId()));
        return "new basket made with basketID "+ basket.getId();
    }

    public String BasketAdd(Long basketId, Long productId, Integer quantity){
        Catalog catalog= catalogService.Get(productId);
        if (catalog.getQuantity()<quantity){
            throw new RuntimeException("Quantity asked is not in stock");
        }
        Basket basket= basketRepo.getBasketById(basketId);
        Long customerId= basket.getUserId();
        CustomerNetCredit netCredit = netCreditRepo.getCustomerNetCreditByCustomerId(customerId);
        BigDecimal walletAmount =netCredit.getWalletCredit();
        BigDecimal productprice = catalog.getPrice().multiply(BigDecimal.valueOf(quantity));
        if (walletAmount.compareTo(productprice)<0){
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


    public String customerAddCredit(Long customerId, BigDecimal creditAmount,String type,Integer flag ){
        Credits credits=new Credits(customerId,creditAmount,type,flag);
        creditRepo.save(credits);
        CustomerNetCredit netCredit= netCreditRepo.getCustomerNetCreditByCustomerId(customerId);
        BigDecimal newCredit=netCredit.getWalletCredit().add(creditAmount);
        netCredit.setWalletCredit(newCredit);
        netCreditRepo.save(netCredit);
        logRepo.save(new Log("Credit added to userId " + customerId + "'s wallet, of value "+ creditAmount.toString()));
        return "Credit added to userId " + customerId + "'s wallet, and added to net credit";
    }




}
