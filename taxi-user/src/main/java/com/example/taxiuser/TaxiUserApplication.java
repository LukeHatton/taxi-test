package com.example.taxiuser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.example")
@EntityScan(basePackages = "com.example.bean")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.example.taxiorder")
public class TaxiUserApplication {

  public static void main(String[] args) {
    SpringApplication.run(TaxiUserApplication.class, args);
  }

}
