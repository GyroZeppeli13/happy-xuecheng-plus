package com.xuecheng.base.exception;

/**
 * 错误响应参数带errCode包装
 */
public class RestErrorCodeResponse extends RestErrorResponse{

    private Long errCode;

    public RestErrorCodeResponse(Long errCode, String errMessage) {
        super(errMessage);
        this.errCode = errCode;
    }


    public Long getErrCode() {
        return errCode;
    }

    public void setErrCode(Long errCode) {
        this.errCode = errCode;
    }
}
