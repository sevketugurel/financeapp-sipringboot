package com.example.investmentservice.model;

import  com.example.investmentservice.converter.JsonAttributeConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

@Entity
@Table(name = "investment_transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    private String serviceName = "InvestmentService";  // Servis adı "InvestmentService" olarak ayarlandı

    @Getter
    @Setter
    private String accountId;

    @Getter
    @Setter
    private double amount;

    @Getter
    @Setter
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    @Getter
    @Setter
    @Convert(converter = JsonAttributeConverter.class)
    @Column(columnDefinition = "TEXT")
    private Map<String, Object> details;
}