package be.kuleuven.restservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMasterId() {
        return masterId;
    }

    public void setMasterId(String masterId) {
        this.masterId = masterId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public OrderStatus getOrderStatus(){
        return status;
    }

    public void setStatus(OrderStatus status){
        this.status = status;
    }


    public boolean addItem(Item item, int quantity) {
        if (item != null && quantity > 0 && item.getStock() >= quantity) {
            int currentStock = item.getStock();
            int newStock = currentStock - quantity;
            item.setStock(newStock); // Update item stock
            items.put(item, items.getOrDefault(item, 0) + quantity);
            return true;
        } else {
            assert item != null;
            status = OrderStatus.ROOTSTOCK;
            System.out.println("Insufficient stock for item: " + item.getName());
            return false;
        }
    }

    public boolean setItems(HashMap<Item, Integer> newItems) {
        Map<Item, Integer> previousStocks = new HashMap<>();

        for (Map.Entry<Item, Integer> entry : newItems.entrySet()) {
            Item item = entry.getKey();
            int quantity = entry.getValue();

            previousStocks.put(item, item.getStock());

            if (!addItem(item, quantity)) {
                // Undo the stock reductions for previously added items
                for (Map.Entry<Item, Integer> revertEntry : previousStocks.entrySet()) {
                    revertEntry.getKey().setStock(revertEntry.getValue());
                }
                return false; // Exit the loop if out of stock
            }
        }
        return true; // All items added successfully
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
    protected String masterId;
    protected String address;
    protected Map<Item, Integer> items = new HashMap<>();
    protected OrderStatus status;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order ID: ").append(id).append("\n");
        sb.append("Master ID: ").append(masterId).append("\n");
        sb.append("Address: ").append(address).append("\n");
        sb.append("Status: ").append(status).append("\n");
        sb.append("Items: \n");
        for (Map.Entry<Item, Integer> entry : items.entrySet()) {
            sb.append(" - Item: ").append(entry.getKey().getName()).append(entry.getKey().getId())
                    .append(", Quantity: ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }

}

