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
        List<Map<String, Object>> items = (List<Map<String, Object>>) orderData.get("items");
        double total = (double) orderData.get("price");

        // Split items by supplier
        Map<String, List<Map<String, Object>>> itemsBySupplier = new HashMap<>();
        for (Map<String, Object> item : items) {
            String supplierId = (String) item.get("supplierId");
            itemsBySupplier.computeIfAbsent(supplierId, k -> new ArrayList<>()).add(item);
        }

        // Create order for customer
        Map<String, Object> customerOrder = new HashMap<>();
        customerOrder.put("firstName", firstName);
        customerOrder.put("lastName", lastName);
        customerOrder.put("address", address);
        customerOrder.put("totalPrice", total);
        customerOrder.put("shippingStatus", "PENDING");
        customerOrder.put("items", items);

        ApiFuture<DocumentReference> future = db.collection("orders").add(customerOrder);
        try {
            DocumentReference docRef = future.get();
            String customerOrderId = docRef.getId();

            // Create orders for suppliers and send requests
            int confirmedSuppliers = 0;
            for (Map.Entry<String, List<Map<String, Object>>> entry : itemsBySupplier.entrySet()) {
                String supplierId = entry.getKey();
                List<Map<String, Object>> supplierItems = entry.getValue();

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
                    String apiUrl = supplierDoc.getString("endpoint").trim() + "/orders";
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

            // If all suppliers confirmed, update customer order status to confirmed
            if (confirmedSuppliers == itemsBySupplier.size()) {
                db.collection("orders").document(customerOrderId)
                        .update("shippingStatus", "CONFIRMED");
            }

            return ResponseEntity.ok("Order created successfully with ID: " + customerOrderId);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error creating order");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

}
