package com.alios.d.gw.demo;

import com.alibaba.cloudapi.sdk.core.model.ApiResponse;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alios.d.gw.sdk.ApiTransferParamDTO;

/**
 * 云云对接demo程序
 */
public class YydjApiDemo {
    private static void printResponse(ApiResponse response) {
        try {
            System.out.println("response code = " + response.getStatusCode());
            System.out.println("response content = " + new String(response.getBody(), "utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 本main方法可以脱离容器直接执行调用，仅测试使用。
     * 正常程序调用不应该像下面这样调用，而是应该通过SyncApiClientConfiguration初始化一个单例client bean，再注入调用
     * @param args
     */
    public static void main(String[] args) {
        ApiGwClient syncClient = ApiGwClient.newBuilder()
                .stage("release")
                .groupHost("api host")  //api网关host
                .appKey("your appkey")
                .appSecret("your appsecret")
                .build();
        ApiTransferParamDTO apiTransferParamDTO = new ApiTransferParamDTO();
        /**
         * 云云对接的参数形式,除了data中的字段是用户自定义以外，其他的参数都是必传参数
         * [{
         *     "tenantId":"testvend", //租户id
         * 	"eventId": 1,//事件id
         * 	"dataId":"流水号ID，用以标识唯一",
         * 	"itemId": "数据所属主体ID，例如车的vin号，手机的imei号等",
         * 	"serverTimestamp": "1519696866000", //云云对接服务端时间
         * 	"clientTimestamp": "1519696866001", //云云对接客户端时间
         * 	"reissueCount": "重传次数",
         * 	"data": {//用户的自定义参数
         * 		"Driver_Door_Lock_Status": "1",
         * 		"Passenger_Door_Lock_Status": "0",
         * 		"RL_Door_Lock_Status": "0",
         * 		"RR_Door_Lock_Status": "0",
         * 		"Locking_Control_Fail_reason": ""
         *        }
         * }]
         */
        JSONObject jsonData = new JSONObject();
        jsonData.put("tenantId", "testvend");
        jsonData.put("eventId", 86);
        jsonData.put("dataId", "dataId");
        jsonData.put("itemId", "itemId");
        jsonData.put("serverTimestamp", 1519696866000L);
        jsonData.put("clientTimestamp", 1519696866000L);
        jsonData.put("reissueCount", 0);
        JSONObject userData = new JSONObject();
        userData.put("key1", "value1");
        userData.put("key2", "value2");
        jsonData.put("data", userData);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(jsonData);
        apiTransferParamDTO.addParam("jsonData",JSONObject.toJSONString(jsonArray));
        ApiResponse response = syncClient.云云对接例子(JSONObject.toJSONString(apiTransferParamDTO));
        printResponse(response);
    }
}
