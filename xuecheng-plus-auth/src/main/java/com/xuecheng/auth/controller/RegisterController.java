package com.xuecheng.auth.controller;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.ucenter.model.dto.RegisterParamsDto;
import com.xuecheng.ucenter.service.RegisterService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class RegisterController {

    @Autowired
    RegisterService registerService;

    @ApiOperation("注册账号服务")
    @PostMapping(value = "/register")
    public RestResponse Register(@RequestBody RegisterParamsDto registerParamsDto){
        return registerService.registration(registerParamsDto);
    }
}
