package com.atguigu.project;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * 加事务；
 * 1）、@EnableTransactionManagement ；开启事务管理功能
 * 2）、给方法上加@Transaction；
 */
@EnableCircuitBreaker //开启断路保护
@EnableFeignClients  //开启feign的声明式远程调用功能
@EnableDiscoveryClient //开始服务的注册发现功能
@MapperScan("com.atguigu.project.dao")
@EnableTransactionManagement //开启基于注解的事务管理功能；
@SpringBootApplication
public class AtcrowdfundingProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(AtcrowdfundingProjectApplication.class, args);
    }

}
