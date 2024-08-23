package com.example.investmentservice.exception;

import com.example.investmentservice.exception.TransactionServiceException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;

public class CustomFeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        String message = "Unexpected error occurred during Feign client call.";

        switch (response.status()) {
            case 400:
                return new TransactionServiceException("Bad request to " + methodKey + ": " + message);
            case 404:
                return new TransactionServiceException("Resource not found: " + message);
            case 500:
                return new TransactionServiceException("Internal server error during transaction: " + message);
            default:
                return new Exception("Generic error: " + message);
        }
    }
}
