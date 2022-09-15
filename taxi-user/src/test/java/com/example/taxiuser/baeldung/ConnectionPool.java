package com.example.taxiuser.baeldung;

import java.sql.Connection;

/**
 * project : taxi-test
 * <p>description:
 * 自定义线程池接口，线程池的实现类会实现此接口
 *
 * @author : consi
 * @since : 2022/9/15
 **/
public interface ConnectionPool {

  /**
   * 获取一个连接
   *
   * @return connection instance
   */
  Connection getConnection();

  /**
   * 释放一个连接。连接不会close，而是放回到线程池中
   *
   * @param connection 要释放的连接
   * @return 连接释放是否成功 true:成功 false:失败
   */
  boolean releaseConnection(Connection connection);

  /**
   * 获得此连接池连接到的URL
   *
   * @return string of url
   */
  String getUrl();

  /**
   * 获得此连接池使用的用户名
   *
   * @return string of username
   */
  String getUser();

  /**
   * 获得此连接池使用的对应user的密码
   *
   * @return string of password
   */
  String getPassword();
}
