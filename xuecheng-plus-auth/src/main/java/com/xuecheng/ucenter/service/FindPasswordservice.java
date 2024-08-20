package com.xuecheng.ucenter.service;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.ucenter.model.dto.FindPasswordParamsDto;

public interface FindPasswordservice {
    RestResponse findPasswordByPhone(FindPasswordParamsDto findPasswordParamsDto);
}
