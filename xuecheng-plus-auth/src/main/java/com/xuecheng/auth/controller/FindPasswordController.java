package com.xuecheng.auth.controller;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.ucenter.model.dto.FindPasswordParamsDto;
import com.xuecheng.ucenter.service.FindPasswordservice;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class FindPasswordController {
    @Autowired
    FindPasswordservice findPasswordService;

    @ApiOperation("找回密码服务")
    @PostMapping(value = "/findpassword")
    public RestResponse findPassword(@RequestBody FindPasswordParamsDto findPasswordParamsDto) {
        RestResponse restResponse = findPasswordService.findPasswordByPhone(findPasswordParamsDto);
        return restResponse;
    }
}
