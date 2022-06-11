package com.example.taxidriver.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

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
@Table(name = "taxi_driver")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxiDriver {

  @Id
  @GeneratedValue
  private Long id;

  @Column(name = "name", length = 64)
  private String name;
}
