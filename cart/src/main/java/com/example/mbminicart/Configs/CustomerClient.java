package com.example.mbminicart.Configs;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CustomerClient {
    private final RestTemplate restTemplate;

    @Value("${customer.service.url:http://localhost:8081}")
    private String customerUrl;

    public CustomerClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getCatalogById(Long id) {
        return restTemplate.getForObject(customerUrl + "/data/" + id, String.class);
    }

}
