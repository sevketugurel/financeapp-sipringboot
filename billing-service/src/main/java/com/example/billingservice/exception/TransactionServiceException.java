package com.example.billingservice.exception;

public class TransactionServiceException extends RuntimeException {

    public TransactionServiceException(String message) {
        super(message);
    }

    public TransactionServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}