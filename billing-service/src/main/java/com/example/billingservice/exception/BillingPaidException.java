package com.example.billingservice.exception;

public class BillingPaidException extends IllegalStateException {

    public BillingPaidException(String message) {
        super(message);
    }

    public BillingPaidException(String message, Throwable cause) {
        super(message, cause);
    }
}