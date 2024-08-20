package com.xuecheng.ucenter.service;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.ucenter.model.dto.RegisterParamsDto;

public interface RegisterService {
    public RestResponse registration(RegisterParamsDto registerParamsDto);
}
