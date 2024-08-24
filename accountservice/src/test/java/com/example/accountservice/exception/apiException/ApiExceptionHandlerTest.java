package com.example.accountservice.exception.apiException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiExceptionHandlerTest {

    private ApiExceptionHandler apiExceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        apiExceptionHandler = new ApiExceptionHandler();
    }

    @Test
    void testHandleApiRequestException() {
        // Arrange
        String errorMessage = "Test exception message";
        ApiRequestException exception = new ApiRequestException(errorMessage);

        // Act
        ResponseEntity<Object> response = apiExceptionHandler.handleApiRequestException(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiException apiException = (ApiException) response.getBody();
        assertEquals(errorMessage, apiException.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, apiException.getHttpStatus());
        assertEquals(exception, apiException.getThrowable());
        assertEquals(ZonedDateTime.now(ZoneId.of("Z")).getZone(), apiException.getTimestamp().getZone());
    }
}
