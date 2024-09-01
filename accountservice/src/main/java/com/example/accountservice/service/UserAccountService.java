package com.example.accountservice.service;

import com.example.accountservice.client.TransactionClient;
import com.example.accountservice.exception.*;
import com.example.accountservice.exception.apiException.ApiRequestException;
import com.example.accountservice.model.Transaction;
import com.example.accountservice.model.Account;
import com.example.accountservice.repository.UserAccountRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserAccountService {

    private final UserAccountRepository repository;
    private final TransactionClient transactionClient;

    @Autowired
    public UserAccountService(UserAccountRepository repository, TransactionClient transactionClient) {
        this.repository = repository;
        this.transactionClient = transactionClient;
    }

    public List<Account> getAllAccounts() {
        return repository.findAll();
    }

    public Account getAccountById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + id));
    }

    public Account saveAccount(Account account) {
        // Ensure IBAN is unique
        if (repository.findByIban(account.getIban()).isPresent()) {
            throw new AccountAlreadyExistsException("Account with IBAN already exists: " + account.getIban());
        }
        return repository.save(account);
    }

    public void deleteAccount(Long id) {
        if (!repository.existsById(id)) {
            throw new AccountNotFoundException("Account not found with ID: " + id);
        }
        repository.deleteById(id);
    }

    public Account updateAccount(String username, Account account) {
        Account existingAccount = repository.findByUsername(username)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with username: " + username));

        // Update the existing account with new information
        existingAccount.setBalance(account.getBalance());
        existingAccount.setIban(account.getIban());

        return repository.save(existingAccount);
    }

    public Optional<Account> getAccountByUsername(String username) {
        return repository.findByUsername(username);
    }

    @CircuitBreaker(name = "transactionService", fallbackMethod = "fallbackTransactionService")
    public void transferMoney(String senderIban, String receiverIban, Double amount) {
        Account sender = repository.findByIban(senderIban)
                .orElseThrow(() -> new ApiRequestException("Sender account not found with IBAN: " + senderIban));

        Account receiver = repository.findByIban(receiverIban)
                .orElseThrow(() -> new ApiRequestException("Receiver account not found with IBAN: " + receiverIban));

        if (sender.getBalance() < amount) {
            throw new ApiRequestException("Insufficient funds in sender's account");
        }

        // Create transaction details for sender
        Transaction senderTransaction = createTransaction(sender.getId().toString(), -amount, senderIban, receiverIban, amount);

        // Notify the transaction service for the sender
        try {
            transactionClient.saveTransaction(senderTransaction);
        } catch (Exception e) {
            throw new ApiRequestException("Failed to notify TransactionService for the transaction from sender IBAN: " + senderIban, e);
        }

        // Create transaction details for receiver
        Transaction receiverTransaction = createTransaction(receiver.getId().toString(), amount, senderIban, receiverIban, amount);

        // Notify the transaction service for the receiver
        try {
            transactionClient.saveTransaction(receiverTransaction);
        } catch (Exception e) {
            throw new ApiRequestException("Failed to notify TransactionService for the transaction to receiver IBAN: " + receiverIban, e);
        }

        // Perform the money transfer
        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);

        repository.save(sender);
        repository.save(receiver);
    }

    // Fallback method in case the circuit breaker is triggered
    private void fallbackTransactionService(String senderIban, String receiverIban, Double amount, Throwable throwable) {
        // Log the error or handle the fallback scenario
        throw new ApiRequestException("Transaction service is currently unavailable. Please try again later.");
    }

    private Transaction createTransaction(String accountId, Double amount, String senderIban, String receiverIban, Double transferAmount) {
        Transaction transaction = new Transaction();
        transaction.setServiceName("AccountService");
        transaction.setAccountId(accountId);
        transaction.setAmount(amount);
        transaction.setTimestamp(new Date());

        Map<String, Object> details = new HashMap<>();
        details.put("senderIban", senderIban);
        details.put("receiverIban", receiverIban);
        details.put("transferAmount", transferAmount);
        transaction.setDetails(details);

        return transaction;
    }
}
