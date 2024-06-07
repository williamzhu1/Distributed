package be.kuleuven.supplierservice.exceptions;

public class GoodNotFoundException extends RuntimeException{
    public GoodNotFoundException(String id) {
        super("Could not find meal " + id);
    }
}
