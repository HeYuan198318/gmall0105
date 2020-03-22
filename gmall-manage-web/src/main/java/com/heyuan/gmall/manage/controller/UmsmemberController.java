package com.heyuan.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.heyuan.gmall.bean.UmsMember;
import com.heyuan.gmall.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@CrossOrigin
public class UmsmemberController {

    @Reference
    UserService userService;

    /**
     * 获取所有用户信息
     * @return
     */
    @RequestMapping("getUserInfoList")
    @ResponseBody
    public List<UmsMember> getUserInfoList() {
        List<UmsMember> umsMembers= userService.getAllUser();
        List<UmsMember> umsMembers1=new ArrayList<>();
        for (UmsMember umsMember:umsMembers) {
            if (umsMember.getGender()!=null) {
                if (umsMember.getGender().equals("0")) {
                    umsMember.setGender("未知");
                } else if (umsMember.getGender().equals("1")) {
                    umsMember.setGender("男");
                } else if (umsMember.getGender().equals("2")) {
                    umsMember.setGender("女");
                }
            }
            umsMembers1.add(umsMember);
        }
        return umsMembers1;
    }

    /**
     * 删除当前用户信息
     */
    @RequestMapping("deleteUser")
    @ResponseBody
    public String deleteUser(String userId) {

       userService.delUserInfo(userId);
       //删除对应地址
        userService.delUserAlladdress(userId);
        return "success";
    }
}
