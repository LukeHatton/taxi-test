package com.example.repository;

import com.example.bean.TaxiUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaxiUserRepository extends JpaRepository<TaxiUser, Long> {
}