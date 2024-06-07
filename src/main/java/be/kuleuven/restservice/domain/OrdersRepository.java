package be.kuleuven.restservice.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class OrdersRepository {
    private final Map<String, Order> orders = new HashMap<>();

    // Adds an order to the repository and returns it
    public Order addOrder(Order order) {
        orders.put(order.getOrderId(), order);
        return order;
    }

    // Finds an order by ID
    public Order findOrderById(String id) {
        return orders.get(id);
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
