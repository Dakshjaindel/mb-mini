package com.example.mbminicart.Controllers;


import com.example.mbminicart.DTOs.BasketAddDTO;
import com.example.mbminicart.DTOs.BasketFinalizeDTO;
import com.example.mbminicart.DTOs.CreditAddDTO;
import com.example.mbminicart.DTOs.NewBasketDTO;
import com.example.mbminicart.Entities.BasketItem;
import com.example.mbminicart.Services.BasketService;
import com.example.mbminiframework.Entity.AuthSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.example.mbminicustomer.ConfigsRepo.SessionRepo;

@RestController
public class BasketController {

    @Autowired
    private BasketService service;

    @Autowired
    private SessionRepo sessionRepo;

    @PostMapping({"/consumer/baskets"})
    public @ResponseBody String newBasket(@Valid @RequestBody NewBasketDTO newBasketDTO){
        return service.newBasket(newBasketDTO.getUserId(),newBasketDTO.getDate(),newBasketDTO.getFlag());
    }

    @PostMapping({"/consumer/credits"})
    public @ResponseBody String creditAdd(@Valid @RequestBody CreditAddDTO creditAddDTO, @RequestHeader("AuthKey") String authKey){
        AuthSession session = sessionRepo.findByAuthKey(authKey)
                .orElseThrow(() -> new RuntimeException("Invalid or expired AuthKey"));

        Long userId = session.getUserId();
        return service.customerAddCredit(userId,
                creditAddDTO.getCreditAmount(),
                creditAddDTO.getType(),
                creditAddDTO.getFlag());
    }

    @PostMapping({"/consumer/baskets/itemAdd"})
    public @ResponseBody String basketAdd(@Valid @RequestBody BasketAddDTO basketAddDTO, HttpServletRequest request){
        Long userId=(Long) request.getAttribute("userId");
        return service.BasketAdd(userId,
                basketAddDTO.getProductId(),
                basketAddDTO.getQuantity());
    }

    @PostMapping({"/consumer/baskets/finalize"})
    public @ResponseBody String finalizeBasket(@Valid @RequestBody BasketFinalizeDTO basketFinalizeDTO){
        return service.finalizeBasket(basketFinalizeDTO.getBasketId());
    }

}
