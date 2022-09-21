package com.example.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * <p>Project: taxi
 * <p>Description:
 * <p>
 *
 * @author lizhao 2022/6/10
 */
@Entity
@Getter
@Setter
@Builder
@Table(name = "taxi_order")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TaxiOrder {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @Column(name = "order_name", length = 64)
  private String orderName;

  @Column(name = "order_time")
  private Date orderTime;

  @Column(name = "from_user")
  private Long fromUser;

  @Column(name = "to_driver")
  private Long toDriver;

  @Column(name = "driver_name")
  private String driverName;
}
