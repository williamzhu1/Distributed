package be.kuleuven.restservice.exceptions;

public class OrderInvalidException extends RuntimeException{

    public OrderInvalidException() {
        super("Order invalid");
    }
}