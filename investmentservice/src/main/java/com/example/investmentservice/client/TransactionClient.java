package com.example.investmentservice.client;

import com.example.investmentservice.model.Transaction;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "transactionservice")
public interface TransactionClient {

    @PostMapping("/transactions")
    void saveTransaction(@RequestBody Transaction transaction);
}