package com.example.taxiuser;

import com.example.taxiuser.baeldung.BasicConnectionPool;
import com.example.taxiuser.baeldung.DBCP2Demo;
import com.example.taxiuser.baeldung.HikariCPDemo;
import com.example.taxiuser.threadPool.SimpleHttpServer;
import lombok.Cleanup;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
    BasicConnectionPool connectionPool = BasicConnectionPool.create("jdbc:mysql://localhost:3306/taxi_db", "root",
      "password");
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

  @Test
  void test05() throws SQLException {
    @Cleanup Connection connection = HikariCPDemo.getDataSource().getConnection();
    // 将隔离级别设置为读已提交
    connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
    @Cleanup Statement statement = connection.createStatement();
    // 对于结果是update count类型的语句，返回的也会是false
    boolean execute = statement.execute("INSERT INTO taxi_driver (id, name) VALUE (2,'Bob')");
    // 提交修改
    connection.commit();
    // 看看能否看到提交的事务中的数据
    @Cleanup ResultSet resultSet = statement.executeQuery("SELECT * FROM taxi_db.taxi_driver");
    while (resultSet.next()) {
      log.info("driver: id = {}, name = {}", resultSet.getLong("id"), resultSet.getString("name"));
    }
    log.info("运行结果：{}", execute);
  }

  // 演示SpEL自动类型转换
  static class Simple{
    public List<Boolean> booleanList = new ArrayList<>();
  }

  // 演示SpEL自动增长数组
  static class Demo{
    public List<String> list;
  }

  // 测试SPEL能否被用来判断响应码
  @Test
  public void testForSuccessCode(){
    RequestWrapper request1 = new RequestWrapper(1, "request 1");
    RequestWrapper request2 = new RequestWrapper(2, "request 2");

    ExpressionParser parser = new SpelExpressionParser();
    Expression expression = parser.parseExpression("(code == 1) or (code == 10)");
    // StandardEvaluationContext对象的构造比较昂贵，因此尽量不要多次创建该对象
    EvaluationContext evaluationContext = new StandardEvaluationContext(request1);

    // 如果没有显式创建EvaluationContext对象，则会由Expression隐式地创建
    Object value1 = expression.getValue(request1);
    System.out.println(value1);

    Object value2 = expression.getValue(request2);
    System.out.println(value2);

    // 还可以通过SpEL设置对象的值。底层使用反射
    // 同时测试SpEL能否自动处理类型转换
    Simple simple = new Simple();
    simple.booleanList.add(true);
    SimpleEvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding().build();
    parser.parseExpression("booleanList[0]").setValue(context,simple,"false");
    Boolean x = simple.booleanList.get(0);
    System.out.println(x);

    // 可以看到自动增长了数组，并给其中元素赋予了默认值（调用无参构造方法）
    SpelParserConfiguration configuration = new SpelParserConfiguration(true, true);
    ExpressionParser parser1 = new SpelExpressionParser(configuration);
    Expression expression1 = parser1.parseExpression("list[3]");
    Demo demo = new Demo();
    Object value = expression1.getValue(demo);
    System.out.println(demo.list.size());
    System.out.println(value);

    System.out.println("---");
  }

}
