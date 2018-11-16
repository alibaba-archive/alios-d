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

import com.alibaba.cloudapi.sdk.core.annotation.ThreadSafe;
import com.alibaba.cloudapi.sdk.core.model.ApiCallBack;
import com.alibaba.cloudapi.sdk.core.model.ApiResponse;
import com.alibaba.cloudapi.sdk.core.model.BuilderParams;
import com.alibaba.fastjson.JSONObject;
import com.alios.d.gw.sdk.AbstractApiGwClient;
import com.alios.d.gw.sdk.AbstractBaseApiClientBuilder;
import com.alios.d.gw.sdk.ApiTransferParamDTO;

import java.util.concurrent.ConcurrentHashMap;

@ThreadSafe
public final class ApiGwClient extends AbstractApiGwClient {
    /**
     * apiGwClient本地缓存，为了防止重复创建apiGwClient造成没有必要的开销
     */
    private static final ConcurrentHashMap<String, ApiGwClient> apiGwClientCache = new ConcurrentHashMap<>();
    private static final Object lock = new Object();

    public ApiGwClient(BuilderParams params, String stage, String groupHost) {
        super(params, stage, groupHost);
    }

    @ThreadSafe
    public static class Builder extends AbstractBaseApiClientBuilder<Builder, ApiGwClient> {

        @Override
        protected ApiGwClient build(BuilderParams params, String stage, String groupHost) {
            String cacheKey = params.getAppKey() + params.getAppSecret() + stage + groupHost;
            //如果缓存里没有cacheKey对应的apiGwClient，则创建一个，否则直接返回缓存中的apiGwClient
            if (!apiGwClientCache.contains(cacheKey)) {
                synchronized (lock) {
                    if (!apiGwClientCache.contains(cacheKey)) {
                        apiGwClientCache.put(cacheKey, new ApiGwClient(params, stage, groupHost));
                    }
                }
            }
            return apiGwClientCache.get(cacheKey);
        }
    }

    public static Builder newBuilder(){
        return new Builder();
    }

    public static ApiGwClient getInstance(){
        return getApiClassInstance(ApiGwClient.class);
    }

    /**
     * 同步执行示例，注意_body必须是"{"param":objxxxxx}"这样的结构，可使用ApiTransferParamDTO封装
     * @param _body
     * @return
     */
    public ApiResponse 同步方法例子(String _body) {
        String _apiPath = "cloud.mqtt.getConfig";

        return syncInvokeWrapper(_apiPath, _body);
    }

    /**
     * 同步执行示例
     * @param paramDTO
     * @return
     */
    public ApiResponse 同步方法例子(ApiTransferParamDTO paramDTO) {
        String _apiPath = "aifeng.user.getJobInstanceLog2";

        return syncInvokeWrapper(_apiPath, JSONObject.toJSONString(paramDTO));
    }

    /**
     * 异步执行示例
     * @param _body
     * @param _callBack
     */
    public void 异步方法例子(String _body, ApiCallBack _callBack) {
        String _apiPath = "aifeng.user.getJobInstanceLog2";

        asyncInvokeWrapper(_apiPath, _body, _callBack);
    }


}

