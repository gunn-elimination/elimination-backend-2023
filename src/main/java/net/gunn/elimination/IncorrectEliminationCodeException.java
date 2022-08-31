package net.gunn.elimination;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Incorrect elimination code")
public class IncorrectEliminationCodeException extends Exception {
   public IncorrectEliminationCodeException(String message) {
	  super(message);
   }
}
