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
package com.alios.d.gw.sdk;

import com.alibaba.cloudapi.sdk.core.BaseApiClient;
import com.alibaba.cloudapi.sdk.core.annotation.ThreadSafe;
import com.alibaba.cloudapi.sdk.core.enums.Method;
import com.alibaba.cloudapi.sdk.core.enums.Scheme;
import com.alibaba.cloudapi.sdk.core.exception.SdkClientException;
import com.alibaba.cloudapi.sdk.core.model.ApiCallBack;
import com.alibaba.cloudapi.sdk.core.model.ApiRequest;
import com.alibaba.cloudapi.sdk.core.model.ApiResponse;
import com.alibaba.cloudapi.sdk.core.model.BuilderParams;
import com.alios.d.gw.sdk.util.ApiGwPathUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;

@ThreadSafe
public abstract class AbstractApiGwClient extends BaseApiClient {

    private String groupHost;
    private String stage;

    public AbstractApiGwClient(BuilderParams params, String stage, String groupHost) {
        super(params);
        this.groupHost = groupHost;
        this.stage = stage;
    }

    /**
     * 同步调用，务必使用此接口而不能使用父类中的syncInvoke方法
     * @return
     */
    public ApiResponse syncInvokeWrapper(String _apiPath, String _body){
        //组装path。举例：uc.user.getById 转换为 api网关能识别的 /alios/uc/user/getById
        String path = ApiGwPathUtil.convert2ApiGw(_apiPath);
        byte[] bodyBytes = new byte[0];
        try {
            bodyBytes = StringUtils.isNotBlank(_body) ? _body.getBytes("utf-8") : "".getBytes();
        } catch (UnsupportedEncodingException e) {
            throw new SdkClientException("bytes encode must be utf8");
        }

        ApiRequest _apiRequest = new ApiRequest(Scheme.HTTP, Method.POST_BODY, groupHost, path, bodyBytes);

        //api网关stage默认为线上,比较危险，特在此转换为默认test
        stageInit(_apiRequest);

        return syncInvoke(_apiRequest);
    }

    /**
     * 异步调用，务必使用此接口而不能使用父类中的syncInvoke方法
     * @return
     */
    public void asyncInvokeWrapper(String _apiPath, String _body, ApiCallBack _callBack){
        //组装path。举例：uc.user.getById 转换为 api网关能识别的 /alios/uc/user/getById
        String path = ApiGwPathUtil.convert2ApiGw(_apiPath);

        byte[] bodyBytes = new byte[0];
        try {
            bodyBytes = StringUtils.isNotBlank(_body) ? _body.getBytes("utf-8") : "".getBytes();
        } catch (UnsupportedEncodingException e) {
            throw new SdkClientException("bytes encode must be utf8");
        }

        ApiRequest _apiRequest = new ApiRequest(Scheme.HTTP, Method.POST_BODY, groupHost, path, bodyBytes);

        //api网关stage默认为线上,比较危险，特在此转换为默认test
        stageInit(_apiRequest);

        asyncInvoke(_apiRequest,_callBack);
    }


    /**
     * api网关stage默认为线上,比较危险，特在此转换为默认test
     * @param _apiRequest
     */
    private void stageInit(ApiRequest _apiRequest){
        //默认stage为dev
        if(StringUtils.isBlank(stage) || !(stage.toLowerCase().equals("release") || stage.toLowerCase().equals("pre"))){
            stage = "test";
        }
        _apiRequest.getHeaders().put("X-Ca-Stage",stage);
    }


}

