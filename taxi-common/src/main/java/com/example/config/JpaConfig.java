package com.example.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * project : taxi-test
 * <p>description:
 *
 * @author : consi
 * @since : 2022/9/21
 **/
@Configuration
@EntityScan("com.example.bean")
@EnableJpaRepositories("com.example.repository")
public class JpaConfig {
}
