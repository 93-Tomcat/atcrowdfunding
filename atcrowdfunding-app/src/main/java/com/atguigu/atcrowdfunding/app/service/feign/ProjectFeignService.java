package com.atguigu.atcrowdfunding.app.service.feign;

import com.atguigu.front.bean.TProject;
import com.atguigu.front.bean.TTag;
import com.atguigu.front.bean.TType;
import com.atguigu.front.common.AppResponse;
import com.atguigu.front.vo.req.BaseVo;
import com.atguigu.front.vo.req.ProjectBaseInfoVo;
import com.atguigu.front.vo.req.ProjectReturnVo;
import com.atguigu.front.vo.resp.ProjectAllAllInfoVo;
import com.atguigu.front.vo.resp.ProjectAllInfoVo;
import com.atguigu.front.vo.resp.ProjectTempVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/project")
@FeignClient("ATCROWDFUNDING-PROJECT")
public interface ProjectFeignService {


    @PostMapping("/create/init")
    public AppResponse<ProjectTempVo> init(@RequestParam("accessToken") String accessToken);

    @GetMapping("/sys/tags")
    public AppResponse<List<TTag>> sysTags();

    @GetMapping("/sys/type")
    public AppResponse<List<TType>> sysType();

    @PostMapping("/create/savebaseinfo")
    public AppResponse<String> saveBaseInfo(@RequestBody ProjectBaseInfoVo baseInfoVo);

    @PostMapping("/create/return")
    public AppResponse<String> addReturn(@RequestBody List<ProjectReturnVo> returns);

    @PostMapping("/create/submit")
    public AppResponse<String> submit(@RequestBody BaseVo vo);

    @GetMapping("/all/index")
    public AppResponse<List<ProjectAllInfoVo>> getAllIndex();

    @GetMapping("/info/detail/{id}")
    public AppResponse<ProjectAllAllInfoVo> getDetail(@PathVariable("id") Integer id);

}
