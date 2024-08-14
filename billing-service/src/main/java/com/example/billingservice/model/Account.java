package com.example.billingservice.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Account {

    private Long id;
    private String username;
    private Double balance;
    private String iban;

    // Diğer gereksinimlere göre ek alanlar veya metotlar eklenebilir
}