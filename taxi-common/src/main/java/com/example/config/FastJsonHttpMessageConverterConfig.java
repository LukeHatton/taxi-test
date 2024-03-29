package com.example.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

/**
 * project : taxi-test
 * <p>description:
 * json数据http消息转换器
 *
 * @author : consi
 * @since : 2022/7/12
 **/
@Configuration
public class FastJsonHttpMessageConverterConfig {

  @Bean
  public HttpMessageConverters fastJsonHttpMessageConverters() {
    //1.需要定义一个convert转换消息的对象;
    FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
    //2:添加fastJson的配置信息;
    FastJsonConfig fastJsonConfig = new FastJsonConfig();
    fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
    //3处理中文乱码问题
    List<MediaType> fastMediaTypes = new ArrayList<>();
    fastMediaTypes.add(MediaType.APPLICATION_JSON);
    //4.在convert中添加配置信息.
    fastJsonHttpMessageConverter.setSupportedMediaTypes(fastMediaTypes);
    fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
    return new HttpMessageConverters(fastJsonHttpMessageConverter);
  }
}
