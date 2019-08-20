package com.atguigu.project.controller;

import com.atguigu.front.common.AppResponse;
import com.atguigu.front.constant.AppConstant;
import com.atguigu.project.component.OssTemplate;
import com.atguigu.project.service.ProjectService;
import com.atguigu.front.vo.req.BaseVo;
import com.atguigu.front.vo.req.ProjectBaseInfoVo;
import com.atguigu.front.vo.req.ProjectReturnVo;
import com.atguigu.front.vo.resp.ProjectTempVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "项目发起模块")
@RequestMapping("/project/create")
@RestController
@Slf4j
public class ProjectCreateController {

    @Autowired
    ProjectService projectService;

    @Autowired
    OssTemplate ossTemplate;

    @Autowired
    StringRedisTemplate redisTemplate;
    @ApiOperation("确认项目法人信息")
    @PostMapping("/confirm/legal")
    public AppResponse<String> confirmLegal(){
        return null;
    }



    @ApiOperation("项目创建第1步-项目初始创建")
    @PostMapping("/init")
    public AppResponse<ProjectTempVo> init(@RequestParam("accessToken") String accessToken){
        //点击阅读并同意协议，新创一个临时项目；生成一个项目的临时Token；


        //返回刚创建的项目的临时令牌
        String token = projectService.initProject(accessToken);
        if(StringUtils.isEmpty(token)){
            //没登录
            return AppResponse.fail().msg("项目创建异常，请确认登录状态");
        }


        ProjectTempVo projectTempVo = new ProjectTempVo();
        projectTempVo.setProjectToken(token);
        return AppResponse.success(projectTempVo);
    }
    @ApiOperation("项目创建第2步-项目基本信息保存")
    @PostMapping("/savebaseinfo")
    public AppResponse<String> saveBaseInfo(@RequestBody ProjectBaseInfoVo baseInfoVo){

        boolean b = projectService.saveTempBaseInfo(baseInfoVo);
        return AppResponse.success("");
    }


    /**
     * List<TReturn> SpringMVC默认没办法接受；
     *
     *  <input name="returns[0].supportmoney" value="'/>
     *  <input name="returns[0].content" value="'/>
     *  <input name="returns[0].freight" value="'/>
     *  <input name="returns[1].supportmoney"/>
     *  <input name="returns[2].supportmoney"/>
     *  <input name="returns[3]"/>
     *  <input name="returns[4]"/>
     *
     *  //SpringMVC；
     *  1、可以返回的对象写成json字符串
     *  2、可以将提交的json字符串逆转为对象；放在请求体中
     *      [{id:1},{id:2}]
     *  3、提交数据的时候，必须Post提交，提交的数据必须json；
     *        // $.ajax("return",json)
     *        $.post("return",json)；默认使用 application/x-www-form-urlencoded;k=v&k=v；
     *        $.ajax({
     *            url:"return",
     *            data:jsonStr,//k=v&k=v
     *            contentType:false,
     *            processData:false,
     *            success:function(data){
     *
     *            },
     *            error:function(){
     *
     *            }
     *        })
     *
     *
     * SpringMVC方法签名；@RequestBody List<TReturn> returns；
     *  请求体默认就应该是json字符串。
     * @param
     * @return
     */
    @ApiOperation("项目创建第3步-添加项目回报档位")
    @PostMapping("/return")
    public AppResponse<String> addReturn(@RequestBody List<ProjectReturnVo> returns){

        log.debug("提交来的对象：{}",returns);
        boolean b = projectService.saveTempReturn(returns);

        return AppResponse.success("成功").msg("数据保存成功");
    }

    @ApiOperation("删除项目回报档位")
    @DeleteMapping("/return")
    public AppResponse<String> deleteReturn(){
        return null;
    }


    @ApiOperation("项目提交审核申请")
    @PostMapping("/submit")
    public AppResponse<String> submit(@RequestBody BaseVo vo){

        projectService.submitProjectToDb(vo);
        return AppResponse.success("");
    }

    @ApiOperation("项目草稿保存")
    @PostMapping("/tempsave")
    public AppResponse<String> tempsave(BaseVo vo){
        projectService.tempProjectToDb(vo);
        return AppResponse.success("");
    }


    /**
     * 浏览器想要文件上传的几个要素；
     *    <form method="post" enctype="multipart/form-data">
     *         <input type="file" name="file" multiple>
     *
     *             <input type="file" name="header" >
     *             <input type="file" name="header" >
     *     </form>
     * 服务器：
     *
     * @param file
     * @return
     */
    @ApiOperation("项目图片上传")
    @PostMapping("/update")
    public AppResponse<List<String>> update(@RequestParam("file") MultipartFile[] file,
                                            @RequestParam("accessToken") String accessToken) throws IOException {

        Boolean hasKey = redisTemplate.hasKey(AppConstant.MEMBER_LOGIN_CACHE_PREFIX + accessToken);
        if(!hasKey){
            return AppResponse.fail().msg("请先登录");
        }

        List<String> urls = new ArrayList<>();

        if(file!=null){
            for (MultipartFile multipartFile : file) {
                //判断文件不为null
                if(!multipartFile.isEmpty()){
                    byte[] bytes = multipartFile.getBytes();
                    String filename = multipartFile.getOriginalFilename();
                    try {
                        String upload = ossTemplate.upload(bytes, filename);
                        urls.add(upload);
                    } catch (FileNotFoundException e) {

                    }
                }
            }
        }
        return AppResponse.success(urls);
    }
}
