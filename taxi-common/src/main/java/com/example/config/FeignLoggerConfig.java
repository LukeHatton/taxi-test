package com.example.config;

import feign.Logger;
import feign.Logger.Level;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * project : taxi-test
 * <p>description:
 * 配置OpenFeign的日志输出级别。需要配合客户端的配置才能生效，例如
 * <pre>{@code
 *    # 将对应包下的客户端的日志输出级别配置为debug
 *    logging.level.[feign client所在的包]=debug
 * }</pre>
 *
 * @author : consi
 * @since : 2022/7/12
 **/
@Configuration
public class FeignLoggerConfig {

  @Bean
  public Logger.Level feignLoggerLevel(){
    return Level.FULL;
  }
}
