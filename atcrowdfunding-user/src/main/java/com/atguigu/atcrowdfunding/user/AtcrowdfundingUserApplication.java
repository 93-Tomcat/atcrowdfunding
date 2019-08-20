package com.atguigu.atcrowdfunding.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * java代码发送http请求；
 * 1、RestTemplate：spring自带的rest请求发送工具；
 * 2、HttpComponents；
 */

/**
 * 1、编写复杂的javaBean。getter/setter
 * 2、重写javaBean的hashCode；
 * 3、我们想要用日志;Logger logger = LoggerFactory.getLogger(this.getClass());
 * 对于解决以上麻烦问题的；
 * lombok；
 * 使用；
 * 1）、安装lombok插件
 * 2）、导入lombok依赖
 * 3）、常用注解
 *      @Data：自动生成javaBean的getter/setter
 *      @Slf4j：自动生成log变量；
 *
 *
 *
 *  根据SpringBoot的原理；
 *  1）、导入了哪些场景，这个场景的自动配置就生效。
 *  2）、我们现在自动配置数据源的场景生效；
 *  3）、我们不想让某个自动配置生效；
 *
 *  SpringBoot默认使用的连接池是：HikariCP；
 *
 *  ==使用SpringBoot自动的热启动功能；（不重启服务器重新加载项目）
 *  1）、导入dev-tools
 *  2）、写完代码，页面，配置；ctrl+f9(把项目编译一下)
 *
 *  ==使用SpringBoot自带的redis操作；
 *  1）、导入redisStarter；
 *  2）、配置spring.redis开头的属性;默认只需要指定redis.host；
 *
 */

//1）、开启从注册中心中注册发现服务的功能  @EnableDiscoveryClien
//2）、开启基于Feign的声明式远程调用  @EnableFeignClients
//3）、开启断路器功能  @EnableCircuitBreaker
@ServletComponentScan(basePackages = "com.atguigu.atcrowdfunding.user.component")      //自动扫描servlet的组件（Filter、Listener、Servlet）
@EnableCircuitBreaker
@EnableFeignClients
@EnableDiscoveryClient
@EnableSwagger2
@MapperScan("com.atguigu.atcrowdfunding.user.dao")
@SpringBootApplication
public class AtcrowdfundingUserApplication {


    public static void main(String[] args) {
        SpringApplication.run(AtcrowdfundingUserApplication.class, args);
    }

}
