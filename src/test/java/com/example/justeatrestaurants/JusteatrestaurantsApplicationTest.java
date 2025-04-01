package com.example.justeatrestaurants;

import com.example.justeatrestaurants.model.RestaurantDto;
import com.example.justeatrestaurants.service.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JusteatrestaurantsApplicationTest {

    @Mock
    private RestaurantService restaurantService;

    @InjectMocks
    private JusteatrestaurantsApplication app;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIsValidPostcode_validOnes() {
        assertTrue(app.isValidPostcode("EC4M7RF"));
        assertTrue(app.isValidPostcode("W1A 1AA"));
        assertTrue(app.isValidPostcode("SW1A1AA"));
    }

    @Test
    void testIsValidPostcode_invalidOnes() {
        assertFalse(app.isValidPostcode("123"));
        assertFalse(app.isValidPostcode("INVALID"));
        assertFalse(app.isValidPostcode("ABCDE"));
    }

    @Test
    void testFetchAndDisplay_withResults() {
        List<RestaurantDto> mockList = new ArrayList<>(List.of(
                new RestaurantDto("Pizza Palace", List.of("Italian", "Pizza"), 4.5, "123 Food St, London"),
                new RestaurantDto("Burger Hub", List.of("American", "Burgers"), 3.2, "456 Meal Rd, London")
        ));

        when(restaurantService.fetchRestaurants("EC4M7RF")).thenReturn(mockList);

        app.fetchAndDisplay("EC4M7RF");

        verify(restaurantService).fetchRestaurants("EC4M7RF");
    }


    @Test
    void testFetchAndDisplay_withNoResults() {
        List<RestaurantDto> emptyList = new ArrayList<>();
        when(restaurantService.fetchRestaurants("EMPTY")).thenReturn(emptyList);

        app.fetchAndDisplay("EMPTY");

        verify(restaurantService).fetchRestaurants("EMPTY");
    }

    @Test
    void testSaveToFile_createsExpectedFile() throws IOException {
        // Arrange
        List<RestaurantDto> mockList = List.of(
                new RestaurantDto("Taco Town", List.of("Mexican"), 4.2, "55 Taco Blvd")
        );
        String testPostcode = "TT1 1TT";

        // Act
        app.saveToFile(mockList, testPostcode);

        // Assert
        File folder = new File("FetchedRestaurants");
        assertTrue(folder.exists() && folder.isDirectory());

        File[] files = folder.listFiles((dir, name) -> name.startsWith("restaurants_TT11TT"));
        assertNotNull(files);
        assertTrue(files.length > 0);

        // Optional: read and assert content
        String content = new String(java.nio.file.Files.readAllBytes(files[0].toPath()));
        assertTrue(content.contains("Taco Town"));
        assertTrue(content.contains("Mexican"));
        assertTrue(content.contains("4.2"));
    }

    @Test
    void testPrintBanner_printsExpectedText() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        app.printBanner();

        String output = out.toString();
        assertTrue(output.contains("Welcome to TakeAway Restaurant Fetcher"));
    }

    @Test
    void testHandleUserInput_validPostcodeThenExit() {
        String simulatedInput = "W1A1AA\nexit\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        List<RestaurantDto> mockList = new ArrayList<>(List.of(
                new RestaurantDto("Mock Diner", List.of("British"), 4.0, "123 Mock Street")
        ));

        when(restaurantService.fetchRestaurants("W1A1AA")).thenReturn(mockList);

        app.handleUserInput();

        verify(restaurantService).fetchRestaurants("W1A1AA");
    }

    @Test
    void testHandleUserInput_invalidThenValidThenExit() {
        String simulatedInput = "INVALIDPOSTCODE\nEC4M7RF\nexit\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        List<RestaurantDto> mockList = new ArrayList<>(List.of(
                new RestaurantDto("Mock Spot", List.of("Indian"), 3.9, "1 Curry Ln")
        ));
        when(restaurantService.fetchRestaurants("EC4M7RF")).thenReturn(mockList);

        app.handleUserInput();

        verify(restaurantService).fetchRestaurants("EC4M7RF");
    }




}
