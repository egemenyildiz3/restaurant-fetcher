package com.example.justeatrestaurants;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.justeatrestaurants.model.RestaurantDto;
import com.example.justeatrestaurants.service.RestaurantService;

/**
 * Entry point for the Just Eat Restaurant Viewer Console Application.
 * This app fetches restaurant data by UK postcode using the Just Eat API.
 */
@SpringBootApplication
public class JusteatrestaurantsApplication implements CommandLineRunner {

	// Color codes for CLI
	private static final String RESET = "\u001B[0m";
	private static final String GREEN = "\u001B[32m";
	private static final String RED = "\u001B[31m";
	private static final String YELLOW = "\u001B[33m";
	private static final String CYAN = "\u001B[36m";
	private static final String WHITE_BOLD = "\u001B[1;37m";

	@Autowired
	private RestaurantService restaurantService;

	public static void main(String[] args) {
		// Disable the web server — only need a console app
		SpringApplication app = new SpringApplication(JusteatrestaurantsApplication.class);
		app.setWebApplicationType(WebApplicationType.NONE);
		app.run(args);

	}

	@Override
	public void run(String[] args) {
		//print the welcome text
		printBanner();

		// First run with hardcoded default postcode
		String defaultPostcode = "EC4M7RF";
		System.out.println("Fetching restaurants for default postcode: " + defaultPostcode);
		fetchAndDisplay(defaultPostcode);  // Fetch and display for default postcode

		// Interactive Postcode Search
		handleUserInput();

	}

	private void handleUserInput() {
		// Create a Scanner instance outside the loop to avoid opening/closing repeatedly
		Scanner scanner = new Scanner(System.in);

		// Loop for additional searches
		while (true) {
			// Ask the user to input a postcode or exit
			System.out.print("Enter a UK postcode (or type 'exit' to quit): ");

			String postcode = scanner.nextLine().trim();  // Read postcode from user

			if (postcode.equalsIgnoreCase("exit")) {
				System.out.println("Goodbye");
				break;  // Exit the loop if the user wants to quit
			}

			// Validate UK postcode format
			if (!isValidPostcode(postcode)) {
				System.out.println("- Invalid postcode format. Try again.\n");
				continue;  // Ask again if the postcode format is invalid
			}

			// Fetch and display results for the valid postcode
			fetchAndDisplay(postcode);
		}

		// Close the scanner after the loop ends to free up resources
		scanner.close();
	}


	/**
	 * Fetches, displays, and saves the top 10 restaurants for the given postcode.
	 *
	 * @param postcode A valid UK postcode
	 */
	private void fetchAndDisplay(String postcode) {

		// Fetch restaurants and sort by rating (highest first)
		List<RestaurantDto> restaurants = restaurantService.fetchRestaurants(postcode);
		restaurants.sort(Comparator.comparingDouble(RestaurantDto::getRating).reversed());

		if (restaurants.isEmpty()) {
			System.out.println("No restaurants found for postcode " + postcode + "\n");
			return;
		}

		// Print the restaurant list to the console
		System.out.println("\nTop 10 Restaurants for the postcode " + postcode + ": ");
		int index = 1;
		for (RestaurantDto r : restaurants) {
			// Clean and format output fields
			String cleanedName = r.getName().replaceAll("[^\\x00-\\x7F]", "").trim();
			String cleanedAddress = r.getAddress()
					.replaceAll("[^\\x00-\\x7F]", "")
					.replaceAll(",", ", ")
					.replaceAll("\\s+", " ")
					.trim();
			String cleanedCuisines = r.getCuisines().stream()
					.map(c -> c.replaceAll("[^\\x00-\\x7F]", "").trim())
					.collect(Collectors.joining(", "));
			String ratingStr;
			// Color-based rating displaying
			if (r.getRating() >= 4.0) {
				ratingStr = GREEN + r.getRating() + RESET;
			} else if (r.getRating() > 0 && r.getRating() < 2.5) {
				ratingStr = RED + r.getRating() + RESET;
			} else if (r.getRating() == 0.0) {
				ratingStr = YELLOW + "Not Rated" + RESET;
			} else {
				ratingStr = CYAN + r.getRating() + RESET;
			}


			// Display each restaurant
			System.out.println(WHITE_BOLD + index++ + ". " + cleanedName + RESET);
			System.out.println("   Cuisines: " + cleanedCuisines);
			System.out.println("   Rating: " + ratingStr);
			System.out.println("   Address: " + cleanedAddress);
			System.out.println();
		}

		// Save the same results to a file
		saveToFile(restaurants, postcode);
	}

	/**
	 * Validates a UK postcode using a basic regex.
	 *
	 * @param postcode the postcode to validate
	 * @return true if valid, false otherwise
	 */
	private boolean isValidPostcode(String postcode) {
		String regex = "^[A-Z]{1,2}[0-9][0-9A-Z]?\\s?[0-9][A-Z]{2}$";
		return postcode.toUpperCase().matches(regex);
	}

	/**
	 * Writes the restaurant list to a local file called restaurants.txt
	 *
	 * @param restaurants the list of restaurant results
	 * @param postcode    the postcode used to fetch the data
	 */
	private void saveToFile(List<RestaurantDto> restaurants, String postcode) {
		// Create folder if it doesn't exist
		File folder = new File("FetchedRestaurants");
		if (!folder.exists()) {
			folder.mkdir();
		}

		// Build filename: restaurants_EC4M7RF_20250401_1842.txt
		String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String safePostcode = postcode.replaceAll("\\s+", "");
		String filename = String.format("restaurants_%s_%s.txt", safePostcode.toUpperCase(), timestamp);
		File outputFile = new File(folder, filename);

		try (PrintWriter writer = new PrintWriter(outputFile)) {
			writer.println("Top 10 Restaurants for Postcode: " + postcode);
			int index = 1;

			for (RestaurantDto r : restaurants) {
				String name = r.getName().trim();
				String address = r.getAddress()
						.replaceAll(",", ", ")
						.replaceAll("\\s+", " ")
						.trim();
				String cuisines = r.getCuisines().stream()
						.map(String::trim)
						.collect(Collectors.joining(", "));
				String ratingStr = r.getRating() > 0 ? String.valueOf(r.getRating()) : "Not Rated";

				writer.println(index++ + ". " + name);
				writer.println("   Cuisines: " + cuisines);
				writer.println("   Rating: " + ratingStr);
				writer.println("   Address: " + address);
				writer.println();
			}

			System.out.println("+ Results saved to FetchedRestaurants/" + filename + "\n");
		} catch (IOException e) {
			System.out.println("- Failed to save file: " + e.getMessage());
		}
	}


	private void printBanner() {
		String banner = """
		===========================================
		     ! Welcome to TakeAway Restaurant Fetcher !
		===========================================
		Fetch top-rated restaurants in the UK by postcode.
		""";
		System.out.println(banner);
	}

}
