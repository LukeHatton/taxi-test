package com.example.taxigatewat.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * project : taxi-test
 * <p>description:
 *
 * @author : consi
 * @since : 2022/7/11
 **/
@RestController
public class HelloController {

  @PostMapping("/hello")
  public String sayHello() {
    return "hello world!";
  }
}
