package com.example.taxiuser.baeldung;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.Properties;

/**
 * project : taxi-test
 * <p>description:
 *
 * @author : consi
 * @since : 2022/9/15
 **/
public class DBCP2Demo {

  private static final BasicDataSource dataSource;

  private static final PoolingDataSource<PoolableConnection> poolingDataSource;

  static {

    /* ================ BasicDataSource ================= */
    dataSource = new BasicDataSource();
    dataSource.setUrl("jdbc:mysql://localhost:3306/taxi_db");
    dataSource.setUsername("root");
    dataSource.setPassword("password");

    dataSource.setMinIdle(5);
    dataSource.setMaxIdle(10);
    dataSource.setMaxTotal(25);


    /* ================ PoolingDataSource ================= */
    Properties properties = new Properties();
    properties.setProperty("user", "root");
    properties.setProperty("password", "password");
    // 1. 创建一个ConnectionFactory实例
    ConnectionFactory connectionFactory = new DriverManagerConnectionFactory("jdbc:mysql://localhost:3306/taxi_db", properties);
    // 2. 创建一个PoolableConnectionFactory实例
    PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);
    // 3. 创建一个GenericObjectPoolConfig实例并配置
    GenericObjectPoolConfig<PoolableConnection> config = new GenericObjectPoolConfig<>();
    config.setMinIdle(5);
    config.setMaxIdle(10);
    config.setMaxTotal(25);
    // 4. 初始化ObjectPool
    ObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(poolableConnectionFactory, config);
    // 5. 将ObjectPool对象设置到PoolableConnectionFactory中
    poolableConnectionFactory.setPool(connectionPool);
    // 6. 初始化DataSource实例
    poolingDataSource = new PoolingDataSource<>(connectionPool);

  }

  public static BasicDataSource getDataSource() {
    return dataSource;
  }

  public static PoolingDataSource<PoolableConnection> getPoolingDataSource() {
    return poolingDataSource;
  }
  
}
