package com.example.taxidriver.repository;

import com.example.bean.TaxiDriver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaxiDriverRepository extends JpaRepository<TaxiDriver, Long> {
}