package com.example.justeatrestaurants.service;

import com.example.justeatrestaurants.JusteatrestaurantsApplication;
import com.example.justeatrestaurants.model.RestaurantDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
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

    public List<RestaurantDto> fetchRestaurants(String postcode, JusteatrestaurantsApplication.SortType sortType) {
        List<RestaurantDto> list = new ArrayList<>();

        String url = "https://uk.api.just-eat.io/discovery/uk/restaurants/enriched/bypostcode/" + postcode;
        String json = restTemplate.getForObject(url, String.class);

        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode restaurants = root.path("restaurants");

            for (JsonNode r : restaurants) {
                String name = r.path("name").asText();
                double rating = r.path("rating").path("starRating").asDouble();
                String address = r.path("address").path("firstLine").asText() + ", " +
                        r.path("address").path("postalCode").asText();
                List<String> rawCuisines = new ArrayList<>();
                for (JsonNode c : r.path("cuisines")) {
                    rawCuisines.add(c.path("name").asText());
                }
                List<String> cuisines = rawCuisines.stream().filter(c -> !NON_CUISINE_TAGS.contains(c)).toList();

                list.add(new RestaurantDto(name, cuisines, rating, address));

            }
        }
        catch (Exception e) {
            System.out.println("Error" + e.getMessage());
        }

        if (sortType == JusteatrestaurantsApplication.SortType.ASCENDING) {
            list.sort(Comparator.comparingDouble(RestaurantDto::getRating));
        }
        else if (sortType == JusteatrestaurantsApplication.SortType.DESCENDING) {
            list.sort(Comparator.comparingDouble(RestaurantDto::getRating).reversed());
        }

        return list;
    }
}
