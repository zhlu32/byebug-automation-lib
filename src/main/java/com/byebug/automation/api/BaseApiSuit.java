package com.byebug.automation.api;

import com.byebug.automation.BaseSuit;
import com.byebug.automation.api.halo.ApiRequestData;
import com.byebug.automation.api.servicemap.IServiceMap;

import java.util.Map;

/**
 * Biz业务方的回调函数
 */
public abstract class BaseApiSuit extends BaseSuit {

    /**
     * Biz根据不同的接口，返回特定的请求URL
     *   * 应用场景：默认返回请求的URL，针对个别请求做特殊URL处理，如调用Biz第三方的认证，请求的URL和Biz的默认URL不同
     * @param serviceMap
     * @return
     */
    public abstract String getRequestUrl(IServiceMap serviceMap);

    /**
     * Biz在发送接口请求前回调
     *   * 应用场景：如需要对所有接口请求参数做统一处理，如加密等
     * @param apiRequestData
     */
    public abstract void beforeSendRequestCallback(ApiRequestData apiRequestData);

    /**
     * Biz在发送接口请求前回调，确认需要回调的场景-获取请求头配置
     * @param apiRequestData
     * @return
     */
    public abstract Map<String, String> getRequestHeaderBeforeSendRequestCallback(ApiRequestData apiRequestData);

    /**
     * Biz请求返回后回调，确认需要回调的场景-判断是否不需要做HttpStatusCode的断言
     *      如：被测系统的接口，有统一的HttpStatusCode的错误码，如强制改密
     * @param httpStatusCode
     * @param apiRequestData
     * @param responseData
     * @return
     */
    public abstract boolean noVerifyHttpStatus(int httpStatusCode, ApiRequestData apiRequestData, String responseData);

    /**
     * Biz请求返回后回调
     *    如：统一处理接口返回的心跳值或时间戳等
     * @param apiRequestData
     * @param responseData
     */
    public abstract void afterSendRequestCallback(ApiRequestData apiRequestData, String responseData);

    /**
     * Biz请求返回后回调，确认需要回调的场景-判断是否不需要做ApiAssert的断言
     *    如：被测系统的接口，有统一的ResponseCode的错误码，如强制改密
     * @param apiRequestData
     * @param responseData
     * @return
     */
    public abstract boolean noVerifyResponseCallback(ApiRequestData apiRequestData, String responseData);

    /**
     * Biz请求返回后回调，是否清除自动化测试缓存
     *     如：业务在退出登录后，需要清理缓存
     * @param apiRequestData
     * @return
     */
    public abstract boolean cleanSaveDataCallback(ApiRequestData apiRequestData);

}