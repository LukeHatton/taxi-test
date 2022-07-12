package com.example.taxiorder;

import com.example.bean.TaxiOrder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * project : taxi-test
 * <p>description:
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
}
