package be.kuleuven.restservice.exceptions;

public class MealAlreadyExists extends RuntimeException {

    public MealAlreadyExists() {
        super("MealAlreadyExists");
    }
}
