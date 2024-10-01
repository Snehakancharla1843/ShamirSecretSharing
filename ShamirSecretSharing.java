import java.io.*;
import java.util.*;
import org.json.*;

public class ShamirSecretSharing {

    // Method to decode a value from a given base to decimal (base 10)
    public static long decodeValue(String value, int base) {
        return Long.parseLong(value, base);
    }

    // Lagrange Interpolation to calculate the constant term (c)
    public static double lagrangeInterpolation(List<Long> xValues, List<Long> yValues, int k) {
        double c = 0.0;
        for (int i = 0; i < k; i++) {
            double li = 1.0;
            for (int j = 0; j < k; j++) {
                if (i != j) {
                    li *= (0.0 - xValues.get(j)) / (xValues.get(i) - xValues.get(j));
                }
            }
            c += li * yValues.get(i);
        }
        return c;
    }

    // Method to read the JSON input from a file
    public static JSONObject readJSONFile(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        StringBuilder jsonContent = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonContent.append(line);
        }
        reader.close();
        return new JSONObject(jsonContent.toString());
    }

    // Method to check for wrong points by comparing the actual and expected y-values
    public static List<String> findWrongPoints(List<Long> xValues, List<Long> yValues, int k, double tolerance) {
        List<String> wrongPoints = new ArrayList<>();
        for (int i = 0; i < xValues.size(); i++) {
            double expectedY = lagrangeInterpolation(xValues, yValues, k); // Calculate expected Y value
            if (Math.abs(expectedY - yValues.get(i)) > tolerance) {
                wrongPoints.add("(" + xValues.get(i) + ", " + yValues.get(i) + ")");
            }
        }
        return wrongPoints;
    }

    public static void main(String[] args) {
        try {
            double tolerance = 0.001; // Define tolerance for checking wrong points

            // Load the first test case JSON file
            JSONObject testCase1 = readJSONFile("testcase1.json");

            // Extract n and k
            JSONObject keys = testCase1.getJSONObject("keys");
            int n = keys.getInt("n");
            int k = keys.getInt("k");

            // Lists to store the x and y values
            List<Long> xValues = new ArrayList<>();
            List<Long> yValues = new ArrayList<>();

            // Loop through the JSON to get x, base, and value
            for (String key : testCase1.keySet()) {
                if (!key.equals("keys")) {
                    JSONObject root = testCase1.getJSONObject(key);
                    long x = Long.parseLong(key); // x is the key itself
                    int base = root.getInt("base");
                    String value = root.getString("value");
                    long y = decodeValue(value, base); // Decode y based on the base

                    xValues.add(x);
                    yValues.add(y);
                }
            }

            // Find the constant term c using Lagrange Interpolation
            double constantC1 = lagrangeInterpolation(xValues, yValues, k);
            System.out.printf("Secret for testcase1.json: %.2f%n", constantC1);

            // Load the second test case JSON file and repeat the process
            JSONObject testCase2 = readJSONFile("testcase2.json");

            // Extract n and k for the second test case
            keys = testCase2.getJSONObject("keys");
            n = keys.getInt("n");
            k = keys.getInt("k");

            // Clear the lists for the second test case
            xValues.clear();
            yValues.clear();

            // Loop through the second test case to get x, base, and value
            for (String key : testCase2.keySet()) {
                if (!key.equals("keys")) {
                    JSONObject root = testCase2.getJSONObject(key);
                    long x = Long.parseLong(key); // x is the key itself
                    int base = root.getInt("base");
                    String value = root.getString("value");
                    long y = decodeValue(value, base); // Decode y based on the base

                    xValues.add(x);
                    yValues.add(y);
                }
            }

            // Find the constant term c for the second test case
            double constantC2 = lagrangeInterpolation(xValues, yValues, k);
            System.out.printf("Secret for testcase2.json: %.2f%n", constantC2);

            // Find and print wrong points for the second test case
            List<String> wrongPoints = findWrongPoints(xValues, yValues, k, tolerance);
            if (!wrongPoints.isEmpty()) {
                System.out.println("Wrong points in testcase2.json: " + wrongPoints);
            } else {
                System.out.println("No wrong points in testcase2.json.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
