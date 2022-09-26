package com.example.taxidriver.controller;

import com.example.bean.TaxiDriver;
import com.example.bean.TaxiOrder;
import com.example.repository.TaxiDriverRepository;
import com.example.taxiorder.OrderFeign;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
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
public class DriverController {

  private final TaxiDriverRepository taxiDriverRepository;

  private final OrderFeign orderFeign;

  public DriverController(TaxiDriverRepository taxiDriverRepository, OrderFeign orderFeign) {
    this.taxiDriverRepository = taxiDriverRepository;
    this.orderFeign = orderFeign;
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
    String url = "http://localhost:8080/order/findByDriver";
    ResponseEntity<String> entity = restTemplate.getForEntity(url, String.class, id);
    List<TaxiOrder> orderByDriver = orderFeign.getOrderByDriver(id);
    log.info("==> 通过restTemplate得到的响应：" + entity.getBody());
    log.info("==> 通过openFeign得到的响应：" + orderByDriver.toString());
    return entity;
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
      .uri("http://localhost:8080/order/findByDriver", id)
      .retrieve()
      .toEntity(String.class);
    // 以下相当于声明了两个mono的消费者，同一个消息会被每个监听者单独消费
    mono.subscribe(e -> log.info("==> 异步非阻塞调用返回值：【{}】", e));
    return mono.block();
  }

  /**
   * <strong>这是一个分布式事务框架seata的测试：</strong>
   * 修改具有指定id的driver的name，同时需要修改订单表taxi_order中对应的司机姓名字段taxi_order.driver_name
   *
   * @param driverId   要修改的driverId
   * @param driverName 要修改的driverName
   */
  @RequestMapping("/driver/update")
  // @Transactional
  @GlobalTransactional
  public void setDriverAndOrder(Long driverId, String driverName) {
    log.info("==> 进入了【Driver】setDriverAndOrder方法！");

    // 首先要修改driver信息
    TaxiDriver save = taxiDriverRepository.save(new TaxiDriver(driverId, driverName));
    log.info("==> 保存后的driver信息：【{}】", save);

    /*
     * 然后要使用feign客户端修改order中的driver信息
     * 如果使用spring提供的@Transactional注解，则如果feign远程调用后出现了异常，是不会回滚在远程调用提交的事务的；
     * 而使用seata管理的分布式事务，即使是远程调用中的提交也能被回滚。不过首先需要建立seata需要的事务表undo_log
     */
    log.info("==> 尝试更新order中的driver信息");
    orderFeign.updateOrderDriver(driverId, driverName);
    // 模拟异常，发现taxi_driver中的数据被正常回滚了，但是通过远程调用提交的数据就无法回滚
    // int i = 1 / 0;
    log.info("==> 更新order中的driver信息完成！");

  }
}
