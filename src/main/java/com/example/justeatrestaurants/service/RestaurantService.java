package com.example.justeatrestaurants.service;

import com.example.justeatrestaurants.model.RestaurantDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service responsible for fetching and processing restaurant data
 * from the Just Eat public API.
 */
@Service
public class RestaurantService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Set of tags that are not considered cuisines.
     * These tags are filtered out from the list of cuisines.
     */
    private static final Set<String> NON_CUISINE_TAGS = Set.of(
            "Low Delivery Fee", "Deals", "Collect stamps", "Cheeky Tuesday",
            "Lunch", "Breakfast", "Freebies", "£8 off",
            "Shops", "Pharmacy", "All Night Alcohol", "Gifts", "Electronics",
            "Health and Beauty"
            // The division of tags is subjective and may change over time/after the feedback of product owner
    );


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
                List<String> rawCuisines = new ArrayList<>();
                for (JsonNode c : r.path("cuisines")) {
                    rawCuisines.add(c.path("name").asText());
                }

                // Filter out non-cuisine tags
                List<String> cuisines = rawCuisines.stream()
                        .filter(c -> !NON_CUISINE_TAGS.contains(c))
                        .collect(Collectors.toList());


                // Create DTO and add to the result list
                restaurants.add(new RestaurantDto(name, cuisines, rating, address));
            }

        } catch (Exception e) {
            System.out.println("Error parsing JSON file: " + e.getMessage());
        }

        return restaurants;
    }
}
