package com.example.accountservice.service;

import com.example.accountservice.exception.AccountNotFoundException;
import com.example.accountservice.exception.apiException.ApiRequestException;
import com.example.accountservice.model.Account;
import com.example.accountservice.model.Transaction;
import com.example.accountservice.repository.UserAccountRepository;
import com.example.accountservice.client.TransactionClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserAccountServiceTest {

    @InjectMocks // Injects mock objects into UserAccountService
    private UserAccountService accountService;

    @Mock
    private UserAccountRepository accountRepository;

    @Mock
    private TransactionClient transactionClient;


    private Account sender;
    private Account receiver;

    @BeforeEach // Runs before each test method
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sender = new Account();
        sender.setId(1L);
        sender.setIban("SENDER_IBAN");
        sender.setBalance(1000.0);

        receiver = new Account();
        receiver.setId(2L);
        receiver.setIban("RECEIVER_IBAN");
        receiver.setBalance(500.0);
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
        Account account = new Account(
        );
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

    //aşağıdaki hatalı circuit breaker testi çalışmıyor şimdilik
    @Test
    void transferMoney_circuitBreakerOpen() {
        // Arrange
        when(accountRepository.findByIban("SENDER_IBAN")).thenReturn(Optional.of(sender));
        when(accountRepository.findByIban("RECEIVER_IBAN")).thenReturn(Optional.of(receiver));

        // İlk çağrıda başarısız olmasını sağlıyoruz.
        doThrow(new RuntimeException("Transaction Service is down")).when(transactionClient).saveTransaction(any(Transaction.class));

        // Act & Assert
        // Circuit Breaker'ın belirli sayıda hatadan sonra devreye girdiğini varsayarsak örneğin 3 başarısız çağrının ardından:
        for (int i = 0; i < 3; i++) {
            assertThrows(ApiRequestException.class, () -> accountService.transferMoney("SENDER_IBAN", "RECEIVER_IBAN", 200.0));
        }

        // Circuit Breaker devreye girdiğinde fallback metodunun çalıştığını doğruluyoruz.
        assertThrows(ApiRequestException.class, () -> accountService.transferMoney("SENDER_IBAN", "RECEIVER_IBAN", 200.0));
    }


}
