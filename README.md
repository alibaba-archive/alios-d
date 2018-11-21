# Alios Data SDK for java 使用指南
## 1 SDK简介

本SDK是阿里云Alios数据工程团队提供给用户的调用示例代码，代码文件的层级结构如下：

**SDK实现文件**  

* com.alios.d.gw.sdk
	* AbstractApiGwClient		`抽象网关访问类`
	* AbstractBaseApiClientBuilder		`抽象网关client的Builder类`
	* ApiTransferParamDTO	`辅助开发人员构建请求参数`
	* FileUploadClient      `文件上传Client类`
	* com.alios.d.gw.sdk.dto
	    * FileUploadDTO     `文件上传参数`
	    * ResultCode        `调用方法返回错误码包装类`
	    * ResultCodes       `包含标准错误码的常量类`
	    * ResultDTO         `返回结果的包装类`
	* com.alios.d.gw.sdk.util
	    * ApiGwPathUtil     `网关Path转换工具类`

**SDK调用文件**  

*  com.alios.d.gw.demo
    * ApiGwClient       `通用网关api调用客户端`
    * ApiGwDemo         `通用网关api调用demo程序类`
    * ApiGwUploadDemo   `文件上传Demo程序类`
    * YydjApiDemo       `调用云云对接Demo程序类`
    
本SDK包含了对API网关和OSS的SDK的封装，用户无须关心底层的细节。  
API网关简介：
`https://help.aliyun.com/document_detail/29464.html?spm=a2c4g.11186623.3.2.7b186e23bpJeEF`   
OSS简介：
`https://help.aliyun.com/document_detail/31817.html?spm=a2c4g.11186623.6.542.71812d47tydzEw`


## 2 SDK配置

本SDK需要配置好之后才能正常使用，在**SDK调用文件ApiGwDemo、ApiGwUploadDemo、YydjApiDemo**中，用户可根据需要使用这三个Demo类的一个或者多个，有三个值是必须配置的：

    api host：是api网关的域名
    appKey: Api绑定的的AppKey
    appSecret: Api绑定的的AppSecret
    
    这三个配置可以找api的开发获取。
    

**重要提示：appKey和appSecret是网关认证用户请求的钥匙，这两个配置如果保存在客户端，需要加密处理。** 


## 3 SDK使用

### 3.1 把SDK引入你的项目
配置完成之后，本SDK就可以使用了。直接把com.alios.d.gw.client文件夹复制到你项目的src/java文件夹下，然后就能直接应用了。

### 3.2 maven依赖

	    <dependency>
                <groupId>com.aliyun.api.gateway</groupId>
                <artifactId>sdk-core-java</artifactId>
                <version>1.0.4</version>
            </dependency>
            <dependency>
                <groupId>com.alibaba</groupId>
                <artifactId>fastjson</artifactId>
                <version>1.2.48</version>
            </dependency>
            <dependency>
                <groupId>org.apache.httpcomponents</groupId>
                <artifactId>httpclient</artifactId>
                <version>4.5.3</version>
            </dependency>
            <dependency>
                <groupId>org.apache.httpcomponents</groupId>
                <artifactId>httpmime</artifactId>
                <version>4.5.3</version>
            </dependency>
	

### 3.3 调用Demo

执行Demo类中的main方法即可。
