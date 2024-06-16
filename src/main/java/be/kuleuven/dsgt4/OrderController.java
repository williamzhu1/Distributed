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
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api")
public class OrderController {

    @PostMapping("/order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> orderData) {
        return createOrUpdateOrder(orderData);
    }

    @DeleteMapping("/order/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable String orderId) throws ExecutionException, InterruptedException {
        return deleteOrderAndSupplierOrders(orderId);
    }

    @PutMapping("/retryOrder/{orderId}")
    public ResponseEntity<?> retryOrder(@PathVariable String orderId) throws IOException {
        Firestore db = FirestoreClient.getFirestore();
        try {
            // Fetch the old order
            DocumentReference orderRef = db.collection("orders").document(orderId);
            ApiFuture<DocumentSnapshot> future = orderRef.get();
            DocumentSnapshot oldOrderSnapshot = future.get();
            if(oldOrderSnapshot.get("status") == "CONFIRMED" || oldOrderSnapshot.get("status") == "SHIPPED"|| oldOrderSnapshot.get("status") == "DELIVERED"){
                return ResponseEntity.status(404).body("Order already CONFIRMED,SHIPPED or DELIVERED");
            }

            if (!oldOrderSnapshot.exists()) {
                return ResponseEntity.status(404).body("Order not found");
            }

            // Copy the old order data and update status
            Map<String, Object> oldOrderData = oldOrderSnapshot.getData();

            if (oldOrderData == null) {
                return ResponseEntity.status(404).body("Old order data is null");
            }

            // Ensure quantity values are sent as integers
            Map<String, Integer> formattedItems = new HashMap<>();
            // Get the items map from oldOrderData
            Map<String, Object> itemsMap = (Map<String, Object>) oldOrderData.get("items");

            if (itemsMap != null) {
                for (Map.Entry<String, Object> itemEntry : itemsMap.entrySet()) {
                    String itemName = itemEntry.getKey();
                    Object quantityObj = itemEntry.getValue();
                    if (quantityObj instanceof Long) {
                        formattedItems.put(itemName, ((Long) quantityObj).intValue());
                    }
                }
            }
            Map<String, Object> newOrderData = new HashMap<>(oldOrderData);
            newOrderData.put("items", formattedItems);
            // Call the createOrUpdateOrder function to create a new order with updated data
            deleteOrderAndSupplierOrders(orderId);
            return createOrUpdateOrder(newOrderData);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error updating order");
        }
    }

    @GetMapping("/supplierOrder")
    public ResponseEntity<?> getOrders(@RequestParam String orderId, @RequestBody Map<String, Object> updateData) {
        Firestore db = FirestoreClient.getFirestore();
        String userId = (String) updateData.get("userId");
        try {
            // Fetch supplier document to get API endpoint and API key
            DocumentReference supplierDocRef = db.collection("users").document(userId);
            DocumentSnapshot supplierDoc = supplierDocRef.get().get();
            if (supplierDoc.exists()) {
                String apiUrl = Objects.requireNonNull(supplierDoc.getString("endpoint")).trim() + "orders" + "/" + orderId;
                String apiKey = Objects.requireNonNull(supplierDoc.getString("apikey")).trim();
                // Send PUT request to supplier API
                Map<String, Object> response = sendGetRequest(apiUrl, apiKey);
                return (ResponseEntity<?>) response;
            } else {
                return ResponseEntity.status(404).body("Supplier not found");
            }
        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error fetching orders");
        }
    }

    @PutMapping("/supplierOrder")
    public ResponseEntity<?> updateSupplierOrderStatus(@RequestParam String orderId, @RequestBody Map<String, Object> updateData) {
        Firestore db = FirestoreClient.getFirestore();
        String userId = (String) updateData.get("userId");
        String newStatus = (String) updateData.get("status");

        try {
            // Fetch supplier document to get API endpoint and API key
            DocumentReference supplierDocRef = db.collection("users").document(userId);
            DocumentSnapshot supplierDoc = supplierDocRef.get().get();
            if (supplierDoc.exists()) {
                String apiUrl = Objects.requireNonNull(supplierDoc.getString("endpoint")).trim() + "orders";
                String apiKey = Objects.requireNonNull(supplierDoc.getString("apikey")).trim();

                // Create request body to update status to CANCELLED
                Map<String, Object> Request = new HashMap<>();
                Request.put("status", newStatus);

                // Send PUT request to supplier API
                Map<String, Object> response = sendPutRequest(apiUrl, Request, apiKey);
                return (ResponseEntity<?>) response;
            } else {
                return ResponseEntity.status(404).body("Supplier not found");
            }
        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error updating order status");
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
            byte[] input = new Gson().toJson(data).getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            // Parse response JSON string to Map
            return new Gson().fromJson(response.toString(), Map.class);
        }
    }
    // Method to send GET request
    private Map<String, Object> sendGetRequest(String apiUrl, String apiKey) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Apikey", apiKey); // Set Apikey as a header
        con.setDoOutput(true);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
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
            byte[] input = new Gson().toJson(data).getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            // Parse response JSON string to Map
            return new Gson().fromJson(response.toString(), Map.class);
        }
    }

    // Method to send DELETE request
    private Map<String, Object> sendDeleteRequest(String apiUrl,  String apiKey) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("DELETE");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Apikey", apiKey); // Set Apikey as a header
        con.setDoOutput(true);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            // Parse response JSON string to Map
            return new Gson().fromJson(response.toString(), Map.class);
        }
    }

    @GetMapping("/getorders")
    public ResponseEntity<?> getOrders(@RequestParam String userId) {
        Firestore db = FirestoreClient.getFirestore();

        // Fetch orders from the Firestore database for the given userId
        ApiFuture<QuerySnapshot> future = db.collection("orders")
                .whereEqualTo("userId", userId)
                .get();
        List<Map<String, Object>> orders = new ArrayList<>();
        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                Map<String, Object> orderData = document.getData();
                orderData.put("orderId", document.getId()); // Add order ID to the order data
                Map<String, Object> items = (Map<String, Object>) orderData.get("items");

                // Fetch product names for each item
                Map<String, Object> itemsWithNames = new HashMap<>();
                for (String itemId : items.keySet()) {
                    DocumentReference productRef = db.collection("products").document(itemId);
                    ApiFuture<DocumentSnapshot> productFuture = productRef.get();
                    DocumentSnapshot productSnapshot = productFuture.get();
                    if (productSnapshot.exists()) {
                        String productName = productSnapshot.getString("name");
                        itemsWithNames.put(productName, items.get(itemId));
                    } else {
                        itemsWithNames.put(itemId, items.get(itemId)); // Fallback to ID if name not found
                    }
                }
                orderData.put("items", itemsWithNames);
                orders.add(orderData);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error fetching orders");
        }

        return ResponseEntity.ok(orders);
    }

    private ResponseEntity<?> createOrUpdateOrder(Map<String, Object> orderData){
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
        String customerOrderId = null;
        try {
            DocumentReference docRef = future.get();
            customerOrderId = docRef.getId();

            // Create orders for suppliers and send requests
            int confirmedSuppliers = 0;
            boolean outOfStock = false;
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
                    String apiUrl = Objects.requireNonNull(supplierDoc.getString("endpoint")).trim() + "orders".trim();
                    String apiKey = Objects.requireNonNull(supplierDoc.getString("apikey")).trim();

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
                        if (status.equals("ROOTSTOCK")) {
                            System.out.println("ROOTSOCK on:");
                            for (Map.Entry<String, Integer> entrystock : supplierItems.entrySet()) {
                                System.out.println(entrystock.getKey() + ": " + entrystock.getValue());
                            }
                            outOfStock = true;
                        }

                    } else {
                        // Handle other responses or errors
                        // For simplicity, you can just log them
                        System.out.println("Error or unexpected response from supplier API");
                        Map<String, Object> updateOrder = new HashMap<>();
                        updateOrder.put("status", "CANCELLED");
                        db.collection("orders").document(customerOrderId).update(updateOrder);
                        System.err.println("Error creating order or sending requests to suppliers");
                    }
                }
            }

            // Check if all suppliers confirmed the order
            if (confirmedSuppliers >= itemsBySupplier.size()) {
                // Update customer order status to CONFIRMED
                Map<String, Object> updateOrder = new HashMap<>();
                updateOrder.put("status", "CONFIRMED");
                db.collection("orders").document(customerOrderId).update(updateOrder);
                System.out.println("Suppliers confirmed the order");
            } else if (outOfStock) {
                // Handle if not all suppliers confirmed
                System.out.println("Some items out of stock or not enough stock");
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
                    updateOrder.put("status", "ROOTSTOCK");
                    db.collection("orders").document(customerOrderId).update(updateOrder);
                }
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
            Map<String, Object> updateOrder = new HashMap<>();
            updateOrder.put("status", "CANCELLED");
            assert customerOrderId != null;
            db.collection("orders").document(customerOrderId).update(updateOrder);
            System.err.println("Error creating order or sending requests to suppliers");
        }
        return ResponseEntity.ok("Order created successfully");
    }

    private ResponseEntity<?> deleteOrderAndSupplierOrders(String orderId) throws InterruptedException, ExecutionException {
        try {
            Firestore db = FirestoreClient.getFirestore();

            // Fetch the customer order
            DocumentReference orderDocRef = db.collection("orders").document(orderId);
            ApiFuture<DocumentSnapshot> futureOrder = orderDocRef.get();
            DocumentSnapshot orderSnapshot = futureOrder.get();

            if (orderSnapshot.exists()) {
                Map<String, Object> orderData = orderSnapshot.getData();
                if (orderData != null && orderData.containsKey("items")) {
                    Map<String, Object> items = (Map<String, Object>) orderData.get("items");

                    // Map to store product details fetched from the database
                    Map<String, Map<String, Integer>> itemsBySupplier = new HashMap<>();

                    // Fetch product details for each item
                    for (Map.Entry<String, Object> itemEntry : items.entrySet()) {
                        String itemId = itemEntry.getKey();

                        // Fetch product details from the database
                        DocumentReference productDocRef = db.collection("products").document(itemId);
                        ApiFuture<DocumentSnapshot> productFuture = productDocRef.get();
                        DocumentSnapshot productDoc = productFuture.get();

                        if (productDoc.exists()) {
                            String supplierId = productDoc.getString("supplierId");
                            itemsBySupplier.computeIfAbsent(supplierId, k -> new HashMap<>()).put(itemId, 0);
                        } else {
                            // Handle if product not found
                            System.err.println("Product not found for ID: " + itemId);
                        }
                    }

                    // Delete supplier orders
                    for (Map.Entry<String, Map<String, Integer>> entry : itemsBySupplier.entrySet()) {
                        String supplierId = entry.getKey();
                        Map<String, Integer> supplierItems = entry.getValue();

                        // Get supplier details (API endpoint and API key)
                        DocumentSnapshot supplierDoc = db.collection("users").document(supplierId).get().get();
                        if (supplierDoc.exists()) {
                            String apiUrl = Objects.requireNonNull(supplierDoc.getString("endpoint")).trim() + "orders".trim() + "/" + orderId;
                            String apiKey = Objects.requireNonNull(supplierDoc.getString("apikey")).trim();
                            try {
                                // Send DELETE request to supplier API
                                Map<String, Object> response = sendDeleteRequest(apiUrl, apiKey);

                                // Handle supplier response
                                if (response == null || !response.containsKey("success") || !(Boolean) response.get("success")) {
                                    System.out.println("Error or unexpected response from supplier API");
                                }
                            } catch (Exception e) {
                                // Log the exception and continue
                                System.err.println("Error sending DELETE request to supplier API: " + e.getMessage());
                            }
                        }
                    }
                }

                // Delete the customer order document
                orderDocRef.delete();
                return ResponseEntity.ok("Order and related supplier orders deleted successfully.");
            } else {
                return ResponseEntity.status(403).body("Order not found");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error deleting the order: " + e.getMessage());
        }
    }
}
