package com.byebug.automation.api;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.byebug.automation.api.annotation.InvalidValue;
import com.byebug.automation.api.annotation.InvalidValueAssert;
import com.byebug.automation.api.annotation.UniqueParam;
import com.byebug.automation.api.halo.ApiAssert;
import com.byebug.automation.api.halo.ApiRequestData;
import com.byebug.automation.api.param.BaseParam;
import com.byebug.automation.api.param.UploadFileParam;
import com.byebug.automation.api.servicemap.ApiMethod;
import com.byebug.automation.api.servicemap.IServiceMap;
import com.byebug.automation.config.ByteBugConfig;
import com.byebug.automation.utils.HaloUtil;
import com.byebug.automation.utils.HttpUtil;
import com.byebug.automation.utils.ReportUtil;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.util.EntityUtils;
import org.testng.Assert;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ApiSuit extends BaseApiSuit{

    /**
     * 接口请求返回的所有请求结果
     */
    private Map<String, String> responseDataMap = new HashMap<String, String>();

    /**
     * 请求返回后，当前完整请求的链接（场景：如某些接口跳转后，需要获取跳转后的url）
     * uuid - fullUrl
     */
    protected Map<String, String> requestFullUrlAfterResponseMap = new HashMap<String, String>();

    /**
     * 记录接口返回的set-cookies参数
     */
    protected static Map<String, String> saveSetCookieMap = new HashMap<String, String>();

    public void apiTest(ApiRequestData apiRequestData) {
        apiTest(apiRequestData, false);
    }

    public void apiTest(ApiRequestData apiRequestData, boolean checkInvalidParam) {
        if(apiRequestData == null) {
            return;
        }
        IServiceMap iServiceMap = apiRequestData.getServiceMap();
        ApiAssert apiAssert = apiRequestData.getApiAssert();
        if(iServiceMap == null || apiAssert == null) {
            Assert.fail("请求ApiRequestData的IServiceMap和ApiAssert不可为null");
            return;
        }

        ReportUtil.log("--- test start ---");
        ReportUtil.log("method:" + iServiceMap.getMethod());
        ReportUtil.log("baseUrl:" + getRequestUrl(apiRequestData.getServiceMap()));
        ReportUtil.log("path:" + iServiceMap.getPath());
        ReportUtil.log("des:" + iServiceMap.getDes());
        String bodyParam = JSONObject.toJSONString(apiRequestData.getBaseParam());
        if(StrUtil.isNotEmpty(bodyParam)) {
            ReportUtil.log("body-param:" + bodyParam);
        }

        // sleep休眠时间大于0的情况下进行暂停休眠
        if (apiRequestData.getSleep() > 0) {
            ReportUtil.log(String.format("sleep %s seconds", apiRequestData.getSleep()));
            HaloUtil.sleep(apiRequestData.getSleep());
        }

        // 发送请求前，回调方法
        beforeSendRequestCallback(apiRequestData);

        // 封装请求方法
        HttpUriRequest method = parseHttpRequest(apiRequestData);

        // 发送并校验结果
        sendRequestAndVerifyResult(apiRequestData, method);

        // 判断是否要进行无效参数校验
        if(checkInvalidParam) {
            BaseParam baseParam = apiRequestData.getBaseParam();

            // 获取所有的属性
            List<Field> fields = new ArrayList<Field>();
            Class tmpClass = baseParam.getClass();
            while(tmpClass != null && !tmpClass.getName().equalsIgnoreCase("java.lang.object")) {
                fields.addAll(Arrays.asList(tmpClass.getDeclaredFields()));
                tmpClass = tmpClass.getSuperclass();
            }

            // 含有唯一性的请求参数
            List<Field> uniqueFields = new ArrayList<Field>();
            for(Field f : fields) {
                if(f != null && f.isAnnotationPresent(UniqueParam.class) && f.getType().equals(String.class)) {
                    uniqueFields.add(f);
                }
            }

            // 遍历所有属性，如果含有InvalidValue注解，则遍历取出所有注解value值，然后重新复制给baseParam，再次发送请求
            for(java.lang.reflect.Field field : fields) {
                try {
                    BaseParam invalidBaseParam = (BaseParam) baseParam.clone();
                    if(field.isAnnotationPresent(InvalidValue.class)) {
                        String[] asserts = new String[]{};
                        InvalidValueAssert invalidValueAssert = field.getAnnotation(InvalidValueAssert.class);
                        if(invalidValueAssert != null) {
                            asserts = invalidValueAssert.value();
                        }
                        // values
                        InvalidValue invalidValue = field.getAnnotation(InvalidValue.class);
                        String[] values = invalidValue.value();
                        for(int i = 0; i < values.length; i++) {
                            String value = values[i];
                            // 设置属性为invalidValue
                            field.setAccessible(true);
                            // 如果传入的异常值为null，则设置属性值为null；否则根据属性值的类型，进行转换
                            if (value.equalsIgnoreCase("null")) {
                                field.set(invalidBaseParam, null);
                            } else {
                                if(field.getType().equals(Integer.class) || field.getType().equals(int.class)) {
                                    field.set(invalidBaseParam, Integer.parseInt(value));
                                } else if (field.getType().equals(Long.class) || field.getType().equals(long.class)) {
                                    field.set(invalidBaseParam, Long.parseLong(value));
                                }  else if (field.getType().equals(Double.class) || field.getType().equals(double.class)) {
                                    field.set(invalidBaseParam, Double.parseDouble(value));
                                } else if (field.getType().equals(Float.class) || field.getType().equals(float.class)) {
                                    field.set(invalidBaseParam, Float.parseFloat(value));
                                } else if (field.getType().equals(Boolean.class) || field.getType().equals(boolean.class)) {
                                    field.set(invalidBaseParam, Boolean.parseBoolean(value));
                                } else if (field.getType().equals(Short.class) || field.getType().equals(short.class)) {
                                    field.set(invalidBaseParam, Short.parseShort(value));
                                } else if (field.getType().equals(Byte.class) || field.getType().equals(byte.class)) {
                                    field.set(invalidBaseParam, Byte.parseByte(value));
                                } else {
                                    field.set(invalidBaseParam, value);
                                }
                            }

                            // 为唯一性属性字段随机添加三个数字
                            for(Field uniq : uniqueFields) {
                                String originValue = (String)uniq.get(invalidBaseParam);
                                uniq.setAccessible(true);
                                uniq.set(invalidBaseParam, HaloUtil.replaceLastCharacters(originValue, 3));
                            }

                            // 发送并校验结果
                            String ass = "";
                            if(i < asserts.length) {
                                ass = asserts[i];
                            }
                            ApiRequestData invalidApiRequestData = new ApiRequestData(apiRequestData.getServiceMap(), invalidBaseParam, ApiAssert.createNotSuccess(ass));
                            ReportUtil.log("=== Auto Test invalid value: " + value + " , ClassName: " + baseParam.getClass().getName() + " ===");
                            apiTest(invalidApiRequestData);
                        }
                    }
                }catch (Exception e) {
                    ReportUtil.log("--- Exception When CheckInvalidParam Value ---, Please Check: " + baseParam.getClass().getName() + "\n" + e.toString());
                }
            }

        }
    }

    /**
     * 从SetCookie中获取key对应的value
     * @param key
     * @return
     */
    public String getCookieValueFromSaveCookies(String key) {
        String value = "";
        for(String k : saveSetCookieMap.keySet()) {
            if(k.equals(key)) {
                value = saveSetCookieMap.get(k);
            }
        }
        return value;
    }

    /**
     * 获取接口请求返回后，真实请求的完整地址
     * @param uuid
     * @return
     */
    public String getRequestFullUrlAfterResponse(String uuid) {
        if(StrUtil.isEmpty(uuid) || !requestFullUrlAfterResponseMap.containsKey(uuid)) {
            return "";
        }
        return requestFullUrlAfterResponseMap.get(uuid);
    }

    /**
     * 获取请求跳转地址的url
     * @param uuid
     * @return
     */
    public String getRedirectUrlFromResponseData(String uuid) {
        String responseData = getResponseData(uuid);
        Object redirectUrl = JSONPath.extract(responseData, "$.redirectUrl");
        if(redirectUrl != null) {
            return redirectUrl.toString();
        }
        return "";
    }

    /**
     * 获取下载文件的路径
     * @param uuid
     * @return
     */
    public String getDownloadFilePathFromResponseData(String uuid) {
        String responseData = getResponseData(uuid);
        Object filePath = JSONPath.extract(responseData, "$.filePath");
        if(filePath != null) {
            return filePath.toString();
        }
        return "";
    }

    /**
     * 从接口返回中，根据jsonpath获取返回值（对象）
     */
    public <T> T getObjectFromResponseData(String uuid, String path, Class<T> cls) {
        return HaloUtil.extractObjectByJSONPath(getResponseData(uuid), path, cls);
    }

    /**
     * 从接口返回中，根据jsonpath获取返回值（字符串）
     */
    public String getValueFromResponseData(String uuid, String path) {
        return HaloUtil.extractByJSONPath(getResponseData(uuid), path);
    }

    /**
     * 从接口返回中，获取接口的返回信息
     * @param uuid
     * @return
     */
    public String getResponseData(String uuid) {
        if(StrUtil.isEmpty(uuid) || !responseDataMap.containsKey(uuid)) {
            return "";
        }
        return responseDataMap.get(uuid);
    }

    private HttpUriRequest parseHttpRequest(ApiRequestData apiRequestData) {
        if(apiRequestData == null || apiRequestData.getServiceMap() == null) {
            return null;
        }
        IServiceMap iServiceMap = apiRequestData.getServiceMap();

        String fullUrl = parseUrl(getRequestUrl(apiRequestData.getServiceMap()), iServiceMap.getPath(), apiRequestData.getAppendPathParam());
        ApiMethod method = iServiceMap.getMethod();

        // 读取所有保存下来的cookie值，同时合并回调返回的map中的cookie值
        Map<String, String> callbackMap = getRequestHeaderBeforeSendRequestCallback(apiRequestData);
        if(callbackMap == null) {
            callbackMap = new HashMap<String, String>();
        }
        StringBuilder cookieValueSb = new StringBuilder();
        for(String key : saveSetCookieMap.keySet()) {
            if(StrUtil.isNotEmpty(cookieValueSb.toString())) {
                cookieValueSb.append("; ");
            }
            cookieValueSb.append(key);
            cookieValueSb.append("=");
            cookieValueSb.append(saveSetCookieMap.get(key));
        }
        if(callbackMap.size() > 0) {
            String callbackCookie = callbackMap.get("cookie");
            if(StrUtil.isNotEmpty(callbackCookie)) {
                if(StrUtil.isNotEmpty(cookieValueSb.toString())) {
                    cookieValueSb.append("; ");
                }
                cookieValueSb.append(callbackCookie);
            }
        }
        if(StrUtil.isNotEmpty(cookieValueSb.toString())) {
            callbackMap.put("cookie", cookieValueSb.toString());
        }
        callbackMap.put("Referer", parseUrl(getRequestUrl(apiRequestData.getServiceMap()), iServiceMap.getPath(), apiRequestData.getAppendPathParam()));
        if(ByteBugConfig.REPORT_LOG_REQUEST_HEADERS) {
            for(String key : callbackMap.keySet()) {
                ReportUtil.log("HEAD:" + key + "=" + callbackMap.get(key));
            }
        }
        Header[] publicHeaders = HttpUtil.getHeadsFromMap(callbackMap);

        // 设置http请求信息HttpUriRequest
        if (method == ApiMethod.POST || method == ApiMethod.POST_FORM || method == ApiMethod.PUT
                || method == ApiMethod.PUT_FORM) {
            if(method == ApiMethod.POST_FORM || method == ApiMethod.PUT_FORM) {
                List<BasicNameValuePair> pair =new ArrayList<BasicNameValuePair>();

                Map<String, String > formMapParam = HaloUtil.getObjectMapByReflex(iServiceMap.getRequestParamClass(), apiRequestData.getBaseParam());
                for(String key : formMapParam.keySet()) {
                    pair.add(new BasicNameValuePair(key, formMapParam.get(key)));
                }

                if(method == ApiMethod.POST_FORM) {
                    try {
                        HttpPost postMethod = new HttpPost(fullUrl);
                        postMethod.setHeaders(publicHeaders);
                        postMethod.setEntity(new UrlEncodedFormEntity(pair));
                        return postMethod;
                    } catch (UnsupportedEncodingException e) {
                        ReportUtil.log("接口 " + iServiceMap.getPath() + " 拼接FormEntity异常:" + e.toString());
                    }
                }else {
                    try {
                        HttpPut putMethod = new HttpPut(fullUrl);
                        putMethod.setHeaders(publicHeaders);
                        putMethod.setEntity(new UrlEncodedFormEntity(pair));
                        return putMethod;
                    } catch (UnsupportedEncodingException e) {
                        ReportUtil.log("接口 " + iServiceMap.getPath() + " 拼接FormEntity异常:" + e.toString());
                    }
                }
            }else {
                if(method == ApiMethod.POST) {
                    try {
                        StringEntity entity = new StringEntity(JSONObject.toJSONString(apiRequestData.getBaseParam()), "UTF-8");
                        HttpPost postMethod = new HttpPost(fullUrl);
                        postMethod.setHeaders(publicHeaders);
                        postMethod.setEntity(entity);
                        return postMethod;
                    } catch (Exception e) {
                        ReportUtil.log("Post StringEntity Exception: " + e);
                    }
                }else if(method == ApiMethod.PUT){
                    try {
                        HttpPut putMethod = new HttpPut(fullUrl);
                        putMethod.setHeaders(publicHeaders);
                        HttpEntity entity = new StringEntity(JSONObject.toJSONString(apiRequestData.getBaseParam()), "UTF-8");
                        putMethod.setEntity(entity);
                        return putMethod;
                    } catch (Exception e) {
                        ReportUtil.log("Put StringEntity Exception: " + e.toString());
                    }
                }
            }
            return null;
        }else if(method == ApiMethod.UPLOAD){
            if(!(apiRequestData.getBaseParam() instanceof UploadFileParam)) {
                Assert.fail("上传文件接口(ApiMethod.UPLOAD)必须使用UploadFileParam");
                return null;
            }
            HttpPost postMethod = new HttpPost(fullUrl);
            postMethod.setHeaders(publicHeaders);
            postMethod.removeHeaders("Content-Type");
            MultipartEntityBuilder entityBuilder  = MultipartEntityBuilder.create();
            entityBuilder.setCharset(StandardCharsets.UTF_8);
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            try {
                UploadFileParam uploadFileParam = (UploadFileParam)apiRequestData.getBaseParam();
                String fileAbsolutePath = uploadFileParam.getFilePath();
                if(!(uploadFileParam.getFilePath().startsWith("/") || uploadFileParam.getFilePath().indexOf(":") > 0)) {
                    fileAbsolutePath = System.getProperty("user.dir") + File.separator + uploadFileParam.getFilePath();
                }
                File file = new File(fileAbsolutePath);
                entityBuilder.addBinaryBody(uploadFileParam.getFileKey(), file, ContentType.DEFAULT_BINARY, file.getName());

                if(uploadFileParam.getMap() != null && uploadFileParam.getMap().size() > 0) {
                    for(Map.Entry<String, String> entry : uploadFileParam.getMap().entrySet()) {
                        entityBuilder.addTextBody(entry.getKey(), entry.getValue());
                    }
                }
            } catch (Exception e) {
                ReportUtil.log("Upload File Exception: " + e.toString());
            }
            postMethod.setEntity(entityBuilder.build());
            return postMethod;
        }else {
            String appendUrlParam = apiRequestData.getAppendPathParam();
            if(!StrUtil.isEmpty(appendUrlParam)) {
                fullUrl = fullUrl + appendUrlParam;
            }
            if(method == ApiMethod.DELETE) {
                HttpDelete deleteMethod = new HttpDelete(fullUrl);
                deleteMethod.setHeaders(publicHeaders);
                return deleteMethod;
            }else {
                HttpGet getMethod = new HttpGet(fullUrl);
                getMethod.setHeaders(publicHeaders);
                return getMethod;
            }
        }
    }

    private void sendRequestAndVerifyResult(ApiRequestData apiRequestData, HttpUriRequest method) {
        String responseData = "";
        boolean downloadFileRequest = false;

        ApiAssert apiAssert = apiRequestData.getApiAssert();
        IServiceMap iServiceMap = apiRequestData.getServiceMap();

        try {
            HttpClient httpClient = HttpUtil.initHttpClient();
            if(httpClient == null) {
                Assert.fail("HttpClient初始化错误！！！");
                return;
            }
            BasicHttpContext httpContext = new BasicHttpContext();
            HttpResponse response = httpClient.execute(method, httpContext);
            int responseStatus = response.getStatusLine().getStatusCode();
            ReportUtil.log("返回状态码：" + responseStatus);

            // 记录请求完成后，每个请求对应的全链接地址
            HttpHost targetHost = (HttpHost)httpContext.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
            HttpUriRequest realRequest = (HttpUriRequest)httpContext.getAttribute(ExecutionContext.HTTP_REQUEST);
            requestFullUrlAfterResponseMap.put(apiRequestData.getUuid(), targetHost.toString() + realRequest.getURI());

            if (apiAssert.isNeedAssert()) {
                if(!noVerifyHttpStatus(responseStatus, apiRequestData, responseData)) {
                    Assert.assertEquals(responseStatus, apiAssert.getHttpStatus(), "返回状态码与预期不符合!");
                }
                if(responseStatus != 200){
                    return;
                }
            }
            HttpEntity respEntity = response.getEntity();
            Header respContentType = response.getFirstHeader("Content-Type");

            // 获取set-cookie的值,存入map
            Header[] setCookieHeaders = response.getHeaders("set-cookie");
            if(setCookieHeaders != null && setCookieHeaders.length > 0) {
                for (Header header : setCookieHeaders) {
                    String values = header.getValue();
                    if (StrUtil.isNotEmpty(values)) {
                        String value = values.split(";")[0];
                        if (StrUtil.isNotEmpty(value)) {
                            int index = value.indexOf("=");
                            if (index != -1) {
                                saveSetCookieMap.put(value.substring(0, index), value.substring(index + 1));
                            }
                        }
                    }
                }
            }

            // 判断是否重定向
            Header locationHeader = response.getFirstHeader("location");
            if(locationHeader != null) {
                responseData = "{\"redirectUrl\":\"" + locationHeader.getValue() + "\"}";
            }
            // 判断是否需要下载文件
            else if ((iServiceMap.getMethod() == ApiMethod.DOWNLOAD) ||
                    (respContentType != null && respContentType.getValue() != null &&  (respContentType.getValue().contains("download") || respContentType.getValue().contains("octet-stream")))) {
                downloadFileRequest = true;
                // 下载的文件格式
                String fileType = "";
                if(response.getFirstHeader("Content-disposition") != null) {
                    String conDisposition = response.getFirstHeader("Content-disposition").getValue();
                    int dotLastIndex = conDisposition.lastIndexOf(".");
                    if(dotLastIndex != -1) {
                        fileType = conDisposition.substring(dotLastIndex, conDisposition.length() -1);
                    }
                }
                // 下载文件
                String filePath = System.getProperty("user.dir") + ByteBugConfig.DOWNLOAD_DIR + File.separator + RandomUtil.randomString(8) + fileType;
                InputStream is = response.getEntity().getContent();
                try {
                    FileUtil.writeFromStream(is, filePath);
                }catch (Exception e) {
                    Assert.fail("下载文件失败！！！");
                }

                responseData = "{\"filePath\":\"" + filePath + "\"}";
            }
            // 其它
            else {
                responseData= EntityUtils.toString(respEntity, "UTF-8");
            }
        } catch (Exception e) {
            ReportUtil.log("HttpRequest Exception:" + e.toString());
        } finally {
            method.abort();
        }
        ReportUtil.log("resp:" + responseData);

        // 对返回结果进行全部保存
        saveResponseData(apiRequestData.getUuid(), responseData);

        // 返回结果后，回调
        afterSendRequestCallback(apiRequestData, responseData);

        // 不需要断言场景：文件下载、请求体明确不需要校验、接口回调返回false
        if(!downloadFileRequest && apiAssert.isNeedAssert() && !noVerifyResponseCallback(apiRequestData, responseData)) {
            verifyResult(responseData, apiAssert.getVerify());
        }

        // 清理测试套件保存的数据回调
        if(cleanSaveDataCallback(apiRequestData)) {
            resetSaveData();
        }
    }

    /**
     * 清理测试套件保存的信息，包括
     * 1、接口返回值信息
     * 2、跳转的url
     * 3、setCookies
     */
    public void resetSaveData() {
        responseDataMap.clear();
        requestFullUrlAfterResponseMap.clear();
        saveSetCookieMap.clear();
    }

    private String parseUrl(String url, String path, String appendPathParam) {
        if(StrUtil.isEmpty(appendPathParam)) {
            appendPathParam = "";
        }

        if (path.startsWith("http")) {
            url = "";
        }else {
            if(url.endsWith("/")) {
                if(path.startsWith("/")) {
                    path = path.replaceFirst("/", "");
                }
            }else {
                if(!path.startsWith("/")) {
                    path = "/" + path;
                }
            }
        }

        if(StrUtil.isNotEmpty(appendPathParam)) {
            if(!appendPathParam.startsWith("/") && !appendPathParam.startsWith("?")) {
                appendPathParam = "/" + appendPathParam;
            }
            if(StrUtil.isNotEmpty(path) && path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
        }

        return url + path + appendPathParam;
    }

    private void saveResponseData(String uuid, String json) {
        responseDataMap.put(uuid, json);
    }

    private void verifyResult(String sourceData, String verifyStr) {
        if (StrUtil.isEmpty(verifyStr)) {
            return;
        }
        ReportUtil.log("验证数据：" + verifyStr);

        // 通过';'分隔，通过jsonPath进行一一校验
        Pattern pattern = Pattern.compile("([^;]*)=([^;]*)");
        Matcher m = pattern.matcher(verifyStr.trim());
        while (m.find()) {
            String group1 = m.group(1);
            boolean isNotEqual = group1.contains("!");
            if(isNotEqual) {
                group1 = group1.substring(0, group1.length() - 1);
            }
            String group2 = m.group(2);
            String actualValue = HaloUtil.extractByJSONPath(sourceData, group1);
            String exceptValue = HaloUtil.extractByJSONPath(sourceData, group2);
            ReportUtil.log(String.format(isNotEqual ? "验证转换后的值%s!=%s" : "验证转换后的值%s=%s", actualValue, exceptValue));
            if(isNotEqual) {
                Assert.assertNotEquals(actualValue, exceptValue, "验证预期结果失败。");
            }else {
                Assert.assertEquals(actualValue, exceptValue, "验证预期结果失败。");
            }
        }
    }
}
