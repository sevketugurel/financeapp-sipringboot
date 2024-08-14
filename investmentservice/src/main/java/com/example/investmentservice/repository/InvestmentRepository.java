package com.example.investmentservice.repository;

import com.example.investmentservice.model.Investment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {
    List<Investment> findByUsername(String username);

    Optional<Investment> findByUsernameAndInvestmentType(String username, String investmentType);

}