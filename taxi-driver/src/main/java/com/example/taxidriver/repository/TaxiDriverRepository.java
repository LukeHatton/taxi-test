package com.example.taxidriver.repository;

import com.example.taxidriver.bean.TaxiDriver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaxiDriverRepository extends JpaRepository<TaxiDriver, Long> {
}