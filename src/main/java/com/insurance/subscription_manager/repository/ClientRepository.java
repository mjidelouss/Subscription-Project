package com.insurance.subscription_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.insurance.subscription_manager.entity.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    // Used by the service to enforce the "unique email" rule before insert.
    boolean existsByEmail(String email);
}
