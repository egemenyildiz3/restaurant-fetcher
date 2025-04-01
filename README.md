# Just Eat Restaurant Viewer Console App

This is a Java console application built with Spring Boot. It fetches restaurant data from the Just Eat public API based on a UK postcode. It shows the top 10 restaurants and also saves the result in a text file.


---

## 💻 Why I Used This Stack

No specific language or framework was required in the assignment, so I chose **Java with Spring Boot** and **Gradle** for the following reasons:

- Spring Boot makes it easy to structure clean, testable, and maintainable code
- Gradle helps manage dependencies and build logic in a lightweight way
- Object Oriented Programming is requested in the job description.
- Java is nice for building modular applications that can scale or be extended later (such as turning this into a web app)
- I’m confident working in this stack due to my skills and experience, and it’s widely used in production environments

Even though this is a console app, I wanted to show how I would structure a real-world project with service layers, DTOs, input validation, file output, and a professional approach.

---

## ✅ What the App Does

- Connects to the Just Eat API using a UK postcode
- On startup, it fetches results for a **default hardcoded postcode**
- After that, the user can enter more postcodes or type `exit` to quit
- Shows the **top 10 restaurants** for each postcode
- Displays:
    - Restaurant name
    - Cuisines
    - Rating (as a number or "Not Rated") (Colorful according to rating)
    - Address
- Saves the results to a `.txt` file inside a `FetchedRestaurants/` folder
- Validates that postcodes match the UK format

---

## 🚀 How to Build and Run the App

## Method 1: Run the App Using the Pre-Built JAR (No Setup Needed) (Recommended)

### Requirements

- Java 17 or higher  
  ⚠️ *Java 24 is not compatible with Gradle yet.*  
  👉 [Download Java 17+ from Adoptium](https://adoptium.net/en-GB/temurin/releases/?version=17)

---

### Steps

1. **Download the app: (Note: it may take 1-2 minutes as your system will check if the file is secure.)**

   👉 [Download takeaway-fetcher.jar](https://github.com/egemenyildiz3/restaurant-fetcher/releases/latest)

2. **Open a terminal (Command Prompt, PowerShell, or Terminal)**

3. **Navigate to the folder where the `.jar` file is located:**
For example, if it’s in your `Downloads` folder:

       cd Downloads

4. **Run the app**

       java -jar takeaway-fetcher.jar

5. **The app will first run with the default postcode:**

       EC4M7RF

6. **Then you can enter any UK postcode, or type:**

       exit

✅ The results will also be saved inside the FetchedRestaurants/ folder.


## Method 2: Build and Run the App Yourself

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

- The goal was to clearly display the 4 required fields (name, cuisines, rating, address) in a readable way, not to build a graphical interface. The CLI format was chosen for production speed and clarity.
- The `cuisines` field in the API contains both actual cuisines (like "Thai") and platform tags (like "Low Delivery Fee" or "Cheeky Tuesday"). This is how the API structures the data, and I've displayed it as provided. I assume that sanitizing this input is not necessary as it is how the API is deliberately constructed.
- I used the rating standards of Takeaway.com.
- Descending sorting of the fetched events have been removed due to no specifications existing upon the displaying order.
- I assumed the API structure remains stable (field names don’t change).
- The app only uses the 4 required fields: name, cuisines, rating, address.
- The app handles missing ratings and special characters in API responses.
- Invalid or incorrectly formatted postcodes are rejected.
- By "proper displaying", I assumed it is meant

---

## 🛠️ Improvements I would Make With More Time

- Emojis do not work for many terminals, so was removed. Could work on that more.
- Have a postcode detector by IP address of the users.
- Route detector and distance calculator for users based on their location for user-friendliness.
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
