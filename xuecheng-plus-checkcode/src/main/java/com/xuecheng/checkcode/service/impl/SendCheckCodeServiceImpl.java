package com.xuecheng.checkcode.service.impl;

import com.xuecheng.checkcode.model.CheckCodeParamsDto;
import com.xuecheng.checkcode.model.CheckCodeResultDto;
import com.xuecheng.checkcode.service.CheckCodeService;
import com.xuecheng.checkcode.service.SendCheckCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SendCheckCodeServiceImpl implements SendCheckCodeService {

    @Resource(name = "NumCheckCodeService")
    private CheckCodeService numCheckCodeService;

    /**
     * 给手机号发送验证码
     * @param checkCodeParamsDto 参数对象
     * @return 验证码结果对象
     */
    @Override
    public CheckCodeResultDto sendCheckCodeByPhone(CheckCodeParamsDto checkCodeParamsDto) {
        // 获取参数1，手机号或者邮箱
        String param1 = checkCodeParamsDto.getParam1();

        // 生成验证码
        CheckCodeResultDto checkCodeResultDto = numCheckCodeService.generate(checkCodeParamsDto);

        // 定义手机号的正则表达式
        String regexPhone = "[1-9]\\d{9}";

        // 定义邮箱的正则表达式
        String regexEmail = "\\w+([\\w&&[^_]]{2,6}(\\.[a-zA-Z]{2,3}){1,2})";

        if (param1.matches(regexPhone)) {
            System.out.println("手机号为：" + param1 + "，发送验证码：" + checkCodeResultDto.getAliasing());
        } else if (param1.matches(regexEmail)) {
            System.out.println("邮箱为：" + param1 + "，发送验证码：" + checkCodeResultDto.getAliasing());
        }

        return checkCodeResultDto;
    }
}

