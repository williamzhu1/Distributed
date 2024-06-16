package be.kuleuven.dsgt4;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api")
public class ProductController {

    @PostMapping("/reload-products")
    public ResponseEntity<?> reloadProducts() {
        try {
            Firestore db = FirestoreClient.getFirestore();

            // Query to fetch managers
            ApiFuture<QuerySnapshot> query = db.collection("users").whereEqualTo("role", "manager").get();
            QuerySnapshot querySnapshot = query.get();
            List<QueryDocumentSnapshot> managerDocs = querySnapshot.getDocuments();
            System.out.println("manageGer URL: " + managerDocs);

            List<Map<String, Object>> allProducts = new ArrayList<>();

            for (QueryDocumentSnapshot managerDoc : managerDocs) {
                String apiUrl = ((String) Objects.requireNonNull(managerDoc.get("endpoint"))).trim() + "items";
                String apiKey = ((String) Objects.requireNonNull(managerDoc.get("apikey"))).trim();
                System.out.println("Manager: " + managerDoc.getId());
                System.out.println("Endpoint URL: " + apiUrl);
                System.out.println("API Key: " + apiKey);

                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Apikey", apiKey);

                HttpEntity<String> entity = new HttpEntity<>(headers);
                System.out.println("Request Headers: " + headers);

                try {
                    ResponseEntity<Map> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map.class);
                    Map<String, Object> response = responseEntity.getBody();
                    System.out.println("ResponseEntity: " + responseEntity);
                    System.out.println("Response: " + response);

                    if (response == null || !Boolean.TRUE.equals(response.get("success"))) {
                        System.err.println("Error fetching items from external API for manager: " + managerDoc.getId());
                        continue;  // Skip to the next manager
                    }

                    List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("data");

                    List<ApiFuture<WriteResult>> futures = new ArrayList<>();

                    for (Map<String, Object> item : items) {
                        // Set supplierId as manager's id
                        item.put("supplierId", managerDoc.getId());
                        String itemId = (String) item.get("id");
                        DocumentReference docRef = db.collection("products").document(itemId);
                        ApiFuture<WriteResult> future = docRef.set(item, SetOptions.merge());
                        futures.add(future);
                    }

                    // Wait for all writes to complete
                    for (ApiFuture<WriteResult> future : futures) {
                        future.get();
                    }

                    // Retrieve the updated list of products from Firestore
                    List<Map<String, Object>> managerProducts = new ArrayList<>();
                    ApiFuture<QuerySnapshot> productQuery = db.collection("products").whereEqualTo("supplierId", managerDoc.getId()).get();
                    QuerySnapshot productQuerySnapshot = productQuery.get();
                    for (DocumentSnapshot document : productQuerySnapshot.getDocuments()) {
                        Map<String, Object> product = document.getData();
                        product.put("id", document.getId());
                        managerProducts.add(product);
                    }
                    allProducts.addAll(managerProducts);
                } catch (HttpClientErrorException.Unauthorized unauthorizedException) {
                    System.err.println("Unauthorized access to the external API for manager: " + managerDoc.getId());
                    // Skip to the next manager
                } catch (Exception e) {
                    System.err.println("Error fetching items from external API for manager: " + managerDoc.getId() + " - " + e.getMessage());
                    e.printStackTrace();
                    // Skip to the next manager
                }
            }

            return ResponseEntity.ok(allProducts);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error uploading products to Firestore");
        }
    }

    @PostMapping("/products")
    public ResponseEntity<?> addProduct(@RequestBody Map<String, Object> product) {
        Firestore db = FirestoreClient.getFirestore();

        ApiFuture<DocumentReference> future = db.collection("products").add(product);

        try {
            DocumentReference docRef = future.get();
            System.out.println("Product created with ID: " + docRef.getId());
            return ResponseEntity.ok("Product added successfully with ID: " + docRef.getId());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error adding product");
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