package com.example.accountservice.service;

import com.example.accountservice.exception.AccountNotFoundException;
import com.example.accountservice.model.Account;

import com.example.accountservice.repository.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class UserAccountServiceTest {

    @InjectMocks // Injects mock objects into UserAccountService
    private UserAccountService accountService;
    @Mock
    private UserAccountRepository accountRepository;

    @BeforeEach // Runs before each test method
    void setUp() {
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

    @Test
    void testGetAccountById_ReturnsAccountWithBalance() {

        Long validId = 1L;
        Account account = new Account();
        account.setId(validId);
        account.setUsername("Test Account");
        account.setBalance(1000.0);

        when(accountRepository.findById(validId)).thenReturn(Optional.of(account));

        // Act
        Account result = accountService.getAccountById(validId);

        // Assert
        assertEquals(validId, result.getId());
        assertEquals("Test Account", result.getUsername());
        assertEquals(1000.0, result.getBalance());
    }
}
