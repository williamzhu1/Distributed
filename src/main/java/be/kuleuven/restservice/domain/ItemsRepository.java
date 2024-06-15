package be.kuleuven.restservice.domain;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Component
public class ItemsRepository {
    private Firestore db;

    @PostConstruct
    public void initFirestore() throws IOException {
        // Initialize Firebase
        InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase.json");
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        FirebaseApp.initializeApp(options);

        // Initialize Firestore
        db = FirestoreClient.getFirestore();
    }

    // Method to convert Firestore DocumentSnapshot to Item object
    private Item documentToItem(DocumentSnapshot document) {
        if (document.exists()) {
            Item item = document.toObject(Item.class);
            assert item != null;
            item.setId(document.getId());
            return item;
        } else {
            return null;
        }
    }

    private Order documentToOrder(DocumentSnapshot document) {
        if (document.exists()) {
            Order order = document.toObject(Order.class);
            assert order != null;
            order.setId(document.getId());
            return order;
        } else {
            return null;
        }
    }

    public Optional<Item> findItem(String id) throws ExecutionException, InterruptedException {
        Assert.notNull(id, "The item id must not be null");
        DocumentReference docRef = db.collection("supplierItems").document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        return Optional.ofNullable(documentToItem(document));
    }

    public Collection<Item> getAllItems() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> query = db.collection("supplierItems").get();
        QuerySnapshot querySnapshot = query.get();
        List<Item> items = new ArrayList<>();
        for (QueryDocumentSnapshot document : querySnapshot) {
            Item item = document.toObject(Item.class);
            item.setId(document.getId());
            items.add(item);
        }
        return items;
    }

    public Optional<Item> addItem(Item newItem) throws ExecutionException, InterruptedException {
        Assert.notNull(newItem, "The item object must not be null");

        // Create a map to hold only the specified fields
        Map<String, Object> itemData = new HashMap<>();
        itemData.put("name", newItem.getName());
        itemData.put("description", newItem.getDescription());
        itemData.put("stock", newItem.getStock());
        itemData.put("price", newItem.getPrice());
        itemData.put("category", newItem.getCategory());
        itemData.put("manufacturer", newItem.getManufacturer());

        // Add item data to Firestore
        DocumentReference docRef = db.collection("supplierItems").document();
        ApiFuture<WriteResult> future = docRef.set(itemData);
        future.get(); // Wait for operation to complete

        // Return the added item
        return Optional.of(newItem);
    }


    public Optional<Item> updateItem(String id, Item updatedItem) throws ExecutionException, InterruptedException {
        Assert.notNull(id, "The item id must not be null");
        Assert.notNull(updatedItem, "The updated item object must not be null");
        Map<String, Object> updates = new HashMap<>();

        if (updatedItem.getName() != null) {
            updates.put("name", updatedItem.getName());
        }
        if (updatedItem.getDescription() != null) {
            updates.put("description", updatedItem.getDescription());
        }
        if (updatedItem.getStock() != null) {
            updates.put("stock", updatedItem.getStock());
        }
        if (updatedItem.getPrice() != null) {
            updates.put("price", updatedItem.getPrice());
        }
        if (updatedItem.getCategory() != null) {
            updates.put("category", updatedItem.getCategory());
        }
        if (updatedItem.getManufacturer() != null) {
            updates.put("manufacturer", updatedItem.getManufacturer());
        }

        if (!updates.isEmpty()) {
            ApiFuture<WriteResult> writeResult = db.collection("supplierItems").document(id).update(updates);
            writeResult.get(); // Wait for update to complete
        }

        return findItem(id); // Return updated item
    }

    public void deleteItem(String id) {
        Assert.notNull(id, "The item id must not be null");
        db.collection("supplierItems").document(id).delete();
    }

    public Optional<Order> findOrder(String id) throws ExecutionException, InterruptedException {
        Assert.notNull(id, "The order id must not be null");
        DocumentReference docRef = db.collection("supplierOrders").document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        return Optional.ofNullable(documentToOrder(document));
    }

    public Collection<Order> getAllOrders() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> query = db.collection("supplierOrders").get();
        QuerySnapshot querySnapshot = query.get();
        List<Order> orders = new ArrayList<>();
        for (QueryDocumentSnapshot document : querySnapshot) {
            Order order = document.toObject(Order.class);
            order.setId(document.getId());
            orders.add(order);
        }
        return orders;
    }

    public Optional<Order> addOrder(Order order) throws ExecutionException, InterruptedException {
        Assert.notNull(order, "The order object must not be null");

        // Add order to Firestore
        DocumentReference docRef = db.collection("supplierOrders").document();
        ApiFuture<WriteResult> future = docRef.set(order);
        future.get(); // Wait for operation to complete

        if (order.getStatus() == OrderStatus.PENDING) {
            // Fetch all items from Firestore
            Collection<Item> allItems = getAllItems();

            // Check if all items in the order exist and have sufficient stock
            boolean allItemsAvailable = order.getItems().entrySet().stream()
                    .allMatch(entry -> {
                        String itemId = entry.getKey();
                        int quantity = entry.getValue();
                        Optional<Item> foundItem = allItems.stream().filter(item -> item.getId().equals(itemId)).findFirst();
                        if (foundItem.isPresent()) {
                            return foundItem.get().getStock() >= quantity; // Item found and has sufficient stock
                        } else {
                            return false; // Item not found
                        }
                    });

            // If all items are available with sufficient stock, set order status to CONFIRMED and update item stock
            if (allItemsAvailable) {
                order.getItems().forEach((itemId, quantity) -> {
                    Optional<Item> foundItem = allItems.stream().filter(item -> item.getId().equals(itemId)).findFirst();
                    foundItem.ifPresent(item -> {
                        int newStock = item.getStock() - quantity;
                        item.setStock(newStock);
                        try {
                            updateItem(item.getId(), item); // Update item stock in Firestore
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace(); // Handle exception properly
                        }
                    });
                });
                order.setStatus(OrderStatus.CONFIRMED);
            } else {
                // Set order status to ROOTSTOCK or CANCELLED based on item availability
                order.getItems().forEach((itemId, quantity) -> {
                    Optional<Item> foundItem = allItems.stream().filter(item -> item.getId().equals(itemId)).findFirst();
                    foundItem.ifPresent(item -> {
                        if (item.getStock() < quantity) {
                            order.setStatus(OrderStatus.ROOTSTOCK);
                        } else {
                            order.setStatus(OrderStatus.CANCELLED);
                        }
                    });
                });
            }

            // Update the order in Firestore with the updated status
            docRef.set(order).get(); // Wait for operation to complete
        }

        // Return the added order
        return Optional.of(order);
    }

    public Optional<Order> updateOrder(String id, Order updatedOrder) throws ExecutionException, InterruptedException {
        Assert.notNull(id, "The order id must not be null");
        Assert.notNull(updatedOrder, "The updated order object must not be null");

        Map<String, Object> updates = new HashMap<>();
        updates.put("masterId", updatedOrder.getMasterId());
        updates.put("status", updatedOrder.getStatus().toString());
        updates.put("address", updatedOrder.getAddress());
        updates.put("items", updatedOrder.getItems());

        ApiFuture<WriteResult> writeResult = db.collection("supplierOrders").document(id).update(updates);
        writeResult.get(); // Wait for update to complete

        return findOrder(id); // Return updated order
    }

    public void deleteOrder(String id) {
        Assert.notNull(id, "The order id must not be null");
        db.collection("supplierOrders").document(id).delete();
    }
}
