package com.example.transactionservice.service;

import com.example.transactionservice.model.Transaction;
import com.example.transactionservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository repository;

    // Tüm işlemleri getirir
    public List<Transaction> getAllTransactions() {
        // Veritabanından tüm işlemleri alır ve geri döner
        return repository.findAll();
    }

    // Yeni bir işlemi kaydeder
    public Transaction saveTransaction(Transaction transaction) {
        // İşlem kaydedilir ve geri döner
        return repository.save(transaction);
    }
}