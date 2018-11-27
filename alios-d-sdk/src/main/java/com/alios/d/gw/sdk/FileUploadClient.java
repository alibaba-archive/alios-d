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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ThreadSafe
public final class FileUploadClient extends AbstractApiGwClient {

    private static final ConcurrentHashMap<String, FileUploadClient> apiGwClientCache = new ConcurrentHashMap<>();
    private static final Object lock = new Object();

    public FileUploadClient(BuilderParams params, String stage, String groupHost) {
        super(params, stage, groupHost);
    }

    @ThreadSafe
    public static class Builder extends AbstractBaseApiClientBuilder<Builder, FileUploadClient> {

        @Override
        protected FileUploadClient build(BuilderParams params, String stage, String groupHost) {
            String cacheKey = params.getAppKey() + params.getAppSecret() + stage + groupHost;
            if (!apiGwClientCache.contains(cacheKey)) {
                synchronized (lock) {
                    if (!apiGwClientCache.contains(cacheKey)) {
                        FileUploadClient fileUploadClient = new FileUploadClient(params, stage, groupHost);
                        apiGwClientCache.put(cacheKey, fileUploadClient);
                    }
                }
            }
            return apiGwClientCache.get(cacheKey);
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
    public ResultDTO upload(FileUploadDTO fileUploadDTO, String localFilePath) {
        if (fileUploadDTO == null || fileUploadDTO.getEventId() == null || fileUploadDTO.getItemId() == null
                || fileUploadDTO.getData() == null || fileUploadDTO.getTenantId() == null || fileUploadDTO.getDataId() == null
                || fileUploadDTO.getServerTimestamp() == null || fileUploadDTO.getClientTimestamp() == null
                || fileUploadDTO.getReissueCount() == null || localFilePath == null) {
            return ResultDTO.getResult(null, ResultCodes.REQUEST_PARAM_ERROR);
        }
        ResultDTO<JSONObject> uploadMetaResultDTO = getUploadMeta(fileUploadDTO, localFilePath);
        if (!uploadMetaResultDTO.isSuccess()) {
            return uploadMetaResultDTO;
        }
        JSONObject uploadMeta = uploadMetaResultDTO.getData();
        Map<String, String> formFields = new LinkedHashMap<String, String>();

        formFields.put("key", uploadMeta.getString("dir") + uploadMeta.getString("uploadFileName"));
        formFields.put("Content-Disposition", "attachment;filename="
                + localFilePath);
        formFields.put("OSSAccessKeyId", uploadMeta.getString("accessId"));
        formFields.put("policy", uploadMeta.getString("policy"));
        formFields.put("Signature", uploadMeta.getString("signature"));
        ResultDTO uploadResultDTO = formUpload((String)uploadMeta.get("host"), formFields, localFilePath);
        return reportEvent(uploadResultDTO, uploadMeta, fileUploadDTO);
    }

    /**
     * 回写事件
     */
    private ResultDTO reportEvent(ResultDTO uploadResultDTO, JSONObject uploadMeta, FileUploadDTO fileUploadDTO) {
        String apiPath = "dataconnectorcloud.data.pushUploadFileResult";
        ApiTransferParamDTO paramDTO = new ApiTransferParamDTO();
        JSONObject cloudSdkJson = new JSONObject();
        cloudSdkJson.put("SinkExtendKey", uploadMeta.getString("sinkKey"));
        cloudSdkJson.put("OssUrl", uploadMeta.getString("dir") + uploadMeta.getString("uploadFileName"));
        cloudSdkJson.put("FileName", uploadMeta.getString("uploadFileName"));
        cloudSdkJson.put("UploadResult", uploadResultDTO.isSuccess());
        if (!uploadResultDTO.isSuccess()) {
            cloudSdkJson.put("UploadMsg", uploadResultDTO.getErrorMsg());
        }
        cloudSdkJson.put("OssTimeStamp", System.currentTimeMillis());
        paramDTO.addParam("cloudSdkJson", cloudSdkJson);

        JSONObject businessJson = JSONObject.parseObject(JSONObject.toJSONString(fileUploadDTO));
        paramDTO.addParam("businessJson", businessJson);

        ApiResponse uploadMetaResponse = syncInvokeWrapper(apiPath, JSONObject.toJSONString(paramDTO));
        int statusCode = uploadMetaResponse.getStatusCode();
        String respJsonStr = new String(uploadMetaResponse.getBody(), Charset.forName("utf-8"));
        JSONObject respJson = JSONObject.parseObject(respJsonStr);
        if (statusCode != 200) {
            if (respJson != null) {
                return ResultDTO.getResult(null,
                        new ResultCode(602, respJson.getString("message"), respJson.getString("localizedMsg")));
            } else {
                return ResultDTO.getResult(null, ResultCodes.SERVER_ERROR);
            }
        }
        return ResultDTO.getResult(null);
    }

    /**
     * 获取上传oss的upload信息
     * @return
     */
    private ResultDTO<JSONObject> getUploadMeta(FileUploadDTO fileUploadDTO, String localFilePath) {
        String apiPath = "dataconfig.uploadurl.get";
        ApiTransferParamDTO paramDTO = new ApiTransferParamDTO();
        JSONObject request = new JSONObject();
        request.put("tenantId", fileUploadDTO.getTenantId());
        request.put("deviceId", fileUploadDTO.getItemId());
        request.put("eventId", fileUploadDTO.getEventId());
        String fileName = localFilePath.substring(localFilePath.lastIndexOf(File.separator) + 1);
        request.put("fileName", fileName);
        paramDTO.addParam("request", request);
        ApiResponse uploadMetaResponse = syncInvokeWrapper(apiPath, JSONObject.toJSONString(paramDTO));
        int statusCode = uploadMetaResponse.getStatusCode();
        String respJsonStr = new String(uploadMetaResponse.getBody(), Charset.forName("utf-8"));
        JSONObject respJson = JSONObject.parseObject(respJsonStr);
        if (statusCode != 200) {
            return ResultDTO.getResult(null,
                    new ResultCode(600, respJson.getString("message"), respJson.getString("localizedMsg")));
        }
        return ResultDTO.getResult(respJson.getJSONObject("data"));
    }

    /**
     * 上传文件到oss
     * @param urlStr
     * @param formFields
     * @param localFile
     * @return
     */
    private ResultDTO formUpload(String urlStr, Map<String, String> formFields, String localFile) {
        String res = "";
        HttpURLConnection conn = null;
        String boundary = "9431149156168";

        try {
            URL url = new URL(urlStr);
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
            File file = new File(localFile);
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

