package com.danta.lunpicrurebackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.danta.lunpicrurebackend.constant.UserConstant;
import com.danta.lunpicrurebackend.exception.BusinessException;
import com.danta.lunpicrurebackend.exception.ErrorCode;
import com.danta.lunpicrurebackend.mapper.UserMapper;
import com.danta.lunpicrurebackend.model.dto.user.UserQueryRequest;
import com.danta.lunpicrurebackend.model.entity.User;
import com.danta.lunpicrurebackend.model.enums.UserRoleEnum;
import com.danta.lunpicrurebackend.model.vo.LoginUserVo;
import com.danta.lunpicrurebackend.model.vo.UserVO;
import com.danta.lunpicrurebackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2024-12-10 18:01:21
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

  /**
     * 用户注册方法，用于处理用户注册逻辑。
     *
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 确认密码
     * @return 返回用户注册后的唯一标识符（如用户ID）
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 检验参数
        if(StrUtil.hasBlank(userAccount, userPassword, checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数不能为空");
        }
        if(userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度不能小于4位");
        }
        if(userPassword.length() < 8 || checkPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码长度不能小于8位");
        }
        if(!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次输入的密码不一致");
        }
        // 2. 检查用户账号是否和数据库中已有的重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        Long count = this.baseMapper.selectCount(queryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号已存在");
        }

        // 3. 密码一定要加密
        String encryptPassword = getEncryptPassword(userPassword);
        // 4. 插入数据到数据库中
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName("无名");
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean saveResult = this.save(user);
        if(!saveResult){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"用户注册失败");
        }
        return user.getId();
    }

    @Override
    public LoginUserVo userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 检验参数
        if(StrUtil.hasBlank(userAccount, userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数不能为空");
        }
        if(userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度不能小于4位");
        }
        if(userPassword.length() < 8 ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码长度不能小于8位");
        }
        // 2. 对用户传递的密码进行加密
        String encryptPassword = getEncryptPassword(userPassword);
        // 3. 查询数据库中的用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        //  不存在，抛异常
        if(user == null){
            log.info("user login failed,userAccount cannot match password");
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号或密码错误");
        }
        // 4. 保存用户登录态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);

        return this.getLoginUserVo(user);
    }

    @Override
    public String getEncryptPassword(String userPassword){
        // 加盐，混淆密码
        final String SALT = "danta";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes() );
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser  = (User) userObj;
        if(currentUser == null || currentUser.getId() == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR,"用户未登录");
        }
        // 从数据库中查询（追求性能可以直接返回）
        Long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if(currentUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR,"用户未登录");
        }
        return currentUser;
    }

    @Override
    public LoginUserVo getLoginUserVo(User user) {
        if(user == null){
           return null;
        }
        LoginUserVo loginUserVo = new LoginUserVo();
        BeanUtil.copyProperties(user, loginUserVo);
        return loginUserVo;
    }

    /**
     * 获得脱敏后的用户
     * @param user
     * @return 脱敏后的用户
     */
    @Override
    public UserVO getUserVo(User user) {
        if(user == null){
            return null;
        }
        UserVO UserVO = new UserVO();
        BeanUtil.copyProperties(user, UserVO);
        return UserVO;

    }

    /**
     * 获得脱敏后的用户列表
     * @param userList
     * @return 脱敏后的用户列表
     */
    @Override
    public List<UserVO> getUserVoList(List<User> userList) {
        if(userList == null){
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVo).collect(Collectors.toList());
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if(userObj == null ){
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);

        return true;
    }

    @Override
    public QueryWrapper<User> getUserQueryWrapper(UserQueryRequest userQueryRequest) {
        if(userQueryRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数不能为空");
        }
        Long id = userQueryRequest.getId();
        String userName = userQueryRequest.getUserName();
        String userAccount = userQueryRequest.getUserAccount();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        int current = userQueryRequest.getCurrent();
        int pageSize = userQueryRequest.getPageSize();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotNull(id), "id", id);
        queryWrapper.eq(ObjectUtil.isNotNull(userRole), "userRole", userRole);
        queryWrapper.like(ObjectUtil.isNotNull(userName), "userName", userName);
        queryWrapper.like(ObjectUtil.isNotNull(userAccount), "userAccount", userAccount);
        queryWrapper.like(ObjectUtil.isNotNull(userProfile),"userProfile", userProfile);
        // sortOrder.equals("ascend") 来判断排序顺序是否是升序（ascend）,如果 sortOrder 不等于 "ascend"，返回 false，表示不需要升序排序，可能是降序排序（descend）。
        // sortField排序的字段
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);


        return null;
    }
}




