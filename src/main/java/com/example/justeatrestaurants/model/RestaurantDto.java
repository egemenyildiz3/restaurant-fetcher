package com.example.justeatrestaurants.model;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing a simplified view
 * of restaurant information fetched from the Just Eat API.
 */
public class RestaurantDto {

    /**
     * Name of the restaurant.
     */
    private final String name;

    /**
     * List of cuisine types offered by the restaurant.
     */
    private final List<String> cuisines;

    /**
     * Star rating of the restaurant (such as 4.5).
     */
    private final double rating;

    /**
     * Formatted address of the restaurant (such as "123 Street, SW1A 1AA").
     */
    private final String address;

    /**
     * Constructor for RestaurantDto.
     *
     * @param name     Restaurant name
     * @param cuisines List of cuisine types
     * @param rating   Star rating
     * @param address  Formatted address
     */
    public RestaurantDto(String name, List<String> cuisines, double rating, String address) {
        this.name = name;
        this.cuisines = cuisines;
        this.rating = rating;
        this.address = address;
    }

    /**
     * @return The name of the restaurant
     */
    public String getName() {
        return name;
    }

    /**
     * @return The list of cuisines the restaurant offers
     */
    public List<String> getCuisines() {
        return cuisines;
    }

    /**
     * @return The restaurant's star rating
     */
    public double getRating() {
        return rating;
    }

    /**
     * @return The formatted address of the restaurant
     */
    public String getAddress() {
        return address;
    }
}
