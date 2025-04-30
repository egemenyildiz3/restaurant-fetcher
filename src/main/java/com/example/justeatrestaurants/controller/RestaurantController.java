package com.example.justeatrestaurants.controller;

import com.example.justeatrestaurants.JusteatrestaurantsApplication;
import com.example.justeatrestaurants.model.RestaurantDto;
import com.example.justeatrestaurants.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @GetMapping("/restaurants")
    public ModelAndView viewRestaurants(
            @RequestParam(value = "postcode", defaultValue = "M17FA") String postcode,
            @RequestParam(value = "sortType", defaultValue = "NO_SORT") JusteatrestaurantsApplication.SortType sortType,
            @RequestParam(value = "amount", defaultValue = "10") int amount,
            @RequestParam(value = "minRating", defaultValue = "0") double minRating) {
        List<RestaurantDto> restaurants = restaurantService.fetchRestaurants(postcode, sortType, amount, minRating);
        ModelAndView modelAndView = new ModelAndView("restaurants");
        modelAndView.addObject("postcode", postcode);
        modelAndView.addObject("restaurants", restaurants);
        return modelAndView;
    }
}
