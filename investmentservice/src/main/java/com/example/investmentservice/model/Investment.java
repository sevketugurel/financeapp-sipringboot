package com.example.investmentservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "investments")
public class Investment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Column(name = "investment_type")
    private String investmentType;

    @Getter
    @Setter
    private Double amount; // Toplam yatırım değeri (miktar * birim fiyat)

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private String status; // Yatırım durumu (aktif, satıldı, vs.)

    @Getter
    @Setter
    @NotNull
    @Column(name = "investment_name", nullable = false)
    private String investmentName; // Yatırımın ismi (örneğin Tesla hissesi)

    @Getter
    @Setter
    private String timeOfPurchase; // Yatırımın yapıldığı zaman

    @Getter
    @Setter
    private int quantity; // Alınan yatırım miktarı (örneğin 100 adet hisse)

    @Getter
    @Setter
    private Double pricePerUnit; // Yatırımın birim fiyatı

    @Getter
    @Setter
    private Double investmentBalance; // Yatırımın toplam bakiyesi

    public Investment() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        this.timeOfPurchase = LocalDateTime.now().format(formatter);
    }

    public Investment(String username, String investmentType, int i, double v) {
        this.username = username;
        this.investmentType = investmentType;
        this.quantity = i;
        this.pricePerUnit = v;
    }

    @PrePersist
    public void onPrePersist() {
        if (this.timeOfPurchase == null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            this.timeOfPurchase = LocalDateTime.now().format(formatter);
        }
    }

    public void updateAmount() {
        this.amount = this.quantity * this.pricePerUnit;
        this.updateInvestmentBalance();
    }

    public void updateInvestmentBalance() {
        this.investmentBalance = this.quantity * this.pricePerUnit;
    }
    public Double getInvestmentBalance() {
        return investmentBalance != null ? investmentBalance : 0.0;
    }
}