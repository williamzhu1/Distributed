package be.kuleuven.supplierservice.domain;

public class Inventory {
    private String id;
    private int shoesSize;
    private int availableQuantity;
    private int onholdQuantity;
    private int soldQuantity;

    // 构造器
    public Inventory(String id, int shoesSize, int availableQuantity, int onholdQuantity, int soldQuantity) {
        this.id = id;
        this.shoesSize = shoesSize;
        this.availableQuantity = availableQuantity;
        this.onholdQuantity = onholdQuantity;
        this.soldQuantity = soldQuantity;
    }

    // 默认构造器
    public Inventory() {
    }

    // Getters
    public String getId() {
        return id;
    }

    public int getShoesSize() {
        return shoesSize;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public int getOnholdQuantity() {
        return onholdQuantity;
    }

    public int getSoldQuantity() {
        return soldQuantity;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setShoesSize(int shoesSize) {
        this.shoesSize = shoesSize;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public void setOnholdQuantity(int onholdQuantity) {
        this.onholdQuantity = onholdQuantity;
    }

    public void setSoldQuantity(int soldQuantity) {
        this.soldQuantity = soldQuantity;
    }

    // Inventory management methods
    public void addAvailable(int quantity) {
        this.availableQuantity += quantity;
    }

    public boolean reserveStock(int quantity) {
        if (quantity <= availableQuantity) {
            this.availableQuantity -= quantity;
            this.onholdQuantity += quantity;
            return true;
        } else {
            return false;
        }
    }

    public boolean reverseBook(int quantity) {
        if (quantity <= onholdQuantity) {
            this.availableQuantity += quantity;
            this.onholdQuantity -= quantity;
            return true;
        } else {
            return false;
        }
    }

    public boolean sellStock(int quantity) {
        if (quantity <= onholdQuantity) {
            this.onholdQuantity -= quantity;
            this.soldQuantity += quantity;
            return true;
        } else {
            return false;
        }
    }
}
