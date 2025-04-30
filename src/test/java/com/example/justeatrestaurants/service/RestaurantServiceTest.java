package com.example.justeatrestaurants.service;

import com.example.justeatrestaurants.JusteatrestaurantsApplication;
import com.example.justeatrestaurants.model.RestaurantDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantServiceTest {

    private final RestaurantService service = new RestaurantService();

    @Test
    void testFetchRestaurants_returnsResults() {
        String testPostcode = "EC4M7RF"; // reliable postcode
        JusteatrestaurantsApplication.SortType sortType = JusteatrestaurantsApplication.SortType.NO_SORT;
        int amount = 10; // limit to 10 results
        double minRating = 0.0; // no minimum rating

        List<RestaurantDto> restaurants = service.fetchRestaurants(testPostcode, sortType, amount, minRating);

        assertNotNull(restaurants);
        assertFalse(restaurants.isEmpty(), "Expected non-empty restaurant list");
        assertTrue(restaurants.size() <= amount, "Should not return more than 10 restaurants");

        for (RestaurantDto r : restaurants) {
            assertNotNull(r.getName());
            assertNotNull(r.getAddress());
            assertTrue(r.getRating() >= minRating, "Rating should be non-negative");

        }
    }

    @Test
    void testFetchRestaurants_invalidPostcodeReturnsEmpty() {
        String invalidPostcode = "INVALID123";

        List<RestaurantDto> restaurants = service.fetchRestaurants(invalidPostcode,
                JusteatrestaurantsApplication.SortType.NO_SORT, 10, 0.0);

        assertNotNull(restaurants, "List should not be null");
        assertTrue(restaurants.isEmpty(), "Expected empty list for invalid postcode");
    }

}
