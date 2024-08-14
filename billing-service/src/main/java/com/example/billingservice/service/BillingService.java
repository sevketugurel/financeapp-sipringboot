package com.example.billingservice.service;

import com.example.billingservice.client.AccountServiceClient;
import com.example.billingservice.client.TransactionClient;
import com.example.billingservice.exception.AccountNotFoundException;
import com.example.billingservice.model.Account;
import com.example.billingservice.model.Billing;
import com.example.billingservice.model.Transaction;
import com.example.billingservice.repository.BillingRepository;
import feign.FeignException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BillingService {

    private final BillingRepository billingRepository;
    private final AccountServiceClient accountServiceClient;
    private final TransactionClient transactionClient;

    public BillingService(BillingRepository billingRepository, AccountServiceClient accountServiceClient,
                          TransactionClient transactionClient) {
        this.billingRepository = billingRepository;
        this.accountServiceClient = accountServiceClient;
        this.transactionClient = transactionClient;
    }

    public List<Billing> getBillingHistory(String username) {
        return billingRepository.findByUsername(username); // Fatura geçmişini getir
    }

    public Billing payBilling(Long id) {
        Billing billing = billingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Billing not found with ID: " + id));

        Account account = retrieveAccount(billing.getUsername());

        // Hesap bakiyesi yeterli mi kontrol et
        if (account.getBalance() >= billing.getAmount()) {
            // Bakiyeden düş
            account.setBalance(account.getBalance() - billing.getAmount());
            accountServiceClient.updateAccount(account.getUsername(),account);

            // Fatura ödendi olarak işaretle ve ödeme tarihini ayarla
            billing.setIsPaid(true);
            billing.setPaymentDate(LocalDateTime.now());
            Billing savedBilling = billingRepository.save(billing);

            // Ödeme işlemini TransactionService'e bildir
            notifyTransaction(account, billing);

            return savedBilling;
        } else {
            throw new RuntimeException("Insufficient balance for user: " + billing.getUsername());
        }
    }

    public void enableAutoPay(Long id) {
        Billing billing = billingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Billing not found with ID: " + id));
        billing.setAutoPay(true); // Otomatik ödeme aktif hale getir
        billingRepository.save(billing);
    }

    private Account retrieveAccount(String username) {
        try {
            ResponseEntity<Account> response = accountServiceClient.getAccountByUsername(username);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new AccountNotFoundException("Account not found for username: " + username);
            }
        } catch (FeignException e) {
            throw new AccountNotFoundException("Account service returned an error: " + e.getMessage());
        }
    }

    private void notifyTransaction(Account account, Billing billing) {
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

        transactionClient.saveTransaction(transaction);
    }

    public Billing createBilling(Billing billing) {
        return billingRepository.save(billing);
    }
}