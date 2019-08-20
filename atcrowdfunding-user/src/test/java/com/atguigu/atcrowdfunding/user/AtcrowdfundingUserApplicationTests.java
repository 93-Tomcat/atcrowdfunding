package com.atguigu.atcrowdfunding.user;

import com.atguigu.atcrowdfunding.user.component.SmsTemplate;
import com.atguigu.atcrowdfunding.user.dao.TMemberMapper;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class AtcrowdfundingUserApplicationTests {

    @Autowired
    SmsTemplate smsTemplate;
    @Autowired
    TMemberMapper memberMapper;


    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Test
    public void test01(){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode("123456");
        //$2a$10$/K5G2oGKu6dj0f3bfo.38Oebf17vuCQUecJ/pqj0p4UYPPy7L/70m
        //$2a$10$1XXFiEu5S/ZZ5CO2cgNpaOW5lHetdtT1dWjso45XmlJBiLyJCxTjG

        //随机数
        // $2a$10$10/djsaljdsakljdlasjdlakjdklajdalk

        System.out.println(encode);
    }

    @Test
    public void stringRedisTemplate(){
        /**
         * redis中存所有东西都是k-v；
         *
         * Redis value的5大数据类型；
         * string：基本值
         * list：数组
         * set：集合
         * hash：k-v
         * zset：带排序的集合
         *
         */
        //stringRedisTemplate.

        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();

//        ops.append("msg"," world");
//        System.out.println("redis操作完成...");

        String msg = ops.get("msg");
        System.out.println(msg);


    }

    @Test
    public void testmemberMapper(){

//        long l = memberMapper.countByExample(null);
//        System.out.println("总记录树："+l);
//
//        JedisPool jedisPool = new JedisPool();
//        Jedis jedis = jedisPool.getResource();
//        jedis.set("a","b");
    }


    @Test
    public void testSendSms(){
        boolean b = smsTemplate.sendCodeSms("17752933338", "7777");
        System.out.println("短信发送；"+b);

    }




    @Test
    public void contextLoads() throws IOException {

        //1、HttpClients创建一个可以发送请求的客户端
        //Collection==Collections(往往加s的是工具类)
        CloseableHttpClient httpclient = HttpClients.createDefault();

        //2、创建一个HttpXXX我men将要发送的请求；
        HttpGet httpGet = new HttpGet("http://www.baidu.com");

        //3、执行请求，收取响应内容
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        try {

            //4、获取响应内容---状态行
            System.out.println(response1.getStatusLine());

            //4.1）、获取响应出来的内容--真正的内容
            HttpEntity entity1 = response1.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            String s = EntityUtils.toString(entity1);
            System.out.println("收到响应："+s);

        } finally {
            //5、关闭连接
            response1.close();
        }

//        HttpPost httpPost = new HttpPost("http://targethost/login");
//        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
//        nvps.add(new BasicNameValuePair("username", "vip"));
//        nvps.add(new BasicNameValuePair("password", "secret"));
//        httpPost.setEntity(new UrlEncodedFormEntity(nvps));
//        CloseableHttpResponse response2 = httpclient.execute(httpPost);
//
//        try {
//            System.out.println(response2.getStatusLine());
//            HttpEntity entity2 = response2.getEntity();
//            // do something useful with the response body
//            // and ensure it is fully consumed
//            EntityUtils.consume(entity2);
//        } finally {
//            response2.close();
//        }
    }

    @Test
    public void post() throws Exception {
        //1、创建一个HttpClient客户端
        CloseableHttpClient httpClient = HttpClients.createDefault();

        //2、创建一个post请求
        HttpPost httpPost = new HttpPost("http://dingxin.market.alicloudapi.com/dx/sendSms");
        //2.1）、post传递参数\
        //尊敬的会员，你的验证码是${code}，本次交易${amount}
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("mobile", "17681281200"));
        nvps.add(new BasicNameValuePair("param", "code:5555"));
        nvps.add(new BasicNameValuePair("tpl_id", "TP1711063"));
        //设置请求参数
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, Charset.forName("UTF-8")));
        //设置请求头
        httpPost.setHeader("Authorization","APPCODE 93b7e19861a24c519a7548b17dc16d75");

        //3、发送请求
        CloseableHttpResponse response = httpClient.execute(httpPost);
        //4、获取响应信息
        HttpEntity content = response.getEntity();
        StatusLine statusLine = response.getStatusLine();
        System.out.println("响应状态行："+statusLine);
        System.out.println("响应的内容："+EntityUtils.toString(content));

        //5、关闭响应
        response.close();
    }

}
