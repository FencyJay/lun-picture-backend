package com.danta.lunpicrurebackend.controller;

import com.danta.lunpicrurebackend.common.BaseResponse;
import com.danta.lunpicrurebackend.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class MainController {

    @GetMapping("/health")
    public BaseResponse<String> health(){
        return ResultUtils.success("ok");
    }
}
