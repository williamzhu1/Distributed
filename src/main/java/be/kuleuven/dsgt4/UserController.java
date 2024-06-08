package be.kuleuven.dsgt4;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api")
public class UserController {

    @CrossOrigin(origins = "http://localhost:3000") // Replace with your frontend's origin
    @PostMapping("/usertest")
    public ResponseEntity<?> handleRequest(@RequestHeader("Authorization") String authorizationHeader,
                                           @RequestBody Map<String, Object> requestBody) {
        // Your logic to handle the request
        return ResponseEntity.ok("Request received and processed");
    }

    @CrossOrigin(origins = "http://localhost:3000") // Replace with your frontend's origin
    @PostMapping("/createitem")
    public ResponseEntity<?> createItem(@RequestBody Map<String, Object> requestBody) {
        Firestore db = FirestoreClient.getFirestore();

        // Create a Map to store the item details
        Map<String, Object> item = new HashMap<>();
        item.put("name", "Generic Item Name");
        item.put("description", "Generic Item Description");
        item.put("price", 100);
        item.put("quantity", 10);

        // Add a new document to the "items" collection
        ApiFuture<DocumentReference> future = db.collection("items").add(item);

        try {
            // Wait for the document reference
            DocumentReference docRef = future.get();
            System.out.println("Item created with ID: " + docRef.getId());

            // Optionally, you can update the document and get a WriteResult if you need to ensure the update
            // ApiFuture<WriteResult> writeResult = docRef.update("status", "created");
            // WriteResult result = writeResult.get();
            // System.out.println("Write result: " + result.getUpdateTime());

            // Return a success response
            return ResponseEntity.ok("Item created successfully with ID: " + docRef.getId());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            // Return an error response
            return ResponseEntity.status(500).body("Error creating item");
        }
    }
}
