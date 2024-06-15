package be.kuleuven.restservice.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class Order {

    protected String firstName;
    protected String lastName;
    protected String id;
    protected String masterId;
    protected String address;
    protected Map<String, Integer> items = new HashMap<>();
    protected OrderStatus status;

    @Override
    public String toString() {
        return "Order ID: " + id + "\n" +
                "Master ID: " + masterId + "\n" +
                "Address: " + address + "\n" +
                "Status: " + status + "\n";
    }

}

