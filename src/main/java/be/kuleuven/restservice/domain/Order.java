package be.kuleuven.restservice.domain;

import java.util.HashMap;
import java.util.Map;

public class Order {

    protected String id;
    protected String address;
    private Map<Item, Integer> items = new HashMap<>();

    public void addItem(Item item, int quantity) {
        items.put(item, items.getOrDefault(item, 0) + quantity);
    }

    public void removeItem(Item item, int quantity) {
        if (items.containsKey(item)) {
            int currentQuantity = items.get(item);
            if (currentQuantity <= quantity) {
                items.remove(item);
            } else {
                items.put(item, currentQuantity - quantity);
            }
        }
    }

    public int getQuantity(Item item) {
        return items.getOrDefault(item, 0);
    }

    public Order(String id, String address) {
        this.id = id; 
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
}
