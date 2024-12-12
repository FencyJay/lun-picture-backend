package com.danta.lunpicrurebackend.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验
 */

@Target(ElementType.METHOD) //生效范围
@Retention(RetentionPolicy.RUNTIME) //运行时生效
public @interface AuthCheck {


    String mustRole() default "";

}
