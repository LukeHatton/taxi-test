package com.example.taxiorder.controller;

import com.example.bean.TaxiDriver;
import com.example.bean.TaxiOrder;
import com.example.repository.TaxiDriverRepository;
import com.example.repository.TaxiOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
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

  private final TaxiOrderRepository orderRepository;

  private final TaxiDriverRepository driverRepository;

  public OrderController(TaxiOrderRepository taxiOrderRepository, TaxiDriverRepository taxiDriverRepository) {
    this.orderRepository = taxiOrderRepository;
    this.driverRepository = taxiDriverRepository;
  }

  @RequestMapping("/findById")
  public TaxiOrder getOrderById(Long id) {
    log.info("==> 进入了【Order】findById方法！");
    return orderRepository.findById(id).orElse(null);
  }

  @RequestMapping("/order/findById")
  public TaxiOrder getOrderById2(Long id) {
    log.info("==> 进入了【Order】findById方法！");
    return orderRepository.findById(id).orElse(null);
  }

  @RequestMapping("/findByUser")
  public List<TaxiOrder> getOrderByUser(Long id) {
    log.info("==> 进入了【Order】findByUser方法！");
    Example<TaxiOrder> example = Example.of(TaxiOrder.builder().fromUser(id).build());
    return orderRepository.findAll(example);
  }

  @RequestMapping("/findByDriver")
  public List<TaxiOrder> getOrderByDriver(Long id) {
    log.info("==> 进入了【Order】findByDriver方法！");
    Example<TaxiOrder> example = Example.of(TaxiOrder.builder().toDriver(id).build());
    return orderRepository.findAll(example);
  }

  @RequestMapping("/order/set")
  @Transactional
  public String setOrder(Long orderId, Long userId, String orderName, Long driverId) {
    log.info("==> 进入了【Order】setOrder方法！");

    // 在jpa中，如果传了主键orderId，就是更新，否则就是插入
    TaxiOrder taxiOrder = new TaxiOrder();
    taxiOrder.setId(orderId);
    taxiOrder.setFromUser(userId);
    taxiOrder.setOrderName(orderName);
    taxiOrder.setToDriver(driverId);
    taxiOrder.setOrderTime(new Date());
    TaxiDriver driver = driverRepository.findById(driverId).orElse(new TaxiDriver());
    taxiOrder.setDriverName(driver.getName());

    TaxiOrder save = orderRepository.save(taxiOrder);
    log.info("==> 保存后的order信息：【{}】", save);

    return save.toString();
  }

  // 更新taxi_order表中保存的driver信息
  @RequestMapping("/order/driver/update")
  @Transactional
  public void updateOrderDriver(Long driverId, String driverName) {
    log.info("==> 进入了【Order】updateOrderDriver方法！");

    List<TaxiOrder> orders = orderRepository.findTaxiOrdersByToDriverIs(driverId);
    for (TaxiOrder order : orders) {
      order.setDriverName(driverName);
      orderRepository.save(order);
    }
    log.info("==> 根据driverId更新order中的driverName完成！");
  }

}
