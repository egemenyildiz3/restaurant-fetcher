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
	private static final String BOLD = "\u001B[1m";
	private static final String GREEN = "\u001B[32m";
	private static final String RED = "\u001B[31m";
	private static final String YELLOW = "\u001B[33m";
	private static final String CYAN = "\u001B[36m";
	private static final String ORANGE = "\u001B[38;5;208m";

	@Autowired
	private RestaurantService restaurantService;

	/**
	 * Main method to launch the application.
	 *
	 * @param args command-line arguments (not used)
	 */
	public static void main(String[] args) {
		// Disable the web server — only need a console app
		SpringApplication app = new SpringApplication(JusteatrestaurantsApplication.class);
		app.setWebApplicationType(WebApplicationType.NONE);
		app.run(args);
	}

	/**
	 * Runs the main application logic after Spring Boot starts.
	 * Fetches restaurants for a default postcode and starts user input loop.
	 *
	 * @param args command-line arguments (not used)
	 */
	@Override
	public void run(String[] args) {
		// Run the welcoming banner
		printBanner();

		// The default Postcode
		String defaultPostcode = "EC4M7RF";

		// Message for indication of the process
		System.out.println("Fetching restaurants for default postcode: " + defaultPostcode + "...");

		// Helper function for fetching the list from the API and presenting it in the CLI
		fetchAndDisplay(defaultPostcode);

		// Additional functionality that allows users to enter their own queries
		handleUserInput();
	}

	/**
	 * Fetches, displays, and saves the top 10 restaurants for the given postcode.
	 *
	 * @param postcode A valid UK postcode
	 */
    void fetchAndDisplay(String postcode) {
		// Obtain the restaurants
		List<RestaurantDto> restaurants = restaurantService.fetchRestaurants(postcode);

		// Sort them by descending rating (Not all the restaurants, only the top 10 we consider)
		restaurants.sort(Comparator.comparingDouble(RestaurantDto::getRating).reversed());

		if (restaurants.isEmpty()) {
			System.out.println("No restaurants found for postcode " + postcode.toUpperCase() + "\n");
			return;
		}

		System.out.println("\nTop 10 Restaurants for the postcode " + postcode.toUpperCase() + ":");
		int index = 1;

		// Create the corresponding strings for displaying
		for (RestaurantDto r : restaurants) {
			String cleanedName = r.getName().replaceAll("[^\\x00-\\x7F]", "").trim();
			String cleanedAddress = r.getAddress()
					.replaceAll("[^\\x00-\\x7F]", "")
					.replaceAll(",", ", ")
					.replaceAll("\\s+", " ")
					.trim();
			String cleanedCuisines = r.getCuisines().stream()
					.map(c -> c.replaceAll("[^\\x00-\\x7F]", "").trim())
					.collect(Collectors.joining(", "));

			// Determine the color of the rating
			String ratingStr;
			if (r.getRating() >= 4.0) {
				ratingStr = GREEN + r.getRating() + RESET;
			} else if (r.getRating() >= 2.5) {
				ratingStr = CYAN  + r.getRating() + RESET;
			} else if (r.getRating() > 0) {
				ratingStr = RED + r.getRating() + RESET;
			} else {
				ratingStr = YELLOW + "[Not Rated]" + RESET;
			}

			// Display the information
			System.out.println(ORANGE + index++ + ". " + cleanedName + RESET);
			System.out.println("   Cuisines: " + cleanedCuisines);
			System.out.println("   Rating: " + ratingStr);
			System.out.println("   Address: " + cleanedAddress);
			System.out.println();
		}

		// Log the information obtained to a txt file
		saveToFile(restaurants, postcode);
	}


	/**
	 * Handles user interaction for entering postcodes and viewing restaurants.
	 * Continues to prompt the user until they type 'exit'.
	 */
    void handleUserInput() {
		Scanner scanner = new Scanner(System.in);

		// Loop that allows users to query as many postcodes as they wish
		while (true) {
			System.out.print("Enter a UK postcode (or type 'exit' to quit): ");
			String postcode = scanner.nextLine().trim();

			if (postcode.equalsIgnoreCase("exit")) {
				System.out.println("Goodbye");
				break;
			}

			if (!isValidPostcode(postcode)) {
				System.out.println("- Invalid postcode format. Try again.\n");
				continue;
			}

			fetchAndDisplay(postcode);
		}

		scanner.close();
	}

	/**
	 * Validates a UK postcode using a basic regex pattern.
	 *
	 * @param postcode the postcode to validate
	 * @return true if the postcode is valid, false otherwise
	 */
    boolean isValidPostcode(String postcode) {
		// Regex for the UK-based postcodes
		String regex = "^[A-Z]{1,2}[0-9][0-9A-Z]?\\s?[0-9][A-Z]{2}$";
		return postcode.toUpperCase().matches(regex);
	}

	/**
	 * Saves the list of restaurants to a timestamped text file inside the FetchedRestaurants folder.
	 *
	 * @param restaurants the list of restaurant results
	 * @param postcode    the postcode used to fetch the data
	 */
    void saveToFile(List<RestaurantDto> restaurants, String postcode) {
		File folder = new File("FetchedRestaurants");
		if (!folder.exists()) {
			folder.mkdir();
		}

		String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String safePostcode = postcode.replaceAll("\\s+", "");
		String filename = String.format("restaurants_%s_%s.txt", safePostcode.toUpperCase(), timestamp);
		File outputFile = new File(folder, filename);

		try (PrintWriter writer = new PrintWriter(outputFile)) {
			writer.println(BOLD + "Top 10 Restaurants for Postcode: " + postcode + RESET);
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
				writer.println(BOLD + "    Cuisines: " + cuisines + RESET);
				writer.println(BOLD + "    Rating: " + ratingStr + RESET);
				writer.println(BOLD + "    Address: " + address + RESET);
				writer.println();
			}

			System.out.println("Results saved to FetchedRestaurants/" + filename + "\n");
		} catch (IOException e) {
			System.out.println("Failed to save file: " + e.getMessage());
		}
	}

	/**
	 * Prints a banner with app title and instructions at startup.
	 */
    void printBanner() {
		String banner = "\n" + ORANGE + """ 
		==========================================
		! Welcome to TakeAway Restaurant Fetcher !
		==========================================
		""" + RESET;
		System.out.println(banner);
	}
}
