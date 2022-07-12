package com.example.taxiorder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TaxiOrderApplication {

  public static void main(String[] args) {
    SpringApplication.run(TaxiOrderApplication.class, args);
  }

}
