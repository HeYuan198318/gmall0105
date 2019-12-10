package com.heyuan.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.heyuan.gmall.bean.UmsMember;
import com.heyuan.gmall.bean.UmsMemberReceiveAddress;
import com.heyuan.gmall.service.UserService;
import com.heyuan.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import com.heyuan.gmall.user.mapper.UserMapper;
import com.heyuan.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class UserServiceImpi implements UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;

    @Override
    public List<UmsMember> getAllUser() {
        List<UmsMember> umsMemberList = userMapper.selectAll();//userMapper.selectAllUser();

        return umsMemberList;
    }

    @Override
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId) {
        // 封装的参数对象 执行的sql语句 where aa=?
//        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
//        umsMemberReceiveAddress.setMemberId(memberId);
//        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = umsMemberReceiveAddressMapper.select(umsMemberReceiveAddress);


        //执行的sql语句 where (aa=?)
        Example example = new Example(UmsMemberReceiveAddress.class);
        example.createCriteria().andEqualTo("memberId",memberId);
        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = umsMemberReceiveAddressMapper.selectByExample(example);

        return umsMemberReceiveAddresses;
    }

    //验证登录
    @Override
    public UmsMember login(UmsMember umsMember) {
        Jedis jedis=null;
        try {
            //先从redis中获取
            jedis = redisUtil.getJedis();
            if (jedis!=null) {
                String umsMemberStr = jedis.get("user:" + umsMember.getUsername() + ":password");

                if (StringUtils.isNotBlank(umsMemberStr)) {
                    //密码正确
                    UmsMember umsMemberFromCache = JSON.parseObject(umsMemberStr, UmsMember.class);
                    return umsMemberFromCache;
                }
            }
                //连接redis失败，开启数据库
                UmsMember umsMemberFromDb=loginFromDb(umsMember);
                if (umsMemberFromDb!=null){
                    jedis.setex("user:" + umsMember.getUsername() + ":password",60*60*24,JSON.toJSONString(umsMemberFromDb));
                }
                return umsMemberFromDb;
        }finally {
           jedis.close();
        }
    }

    //将token存入redis中
    @Override
    public void addUserToken(String token, String memberId) {
        Jedis jedis=redisUtil.getJedis();

        jedis.setex("user:"+memberId+":token",60*60*24,token);
        jedis.close();
    }

    private UmsMember loginFromDb(UmsMember umsMember) {

        List<UmsMember> umsMembers=userMapper.select(umsMember);

        if (umsMembers!=null){
            return umsMembers.get(0);
        }
        return null;
    }
}