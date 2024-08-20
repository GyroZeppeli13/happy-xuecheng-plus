package com.xuecheng.ucenter.model.dto;

import lombok.Data;

/**
 * 找回密码参数封装类
 */
@Data
public class FindPasswordParamsDto {
    // 手机号
    private String cellphone;
    // 邮箱
    private String email;
    // 验证码key
    private String checkcodekey;
    // 用户输入的验证码
    private String checkcode;
    // 确认用户密码
    private String confirmpwd;
    // 用户密码
    private String password;
}
