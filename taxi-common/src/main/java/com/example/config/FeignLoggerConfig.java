package com.example.config;

import feign.Logger;
import feign.Logger.Level;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * project : taxi-test
 * <p>description:
 *
 * @author : consi
 * @since : 2022/7/12
 **/
@Configuration
public class FeignLoggerConfig {

  @Bean
  public Logger.Level feignLoggerLevel(){
    return Level.FULL;
  }
}
