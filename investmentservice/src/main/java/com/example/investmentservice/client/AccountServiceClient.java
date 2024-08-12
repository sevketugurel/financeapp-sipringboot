package com.example.investmentservice.client;

import com.example.investmentservice.dto.AccountDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "accountservice") // Eureka üzerinde accountservice adını taşıyan servise bağlanır.
public interface AccountServiceClient {

    @GetMapping("/accounts/{username}")
    ResponseEntity<AccountDTO> getAccountByUsername(@PathVariable String username);
}
