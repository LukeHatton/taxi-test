package com.example.taxiuser;

/**
 * <p>Project: taxi-test
 * <p>Description:
 * <p>
 *
 * @author lizhao 2022/10/28
 */
public class RequestWrapper {
  Integer code;
  String req;

  public RequestWrapper() {
  }

  public RequestWrapper(Integer code, String req) {
    this.code = code;
    this.req = req;
  }

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public String getReq() {
    return req;
  }

  public void setReq(String req) {
    this.req = req;
  }
}
