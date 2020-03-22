package com.heyuan.gmall.user.service.impl;

import com.heyuan.gmall.service.UserService;
import com.heyuan.gmall.bean.UmsMember;
import com.heyuan.gmall.bean.UmsMemberReceiveAddress;
import com.heyuan.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import com.heyuan.gmall.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;


//user-service层
@Service
public class UserServiceImpi implements UserService {

    @Autowired
    UserMapper userMapper;

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

    @Override
    public UmsMember login(UmsMember umsMember) {
        return null;
    }

    @Override
    public void addUserToken(String token, String memberId) {

    }

    @Override
    public UmsMember addOauthUser(UmsMember umsMember) {
        return null;
    }

    @Override
    public UmsMember checkOauthUser(UmsMember umsCheck) {
        return null;
    }

    @Override
    public UmsMemberReceiveAddress getReceiveAddressById(String receiveAddressId) {
        return null;
    }

    @Override
    public void addUser(UmsMember umsMember) {

    }

    @Override
    public int update(UmsMember umsMember) {
        return 0;
    }

    @Override
    public UmsMember getOneUser(String umsMemberId) {
        return null;
    }

    @Override
    public void addUseraddress(UmsMemberReceiveAddress umsMemberReceiveAddress) {

    }

    @Override
    public int delUseraddress(String userAddressId) {
        return 0;
    }

    @Override
    public void delUserAlladdress(String userId) {

    }

    @Override
    public void delUserInfo(String userId) {

    }
}
