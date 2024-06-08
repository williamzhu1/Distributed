package be.kuleuven.restservice.domain;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class ItemsRepository {
    private static final Map<String, Item> items = new HashMap<>();
    private static final Map<String, Order> orders = new HashMap<>();

    @PostConstruct
    public void initData(){
        Item a = new Item();
        a.setId("5268203c-de76-4921-a3e3-439db69c462a");
        items.put(a.getId(),a);
    }

    public Optional<Item> findItem(String id) {
        Assert.notNull(id, "The item id must not be null");
        Item item = items.get(id);
        return Optional.ofNullable(item);
    }

    public Optional<Item> addItem(Item newItem){
        Assert.notNull(newItem, "The meal object must not be null");
        do {
            newItem.setId(UUID.randomUUID().toString());
        } while (items.containsKey(newItem.id));
        for (Item item : items.values()) {
            // Check if the current meal object is equal to the new meal object
            if (item.equals(newItem)) {
                return Optional.empty();
            }
        }
        items.put(newItem.getId(), newItem);
        return Optional.of(newItem);
    }

    public Optional<Item> updateItem(Item updateItem) {
        Assert.notNull(updateItem, "The updated meal object must not be null");

        // Check if the meal exists in the repository
        if (items.containsKey(updateItem.id)) {
            items.put(updateItem.id, updateItem);
            return Optional.of(updateItem);
        } else {
            return Optional.empty();
        }
    }

    // Method to delete a meal
    public void deleteItem(String id) {
        items.remove(id);
    }

    public Collection<Item> getAllItems() {
        return items.values();
    }

    // ORDERS

    // Adds an order to the repository and returns it
    public Optional<Order> addOrder(Order order) {
        // Generate a unique ID for the order
        do {
            order.setId(UUID.randomUUID().toString());
        } while (orders.containsKey(order.getId()));

        // Validate that all items in the order exist in the items repository
        int validCount = 0;
        for (Item item : order.getItems().keySet()) {
            if (items.containsKey(item.getId())) {
                validCount++;
            }
        }

        // If all items are valid, add the order to the orders map
        if (validCount == order.getItems().size()) {
            orders.put(order.getId(), order);
            return Optional.of(order);
        } else {
            return Optional.empty();
        }
    }

    // Finds an order by ID
    public Optional<Order> findOrder(String id) {
        Assert.notNull(id, "The order id must not be null");
        Order order = orders.get(id);
        return Optional.ofNullable(order);
    }

    // Updates an existing order and returns the updated order
    public Optional<Order> updateOrder(Order updateOrder) {
        Assert.notNull(updateOrder, "The updated order object must not be null");

        // Check if the meal exists in the repository
        if (orders.containsKey(updateOrder.id)) {
            orders.put(updateOrder.id, updateOrder);
            return Optional.of(updateOrder);
        } else {
            return Optional.empty();
        }
    }

    // Deletes an order by ID
    public void deleteOrder(String orderId) {
        orders.remove(orderId);
    }

    // Returns all orders
    public Collection<Order> getAllOrders() {
        return orders.values();
    }

}
