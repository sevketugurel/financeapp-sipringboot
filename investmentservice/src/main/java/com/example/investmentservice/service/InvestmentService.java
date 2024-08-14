package com.example.investmentservice.service;

import com.example.investmentservice.client.AccountServiceClient;
import com.example.investmentservice.client.TransactionClient;
import com.example.investmentservice.exception.AccountNotFoundException;
import com.example.investmentservice.exception.BalanceException;
import com.example.investmentservice.model.Account;
import com.example.investmentservice.model.Investment;
import com.example.investmentservice.model.Transaction;
import com.example.investmentservice.repository.InvestmentRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class InvestmentService {

    private final InvestmentRepository investmentRepository;
    private final AccountServiceClient accountServiceClient;
    private final TransactionClient transactionClient;

    @Autowired
    public InvestmentService(InvestmentRepository investmentRepository,
                             AccountServiceClient accountServiceClient,
                             TransactionClient transactionClient) {
        this.investmentRepository = investmentRepository;
        this.accountServiceClient = accountServiceClient;
        this.transactionClient = transactionClient;
    }

    public Investment buyInvestment(String username, String investmentType, int quantity, double pricePerUnit) {
        // AccountService'ten kullanıcı bakiyesini kontrol et
        Account account = retrieveAccount(username);

        double totalCost = quantity * pricePerUnit;

        // Yatırım bakiyesi yeterli mi kontrol et
        Optional<Investment> optionalInvestment = investmentRepository.findByUsernameAndInvestmentType(username,
                investmentType);
        if (optionalInvestment.isPresent()) { // Eğer yatırım varsa
            Investment existingInvestment = optionalInvestment.get();
            // Eğer yatırım bakiyesi yeterli değilse
            if (existingInvestment.getInvestmentBalance() < totalCost) {
                throw new BalanceException("Insufficient investment balance for " + investmentType);
            }
        }

        // Hesap bakiyesi yeterli mi kontrol et
        if (account.getBalance() >= totalCost) {
            // Yatırım işlemini gerçekleştir ve hesaptan miktarı düş
            account.setBalance(account.getBalance() - totalCost);
            accountServiceClient.updateAccount(account, username);

            Investment investment = optionalInvestment.orElseGet(() -> new Investment(username, investmentType, 0, 0.0));

            // investmentName'i kontrol et ve set et
            if (investment.getInvestmentName() == null || investment.getInvestmentName().isEmpty()) {
                investment.setInvestmentName("STOCK"); // Burada uygun bir yatırım adı atanmalı
            }

            // Yeni ortalama birim fiyatını hesapla
            double totalInvestmentValue = (investment.getQuantity() * investment.getPricePerUnit()) + totalCost;
            int newQuantity = investment.getQuantity() + quantity;
            double newAveragePrice = totalInvestmentValue / newQuantity;

            investment.setQuantity(newQuantity);
            investment.setPricePerUnit(newAveragePrice);
            investment.updateAmount();  // Toplam değeri günceller
            investment.updateInvestmentBalance(); // Yatırım bakiyesini günceller

            Investment savedInvestment = investmentRepository.save(investment);

            // TransactionService'e işlemi bildir
            notifyTransaction(account, totalCost, investmentType, quantity, pricePerUnit, "buy");

            return savedInvestment;
        } else {
            throw new BalanceException("Insufficient account balance for username: " + username);
        }
    }

    public Investment sellInvestment(String username, String investmentType, int quantity, double pricePerUnit) {
        Investment investment = investmentRepository.findByUsernameAndInvestmentType(username, investmentType)
                .orElseThrow(() -> new AccountNotFoundException("Investment not found for username: " + username));

        if (investment.getQuantity() < quantity) {
            throw new BalanceException("Not enough quantity to sell");
        }

        Account account = retrieveAccount(username);
        double totalReturn = quantity * pricePerUnit;

        int remainingQuantity = investment.getQuantity() - quantity;
        if (remainingQuantity == 0) {
            investmentRepository.delete(investment);
            return null; // Burada null dönebiliriz veya isteğe bağlı olarak başka bir değer döndürebiliriz.
        } else {
            investment.setQuantity(remainingQuantity);
            investment.updateAmount();
            investment.updateInvestmentBalance();
            investmentRepository.save(investment);
        }

        account.setBalance(account.getBalance() + totalReturn);
        accountServiceClient.updateAccount(account, username);

        notifyTransaction(account, totalReturn, investmentType, quantity, pricePerUnit, "sell");

        return investment;
    }

    // Kullanıcı adıyla yatırımları getir
    public List<Investment> getInvestmentsByUsername(String username) {
        return investmentRepository.findByUsername(username);
    }

    // Yatırımı sil
    public void deleteInvestment(Long id) {
        investmentRepository.deleteById(id);
    }

    // Account'u Feign Client ile almak için kullanılan yardımcı metot
    private Account retrieveAccount(String username) {
        try {
            ResponseEntity<Account> response = accountServiceClient.getAccountByUsername(username);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                throw new AccountNotFoundException("Failed to retrieve account: " + username + response.getStatusCode());
            }
        } catch (FeignException e) {
            throw new AccountNotFoundException("Account service returned an error: " + e.getMessage());
        }
    }

    // Transaction'ı bildirmek için kullanılan yardımcı metot
    private void notifyTransaction(Account account, double amount, String investmentType, int quantity, double pricePerUnit, String action) {
        Transaction transaction = new Transaction();
        transaction.setServiceName("InvestmentService");
        transaction.setAccountId(account.getId().toString());
        transaction.setAmount(amount);
        transaction.setTimestamp(new Date());

        Map<String, Object> details = new HashMap<>();
        details.put("investmentType", investmentType);
        details.put("quantity", quantity);
        details.put("pricePerUnit", pricePerUnit);
        details.put("action", action);
        transaction.setDetails(details);

        transactionClient.saveTransaction(transaction);
    }
}