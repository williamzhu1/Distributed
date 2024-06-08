package be.kuleuven.restservice.domain;

public enum OrderStatus {

    PENDING("pending"),
    CONFIRMED("confirmed"),
    DELIVERED("delivered"),
    CANCELLED("cancelled");
    private final String value;

    OrderStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static OrderStatus fromValue(String v) {
        for (OrderStatus c: OrderStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
