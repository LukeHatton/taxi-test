package com.example.taxiuser.controller;

import com.example.taxiuser.bean.TaxiUser;
import com.example.taxiuser.repository.TaxiUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * <p>Project: taxi
 * <p>Description:
 * <p>
 *
 * @author lizhao 2022/6/10
 */
@RestController
@Slf4j
public class UserController {

  private final TaxiUserRepository taxiUserRepository;

  public UserController(TaxiUserRepository taxiUserRepository) {
    this.taxiUserRepository = taxiUserRepository;
  }

  @RequestMapping("/findById")
  public TaxiUser findById(Long id) {
    log.info("==> 进入了【User】findById方法！");
    return taxiUserRepository.findById(id).orElse(new TaxiUser());
  }

  @RequestMapping("/findUserOrders/webClient")
  public ResponseEntity<String> getUserOrderWithWebClient(Long id) {
    log.info("==> 进入了【User】getUserOrderWithWebClient方法！");
    Mono<ResponseEntity<String>> mono = WebClient.create()
      .get()
      .uri("http://localhost:8081/findByUser")
      .retrieve()
      .toEntity(String.class);
    return mono.block();
  }
}
