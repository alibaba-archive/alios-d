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
package com.alios.d.gw.demo;

import com.alibaba.fastjson.JSONObject;
import com.alios.d.gw.sdk.FileUploadClient;
import com.alios.d.gw.sdk.dto.FileUploadDTO;

/**
 * author autoCreate
 * 非结构化数据上传参考下面的demo程序
 */
public class ApiGwUploadDemo {
    public static void main(String[] args) {
        FileUploadClient fileUploadClient = FileUploadClient.newBuilder()
                .groupHost("api网关host")
                .appKey("your appkey")
                .appSecret("your appsecret")
                .build();
        FileUploadDTO fileUploadDTO = new FileUploadDTO();
        fileUploadDTO.setEventId("you eventId");
        fileUploadDTO.setDataId("这条数据的唯一标识");
        fileUploadDTO.setItemId("数据所属主体ID，例如车的vin号，手机的imei号，设备id，tenantId等");
        //数据上传的重试次数
        fileUploadDTO.setReissueCount(0);
        //客户端上报数据的时间
        fileUploadDTO.setClientTimestamp(System.currentTimeMillis());
        //服务端上报数据的时间
        fileUploadDTO.setServerTimestamp(System.currentTimeMillis());
        //客户自定义需要上报的数据，
        JSONObject yourData = new JSONObject();
        //客户自定义上报的数据中必须包含key为localFilePath的key
        yourData.put("localFilePath", "/path/to/your/file/file.jpg");
        yourData.put("key", "value");
        fileUploadDTO.setData(yourData);
        fileUploadClient.upload(fileUploadDTO);
    }
}
