package com.atguigu.project.service;

import com.atguigu.front.bean.*;
import com.atguigu.front.vo.req.BaseVo;
import com.atguigu.front.vo.req.ProjectBaseInfoVo;
import com.atguigu.front.vo.req.ProjectReturnVo;
import com.atguigu.front.vo.resp.ProjectAllInfoVo;

import java.util.List;

public interface ProjectService {

    /**
     * 返回刚创建的项目的临时token
     * @param accessToken
     * @return
     */
    String initProject(String accessToken);

    List<TTag> getSysTags();

    List<TType> getSysTypes();

    boolean saveTempBaseInfo(ProjectBaseInfoVo baseInfoVo);

    boolean saveTempReturn(List<ProjectReturnVo> returns);

    void submitProjectToDb(BaseVo vo);

    void tempProjectToDb(BaseVo vo);

    List<TProject> getAllProjects();

    List<ProjectAllInfoVo> getAllProjectsInfos();

    TProject getProjectInfo(Integer id);

    TProjectImages getProjectHeaderImage(Integer id);

    List<TProjectImages> getProjectDetailImage(Integer id);

    List<TReturn> getProjectAllReturns(Integer id);

}
