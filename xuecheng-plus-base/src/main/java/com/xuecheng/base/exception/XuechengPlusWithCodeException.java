package com.xuecheng.base.exception;

public class XuechengPlusWithCodeException extends XueChengPlusException{
    private Long errCode;

    public XuechengPlusWithCodeException() {
        super();
    }

    public XuechengPlusWithCodeException(String errMessage) {
        super(errMessage);
    }

    public XuechengPlusWithCodeException(Long errCode,String errMessage) {
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
