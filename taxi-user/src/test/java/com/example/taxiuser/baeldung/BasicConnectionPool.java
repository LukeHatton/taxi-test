package com.example.taxiuser.baeldung;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * project : taxi-test
 * <p>description:
 * 自定义连接池实现类，实现了{@link ConnectionPool}接口。使用两个链表维持连接的引用，一个用来存放现在的可用连接，另一个则用来存放已经被使用了的连接
 * <p>
 * 这个类实现了连接池的最小化功能，但是不能投入实际应用中，因为这个类并不是线程安全的，而<strong>线程安全则是连接池实现的一个基本要素</strong>
 *
 * @author : consi
 * @since : 2022/9/15
 **/
public class BasicConnectionPool implements ConnectionPool {

  private final String url;

  private final String user;

  private final String password;

  private final LinkedList<Connection> pool;

  private final LinkedList<Connection> usedPool = new LinkedList<>();

  private final static int DEFAULT_POOL_SIZE = 10;

  @Override
  public Connection getConnection() {
    Connection connection = pool.removeFirst();
    usedPool.addLast(connection);
    return connection;
  }

  @Override
  public boolean releaseConnection(Connection connection) {
    pool.addLast(connection);
    return usedPool.remove(connection);
  }

  @Override
  public String getUrl() {
    return this.url;
  }

  @Override
  public String getUser() {
    return this.user;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  public BasicConnectionPool(String url, String user, String password, LinkedList<Connection> pool) {
    this.url = url;
    this.user = user;
    this.password = password;
    this.pool = pool;
  }

  /**
   * 静态方法，用来初始化连接池对象。是对工厂模式的简化使用
   *
   * @param url      url
   * @param user     username
   * @param password password
   * @return BasicConnectionPool对象
   * @throws SQLException 获取连接失败
   */
  public static BasicConnectionPool create(String url, String user, String password) throws SQLException {
    LinkedList<Connection> connections = new LinkedList<>();
    for (int i = 0; i < DEFAULT_POOL_SIZE; i++) {
      connections.addLast(createConnection(url, user, password));
    }
    return new BasicConnectionPool(url, user, password, connections);
  }

  private static Connection createConnection(String url, String user, String password) throws SQLException {
    return DriverManager.getConnection(url, user, password);
  }

  /**
   * 获得当前连接池的大小
   *
   * @return int numbers of connections this connection pool is holding
   */
  public int getSize() {
    return pool.size() + usedPool.size();
  }

}
