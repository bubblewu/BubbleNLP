package com.bubble.bnlp.common.http;

import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * HTTP工具包
 *
 * @author wugang
 * date: 2018-09-27 17:13
 **/
public class HttpUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtil.class);
    private static final String DEFAULT_CHARSET = "UTF-8";

    private static CloseableHttpClient hc;

    static {
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setDefaultHeaders(genHeads())
                .setMaxConnTotal(10)
                .setMaxConnPerRoute(8)
                .setDefaultRequestConfig(RequestConfig.custom().setConnectTimeout(60000).setSocketTimeout(120000).build());
        hc = builder.build();
    }

    public static void main(String[] args) {
        LOGGER.info("test");
    }

    /**
     * 通过URL获取,不需要复杂设置网页,用于直接获取
     *
     * @param url url
     * @return 正常200返回页面内容, 如果状态码是302, 则返回重定向后的url, 其他情况, 返回状态码
     */
    public static String getContextByUrl(String url) {
        HttpUriRequest request = new HttpGet(url);
        String re = "";
        try {
            CloseableHttpResponse hr = hc.execute(request);
            try {
                int statusCode = hr.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.SC_OK) {
                    HttpEntity entity = hr.getEntity();
                    re = EntityUtils.toString(entity, "utf-8");
                    EntityUtils.consume(entity);
                } else if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
                    Header ha = hr.getFirstHeader("Location");
                    re = ha.getValue();
                } else {
                    re = Integer.toString(statusCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                hr.close();
            }
        } catch (Exception e) {
            LOGGER.error("获取页面异常:" + url, e);
            re = "";
        }
        return re.trim();
    }

    /**
     * POST请求
     *
     * @param url 地址
     * @param map POST参数
     * @return 响应结果
     */
    public static String doPost(String url, Map<String, String> map) {
        String result = null;
        HttpPost httpPost;
        try {
            hc = HttpClients.createDefault();
            httpPost = new HttpPost(url);
            //设置参数
            List<NameValuePair> list = new ArrayList<>();
            Iterator iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> elem = (Map.Entry<String, String>) iterator.next();
                list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
            }
            if (list.size() > 0) {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, DEFAULT_CHARSET);
                httpPost.setEntity(entity);
            }
            HttpResponse response = hc.execute(httpPost);
            if (response != null) {
                int status = response.getStatusLine().getStatusCode();
                LOGGER.info("status = {}", status);
                if (HttpURLConnection.HTTP_OK == status) {
                    HttpEntity resEntity = response.getEntity();
                    if (resEntity != null) {
                        result = EntityUtils.toString(resEntity, DEFAULT_CHARSET);
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.error("POST请求状态错误.", ex);
            ex.printStackTrace();
        }
        return result;
    }

    private static List<Header> genHeads() {
        String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.1.2)";
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7"));
        headers.add(new BasicHeader("Accept-Language", "zh-cn,zh;q=0.5"));
        headers.add(new BasicHeader("User-Agent", userAgent));
        return headers;
    }

}
