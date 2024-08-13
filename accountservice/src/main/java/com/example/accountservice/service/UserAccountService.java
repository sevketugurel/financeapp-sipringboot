package com.example.accountservice.service;

import com.example.accountservice.client.TransactionClient;
import com.example.accountservice.model.AccountTransaction;
import com.example.accountservice.model.UserAccount;
import com.example.accountservice.model.TransferRequest;
import com.example.accountservice.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserAccountService {

    private final UserAccountRepository repository;
    private final TransactionClient transactionClient;

    @Autowired
    public UserAccountService(UserAccountRepository repository, TransactionClient transactionClient) {
        this.repository = repository;
        this.transactionClient = transactionClient;
    }

    public List<UserAccount> getAllAccounts() {
        return repository.findAll();
    }

    public UserAccount getAccountById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with ID: " + id));
    }

    public UserAccount saveAccount(UserAccount account) {
        // IBAN'in unique olduğundan emin olun
        if (repository.findByIban(account.getIban()).isPresent()) {
            throw new RuntimeException("Account with IBAN already exists: " + account.getIban());
        }
        return repository.save(account);
    }

    public void deleteAccount(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Account not found with ID: " + id);
        }
        repository.deleteById(id);
    }

    public UserAccount updateAccount(String username, UserAccount account) {
        UserAccount existingAccount = repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Account not found with username: " + username));

        // Güncellenen bilgileri mevcut hesaba aktarın
        existingAccount.setBalance(account.getBalance());
        existingAccount.setIban(account.getIban()); // IBAN'i güncellemek isteyebilirsiniz.
        // Burada diğer alanlar da güncellenebilir.

        return repository.save(existingAccount);
    }

    public UserAccount getAccountByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Account not found with username: " + username));
    }

    public void transferMoney(String senderIban, String receiverIban, Double amount) {
        UserAccount sender = repository.findByIban(senderIban)
                .orElseThrow(() -> new RuntimeException("Sender account not found with IBAN: " + senderIban));
        UserAccount receiver = repository.findByIban(receiverIban)
                .orElseThrow(() -> new RuntimeException("Receiver account not found with IBAN: " + receiverIban));

        if (sender.getBalance() < amount) {
            throw new RuntimeException("Insufficient funds in sender's account");
        }

        // Para transferini gerçekleştirin
        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);

        repository.save(sender);
        repository.save(receiver);

        // TransactionService'e transferi bildir
        AccountTransaction transaction = new AccountTransaction();
        transaction.setServiceName("AccountService");
        transaction.setAccountId(sender.getId().toString());
        transaction.setAmount(-amount);
        transaction.setTimestamp(new Date());

        Map<String, Object> details = new HashMap<>();
        details.put("senderIban", senderIban);
        details.put("receiverIban", receiverIban);
        details.put("transferAmount", amount);
        transaction.setDetails(details);

        transactionClient.saveTransaction(transaction);

        // Alıcı için de bir Transaction kaydedin
        AccountTransaction receiverTransaction = new AccountTransaction();
        receiverTransaction.setServiceName("AccountService");
        receiverTransaction.setAccountId(receiver.getId().toString());
        receiverTransaction.setAmount(amount);
        receiverTransaction.setTimestamp(new Date());
        receiverTransaction.setDetails(details);

        transactionClient.saveTransaction(receiverTransaction);
    }
}