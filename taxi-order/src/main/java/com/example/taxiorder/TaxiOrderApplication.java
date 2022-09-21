package com.example.taxiorder;

import io.seata.spring.annotation.datasource.EnableAutoDataSourceProxy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = "com.example")
@EntityScan(basePackages = "com.example.bean")
@EnableDiscoveryClient
@EnableAutoDataSourceProxy
public class TaxiOrderApplication {

  public static void main(String[] args) {
    SpringApplication.run(TaxiOrderApplication.class, args);
  }

}
