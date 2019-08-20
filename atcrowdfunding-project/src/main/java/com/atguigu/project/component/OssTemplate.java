package com.atguigu.project.component;


import com.aliyun.oss.OSSClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


@Component
@Data
@ConfigurationProperties(prefix = "aliyun.oss")
public class OssTemplate {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

    public String upload(byte[] content,String fileName) throws FileNotFoundException {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        // 云账号 AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，
        //创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
        //自己创建一个子账号，添加阿里云oss的操作权限
        // 创建OSSClient实例。
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        fileName = UUID.randomUUID().toString().replace("-","")+"_"+fileName;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = dateFormat.format(new Date());
        //拼接指定的路径
        ossClient.putObject(bucketName, "pic/"+format+"/"+fileName, new ByteArrayInputStream(content));
        // 关闭OSSClient。
        ossClient.shutdown();

        //返回这个东西的访问地址；
        //https://atcrowdfunding-0222.oss-cn-shanghai.aliyuncs.com/pic/2019-06-18/bba97a58a028419a9e0fcfa7d58dce12_105711snlhrbuhu7sjqrr3.jpg
        String url = "https://"+bucketName+"."+endpoint.substring(7)+"/pic/"+format+"/"+fileName;
        return url;
    }
}
