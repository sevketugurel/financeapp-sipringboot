package com.example.accountservice.service;

import com.example.accountservice.model.UserAccount;
import com.example.accountservice.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAccountService {

    private final UserAccountRepository repository;

    @Autowired
    public UserAccountService(UserAccountRepository repository) {
        this.repository = repository;
    }

    public List<UserAccount> getAllAccounts() {
        return repository.findAll();
    }

    public UserAccount getAccountById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with ID: " + id));
    }

    public UserAccount saveAccount(UserAccount account) {
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
        existingAccount.setBalance(account.getBalance());
        // Burada diğer alanlar da güncellenebilir.
        return repository.save(existingAccount);
    }

    public UserAccount getAccountByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Account not found with username: " + username));
    }
}