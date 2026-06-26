package com.example.mbminicart;

import com.example.mbminicart.Services.BasketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartKafkaConsumer {

    private final BasketService basketService;

    @KafkaListener(topics = "basketDeplete", groupId = "cartGroup")
    public void onQuantityUpdate(com.example.mbminiframework.Entity.CatalogQuantityUpdateDTO payload) {
        log.info("Kafka event received: {}", payload);
        basketService.itemQuantityUpdate(payload);
    }

}