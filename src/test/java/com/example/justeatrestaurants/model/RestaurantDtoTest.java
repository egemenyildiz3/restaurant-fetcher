package com.example.justeatrestaurants.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantDtoTest {

    @Test
    void testRestaurantDtoFields() {
        // Arrange
        String name = "Testaurant";
        List<String> cuisines = Arrays.asList("Italian", "Pizza");
        double rating = 4.3;
        String address = "123 Test St, SW1A 1AA";

        // Act
        RestaurantDto dto = new RestaurantDto(name, cuisines, rating, address);

        // Assert
        assertEquals(name, dto.getName());
        assertEquals(cuisines, dto.getCuisines());
        assertEquals(rating, dto.getRating());
        assertEquals(address, dto.getAddress());
    }
}
