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
package com.alios.d.gw.sdk.util;


import org.apache.commons.lang3.StringUtils;

/**
 * ApiGwPathUtil
 *
 * @author aifeng
 * @version Create on 1/19/18 3:32 PM
 */

public class ApiGwPathUtil {
    private static final String PATH_PREFIX = "/alios";

    /**
     * 组装path成api网关识别的模式。举例：uc.user.getById 转换为 api网关能识别的 /alios/uc/user/getById
     * @param _apiPath
     * @return
     */
    public static String convert2ApiGw(String _apiPath){
        if(StringUtils.isNotBlank(_apiPath) && _apiPath.contains(".")){
            StringBuilder path = new StringBuilder();
            path.append(PATH_PREFIX);
            path.append(_apiPath.startsWith("/") ? "" : "/");
            path.append(_apiPath.replace(".", "/"));
            return path.toString();
        }
        return _apiPath;
    }


    /**
     * 组装path成api网关识别的模式。举例：/alios/uc/user/getById 转换为 api网关能识别的 uc.user.getById
     * @param _apiPath
     * @return
     */
    public static String convert2AliosGw(String _apiPath){
        if(StringUtils.isNotBlank(_apiPath) && _apiPath.contains("/")){
            if(_apiPath.startsWith("/")){
                _apiPath = _apiPath.substring(1);
            }
            if(_apiPath.startsWith("alios/")){
                _apiPath = _apiPath.substring("alios/".length());
            }

            return _apiPath.replace("/", ".");
        }
        return _apiPath;
    }
}
