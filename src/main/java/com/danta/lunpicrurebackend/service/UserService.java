package com.danta.lunpicrurebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.danta.lunpicrurebackend.model.dto.user.UserQueryRequest;
import com.danta.lunpicrurebackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.danta.lunpicrurebackend.model.vo.LoginUserVo;
import com.danta.lunpicrurebackend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author Administrator
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2024-12-10 18:01:21
*/
public interface UserService extends IService<User> {


    /**
     * 用户注册
     * <p>
     * 该方法用于用户注册，接收用户账号、用户密码以及确认密码作为参数。
     * 在注册前会进行密码一致性校验。
     *
     * @param userAccount 用户账号，字符串类型，用于唯一标识一个用户
     * @param userPassword 用户密码，字符串类型，用于用户登录验证
     * @param checkPassword 确认密码，字符串类型，用于确保用户输入的密码一致
     * @return 返回一个长整型值，通常为用户ID，用于后续操作中标识用户
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    LoginUserVo userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取加密后的密码
     * @param userPassword
     * @return
     */
    String getEncryptPassword(String userPassword);

    /**
     * 获取当前登录用户
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获得脱敏后的用户登录信息
     * @param user
     * @return
     */
    LoginUserVo getLoginUserVo(User user);


    /**
     * 获得脱敏后的用户信息
     * @param user
     * @return
     */
    UserVO getUserVo(User user);


    /**
     * 获得脱敏后的用户列表
     * @param userList
     * @return 脱敏后的用户列表
     */
    List<UserVO> getUserVoList(List<User> userList);

    /**
     * 用户注销
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);


    QueryWrapper<User> getUserQueryWrapper(UserQueryRequest userQueryRequest);
}

