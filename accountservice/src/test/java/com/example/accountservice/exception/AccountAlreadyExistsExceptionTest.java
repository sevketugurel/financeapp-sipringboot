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

        // if an account with the same IBAN already exists, an AccountAlreadyExistsException should be thrown
        AccountAlreadyExistsException exception = assertThrows(AccountAlreadyExistsException.class, () -> {
            accountService.saveAccount(account);
        });

        // Additional Assertion (Optional)
        assertEquals("Account with IBAN already exists: " + iban, exception.getMessage());
    }

    @Test
    void testGetAccountById_ThrowsAccountNotFoundException() {
        // Arrange
        Long invalidId = 1L;
        when(accountRepository.findById(invalidId)).thenReturn(Optional.empty());
        // if an account with the given ID does not exist, an AccountNotFoundException should be thrown
        assertThrows(AccountNotFoundException.class, () -> accountService.getAccountById(invalidId));
    }
}
