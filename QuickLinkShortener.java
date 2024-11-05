import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class QuickLinkShortener {
    private static final String MAPPINGS_FILE = "url_mappings.txt";
    private static final String BASE_URL = "http://short.ly/";
    private Map<String, String> urlMappings = new HashMap<>();
    
    public QuickLinkShortener() {
        loadMappings();  // Load mappings from file at startup
    }

    public String shortenURL(String longUrl) {
        if (!isValidURL(longUrl)) {
            return "Error: Invalid URL";
        }
        
        // Check if the URL is already shortened
        for (Map.Entry<String, String> entry : urlMappings.entrySet()) {
            if (entry.getValue().equals(longUrl)) {
                return "Error: This URL has already been shortened as " + BASE_URL + entry.getKey();
            }
        }

        // Generate a unique short code
        String shortCode = generateShortCode();
        urlMappings.put(shortCode, longUrl);
        saveMappings();
        return BASE_URL + shortCode;
    }

    public String retrieveURL(String shortCode) {
        return urlMappings.getOrDefault(shortCode, "Error: URL not found");
    }

    private void loadMappings() {
        try (BufferedReader reader = new BufferedReader(new FileReader(MAPPINGS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    urlMappings.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading mappings: " + e.getMessage());
        }
    }

    private void saveMappings() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MAPPINGS_FILE))) {
            for (Map.Entry<String, String> entry : urlMappings.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving mappings: " + e.getMessage());
        }
    }

    private boolean isValidURL(String url) {
        return url.startsWith("http://") || url.startsWith("https://");
    }

    private String generateShortCode() {
        return "abc" + (urlMappings.size() + 1); // Simple unique code for demonstration
    }

    public static void main(String[] args) {
        QuickLinkShortener quickLinkShortener = new QuickLinkShortener();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to QuickLink Shortener!");
        System.out.println("Please enter a long URL to shorten it, a shortened URL to retrieve the original, or type 'exit' to quit.");

        while (true) {
            System.out.print("\n> ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Thank you for using QuickLink Shortener!");
                break;
            }

            if (input.startsWith(BASE_URL)) {
                // Extract the short code from the shortened URL
                String shortCode = input.replace(BASE_URL, "");
                System.out.println("Original URL: " + quickLinkShortener.retrieveURL(shortCode));
            } else if (input.matches("^[a-zA-Z0-9]+$")) {
                // If input is just the short code without the base URL
                System.out.println("Original URL: " + quickLinkShortener.retrieveURL(input));
            } else {
                // Otherwise, assume it's a long URL to shorten
                String result = quickLinkShortener.shortenURL(input);
                if (result.startsWith("Error")) {
                    System.out.println(result);
                } else {
                    System.out.println("Shortened URL: " + result);
                }
            }
        }

        scanner.close();
    }
}
