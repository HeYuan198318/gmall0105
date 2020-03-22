package com.heyuan.gmall.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.heyuan.gmall.annotations.LoginRequired;
import com.heyuan.gmall.bean.UmsMember;
import com.heyuan.gmall.bean.UmsMemberReceiveAddress;
import com.heyuan.gmall.service.UserService;
import com.heyuan.gmall.user.util.UmsUploadUtil;
import io.jsonwebtoken.impl.Base64UrlCodec;
import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {

    public String nickname="";
    public String umsMemberId="";
    @Reference
    UserService userService;

    /**
     * 通过用户ID获取返回地址
     * @param memberId
     * @return
     */
    @RequestMapping("getReceiveAddressByMemberId")
    @ResponseBody
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId){
        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = userService.getReceiveAddressByMemberId(memberId);
        return umsMemberReceiveAddresses;
    }


    @RequestMapping("getAllUser")
    @ResponseBody
    public List<UmsMember> getAllUser(){
        List<UmsMember> umsMembers = userService.getAllUser();
        return umsMembers;
    }
    @RequestMapping("fileUpload")
    @ResponseBody
    public String fileUpload(@RequestParam("file") MultipartFile multipartFile) throws IOException, MyException {
        // 将图片或者音视频上传到分布式的文件存储系统
        // 将图片的存储路径返回给页面
        String imgUrl = UmsUploadUtil.uploadImage(multipartFile);
        System.out.println(imgUrl);
        return imgUrl;
    }

    @RequestMapping("home-setting-info.html")
    @LoginRequired(loginSuccess = true)
    public String index(String token, ModelMap modelMap){
        //解析token,获取用户数据
        //解析私有部分
        if (token!=null) {
            String tokenUserInfo = StringUtils.substringBetween(token, ".");
            Base64UrlCodec base64UrlCodec = new Base64UrlCodec();
            byte[] tokenBytes = base64UrlCodec.decode(tokenUserInfo);
            String tokenJson = null;
            try {
                tokenJson = new String(tokenBytes, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Map map1 = JSON.parseObject(tokenJson, Map.class);
            nickname=map1.get("nickname").toString();
            umsMemberId=map1.get("memberId").toString();
            UmsMember umsMember=userService.getOneUser(umsMemberId);

            modelMap.put("nickname", umsMember.getNickname());
            return "home-setting-info";
        }
        //从数据库获取数据
        modelMap.put("nickname", nickname);
        return "home-setting-info";
    }

    @RequestMapping("updateinfo")
    @LoginRequired(loginSuccess = true)
    public String update(UmsMember umsMember){
        umsMember.setId(umsMemberId);
        int i=userService.update(umsMember);
        if (i!=0){
            return "redirect:http://127.0.0.1:8080/home-setting-info.html";
        }
        return "redirect:http://127.0.0.1:8080/home-setting-info.html";
    }
    //收藏
    @RequestMapping("home-person-collect.html")
    @LoginRequired(loginSuccess = true)
    public String collect(){
        return "home-person-collect";
    }
    //足迹
    @RequestMapping("home-person-footmark.html")
    @LoginRequired(loginSuccess = true)
    public String footmark(){
        return "home-person-footmark";
    }
    //个人信息
    //地址
    @RequestMapping("home-setting-address.html")
    @LoginRequired(loginSuccess = true)
    public String address(ModelMap modelMap,String token){
        modelMap.put("nickname", nickname);
        List<UmsMemberReceiveAddress> umsMemberReceiveAddressList=getReceiveAddressByMemberId(umsMemberId);
        modelMap.put("UserReceiveAddresList",umsMemberReceiveAddressList);
        return "home-setting-address";
    }
    @RequestMapping("home-setting-safe.html")
    @LoginRequired(loginSuccess = true)
    public String safe(ModelMap modelMap){
        modelMap.put("nickname", nickname);
        return "home-setting-safe";
    }

    @RequestMapping("addUseraddress")
    @LoginRequired(loginSuccess = true)
    public String addUseraddress(UmsMemberReceiveAddress umsMemberReceiveAddress){
        umsMemberReceiveAddress.setMemberId(umsMemberId);
        userService.addUseraddress(umsMemberReceiveAddress);
        return "redirect:http://127.0.0.1:8080/home-setting-address.html";
    }
    /**
     * 删除当前地址
     */
    @RequestMapping("delUseraddress")
    @LoginRequired(loginSuccess = true)
    @ResponseBody
    public String delUseraddress(String userAddressId){
        int i=userService.delUseraddress(userAddressId);
        if (i!=0) {
            return "success";
        }else {return "fail";}
    }
}
