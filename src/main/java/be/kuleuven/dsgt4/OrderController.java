package be.kuleuven.dsgt4;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            itemsBySupplier.computeIfAbsent(supplierId, k -> new java.util.ArrayList<>()).add(item);
        }

        // Create order for customer
        Map<String, Object> customerOrder = new HashMap<>();
        customerOrder.put("firstName", firstName);
        customerOrder.put("lastName", lastName);
        customerOrder.put("address", address);
        customerOrder.put("totalPrice", total);
        customerOrder.put("shippingStatus", "pending");
        customerOrder.put("items", items);

        ApiFuture<DocumentReference> future = db.collection("orders_customer").add(customerOrder);
        try {
            DocumentReference docRef = future.get();
            String customerOrderId = docRef.getId();

            // Create orders for suppliers
            for (Map.Entry<String, List<Map<String, Object>>> entry : itemsBySupplier.entrySet()) {
                String supplierId = entry.getKey();
                List<Map<String, Object>> supplierItems = entry.getValue();

                Map<String, Object> supplierOrder = new HashMap<>();
                supplierOrder.put("orderId", customerOrderId);
                supplierOrder.put("address", address);
                supplierOrder.put("items", supplierItems);
                supplierOrder.put("shippingStatus", "pending");
                supplierOrder.put("customerFirstName", firstName);
                supplierOrder.put("customerLastName", lastName);

                db.collection("orders_supplier").document(supplierId).collection("orders").add(supplierOrder);
            }

            return ResponseEntity.ok("Order created successfully with ID: " + customerOrderId);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error creating order");
        }
    }
}
