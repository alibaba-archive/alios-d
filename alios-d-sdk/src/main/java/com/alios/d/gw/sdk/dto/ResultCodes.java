package com.alios.d.gw.sdk.dto;

public class ResultCodes {
    public final static ResultCode SUCCESS = new ResultCode(200, "success", "成功");
    public final static ResultCode REQUEST_PARAM_ERROR = new ResultCode(460, "request parameter error.", "请求参数错误");
    public final static ResultCode SERVER_ERROR = new ResultCode(500, "server error.", "服务器错误");
    public final static ResultCode NOT_A_FILE_ERROR = new ResultCode(700, "The uploaded file type must be a file", "上传的文件类型必须是文件");
    public final static ResultCode OSS_UPLOAD_FAILED = new ResultCode(701, "oss upload failed", "上传的文件到oss失败");
}
