package com.byebug.automation.api.servicemap;

import com.byebug.automation.api.param.BaseParam;

public interface IServiceMap {

    // uri - path前缀
    String getPath();
    String setPath(String path);

    // http动词
    ApiMethod getMethod();

    // body请求参数类
    Class<BaseParam> getRequestParamClass();

    // 接口功能描述
    String getDes();

    // 备用字段
    String getExtra();

}
