package com.atguigu.front.vo.req;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@ToString
@Data
public class ProjectBaseInfoVo  extends BaseVo{

//    private String accessToken;//访问令牌
//    private String projectToken;//项目的临时token；项目的唯一标识

    private List<Integer> typeids; //项目的分类id
    private List<Integer> tagids; //项目的标签id


    private String name;//项目名称
    private String remark;//项目简介
    private Long money;//筹资金额
    private Integer day;//筹资天数
    private String headerImage;//项目头部图片
    private List<String> detailsImage;//项目详情图片
}
