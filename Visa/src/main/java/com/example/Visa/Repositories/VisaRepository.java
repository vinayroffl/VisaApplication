package com.example.Visa.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Visa.Entities.Visa;

@Repository
public interface VisaRepository extends JpaRepository<Visa, Integer> {

    Optional<Visa> findByApplicationId(String applicationId);

    boolean existsByApplicationId(String applicationId);
}
