package com.example.billingservice.client;

import com.example.billingservice.config.FeignConfig;
import com.example.billingservice.model.Transaction;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "eureka-feignclient", configuration = FeignConfig.class)
public interface TransactionClient {

    @PostMapping("/transactions")
    void saveTransaction(@RequestBody Transaction transaction);
}