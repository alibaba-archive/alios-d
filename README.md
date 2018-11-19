# alios-d
请求网关的demo类:  ApiGwDemo    
非结构化数据上传的demo类: ApiGwUploadDemo

调用api服务

一、如果调用api？\
ApiGwDemo和ApiGwClient是调用api的例子类，用户只需要稍微修改一下这两个类，就可以进行api的调用。\
1、将ApiGwDemo中的appKey和appSecret替换成你自己的应用对应的appKey和appSecret，将groupHost替换成对应的api网关的host；\
2、将ApiGwClient中的_apiPath参数替换成你想要请求的api的path（path列表可向api开发索要，示例：a.b.c）；\
3、参数要以json字符串的形式传递，形式为：{"params": {"key1":"value1", "key2":"value2"}}，其中，key1、key2和key3是api需要用户传递的参数。用户可以自己拼装成这种形式的字符串，也可以使用sdk中的ApiTransferParamDTO来组装，再转成json字符串。\
4、执行ApiGwDemo中的main方法即可调用指定的api。\
二、返回结果示例。\
最外层返回ApiResponse\
{\
	"statusCode": 200,//响应状态码，大于等于200小于300表示成功；大于等于400小于500为客户端错误；大于500为服务端错误。200表示成功，errorCode同HTTP，例如404\
	"body": //byte数组，请使用new String(response.getBody(), "utf-8")转换成String\
}\
body转换成的字符串如无特殊说明，为下述Json\
{\
	"id": 1111,//requestId\
	"code": 200,//返回码，200为成功，其他为失败，可调用hasSucceeded()查询是否成功\
	"message": "",//解释\
	"localizedMsg": "中文描述",//中文描述\
	"data"://服务提供者的返回，不同接口有不同返回类型，具体请联系接口提供者\
}
