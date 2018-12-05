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

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
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
     * 上传本地文件
     *
     * @param fileUploadDTO
     */
    public ResultDTO upload(FileUploadDTO fileUploadDTO, List<String> localFilePathList) {
        if (!isUploadParamLegal(fileUploadDTO, localFilePathList)) {
            return ResultDTO.getResult(null, ResultCodes.REQUEST_PARAM_ERROR);
        }
        ResultDTO uploadMetaResultDTO = getUploadMeta(fileUploadDTO, localFilePathList);
        if (!uploadMetaResultDTO.isSuccess()) {
            return uploadMetaResultDTO;
        }
        JSONObject uploadMeta = (JSONObject)uploadMetaResultDTO.getData();
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
        ResultDTO uploadResultDTO = ResultDTO.getResult(null);
        for (int i = 0;i<localFilePathList.size();i++) {
            String localFilePath = localFilePathList.get(i);
            Map<String, String> formFields = new LinkedHashMap<String, String>();

            formFields.put("key", (String) uploadMeta.getJSONArray(OSS_GET_UPLOAD_META_KEY_OSSKEY).get(i));
            formFields.put("Content-Disposition", "attachment;filename="
                    + localFilePath);
            formFields.put("OSSAccessKeyId", uploadMeta.getString(OSS_GET_UPLOAD_META_KEY_ACCESSID));
            formFields.put("policy", uploadMeta.getString(OSS_GET_UPLOAD_META_KEY_POLICY));
            formFields.put("Signature", uploadMeta.getString(OSS_GET_UPLOAD_META_KEY_SIGNATURE));
            uploadResultDTO = formUpload(formFields, uploadMeta.getString(OSS_GET_UPLOAD_META_KEY_HOST), localFilePath);
            if (!uploadResultDTO.isSuccess()) {
                break;
            }
        }
        return reportEvent(uploadResultDTO, uploadMeta, fileUploadDTO);
    }

    /**
     * 上传的参数是否合法。合法返回true，否则返回false。
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
     * @return
     */
    private ResultDTO getUploadMeta(FileUploadDTO fileUploadDTO, List<String> localFilePathList) {
        ApiTransferParamDTO paramDTO = new ApiTransferParamDTO();
        JSONObject request = new JSONObject();
        request.put(OSS_GET_UPLOAD_META_KEY_TENANTID, fileUploadDTO.getTenantId());
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

    /**
     * 上传文件到oss
     * @param formFields
     * @param host
     * @param localFilePath
     * @return
     */
    private ResultDTO formUpload(Map<String, String> formFields, String host, String localFilePath) {
        String res = "";
        HttpURLConnection conn = null;
        String boundary = "9431149156168";

        try {
            URL url = new URL(host);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            conn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + boundary);
            OutputStream out = new DataOutputStream(conn.getOutputStream());

            // text
            if (formFields != null) {
                StringBuffer strBuf = new StringBuffer();
                Iterator<Map.Entry<String, String>> iter = formFields.entrySet().iterator();
                int i = 0;

                while (iter.hasNext()) {
                    Map.Entry<String, String> entry = iter.next();
                    String inputName = entry.getKey();
                    String inputValue = entry.getValue();

                    if (inputValue == null) {
                        continue;
                    }

                    if (i == 0) {
                        strBuf.append("--").append(boundary).append("\r\n");
                        strBuf.append("Content-Disposition: form-data; name=\""
                                + inputName + "\"\r\n\r\n");
                        strBuf.append(inputValue);
                    } else {
                        strBuf.append("\r\n").append("--").append(boundary).append("\r\n");
                        strBuf.append("Content-Disposition: form-data; name=\""
                                + inputName + "\"\r\n\r\n");
                        strBuf.append(inputValue);
                    }

                    i++;
                }
                out.write(strBuf.toString().getBytes());
            }

            // file
            File file = new File(localFilePath);
            String filename = file.getName();
            String contentType = new MimetypesFileTypeMap().getContentType(file);
            if (contentType == null || contentType.equals("")) {
                contentType = "application/octet-stream";
            }

            StringBuffer strBuf = new StringBuffer();
            strBuf.append("\r\n").append("--").append(boundary)
                    .append("\r\n");
            strBuf.append("Content-Disposition: form-data; name=\"file\"; "
                    + "filename=\"" + filename + "\"\r\n");
            strBuf.append("Content-Type: " + contentType + "\r\n\r\n");

            out.write(strBuf.toString().getBytes());

            DataInputStream in = new DataInputStream(new FileInputStream(file));
            int bytes = 0;
            byte[] bufferOut = new byte[1024];
            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
            in.close();

            byte[] endData = ("\r\n--" + boundary + "--\r\n").getBytes();
            out.write(endData);
            out.flush();
            out.close();

            // Gets the file data
            strBuf = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                strBuf.append(line).append("\n");
            }
            res = strBuf.toString();
            reader.close();
            reader = null;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getResult(false, new ResultCode(601, "upload to oss error", "上传文件到oss失败"));
        } finally {
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
        }
        return ResultDTO.getResult(true);
    }

}

