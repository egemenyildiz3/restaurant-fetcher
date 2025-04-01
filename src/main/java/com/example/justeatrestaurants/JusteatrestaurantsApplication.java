package com.example.justeatrestaurants;

import com.example.justeatrestaurants.model.RestaurantDto;
import com.example.justeatrestaurants.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.WebApplicationType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Main entry point for the Just Eat Restaurant Viewer Console App.
 * <p>
 * This application fetches and displays the top 10 restaurants from the Just Eat API
 * for a given UK postcode, printing name, cuisines, rating, and address to the console.
 */
@SpringBootApplication
public class JusteatrestaurantsApplication implements CommandLineRunner {

	/**
	 * Service that handles fetching and parsing restaurant data from the Just Eat API.
	 */
	@Autowired
	private RestaurantService restaurantService;

	/**
	 * Application entry point. Configured as a non-web Spring Boot app.
	 *
	 * @param args CLI arguments
	 */
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(JusteatrestaurantsApplication.class);
		app.setWebApplicationType(WebApplicationType.NONE); // Disable web server
		app.run(args);
	}

	/**
	 * Main console logic. Called automatically when the Spring Boot app starts.
	 * Fetches and prints the top 10 restaurants for a hardcoded postcode.
	 */
	@Override
	public void run(String[] args) {
		String postcode = "EC4M7RF"; // Example postcode from provided list
		List<RestaurantDto> restaurants = restaurantService.fetchRestaurants(postcode);

		System.out.println("Top 10 Restaurants:");
		int index = 1;

		for (RestaurantDto r : restaurants) {
			// Remove non-ASCII characters for clean console output
			String cleanedName = r.getName().replaceAll("[^\\x00-\\x7F]", "");
			String cleanedAddress = r.getAddress().replaceAll("[^\\x00-\\x7F]", "");
			String cleanedCuisines = r.getCuisines().stream()
					.map(c -> c.replaceAll("[^\\x00-\\x7F]", ""))
					.collect(Collectors.joining(", "));

			// Handle missing ratings
			String ratingStr = r.getRating() > 0 ? String.valueOf(r.getRating()) : "Not Rated";

			// Display restaurant info
			System.out.println(index++ + ". " + cleanedName);
			System.out.println("   Cuisines: " + cleanedCuisines);
			System.out.println("   Rating: " + ratingStr);
			System.out.println("   Address: " + cleanedAddress);
			System.out.println();
		}
	}
}
