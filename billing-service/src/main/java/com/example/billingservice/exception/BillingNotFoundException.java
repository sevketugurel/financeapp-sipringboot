package com.example.billingservice.exception;

public class BillingNotFoundException extends RuntimeException {

    public BillingNotFoundException(String message) {
        super(message);
    }

    public BillingNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}