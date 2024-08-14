package com.example.investmentservice.client;

import com.example.investmentservice.model.Account;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "accountservice")
public interface AccountServiceClient {

    @GetMapping("/accounts/user/{username}")
    ResponseEntity<Account> getAccountByUsername(@PathVariable("username") String username);

    @PutMapping("/accounts/{username}")
    ResponseEntity<Void> updateAccount(@RequestBody Account account, @PathVariable("username") String username);
}