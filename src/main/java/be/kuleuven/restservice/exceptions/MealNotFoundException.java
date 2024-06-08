package be.kuleuven.restservice.exceptions;

public class MealNotFoundException extends RuntimeException {

    public MealNotFoundException(String id) {
        super("Could not find meal " + id);
    }
}
