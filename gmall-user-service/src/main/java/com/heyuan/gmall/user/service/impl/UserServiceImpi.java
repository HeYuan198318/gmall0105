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
                String umsMemberStr = jedis.get("user:" + umsMember.getPassword()+umsMember.getUsername() + ":info");

                if (StringUtils.isNotBlank(umsMemberStr)) {
                    //密码正确
                    UmsMember umsMemberFromCache = JSON.parseObject(umsMemberStr, UmsMember.class);
                    return umsMemberFromCache;
                }
            }
                //连接redis失败，开启数据库
                UmsMember umsMemberFromDb=loginFromDb(umsMember);
                if (umsMemberFromDb!=null){
                    jedis.setex("user:" + umsMember.getPassword()+umsMember.getUsername() + ":info",60*60*24, JSON.toJSONString(umsMemberFromDb));                }
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

    //添加微博登录的用户信息在数据库
    @Override
    public UmsMember addOauthUser(UmsMember umsMember) {
        userMapper.insertSelective(umsMember);
        return umsMember;
    }

    @Override
    public UmsMember checkOauthUser(UmsMember umsCheck) {
        UmsMember umsMember = userMapper.selectOne(umsCheck);
        return umsMember;
    }

    //获取收货用户对象
    @Override
    public UmsMemberReceiveAddress getReceiveAddressById(String receiveAddressId) {
        UmsMemberReceiveAddress umsMemberReceiveAddress=new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setId(receiveAddressId);
        UmsMemberReceiveAddress umsMemberReceiveAddress1=umsMemberReceiveAddressMapper.selectOne(umsMemberReceiveAddress);
        return umsMemberReceiveAddress1;
    }

    /**
     * 添加用户
     * @param umsMember
     */
    @Override
    public void addUser(UmsMember umsMember) {

        userMapper.insert(umsMember);
    }

    /**
     * 更新用户信息
     * @param umsMember
     */
    @Override
    public int update(UmsMember umsMember) {
        return userMapper.updateByPrimaryKeySelective(umsMember);
    }

    @Override
    public UmsMember getOneUser(String umsMemberId) {
        UmsMember umsMember=new UmsMember();
        umsMember.setId(umsMemberId);

        return userMapper.selectOne(umsMember);
    }

    /**
     * 添加地址
     */
    @Override
    public void addUseraddress(UmsMemberReceiveAddress umsMemberReceiveAddress) {
        umsMemberReceiveAddressMapper.insert(umsMemberReceiveAddress);
    }

    /**
     * 删除地址
     * @param userAddressId
     */
    @Override
    public int delUseraddress(String userAddressId) {
        UmsMemberReceiveAddress umsMemberReceiveAddress=new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setId(userAddressId);
        int i=umsMemberReceiveAddressMapper.deleteByPrimaryKey(umsMemberReceiveAddress);
        return i;
    }

    /**
     * 删除当前用户所有地址信息
     * @param userId
     */
    @Override
    public void delUserAlladdress(String userId) {
        UmsMemberReceiveAddress umsMemberReceiveAddress=new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setMemberId(userId);
        umsMemberReceiveAddressMapper.delete(umsMemberReceiveAddress);

    }

    /**
     * 删除当前用户信息
     * @param userId
     */
    @Override
    public void delUserInfo(String userId) {
        UmsMember umsMember=new UmsMember();
        umsMember.setId(userId);
        userMapper.deleteByPrimaryKey(umsMember);

    }

    private UmsMember loginFromDb(UmsMember umsMember) {

        List<UmsMember> umsMembers=userMapper.select(umsMember);
        if (umsMembers.size()!=0){
            return umsMembers.get(0);
        }
        else {
            return null;
        }
    }
}
