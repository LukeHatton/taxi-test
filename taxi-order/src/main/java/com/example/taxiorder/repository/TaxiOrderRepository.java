package com.example.taxiorder.repository;

import com.example.taxiorder.bean.TaxiOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaxiOrderRepository extends JpaRepository<TaxiOrder, Long> {
}