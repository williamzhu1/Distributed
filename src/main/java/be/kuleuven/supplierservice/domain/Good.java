package be.kuleuven.supplierservice.domain;

import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Good {
    private String supplierId;
    private String id;
    private String name;
    private String imageUrl;
    private float price;
    private String sex;

    private List<Inventory> inventories;

    // Full constructor
    public Good(String supplierId,String id, String name, String imageUrl, float price, String sex, List<Inventory> inventories) {
        this.supplierId = supplierId;
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.sex = sex;
        this.inventories = new ArrayList<>(inventories); // Initialize with a copy of the provided list
    }

    public Good(String supplierId,String id, String name, String imageUrl, float price, String sex) {
        this.supplierId=supplierId;
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.sex = sex;
        this.inventories = new ArrayList<>();
    }

    // Default constructor
    public Good() {
        this.inventories = new ArrayList<>(); // Initialize an empty list for inventories
    }

    // Getters
    public String  getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public float getPrice() {
        return price;
    }

    public String getSex() {
        return sex;
    }

    public List<Inventory> getInventories() {
        return new ArrayList<>(inventories); // Return a copy to protect internal data
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setInventories(List<Inventory> inventories) {
        this.inventories = new ArrayList<>(inventories); // Set with a copy of the list
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    // Methods to manage inventories
    public void addInventory(Inventory inventory) {
        this.inventories.add(inventory);
    }

    public void removeInventory(Inventory inventory) {
        this.inventories.remove(inventory);
    }

    public Optional<Inventory> findInventory(String id){
        Assert.notNull(id,"inventory id could not be null");
        if (this.inventories.isEmpty()) return null;
        for( Inventory inventory : inventories){
            if (inventory.getId().equals(id)) return Optional.ofNullable(inventory);
        }
        return null;
    }
}
