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
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@RestController
public class RestRpcController {

    private final ItemsRepository itemsRepository;

    @Autowired
    public RestRpcController(ItemsRepository itemsRepository) {
        this.itemsRepository = itemsRepository;
    }

    @GetMapping("/items/{id}")
    ResponseEntity<ApiResponse<Item>> getItem(@PathVariable String id) {
        try {
            Optional<Item> item = itemsRepository.findItem(id);
            if (item.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(true, item.get(), "Item retrieved successfully."));
            } else {
                throw new ItemNotFoundException(id);
            }
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, null, "Failed to retrieve item."));
        }
    }

    @GetMapping("/items")
    ResponseEntity<ApiResponse<Collection<Item>>> getItems() {
        try {
            Collection<Item> items = itemsRepository.getAllItems();
            return ResponseEntity.ok(new ApiResponse<>(true, items, "Items retrieved successfully."));
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, null, "Failed to retrieve items."));
        }
    }

    @PostMapping("/items")
    ResponseEntity<ApiResponse<Item>> addItem(@RequestBody Item item) {
        try {
            Optional<Item> newItem = itemsRepository.addItem(item);
            if (newItem.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(true, newItem.get(), "Item added successfully."));
            } else {
                throw new RuntimeException("Failed to add item.");
            }
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, null, "Failed to add item."));
        }
    }

    @PutMapping("/items/{id}")
    ResponseEntity<ApiResponse<Item>> updateItem(@PathVariable String id, @RequestBody Item item) {
        try {
            Optional<Item> updatedItem = itemsRepository.updateItem(id, item);
            if (updatedItem.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(true, updatedItem.get(), "Item updated successfully."));
            } else {
                throw new ItemNotFoundException(id);
            }
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, null, "Failed to update item."));
        }
    }

    @DeleteMapping("/items/{id}")
    ResponseEntity<ApiResponse<Void>> deleteItem(@PathVariable String id) {
        try {
            itemsRepository.deleteItem(id);
            return ResponseEntity.ok(new ApiResponse<>(true, null, "Item deleted successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, null, "Failed to delete item."));
        }
    }

    @GetMapping("/orders/{id}")
    ResponseEntity<ApiResponse<Order>> getOrder(@PathVariable String id) {
        try {
            Optional<Order> order = itemsRepository.findOrder(id);
            return order.map(value -> ResponseEntity.ok(new ApiResponse<>(true, value, "Order retrieved successfully.")))
                    .orElseThrow(() -> new OrderNotFoundException(id));
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, null, "Failed to retrieve order."));
        }
    }

    @GetMapping("/orders")
    ResponseEntity<ApiResponse<Collection<Order>>> getOrders() {
        try {
            Collection<Order> orders = itemsRepository.getAllOrders();
            return ResponseEntity.ok(new ApiResponse<>(true, orders, "Orders retrieved successfully."));
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, null, "Failed to retrieve orders."));
        }
    }

    @PostMapping("/orders")
    ResponseEntity<ApiResponse<Order>> addOrder(@RequestBody Order order) {
        try {
            Optional<Order> newOrder = itemsRepository.addOrder(order);
            if (newOrder.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(true, newOrder.get(), "Order added successfully."));
            } else {
                throw new RuntimeException("Failed to add order.");
            }
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, null, "Failed to add order."));
        }
    }

    @PutMapping("/orders/{id}")
    ResponseEntity<ApiResponse<Order>> updateOrder(@PathVariable String id, @RequestBody Order order) {
        try {
            Optional<Order> updatedOrder = itemsRepository.updateOrder(id, order);
            if (updatedOrder.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(true, updatedOrder.get(), "Order updated successfully."));
            } else {
                throw new OrderNotFoundException(id);
            }
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, null, "Failed to update order."));
        }
    }

    @DeleteMapping("/orders/{id}")
    ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable String id) {
        try {
            itemsRepository.deleteOrder(id);
            return ResponseEntity.ok(new ApiResponse<>(true, null, "Order deleted successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, null, "Failed to delete order."));
        }
    }
}
