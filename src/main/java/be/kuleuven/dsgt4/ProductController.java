package be.kuleuven.dsgt4;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api")

public class ProductController {

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
