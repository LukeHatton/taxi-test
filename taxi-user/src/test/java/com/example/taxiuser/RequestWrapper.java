package com.example.taxiuser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * <p>Project: taxi-test
 * <p>Description:
 * <p>
 *
 * @author lizhao 2022/10/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestWrapper {
  Integer code;
  String req;
}
