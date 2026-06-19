package com.example.mbminicart.Controllers;


import com.example.mbminicart.DTOs.BasketAddDTO;
import com.example.mbminicart.DTOs.BasketFinalizeDTO;
import com.example.mbminicart.DTOs.CreditAddDTO;
import com.example.mbminicart.DTOs.NewBasketDTO;
import com.example.mbminicart.Entities.BasketItem;
import com.example.mbminicart.Services.BasketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BasketController {

    @Autowired
    private BasketService service;

    @PostMapping({"/consumer/baskets"})
    public @ResponseBody String newBasket(@Valid @RequestBody NewBasketDTO newBasketDTO){
        return service.newBasket(newBasketDTO.getUserId(),newBasketDTO.getDate(),newBasketDTO.getFlag());
    }

    @PostMapping({"/consumer/credits"})
    public @ResponseBody String creditAdd(@Valid @RequestBody CreditAddDTO creditAddDTO){
        return service.customerAddCredit(creditAddDTO.getCustomerId(),
                creditAddDTO.getCreditAmount(),
                creditAddDTO.getType(),
                creditAddDTO.getFlag());
    }

    @PostMapping({"/consumer/baskets/itemAdd"})
    public @ResponseBody String basketAdd(@Valid @RequestBody BasketAddDTO basketAddDTO){
        return service.BasketAdd(basketAddDTO.getBasketId(),
                basketAddDTO.getProductId(),
                basketAddDTO.getQuantity());
    }

    @PostMapping({"/consumer/baskets/finalize"})
    public @ResponseBody String finalizeBasket(@Valid @RequestBody BasketFinalizeDTO basketFinalizeDTO){
        return service.finalizeBasket(basketFinalizeDTO.getBasketId());
    }

}
