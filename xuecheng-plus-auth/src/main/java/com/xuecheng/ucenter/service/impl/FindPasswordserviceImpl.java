package com.xuecheng.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.ucenter.feignclient.CheckCodeClient;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.FindPasswordParamsDto;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.FindPasswordservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FindPasswordserviceImpl implements FindPasswordservice {

    @Autowired
    CheckCodeClient checkCodeClient;

    @Autowired
    XcUserMapper xcUserMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    public RestResponse findPasswordByPhone(FindPasswordParamsDto findPasswordParamsDto) {
        // 1.校验验证码，不一致则抛出异常
        String checkcodekey = findPasswordParamsDto.getCheckcodekey();
        String checkcode = findPasswordParamsDto.getCheckcode();
        Boolean verify = checkCodeClient.verify(checkcodekey, checkcode);
        if (!verify) {
            XueChengPlusException.cast("输入的验证码不正确！");
        }

        // 2.判断两次密码是否一致，不一致则抛出异常
        String confirmpwd = findPasswordParamsDto.getConfirmpwd();
        String password = findPasswordParamsDto.getPassword();
        if (!confirmpwd.equals(password)) {
            XueChengPlusException.cast("两次密码输入不一致");
        }

        // 3.通过手机/邮箱查询用户
        String ceilphone = findPasswordParamsDto.getCellphone();
        String email = findPasswordParamsDto.getEmail();
        XcUser xcUser = null;
        if (ceilphone != null) {
            // 根据手机查询
            xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>()
                    .eq(XcUser::getCellphone, ceilphone));
        } else {
            // 根据邮箱查询
            xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>()
                    .eq(XcUser::getEmail, email));
        }

        // 4.如果找到用户更新为新密码
        if (xcUser == null) {
            XueChengPlusException.cast("用户不存在");
        }
        // 给密码加密
        String encode = passwordEncoder.encode(password);
        xcUser.setPassword(encode);
        xcUser.setUpdateTime(LocalDateTime.now());
        // 写入数据库
        int i = xcUserMapper.updateById(xcUser);

        // 5.构建响应
        RestResponse<String> restResponse = new RestResponse<>();
        restResponse.setCode(200);
        restResponse.setMsg("找回成功");
        return restResponse;
    }
}
