package org.example.tpremise.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MontantPositifExption extends RuntimeException {
    public MontantPositifExption(String message) {
        super(message);
    }
}
