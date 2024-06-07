package be.kuleuven.foodrestservice.controllers;

import be.kuleuven.foodrestservice.domain.Meal;
import be.kuleuven.foodrestservice.domain.MealsRepository;
import be.kuleuven.supplierservice.domain.Order;
import be.kuleuven.foodrestservice.domain.OrdersRepository;
import be.kuleuven.foodrestservice.exceptions.MealNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import java.util.*;


@RestController
public class MealsRestController {

    private final MealsRepository mealsRepository;
    private final OrdersRepository ordersRepository;

    @Autowired
    MealsRestController(MealsRepository mealsRepository, OrdersRepository ordersRepository) {
        this.mealsRepository = mealsRepository;
        this.ordersRepository = ordersRepository;
    }

    @GetMapping("/rest/meals/{id}")
    EntityModel<Meal> getMealById(@PathVariable String id) {
        Meal meal = mealsRepository.findMeal(id).orElseThrow(() -> new MealNotFoundException(id));

        return mealToEntityModel(id, meal);
    }

    @GetMapping("/rest/meals")
    CollectionModel<EntityModel<Meal>> getMeals() {
        Collection<Meal> meals = mealsRepository.getAllMeal();

        List<EntityModel<Meal>> mealEntityModels = new ArrayList<>();
        for (Meal m : meals) {
            EntityModel<Meal> em = mealToEntityModel(m.getId(), m);
            mealEntityModels.add(em);
        }
        return CollectionModel.of(mealEntityModels,
                linkTo(methodOn(MealsRestController.class).getMeals()).withSelfRel());
    }
    @GetMapping("/rest/meals/cheapest")
    public EntityModel<Meal> getCheapestMeal() {
        Meal meal = mealsRepository.getAllMeal().stream()
                .min(Comparator.comparing(Meal::getPrice))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No meals found"));
        return EntityModel.of(meal,
                linkTo(methodOn(MealsRestController.class).getMealById(meal.getId())).withSelfRel(),
                linkTo(methodOn(MealsRestController.class).getMeals()).withRel("meals"));
    }

    @GetMapping("/rest/meals/largest")
    public EntityModel<Meal> getLargestMeal() {
        Meal meal = mealsRepository.getAllMeal().stream()
                .max(Comparator.comparing(Meal::getKcal))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No meals found"));
        return EntityModel.of(meal,
                linkTo(methodOn(MealsRestController.class).getMealById(meal.getId())).withSelfRel(),
                linkTo(methodOn(MealsRestController.class).getMeals()).withRel("meals"));
    }

    @PostMapping("/rest/meals")
    public ResponseEntity<EntityModel<Meal>> addMeal(@RequestBody Meal newMeal) {
        mealsRepository.addMeal(newMeal);
        return new ResponseEntity<>(EntityModel.of(newMeal,
                linkTo(methodOn(MealsRestController.class).getMealById(newMeal.getId())).withSelfRel(),
                linkTo(methodOn(MealsRestController.class).getMeals()).withRel("meals")), HttpStatus.CREATED);
    }

    @PutMapping("/rest/meals/{id}")
    public ResponseEntity<EntityModel<Meal>> updateMeal(@PathVariable String id, @RequestBody Meal updatedMeal) {
        Meal meal = mealsRepository.updateMeal(id, updatedMeal);
        return ResponseEntity.ok(EntityModel.of(meal,
                linkTo(methodOn(MealsRestController.class).getMealById(meal.getId())).withSelfRel(),
                linkTo(methodOn(MealsRestController.class).getMeals()).withRel("meals")));
    }

    @DeleteMapping("/rest/meals/{id}")
    public ResponseEntity<?> deleteMeal(@PathVariable String id) {
        mealsRepository.deleteMeal(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/rest/orders")
    public ResponseEntity<EntityModel<Order>> addOrder(@RequestBody Order newOrder) {
        Order savedOrder = ordersRepository.addOrder(newOrder);
        return new ResponseEntity<>(EntityModel.of(savedOrder,
                linkTo(methodOn(MealsRestController.class).addOrder(newOrder)).withSelfRel()), HttpStatus.CREATED);
    }

    private EntityModel<Meal> mealToEntityModel(String id, Meal meal) {
        return EntityModel.of(meal,
                linkTo(methodOn(MealsRestController.class).getMealById(id)).withSelfRel(),
                linkTo(methodOn(MealsRestController.class).getMeals()).withRel("rest/meals"));
    }

}
