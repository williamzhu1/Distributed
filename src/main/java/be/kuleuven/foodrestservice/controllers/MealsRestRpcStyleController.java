package be.kuleuven.foodrestservice.controllers;

import be.kuleuven.foodrestservice.domain.Meal;
import be.kuleuven.foodrestservice.domain.MealsRepository;
import be.kuleuven.supplierservice.domain.Order;
import be.kuleuven.foodrestservice.exceptions.MealNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import be.kuleuven.foodrestservice.domain.OrdersRepository;
import java.util.Collection;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;

@RestController
public class MealsRestRpcStyleController {

    private final MealsRepository mealsRepository;
    private final OrdersRepository ordersRepository;
    @Autowired
    MealsRestRpcStyleController(MealsRepository mealsRepository, OrdersRepository ordersRepository) {
        this.mealsRepository = mealsRepository;
        this.ordersRepository = ordersRepository;
    }

    @GetMapping("/restrpc/meals/{id}")
    Meal getMealById(@PathVariable String id) {
        Optional<Meal> meal = mealsRepository.findMeal(id);

        return meal.orElseThrow(() -> new MealNotFoundException(id));
    }

    @GetMapping("/restrpc/meals")
    Collection<Meal> getMeals() {
        return mealsRepository.getAllMeal();
    }

    // Function to get the cheapest meal
    @GetMapping("/restrpc/meals/cheapest")
    public Meal getCheapestMeal() {
        return mealsRepository.getAllMeal().stream()
                .min(Comparator.comparing(Meal::getPrice))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No meals found"));
    }

    // Function to get the largest meal
    @GetMapping("/restrpc/meals/largest")
    public Meal getLargestMeal() {
        return mealsRepository.getAllMeal().stream()
                .max(Comparator.comparing(Meal::getKcal))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No meals found"));
    }

    // Function to add a new meal
    @PostMapping("/restrpc/meals")
    public ResponseEntity<Meal> addMeal(@RequestBody Meal newMeal) {
        mealsRepository.addMeal(newMeal);
        return new ResponseEntity<>(newMeal, HttpStatus.CREATED);
    }

    // Function to update an existing meal
    @PutMapping("/restrpc/meals/{id}")
    public ResponseEntity<Meal> updateMeal(@PathVariable String id, @RequestBody Meal updatedMeal) {
        Meal meal = mealsRepository.updateMeal(id, updatedMeal);
        return ResponseEntity.ok(meal);
    }

    // Function to delete a meal
    @DeleteMapping("/restrpc/meals/{id}")
    public ResponseEntity<Void> deleteMeal(@PathVariable String id) {
        mealsRepository.deleteMeal(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/restrpc/orders")
    public ResponseEntity<Order> addOrder(@RequestBody Order newOrder) {
        Order savedOrder = ordersRepository.addOrder(newOrder); // Add the new order using the repository
        return new ResponseEntity<>(savedOrder, HttpStatus.CREATED); // Return the saved order with a 201 Created status
    }

}
