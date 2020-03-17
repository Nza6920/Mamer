package com.example.my.mamer.config;

import com.example.my.mamer.bean.User;

import org.json.JSONObject;

public class GlobalUserInfo {
//    当前用户
    public User user;
//    token
    public String token ;
//    token_type
    public String tokenType;
//    请求对象
    public JSONObject request=new JSONObject();

}
