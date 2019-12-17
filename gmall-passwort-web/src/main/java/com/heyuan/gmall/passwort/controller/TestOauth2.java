package com.heyuan.gmall.passwort.controller;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.util.HttpclientUtil;

import java.util.HashMap;
import java.util.Map;

public class TestOauth2 {

    public static String getCode(){

        // 1 获得授权码
        // 1590848947
        // http://passport.gmall.com:8085/lvlogin

        String s1 = HttpclientUtil.doGet("https://api.weibo.com/oauth2/authorize?client_id=1590848947&response_type=code&redirect_uri=http://password.gmall.com:8086/lvlogin");

        System.out.println(s1);

        // 在第一步和第二部返回回调地址之间,有一个用户操作授权的过程

        // 2 返回授权码到回调地址
         String s2="http://password.gmall.com:8085/vlogin?code=9646f7b62cf7dd98934020f0df41afa4";

        return null;
    }

    public static String getAccess_token(){
        // 3 换取access_token
        // client_secret=a79777bba04ac70d973ee002d27ed58c
        String s3 = "https://api.weibo.com/oauth2/access_token?";//?client_id=187638711&client_secret=a79777bba04ac70d973ee002d27ed58c&grant_type=authorization_code&redirect_uri=http://passport.gmall.com:8085/vlogin&code=CODE";
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("client_id","187638711");
        paramMap.put("client_secret","a79777bba04ac70d973ee002d27ed58c");
        paramMap.put("grant_type","authorization_code");
        paramMap.put("redirect_uri","http://passport.gmall.com:8085/vlogin");
        paramMap.put("code","b882d988548ed2b9174af641d20f0dc1");// 授权有效期内可以使用，没新生成一次授权码，说明用户对第三方数据进行重启授权，之前的access_token和授权码全部过期
        String access_token_json = HttpclientUtil.doPost(s3, paramMap);

       Map<String,String> access_map = JSON.parseObject(access_token_json,Map.class);

       System.out.println(access_map.get("access_token"));
       System.out.println(access_map.get("uid"));

        return access_map.get("access_token");
    }

    public static Map<String,String> getUser_info(){

        // 4 用access_token查询用户信息
        String s4 = "https://api.weibo.com/2/users/show.json?access_token=2.00HMAs7H0p5_hMdbefcb34140Lydjf&uid=6809985023";
        String user_json = HttpclientUtil.doGet(s4);
        Map<String,String> user_map = JSON.parseObject(user_json,Map.class);

        System.out.println(user_map.get("1"));

        return user_map;
    }


    public static void main(String[] args) {

        //getUser_info();

        // 1 获得授权码
        // 1590848947
        // http://passport.gmall.com:8085/lvlogin

        //String s1 = HttpclientUtil.doGet("https://api.weibo.com/oauth2/authorize?client_id=1590848947&response_type=code&redirect_uri=http://password.gmall.com:8085/vlogin");

        //System.out.println(s1);

        // 2 返回授权码到回调地址
        String s2="http://password.gmall.com:8085/vlogin?code=9646f7b62cf7dd98934020f0df41afa4";

        //http://password.gmall.com:8085/vlogin?code=555fe7cadf9c9f19dffffa7ba10d01b0
        //357f98f9c9d412faea21cc47b11878e5
        //String s3="https://api.weibo.com/oauth2/access_token?client_id=1590848947&client_secret=357f98f9c9d412faea21cc47b11878e5&grant_type=9646f7b62cf7dd98934020f0df41afa4&redirect_uri=http://passport.gmall.com:8085/vlogin&code=CODE";

        String s3 = "https://api.weibo.com/oauth2/access_token?";
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("client_id","1590848947");
        paramMap.put("client_secret","357f98f9c9d412faea21cc47b11878e5");
        paramMap.put("grant_type","authorization_code");
        paramMap.put("redirect_uri","http://password.gmall.com:8085/vlogin");
        paramMap.put("code","b90155cba0a4c565341a727eb07e08be");// 授权有效期内可以使用，没新生成一次授权码，说明用户对第三方数据进行重启授权，之前的access_token和授权码全部过期
//        String access_token=HttpclientUtil.doPost(s3,paramMap);
//
//        Map<String,String> access_map=JSON.parseObject(access_token,Map.class);
//
//        System.out.println(access_map.get("access_token"));
//        System.out.println(access_token);

        //4用access查询用户信息
        String s4 = "https://api.weibo.com/2/users/show.json?access_token=2.00H1GWzGzTCfjBcb8e9cfc1aQenG1B&uid=1";
        String user_json = HttpclientUtil.doGet(s4);
        Map<String,String> user_map = JSON.parseObject(user_json,Map.class);

        System.out.println(user_map.get("1"));



    }
}
