package be.kuleuven.restservice.controllers;

import be.kuleuven.restservice.domain.Item;
import be.kuleuven.restservice.domain.ItemsRepository;
import be.kuleuven.restservice.domain.Order;
import be.kuleuven.restservice.exceptions.MealNotFoundException;
import be.kuleuven.restservice.exceptions.OrderNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@RestController
public class RestRpcController {

    private final ItemsRepository itemsRepository;

    @Autowired
    RestRpcController(ItemsRepository itemsRepository) {
        this.itemsRepository = itemsRepository;
    }

    @GetMapping("/items/{id}")
    Item getMealById(@PathVariable String id) {
        Optional<Item> item = itemsRepository.findMeal(id);

        return item.orElseThrow(() -> new MealNotFoundException(id));
    }

    @GetMapping("/items")
    Collection<Item> getItems() {
        return itemsRepository.getAllMeal();
    }

    @PostMapping("/items")
    Item addMeal(@RequestBody Item item) {
        Optional<Item> newItem = itemsRepository.addMeal(item);
        return newItem.orElseThrow();
    }

    @PutMapping("/items/{id}")
    void updateMeal(@RequestBody Item item) {
        itemsRepository.updateMeal(item);
    }

    @DeleteMapping("/items/{id}")
    void deleteItemById(@PathVariable String id) {
        itemsRepository.deleteMeal(id);
    }


    @GetMapping("/orders/{id}")
    Order getOrderById(@PathVariable String id) {
        return itemsRepository.findOrder(id).orElseThrow(() -> new OrderNotFoundException(id));
    }

    @GetMapping("/orders")
    Collection<Order> getOrders() {
        return itemsRepository.getAllOrder();
    }

    @PostMapping("/orders")
    void addOrder(@RequestBody Order order) {
        itemsRepository.addOrder(order);
    }

}
