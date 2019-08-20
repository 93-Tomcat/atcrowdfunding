package com.atguigu.atcrowdfunding.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
 * 静态资源；
 * Sb觉得：
 *      "classpath:/META-INF/resources/", "classpath:/resources/",
 * 	    "classpath:/static/", "classpath:/public/" ;
 *
 */
@EnableFeignClients
@EnableDiscoveryClient //服务发现功能
@SpringBootApplication
public class AtcrowdfundingAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(AtcrowdfundingAppApplication.class, args);
    }

}
