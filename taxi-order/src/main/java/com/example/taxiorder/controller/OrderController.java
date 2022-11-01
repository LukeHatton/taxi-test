package com.example.taxiorder.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.example.bean.TaxiDriver;
import com.example.bean.TaxiOrder;
import com.example.repository.TaxiDriverRepository;
import com.example.repository.TaxiOrderRepository;
import com.zaxxer.hikari.HikariPoolMXBean;
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
@SuppressWarnings("unused")
@RestController
@Slf4j
public class OrderController {

  private final TaxiOrderRepository orderRepository;

  private final TaxiDriverRepository driverRepository;

  private final HikariPoolMXBean hikariPoolMXBean;

  public OrderController(TaxiOrderRepository taxiOrderRepository, TaxiDriverRepository taxiDriverRepository,
                         HikariPoolMXBean hikariPoolMXBean) {
    this.orderRepository = taxiOrderRepository;
    this.driverRepository = taxiDriverRepository;
    this.hikariPoolMXBean = hikariPoolMXBean;
  }

  @RequestMapping("/findById")
  @SentinelResource(
    value = "taxiOrder.getOrderById",
    blockHandler = "getOrderByIdBlockHandler",
    fallback = "getOrderByIdFallbackHandler")
  public TaxiOrder getOrderById(Long id) throws SystemBlockException {
    log.info("==> 进入了【Order】findById方法！");
    if (id == 2) {
      throw new SystemBlockException("taxiOrder.getOrderById", "模拟抛出SystemBlockException");
    }
    if (id == 3) {
      throw new RuntimeException("模拟抛出运行时异常");
    }
    return orderRepository.findById(id).orElse(null);
  }

  // 在sentinel中，因为系统本身过载的原因导致进行限流，称为"降级"；因为下游系统过载而进行限流，称为"熔断"
  public TaxiOrder getOrderByIdBlockHandler(Long id, BlockException e) {
    log.warn("发生sentinel异常，自动降级", e);
    return new TaxiOrder(null, "系统繁忙，请稍后重试", null, null, null, null);
  }

  public TaxiOrder getOrderByIdFallbackHandler(Long id, Throwable throwable) {
    log.warn("发生系统异常，调用fallback函数", throwable);
    return new TaxiOrder(null, "系统错误，么得办法", null, null, null, null);
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

  @RequestMapping("/order/monitor")
  public String getMonitorInfo() {
    return "Hikari Pool State : " +
      "Active=[" + hikariPoolMXBean.getActiveConnections() + "] " +
      "Idle=[" + hikariPoolMXBean.getIdleConnections() + "] " +
      "Await=[" + hikariPoolMXBean.getThreadsAwaitingConnection() + "] " +
      "Total=[" + hikariPoolMXBean.getTotalConnections() + "] ";
  }

}
