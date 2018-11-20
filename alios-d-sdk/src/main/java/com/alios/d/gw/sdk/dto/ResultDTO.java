package com.alios.d.gw.sdk.dto;

/**
 * 结果包装类
 *
 * @author baixin
 */
public class ResultDTO<T> {
    private boolean success;
    private int errorCode;
    private String errorMsg;
    private String localizedMsg;
    private T data;

    private ResultDTO(){}

    public static <T> ResultDTO getResult(T v, ResultCode resultCode) {
        if (resultCode == null) {
            resultCode = ResultCodes.SUCCESS;
        }
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setData(v);
        resultDTO.setSuccess(resultCode.isSuccess());
        resultDTO.setErrorCode(resultCode.getCode());
        resultDTO.setErrorMsg(resultCode.getMessage());
        resultDTO.setLocalizedMsg(resultCode.getLocalizedMsg());
        return resultDTO;
    }

    public static <T> ResultDTO getResult(T v) {
        return getResult(v, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getLocalizedMsg() {
        return localizedMsg;
    }

    public void setLocalizedMsg(String localizedMsg) {
        this.localizedMsg = localizedMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
