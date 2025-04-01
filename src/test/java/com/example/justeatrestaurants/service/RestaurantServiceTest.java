package com.example.justeatrestaurants.service;

import com.example.justeatrestaurants.model.RestaurantDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantServiceTest {

    private final RestaurantService service = new RestaurantService();

    @Test
    void testFetchRestaurants_returnsResults() {
        String testPostcode = "EC4M7RF"; // reliable postcode
        List<RestaurantDto> restaurants = service.fetchRestaurants(testPostcode);

        assertNotNull(restaurants);
        assertFalse(restaurants.isEmpty(), "Expected non-empty restaurant list");
        assertTrue(restaurants.size() <= 10, "Should not return more than 10 restaurants");

        for (RestaurantDto r : restaurants) {
            assertNotNull(r.getName());
            assertNotNull(r.getAddress());
        }
    }

    @Test
    void testFetchRestaurants_invalidPostcodeReturnsEmpty() {
        String invalidPostcode = "INVALID123";

        List<RestaurantDto> restaurants = service.fetchRestaurants(invalidPostcode);

        assertNotNull(restaurants, "List should not be null");
        assertTrue(restaurants.isEmpty(), "Expected empty list for invalid postcode");
    }

}
