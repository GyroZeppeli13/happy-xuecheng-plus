package com.xuecheng.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.ucenter.feignclient.CheckCodeClient;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.mapper.XcUserRoleMapper;
import com.xuecheng.ucenter.model.dto.RegisterParamsDto;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.model.po.XcUserRole;
import com.xuecheng.ucenter.service.RegisterService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    XcUserMapper xcUserMapper;

    @Autowired
    XcUserRoleMapper xcUserRoleMapper;

    @Autowired
    CheckCodeClient checkCodeClient;

    /**
     * 注册学生用户
     * @param registerParamsDto 注册用户表单信息
     * @return
     * @override
     */
    public RestResponse registration(RegisterParamsDto registerParamsDto) {
        // 校验验证码
        checkForm(registerParamsDto);
        //判断两次密码是否一致，不一致则抛出异常
        String confirmpwd = registerParamsDto.getConfirmpwd();
        String password = registerParamsDto.getPassword();
        if (!confirmpwd.equals(password)) {
            XueChengPlusException.cast("两次密码输入不一致");
        }
        // 查询用户
        XcUser xcuser = isExistUser(registerParamsDto);
        if (xcuser != null) {
            XueChengPlusException.cast("用户已存在");
        }
        // 向用户表，用户角色关系表添加数据，角色为学生角色
        // 写入用户表
        xcuser = new XcUser();
        BeanUtils.copyProperties(registerParamsDto, xcuser);
        String encode = passwordEncoder.encode(xcuser.getPassword()); // 给密码加密
        xcuser.setPassword(encode);
        xcuser.setUtype("101001");
        xcuser.setStatus("1");
        xcuser.setName("学生" + UUID.randomUUID());
        xcuser.setCreateTime(LocalDateTime.now());
        xcUserMapper.insert(xcuser);

        // 写入用户角色关系表
        XcUserRole xcUserRole = new XcUserRole();
        xcUserRole.setUserId(xcuser.getId());
        xcUserRole.setRoleId("17"); // 学生
        xcUserRole.setCreateTime(LocalDateTime.now());
        xcUserRoleMapper.insert(xcUserRole);

        return RestResponse.success(true,  "注册成功");
    }

    private XcUser isExistUser(RegisterParamsDto registerParamsDto) {
        // 通过手机/邮箱查询用户
        String cellphone = registerParamsDto.getCellphone();
        String email = registerParamsDto.getEmail();
        XcUser xcuser = null;
        if (cellphone == null && email == null) {
            XueChengPlusException.cast("手机号码和邮箱不能都为空");
        }
        if (cellphone != null) {
            // 根据手机查询
            xcuser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>()
                    .eq(XcUser::getCellphone, cellphone));
        } else if (email != null) {
            // 根据邮箱查询
            xcuser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>()
                    .eq(XcUser::getEmail, email));
        }
        return xcuser;
    }

    private void checkForm(RegisterParamsDto registerParamsDto) {
        // 1.校验验证码，不一致则抛出异常
        String checkcodekey = registerParamsDto.getCheckcodekey();
        String checkcode = registerParamsDto.getCheckcode();
        Boolean verify = checkCodeClient.verify(checkcodekey, checkcode);
        if (!verify) {
            XueChengPlusException.cast("输入的验证码不正确！");
        }
        // 2.判断两次密码是否一致，不一致则抛出异常
        String confirmpwd = registerParamsDto.getConfirmpwd();
        String password = registerParamsDto.getPassword();
        if (!confirmpwd.equals(password)) {
            XueChengPlusException.cast("两次密码输入不一致");
        }
    }
}
