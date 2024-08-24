package com.example.accountservice.exception;

import com.example.accountservice.model.Account;
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

class AccountAlreadyExistsExceptionTest {

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
    void testCreateAccount_ThrowsAccountAlreadyExistsException() {
        // Arrange
        String iban = "TR1234563543467";
        Account account = new Account();
        account.setIban(iban);

        // Mock the repository to return an account with the same IBAN
        when(accountRepository.findByIban(iban)).thenReturn(Optional.of(account));

        // Act and Assert
        AccountAlreadyExistsException exception = assertThrows(AccountAlreadyExistsException.class, () -> {
            accountService.saveAccount(account);
        });

        // Additional Assertion (Optional)
        assertEquals("Account with IBAN already exists: " + iban, exception.getMessage());
    }
}
