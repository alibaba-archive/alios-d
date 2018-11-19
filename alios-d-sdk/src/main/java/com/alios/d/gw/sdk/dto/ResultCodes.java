package com.alios.d.gw.sdk.dto;

public class ResultCodes {
    public final static ResultCode SUCCESS = new ResultCode(200, "success", "成功");
    public final static ResultCode REQUEST_PARAM_ERROR = new ResultCode(460, "request parameter error.", "请求参数错误");
}
