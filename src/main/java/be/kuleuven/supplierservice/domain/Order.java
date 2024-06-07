package be.kuleuven.supplierservice.domain;

public class Order {
    private boolean success;
    private String orderId;

    private String goodsId;

    // inventoryId to quantity
    private String InventoryId;
    private int Quantity;

    private String address;
    private Status status;

    /**
     * Constructor to initialize the Order with an address.
     */
    public Order(String orderId, String goodsId, String address, String inventoryId, int quantity ) {
        this.orderId = orderId; // Use the passed orderId instead of generating a new one
        this.goodsId = goodsId;
        this.address = address;
        this.status = Status.PENDING;
        this.InventoryId = inventoryId;
        this.Quantity = quantity;
    }



    // Getters and setters

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getOrderId() {
        return orderId;
    }
    public void setOrderId(String orderId){
        this.orderId = orderId;
    }
    public String getGoodsId() {
        return goodsId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getInventoryId() {
        return InventoryId;
    }

    public void setInventoryId(String inventoryId) {
        InventoryId = inventoryId;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    // Overriding toString method for debugging purposes
    @Override
    public String toString() {
        return "Order{" +
                "success=" + success +
                ", orderId='" + orderId + '\'' +
                ", goodsId=" + goodsId +
                ", address='" + address + '\'' +
                ", status=" + status +
                '}';
    }
}
