package com.example.billingservice.service;

import com.example.billingservice.client.AccountServiceClient;
import com.example.billingservice.client.TransactionClient;
import com.example.billingservice.exception.BillingNotFoundException;
import com.example.billingservice.exception.BillingPaidException;
import com.example.billingservice.exception.InsufficientBalanceException;
import com.example.billingservice.model.Account;
import com.example.billingservice.model.Billing;
import com.example.billingservice.model.Transaction;
import com.example.billingservice.repository.BillingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BillingServiceTest {

    @Mock
    private BillingRepository billingRepository;
    @Mock
    private AccountServiceClient accountServiceClient;
    @Mock
    private TransactionClient transactionClient;
    @Mock
    private Transaction transaction;

    @InjectMocks
    private BillingService billingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetBillingHistory_shouldReturnBillingHistory() {
        String username = "testuser";

        // Fatura geçmişini oluşturma
        List<Billing> billingHistory = List.of(
                new Billing(1L, username, 100.0, false, LocalDateTime.now()),
                new Billing(2L, username, 200.0, true, LocalDateTime.now())
        );

        // Kullanıcı adına göre fatura geçmişini döndür
        when(billingRepository.findByUsername(username)).thenReturn(billingHistory);

        //metodu çağırma
        List<Billing> result = billingService.getBillingHistory(username);

        //doğrulama
        assertThat(result).isEqualTo(billingHistory);
        verify(billingRepository).findByUsername(username);

    }

    @Test
    void testPayBilling_SuccessfulPayment() {
        // Arrange
        Long billingId = 1L;
        Billing billing = new Billing();
        billing.setId(billingId);
        billing.setUsername("testUser");
        billing.setAmount(100.0);
        billing.setIsPaid(false);

        Account account = new Account();
        account.setId(1L); // ID ataması yapılıyor
        account.setUsername("testUser");
        account.setBalance(2000.0);

        when(billingRepository.findById(billingId)).thenReturn(Optional.of(billing));
        when(accountServiceClient.getAccountByUsername(billing.getUsername()))
                .thenReturn(ResponseEntity.of(Optional.of(account)));
        when(billingRepository.save(any(Billing.class))).thenReturn(billing);

        // Act
        Billing result = billingService.payBilling(billingId);

        // Assert
        assertNotNull(result);
        assertTrue(result.getIsPaid());

        // Burada, paymentDate'in doğru bir şekilde ayarlandığını kontrol etmek için sabit bir tarih kullanmalıyız.
        LocalDateTime expectedDate = LocalDateTime.now();
        assertEquals(expectedDate.getDayOfMonth(), result.getPaymentDate().getDayOfMonth());
        assertEquals(expectedDate.getMonth(), result.getPaymentDate().getMonth());
        assertEquals(expectedDate.getYear(), result.getPaymentDate().getYear());

        verify(accountServiceClient, times(1)).updateAccount(eq(billing.getUsername()), any(Account.class));
        verify(billingRepository, times(1)).save(any(Billing.class));
        verify(transactionClient, times(1)).saveTransaction(any(Transaction.class)); // Ensure this matches your actual implementation
    }

    @Test
    void testPayBilling_BillingNotFound() {
        // Arrange
        Long billingId = 1L;

        when(billingRepository.findById(billingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BillingNotFoundException.class, () -> billingService.payBilling(billingId));
    }

    @Test
    void testPayBilling_InsufficientBalance() {
        // Arrange
        Long billingId = 1L;
        Billing billing = new Billing();
        billing.setId(billingId);
        billing.setUsername("testUser");
        billing.setAmount(300.0);
        billing.setIsPaid(false);

        Account account = mock(Account.class);
        account.setUsername("testUser");
        account.setBalance(100.0);

        when(billingRepository.findById(billingId)).thenReturn(Optional.of(billing));
        when(accountServiceClient.getAccountByUsername(billing.getUsername())).thenReturn(ResponseEntity.of(Optional.of(account)));

        // Act & Assert
        assertThrows(InsufficientBalanceException.class, () -> billingService.payBilling(billingId));
    }


}