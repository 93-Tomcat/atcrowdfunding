package com.atguigu.project;

import com.aliyun.oss.OSSClient;
import com.atguigu.project.component.OssTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AtcrowdfundingProjectApplicationTests {

    @Autowired
    OssTemplate ossTemplate;

    @Test
    public void testossTemplate() throws IOException {
        FileInputStream inputStream = new FileInputStream("C:\\Users\\lfy\\Pictures\\Camera Roll\\105711snlhrbuhu7sjqrr3.jpg");

        byte[] bytes = new byte[inputStream.available()];

        int read = inputStream.read(bytes);

        String upload = ossTemplate.upload(bytes, "105711snlhrbuhu7sjqrr3.jpg");
        System.out.println("上传完成..."+upload);
    }



    @Test
    public void contextLoads() throws IOException {

        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = "http://oss-cn-shanghai.aliyuncs.com";
        // 云账号 AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，
        //创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
        //自己创建一个子账号，添加阿里云oss的操作权限
        String accessKeyId = "LTAIWLqMP8oS6WIr";
        String accessKeySecret = "4TGV3M6A9aEjCND5BfZBv0h7SYkl0Q";


        // 创建OSSClient实例。
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);

        // 上传文件流。
        InputStream inputStream = new FileInputStream("C:\\Users\\lfy\\Pictures\\Camera Roll\\153_141031152509_1.jpg");

        byte[] content = new byte[inputStream.available()];
        int read = inputStream.read(content);
        //
        ossClient.putObject("atcrowdfunding-0222", "153_141031152509_1.jpg", new ByteArrayInputStream(content));

        // 关闭OSSClient。
        ossClient.shutdown();
        System.out.println("上成功/....");
    }

}
