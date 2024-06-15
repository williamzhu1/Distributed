package be.kuleuven.dsgt4;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
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

    //get all items
    @CrossOrigin(origins = "http://localhost:3000") // Replace with your frontend's origin
    @GetMapping("/getitems")
    public ResponseEntity<?> getItems() {
        Firestore db = FirestoreClient.getFirestore();

        // Get all documents from the "items" collection
        ApiFuture<QuerySnapshot> future = db.collection("items").get();

        try {
            // Wait for the query snapshot
            QuerySnapshot querySnapshot = future.get();
            List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();

            // Create a list to store the items
            List<Map<String, Object>> items = new ArrayList<>();

            // Iterate over the documents and add them to the list
            for (QueryDocumentSnapshot document : documents) {
                items.add(document.getData());
            }

            // Return the list of items
            return ResponseEntity.ok(items);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            // Return an error response
            return ResponseEntity.status(500).body("Error getting items");
        }
    }

    @CrossOrigin(origins = "http://localhost:3000") // Replace with your frontend's origin
    @GetMapping("/getsuppliers")
    public ResponseEntity<?> getSuppliers() {
        Firestore db = FirestoreClient.getFirestore();

        // Get all documents from the "users" collection
        ApiFuture<QuerySnapshot> future = db.collection("users").get();

        try {
            // Wait for the query snapshot
            QuerySnapshot querySnapshot = future.get();
            List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();

            // Create a list to store users with the required fields
            List<Map<String, Object>> users = new ArrayList<>();

            // Iterate over the documents
            for (QueryDocumentSnapshot document : documents) {
                Map<String, Object> data = document.getData();
                // Check if the document has all the required fields
                if (data.containsKey("apiKey") && data.containsKey("company") && data.containsKey("endpoint")) {
                    Map<String, Object> filteredData = new HashMap<>();
                    filteredData.put("apiKey", data.get("apiKey"));
                    filteredData.put("company", data.get("company"));
                    filteredData.put("endpoint", data.get("endpoint"));
                    users.add(filteredData);
                }
            }

            // Return the list of users
            return ResponseEntity.ok(users);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            // Return an error response
            return ResponseEntity.status(500).body("Error getting user data");
        }
    }

    //put the new item in the body of the request
    @CrossOrigin(origins = "http://localhost:3000") 
    @PostMapping("/createitem")
    public ResponseEntity<?> createItem(@RequestBody Map<String, Object> requestBody) {
        Firestore db = FirestoreClient.getFirestore();

        // Add a new document to the "items" collection
        ApiFuture<DocumentReference> future = db.collection("items").add(requestBody);

        try {
            // Wait for the document reference
            DocumentReference docRef = future.get();
            System.out.println("Item created with ID: " + docRef.getId());
            return ResponseEntity.ok("Item created successfully with ID: " + docRef.getId());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            // Return an error response
            return ResponseEntity.status(500).body("Error creating item");
        }
    }

    @CrossOrigin(origins = "http://localhost:3000") 
    @PostMapping("/createorder")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> requestBody) {
        Firestore db = FirestoreClient.getFirestore();

        // Add a new document to the "items" collection
        ApiFuture<DocumentReference> future = db.collection("orders").add(requestBody);

        try {
            // Wait for the document reference
            DocumentReference docRef = future.get();
            System.out.println("Item created with ID: " + docRef.getId());
            return ResponseEntity.ok("Item created successfully with ID: " + docRef.getId());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            // Return an error response
            return ResponseEntity.status(500).body("Error creating item");
        }
    }



//put in the body json: {"id":"id","orderStatus":"status"}
//eg: body: JSON.stringify({ id, orderStatus: newStatus }),
@CrossOrigin(origins = "http://localhost:3000")
@PostMapping("/updateOrderStatus")
public ResponseEntity<?> updateOrderStatus(@RequestBody Map<String, Object> requestBody) {
    Firestore db = FirestoreClient.getFirestore();

    // Extract the order ID and the new status from the request body
    String orderId = (String) requestBody.get("id");
    String newStatus = (String) requestBody.get("orderStatus");

    // Get a reference to the document to update
    DocumentReference docRef = db.collection("orders").document(orderId);

    // Create a map with the fields to update
    Map<String, Object> updates = new HashMap<>();
    updates.put("orderStatus", newStatus);

    // Update the document
    ApiFuture<WriteResult> future = docRef.update(updates);

    try {
        // Wait for the update operation to complete
        WriteResult result = future.get();
        System.out.println("Order updated at: " + result.getUpdateTime());
        return ResponseEntity.ok("Order updated successfully");
    } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
        // Return an error response
        return ResponseEntity.status(500).body("Error updating order");
    }
}
//            body: JSON.stringify({ orderId }),
@CrossOrigin(origins = "http://localhost:3000")
@PostMapping("/deleteOrder")
public ResponseEntity<?> deleteOrder(@RequestBody Map<String, Object> requestBody) {
    Firestore db = FirestoreClient.getFirestore();

    // Extract the order ID from the request body
    String orderId = (String) requestBody.get("orderId");

    // Get a reference to the document to delete
    DocumentReference docRef = db.collection("orders").document(orderId);

    // Delete the document
    ApiFuture<WriteResult> future = docRef.delete();

    try {
        // Wait for the delete operation to complete
        future.get();
        System.out.println("Order deleted with ID: " + orderId);
        return ResponseEntity.ok("Order deleted successfully");
    } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
        // Return an error response
        return ResponseEntity.status(500).body("Error deleting order");
    }
}

    // @CrossOrigin(origins = "http://localhost:3000") // Replace with your frontend's origin
    // @PostMapping("/createitem")
    // public ResponseEntity<?> createItem(@RequestBody Map<String, Object> requestBody) {
    //     Firestore db = FirestoreClient.getFirestore();

    //     // Create a Map to store the item details
    //     Map<String, Object> item = new HashMap<>();
    //     item.put("name", "Generic Item Name");
    //     item.put("description", "Generic Item Description");
    //     item.put("price", 100);
    //     item.put("quantity", 10);

    //     // Add a new document to the "items" collection
    //     ApiFuture<DocumentReference> future = db.collection("items").add(item);

    //     try {
    //         // Wait for the document reference
    //         DocumentReference docRef = future.get();
    //         System.out.println("Item created with ID: " + docRef.getId());

    //         // Optionally, you can update the document and get a WriteResult if you need to ensure the update
    //         // ApiFuture<WriteResult> writeResult = docRef.update("status", "created");
    //         // WriteResult result = writeResult.get();
    //         // System.out.println("Write result: " + result.getUpdateTime());

    //         // Return a success response
    //         return ResponseEntity.ok("Item created successfully with ID: " + docRef.getId());
    //     } catch (InterruptedException | ExecutionException e) {
    //         e.printStackTrace();
    //         // Return an error response
    //         return ResponseEntity.status(500).body("Error creating item");
    //     }
    // }
}
