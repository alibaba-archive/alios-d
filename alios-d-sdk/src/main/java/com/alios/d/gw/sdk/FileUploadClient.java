/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alios.d.gw.sdk;

import com.alibaba.cloudapi.sdk.core.annotation.ThreadSafe;
import com.alibaba.cloudapi.sdk.core.model.ApiResponse;
import com.alibaba.cloudapi.sdk.core.model.BuilderParams;
import com.alibaba.fastjson.JSONObject;
import com.alios.d.gw.sdk.dto.FileUploadDTO;
import com.alios.d.gw.sdk.dto.ResultCode;
import com.alios.d.gw.sdk.dto.ResultCodes;
import com.alios.d.gw.sdk.dto.ResultDTO;
import com.alios.d.gw.sdk.util.OSSFormUploadUtil;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.alios.d.gw.sdk.constant.Constants.*;

@ThreadSafe
public final class FileUploadClient extends AbstractApiGwClient {

    private static final ConcurrentHashMap<String, FileUploadClient> API_GW_CLIENT_CACHE = new ConcurrentHashMap<>();
    private static final Object LOCK = new Object();

    public FileUploadClient(BuilderParams params, String stage, String groupHost) {
        super(params, stage, groupHost);
    }

    @ThreadSafe
    public static class Builder extends AbstractBaseApiClientBuilder<Builder, FileUploadClient> {

        @Override
        protected FileUploadClient build(BuilderParams params, String stage, String groupHost) {
            String cacheKey = params.getAppKey() + params.getAppSecret() + stage + groupHost;
            if (!API_GW_CLIENT_CACHE.contains(cacheKey)) {
                synchronized (LOCK) {
                    if (!API_GW_CLIENT_CACHE.contains(cacheKey)) {
                        FileUploadClient fileUploadClient = new FileUploadClient(params, stage, groupHost);
                        API_GW_CLIENT_CACHE.put(cacheKey, fileUploadClient);
                    }
                }
            }
            return API_GW_CLIENT_CACHE.get(cacheKey);
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static FileUploadClient getInstance() {
        return getApiClassInstance(FileUploadClient.class);
    }


    /**
     * 上传本地文件，并且上报事件
     *
     * @param fileUploadDTO 事件字段
     * @param localFilePathList 本地文件PathList
     */
    public ResultDTO upload(FileUploadDTO fileUploadDTO, List<String> localFilePathList) {
        if (!isUploadParamLegal(fileUploadDTO, localFilePathList)) {
            return ResultDTO.getResult(null, ResultCodes.REQUEST_PARAM_ERROR);
        }
        //获取上传文件到oss所需要的meta信息
        ResultDTO uploadMetaResultDTO = getUploadMeta(fileUploadDTO, localFilePathList);
        if (!uploadMetaResultDTO.isSuccess()) {
            return uploadMetaResultDTO;
        }
        JSONObject uploadMeta = (JSONObject) uploadMetaResultDTO.getData();
        //校验要上传的本地文件是否合法
        ResultDTO checkLocalFileResultDTO = checkLocalFile(uploadMeta, localFilePathList);
        if (!checkLocalFileResultDTO.isSuccess()) {
            return checkLocalFileResultDTO;
        }
        //上传
        ResultDTO uploadResultDTO = doUpload(uploadMeta, localFilePathList);
        //上报非结构化数据上传事件
        return reportEvent(uploadResultDTO, uploadMeta, fileUploadDTO);
    }

    /**
     * 执行上传文件到oss的操作
     * @param uploadMeta    上传文件所需的meta信息
     * @param localFilePathList 本次需要上传的本地文件
     * @return
     */
    private ResultDTO doUpload(JSONObject uploadMeta, List<String> localFilePathList) {
        ResultDTO uploadResultDTO = ResultDTO.getResult(null);
        for (int i = 0; i < localFilePathList.size(); i++) {
            String localFilePath = localFilePathList.get(i);
            Map<String, String> formFields = new LinkedHashMap<String, String>();

            formFields.put("key", (String) uploadMeta.getJSONArray(OSS_GET_UPLOAD_META_KEY_OSSKEY).get(i));
            formFields.put("Content-Disposition", "attachment;filename="
                    + localFilePath);
            formFields.put("OSSAccessKeyId", uploadMeta.getString(OSS_GET_UPLOAD_META_KEY_ACCESSID));
            formFields.put("policy", uploadMeta.getString(OSS_GET_UPLOAD_META_KEY_POLICY));
            formFields.put("Signature", uploadMeta.getString(OSS_GET_UPLOAD_META_KEY_SIGNATURE));
            uploadResultDTO = OSSFormUploadUtil.formUpload(formFields, uploadMeta.getString(OSS_GET_UPLOAD_META_KEY_HOST), localFilePath);
            if (!uploadResultDTO.isSuccess()) {
                break;
            }
        }
        return uploadResultDTO;
    }

    /**
     * 校验要上传的文件是否合法
     * @param uploadMeta oss上传的meta数据
     * @param localFilePathList 需要上传的本地文件的pathList
     * @return
     */
    private ResultDTO checkLocalFile(JSONObject uploadMeta, List<String> localFilePathList) {
        Integer singleMaxSize = uploadMeta.getInteger(OSS_GET_UPLOAD_META_KEY_SINGLE_MAX_SIZE);
        if (singleMaxSize != null && singleMaxSize > 0) {
            for (String localFilePath : localFilePathList) {
                File localFile = new File(localFilePath);
                if (!localFile.isFile()) {
                    return ResultDTO.getResult(null, ResultCodes.NOT_A_FILE_ERROR);
                }
                if (localFile.length() > singleMaxSize * 1024 * 1024) {
                    return ResultDTO.getResult(null,
                            new ResultCode(RESULT_CODE_FILE_TOO_LARGE, "file size shoud not exceed " + singleMaxSize + "M", "上传文件的大小不能超过" + singleMaxSize + "M"));
                }
            }
        }
        return ResultDTO.getResult(true);
    }

    /**
     * 上传的参数是否合法。合法返回true，否则返回false。
     *
     * @param fileUploadDTO
     * @param localFilePathList
     * @return
     */
    private boolean isUploadParamLegal(FileUploadDTO fileUploadDTO, List<String> localFilePathList) {
        return !(fileUploadDTO == null || fileUploadDTO.getEventId() == null || fileUploadDTO.getItemId() == null
                || fileUploadDTO.getData() == null || fileUploadDTO.getDataId() == null
                || fileUploadDTO.getServerTimestamp() == null || fileUploadDTO.getClientTimestamp() == null
                || fileUploadDTO.getReissueCount() == null || localFilePathList == null || localFilePathList.isEmpty());
    }

    /**
     * 回写事件
     */
    private ResultDTO reportEvent(ResultDTO uploadResultDTO, JSONObject uploadMeta, FileUploadDTO fileUploadDTO) {
        ApiTransferParamDTO paramDTO = new ApiTransferParamDTO();
        JSONObject cloudSdkJson = new JSONObject();
        cloudSdkJson.put(REPORT_EVENT_KEY_SINK_EXTEND_KEY, uploadMeta.getString(OSS_GET_UPLOAD_META_KEY_SINKEXTENDKEY));
        cloudSdkJson.put(REPORT_EVENT_KEY_OSSKEY, uploadMeta.getJSONArray(OSS_GET_UPLOAD_META_KEY_OSSKEY).toJSONString());
        cloudSdkJson.put(REPORT_EVENT_KEY_OSS_PROTOCOL, uploadMeta.getJSONArray(OSS_GET_UPLOAD_META_KEY_OSSPROTOCOL).toJSONString());
        cloudSdkJson.put(REPORT_EVENT_KEY_OSS_FILENAME, uploadMeta.getJSONArray(OSS_GET_UPLOAD_META_KEY_FILENAMELIST).toJSONString());
        cloudSdkJson.put(REPORT_EVENT_KEY_OSS_UPLOADRESULT, uploadResultDTO.isSuccess());
        if (!uploadResultDTO.isSuccess()) {
            cloudSdkJson.put(REPORT_EVENT_KEY_OSS_UPLOADMSG, uploadResultDTO.getErrorMsg());
        }
        cloudSdkJson.put(REPORT_EVENT_KEY_OSS_OSSTIMESTAMP, System.currentTimeMillis());
        paramDTO.addParam(REPORT_EVENT_KEY_OSS_CLOUDSDKJSON, cloudSdkJson.toJSONString());

        JSONObject businessJson = JSONObject.parseObject(JSONObject.toJSONString(fileUploadDTO));
        paramDTO.addParam(REPORT_EVENT_KEY_OSS_BUSINESSJSON, businessJson.toJSONString());

        ApiResponse uploadMetaResponse = syncInvokeWrapper(REPORT_EVENT_PATH, JSONObject.toJSONString(paramDTO));
        // TODO: 2018/12/10 写明注释
        if (!uploadResultDTO.isSuccess()) {
            return uploadResultDTO;
        }
        int statusCode = uploadMetaResponse.getStatusCode();
        String respJsonStr = new String(uploadMetaResponse.getBody(), Charset.forName(CHARSET_UTF8));
        JSONObject respJson = JSONObject.parseObject(respJsonStr);
        if (statusCode != RESPONSE_CODE_SUCCESS) {
            if (respJson != null) {
                return ResultDTO.getResult(null,
                        new ResultCode(respJson.getInteger(GATEWAY_KEY_CODE), respJson.getString(GATEWAY_KEY_MESSAGE), respJson.getString(GATEWAY_KEY_LOCALIZEDMSG)));
            } else {
                return ResultDTO.getResult(null, ResultCodes.SERVER_ERROR);
            }
        }
        if (respJson.getInteger(GATEWAY_KEY_CODE) != RESPONSE_CODE_SUCCESS) {
            return ResultDTO.getResult(respJson.get(GATEWAY_KEY_DATA),
                    new ResultCode(respJson.getInteger(GATEWAY_KEY_CODE), respJson.getString(GATEWAY_KEY_MESSAGE), respJson.getString(GATEWAY_KEY_LOCALIZEDMSG)));
        }
        return ResultDTO.getResult(null);
    }

    /**
     * 获取上传oss的upload信息
     *
     * @return
     */
    private ResultDTO getUploadMeta(FileUploadDTO fileUploadDTO, List<String> localFilePathList) {
        ApiTransferParamDTO paramDTO = new ApiTransferParamDTO();
        JSONObject request = new JSONObject();
        request.put(OSS_GET_UPLOAD_META_KEY_DEVICEID, fileUploadDTO.getItemId());
        request.put(OSS_GET_UPLOAD_META_KEY_EVENTID, fileUploadDTO.getEventId());
        List<String> fileNameList = new ArrayList<>();
        for (String localFilePath : localFilePathList) {
            fileNameList.add(localFilePath.substring(localFilePath.lastIndexOf(File.separator) + 1));
        }
        request.put(OSS_GET_UPLOAD_META_KEY_FILENAMELIST, fileNameList);
        paramDTO.addParam(OSS_GET_UPLOAD_META_KEY_REQUEST, request);
        ApiResponse uploadMetaResponse = syncInvokeWrapper(OSS_GET_UPLOAD_META_PATH, JSONObject.toJSONString(paramDTO));
        int statusCode = uploadMetaResponse.getStatusCode();
        String respJsonStr = new String(uploadMetaResponse.getBody(), Charset.forName(CHARSET_UTF8));
        JSONObject respJson = JSONObject.parseObject(respJsonStr);
        if (statusCode != RESPONSE_CODE_SUCCESS) {
            if (respJson != null) {
                return ResultDTO.getResult(null,
                        new ResultCode(respJson.getInteger(GATEWAY_KEY_CODE), respJson.getString(GATEWAY_KEY_MESSAGE), respJson.getString(GATEWAY_KEY_LOCALIZEDMSG)));
            } else {
                return ResultDTO.getResult(null, ResultCodes.SERVER_ERROR);
            }
        }
        if (RESPONSE_CODE_SUCCESS != respJson.getInteger(GATEWAY_KEY_CODE)) {
            return ResultDTO.getResult(respJson.get(GATEWAY_KEY_DATA),
                    new ResultCode(respJson.getInteger(GATEWAY_KEY_CODE), respJson.getString(GATEWAY_KEY_MESSAGE), respJson.getString(GATEWAY_KEY_LOCALIZEDMSG)));
        }
        JSONObject data = respJson.getJSONObject(GATEWAY_KEY_DATA);
        data.put(OSS_GET_UPLOAD_META_KEY_FILENAMELIST, fileNameList);
        return ResultDTO.getResult(data);
    }
}

