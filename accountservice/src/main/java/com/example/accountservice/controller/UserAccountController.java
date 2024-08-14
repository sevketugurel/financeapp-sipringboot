package com.example.accountservice.controller;

import com.example.accountservice.model.TransferRequest;
import com.example.accountservice.model.Account;
import com.example.accountservice.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class UserAccountController {

    @Autowired
    private UserAccountService service;

    @GetMapping
    public List<Account> getAllAccounts() {
        return service.getAllAccounts();
    }

    @GetMapping("/{id}")
    public Account getAccountById(@PathVariable Long id) {
        return service.getAccountById(id);
    }

    @PostMapping
    public Account createAccount(@RequestBody Account account) {
        return service.saveAccount(account);
    }

    @PutMapping("/{username}")
    public Account updateAccount(@PathVariable String username, @RequestBody Account account) {
        return service.updateAccount(username, account);
    }

    @DeleteMapping("/{id}")
    public void deleteAccount(@PathVariable Long id) {
        service.deleteAccount(id);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<Account> getAccountByUsername(@PathVariable String username) {
        Account account = service.getAccountByUsername(username);
        if (account != null) {
            return ResponseEntity.ok(account);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transferMoney(@RequestBody TransferRequest request) {
        try {
            service.transferMoney(request.getSenderIban(), request.getReceiverIban(), request.getAmount());
            return ResponseEntity.ok("Transfer successful");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}