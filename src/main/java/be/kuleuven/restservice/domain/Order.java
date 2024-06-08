package be.kuleuven.restservice.domain;

import java.util.HashMap;
import java.util.Map;

public class Order {


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

    public void addItem(Item item, int quantity) {
        if (item != null && quantity > 0 && item.getStock() >= quantity) {
            int currentStock = item.getStock();
            int newStock = currentStock - quantity;
            item.setStock(newStock); // Update item stock
            items.put(item, items.getOrDefault(item, 0) + quantity);
        } else {
            assert item != null;
            System.out.println("Insufficient stock for item: " + item.getName());
        }
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

    public Map<Item, Integer> getItems() {
        return items;
    }

    protected String id;
    protected String address;
    protected Map<Item, Integer> items = new HashMap<>();
    protected OrderStatus status;

}

