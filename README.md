# Just Eat Restaurant Viewer Console App

This is a simple Java console application built with Spring Boot. It fetches restaurant data from the Just Eat public API based on a UK postcode. It shows the top 10 restaurants and also saves the result in a text file.


---

## 💻 Why I Used This Stack

No specific language or framework was required in the assignment, so I chose **Java with Spring Boot** and **Gradle** for the following reasons:

- I’m confident working in this stack due to my skills and experience, and it’s widely used in production environments
- Spring Boot makes it easy to structure clean, testable, and maintainable code
- Gradle helps manage dependencies and build logic in a lightweight way
- Object Oriented Programming is requested in the job description.
- Java is nice for building modular applications that can scale or be extended later (such as turning this into a web app)

Even though this is a console app, I wanted to show how I would structure a real-world project with service layers, DTOs, input validation, file output, and a professional approach.

---

## ✅ What the App Does

- Connects to the Just Eat API using a UK postcode
- On startup, it fetches results for a **default hardcoded postcode**
- After that, the user can enter more postcodes or type `exit` to quit
- Shows the **top 10 restaurants** for each postcode, sorted descending by rating (NOTE: It does **not** return the top 10 highest-rated restaurants.)
- Displays:
    - Restaurant name
    - Cuisines
    - Rating (as a number or "Not Rated") (Colorful according to rating)
    - Address
- Saves the results to a `.txt` file inside a `FetchedRestaurants/` folder
- Validates that postcodes match the UK format

---

## 🚀 How to Build and Run the App

### Requirements

- Java 17 or higher (WARNING: Java 24 is not compatible with Gradle yet.)
- Gradle (or use the wrapper `./gradlew`)

### Steps

1. **Clone the repository:**

       git clone https://github.com/egemenyildiz3/restaurant-fetcher.git
       cd restaurant-fetcher

2. **Run the app:**

    for Windows:

       gradle bootRun 

    for MacOS/Linux:

        ./gradlew bootRun

3. **The app will first run with the default postcode:**

       EC4M7RF

4. **Then you can enter another UK postcode, or type:**

       exit

! Results will also be saved inside the `FetchedRestaurants/` folder.

---

## 🔍 API Used, Given by the Assessment Centre

    https://uk.api.just-eat.io/discovery/uk/restaurants/enriched/bypostcode/EC4M7RF

---

## 📝 Assumptions & Notes

- The goal was to clearly display the 4 required fields (name, cuisines, rating, address) in a readable way, not to build a graphical interface. The CLI format was chosen for speed and clarity.
- The `cuisines` field in the API contains both actual cuisines (like "Thai") and platform tags (like "Low Delivery Fee" or "Cheeky Tuesday"). This is how the API structures the data, and I've displayed it as provided.
- I assumed the API structure remains stable (field names don’t change).
- The app only uses the 4 required fields: name, cuisines, rating, address.
- The app handles missing ratings and special characters in API responses.
- Invalid or incorrectly formatted postcodes are rejected.
- By "proper displaying", I assumed it is meant

---

## 🛠️ Improvements I would Make With More Time

- Add automated tests for edge cases and postcode validation
- Add postcode autocomplete or suggestion feature
- Turn this into a web interface (simple form + results page)
- Cache or use the logs for repeated postcode queries to reduce API calls
- Allow user to choose file name or download results as CSV

---

## 📁 Folder & File Output

All result files are saved in:

    FetchedRestaurants/

File names are based on the postcode and time of the query.  
Example:

    restaurants_EC4M7RF_20250401_1633.txt

---

## 🤝 Contact

If you have any questions, feel free to reach out via egemenyildiz03@gmail.com.  

Thank you for reviewing my project!
