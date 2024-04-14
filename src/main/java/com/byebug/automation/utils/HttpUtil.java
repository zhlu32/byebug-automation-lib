package com.byebug.automation.utils;

import cn.hutool.core.util.StrUtil;
import com.byebug.automation.config.ByteBugConfig;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;

import javax.net.ssl.SSLContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpUtil {

    /**
     * 返回API HttpClient对象
     * @return
     */
    public static HttpClient initHttpClient() {
        try {
            // 绕过https证书验证
            TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
            SSLConnectionSocketFactory sslFactory = new SSLConnectionSocketFactory(sslContext,
                    NoopHostnameVerifier.INSTANCE);

            // 设置协议http和https对应的处理socket链接工厂的对象
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", sslFactory)
                    .build();
            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

            // 设置是否自动跳转
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(ByteBugConfig.HTTP_CLIENT_TIMEOUT).setSocketTimeout(ByteBugConfig.HTTP_CLIENT_TIMEOUT).
                    setRedirectsEnabled(ByteBugConfig.HTTP_CLIENT_REDIRECT).build();
            return HttpClients.custom().setRedirectStrategy(new LaxRedirectStrategy())
                    .setDefaultRequestConfig(requestConfig).setConnectionManager(connManager).build();

        } catch (Exception e) {
            ReportUtil.log("初始化HTTPClient报错" + e);
        }
        return null;
    }

    /**
     * 把Header的map转换为Header[]
     * @param headerMap
     * @return
     */
    public static Header[] getHeadsFromMap(Map<String, String> headerMap) {
        List<Header> headers = new ArrayList<Header>();
        if(headerMap != null && headerMap.size() > 0) {
            headerMap.forEach((key, value) -> {
                Header header = new BasicHeader(key, value);
                headers.add(header);
            });
        }

        return headers.toArray(new Header[headers.size()]);
    }

    public static String getIPOrHostFromAddress(String address) {
        if(StrUtil.isEmpty(address)) {
            return "";
        }
        int index = address.indexOf("://");
        String substring = address.substring(index + 3);
        System.out.println(substring);
        int subIndex = substring.indexOf("/");
        if(subIndex > -1) {
            substring = substring.substring(0, subIndex);
        }
        int portIndex = substring.indexOf(":");
        if(portIndex > -1) {
            substring = substring.substring(0, portIndex);
        }

        System.out.println(substring);
        return substring;
    }

}