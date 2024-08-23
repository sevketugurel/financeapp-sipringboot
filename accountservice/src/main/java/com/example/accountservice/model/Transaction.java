package com.example.accountservice.model;

import com.example.accountservice.converter.JsonAttributeConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Map;

@Entity
@Table(name = "account_transaction")
@Getter
@Setter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String serviceName = "AccountService";


    private String accountId;


    private double amount;


    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;


    @Convert(converter = JsonAttributeConverter.class)
    @Column(columnDefinition = "TEXT")
    private Map<String, Object> details;
}