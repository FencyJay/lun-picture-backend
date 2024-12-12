package com.danta.lunpicrurebackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.danta.lunpicrurebackend.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class LunPictrueBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(LunPictrueBackendApplication.class, args);
    }

}
