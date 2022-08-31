package net.gunn.elimination;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "No other players")
public class EmptyGameException extends Exception {
    public EmptyGameException(String message) {
        super(message);
    }
}
