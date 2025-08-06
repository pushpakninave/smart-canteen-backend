package com.smartcanteen.exception; // <<< THIS MUST BE THE PACKAGE NAME

import org.springframework.http.HttpStatus; // For @ResponseStatus
import org.springframework.web.bind.annotation.ResponseStatus; // To apply HTTP status automatically

// @ResponseStatus annotation makes Spring automatically return an HTTP 404 Not Found status
// when this exception is thrown from a controller method.
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    // Constructor that takes a message, which will be the detail message of the exception.
    public ResourceNotFoundException(String message) {
        super(message);
    }

    // You can add more constructors or fields if needed, but this basic one is sufficient for now.
}