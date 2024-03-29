package com.example.taxidriver;

import io.seata.spring.annotation.datasource.EnableAutoDataSourceProxy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.example")
@EntityScan(basePackages = "com.example.bean")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.example.taxiorder")
@EnableAutoDataSourceProxy
public class TaxiDriverApplication {

  public static void main(String[] args) {
    SpringApplication.run(TaxiDriverApplication.class, args);
  }

}
