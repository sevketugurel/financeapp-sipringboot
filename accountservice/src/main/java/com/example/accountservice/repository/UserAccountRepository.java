package com.example.accountservice.repository;

import com.example.accountservice.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByUsername(String username);
    Optional<Account> findByIban(String iban);

}