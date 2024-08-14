package com.example.investmentservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InvestmentNotFoundException extends RuntimeException {
    public InvestmentNotFoundException(String username) {
        super("Investment not found for username: " + username);
    }
}
