package com.heyuan.gmall.service;

import com.heyuan.gmall.bean.UmsMember;
import com.heyuan.gmall.bean.UmsMemberReceiveAddress;
import java.util.List;

public interface UserService {
    //查询所有用户
    List<UmsMember> getAllUser();
    //获取用户地址
    List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId);


    UmsMember login(UmsMember umsMember);

    void addUserToken(String token, String memberId);

    UmsMember addOauthUser(UmsMember umsMember);

    UmsMember checkOauthUser(UmsMember umsCheck);

    UmsMemberReceiveAddress getReceiveAddressById(String receiveAddressId);

    void addUser(UmsMember umsMember);

    int update(UmsMember umsMember);

    UmsMember getOneUser(String umsMemberId);

    void addUseraddress(UmsMemberReceiveAddress umsMemberReceiveAddress);

    int delUseraddress(String userAddressId);

    void delUserAlladdress(String userId);

    void delUserInfo(String userId);
}
