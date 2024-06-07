package be.kuleuven.restservice.domain;

import java.util.Random;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ItemsRepository {
    private static final Map<String, Item> items = new HashMap<>();

    @PostConstruct
    public void initData(){
        Item a = new Item();
        items.put(a.getId(),a);
    }

    public Optional<Item> findItem(String id) {
        Assert.notNull(id, "The item id must not be null");
        Item item = items.get(id);
        return Optional.ofNullable(item);
    }

    public void addItem(Item item){
        Assert.notNull(item.getId(), "Meal ID must not be null");
        items.put(item.getId(), item);
    }

    public Item updatedItem(String id, Item updatedItem) {
        updatedItem.setId(id);
        items.put(id, updatedItem);
        return updatedItem;
    }

    // Method to delete a meal
    public void deleteItem(String id) {
        items.remove(id);
    }

    public Collection<Item> getAllItem() {
        return items.values();
    }
    
}
