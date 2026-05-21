package org.example.tpremise.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RemiseException extends RuntimeException {
    public RemiseException(String message) {
        super(message);
    }
}