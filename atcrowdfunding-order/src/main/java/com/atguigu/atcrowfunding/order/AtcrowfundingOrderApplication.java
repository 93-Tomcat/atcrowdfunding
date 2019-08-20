package com.atguigu.atcrowfunding.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@EnableCircuitBreaker
@EnableFeignClients
@EnableDiscoveryClient
@MapperScan("com.atguigu.atcrowfunding.order.dao")
@SpringBootApplication
public class AtcrowfundingOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(AtcrowfundingOrderApplication.class, args);
    }

}
