package be.kuleuven.restservice.controllers;

import be.kuleuven.restservice.domain.ApiResponse;
import be.kuleuven.restservice.domain.Item;
import be.kuleuven.restservice.domain.ItemsRepository;
import be.kuleuven.restservice.domain.Order;
import be.kuleuven.restservice.exceptions.ItemNotFoundException;
import be.kuleuven.restservice.exceptions.OrderNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
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
    ResponseEntity<EntityModel<ApiResponse<Item>>> getItem(@PathVariable String id) {
        try {
            Optional<Item> item = itemsRepository.findItem(id);
            if (item.isPresent()) {
                EntityModel<ApiResponse<Item>> resource = EntityModel.of(
                        new ApiResponse<>(true, item.get(), "Item retrieved successfully.")
                );

                // Add HATEOAS links
                Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).getItem(id)).withSelfRel();
                Link ItemLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).getItem("id")).withRel("item/update/delete");
                Link addItemLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).addItem(null)).withRel("add-item/all-items");
                Link ordersLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).getOrders()).withRel("orders");
                resource.add(selfLink, ItemLink, addItemLink, ordersLink);

                return ResponseEntity.ok(resource);
            } else {
                throw new ItemNotFoundException(id);
            }
        } catch (InterruptedException | ExecutionException e) {
            EntityModel<ApiResponse<Item>> errorResource = EntityModel.of(
                    new ApiResponse<>(false, null, "Failed to retrieve item.")
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResource);
        }
    }

    @GetMapping("/items")
    ResponseEntity<EntityModel<ApiResponse<Collection<Item>>>> getItems() {
        try {
            Collection<Item> items = itemsRepository.getAllItems();
            EntityModel<ApiResponse<Collection<Item>>> resource = EntityModel.of(
                    new ApiResponse<>(true, items, "Items retrieved successfully.")
            );

            // Add HATEOAS links
            Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).getItems()).withSelfRel();
            Link ItemLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).getItem("id")).withRel("item/update/delete");
            Link addItemLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).addItem(null)).withRel("add-item/all-items");
            Link ordersLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).getOrders()).withRel("orders");
            resource.add(selfLink, ItemLink, addItemLink, ordersLink);

            return ResponseEntity.ok(resource);
        } catch (InterruptedException | ExecutionException e) {
            EntityModel<ApiResponse<Collection<Item>>> errorResource = EntityModel.of(
                    new ApiResponse<>(false, null, "Failed to retrieve items.")
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResource);
        }
    }

    @PostMapping("/items")
    ResponseEntity<EntityModel<ApiResponse<Item>>> addItem(@RequestBody Item item) {
        try {
            Optional<Item> newItem = itemsRepository.addItem(item);
            if (newItem.isPresent()) {
                EntityModel<ApiResponse<Item>> resource = EntityModel.of(
                        new ApiResponse<>(true, newItem.get(), "Item added successfully.")
                );

                // Add HATEOAS links
                Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).getItem(newItem.get().getId())).withSelfRel();
                Link ItemLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).getItem("id")).withRel("item/update/delete");
                Link addItemLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).addItem(null)).withRel("add-item/all-items");
                Link ordersLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).getOrders()).withRel("orders");
                resource.add(selfLink, ItemLink, addItemLink, ordersLink);

                return ResponseEntity.created(selfLink.toUri()).body(resource);
            } else {
                throw new RuntimeException("Failed to add item.");
            }
        } catch (InterruptedException | ExecutionException e) {
            EntityModel<ApiResponse<Item>> errorResource = EntityModel.of(
                    new ApiResponse<>(false, null, "Failed to add item.")
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResource);
        }
    }

    @PutMapping("/items/{id}")
    ResponseEntity<EntityModel<ApiResponse<Item>>> updateItem(@PathVariable String id, @RequestBody Item item) {
        try {
            Optional<Item> updatedItem = itemsRepository.updateItem(id, item);
            if (updatedItem.isPresent()) {
                EntityModel<ApiResponse<Item>> resource = EntityModel.of(
                        new ApiResponse<>(true, updatedItem.get(), "Item updated successfully.")
                );

                // Add HATEOAS links
                Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).getItem(id)).withSelfRel();
                Link ItemLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).getItem("id")).withRel("item/update/delete");
                Link addItemLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).addItem(null)).withRel("add-item/all-items");
                Link ordersLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).getOrders()).withRel("orders");
                resource.add(selfLink, ItemLink, addItemLink, ordersLink);

                return ResponseEntity.ok(resource);
            } else {
                throw new ItemNotFoundException(id);
            }
        } catch (InterruptedException | ExecutionException e) {
            EntityModel<ApiResponse<Item>> errorResource = EntityModel.of(
                    new ApiResponse<>(false, null, "Failed to update item.")
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResource);
        }
    }

    @DeleteMapping("/items/{id}")
    ResponseEntity<EntityModel<ApiResponse<Void>>> deleteItem(@PathVariable String id) {
        try {
            itemsRepository.deleteItem(id);
            EntityModel<ApiResponse<Void>> resource = EntityModel.of(
                    new ApiResponse<>(true, null, "Item deleted successfully.")
            );

            // Add HATEOAS links
            Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).getItem(id)).withSelfRel();
            Link ItemLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).getItem("id")).withRel("item/update/delete");
            Link addItemLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).addItem(null)).withRel("add-item/all-items");
            Link ordersLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).getOrders()).withRel("orders");
            resource.add(selfLink, ItemLink, addItemLink, ordersLink);

            return ResponseEntity.ok(resource);
        } catch (Exception e) {
            EntityModel<ApiResponse<Void>> errorResource = EntityModel.of(
                    new ApiResponse<>(false, null, "Failed to delete item.")
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResource);
        }
    }

    @GetMapping("/orders/{id}")
    ResponseEntity<EntityModel<ApiResponse<Order>>> getOrder(@PathVariable String id) {
        try {
            Optional<Order> order = itemsRepository.findOrder(id);
            if (order.isPresent()) {
                EntityModel<ApiResponse<Order>> resource = EntityModel.of(
                        new ApiResponse<>(true, order.get(), "Order retrieved successfully.")
                );

                // Add HATEOAS links
                Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).getOrder(id)).withSelfRel();
                Link orderLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).getOrder("id")).withRel("order/update/delete");
                Link addOrderLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).addOrder(null)).withRel("add-order/all-orders");
                Link itemsLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).getItems()).withRel("items");
                resource.add(selfLink, orderLink, addOrderLink, itemsLink);

                return ResponseEntity.ok(resource);
            } else {
                throw new OrderNotFoundException(id);
            }
        } catch (InterruptedException | ExecutionException e) {
            EntityModel<ApiResponse<Order>> errorResource = EntityModel.of(
                    new ApiResponse<>(false, null, "Failed to retrieve order.")
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResource);
        }
    }

    @GetMapping("/orders")
    ResponseEntity<EntityModel<ApiResponse<Collection<Order>>>> getOrders() {
        try {
            Collection<Order> orders = itemsRepository.getAllOrders();
            EntityModel<ApiResponse<Collection<Order>>> resource = EntityModel.of(
                    new ApiResponse<>(true, orders, "Orders retrieved successfully.")
            );

            // Add HATEOAS links
            Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).getOrders()).withSelfRel();
            Link orderLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).getOrder("id")).withRel("order/update/delete");
            Link addOrderLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).addOrder(null)).withRel("add-order/all-orders");
            Link itemsLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).getItems()).withRel("items");
            resource.add(selfLink, orderLink, addOrderLink, itemsLink);

            return ResponseEntity.ok(resource);
        } catch (InterruptedException | ExecutionException e) {
            EntityModel<ApiResponse<Collection<Order>>> errorResource = EntityModel.of(
                    new ApiResponse<>(false, null, "Failed to retrieve orders.")
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResource);
        }
    }

    @PostMapping("/orders")
    ResponseEntity<EntityModel<ApiResponse<Order>>> addOrder(@RequestBody Order order) {
        try {
            Optional<Order> newOrder = itemsRepository.addOrder(order);
            if (newOrder.isPresent()) {
                EntityModel<ApiResponse<Order>> resource = EntityModel.of(
                        new ApiResponse<>(true, newOrder.get(), "Order added successfully.")
                );

                // Add HATEOAS links
                Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).getOrder(newOrder.get().getId())).withSelfRel();
                Link orderLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).getOrder("id")).withRel("order/update/delete");
                Link addOrderLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).addOrder(null)).withRel("add-order/all-orders");
                Link itemsLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).getItems()).withRel("items");
                resource.add(selfLink, orderLink, addOrderLink, itemsLink);

                return ResponseEntity.created(selfLink.toUri()).body(resource);
            } else {
                throw new RuntimeException("Failed to add order.");
            }
        } catch (InterruptedException | ExecutionException e) {
            EntityModel<ApiResponse<Order>> errorResource = EntityModel.of(
                    new ApiResponse<>(false, null, "Failed to add order.")
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResource);
        }
    }

    @PutMapping("/orders/{id}")
    ResponseEntity<EntityModel<ApiResponse<Order>>> updateOrder(@PathVariable String id, @RequestBody Order order) {
        try {
            Optional<Order> updatedOrder = itemsRepository.updateOrder(id, order);
            if (updatedOrder.isPresent()) {
                EntityModel<ApiResponse<Order>> resource = EntityModel.of(
                        new ApiResponse<>(true, updatedOrder.get(), "Order updated successfully.")
                );

                // Add HATEOAS links
                Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).getOrder(id)).withSelfRel();
                Link orderLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).getOrder("id")).withRel("order/update/delete");
                Link addOrderLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).addOrder(null)).withRel("add-order/all-orders");
                Link itemsLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).getItems()).withRel("items");
                resource.add(selfLink, orderLink, addOrderLink, itemsLink);

                return ResponseEntity.ok(resource);
            } else {
                throw new OrderNotFoundException(id);
            }
        } catch (InterruptedException | ExecutionException e) {
            EntityModel<ApiResponse<Order>> errorResource = EntityModel.of(
                    new ApiResponse<>(false, null, "Failed to update order.")
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResource);
        }
    }

    @DeleteMapping("/orders/{id}")
    ResponseEntity<EntityModel<ApiResponse<Void>>> deleteOrder(@PathVariable String id) {
        try {
            itemsRepository.deleteOrder(id);
            EntityModel<ApiResponse<Void>> resource = EntityModel.of(
                    new ApiResponse<>(true, null, "Order deleted successfully.")
            );

            // Add HATEOAS links
            Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).getOrder(id)).withSelfRel();
            Link orderLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).getOrder("id")).withRel("order/update/delete");
            Link addOrderLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).addOrder(null)).withRel("add-order/all-orders");
            Link itemsLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestRpcController.class).getItems()).withRel("items");
            resource.add(selfLink, orderLink, addOrderLink, itemsLink);

            return ResponseEntity.ok(resource);
        } catch (Exception e) {
            EntityModel<ApiResponse<Void>> errorResource = EntityModel.of(
                    new ApiResponse<>(false, null, "Failed to delete order.")
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResource);
        }
    }
}
