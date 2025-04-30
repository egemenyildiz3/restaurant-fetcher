package com.example.justeatrestaurants;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.example.justeatrestaurants.controller.RestaurantController;
import com.fasterxml.jackson.databind.ObjectMapper;
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

	@Autowired
	private RestaurantController restaurantController;

	public enum SortType {
		NO_SORT,
		ASCENDING,
		DESCENDING
	}

	private static String choice = "cli";

	/**
	 * Main method to launch the application.
	 *
	 * @param args command-line arguments (not used)
	 */
	public static void main(String[] args) {
		// Disable the web server — only need a console app
		choice = choiceGetter();
		if (choice.equalsIgnoreCase("cli")) {
			// Run the CLI application
			SpringApplication app = new SpringApplication(JusteatrestaurantsApplication.class);
			app.setWebApplicationType(WebApplicationType.NONE);
			app.run(args);
		} else {
			// Run the web application
			SpringApplication.run(JusteatrestaurantsApplication.class, args);

		}

	}

	/**
	 * Runs the main application logic after Spring Boot starts.
	 * Fetches restaurants for a default postcode and starts user input loop.
	 *
	 * @param args command-line arguments (not used)
	 */
	@Override
	public void run(String[] args) {
		if (choice.equalsIgnoreCase("webapp")) {
			System.out.println("Web application is running. Visit http://localhost:8080/restaurants");
			return;
		}
		// Run the welcoming banner
		printBanner();

		// The default Postcode
		String defaultPostcode = "EC4M7RF";

		// Message for indication of the process
		System.out.println("Fetching restaurants for default postcode: " + defaultPostcode + "...");

		// Helper function for fetching the list from the API and presenting it in the CLI
		fetchAndDisplay(defaultPostcode,SortType.NO_SORT, 10, 0);

		// Additional functionality that allows users to enter their own queries
		handleUserInput();
	}

	/**
	 * Gets the user's choice of application type (CLI or Web).
	 *
	 * @return the user's choice
	 */
	private static String choiceGetter() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Choose the application type (cli/webapp): ");
        return scanner.nextLine().trim();
	}

	/**
	 * Fetches, displays, and saves the first 10 restaurants for the given postcode.
	 *
	 * @param postcode A valid UK postcode
	 */
    void fetchAndDisplay(String postcode, SortType sortType, int amount, double minRating) {
		// Obtain the restaurants
		List<RestaurantDto> restaurants = (List<RestaurantDto>) restaurantController.viewRestaurants(postcode, sortType, amount, minRating).getModel().get("restaurants");

		// Sort them by descending rating (Not all the restaurants, only the top 10 we consider)
		// restaurants.sort(Comparator.comparingDouble(RestaurantDto::getRating).reversed());

		if (restaurants.isEmpty()) {
			System.out.println("No restaurants found for postcode " + postcode.toUpperCase() + "\n");
			return;
		}

		System.out.println(BOLD + "\nHere is 10 Restaurants for Postcode " + postcode.toUpperCase() + ":" + RESET);

		int index = 1;
		// Add space for the indices that are powers of 10
		StringBuilder space = new StringBuilder();

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

			// Add a space if index gets a new digit, so that the name and the presented values are aligned.
			if (index == 10 || index == 100 || index == 1000) {
				space.append(" ");
			}

			// Display the information
			System.out.println(ORANGE + index++ + ". " + cleanedName + RESET);
			System.out.println(BOLD + space + "   Cuisines: " + RESET + cleanedCuisines);
			System.out.println(BOLD + space + "   Rating: " + RESET + ratingStr);
			System.out.println(BOLD + space + "   Address: " + RESET + cleanedAddress);
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

			System.out.print("Enter sort type (asc, desc, no sort): ");
			String sortTypeInput = scanner.nextLine().trim();
			SortType sortType = SortType.NO_SORT;
			if (sortTypeInput.equalsIgnoreCase("asc")) {
				sortType = SortType.ASCENDING;
			} else if (sortTypeInput.equalsIgnoreCase("desc")) {
				sortType = SortType.DESCENDING;
			}

			System.out.print("Enter how many restaurants you want to see: ");
			int amount = 10;
			try {
				amount = Integer.parseInt(scanner.nextLine().trim());
				if (amount < 1) {
					System.out.println("- Invalid number. Defaulting to 10.\n");
				}
			} catch (NumberFormatException e) {
				System.out.println("- Invalid number. Defaulting to 10.\n");
			}

			System.out.print("Enter minimum rating (0-5): ");
			double minRating = 0;
			try {
				minRating = Double.parseDouble(scanner.nextLine().trim());
				if (minRating < 0 || minRating > 5.0) {
					System.out.println("- Invalid rating. Defaulting to 0.\n");
				}
			} catch (NumberFormatException e) {
				System.out.println("- Invalid rating. Defaulting to 0.\n");
			}

			fetchAndDisplay(postcode, sortType, amount, minRating);
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
//			writer.println("Here is 10 Restaurants for Postcode " + postcode.toUpperCase() + ": ");
//
//			int index = 1;
//			// Add space for the indices that are powers of 10
//			StringBuilder space = new StringBuilder();
//
//			for (RestaurantDto r : restaurants) {
//				String name = r.getName().trim();
//				String address = r.getAddress()
//						.replaceAll(",", ", ")
//						.replaceAll("\\s+", " ")
//						.trim();
//				String cuisines = r.getCuisines().stream()
//						.map(String::trim)
//						.collect(Collectors.joining(", "));
//				String ratingStr = r.getRating() > 0 ? String.valueOf(r.getRating()) : "Not Rated";
//
//				writer.println(index++ + ". " + name);
//
//				// Add a space if index gets a new digit, so that the name and the presented values are aligned.
//				if (index == 10 || index == 100 || index == 1000) {
//					space.append(" ");
//				}
//
//				writer.println(space + "   Cuisines: " + cuisines);
//				writer.println(space + "   Rating: " + ratingStr);
//				writer.println(space + "   Address: " + address);
//				writer.println();
//			}
			String json = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(restaurants);
			writer.write(json);
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

