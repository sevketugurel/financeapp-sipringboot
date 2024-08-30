package com.example.investmentservice.service;

import com.example.investmentservice.client.AccountServiceClient;
import com.example.investmentservice.client.TransactionClient;
import com.example.investmentservice.exception.AccountNotFoundException;
import com.example.investmentservice.exception.BalanceException;
import com.example.investmentservice.model.Account;
import com.example.investmentservice.model.Investment;
import com.example.investmentservice.repository.InvestmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class InvestmentServiceTest {

    @Mock
    private InvestmentRepository investmentRepository;

    @Mock
    private AccountServiceClient accountServiceClient;

    @Mock
    private TransactionClient transactionClient;

    @InjectMocks
    private InvestmentService investmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBuyInvestment_Success() {
        // Arrange
        String username = "testuser";
        String investmentType = "STOCK";
        int quantity = 10;
        double pricePerUnit = 100.0;

        Account account = new Account();
        account.setId(1L);
        account.setBalance(2000.0);

        Investment investment = new Investment(username, investmentType, 0, 0.0);
        investment.setInvestmentName("STOCK");

        when(accountServiceClient.getAccountByUsername(username)).thenReturn(new ResponseEntity<>(account, HttpStatus.OK));
        when(investmentRepository.findByUsernameAndInvestmentType(username, investmentType)).thenReturn(Optional.of(investment));
        when(investmentRepository.save(any(Investment.class))).thenReturn(investment);

        // Act
        Investment result = investmentService.buyInvestment(username, investmentType, quantity, pricePerUnit);

        // Assert
        assertNotNull(result);
        assertEquals(10, result.getQuantity());
        verify(accountServiceClient, times(1)).updateAccount(any(Account.class), eq(username));
        verify(transactionClient, times(1)).saveTransaction(any());
    }

    @Test
    void testBuyInvestment_InsufficientBalance() {
        // Arrange
        String username = "testuser";
        String investmentType = "STOCK";
        int quantity = 10;
        double pricePerUnit = 1000.0;

        Account account = new Account();
        account.setId(1L);
        account.setBalance(500.0);

        when(accountServiceClient.getAccountByUsername(username)).thenReturn(new ResponseEntity<>(account, HttpStatus.OK));

        // Act & Assert
        assertThrows(BalanceException.class, () -> investmentService.buyInvestment(username, investmentType, quantity, pricePerUnit));
        verify(investmentRepository, never()).save(any());
        verify(transactionClient, never()).saveTransaction(any());
    }

    @Test
    void testSellInvestment_Success() {
        // Arrange
        String username = "testuser";
        String investmentType = "STOCK";
        int quantity = 5;
        double pricePerUnit = 100.0;

        Investment investment = new Investment(username, investmentType, 10, 100.0);

        Account account = new Account();
        account.setId(1L);
        account.setBalance(1000.0);

        when(investmentRepository.findByUsernameAndInvestmentType(username, investmentType)).thenReturn(Optional.of(investment));
        when(accountServiceClient.getAccountByUsername(username)).thenReturn(new ResponseEntity<>(account, HttpStatus.OK));

        // Act
        Investment result = investmentService.sellInvestment(username, investmentType, quantity, pricePerUnit);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.getQuantity());
        verify(accountServiceClient, times(1)).updateAccount(any(Account.class), eq(username));
        verify(transactionClient, times(1)).saveTransaction(any());
    }

    @Test
    void testSellInvestment_NotEnoughQuantity() {
        // Arrange
        String username = "testuser";
        String investmentType = "STOCK";
        int quantity = 15;
        double pricePerUnit = 100.0;

        Investment investment = new Investment(username, investmentType, 10, 100.0);

        when(investmentRepository.findByUsernameAndInvestmentType(username, investmentType)).thenReturn(Optional.of(investment));

        // Act & Assert
        assertThrows(BalanceException.class, () -> investmentService.sellInvestment(username, investmentType, quantity, pricePerUnit));
        verify(accountServiceClient, never()).updateAccount(any(), eq(username));
        verify(transactionClient, never()).saveTransaction(any());
    }

    @Test
    void testSellInvestment_InvestmentNotFound() {
        // Arrange
        String username = "testuser";
        String investmentType = "STOCK";
        int quantity = 5;
        double pricePerUnit = 100.0;

        when(investmentRepository.findByUsernameAndInvestmentType(username, investmentType)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AccountNotFoundException.class, () -> investmentService.sellInvestment(username, investmentType, quantity, pricePerUnit));
    }
}
