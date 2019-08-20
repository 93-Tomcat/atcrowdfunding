package com.atguigu.atcrowdfunding.user.component;

import com.atguigu.atcrowdfunding.user.utils.HttpUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 发送短信Template
 */
@ConfigurationProperties(prefix = "aliyun.sms")
@Component
@Data
@Slf4j
public class SmsTemplate {

    private String host;
    private String path;
    private String method;
    private String appcode;

    //发送短信
    public boolean sendCodeSms(String mobile,String code){
        log.debug("短信的配置信息：host={}，path={}。method={}。appcode={}",host,path,method,appcode);

        log.debug("给手机号：{}；发送了一个【{}】验证码",mobile,code);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", mobile);
        querys.put("param", "code:"+code);
        querys.put("tpl_id", "TP1711063");
        Map<String, String> bodys = new HashMap<String, String>();

        try {

            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode == 200){
                //短信发送成功
                return true;
            }

        } catch (Exception e) {
            log.error("短信发送出现问题；手机号{}，验证码{}，异常原因{}",mobile,code,e.getMessage());
        }
        return  false;
    }
}
