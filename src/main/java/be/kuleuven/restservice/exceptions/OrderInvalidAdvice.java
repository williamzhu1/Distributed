package be.kuleuven.restservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public class OrderInvalidAdvice {


    @ResponseBody
    @ExceptionHandler(OrderInvalidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String orderNotFoundHandler(OrderInvalidException ex) {
        return ex.getMessage();
    }
}
