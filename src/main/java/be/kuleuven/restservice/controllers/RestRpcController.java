package be.kuleuven.restservice.controllers;

import be.kuleuven.restservice.domain.ApiResponse;
import be.kuleuven.restservice.domain.Item;
import be.kuleuven.restservice.domain.ItemsRepository;
import be.kuleuven.restservice.domain.Order;
import be.kuleuven.restservice.exceptions.ItemNotFoundException;
import be.kuleuven.restservice.exceptions.OrderNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

@RestController
public class RestRpcController {

    private final ItemsRepository itemsRepository;

    @Autowired
    RestRpcController(ItemsRepository itemsRepository) {
        this.itemsRepository = itemsRepository;
    }

    @GetMapping("/items/{id}")
    ResponseEntity<ApiResponse<Item>> getItem(@PathVariable String id) {
        Optional<Item> item = itemsRepository.findItem(id);
        if (item.isPresent()) {
            return ResponseEntity.ok(new ApiResponse<>(true, item.get(), "Item retrieved successfully."));
        } else {
            throw new ItemNotFoundException(id);
        }
    }

    @GetMapping("/items")
    ResponseEntity<ApiResponse<Collection<Item>>> getItems() {
        Collection<Item> items = itemsRepository.getAllItems();
        return ResponseEntity.ok(new ApiResponse<>(true, items, "Items retrieved successfully."));
    }

    @PostMapping("/items")
    ResponseEntity<ApiResponse<Item>> addItem(@RequestBody Item item) {
        Optional<Item> newItem = itemsRepository.addItem(item);
        return newItem.map(value -> ResponseEntity.ok(new ApiResponse<>(true, value, "Item added successfully.")))
                .orElseThrow(() -> new RuntimeException("Failed to add item."));
    }

    @PutMapping("/items/{id}")
    ResponseEntity<ApiResponse<Item>> updateItem(@PathVariable String id, @RequestBody Item item) {
        Optional<Item> updatedItem = itemsRepository.updateItem(id, item);
        if (updatedItem.isPresent()) {
            return ResponseEntity.ok(new ApiResponse<>(true, updatedItem.get(), "Item updated successfully."));
        } else {
            throw new ItemNotFoundException(id);
        }
    }

    @DeleteMapping("/items/{id}")
    ResponseEntity<ApiResponse<Void>> deleteItem(@PathVariable String id) {
        itemsRepository.deleteItem(id);
        return ResponseEntity.ok(new ApiResponse<>(true, null, "Item deleted successfully."));
    }

    @GetMapping("/orders/{id}")
    ResponseEntity<ApiResponse<Order>> getOrder(@PathVariable String id) {
        Optional<Order> order = itemsRepository.findOrder(id);
        return order.map(value -> ResponseEntity.ok(new ApiResponse<>(true, value, "Order retrieved successfully.")))
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    @GetMapping("/orders")
    ResponseEntity<ApiResponse<Collection<Order>>> getOrders() {
        Collection<Order> orders = itemsRepository.getAllOrders();
        return ResponseEntity.ok(new ApiResponse<>(true, orders, "Orders retrieved successfully."));
    }

    @PostMapping("/orders")
    ResponseEntity<ApiResponse<Order>> addOrder(@RequestBody Order order) {
        if (order.setItems(new HashMap<>(order.getItems()))) {
            itemsRepository.addOrder(order);
            return ResponseEntity.ok(new ApiResponse<>(true, order, "Order added successfully."));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, null, "Insufficient stock for one or more items."));
        }
    }

    @PutMapping("/orders/{id}")
    ResponseEntity<ApiResponse<Order>> updateOrder(@PathVariable String id, @RequestBody Order order) {
        Optional<Order> updatedOrder = itemsRepository.editOrder(id, order);
        if (updatedOrder.isPresent()) {
            return ResponseEntity.ok(new ApiResponse<>(true, updatedOrder.get(), "Order updated successfully."));
        } else {
            throw new OrderNotFoundException(id);
        }
    }

    @DeleteMapping("/orders/{id}")
    ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable String id) {
        itemsRepository.deleteOrder(id);
        return ResponseEntity.ok(new ApiResponse<>(true, null, "Order deleted successfully."));
    }
}
