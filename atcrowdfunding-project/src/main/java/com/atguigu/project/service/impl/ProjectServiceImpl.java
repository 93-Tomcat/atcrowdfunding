package com.atguigu.project.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.front.bean.*;
import com.atguigu.front.constant.AppConstant;
import com.atguigu.front.enume.ImgTypeEnume;
import com.atguigu.front.enume.ProjectStatusEnume;
import com.atguigu.front.vo.resp.ProjectAllInfoVo;
import com.atguigu.project.dao.*;
import com.atguigu.project.service.ProjectService;
import com.atguigu.front.vo.ProjectRedisStorageVo;
import com.atguigu.front.vo.req.BaseVo;
import com.atguigu.front.vo.req.ProjectBaseInfoVo;
import com.atguigu.front.vo.req.ProjectReturnVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    TTagMapper tagMapper;

    @Autowired
    TTypeMapper typeMapper;

    @Autowired
    TProjectMapper projectMapper;

    @Autowired
    TProjectImagesMapper imagesMapper;

    @Autowired
    TProjectTagMapper projectTagMapper;

    @Autowired
    TProjectTypeMapper projectTypeMapper;

    @Autowired
    TReturnMapper returnMapper;

    @Override
    public String initProject(String accessToken) {

        String jsonString = redisTemplate.opsForValue().get(AppConstant.MEMBER_LOGIN_CACHE_PREFIX + accessToken);
        TMember member = JSON.parseObject(jsonString, TMember.class);
        String projectToken = UUID.randomUUID().toString().replace("-", "");

        if(member==null){
            return null;
        }

        /**
         *     private String accessToken;//访问令牌
         *     private String projectToken;//项目的临时token；项目的唯一标识
         *     private Integer memberid;//会员id
         */
        ProjectRedisStorageVo storageVo = new ProjectRedisStorageVo();
        storageVo.setAccessToken(accessToken);
        storageVo.setProjectToken(projectToken);
        storageVo.setMemberid(member.getId());

        //将临时项目的基本信息，保存到redis中；
        redisTemplate.opsForValue().set(AppConstant.PROJECT_TEMP_CACHE_PREFIX+projectToken,JSON.toJSONString(storageVo));


        return projectToken;
    }

    @Override
    public List<TTag> getSysTags() {

        return tagMapper.selectByExample(null);
    }

    @Override
    public List<TType> getSysTypes() {


        return typeMapper.selectByExample(null);
    }

    @Override
    public boolean saveTempBaseInfo(ProjectBaseInfoVo baseInfoVo) {
        //页面提交了第一屏的基本数据
        String projectToken = baseInfoVo.getProjectToken();
        //获取redis之前保存的项目的临时信息
        String s = redisTemplate.opsForValue().get(AppConstant.PROJECT_TEMP_CACHE_PREFIX + projectToken);
        ProjectRedisStorageVo storageVo = JSON.parseObject(s, ProjectRedisStorageVo.class);

        //增量复制新的vo的值到原来vo
        BeanUtils.copyProperties(baseInfoVo,storageVo);

        //把这个对象转为json存到redis中；
        String jsonString = JSON.toJSONString(storageVo);
        redisTemplate.opsForValue().set(AppConstant.PROJECT_TEMP_CACHE_PREFIX + projectToken,jsonString);

        return true;
    }

    @Override
    public boolean saveTempReturn(List<ProjectReturnVo> returns) {
        String projectToken = returns.get(0).getProjectToken();
        //临时保存redis
        String s = redisTemplate.opsForValue().get(AppConstant.PROJECT_TEMP_CACHE_PREFIX + projectToken);
        ProjectRedisStorageVo storageVo = JSON.parseObject(s, ProjectRedisStorageVo.class);

        List<TReturn> returnsList = new ArrayList<>();
        for (ProjectReturnVo aReturn : returns) {
            TReturn tReturn = new TReturn();
            BeanUtils.copyProperties(aReturn,tReturn);
            returnsList.add(tReturn);
        }
        storageVo.setProjectReturns(returnsList);

        //重新保存到redis中；
        String jsonString = JSON.toJSONString(storageVo);
        redisTemplate.opsForValue().set(AppConstant.PROJECT_TEMP_CACHE_PREFIX + projectToken,jsonString);

        return true;
    }

    /**
     * 保存
     * @param vo
     */
    @Override
    public void submitProjectToDb(BaseVo vo) {
        //保存需要审核的
        saveProject(vo,ProjectStatusEnume.SUBMIT_AUTH);

    }

    /**
     * 以前做事务；
     *  <tx:annotation-driven/>
     * @param vo
     * @param statusEnume
     */
    @Transactional
    public void saveProject(BaseVo vo,ProjectStatusEnume statusEnume) {
        String s = redisTemplate.opsForValue().get(AppConstant.PROJECT_TEMP_CACHE_PREFIX + vo.getProjectToken());
        ProjectRedisStorageVo storageVo = JSON.parseObject(s, ProjectRedisStorageVo.class);

        //保存到数据库；
        //memberid,name,remark,money,day:这几个redis有，直接拷贝

        //[supportmoney  supporter  completion follower】；计算来的
        //1、拿到项目的基本数据。保存到数据库
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TProject tProject = new TProject();
        BeanUtils.copyProperties(storageVo,tProject);
        //6、指定项目其他默认值
        //status：项目目前状态  deploydate(管理员将项目审核通过并发布的日期)：我们需要给默认值 createdate
        tProject.setStatus(statusEnume.getCode());
        tProject.setCreatedate(format.format(new Date()));

        //设置获取自增id；
        int i = projectMapper.insertSelective(tProject);
        //保存了项目以后要获取到项目的id；mybatis，获取到自增id；
        Integer projectId = tProject.getId();


        //2、保存项目图片的信息；t_project_images
        TProjectImages images = new TProjectImages();
        //2.1、保存头图
        String headerImage = storageVo.getHeaderImage();
        images.setImgurl(headerImage);
        images.setImgtype(ImgTypeEnume.HEADER_IMG.getCode());
        images.setProjectid(projectId);
        imagesMapper.insertSelective(images);

        //2.2、保存详情图
        List<String> detailsImage = storageVo.getDetailsImage();
        detailsImage.forEach((url)->{
            TProjectImages detailImage = new TProjectImages();
            detailImage.setImgurl(url);
            detailImage.setImgtype(ImgTypeEnume.DETAIL_IMG.getCode());
            detailImage.setProjectid(projectId);
            imagesMapper.insertSelective(detailImage);
        });


        //3、保存项目标签
        List<Integer> tagids = storageVo.getTagids();
        tagids.forEach((id)->{
            TProjectTag tag = new TProjectTag();
            tag.setProjectid(projectId);
            tag.setTagid(id);
            projectTagMapper.insertSelective(tag);
        });

        //4、保存项目分类
        List<Integer> typeids = storageVo.getTypeids();
        typeids.forEach((id)->{
            TProjectType type = new TProjectType();
            type.setProjectid(projectId);
            type.setTypeid(id);
            projectTypeMapper.insertSelective(type);
        });

        //5、保存项目的回报档位信息
        List<TReturn> projectReturns = storageVo.getProjectReturns();
        projectReturns.forEach((projectReturn)->{
            projectReturn.setProjectid(projectId);
            returnMapper.insertSelective(projectReturn);
        });

        //6、项目保存成功，将redis中之前的数据删除
        redisTemplate.delete(AppConstant.PROJECT_TEMP_CACHE_PREFIX+vo.getProjectToken());
    }

    /**
     * 保存草稿
     * @param vo
     */
    @Override
    public void tempProjectToDb(BaseVo vo) {
        //保存成草稿
        saveProject(vo,ProjectStatusEnume.DRAFT);
    }

    @Override
    public List<TProject> getAllProjects() {
        return projectMapper.selectByExample(null);
    }

    @Override
    public List<ProjectAllInfoVo> getAllProjectsInfos() {

        List<ProjectAllInfoVo> projectAllInfoVos = projectMapper.getAllProjectsInfos();
        return projectAllInfoVos;
    }

    @Override
    public TProject getProjectInfo(Integer id) {
        return projectMapper.selectByPrimaryKey(id);
    }

    @Override
    public TProjectImages getProjectHeaderImage(Integer id) {
        TProjectImagesExample example = new TProjectImagesExample();
        example.createCriteria().andProjectidEqualTo(id).andImgtypeEqualTo((byte)0);
        List<TProjectImages> tProjectImages = imagesMapper.selectByExample(example);
        return tProjectImages==null?null:tProjectImages.get(0);
    }

    @Override
    public List<TProjectImages> getProjectDetailImage(Integer id) {
        TProjectImagesExample example = new TProjectImagesExample();
        example.createCriteria().andProjectidEqualTo(id).andImgtypeEqualTo((byte)1);
        List<TProjectImages> tProjectImages = imagesMapper.selectByExample(example);
        return tProjectImages;
    }

    @Override
    public List<TReturn> getProjectAllReturns(Integer id) {
        TReturnExample example = new TReturnExample();
        example.createCriteria().andProjectidEqualTo(id);
        List<TReturn> returns = returnMapper.selectByExample(example);
        return returns;
    }
}
