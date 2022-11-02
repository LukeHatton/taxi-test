package com.example.taxiuser;

import com.example.taxiuser.baeldung.BasicConnectionPool;
import com.example.taxiuser.baeldung.DBCP2Demo;
import com.example.taxiuser.baeldung.HikariCPDemo;
import com.example.taxiuser.threadPool.SimpleHttpServer;
import lombok.Cleanup;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
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
import java.util.Arrays;
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
  static class Simple {
    public List<Boolean> booleanList = new ArrayList<>();
  }

  // 演示SpEL自动增长数组
  static class Demo {
    public List<String> list;
  }

  // 测试SPEL能否被用来判断响应码
  @Test
  public void testForSuccessCode() {
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
    parser.parseExpression("booleanList[0]").setValue(context, simple, "false");
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

  public static String reverseString(String input) {
    StringBuilder backwards = new StringBuilder(input.length());
    for (int i = 0; i < input.length(); i++) {
      backwards.append(input.charAt(input.length() - 1 - i));
    }
    return backwards.toString();
  }

  @Test
  void test06() throws NoSuchMethodException {
    /* ================ SpEL reference: function ================= */
    /*
     * 可以在SpEL中调用类级别的static方法和函数
     * 通过'#'来访问context中的函数，或context中定义的bean
     */
    ExpressionParser parser = new SpelExpressionParser();
    SimpleEvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding().build();
    context.setVariable("reverseString", SimpleTest.class.getDeclaredMethod("reverseString", String.class));

    String value = parser.parseExpression("#reverseString('hello')").getValue(context, String.class);
    System.out.println(value);

    /* ================ SpEL reference: bean reference ================= */
    /*
     * 使用'@bean_name'访问bean容器中的bean
     * 使用'&bean_name'访问bean容器中的工厂bean（即那些用来创建bean的bean）
     * 因为需要注册一个BeanResolver，这个对象只能在Spring环境中使用，因此这里就不演示了
     */
    StandardEvaluationContext context1 = new StandardEvaluationContext();
    context1.setBeanResolver(new BeanFactoryResolver(new GenericApplicationContext()));
  }

  @Test
  void test07() {
    /* ================ SpEL reference: ternary operator ================= */
    // 在SpEL中使用三元操作符，即 if then else
    RequestWrapper request = new RequestWrapper(1, "request 1");

    ExpressionParser parser = new SpelExpressionParser();
    Expression expression = parser.parseExpression("getCode() eq 1 ? 'true' : 'false'");
    String value = expression.getValue(request, String.class);

    System.out.println(value);

    /* ================ SpEL reference: elvis operator ================= */
    // 其实就是对三元操作符的一个简化，可以用来应用默认值。在properties文件中的默认值格式就和elvis operator很像
    RequestWrapper request1 = new RequestWrapper(null, "request 2");
    Expression expression1 = parser.parseExpression("code?:'blablabla'");
    String value1 = expression1.getValue(request1, String.class);

    System.out.println(value1);
  }

  @Test
  void test08() {
    /* ================ SpEL reference: safe navigation operator ================= */
    /*
     * 当处理对象类型时，经常需要进行非空判断
     * 如使用mapper从数据库中查找某条记录，随后操作这个对象的属性。可能出现这种情况：没有查得数据，因此后续的对象属性处理会抛出空指针异常
     * 在SpEL中，可以使用safe navigation operator来避免这个问题：对于null指针，获取对象的属性值会直接返回一个null，而不是抛出异常
     */
    RequestWrapper request = null;
    ExpressionParser parser = new SpelExpressionParser();
    // 通过#root指代当前传入parser的root object
    Expression expression = parser.parseExpression("#root?.code");
    Object value = expression.getValue(request);
    System.out.println(value);
  }

  @SuppressWarnings("unchecked")
  @Test
  void test09() {
    /* ================ SpEL reference: collection ================= */
    /*
     * SpEL支持对Iterable和Map接口实现的选择和注入。以下会分别展示如何进行这种操作
     * 选择语法：'.?[selectionExpression]'
     * 选择第一个符合条件的元素：'.^[selectionExpression]'
     * 选择最后一个符合条件的元素：'.$[selectionExpression]'
     * map可以用SpEL预置变量'key'和'value'来指代key和value
     */
    List<RequestWrapper> list = new ArrayList<>(Arrays.asList(
      new RequestWrapper(1, "req1"),
      new RequestWrapper(2, "req2"),
      new RequestWrapper(3, "req3")));

    ExpressionParser parser = new SpelExpressionParser();
    List<RequestWrapper> value = (List<RequestWrapper>) parser.parseExpression("#root.?[code > 1]").getValue(list);
    System.out.println(value);

    /*
     * 注入语法：'.![projectionExpression]'
     */
    List<Integer> list1 = (List<Integer>) parser.parseExpression("#root.![code]").getValue(list);
    System.out.println(list1);

  }

  @Test
  void test010() {
    /* ================ SpEL reference: expression templating ================= */
    /*
     * 可以将字符串常量与SpEL表达式写在一个字符串中，并用指定的模式来区分常量与SpEL表达式
     * 需要在使用SpelExpressionParser时传入一个ParserContext对象，不传的话就是null，parser会将整个字符串都作为SpEL表达式解析
     * 默认使用TemplateParserContext中定义的匹配字符，不过用户也可以设置自己的匹配字符
     */
    ExpressionParser parser = new SpelExpressionParser();
    String value = parser
      .parseExpression("A random number is #{T(java.lang.Math).random()}", new TemplateParserContext())
      .getValue(String.class);
    System.out.println(value);
  }


}
