package com.example.investmentservice.controller;

import com.example.investmentservice.model.Investment;
import com.example.investmentservice.service.InvestmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/investments")
public class InvestmentController {

    private final InvestmentService investmentService;

    @Autowired
    public InvestmentController(InvestmentService investmentService) {
        this.investmentService = investmentService;
    }

    // Kullanıcı adıyla yatırımları al
    @GetMapping("/user/{username}")
    public ResponseEntity<List<Investment>> getInvestmentsByUsername(@PathVariable String username) {
        try {
            List<Investment> investments = investmentService.getInvestmentsByUsername(username);
            if (investments.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(investments, HttpStatus.OK);
        } catch (Exception e) {
            // Hata mesajını logla
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Yeni yatırım oluştur
    @PostMapping("/buy")
    public ResponseEntity<Investment> buyInvestment(@RequestBody Investment investmentRequest) {
        try {
            Investment createdInvestment = investmentService.buyInvestment(
                    investmentRequest.getUsername(),
                    investmentRequest.getInvestmentType(),
                    investmentRequest.getQuantity(),
                    investmentRequest.getPricePerUnit()
            );
            return new ResponseEntity<>(createdInvestment, HttpStatus.CREATED);
        } catch (Exception e) {
            // Hata mesajını logla
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Yatırımı sat
    @PostMapping("/sell")
    public ResponseEntity<Investment> sellInvestment(@RequestBody Investment investmentRequest) {
        try {
            Investment updatedInvestment = investmentService.sellInvestment(
                    investmentRequest.getUsername(),
                    investmentRequest.getInvestmentType(),
                    investmentRequest.getQuantity(),
                    investmentRequest.getPricePerUnit()
            );
            return new ResponseEntity<>(updatedInvestment, HttpStatus.OK);
        } catch (Exception e) {
            // Hata mesajını logla
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Yatırımı id ile sil
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvestment(@PathVariable Long id) {
        try {
            investmentService.deleteInvestment(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            // Hata mesajını logla
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}