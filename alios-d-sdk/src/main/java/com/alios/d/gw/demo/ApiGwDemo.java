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
import com.alibaba.fastjson.JSONObject;
import com.alios.d.gw.sdk.ApiTransferParamDTO;

/**
 * ApiGwDemo
 * 直接调用api网关，参考这个demo程序
 * @author aifeng
 * @version Create on 1/17/18 11:16 AM
 */

public class ApiGwDemo {

    static int count = 0;
    static int hsfCount = 0;

    public static void main(String[] args) {
        ApiGwDemo demo = new ApiGwDemo();
        for (int i = 0;i<1;i++) {
            System.out.println(demo.apiRequest());
        }

        System.out.println("count: " + count);
        System.out.println("count: " + hsfCount);
    }

    /**
     * 获取mqtt token
     * @return
     */
    public String apiRequest() {
        ApiGwClient syncClient = ApiGwClient.newBuilder()
                .stage("release")
                .groupHost("alios-d-gw-qa.aliyuncs.com")  //api网关host
                .appKey("xxxx") //your appKey
                .appSecret("xxxxx") //your appSecret
                .build();
        ApiTransferParamDTO apiTransferParamDTO = new ApiTransferParamDTO();
        JSONObject gaodeHighwayRealtimeQueryDTO = new JSONObject();
        gaodeHighwayRealtimeQueryDTO.put("hid","G4201_1");
        gaodeHighwayRealtimeQueryDTO.put("startKmPileId","k0");
        gaodeHighwayRealtimeQueryDTO.put("endKmPileId","k85");
        gaodeHighwayRealtimeQueryDTO.put("toPage",1);
        gaodeHighwayRealtimeQueryDTO.put("perPageSize",1);

        apiTransferParamDTO.addParam("gaodeHighwayRealtimeQueryDTO", gaodeHighwayRealtimeQueryDTO);
        ApiResponse response = syncClient.doApiRequest("business.gaodeRealtimeDataFacade.queryEvent", JSONObject.toJSONString(apiTransferParamDTO));
        if (response != null && response.getStatusCode() == 200) {
            count ++;
            String body = new String(response.getBody());
            System.out.println("body: " + body);
            JSONObject bodyJson = JSONObject.parseObject(body);
            int code = bodyJson.getInteger("code");
            if (code == 200) {
                hsfCount++;
                String dataStr = bodyJson.getString("data");
                return dataStr;
            } else {
                return bodyJson.toJSONString();
            }
        }
        return JSON.toJSONString(response);
    }
}
