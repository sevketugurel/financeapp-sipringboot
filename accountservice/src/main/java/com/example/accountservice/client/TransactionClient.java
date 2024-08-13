package com.example.accountservice.client;

import com.example.accountservice.model.AccountTransaction;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "transactionservice")
public interface TransactionClient {

    @PostMapping("/transactions")
    void saveTransaction(@RequestBody AccountTransaction transaction);
}