package be.kuleuven.restservice.controllers;

import be.kuleuven.restservice.domain.*;
import be.kuleuven.restservice.exceptions.ItemNotFoundException;
import be.kuleuven.restservice.exceptions.OrderNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class MedRestRpcController {
    private final ItemsRepository itemsRepository;

    @Autowired
    MedRestRpcController(ItemsRepository itemsRepository) {
        this.itemsRepository = itemsRepository;
    }

    @GetMapping("/items/{id}")
    Item getMealById(@PathVariable String id) {
        Optional<Item> item = itemsRepository.findItem(id);

        return item.orElseThrow(() -> new ItemNotFoundException(id));
    }
    @PostMapping("/items")
    Item addMeal(@RequestBody Item item) {
        Optional<Item> newItem = itemsRepository.addItem(item);
        return newItem.orElseThrow();
    }

    @PutMapping("/items")
    Item updateMeal(@RequestBody Item item) {
        Optional<Item> updatedItem = itemsRepository.updateItem(item);
        return updatedItem.orElseThrow();
    }

    @DeleteMapping("/items/{id}")
    void deleteMealById(@PathVariable String id) {
        itemsRepository.deleteItem(id);
    }

    @GetMapping("/items")
    Collection<Item> getItems(){return itemsRepository.getAllItems();}

    // order

    @GetMapping("/orders/{id}")
    Order getOrderById(@PathVariable String id) {
        Optional<Order> order = itemsRepository.findOrder(id);

        return order.orElseThrow(() -> new OrderNotFoundException(id));
    }

    @GetMapping("/orders")
    Collection<Order> getOrders() {
        return itemsRepository.getAllOrders();
    }

    @PostMapping("/orders")
    Order addOrder(@RequestBody Order order) {
        Optional<Order> newOrder = itemsRepository.addOrder(order);
        return newOrder.orElseThrow();
    }

    @PutMapping("/orders")
    Order updateOrder(@RequestBody Order order) {
        Optional<Order> updatedOrder = itemsRepository.updateOrder(order);
        return updatedOrder.orElseThrow();
    }

}
