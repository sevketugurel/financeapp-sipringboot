package com.example.billingservice.client;

import com.example.billingservice.config.FeignConfig;
import com.example.billingservice.model.Account;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "accountservice", configuration = FeignConfig.class) // Eureka üzerinde accountservice adını taşıyan servise bağlanır.
public interface AccountServiceClient {

    @GetMapping("/accounts/user/{username}")
    ResponseEntity<Account> getAccountByUsername(@PathVariable String username);

    @PutMapping("/accounts/{username}")
    ResponseEntity<Account> updateAccount(@PathVariable String username, Account account);
}
