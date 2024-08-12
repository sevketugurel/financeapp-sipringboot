package com.example.accountservice.controller;

import com.example.accountservice.model.UserAccount;
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

//    @GetMapping("/test")
//    public String test() {
//        return "Account service is running";
//    }

    @GetMapping
    public List<UserAccount> getAllAccounts() {
        return service.getAllAccounts();
    }

    @GetMapping("/user/{id}")
    public UserAccount getAccountById(@PathVariable Long id) {
        return service.getAccountById(id);
    }

    @PostMapping
    public UserAccount createAccount(@RequestBody UserAccount account) {
        return service.saveAccount(account);
    }
    @PutMapping("/{username}")
    public UserAccount updateAccount(@PathVariable String username, @RequestBody UserAccount account) {
        return service.updateAccount(username, account);
    }

    @DeleteMapping("/{id}")
    public void deleteAccount(@PathVariable Long id) {
        service.deleteAccount(id);
    }
    @GetMapping("/{username}")
    public ResponseEntity<UserAccount> getAccountByUsername(@PathVariable String username) {
        UserAccount account = service.getAccountByUsername(username);
        if (account != null) {
            return ResponseEntity.ok(account);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}