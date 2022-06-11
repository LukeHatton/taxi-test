package com.example.taxiuser.repository;

import com.example.taxiuser.bean.TaxiUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaxiUserRepository extends JpaRepository<TaxiUser, Long> {
}