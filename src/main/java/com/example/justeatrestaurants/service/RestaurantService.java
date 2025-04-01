package com.example.justeatrestaurants.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestaurantService {

    private final RestTemplate restTemplate;

    public RestaurantService() {
        this.restTemplate = new RestTemplate();
    }

    public String fetchRestaurantsByZipcode(String postcode) {
        String url = "https://uk.api.just-eat.io/discovery/uk/restaurants/enriched/bypostcode/" + postcode;
        return restTemplate.getForObject(url, String.class);
    }
}