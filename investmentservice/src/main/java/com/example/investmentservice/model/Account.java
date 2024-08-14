package com.example.investmentservice.model;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

public class Account {

    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private Double balance; // Hesap bakiyesi

    @Getter
    @Setter
    private Double investmentBalance=0.0; // Toplam yatırım bakiyesi

    @Getter
    @Setter
    @Column(unique = true, nullable = false)
    private String iban;

    public double getBalance() {
        return balance;
    }

    // Toplam bakiyeyi hesaplayan metot
    public double getTotalBalance() {
        return balance + investmentBalance;
    }

    // Hesap bakiyesini günceller
    public void updateBalance(double amount) {
        this.balance += amount;
    }

    // Yatırım bakiyesini günceller
    public void updateInvestmentBalance(double amount) {
        this.investmentBalance += amount;
    }

    public Double getInvestmentBalance() {
        return investmentBalance != null ? investmentBalance : 0.0;
    }
}