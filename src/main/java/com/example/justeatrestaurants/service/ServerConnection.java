package com.example.justeatrestaurants.service;

import org.springframework.web.client.RestTemplate;

public class ServerConnection {
    private RestTemplate restTemplate;

    public ServerConnection() {
        this.restTemplate = new RestTemplate();
    }

    public String getServerResponse(String url) {
        String json = restTemplate.getForObject(url, String.class);
        return json;
    }
}
