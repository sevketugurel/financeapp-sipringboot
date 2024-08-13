package com.example.billingservice.controller;

import com.example.billingservice.model.Billing;
import com.example.billingservice.service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/billings")
public class BillingController {

    @Autowired
    private BillingService billingService;

    @GetMapping("/{username}")
    public List<Billing> getBillingHistory(@PathVariable String username) {
        return billingService.getBillingHistory(username);
    }

    @PostMapping
    public ResponseEntity<Billing> createBilling(@RequestBody Billing billing) {
        Billing createdBilling = billingService.createBilling(billing);
        return new ResponseEntity<>(createdBilling, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<Billing> payBilling(@PathVariable Long id) {
        Billing paidBilling = billingService.payBilling(id);
        return new ResponseEntity<>(paidBilling, HttpStatus.OK);
    }

    @PostMapping("/{id}/auto-pay")
    public ResponseEntity<Void> enableAutoPay(@PathVariable Long id) {
        billingService.enableAutoPay(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}