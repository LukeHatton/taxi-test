package com.example.taxiorder.repository;

import com.example.bean.TaxiOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaxiOrderRepository extends JpaRepository<TaxiOrder, Long> {
}