package com.example.investmentservice.exception;

public class AccountServiceException extends Throwable {
    public AccountServiceException(String message) {
        super(message);
    }

    public AccountServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
