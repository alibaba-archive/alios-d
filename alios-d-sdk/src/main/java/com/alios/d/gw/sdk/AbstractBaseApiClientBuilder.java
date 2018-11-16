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
import com.alibaba.cloudapi.sdk.core.BaseApiClientBuilder;
import com.alibaba.cloudapi.sdk.core.model.BuilderParams;

/**
 * AbstractBaseApiClientBuilder
 *
 * @author aifeng
 * @version Create on 1/19/18 2:12 PM
 */
public abstract class AbstractBaseApiClientBuilder<Subclass extends BaseApiClientBuilder, TypeToBuild extends AbstractApiGwClient> extends BaseApiClientBuilder {
    private String stage;
    private String groupHost;

    public AbstractBaseApiClientBuilder<Subclass, TypeToBuild> stage(String stage) {
        this.stage = stage;
        return this;
    }

    public BaseApiClientBuilder<Subclass, TypeToBuild> groupHost(String groupHost) {
        this.groupHost = groupHost;
        return this;
    }

    @Override
    protected BaseApiClient build(BuilderParams params) {
        return build(params,stage,groupHost);
    }

    protected abstract AbstractApiGwClient build(BuilderParams params, String stage, String groupHost);
}
