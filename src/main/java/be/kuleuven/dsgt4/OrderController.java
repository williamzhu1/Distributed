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

        // Extract customer information
        String firstName = (String) orderData.get("firstName");
        String lastName = (String) orderData.get("lastName");
        String address = (String) orderData.get("address");
        List<Map<String, Integer>> items = (List<Map<String, Integer>>) orderData.get("items");

        // Map to store product details fetched from the database
        Map<String, Map<String, Integer>> itemsBySupplier = new HashMap<>();

        // Fetch product details for each item
        for (Map<String, Integer> itemEntry : items) {
            for (Map.Entry<String, Integer> entry : itemEntry.entrySet()) {
                String itemId = entry.getKey();
                int quantity = entry.getValue();

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
        }

        // Create order for customer
        Map<String, Object> customerOrder = new HashMap<>();
        customerOrder.put("firstName", firstName);
        customerOrder.put("lastName", lastName);
        customerOrder.put("address", address);
        customerOrder.put("shippingStatus", "PENDING");
        customerOrder.put("items", items);

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
                supplierOrder.put("LastName", lastName);

                // Get supplier details (API endpoint and API key)
                DocumentSnapshot supplierDoc = db.collection("users").document(supplierId).get().get();
                if (supplierDoc.exists()) {
                    String apiUrl = supplierDoc.getString("endpoint").trim() + "/orders".trim();
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
                updateOrder.put("shippingStatus", "CONFIRMED");
                db.collection("orders_customer").document(customerOrderId).update(updateOrder);
            } else {
                // Handle if not all suppliers confirmed
                System.out.println("Not all suppliers confirmed the order");
            }
        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
            System.err.println("Error creating order or sending requests to suppliers");
        }
        return ResponseEntity.ok("Order created successfully" );
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

    @GetMapping("/getorders")
    public ResponseEntity<?> getOrders(@RequestParam String firstName, @RequestParam String lastName) {
        Firestore db = FirestoreClient.getFirestore();

        // Fetch orders from the Firestore database for the given firstName and lastName
        ApiFuture<QuerySnapshot> future = db.collection("orders")
                .whereEqualTo("firstName", firstName)
                .whereEqualTo("lastName", lastName)
                .get();
        List<Map<String, Object>> orders = new ArrayList<>();
        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                orders.add(document.getData());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error fetching orders");
        }

        return ResponseEntity.ok(orders);
    }

}
