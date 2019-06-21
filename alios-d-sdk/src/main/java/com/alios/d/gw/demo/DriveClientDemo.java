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

import com.alibaba.cloudapi.sdk.core.model.ApiResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alios.d.gw.sdk.ApiTransferParamDTO;

/**
 * DriveClientDemo
 * 直接调用api网关，参考这个demo程序
 * @author aifeng
 * @version Create on 1/17/18 11:16 AM
 */

public class DriveClientDemo {

    /**
     * client调用示例
     * @param args
     */
    public static void main(String[] args) {
        DriveClientDemo demo = new DriveClientDemo();
        System.out.println(demo.tripList());
        System.out.println(demo.tripGet());
        System.out.println(demo.last100Km());
    }

    /**
     * 驾驶行程列表
     * @return
     */
    public String tripList() {
        //组装请求参数
        /*
         * 参数为以下形式的json：
         *
         * {
         *     "params": {
         *         "requestDTO": {
         *             "requestContext": {
         *                 "account": "18059142963",  // 用户名，没有可以为空
         *                  "accountId": "4398046511567",  // 用户ID，没有可以为空
         *                  "channelId": "APP", // 静态值，[APP,CLOUD]可选
         *                  "requestId": "1528102294320", //客户端发送的请求ID, 方便debug
         *                  "tenantId": "jinkang",// 租户ID
         *                  "vin": "LVFAG7A31HK000026"// 车辆唯一标识
         *             }
         *         },
         *         "requestList": [
         *              {
         *                  "requestBody": "{\"startTime\":1559269935000,\"endTime\":1559269935444}",
         *                  "requestType": "trip_list"
         *              }
         *         ]
         *     }
         * }
         *
         */
        ApiTransferParamDTO apiTransferParamDTO = new ApiTransferParamDTO();
        JSONObject requestDTO = new JSONObject();
        JSONObject requestContext = new JSONObject();
        requestContext.put("account", "18059142963");
        requestContext.put("accountId", "4398046511567");
        requestContext.put("channelId", "APP");
        requestContext.put("requestId", "1528102294320");
        requestContext.put("tenantId", "vyingshi");
        requestContext.put("vin", "LVFAG7A31HK000026");
        requestDTO.put("requestContext", requestContext);
        JSONArray requestList = new JSONArray();
        JSONObject carCommonRequestDTO = new JSONObject();
        carCommonRequestDTO.put("requestType", "trip_list");
        carCommonRequestDTO.put("requestBody", "{'startTime':1559269935000,'endTime':1559269935444}");
        requestList.add(carCommonRequestDTO);
        requestDTO.put("requestList", requestList);
        apiTransferParamDTO.addParam("requestDTO", requestDTO);
        //发起请求
        return sendReq(JSONObject.toJSONString(apiTransferParamDTO));
    }

    /**
     * 获取指定驾驶行程明细
     * @return
     */
    public String tripGet() {
        //组装请求参数
        /*
         * 参数为以下形式的json：
         *
         * {
         *     "params": {
         *         "requestDTO": {
         *             "requestContext": {
         *                 "account": "18059142963",  // 用户名，没有可以为空
         *                  "accountId": "4398046511567",  // 用户ID，没有可以为空
         *                  "channelId": "APP",  // 静态值，[APP,CLOUD]可选
         *                  "requestId": "1528102294320",  //客户端发送的请求ID, 方便debug
         *                  "tenantId": "jinkang",  // 租户ID
         *                  "vin": "LVFAG7A31HK000026"  // 车辆唯一标识
         *             }
         *         },
         *         "requestList": [
         *              {
         *                  "requestBody": "{\"routeId\":1333}",
         *                  "requestType": "trip_get"
         *              }
         *         ]
         *     }
         * }
         *
         */
        ApiTransferParamDTO apiTransferParamDTO = new ApiTransferParamDTO();
        JSONObject requestDTO = new JSONObject();
        JSONObject requestContext = new JSONObject();
        requestContext.put("account", "18059142963");
        requestContext.put("accountId", "4398046511567");
        requestContext.put("channelId", "APP");
        requestContext.put("requestId", "1528102294320");
        requestContext.put("tenantId", "vyingshi");
        requestContext.put("vin", "LVFAG7A31HK000026");
        requestDTO.put("requestContext", requestContext);
        JSONArray requestList = new JSONArray();
        JSONObject carCommonRequestDTO = new JSONObject();
        carCommonRequestDTO.put("requestType", "trip_get");
        carCommonRequestDTO.put("requestBody", "{'routeId':1333}");
        requestList.add(carCommonRequestDTO);
        requestDTO.put("requestList", requestList);
        apiTransferParamDTO.addParam("requestDTO", requestDTO);
        //发起请求
        return sendReq(JSONObject.toJSONString(apiTransferParamDTO));
    }

    /**
     * 总体驾驶评分
     * 最近100公里的驾驶行为评分
     * @return
     */
    public String last100Km() {
        //组装请求参数
        /*
         * 参数为以下形式的json：
         *
         * {
         *     "params": {
         *         "requestDTO": {
         *             "requestContext": {
         *                 "account": "18059142963",  // 用户名，没有可以为空
         *                  "accountId": "4398046511567",  // 用户ID，没有可以为空
         *                  "channelId": "APP",  // 静态值，[APP,CLOUD]可选
         *                  "requestId": "1528102294320",  //客户端发送的请求ID, 方便debug
         *                  "tenantId": "jinkang",  // 租户ID
         *                  "vin": "LVFAG7A31HK000026"  // 车辆唯一标识
         *             }
         *         },
         *         "requestList": [
         *              {
         *                  "requestBody": "{\"analysisType\":\"last_1000km\"}",
         *                  "requestType": "analysis_drive"
         *              }
         *         ]
         *     }
         * }
         *
         */
        ApiTransferParamDTO apiTransferParamDTO = new ApiTransferParamDTO();
        JSONObject requestDTO = new JSONObject();
        JSONObject requestContext = new JSONObject();
        requestContext.put("account", "18059142963");
        requestContext.put("accountId", "4398046511567");
        requestContext.put("channelId", "APP");
        requestContext.put("requestId", "1528102294320");
        requestContext.put("tenantId", "vyingshi");
        requestContext.put("vin", "LVFAG7A31HK000026");
        requestDTO.put("requestContext", requestContext);
        JSONArray requestList = new JSONArray();
        JSONObject carCommonRequestDTO = new JSONObject();
        carCommonRequestDTO.put("requestType", "analysis_drive");
        carCommonRequestDTO.put("requestBody", "{'analysisType':'last_1000km'}");
        requestList.add(carCommonRequestDTO);
        requestDTO.put("requestList", requestList);
        apiTransferParamDTO.addParam("requestDTO", requestDTO);
        //发起请求
        return sendReq(JSONObject.toJSONString(apiTransferParamDTO));
    }

    /**
     * 发送请求
     * @param paramJson
     * @return
     */
    private String sendReq(String paramJson) {
        ApiGwClient syncClient = ApiGwClient.newBuilder()
                .stage("release")
                .groupHost("alios-d-gw-gongban-test.aliyuncs.com")  //api网关host
                .appKey("xxx") //your appkey
                .appSecret("xxx") //your appSecret
                .build();
        //请求的apiPath
        String apiPath = "carservice.client.call";
        //执行api请求并获取结果
        ApiResponse response = syncClient.doApiRequest(apiPath, paramJson);
        return getResponseStr(response);
    }

    /**
     * 解析返回的结果
     * @param response
     * @return
     */
    private String getResponseStr(ApiResponse response) {
        if (response != null && response.getStatusCode() == 200) {
            String body = new String(response.getBody());
            JSONObject bodyJson = JSONObject.parseObject(body);
            int code = bodyJson.getInteger("code");
            if (code == 200) {
                return bodyJson.getString("data");
            } else {
                return bodyJson.toJSONString();
            }
        }
        return JSON.toJSONString(response);
    }
}
