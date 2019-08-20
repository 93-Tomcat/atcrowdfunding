package com.atguigu.atcrowdfunding.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.atcrowdfunding.user.dao.TMemberAddressMapper;
import com.atguigu.atcrowdfunding.user.dao.TMemberMapper;
import com.atguigu.atcrowdfunding.user.service.MemberService;
import com.atguigu.front.bean.TMemberAddress;
import com.atguigu.front.bean.TMemberAddressExample;
import com.atguigu.front.vo.req.MemberRegisterVo;
import com.atguigu.front.bean.TMember;
import com.atguigu.front.bean.TMemberExample;
import com.atguigu.front.constant.AppConstant;
import com.atguigu.front.enume.AuthStatusEnume;
import com.atguigu.front.enume.UserTypeEnume;
import com.atguigu.front.exception.UserEmailException;
import com.atguigu.front.exception.UserLoginacctException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberServiceImpl implements MemberService {

    /**
     * 调低autowired的报错级别
     */
    @Autowired
    TMemberMapper memberMapper;

    @Autowired
    TMemberAddressMapper memberAddressMapper;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public void regist(MemberRegisterVo memberRegisterVo) {

        //1、检查手机号是否被占用
        String mobile = memberRegisterVo.getMobile();
        String email = memberRegisterVo.getEmail();
        //true代表可以用
        boolean b = checkMobile(mobile);
        if(!b){
            throw new UserLoginacctException();
        }
        //2、检查邮箱是否被占用
        boolean b1 = checkEmail(email);
        if(!b1){
            throw new UserEmailException();
        }

        //3、将会员信息保存
        TMember member = new TMember();
        member.setLoginacct(mobile);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode(memberRegisterVo.getPassword());
        member.setUserpswd(encode);
        member.setUsername(mobile);
        member.setEmail(memberRegisterVo.getEmail());

        //实名认证状态 0 - 未实名认证， 1 - 实名认证申请中， 2 - 已实名认证
        member.setAuthstatus(AuthStatusEnume.UNAUTH.getCode());
        // 用户类型: 0 - 个人， 1 - 企业
        member.setUsertype(UserTypeEnume.PERSONAL.getCode());
        member.setRealname("未命名");

        //账户类型: 0 - 企业， 1 - 个体， 2 - 个人， 3 - 政府
        memberMapper.insertSelective(member);


    }

    @Override
    public TMember login(String loginacct, String password) {
        TMemberExample example = new TMemberExample();
        example.createCriteria().andLoginacctEqualTo(loginacct);
        List<TMember> tMembers = memberMapper.selectByExample(example);
        
       
        if(tMembers!=null&&tMembers.size()==1){
            //密码是否匹配
            TMember member = tMembers.get(0);
            //$2a$10$/Q8t1GOZGMbTLvUHml7KEO3h49dJf6HK6r.K8LzJHynn/Tq2Tk1nC
            String userpswd = member.getUserpswd();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            boolean matches = passwordEncoder.matches(password, userpswd);
            if(!matches){
                return null;
            }else {
                return member;
            }
        }else {
            return null;
        }
        
    }

    @Override
    public TMember getMemberInfo(String accessToken) {

        String jsonString = redisTemplate.opsForValue().get(AppConstant.MEMBER_LOGIN_CACHE_PREFIX + accessToken);
        TMember member = JSON.parseObject(jsonString, TMember.class);
        //去数据库实时查
        TMember member1 = memberMapper.selectByPrimaryKey(member.getId());

        return member1;
    }

    @Override
    public List<TMemberAddress> getMemberAddress(Integer id) {
        TMemberAddressExample example = new TMemberAddressExample();
        example.createCriteria().andMemberidEqualTo(id);
        return memberAddressMapper.selectByExample(example);
    }

    @Override
    public TMemberAddress addAddress(String accessToken, String address) {
        TMemberAddress memberAddress = new TMemberAddress();


        String jsonString = redisTemplate.opsForValue().get(AppConstant.MEMBER_LOGIN_CACHE_PREFIX + accessToken);
        TMember member = JSON.parseObject(jsonString, TMember.class);

        memberAddress.setAddress(address);
        memberAddress.setMemberid(member.getId());
        memberAddressMapper.insertSelective(memberAddress);
        return memberAddress;
    }

    private boolean checkEmail(String email) {
        TMemberExample example = new TMemberExample();
        example.createCriteria().andEmailEqualTo(email);
        long l = memberMapper.countByExample(example);
        return l==0?true:false;
    }

    private boolean checkMobile(String mobile) {
        TMemberExample example = new TMemberExample();
        example.createCriteria().andLoginacctEqualTo(mobile);
        long l = memberMapper.countByExample(example);
        return l==0?true:false;
    }
}
