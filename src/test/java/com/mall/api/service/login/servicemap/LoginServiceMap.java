package com.mall.api.service.login.servicemap;

import com.byebug.automation.api.param.BaseParam;
import com.byebug.automation.api.servicemap.ApiMethod;
import com.byebug.automation.api.servicemap.IServiceMap;
import com.mall.api.service.login.param.LoginParam;

public enum LoginServiceMap implements IServiceMap {
    LOGIN("/admin/login", ApiMethod.POST, LoginParam.class, "登录接口"),
    ;

    private String path;
    private ApiMethod apiMethod;
    private Class<BaseParam> requestParamClass;
    private String des, extra;

    LoginServiceMap(String path, ApiMethod apiMethod, Class requestParamClass, String des) {
        this.path = path;
        this.apiMethod = apiMethod;
        this.requestParamClass = requestParamClass;
        this.des = des;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String setPath(String path) {
        return this.path = path;
    }

    @Override
    public ApiMethod getMethod() {
        return apiMethod;
    }

    @Override
    public Class<BaseParam> getRequestParamClass() {
        return requestParamClass;
    }

    @Override
    public String getDes() {
        return des;
    }

    @Override
    public String getExtra() {
        return extra;
    }
}
