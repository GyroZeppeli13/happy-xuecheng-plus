package com.xuecheng.ucenter.model.dto;

import lombok.Data;

@Data
public class RegisterParamsDto extends FindPasswordParamsDto {
    // 用户名
    private String username;
    // 昵称
    private String nickname;
}
