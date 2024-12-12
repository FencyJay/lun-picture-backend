package com.danta.lunpicrurebackend.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求
 */
@Data
public class UserLoginRequest implements Serializable {

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;




    private static final long serialVersionUID = -136120010600253235L;
}
