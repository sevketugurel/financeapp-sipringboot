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
    private Double amount;

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private String status;

    @Getter
    @Setter
    @NotNull
    @Column(name = "investment_name", nullable = false)
    private String investmentName;

    @Getter
    @Setter
    private String time_of_purchase;

    // Zaman damgası oluşturma ve stringe çevrme


    public Investment() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        this.time_of_purchase = LocalDateTime.now().format(formatter);
    }

    @Getter
    @Setter
    private Long price;
}