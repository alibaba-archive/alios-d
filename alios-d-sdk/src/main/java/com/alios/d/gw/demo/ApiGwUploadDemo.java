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
import com.alios.d.gw.sdk.dto.ResultDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * author autoCreate
 * 非结构化数据上传参考下面的demo程序
 */
public class ApiGwUploadDemo {
    public static void main(String[] args) {
        FileUploadClient fileUploadClient = FileUploadClient.newBuilder()
                .stage("release")
                .groupHost("api host")//api网关host
                .appKey("appKey")
                .appSecret("appSecret")
                .build();
        FileUploadDTO fileUploadDTO = new FileUploadDTO();
        fileUploadDTO.setTenantId("testvend");
        fileUploadDTO.setEventId(86);
        fileUploadDTO.setDataId("11");
        fileUploadDTO.setItemId("sdf");
        //数据上传的重试次数
        fileUploadDTO.setReissueCount(0);
        //客户端上报数据的时间
        fileUploadDTO.setClientTimestamp(System.currentTimeMillis());
        //服务端上报数据的时间
        fileUploadDTO.setServerTimestamp(System.currentTimeMillis());
        //客户自定义需要上报的数据，
        JSONObject yourData = new JSONObject();
        //客户自定义上报的数据
        yourData.put("key", "value");
        fileUploadDTO.setData(yourData);
        List<String> localFilePathList = new ArrayList<>();
        String localFilePath = "/Users/zhangchun/Downloads/test/d.txt";
        String localFilePath1 = "/Users/zhangchun/Downloads/test/a.txt";
        String localFilePath2 = "/Users/zhangchun/Downloads/test/b.txt";
        String localFilePath3 = "/Users/zhangchun/Downloads/test/c.txt";
        localFilePathList.add(localFilePath);
        localFilePathList.add(localFilePath1);
        localFilePathList.add(localFilePath2);
        localFilePathList.add(localFilePath3);
        ResultDTO resultDTO = fileUploadClient.upload(fileUploadDTO, localFilePathList);
        System.out.println("result:" + JSONObject.toJSONString(resultDTO));
    }
}
