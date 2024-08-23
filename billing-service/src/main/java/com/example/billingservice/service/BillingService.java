package com.example.billingservice.service;

import com.example.billingservice.client.AccountServiceClient;
import com.example.billingservice.client.TransactionClient;
import com.example.billingservice.exception.*;
import com.example.billingservice.model.Account;
import com.example.billingservice.model.Billing;
import com.example.billingservice.model.Transaction;
import com.example.billingservice.repository.BillingRepository;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BillingService {

    private static final Logger logger = LoggerFactory.getLogger(BillingService.class);

    private final BillingRepository billingRepository;
    private final AccountServiceClient accountServiceClient;
    private final TransactionClient transactionClient;

    public BillingService(
            BillingRepository billingRepository, AccountServiceClient accountServiceClient, TransactionClient transactionClient) {
        this.billingRepository = billingRepository;
        this.accountServiceClient = accountServiceClient;
        this.transactionClient = transactionClient;
    }

    public List<Billing> getBillingHistory(String username) {
        logger.info("Retrieving billing history for username: {}", username);
        return billingRepository.findByUsername(username);
    }

    public Billing payBilling(Long id) {
        logger.info("Attempting to pay billing with ID: {}", id);

        Billing billing = billingRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Billing not found with ID: {}", id);
                    return new BillingNotFoundException("Billing not found with ID: " + id);
                });

        logger.info("Billing found for ID: {}. Username: {}", id, billing.getUsername());

        Account account = retrieveAccount(billing.getUsername());
        if(!billing.getIsPaid()){
            logger.info("Billing is not paid for ID: {}. Proceeding with payment.", id);
            if (account.getBalance() >= billing.getAmount()) {
                logger.info("Sufficient balance available for user: {}. Proceeding with payment.", billing.getUsername());

                account.setBalance(account.getBalance() - billing.getAmount());
                accountServiceClient.updateAccount(account.getUsername(), account);
                logger.info("Account balance updated for user: {}", billing.getUsername());

                billing.setIsPaid(true);
                billing.setPaymentDate(LocalDateTime.now());
                Billing savedBilling = billingRepository.save(billing);
                logger.info("Billing marked as paid and saved for ID: {}", id);

                notifyTransaction(account, billing);

                return savedBilling;
            } else {
                logger.error("Insufficient balance for user: {}", billing.getUsername());
                throw new InsufficientBalanceException("Insufficient balance for user: " + billing.getUsername());
            }
        } else {
            logger.error("Billing is already paid for ID: {}", id);
            throw new BillingPaidException("Billing is already paid for ID: " + id);
        }
        
    }

    public void enableAutoPay(Long id) {
        logger.info("Enabling auto-pay for Billing ID: {}", id);

        Billing billing = billingRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Billing not found with ID: {}", id);
                    return new BillingNotFoundException("Billing not found with ID: " + id);
                });

        billing.setAutoPay(true);
        billingRepository.save(billing);

        logger.info("Auto-pay enabled and saved for Billing ID: {}", id);
    }

    private Account retrieveAccount(String username) {
        logger.info("Retrieving account for username: {}", username);

        try {
            ResponseEntity<Account> response = accountServiceClient.getAccountByUsername(username);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                logger.info("Account successfully retrieved for username: {}", username);
                return response.getBody();
            } else {
                logger.error("Account not found for username: {}", username);
                throw new AccountNotFoundException("Account not found for username: " + username);
            }
        } catch (FeignException e) {
            logger.error("Error occurred while retrieving account for username: {}. FeignException: {}", username, e.getMessage());
            throw new AccountNotFoundException("Account service returned an error: " + e.getMessage());
        }
    }

    private void notifyTransaction(Account account, Billing billing) {
        logger.info("Notifying TransactionService about payment for Billing ID: {}", billing.getId());

        Transaction transaction = new Transaction();
        transaction.setServiceName("BillingService");
        transaction.setAccountId(account.getId().toString());
        transaction.setAmount(billing.getAmount());
        transaction.setTimestamp(new Date());

        Map<String, Object> details = new HashMap<>();
        details.put("billingId", billing.getId());
        details.put("billingDescription", billing.getDescription());
        details.put("paymentDate", billing.getPaymentDate());
        details.put("action", "pay");
        transaction.setDetails(details);

        try {
            transactionClient.saveTransaction(transaction);
            logger.info("Transaction successfully notified for Billing ID: {}", billing.getId());
        } catch (FeignException e) {
            logger.error("Failed to notify TransactionService for Billing ID: {}. FeignException: {}", billing.getId(), e.getMessage());
            throw new TransactionServiceException("Failed to notify TransactionService for Billing ID: " + billing.getId(), e);
        }
    }

    public Billing createBilling(Billing billing) {
        logger.info("Creating new billing for username: {}", billing.getUsername());
        if(retrieveAccount(billing.getUsername()) == null){
            logger.error("Account not found for username: {}", billing.getUsername());
            throw new AccountNotFoundException("Account not found for username: " + billing.getUsername());
        }
        Billing createdBilling = billingRepository.save(billing);
        logger.info("Billing created with ID: {}", createdBilling.getId());
        return createdBilling;
    }
}