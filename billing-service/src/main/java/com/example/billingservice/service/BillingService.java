package com.example.billingservice.service;

import com.example.billingservice.client.AccountServiceClient;
import com.example.billingservice.dto.AccountDTO;
import com.example.billingservice.exception.AccountNotFoundException;
import com.example.billingservice.model.Billing;
import com.example.billingservice.repository.BillingRepository;
import feign.FeignException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BillingService {

    private final BillingRepository billingRepository;
    private final AccountServiceClient accountServiceClient;

    public BillingService(BillingRepository billingRepository, AccountServiceClient accountServiceClient) {
        this.billingRepository = billingRepository;
        this.accountServiceClient = accountServiceClient;
    }

    public List<Billing> getBillingHistory(String username) {
        return billingRepository.findByUsername(username); // Fatura geçmişi
    }

    public Billing createBilling(Billing billing) {
        AccountDTO account = retrieveAccount(billing.getUsername());

        billingRepository.save(billing);
        return billing;
    }

    public Billing payBilling(Long id) {
        Billing billing = billingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Billing not found with ID: " + id));
        billing.setIsPaid(true);
        billing.setPaymentDate(LocalDateTime.now()); // Ödeme tarihi
        return billingRepository.save(billing);
    }

    public void enableAutoPay(Long id) {
        Billing billing = billingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Billing not found with ID: " + id));
        billing.setAutoPay(true); // Otomatik ödeme aktif hale getir
        billingRepository.save(billing);
    }

    private AccountDTO retrieveAccount(String username) {
        try {
            ResponseEntity<AccountDTO> response = accountServiceClient.getAccountByUsername(username);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new AccountNotFoundException("Account not found for username: " + username);
            }
        } catch (FeignException e) {
            throw new AccountNotFoundException("Account service returned an error: " + e.getMessage());
        }
    }
}