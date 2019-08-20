package com.atguigu.project.dao;

import com.atguigu.front.bean.TProject;
import com.atguigu.front.bean.TProjectExample;
import java.util.List;

import com.atguigu.front.vo.resp.ProjectAllInfoVo;
import org.apache.ibatis.annotations.Param;

public interface TProjectMapper {
    long countByExample(TProjectExample example);

    int deleteByExample(TProjectExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(TProject record);

    int insertSelective(TProject record);

    List<TProject> selectByExample(TProjectExample example);

    TProject selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") TProject record, @Param("example") TProjectExample example);

    int updateByExample(@Param("record") TProject record, @Param("example") TProjectExample example);

    int updateByPrimaryKeySelective(TProject record);

    int updateByPrimaryKey(TProject record);

    List<ProjectAllInfoVo> getAllProjectsInfos();
}