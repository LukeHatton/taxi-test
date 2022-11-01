package com.example.taxiorder.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.sql.DataSource;
import java.lang.management.ManagementFactory;
import java.util.Properties;

/**
 * <p>Project: taxi-test
 * <p>Description:
 * <p>
 *
 * @author lizhao 2022/9/29
 */
@Configuration
@Slf4j
public class HikariCPConfig {

  @Bean
  @Primary
  public DataSource getHikariDataSource() {
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
    config.setRegisterMbeans(true);

    HikariDataSource hikariDataSource = new HikariDataSource(config);
    log.info("==> HikariCP初始化完成");

    return hikariDataSource;
  }

  @Bean
  public HikariPoolMXBean getHikariPoolMXBean(HikariDataSource dataSource) throws MalformedObjectNameException {
    // 初始化mxBean
    MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
    // 注意：名称字符串必须与连接池名称严格一致
    ObjectName poolName = new ObjectName("com.zaxxer.hikari:type=Pool (" + dataSource.getPoolName() + ")");
    return JMX.newMXBeanProxy(mBeanServer, poolName, HikariPoolMXBean.class);
  }

}
