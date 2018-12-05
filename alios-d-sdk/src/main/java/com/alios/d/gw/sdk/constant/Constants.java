package com.alios.d.gw.sdk.constant;

/**
 * 常量
 */
public class Constants {

    /**
     * 获取oss上传信息
     */
    public static final String OSS_GET_UPLOAD_META_PATH = "dataconfig.uploadurl.get";
    public static final String OSS_GET_UPLOAD_META_KEY_TENANTID = "tenantId";
    public static final String OSS_GET_UPLOAD_META_KEY_DEVICEID = "deviceId";
    public static final String OSS_GET_UPLOAD_META_KEY_EVENTID = "eventId";
    public static final String OSS_GET_UPLOAD_META_KEY_FILENAMELIST = "fileNameList";
    public static final String OSS_GET_UPLOAD_META_KEY_REQUEST = "request";
    public static final String OSS_GET_UPLOAD_META_KEY_SINKEXTENDKEY = "sinkExtendKey";
    public static final String OSS_GET_UPLOAD_META_KEY_OSSPROTOCOL = "ossProtocol";
    public static final String OSS_GET_UPLOAD_META_KEY_OSSKEY = "ossKey";
    public static final String OSS_GET_UPLOAD_META_KEY_ACCESSID = "accessId";
    public static final String OSS_GET_UPLOAD_META_KEY_POLICY = "policy";
    public static final String OSS_GET_UPLOAD_META_KEY_SIGNATURE = "signature";
    public static final String OSS_GET_UPLOAD_META_KEY_HOST = "host";

    /**
     * 单个上传文件的最大大小
     */
    public static final String OSS_GET_UPLOAD_META_KEY_SINGLE_MAX_SIZE = "singleMaxSize";

    /**
     * 回写事件
     */
    public static final String REPORT_EVENT_PATH = "dataconnectorcloud.data.pushUploadFileResult";
    public static final String REPORT_EVENT_KEY_SINK_EXTEND_KEY = "SinkExtendKey";
    public static final String REPORT_EVENT_KEY_OSSKEY = "OssKey";
    public static final String REPORT_EVENT_KEY_OSS_PROTOCOL = "OssProtocol";
    public static final String REPORT_EVENT_KEY_OSS_FILENAME = "FileName";
    public static final String REPORT_EVENT_KEY_OSS_UPLOADRESULT = "UploadResult";
    public static final String REPORT_EVENT_KEY_OSS_UPLOADMSG = "UploadMsg";
    public static final String REPORT_EVENT_KEY_OSS_OSSTIMESTAMP = "OssTimeStamp";
    public static final String REPORT_EVENT_KEY_OSS_CLOUDSDKJSON = "cloudSdkJson";
    public static final String REPORT_EVENT_KEY_OSS_BUSINESSJSON = "businessJson";

    /**
     * 字符集
     */
    public static final String CHARSET_UTF8 = "utf-8";

    /**
     * 返回码
     */
    public static final int RESPONSE_CODE_SUCCESS = 200;
    public static final int RESULT_CODE_FILE_TOO_LARGE = 800;

    /**
     * 网关返回结果的key
     */
    public static final String GATEWAY_KEY_CODE = "code";
    public static final String GATEWAY_KEY_MESSAGE = "message";
    public static final String GATEWAY_KEY_LOCALIZEDMSG = "localizedMsg";
    public static final String GATEWAY_KEY_DATA = "data";

}
