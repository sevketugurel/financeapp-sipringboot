package com.example.accountservice.exception;

import feign.Request;
import feign.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class CustomFeignErrorDecoderTest {
    // CustomFeignErrorDecoder instance to test
    private CustomFeignErrorDecoder errorDecoder;

    @BeforeEach
    void setUp() {
        errorDecoder = new CustomFeignErrorDecoder();
    }

    @Test
    void testDecode_BadRequest() {
        // The mock response simulates an HTTP response with a status code
        Response response = createMockResponse(400, "Bad Request");

        // Act
        Exception exception = errorDecoder.decode("testMethod", response);

        // Verifies that the exception is of the expected type
        assertTrue(exception instanceof TransactionServiceException);
        assertEquals("Bad request to testMethod: Unexpected error occurred during Feign client call.", exception.getMessage());
    }

    @Test
    void testDecode_NotFound() {
        // Arrange
        Response response = createMockResponse(404, "Not Found");

        // Act
        Exception exception = errorDecoder.decode("testMethod", response);

        // Assert
        assertTrue(exception instanceof TransactionServiceException);
        assertEquals("Resource not found: Unexpected error occurred during Feign client call.", exception.getMessage());
    }

    @Test
    void testDecode_InternalServerError() {
        // Arrange
        Response response = createMockResponse(500, "Internal Server Error");

        // Act
        Exception exception = errorDecoder.decode("testMethod", response);

        // Assert
        assertTrue(exception instanceof TransactionServiceException);
        assertEquals("Internal server error during transaction: Unexpected error occurred during Feign client call.", exception.getMessage());
    }

    @Test
    void testDecode_GenericError() {
        // Arrange
        Response response = createMockResponse(418, "I'm a teapot");  // Example of a non-handled status code

        // Act
        Exception exception = errorDecoder.decode("testMethod", response);

        // Assert
        assertTrue(exception instanceof Exception);
        assertEquals("Generic error: Unexpected error occurred during Feign client call.", exception.getMessage());
    }

    // Utility method to create a mock Response
    private Response createMockResponse(int status, String reason) {
        return Response.builder()
                .request(Request.create(Request.HttpMethod.GET, "/test", Collections.emptyMap(), null, StandardCharsets.UTF_8))
                .status(status)
                .reason(reason)
                .build();
    }

}