package com.atguigu.atcrowdfunding.user.component;

import com.atguigu.front.constant.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Servlet3.0以后  servlet,filter,listener都有注解
 * 一定要让Spring扫描到；
 * @ServletComponentScan
 */
@Slf4j
@WebFilter(urlPatterns = "/*")
public class LoginKeyExpireFilter implements Filter {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        log.debug("LoginKeyExpireFilter进行拦截，对登录信息进行自动续期；是否能redisTemplate{}",redisTemplate);
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String accessToken = request.getParameter("accessToken");
        if(StringUtils.isEmpty(accessToken)){
            //非登录后发送的请求，不用进行任何操作;
            filterChain.doFilter(request,servletResponse);
        }else {
            redisTemplate.expire(AppConstant.MEMBER_LOGIN_CACHE_PREFIX+accessToken,30, TimeUnit.MINUTES);
            filterChain.doFilter(request,servletResponse);
        }


    }

    @Override
    public void destroy() {

    }
}
