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
                .appKey("appKey") //你的appKey
                .appSecret("appSecret") //你的appSecret
                .build();
        FileUploadDTO fileUploadDTO = new FileUploadDTO();
        //在知点中定义的事件id
        fileUploadDTO.setEventId(10262);
        //流水号ID，用以标识唯一
        fileUploadDTO.setDataId("11");
        //数据所属主体ID，例如车的vin号，手机的imei号等
        fileUploadDTO.setItemId("sdf");
        //数据上传的重试次数
        fileUploadDTO.setReissueCount(0);
        //客户端上报的时间
        fileUploadDTO.setClientTimestamp(System.currentTimeMillis());
        //服务端上报的时间
        fileUploadDTO.setServerTimestamp(System.currentTimeMillis());
        //自定义的上报的字段
        JSONObject yourData = new JSONObject();
        yourData.put("leaveTime", 1543047652930L);
        yourData.put("eventID", "f24164374162a711c1392b51b7e82347");
        yourData.put("lng", "113.67");
        yourData.put("level", "llllllll");
        yourData.put("travelDirection", "SW");
        yourData.put("startKmPileId", "xxxxx");
        yourData.put("memo", "mmmmmm");
        yourData.put("roadid", "eeeeeeeeeee");
        yourData.put("confirmTime", 1543047652930L);
        yourData.put("pathid", "ppppppp");
        yourData.put("extAttr", "{}");
        yourData.put("typeCode", "1");
        yourData.put("eventCode", "1001");
        yourData.put("eventTitle", "测试封路");
        yourData.put("entryTime", 1543047652930L);
        yourData.put("roadType", "wwwwwww");
        yourData.put("cameraId", "51660103041310833423");
        yourData.put("confirmStatus", "ccccccc");
        yourData.put("cameraDirection", "SW");
        yourData.put("lat", "42.99");
        yourData.put("cameraName", "qqqqq");
        fileUploadDTO.setData(yourData);
        //需要上传的本地文件pathList
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
