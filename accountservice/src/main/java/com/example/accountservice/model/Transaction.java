package com.example.accountservice.model;

import com.example.accountservice.converter.JsonAttributeConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

@Entity
@Table(name = "account_transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    private String serviceName = "AccountService";

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