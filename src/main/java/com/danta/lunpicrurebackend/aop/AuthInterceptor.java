package com.danta.lunpicrurebackend.aop;

import com.danta.lunpicrurebackend.annotation.AuthCheck;
import com.danta.lunpicrurebackend.exception.BusinessException;
import com.danta.lunpicrurebackend.exception.ErrorCode;
import com.danta.lunpicrurebackend.model.entity.User;
import com.danta.lunpicrurebackend.model.enums.UserRoleEnum;
import com.danta.lunpicrurebackend.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    @Around("@annotation(authCheck)") //环绕通知，作用在被 @AuthCheck 注解标记的方法上
    private Object doIntercept(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        // 从注解中获取必须角色
        String mustRole = authCheck.mustRole();

        // 获取当前登录的用户信息
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        User loginUser = userService.getLoginUser(request);



        // 根据注解中指定的角色（mustRole）获取对应的角色枚举
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);

        // 如果没有指定角色，直接执行方法
        if (mustRoleEnum == null) {
            return joinPoint.proceed();
        }

        // 获取当前登录用户的角色
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());

        // 如果用户角色无效，抛出权限错误
        if (userRoleEnum == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 如果用户角色不是管理员且不符合要求，抛出权限错误
        if (UserRoleEnum.ADMIN.equals(mustRoleEnum) && !UserRoleEnum.ADMIN.equals(userRoleEnum)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 如果一切正常，继续执行方法
        return joinPoint.proceed();
    }


}
