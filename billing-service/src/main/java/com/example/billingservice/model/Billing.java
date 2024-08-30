package com.example.billingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "billings")
@AllArgsConstructor

public class Billing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private Double amount;

    @Getter
    @Setter
    private LocalDateTime billingDate;

    @Getter
    @Setter
    private LocalDateTime paymentDate;

    @Getter
    @Setter
    private Boolean isPaid;

    @Getter
    @Setter
    private Boolean autoPay;

    @Getter
    @Setter
    private String description;  // Faturanın açıklaması

    public Billing() {
        this.billingDate = LocalDateTime.now(); // Fatura oluşturulduğunda tarih atanır
        this.isPaid = false; // Varsayılan olarak ödenmemiş
        this.autoPay = false; // Varsayılan olarak otomatik ödeme devre dışı
    }


    public Billing(long l, String username, double v, boolean b, LocalDateTime now) {
    }
}