package be.kuleuven.dsgt4;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.google.gson.Gson;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api")
public class OrderController {

    @PostMapping("/createorde")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> orderData) {
        Firestore db = FirestoreClient.getFirestore();

        System.out.println("Order Data:");
        for (Map.Entry<String, Object> entry : orderData.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            System.out.println(key + ": " + value);
        }

        // Extract customer information
        String firstName = (String) orderData.get("firstName");
        String lastName = (String) orderData.get("lastName");
        String userId = (String) orderData.get("userId");
        String address = (String) orderData.get("address");
        Map<String, Object> items = (Map<String, Object>) orderData.get("items");

        // Print the extracted fields
        System.out.println("First Name: " + firstName);
        System.out.println("Last Name: " + lastName);
        System.out.println("userId: " + userId);
        System.out.println("Address: " + address);

        System.out.println("Items:");
        for (Map.Entry<String, Object> entry : items.entrySet()) {
            String itemId = entry.getKey();
            Integer quantity = (Integer) entry.getValue();
            System.out.println("{id=" + itemId + ", quantity=" + quantity + "}");
        }

        // Map to store product details fetched from the database
        Map<String, Map<String, Integer>> itemsBySupplier = new HashMap<>();

        // Fetch product details for each item
        for (Map.Entry<String, Object> itemEntry : items.entrySet()) {
            String itemId = itemEntry.getKey();
            Integer quantity = (Integer) itemEntry.getValue();

            // Fetch product details from the database
            DocumentReference productDocRef = db.collection("products").document(itemId);
            ApiFuture<DocumentSnapshot> productFuture = productDocRef.get();

            try {
                DocumentSnapshot productDoc = productFuture.get();
                if (productDoc.exists()) {
                    String supplierId = productDoc.getString("supplierId");
                    // Add item details to the corresponding supplier
                    itemsBySupplier.computeIfAbsent(supplierId, k -> new HashMap<>()).put(itemId, quantity);
                } else {
                    // Handle if product not found
                    System.err.println("Product not found for ID: " + itemId);
                }
            } catch (InterruptedException | ExecutionException e) {
                // Handle exceptions
                e.printStackTrace();
                System.err.println("Error fetching product details for ID: " + itemId);
            }
        }

        // Create order for customer
        Map<String, Object> customerOrder = new HashMap<>();
        customerOrder.put("firstName", firstName);
        customerOrder.put("lastName", lastName);
        customerOrder.put("userId", userId);
        customerOrder.put("address", address);
        customerOrder.put("status", "PENDING");
        customerOrder.put("items", items);
        System.out.println("Order Data:");
        for (Map.Entry<String, Object> entry : customerOrder.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            System.out.println(key + ": " + value);
        }

        ApiFuture<DocumentReference> future = db.collection("orders").add(customerOrder);
        try {
            DocumentReference docRef = future.get();
            String customerOrderId = docRef.getId();

            // Create orders for suppliers and send requests
            int confirmedSuppliers = 0;
            for (Map.Entry<String, Map<String, Integer>> entry : itemsBySupplier.entrySet()) {
                String supplierId = entry.getKey();
                Map<String, Integer> supplierItems = entry.getValue();

                // Create supplier order
                Map<String, Object> supplierOrder = new HashMap<>();
                supplierOrder.put("masterId", customerOrderId);
                supplierOrder.put("address", address);
                supplierOrder.put("items", supplierItems);
                supplierOrder.put("status", "PENDING"); // Initial status
                supplierOrder.put("firstName", firstName);
                supplierOrder.put("lastName", lastName);
                for (Map.Entry<String, Object> entry2 : supplierOrder.entrySet()) {
                    String key = entry2.getKey();
                    Object value = entry2.getValue();
                    System.out.println(key + ": " + value);
                }

                // Get supplier details (API endpoint and API key)
                DocumentSnapshot supplierDoc = db.collection("users").document(supplierId).get().get();
                if (supplierDoc.exists()) {
                    String apiUrl = supplierDoc.getString("endpoint").trim() + "orders".trim();
                    String apiKey = supplierDoc.getString("apikey").trim();

                    // Send POST request to supplier API
                    Map<String, Object> response = sendPostRequest(apiUrl, supplierOrder, apiKey);

                    // Handle supplier response
                    if (response != null && response.containsKey("success") && (Boolean) response.get("success")) {
                        Map<String, Object> responseData = (Map<String, Object>) response.get("data");
                        String status = (String) responseData.get("status");
                        // Check if supplier confirmed the order
                        if (status.equals("CONFIRMED")) {
                            confirmedSuppliers++;
                        }
                        if (status.equals("ROOTSTOCK")){

                        }

                    } else {
                        // Handle other responses or errors
                        // For simplicity, you can just log them
                        System.out.println("Error or unexpected response from supplier API");
                    }
                }
            }

            // Check if all suppliers confirmed the order
            if (confirmedSuppliers == itemsBySupplier.size()) {
                // Update customer order status to CONFIRMED
                Map<String, Object> updateOrder = new HashMap<>();
                updateOrder.put("status", "CONFIRMED");
                db.collection("orders").document(customerOrderId).update(updateOrder);
                System.out.println("Suppliers confirmed the order");
            } else {
                // Handle if not all suppliers confirmed
                System.out.println("Not all suppliers confirmed the order");
                for (Map.Entry<String, Map<String, Integer>> entry : itemsBySupplier.entrySet()) {
                    String supplierId = entry.getKey();

                    // Get supplier details (API endpoint and API key)
                    DocumentSnapshot supplierDoc = db.collection("users").document(supplierId).get().get();
                    if (supplierDoc.exists()) {
                        String apiUrl = supplierDoc.getString("endpoint").trim() + "orders".trim() + "/" + customerOrderId;
                        String apiKey = supplierDoc.getString("apikey").trim();

                        // Create request body to update status to CANCELLED
                        Map<String, Object> cancelRequest = new HashMap<>();
                        cancelRequest.put("status", "CANCELLED");

                        // Send PUT request to supplier API
                        sendPutRequest(apiUrl, cancelRequest, apiKey);
                    }
                    Map<String, Object> updateOrder = new HashMap<>();
                    updateOrder.put("status", "CANCELLED");
                    db.collection("orders").document(customerOrderId).update(updateOrder);
                }

            }
        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
            System.err.println("Error creating order or sending requests to suppliers");
        }
        return ResponseEntity.ok("Order created successfully");
    }

    // Method to send a POST request to supplier API
    private Map<String, Object> sendPostRequest(String apiUrl, Map<String, Object> data, String apiKey) throws IOException {
        // Implement your HTTP POST request logic here
        // You can use libraries like Apache HttpClient or HttpURLConnection
        // Here is a simplified example using HttpURLConnection

        URL url = new URL(apiUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Apikey", apiKey); // Set Apikey as a header
        con.setDoOutput(true);

        try (OutputStream os = con.getOutputStream()) {
            byte[] input = new Gson().toJson(data).getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            // Parse response JSON string to Map
            return new Gson().fromJson(response.toString(), Map.class);
        }
    }

    // Method to send PUT request
    private Map<String, Object> sendPutRequest(String apiUrl, Map<String, Object> data, String apiKey) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("PUT");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Apikey", apiKey); // Set Apikey as a header
        con.setDoOutput(true);

        try (OutputStream os = con.getOutputStream()) {
            byte[] input = new Gson().toJson(data).getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            // Parse response JSON string to Map
            return new Gson().fromJson(response.toString(), Map.class);
        }
    }


}
