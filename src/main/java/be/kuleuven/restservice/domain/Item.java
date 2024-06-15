package be.kuleuven.restservice.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
public class Item {

    protected String id;
    protected String name;
    protected String description;
    protected Double price;
    protected String category;
    protected String manufacturer;
    protected Integer stock;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(name, item.name) &&
                Objects.equals(price, item.price) &&
                Objects.equals(manufacturer, item.manufacturer) &&
                Objects.equals(category, item.category) &&
                Objects.equals(description, item.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, description, category, manufacturer);
    }

    @Override
    public String toString() {
        return "{id='" + id + "', name='" + name + "', price=" + price + "}";
    }
}

