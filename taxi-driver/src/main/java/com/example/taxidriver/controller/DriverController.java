package com.example.taxidriver.controller;

import com.example.bean.TaxiDriver;
import com.example.taxidriver.repository.TaxiDriverRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
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
public class DriverController {

  private final TaxiDriverRepository taxiDriverRepository;

  public DriverController(TaxiDriverRepository taxiDriverRepository) {
    this.taxiDriverRepository = taxiDriverRepository;
  }

  @RequestMapping("/findById")
  public TaxiDriver findById(Long id) {
    log.info("==> 进入了【Driver】findById方法！");
    return taxiDriverRepository.findById(id).orElse(new TaxiDriver());
  }

  /**
   * 通过RestTemplate进行远程http调用
   *
   * @param id 司机id
   */
  @RequestMapping("/findDriverOrders/restTemplate")
  public ResponseEntity<String> getDriverOrderWithRestTemplate(Long id) {
    log.info("==> 进入了【Driver】getDriverOrderWithRestTemplate方法！");
    RestTemplate restTemplate = new RestTemplate();
    String url = "http://localhost:8081/findByDriver";
    return restTemplate.getForEntity(url, String.class, id);
  }

  /**
   * 通过WebClient进行远程http调用
   *
   * @param id 司机id
   */
  @RequestMapping("/findDriverOrders/webClient")
  public ResponseEntity<String> getDriverOrderWithWebClient(Long id) {
    log.info("==> 进入了【Driver】getDriverOrderWithWebClient方法！");
    Mono<ResponseEntity<String>> mono = WebClient.create()
      .get()
      .uri("http://localhost:8081/findByDriver", id)
      .retrieve()
      .toEntity(String.class);
    // 以下相当于声明了两个mono的消费者，同一个消息会被每个监听者单独消费
    mono.subscribe(e -> log.info("==> 异步非阻塞调用返回值：【{}】", e));
    return mono.block();
  }
}
