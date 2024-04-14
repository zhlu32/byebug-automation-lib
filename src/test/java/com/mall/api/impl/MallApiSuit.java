package com.mall.api.impl;

import com.byebug.automation.api.ApiSingleton;
import com.byebug.automation.api.ApiSuit;
import com.byebug.automation.api.halo.ApiRequestData;
import com.byebug.automation.api.servicemap.IServiceMap;
import com.byebug.automation.config.ByteBugConfig;

import java.util.HashMap;
import java.util.Map;

public class MallApiSuit extends ApiSuit {
    @Override
    public void beforeSuitCallback() {
        ByteBugConfig.RESPONSE_CODE_SUCCESS = 200;

        ApiSingleton.getInstance().setByeBugCallbackListener(new MallCallbackListener());
    }

    @Override
    public void beforeTestCallback() {

    }

    @Override
    public void beforeClassCallback() {

    }

    @Override
    public void beforeMethodCallback() {

    }

    @Override
    public void afterMethodCallback() {

    }

    @Override
    public void afterClassCallback() {

    }

    @Override
    public void afterTestCallback() {

    }

    @Override
    public void afterSuitCallback() {

    }

    @Override
    public String getRequestUrl(IServiceMap serviceMap) {
        return MallProperties.get(MallProperties.g_mall_host);
    }

    @Override
    public void beforeSendRequestCallback(ApiRequestData apiRequestData) {

    }

    @Override
    public Map<String, String> getRequestHeaderBeforeSendRequestCallback(ApiRequestData apiRequestData) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("Content-Type", "application/json");
        return map;
    }

    @Override
    public boolean noVerifyHttpStatus(int httpStatusCode, ApiRequestData apiRequestData, String responseData) {
        return false;
    }

    @Override
    public void afterSendRequestCallback(ApiRequestData apiRequestData, String responseData) {

    }

    @Override
    public boolean noVerifyResponseCallback(ApiRequestData apiRequestData, String responseData) {
        return false;
    }

    @Override
    public boolean cleanSaveDataCallback(ApiRequestData apiRequestData) {
        return false;
    }
}
