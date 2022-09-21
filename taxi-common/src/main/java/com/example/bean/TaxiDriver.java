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
@ToString
public class TaxiDriver {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @Column(name = "name", length = 64)
  private String name;
}
