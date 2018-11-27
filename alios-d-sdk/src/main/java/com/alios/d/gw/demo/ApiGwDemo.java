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

    public static void main(String[] args) {
        System.out.println(new ApiGwDemo().apiRequest());
    }

    /**
     * 获取mqtt token
     * @return
     */
    public JSONObject apiRequest() {
        ApiGwClient syncClient = ApiGwClient.newBuilder()
                .stage("release")
                .groupHost("api host")  //api网关host
                .appKey("your appKey")
                .appSecret("your appSecret")
                .build();
        ApiTransferParamDTO apiTransferParamDTO = new ApiTransferParamDTO();
        apiTransferParamDTO.addParam("param1","value1");
        apiTransferParamDTO.addParam("param2","value2");
        apiTransferParamDTO.addParam("param3",3);
        ApiResponse response = syncClient.doApiRequest(JSONObject.toJSONString(apiTransferParamDTO));
        if (response != null && response.getStatusCode() == 200) {
            String body = new String(response.getBody());
            JSONObject bodyJson = JSONObject.parseObject(body);
            int code = bodyJson.getInteger("code");
            if (code == 200) {
                String dataStr = bodyJson.getString("data");
                return JSONObject.parseObject(dataStr);
            }
        }
        return null;
    }
}
