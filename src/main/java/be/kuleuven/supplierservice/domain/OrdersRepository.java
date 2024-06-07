package be.kuleuven.supplierservice.domain;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class OrdersRepository {

    private static final Map<String, Order> orders = new HashMap<>();

    public String generateOrderId() {
        String uniqueID;
        do {
            uniqueID = UUID.randomUUID().toString();
        } while (orders.containsKey(uniqueID));
        return uniqueID;
    }

    // Adds an order to the repository and returns it
    public Order addOrder(Order order) {
        orders.put(order.getOrderId(), order);
        return order;
    }

    // Finds an order by ID
    public Optional<Order> findOrderById(String id) {
        Assert.notNull(id,"order id should not be null");
        return Optional.ofNullable(orders.get(id));
    }


    // Updates an existing order and returns the updated order
    public Order updateOrder(String orderId, Order order) {
        orders.put(orderId, order);
        return order;
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
