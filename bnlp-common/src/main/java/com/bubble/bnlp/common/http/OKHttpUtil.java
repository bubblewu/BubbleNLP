package com.bubble.bnlp.common.http;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * OKHttp工具包
 *
 * @author wugang
 * date: 2018-09-27 18:45
 **/
public class OKHttpUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(OKHttpUtil.class);

    /**
     * 基于OkHttpClient的POST请求
     *
     * @param url       URL地址
     * @param paramBody 请求参数：eg："[{"phoneId\":\"51874073\"}]" 或 [52991957, 123456]
     * @return 请求结果
     */
    public static String doPost(String url, String paramBody) {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, paramBody);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Cache-Control", "no-cache")
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                if (response.body() != null) {
                    return response.body().string();
                } else {
                    LOGGER.warn("query {}, result is null", url);
                }
            }
        } catch (IOException e) {
            LOGGER.error("post {} error.", url);
        }
        return "";
    }

    /**
     * HTTP的GET请求
     *
     * @param url URL地址
     * @return 结果数据
     */
    public static String doGet(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Cache-Control", "no-cache")
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                if (response.body() != null) {
                    return response.body().string();
                } else {
                    LOGGER.warn("get {}, result is null", url);
                }
            }
        } catch (IOException e) {
            LOGGER.error("get {} error.", url, e);
        }
        return "";
    }

}
