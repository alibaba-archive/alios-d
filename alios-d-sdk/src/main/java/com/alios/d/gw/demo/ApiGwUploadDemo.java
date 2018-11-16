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

import com.alios.d.gw.sdk.FileUploadClient;

/**
 * author autoCreate
 * 非结构化数据上传参考下面的demo程序
 */
public class ApiGwUploadDemo {
    public static void main(String[] args) {
        FileUploadClient fileUploadClient = FileUploadClient.newBuilder()
                //必填
                .tenantId("your tenantId")
                //必填
                .eventId("your eventId")
                .eventVersion(1)
                //必填
                .deviceId("your deviceId")
                .vinId("your vinId")
                .groupHost("api网关host")
                .appKey("your appkey")
                .appSecret("your appsecret")
                .build();
        fileUploadClient.upload("/path/to/file/file.zip");
    }
}
