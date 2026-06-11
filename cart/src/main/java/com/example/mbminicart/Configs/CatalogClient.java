package com.example.mbminicart.Configs;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;


@Service
public class CatalogClient {
    private final RestTemplate restTemplate;

    @Value("${catalog.service.url:http://localhost:8080}")
    private String catalogUrl;

    public CatalogClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getCatalogById(Long id) {
        return restTemplate.getForObject(catalogUrl + "/catalog/" + id, String.class);
    }

}
