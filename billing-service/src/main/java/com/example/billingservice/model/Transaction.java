package com.example.billingservice.model;

import com.example.billingservice.converter.JsonAttributeConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

@Entity
@Table(name = "billing_transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    private String serviceName = "BillingService";

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