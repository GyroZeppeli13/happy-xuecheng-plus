package com.xuecheng.checkcode.service;

import com.xuecheng.checkcode.model.CheckCodeParamsDto;
import com.xuecheng.checkcode.model.CheckCodeResultDto;

public interface SendCheckCodeService {
    CheckCodeResultDto sendCheckCodeByPhone(CheckCodeParamsDto checkCodeParamsDto);
}
