package com.alios.d.gw.sdk.dto;

public class ResultCode {
    private final int code;
    private final String message;
    private final String localizedMsg;

    public ResultCode(int code, String message, String localizedMsg) {
        this.code = code;
        this.message = message;
        this.localizedMsg = localizedMsg;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getLocalizedMsg() {
        return localizedMsg;
    }

    public boolean isSuccess() {
        return 200 == code;
    }
}
