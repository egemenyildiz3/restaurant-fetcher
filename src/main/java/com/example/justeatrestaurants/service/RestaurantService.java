package com.example.justeatrestaurants.service;

import com.example.justeatrestaurants.model.RestaurantDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for fetching and processing restaurant data
 * from the Just Eat public API.
 */
@Service
public class RestaurantService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Default constructor initializing RestTemplate.
     */
    public RestaurantService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Fetches restaurant data from the Just Eat API for a given UK postcode.
     * Parses and extracts the first 10 restaurants with relevant fields.
     *
     * @param postcode UK postcode (such as "EC4M7RF")
     * @return List of RestaurantDto objects with name, cuisines, rating, and address
     */
    public List<RestaurantDto> fetchRestaurants(String postcode) {
        String url = "https://uk.api.just-eat.io/discovery/uk/restaurants/enriched/bypostcode/" + postcode;
        String json = restTemplate.getForObject(url, String.class);

        List<RestaurantDto> restaurants = new ArrayList<>();

        try {
            // Parse the JSON response into a tree structure
            JsonNode root = objectMapper.readTree(json);
            JsonNode restaurantNodes = root.path("restaurants");

            // Limit to first 10 restaurants
            for (int i = 0; i < Math.min(10, restaurantNodes.size()); i++) {
                JsonNode r = restaurantNodes.get(i);

                // Extract restaurant name, rating, and address
                String name = r.path("name").asText();
                double rating = r.path("rating").path("starRating").asDouble();
                String address = r.path("address").path("firstLine").asText() + ", " +
                        r.path("address").path("postalCode").asText();

                // Extract list of cuisines
                List<String> cuisines = new ArrayList<>();
                for (JsonNode c : r.path("cuisines")) {
                    cuisines.add(c.path("name").asText());
                }

                // Create DTO and add to the result list
                restaurants.add(new RestaurantDto(name, cuisines, rating, address));
            }

        } catch (Exception e) {
            System.out.println("Error parsing JSON file: " + e.getMessage());
        }

        return restaurants;
    }
}
