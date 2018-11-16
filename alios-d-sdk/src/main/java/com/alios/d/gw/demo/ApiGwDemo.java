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
import com.alibaba.fastjson.JSONObject;
import com.alios.d.gw.sdk.ApiTransferParamDTO;

/**
 * ApiGwDemo
 * 直接调用api网关，参考这个demo程序
 * @author aifeng
 * @version Create on 1/17/18 11:16 AM
 */

public class ApiGwDemo {

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
                .groupHost("api网关host")
                .appKey("your appkey")
                .appSecret("your appsecret")
                .build();
        ApiTransferParamDTO apiTransferParamDTO = new ApiTransferParamDTO();
        apiTransferParamDTO.addParam("paramKey","paramValue");
        ApiResponse response = syncClient.同步方法例子(JSONObject.toJSONString(apiTransferParamDTO));
        printResponse(response);
    }
}
