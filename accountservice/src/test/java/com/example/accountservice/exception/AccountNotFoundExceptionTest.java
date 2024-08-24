package com.example.accountservice.exception;

import com.example.accountservice.repository.UserAccountRepository;
import com.example.accountservice.service.UserAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AccountNotFoundExceptionTest {

    @Mock
    private UserAccountRepository accountRepository;

    @InjectMocks
    private UserAccountService accountService;

    @BeforeEach
    void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAccountById_ThrowsAccountNotFoundException() {
        // Arrange
        Long invalidId = 1L;
        when(accountRepository.findById(invalidId)).thenReturn(Optional.empty());
        // Act & Assert
        assertThrows(AccountNotFoundException.class, () -> accountService.getAccountById(invalidId));
    }

}