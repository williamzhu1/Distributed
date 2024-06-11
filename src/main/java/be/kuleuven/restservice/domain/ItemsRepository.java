package be.kuleuven.restservice.domain;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class ItemsRepository {
    // map: id -> meal
    private static final Map<String, Item> items = new HashMap<>();
    private static final Map<String, Order> orders = new HashMap<>();

    @PostConstruct
    public void initData() {

        Item a = new Item();
        a.setId("5268203c-de76-4921-a3e3-439db69c462a");
        a.setName("Monkey Brain Soup");
        a.setDescription("Good for brain");
        a.setStock(10);
        a.setPrice((10.00));

        items.put(a.getId(), a);

        Item b = new Item();
        b.setId("4237681a-441f-47fc-a747-8e0169bacea1");
        b.setName("Fish Bladder");
        b.setDescription("BLU BLU BLU");
        b.setStock(10);
        b.setPrice((7.00));

        items.put(b.getId(), b);

        Item c = new Item();
        c.setId("cfd1601f-29a0-485d-8d21-7607ec0340c8");
        c.setName("Gingsen");
        c.setDescription("100 year old GINGSEN");
        c.setStock(10);
        c.setPrice(5.00);

        items.put(c.getId(), c);

        Order estebanOrder = new Order();
        estebanOrder.setId("cfd1601f-29a0-485d-8d21-7607ec0340c1");
        estebanOrder.setMasterId("1");
        estebanOrder.setStatus(OrderStatus.PENDING);
        estebanOrder.setAddress("Leuven");
        estebanOrder.addItem(c,3);
        estebanOrder.addItem(a,2);
        orders.put(estebanOrder.id,estebanOrder);
    }

    public Optional<Item> findItem(String id) {
        Assert.notNull(id, "The meal id must not be null");
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

    public Optional<Item> updateItem(String id,Item updatedItem){
        Assert.notNull(updatedItem, "The updated meal object must not be null");

        // Check if the meal exists in the repository
        if (items.containsKey(id)) {
            items.put(id, updatedItem);
            return Optional.of(updatedItem);
        } else {
            return Optional.empty();
        }
    }

    public void deleteItem(String id){
        Assert.notNull(id, "The meal id must not be null");
        items.remove(id);
    }

    public Collection<Item> getAllItems() {
        return items.values();
    }

    public Optional<Order> findOrder(String id) {
        Assert.notNull(id, "The order id must not be null");
        Order order = orders.get(id);
        return Optional.ofNullable(order);
    }

    public Collection<Order> getAllOrders() {
        return orders.values();
    }

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

    // Updates an existing order by its ID
    public Optional<Order> editOrder(String id, Order updatedOrder) {
        Assert.notNull(updatedOrder, "The updated order object must not be null");

        // Check if the order exists in the repository
        if (orders.containsKey(id)) {
            updatedOrder.setId(id);  // Ensure the ID remains the same
            orders.put(id, updatedOrder);
            return Optional.of(updatedOrder);
        } else {
            return Optional.empty();
        }
    }

    // Deletes an order by its ID
    public void deleteOrder(String id) {
        Assert.notNull(id, "The order id must not be null");
        orders.remove(id);
    }
}
