package com.example.repository;

import com.example.bean.TaxiOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaxiOrderRepository extends JpaRepository<TaxiOrder, Long> {

  List<TaxiOrder> findTaxiOrdersByToDriverIs(Long driverId);
}