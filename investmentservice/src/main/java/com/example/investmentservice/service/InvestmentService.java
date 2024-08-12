package com.example.investmentservice.service;

import com.example.investmentservice.client.AccountServiceClient;
import com.example.investmentservice.dto.AccountDTO;
import com.example.investmentservice.exception.AccountNotFoundException;
import com.example.investmentservice.exception.BalanceException;
import com.example.investmentservice.model.Investment;
import com.example.investmentservice.repository.InvestmentRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InvestmentService {

    private final InvestmentRepository investmentRepository;
    private final AccountServiceClient accountServiceClient;

    @Autowired
    public InvestmentService(InvestmentRepository investmentRepository, AccountServiceClient accountServiceClient) {
        this.investmentRepository = investmentRepository;
        this.accountServiceClient = accountServiceClient;
    }

    public List<Investment> getInvestmentsByUsername(String username) {
        return investmentRepository.findByUsername(username);
    }

    public Investment saveInvestment(Investment investment) {
        // AccountService'ten kullanıcı adını ve bakiyesini doğrula
        AccountDTO account;
        try {
            ResponseEntity<AccountDTO> response = accountServiceClient.getAccountByUsername(investment.getUsername());
            if (response.getStatusCode().is2xxSuccessful()) {
                account = response.getBody();
            } else {
                throw new AccountNotFoundException("Failed to retrieve account: " + response.getStatusCode());
            }
        } catch (FeignException e) {
            throw new AccountNotFoundException("Account service returned an error: " + e.getMessage());
        }

        if (account == null) {
            throw new AccountNotFoundException("Account not found for username: " + investment.getUsername());
        }

        // Hesap bakiyesi yatırım tutarını karşılayabiliyor mu kontrol et
        if (account.getBalance() >= investment.getAmount()) {
            // Yatırım işlemini gerçekleştir
            Investment savedInvestment = investmentRepository.save(investment);
            // Log the successful investment
            return savedInvestment;
        } else {
            throw new BalanceException("Insufficient balance for username: " + investment.getUsername());
        }
    }

    public void deleteInvestment(Long id) {
        investmentRepository.deleteById(id);
    }
}