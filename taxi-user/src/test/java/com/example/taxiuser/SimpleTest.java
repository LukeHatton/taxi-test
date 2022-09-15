package com.example.taxiuser;

import com.example.taxiuser.baeldung.BasicConnectionPool;
import com.example.taxiuser.baeldung.DBCP2Demo;
import com.example.taxiuser.baeldung.HikariCPDemo;
import com.example.taxiuser.threadPool.SimpleHttpServer;
import lombok.Cleanup;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * project : taxi-test
 * <p>description:
 *
 * @author : consi
 * @since : 2022/8/24
 **/
public class SimpleTest {

  private static final Logger log = LoggerFactory.getLogger(SimpleTest.class);

  volatile int i = 1;

  public static void main1(String[] args) {
    ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(false, false);
    for (ThreadInfo threadInfo : threadInfos) {
      log.info("线程信息：【{}】", threadInfo.getThreadName());
    }
  }

  public static void main(String[] args) throws IOException {
    SimpleHttpServer.setPort(8080);
    SimpleHttpServer.setBasePath("D:\\摄影");
    SimpleHttpServer.start();

  }

  // 基本连接池实现
  @Test
  void test01() throws SQLException {
    BasicConnectionPool connectionPool = BasicConnectionPool.create("jdbc:mysql://localhost:3306/taxi_db", "root", "password");
    Connection connection = connectionPool.getConnection();

    assertTrue(connection.isValid(1000));
  }

  // DBCP 2 BasicDataSource
  @Test
  void test02() throws SQLException {
    @Cleanup Connection connection = DBCP2Demo.getDataSource().getConnection();
    @Cleanup Statement statement = connection.createStatement();
    @Cleanup ResultSet resultSet = statement.executeQuery("SELECT * FROM taxi_db.taxi_driver");
    while (resultSet.next()) {
      log.info("driver: id = {}, name = {}", resultSet.getLong("id"), resultSet.getString("name"));
    }
  }

  // DBCP 2 PoolingDataSource
  @Test
  void test03() throws SQLException {
    @Cleanup Connection connection = DBCP2Demo.getPoolingDataSource().getConnection();
    @Cleanup Statement statement = connection.createStatement();
    @Cleanup ResultSet resultSet = statement.executeQuery("SELECT * FROM taxi_db.taxi_driver");
    while (resultSet.next()) {
      log.info("driver: id = {}, name = {}", resultSet.getLong("id"), resultSet.getString("name"));
    }
  }

  @Test
  void test04() throws SQLException {
    @Cleanup Connection connection = HikariCPDemo.getDataSource().getConnection();
    @Cleanup Statement statement = connection.createStatement();
    @Cleanup ResultSet resultSet = statement.executeQuery("SELECT * FROM taxi_db.taxi_driver");
    while (resultSet.next()) {
      log.info("driver: id = {}, name = {}", resultSet.getLong("id"), resultSet.getString("name"));
    }
  }
}
