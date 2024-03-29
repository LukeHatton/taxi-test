package com.example.taxigateway;

import io.seata.spring.annotation.datasource.EnableAutoDataSourceProxy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

@SpringBootApplication
@EnableDiscoveryClient
@EnableAutoDataSourceProxy
public class TaxiGatewayApplication {

  public static void main(String[] args) {
    SpringApplication.run(TaxiGatewayApplication.class, args);
  }

  /**
   * 构建路由映射
   * <p>
   * <strong>注意：没有添加stripPrefix配置时，访问的代理服务器url是会将pattern中的内容追加到url头部的</strong>
   * <p>
   * 如果在配置文件中指定路由规则，则将这个bean注释掉
   *
   * @param builder 路由映射构造器
   * @return 路由映射信息对象
   */
  // @Bean
  public RouteLocator routeLocator(RouteLocatorBuilder builder) {
    return builder.routes()
      // 路由规则：所有符合/xxx/**的请求，都以负载均衡方式（lb, load balance）转发到注册中心的taxi-xxx服务上
      .route(predicate -> predicate.path("/order/**").uri("lb://taxi-order"))
      .route(predicate -> predicate.path("/driver/**").uri("lb://taxi-driver"))
      .route(predicate -> predicate.path("/user/**").uri("lb://taxi-user"))
      .build();
  }

}
