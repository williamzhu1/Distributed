package be.kuleuven.restservice.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class Order {

    protected String id;
    protected String masterId;
    protected String address;
    protected Map<String, Integer> items = new HashMap<>();
    protected OrderStatus status;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order ID: ").append(id).append("\n");
        sb.append("Master ID: ").append(masterId).append("\n");
        sb.append("Address: ").append(address).append("\n");
        sb.append("Status: ").append(status).append("\n");
        return sb.toString();
    }

}

