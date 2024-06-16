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
import org.springframework.web.client.RestClientException;

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

        // Validate required fields
        if (!product.containsKey("supplierId") || !product.containsKey("name") || !product.containsKey("price") ||
                !product.containsKey("category") || !product.containsKey("description") || !product.containsKey("manufacturer") ||
                !product.containsKey("stock")) {
            return ResponseEntity.badRequest().body("Missing required product fields");
        }

        String supplierId = (String) product.get("supplierId");

        // Retrieve supplier details
        ApiFuture<DocumentSnapshot> futureSupplier = db.collection("users").document(supplierId).get();
        try {
            DocumentSnapshot supplierDoc = futureSupplier.get();
            if (!supplierDoc.exists()) {
                return ResponseEntity.status(404).body("Supplier not found");
            }

            String apiUrl = supplierDoc.getString("endpoint");
            String apiKey = supplierDoc.getString("apikey");

            // Prepare to call supplier API
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Apikey", apiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(product, headers);
            ResponseEntity<Map> supplierResponse = restTemplate.postForEntity(apiUrl + "/items", request, Map.class);

            // Check response from supplier API
            if (supplierResponse.getStatusCode().is2xxSuccessful() && Boolean.TRUE.equals(supplierResponse.getBody().get("success"))) {
                // Log the received payload for debugging
                System.out.println("Received Product Payload: " + product);

                // Call reloadProducts method after successful addition in supplier's API
                ResponseEntity<?> reloadResponse = reloadProducts();
                if (reloadResponse.getStatusCode().is2xxSuccessful()) {
                    System.out.println("Product added and products reloaded successfully");
                    return ResponseEntity.ok("Product added successfully");
                } else {
                    System.err.println("Error reloading products: " + reloadResponse.getBody());
                    return ResponseEntity.status(500).body("Product added in supplier's API, but error reloading products");
                }
            } else {
                return ResponseEntity.status(500).body("Error adding product to supplier's API");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error retrieving supplier details");
        } catch (RestClientException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error calling supplier's API");
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

                String supplierId = (String) product.get("supplierId");
                if (supplierId != null) {
                    DocumentReference supplierRef = db.collection("users").document(supplierId);
                    DocumentSnapshot supplierSnapshot = supplierRef.get().get();
                    if (supplierSnapshot.exists()) {
                        String companyName = supplierSnapshot.getString("companyName");
                        product.put("companyName", companyName);
                    } else {
                        product.put("companyName", "Unknown Supplier");
                    }
                } else {
                    product.put("companyName", "Unknown Supplier");
                }

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

    @GetMapping("/products/supplier/{supplierId}")
    public ResponseEntity<List<Map<String, Object>>> getProductsBySupplier(@PathVariable String supplierId) {
        Firestore db = FirestoreClient.getFirestore();
        List<Map<String, Object>> productsList = new ArrayList<>();

        ApiFuture<QuerySnapshot> future = db.collection("products").whereEqualTo("supplierId", supplierId).get();
        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                productsList.add(document.getData());
            }
            return ResponseEntity.ok(productsList);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable String id, @RequestBody Map<String, Object> product) {
        Firestore db = FirestoreClient.getFirestore();

        try {
            // Fetch the product details to get the supplierId
            ApiFuture<DocumentSnapshot> productFuture = db.collection("products").document(id).get();
            DocumentSnapshot productSnapshot = productFuture.get();
            if (!productSnapshot.exists()) {
                return ResponseEntity.status(404).body("Product not found");
            }

            String supplierId = (String) productSnapshot.get("supplierId");

            // Retrieve supplier details
            ApiFuture<DocumentSnapshot> futureSupplier = db.collection("users").document(supplierId).get();
            DocumentSnapshot supplierDoc = futureSupplier.get();
            if (!supplierDoc.exists()) {
                return ResponseEntity.status(404).body("Supplier not found");
            }

            String apiUrl = supplierDoc.getString("endpoint");
            String apiKey = supplierDoc.getString("apikey");

            // Prepare to call supplier API
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Apikey", apiKey);

            // Prepare the request body for the supplier's API
            Map<String, Object> supplierProduct = new HashMap<>();
            supplierProduct.put("name", product.get("name"));
            supplierProduct.put("description", product.get("description"));
            supplierProduct.put("price", product.get("price"));
            supplierProduct.put("category", product.get("category"));
            supplierProduct.put("manufacturer", product.get("manufacturer"));
            supplierProduct.put("stock", product.get("stock"));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(supplierProduct, headers);

            // Log the request details for debugging
            System.out.println("Request URL: " + apiUrl + "items/" + id);
            System.out.println("Request Headers: " + headers);
            System.out.println("Request Body: " + supplierProduct);

            ResponseEntity<Map> supplierResponse = restTemplate.exchange(apiUrl + "items/" + id, HttpMethod.PUT, request, Map.class);

            // Check response from supplier API
            if (supplierResponse.getStatusCode().is2xxSuccessful() && Boolean.TRUE.equals(supplierResponse.getBody().get("success"))) {
                // Call reloadProducts method after successful update in supplier's API
                ResponseEntity<?> reloadResponse = reloadProducts();
                if (reloadResponse.getStatusCode().is2xxSuccessful()) {
                    System.out.println("Product updated and products reloaded successfully");
                    return ResponseEntity.ok("Product updated successfully");
                } else {
                    System.err.println("Error reloading products: " + reloadResponse.getBody());
                    return ResponseEntity.status(500).body("Product updated in supplier's API, but error reloading products");
                }
            } else {
                System.err.println("Error updating product in supplier's API: " + supplierResponse.getBody().get("message"));
                return ResponseEntity.status(500).body("Error updating product in supplier's API: " + supplierResponse.getBody().get("message"));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error retrieving product or supplier details");
        } catch (RestClientException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error calling supplier's API: " + e.getMessage());
        }
    }




    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable String id) {
        Firestore db = FirestoreClient.getFirestore();

        try {
            // Fetch the product details to get the supplierId
            ApiFuture<DocumentSnapshot> productFuture = db.collection("products").document(id).get();
            DocumentSnapshot productSnapshot = productFuture.get();
            if (!productSnapshot.exists()) {
                return ResponseEntity.status(404).body("Product not found");
            }

            Map<String, Object> productData = productSnapshot.getData();
            String supplierId = (String) productData.get("supplierId");

            // Retrieve supplier details
            ApiFuture<DocumentSnapshot> supplierFuture = db.collection("users").document(supplierId).get();
            DocumentSnapshot supplierSnapshot = supplierFuture.get();
            if (!supplierSnapshot.exists()) {
                return ResponseEntity.status(404).body("Supplier not found");
            }

            String apiUrl = supplierSnapshot.getString("endpoint");
            String apiKey = supplierSnapshot.getString("apikey");

            // Prepare to call supplier API
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Apikey", apiKey);

            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<Map> supplierResponse = restTemplate.exchange(apiUrl + "/items/" + id, HttpMethod.DELETE, request, Map.class);

            // Check response from supplier API
            if (supplierResponse.getStatusCode().is2xxSuccessful() && Boolean.TRUE.equals(supplierResponse.getBody().get("success"))) {
                // Delete the product from Firestore if the supplier API call was successful
                ApiFuture<WriteResult> future = db.collection("products").document(id).delete();
                WriteResult result = future.get();
                System.out.println("Product deleted at: " + result.getUpdateTime());
                return ResponseEntity.ok("Product deleted successfully");
            } else {
                return ResponseEntity.status(500).body("Error deleting product from supplier's API");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error retrieving product or supplier details");
        } catch (RestClientException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error calling supplier's API");
        }
    }


}