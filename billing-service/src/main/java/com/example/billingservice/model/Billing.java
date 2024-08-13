package com.example.billingservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "billings")
public class Billing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @NotNull
    private String username; // Kullanıcı adı

    @Getter
    @Setter
    @NotNull
    private Double amount; // Fatura tutarı

    @Getter
    @Setter
    @NotNull
    private LocalDateTime billingDate; // Fatura tarihi

    @Getter
    @Setter
    private LocalDateTime paymentDate; // Ödeme tarihi

    @Getter
    @Setter
    private Boolean isPaid; // Fatura ödendi mi?

    @Getter
    @Setter
    private Boolean autoPay; // Otomatik ödeme aktif mi?

    @Setter
    @Getter
    private Long price;

    private String billingName;

    public Billing() {
        this.billingDate = LocalDateTime.now(); // Fatura oluşturulduğunda tarih atanır
        this.isPaid = false; // Varsayılan olarak ödenmemiş
        this.autoPay = false; // Varsayılan olarak otomatik ödeme devre dışı
    }
}