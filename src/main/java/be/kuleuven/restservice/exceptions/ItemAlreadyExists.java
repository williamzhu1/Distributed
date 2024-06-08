package be.kuleuven.restservice.exceptions;

public class ItemAlreadyExists extends RuntimeException {

    public ItemAlreadyExists() {
        super("ItemAlreadyExists");
    }
}
