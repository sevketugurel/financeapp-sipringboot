package com.example.billingservice.repository;

import com.example.billingservice.model.Billing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillingRepository extends JpaRepository<Billing, Long> {
    List<Billing> findByUsername(String username); // Kullanıcı adına göre faturaları getir
    List<Billing> findByIsPaid(Boolean isPaid); // Ödenmiş ya da ödenmemiş faturaları getir
}