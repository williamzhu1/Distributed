package be.kuleuven.dsgt4;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @CrossOrigin(origins = "http://localhost:3000") // Replace with your frontend's origin
    @PostMapping("/usertest")
    public ResponseEntity<?> handleRequest(@RequestHeader("Authorization") String authorizationHeader,
                                           @RequestBody Map<String, Object> requestBody) {
        // Your logic to handle the request
        return ResponseEntity.ok("Request received and processed");
    }
}
