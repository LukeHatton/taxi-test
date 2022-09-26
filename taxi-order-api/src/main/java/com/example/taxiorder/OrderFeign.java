package com.example.taxiorder;

import com.example.bean.TaxiOrder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * project : taxi-test
 * <p>description:
 * taxi-order远程调用接口
 *
 * @author : consi
 * @since : 2022/7/12
 **/
@FeignClient("taxi-order")
public interface OrderFeign {

  @RequestMapping("/findById")
  TaxiOrder getOrderById(Long id);

  @RequestMapping("/order/findById")
  TaxiOrder getOrderById2(Long id);

  @RequestMapping("/findByUser")
  List<TaxiOrder> getOrderByUser(Long id);

  @RequestMapping("/findByDriver")
  List<TaxiOrder> getOrderByDriver(Long id);

  // 使用openFeign进行远程调用，必须将参数封装为对象，或使用@RequestParam标注参数名，否则会导致绑定失败
  @RequestMapping("/order/set")
  String setOrder(@RequestParam("orderId") Long orderId,
                  @RequestParam("userId") Long userId,
                  @RequestParam("orderName") String orderName,
                  @RequestParam("driverId") Long driverId);

  @RequestMapping("/order/driver/update")
  void updateOrderDriver(@RequestParam("driverId") Long driverId,
                         @RequestParam("driverName") String driverName);
}
