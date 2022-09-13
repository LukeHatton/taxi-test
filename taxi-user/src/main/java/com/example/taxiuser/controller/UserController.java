package com.example.taxiuser.controller;

import com.example.bean.TaxiOrder;
import com.example.bean.TaxiUser;
import com.example.taxiorder.OrderFeign;
import com.example.taxiuser.repository.TaxiUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

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

  private final OrderFeign orderFeign;

  public UserController(TaxiUserRepository taxiUserRepository, OrderFeign orderFeign) {
    this.taxiUserRepository = taxiUserRepository;
    this.orderFeign = orderFeign;
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
      .uri("http://localhost:8080/user/findByUser")
      .retrieve()
      .toEntity(String.class);
    List<TaxiOrder> orderByUser = orderFeign.getOrderByUser(id);
    ResponseEntity<String> entity = mono.block();
    log.info("==> 通过WebClient得到的响应：" + (entity != null ? entity.getBody() : ""));
    log.info("==> 通过openFeign得到的响应：" + orderByUser.toString());
    return entity;
  }
}
