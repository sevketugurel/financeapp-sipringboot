package com.example.billingservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Account {

    private Long id;
    private String username;
    private Double balance;
    private String iban;

    public Account(long l, String username, double v, String tr123456789, LocalDateTime now) {
    }

    // Diğer gereksinimlere göre ek alanlar veya metotlar eklenebilir
}