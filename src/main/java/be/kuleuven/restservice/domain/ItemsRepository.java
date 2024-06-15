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
import org.springframework.beans.factory.annotation.Value;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
public class ItemsRepository {
    private Firestore db;
    @Value("${firebase.collection.orders}")
    private String ordersCollection;
    @Value("${firebase.collection.items}")
    private String itemsCollection;

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
        DocumentReference docRef = db.collection(itemsCollection).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        return Optional.ofNullable(documentToItem(document));
    }

    public Collection<Item> getAllItems() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> query = db.collection(itemsCollection).get();
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
        if (newItem.getName() == null || newItem.getDescription() == null || newItem.getStock() == null || newItem.getPrice() == null || newItem.getCategory() == null || newItem.getManufacturer() == null) {
            throw new IllegalArgumentException("One or more required fields of the item are null.");
        }

        // Create a map to hold only the specified fields
        Map<String, Object> itemData = new HashMap<>();
        itemData.put("name", newItem.getName());
        itemData.put("description", newItem.getDescription());
        itemData.put("stock", newItem.getStock());
        itemData.put("price", newItem.getPrice());
        itemData.put("category", newItem.getCategory());
        itemData.put("manufacturer", newItem.getManufacturer());

        // Add item data to Firestore
        DocumentReference docRef = db.collection(itemsCollection).document();
        ApiFuture<WriteResult> future = docRef.set(itemData);
        future.get(); // Wait for operation to complete


        // Return the added item with only the correct data
        Item addedItem = new Item();
        addedItem.setId(docRef.getId());
        addedItem.setName(newItem.getName());
        addedItem.setDescription(newItem.getDescription());
        addedItem.setStock(newItem.getStock());
        addedItem.setPrice(newItem.getPrice());
        addedItem.setCategory(newItem.getCategory());
        addedItem.setManufacturer(newItem.getManufacturer());

        // Return the added item
        return Optional.of(addedItem);
    }


    public Optional<Item> updateItem(String id, Item updatedItem) throws ExecutionException, InterruptedException {
        Assert.notNull(id, "The item id must not be null");
        Assert.notNull(updatedItem, "The updated item object must not be null");

        if (updatedItem.getName() == null && updatedItem.getDescription() == null && updatedItem.getStock() == null && updatedItem.getPrice() == null && updatedItem.getCategory() == null && updatedItem.getManufacturer() == null) {
            throw new IllegalArgumentException("No updatable values is sent.");
        }
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
            ApiFuture<WriteResult> writeResult = db.collection(itemsCollection).document(id).update(updates);
            writeResult.get(); // Wait for update to complete
        }

        return findItem(id); // Return updated item
    }

    public void deleteItem(String id) throws ExecutionException, InterruptedException {
        Assert.notNull(id, "The item id must not be null");
        // Check if the item exists
        ApiFuture<DocumentSnapshot> future = db.collection(itemsCollection).document(id).get();
        DocumentSnapshot document = future.get();
        if (!document.exists()) {
            throw new IllegalArgumentException("Item with id " + id + " does not exist.");
        }
        db.collection(itemsCollection).document(id).delete();
    }

    public Optional<Order> findOrder(String id) throws ExecutionException, InterruptedException {
        Assert.notNull(id, "The order id must not be null");
        DocumentReference docRef = db.collection(ordersCollection).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        return Optional.ofNullable(documentToOrder(document));
    }

    public Collection<Order> getAllOrders() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> query = db.collection(ordersCollection).get();
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
        if (order.getMasterId() == null || order.getAddress() == null || order.getStatus() == null || order.getFirstName() == null || order.getLastName() == null || order.getItems() == null) {
            throw new IllegalArgumentException("One or more required fields of the order are null.");
        }

        // Fetch all items from Firestore
        Collection<Item> allItems = getAllItems(); // Wait for items to be fetched

        // Check if all items in the order exist in the items collection
        Set<String> availableItemIds = allItems.stream()
                .map(Item::getId)
                .collect(Collectors.toCollection(HashSet::new));

        for (String itemsId : order.getItems().keySet()) {
            if (!availableItemIds.contains(itemsId)) {
                throw new IllegalArgumentException("Item with ID " + itemsId + " does not exist.");
            }
        }

        // Create order data
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("masterId", order.getMasterId());
        orderData.put("firstName", order.getFirstName());
        orderData.put("lastName", order.getLastName());
        orderData.put("address", order.getAddress());
        orderData.put("status", order.getStatus().toString());

        // Add items to order data
        Map<String, Integer> itemsData = new HashMap<>(order.getItems());
        orderData.put("items", itemsData);

        // Add order to Firestore
        String orderId = order.getMasterId();
        DocumentReference docRef = db.collection(ordersCollection).document(orderId);
        ApiFuture<WriteResult> future = docRef.set(orderData);
        future.get(); // Wait for operation to complete
        order.setId(docRef.getId());

        // Handle order status logic
        if (order.getStatus() == OrderStatus.PENDING) {
            // Check if all items are available with sufficient stock
            boolean allItemsAvailable = order.getItems().entrySet().stream()
                    .allMatch(entry -> {
                        String itemId = entry.getKey();
                        int quantity = entry.getValue();
                        Optional<Item> foundItem = allItems.stream().filter(item -> item.getId().equals(itemId)).findFirst();
                        return foundItem.filter(item -> item.getStock() >= quantity).isPresent();
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
                            order.setStatus(OrderStatus.ROOTSTOCK);
                        }
                    });
                });
            }

            // Update the order in Firestore with the updated status
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("status", order.getStatus().toString());
            docRef.update(updateData).get();
        }

        // Return the added order with only the correct data
        Order addedOrder = new Order();
        addedOrder.setId(docRef.getId());
        addedOrder.setMasterId(order.getMasterId());
        addedOrder.setFirstName(order.getFirstName());
        addedOrder.setLastName(order.getLastName());
        addedOrder.setItems(order.getItems());
        addedOrder.setAddress(order.getAddress());
        addedOrder.setStatus(order.getStatus());

        return Optional.of(addedOrder);
    }

    public Optional<Order> updateOrder(String id, Order updatedOrder) throws ExecutionException, InterruptedException {
        Assert.notNull(id, "The order id must not be null");
        Assert.notNull(updatedOrder, "The updated order object must not be null");
        if ( updatedOrder.getFirstName() == null && updatedOrder.getLastName() == null && updatedOrder.getAddress() == null && updatedOrder.getStatus() == null) {
            throw new IllegalArgumentException("No updatable values is sent.");
        }

        Map<String, Object> updates = new HashMap<>();
        if (updatedOrder.getFirstName() != null) {
            updates.put("firstName", updatedOrder.getFirstName());
        }
        if (updatedOrder.getFirstName() != null) {
            updates.put("lastName", updatedOrder.getLastName());
        }
        if (updatedOrder.getStatus() != null) {
            updates.put("status", updatedOrder.getStatus().toString());

            // Check if order status changes from CONFIRMED to CANCELLED
            if (updatedOrder.getStatus() == OrderStatus.CANCELLED) {
                // Retrieve the original order from Firestore
                Optional<Order> originalOrder = findOrder(id);
                if (originalOrder.isPresent() && originalOrder.get().getStatus() == OrderStatus.CONFIRMED) {
                    // Add items back to stock
                    originalOrder.get().getItems().forEach((itemId, quantity) -> {
                        try {
                            Optional<Item> foundItem = findItem(itemId);
                            foundItem.ifPresent(item -> {
                                item.setStock(item.getStock() + quantity); // Add quantity back to stock
                                try {
                                    updateItem(itemId, item); // Update item stock in Firestore
                                } catch (InterruptedException | ExecutionException e) {
                                    e.printStackTrace(); // Handle exception properly
                                }
                            });
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace(); // Handle exception properly
                        }
                    });
                }
            }
        }
        if (updatedOrder.getAddress() != null) {
            updates.put("address", updatedOrder.getAddress());
        }

        // Perform the update in Firestore
        ApiFuture<WriteResult> writeResult = db.collection(ordersCollection).document(id).update(updates);
        writeResult.get(); // Wait for update to complete

        return findOrder(id); // Return updated order
    }

    public void deleteOrder(String id) throws ExecutionException, InterruptedException {
        Assert.notNull(id, "The order id must not be null");
        // Check if the order exists
        ApiFuture<DocumentSnapshot> future = db.collection(ordersCollection).document(id).get();
        DocumentSnapshot document = future.get();
        if (!document.exists()) {
            throw new IllegalArgumentException("Order with id " + id + " does not exist.");
        }
        db.collection(ordersCollection).document(id).delete();
    }
}
