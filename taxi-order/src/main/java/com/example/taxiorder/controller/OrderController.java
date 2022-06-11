package com.example.taxiorder.controller;

import com.example.taxiorder.bean.TaxiOrder;
import com.example.taxiorder.repository.TaxiOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
public class OrderController {

  private final TaxiOrderRepository taxiOrderRepository;

  public OrderController(TaxiOrderRepository taxiOrderRepository) {
    this.taxiOrderRepository = taxiOrderRepository;
  }

  @RequestMapping("/findById")
  public TaxiOrder getOrderById(Long id) {
    log.info("==> 进入了【Order】findById方法！");
    return taxiOrderRepository.findById(id).orElse(null);
  }

  @RequestMapping("/findByUser")
  public List<TaxiOrder> getOrderByUser(Long id) {
    log.info("==> 进入了【Order】findByUser方法！");
    Example<TaxiOrder> example = Example.of(TaxiOrder.builder().fromUser(id).build());
    return taxiOrderRepository.findAll(example);
  }

  @RequestMapping("/findByDriver")
  public List<TaxiOrder> getOrderByDriver(Long id) {
    log.info("==> 进入了【Order】findByDriver方法！");
    Example<TaxiOrder> example = Example.of(TaxiOrder.builder().toDriver(id).build());
    return taxiOrderRepository.findAll(example);
  }
}
