package pe.edu.utp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EmailAlreadyException extends RuntimeException {

    public EmailAlreadyException() {
        super();
    }

    public EmailAlreadyException(String message) {
        super(message);
    }

}
