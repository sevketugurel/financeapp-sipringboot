package com.example.accountservice.client;

import com.example.accountservice.model.Transaction;
import com.example.accountservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "transactionservice", configuration = FeignConfig.class)
// By adding configuration = FeignConfig.class, we are telling Feign to use your custom error decoder for this client.
public interface TransactionClient {

    @PostMapping("/transactions")
    void saveTransaction(@RequestBody Transaction transaction);
}
