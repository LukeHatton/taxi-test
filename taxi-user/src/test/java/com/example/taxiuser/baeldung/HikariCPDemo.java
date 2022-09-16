package com.example.taxiuser.baeldung;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * project : taxi-test
 * <p>description:
 *
 * @author : consi
 * @since : 2022/9/15
 **/
public class HikariCPDemo {

  private static final HikariDataSource dataSource;

  static {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl("jdbc:mysql://localhost:3306/taxi_db");
    config.setUsername("root");
    config.setPassword("password");
    config.setAutoCommit(false);
    config.setIdleTimeout(60 * 1000);
    config.setConnectionTimeout(10 * 1000);
    config.setDriverClassName("com.mysql.cj.jdbc.Driver");
    Properties properties = new Properties();
    properties.setProperty("minimumIdle", "5");
    properties.setProperty("maximumPoolSize", "25");
    config.setDataSourceProperties(properties);

    dataSource = new HikariDataSource(config);
  }

  public static DataSource getDataSource() {
    return dataSource;
  }

}
