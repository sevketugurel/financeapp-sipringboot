package com.example.billingservice.exception;

import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomFeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        String message = "Unexpected error occurred during Feign client call.";

        return switch (response.status()) {
            case 400 -> new TransactionServiceException("Bad request to " + methodKey + ": " + message);
            case 404 -> new TransactionServiceException("Resource not found: " + message);
            case 500 -> new TransactionServiceException("Internal server error during transaction: " + message);
            default -> new Exception("Generic error: " + message);
        };
    }
}
