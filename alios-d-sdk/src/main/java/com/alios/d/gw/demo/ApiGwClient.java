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
import com.alibaba.cloudapi.sdk.core.model.ApiResponse;
import com.alibaba.cloudapi.sdk.core.model.BuilderParams;
import com.alios.d.gw.sdk.AbstractApiGwClient;
import com.alios.d.gw.sdk.AbstractBaseApiClientBuilder;

import java.util.concurrent.ConcurrentHashMap;

@ThreadSafe
public final class ApiGwClient extends AbstractApiGwClient {
    /**
     * apiGwClient本地缓存，为了防止重复创建apiGwClient造成没有必要的开销
     */
    private static final ConcurrentHashMap<String, ApiGwClient> API_GW_CLIENT_CACHE = new ConcurrentHashMap<>();
    private static final Object LOCK = new Object();

    public ApiGwClient(BuilderParams params, String stage, String groupHost) {
        super(params, stage, groupHost);
    }

    @ThreadSafe
    public static class Builder extends AbstractBaseApiClientBuilder<Builder, ApiGwClient> {

        @Override
        protected ApiGwClient build(BuilderParams params, String stage, String groupHost) {
            String cacheKey = params.getAppKey() + params.getAppSecret() + stage + groupHost;
            //如果缓存里没有cacheKey对应的apiGwClient，则创建一个，否则直接返回缓存中的apiGwClient
            if (!API_GW_CLIENT_CACHE.contains(cacheKey)) {
                synchronized (LOCK) {
                    if (!API_GW_CLIENT_CACHE.contains(cacheKey)) {
                        API_GW_CLIENT_CACHE.put(cacheKey, new ApiGwClient(params, stage, groupHost));
                    }
                }
            }
            return API_GW_CLIENT_CACHE.get(cacheKey);
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static ApiGwClient getInstance() {
        return getApiClassInstance(ApiGwClient.class);
    }

    public ApiResponse doApiRequest(String _body) {
        String _apiPath = "gaode.api.service";

        return syncInvokeWrapper(_apiPath, _body);
    }

    /**
     * 云云对接同步执行示例
     * 云云对接的apiPath为dataconnectorcloud.data.commonPushBatch
     *
     * @param _body
     * @return
     */
    public ApiResponse commonPushBatch(String _body) {
        String _apiPath = "dataconnectorcloud.data.commonPushBatch";

        return syncInvokeWrapper(_apiPath, _body);
    }



}

