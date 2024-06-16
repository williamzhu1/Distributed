package be.kuleuven.dsgt4;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api")
public class ProductController {

    private static final Logger LOGGER = Logger.getLogger(ProductController.class.getName());

    @PostMapping("/reload-products")
    public ResponseEntity<?> reloadProducts() {
        Firestore db = FirestoreClient.getFirestore();
        List<Map<String, String>> endpointsAndKeys = new ArrayList<>();

        // Step 1: Fetch user data to get endpoints, API keys, and user IDs
        try {
            ApiFuture<QuerySnapshot> usersQuery = db.collection("users").get();
            QuerySnapshot usersSnapshot = usersQuery.get();
            for (DocumentSnapshot document : usersSnapshot.getDocuments()) {
                Map<String, Object> userData = document.getData();
                if (userData != null && userData.containsKey("endpoint") && userData.containsKey("apikey")) {
                    String userId = document.getId(); // Get the user ID
                    Map<String, String> endpointAndKey = new HashMap<>();
                    endpointAndKey.put("endpoint", (String) userData.get("endpoint"));
                    endpointAndKey.put("apikey", (String) userData.get("apikey"));
                    endpointAndKey.put("userId", userId); // Add user ID to the endpoint and key data
                    endpointsAndKeys.add(endpointAndKey);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.log(Level.SEVERE, "Error fetching user data from Firestore", e);
            return ResponseEntity.status(500).body("Error fetching user data");
        }

        List<Map<String, Object>> allProducts = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();

        // Step 2: Fetch products from each endpoint
        for (Map<String, String> endpointAndKey : endpointsAndKeys) {
            String userId = endpointAndKey.get("userId"); // Get the user ID
            String apiUrl = endpointAndKey.get("endpoint").trim() + "items";
            String apikey = endpointAndKey.get("apikey");

            LOGGER.log(Level.SEVERE, "Fetching products from: " + apiUrl);
            LOGGER.log(Level.SEVERE, "Using API key: " + apikey);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Apikey", apikey);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            try {
                if (!apiUrl.startsWith("http://")) {
                    LOGGER.log(Level.WARNING, "Skipping non-absolute URL: {0}", apiUrl);
                    continue;
                }
                ResponseEntity<Map> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map.class);
                Map<String, Object> response = responseEntity.getBody();

//                if (response == null) {
//                    LOGGER.log(Level.WARNING, "Error fetching items from external API at {0}", apiUrl);
//                    continue;
//                }

                List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("data");

                // Step 3: Add user ID to each fetched product
                for (Map<String, Object> item : items) {
                    item.put("userId", userId); // Add user ID to the product
                }

                allProducts.addAll(items);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error fetching items from external API at " + apiUrl, e);
                return ResponseEntity.status(500).body("Error fetching items from external API at " + apiUrl + ": " + e.getMessage());
            }
        }

        // Step 4: Store the fetched products in Firestore
        List<ApiFuture<WriteResult>> futures = new ArrayList<>();
        for (Map<String, Object> item : allProducts) {
            ApiFuture<WriteResult> future = db.collection("products").document((String) item.get("id")).set(item);
            futures.add(future);
        }

        try {
            for (ApiFuture<WriteResult> future : futures) {
                future.get();
            }

            // Step 5: Retrieve the updated list of products from Firestore
            List<Map<String, Object>> updatedProducts = new ArrayList<>();
            ApiFuture<QuerySnapshot> query = db.collection("products").get();
            QuerySnapshot querySnapshot = query.get();
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                Map<String, Object> product = document.getData();
                product.put("id", document.getId());
                updatedProducts.add(product);
            }

            return ResponseEntity.ok(updatedProducts);
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.log(Level.SEVERE, "Error uploading products to Firestore", e);
            return ResponseEntity.status(500).body("Error uploading products to Firestore: " + e.getMessage());
        }
    }

    @PostMapping("/products")
    public ResponseEntity<?> addProduct(@RequestBody Map<String, Object> product) {
        Firestore db = FirestoreClient.getFirestore();

        ApiFuture<DocumentReference> future = db.collection("products").add(product);

        try {
            DocumentReference docRef = future.get();
            System.out.println("Product created with ID: " + docRef.getId());

            // Creating a response map to return as JSON
            Map<String, String> response = new HashMap<>();
            response.put("message", "Product added successfully");
            response.put("id", docRef.getId());

            // Return response entity with the response map
            return ResponseEntity.ok(response);
        } catch (InterruptedException | ExecutionException e) {
            // Logging the error
            e.printStackTrace();

            // Returning a 500 status with error message
            return ResponseEntity.status(500).body(Collections.singletonMap("error", "Error adding product"));
        }
    }



    @GetMapping("/products")
    public ResponseEntity<?> getProducts() {
        Firestore db = FirestoreClient.getFirestore();

        ApiFuture<QuerySnapshot> future = db.collection("products").get();
        List<Map<String, Object>> products = new ArrayList<>();

        try {
            QuerySnapshot querySnapshot = future.get();
            for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
                Map<String, Object> product = document.getData();
                product.put("id", document.getId()); // Include the document ID in the product data
                products.add(product);
            }
            return ResponseEntity.ok(products);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error getting products");
        }
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<?> getProductById(@PathVariable String id) {
        Firestore db = FirestoreClient.getFirestore();

        DocumentReference docRef = db.collection("products").document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();

        try {
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                Map<String, Object> product = document.getData();
                product.put("id", document.getId()); // Include the document ID in the product data
                return ResponseEntity.ok(product);
            } else {
                return ResponseEntity.status(404).body("Product not found");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error getting product");
        }
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable String id, @RequestBody Map<String, Object> product) {
        Firestore db = FirestoreClient.getFirestore();

        ApiFuture<WriteResult> future = db.collection("products").document(id).set(product);

        try {
            WriteResult result = future.get();
            System.out.println("Product updated at: " + result.getUpdateTime());
            return ResponseEntity.ok("Product updated successfully");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error updating product");
        }
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable String id) {
        Firestore db = FirestoreClient.getFirestore();

        ApiFuture<WriteResult> future = db.collection("products").document(id).delete();

        try {
            WriteResult result = future.get();
            System.out.println("Product deleted at: " + result.getUpdateTime());
            return ResponseEntity.ok("Product deleted successfully");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error deleting product");
        }
    }
}
