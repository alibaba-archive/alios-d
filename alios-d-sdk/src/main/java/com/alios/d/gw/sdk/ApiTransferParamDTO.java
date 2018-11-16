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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * ApiTransferParamDTO
 * 传输给网关的参数
 *
 * @author aifeng
 * @version Create on 1/29/18 11:32 AM
 */

public class ApiTransferParamDTO implements Serializable{
    private static final long serialVersionUID = 2383335247201374653L;

    private Map<String,Object> params;

    @Override
    public String toString() {
        return "ApiTransferParamDTO{" +
                "params='" + params + '\'' +
                '}';
    }

    public void addParam(String key, Object value){
        if(params == null){
            params = new HashMap<>();
        }
        params.put(key,value);
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
