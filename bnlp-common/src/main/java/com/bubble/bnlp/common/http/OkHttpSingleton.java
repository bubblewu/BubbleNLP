package com.bubble.bnlp.common.http;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.util.concurrent.TimeUnit;

/**
 * 修改OkHttpClient默认长连接配置，防止内存溢出
 * 参考：[解决retrofit OKHttp创建大量对外连接时内存溢出](https://blog.csdn.net/tianyaleixiaowu/article/details/78811488)
 * [OkHttp3的连接池及连接建立过程分析](https://www.jianshu.com/p/e6fccf55ca01)
 *
 * @author wugang
 * date: 2018-11-19 17:57
 **/
public class OkHttpSingleton {
    private OkHttpClient okHttpClient;

    private OkHttpSingleton() {

    }

    public static OkHttpSingleton getInstance() {
        return OkHttpHolder.instance;
    }

    private static class OkHttpHolder {
        private static OkHttpSingleton instance = new OkHttpSingleton();

        static {
            init();
        }

        private static void init() {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request.Builder builder = chain.request()
                                .newBuilder()
                                .addHeader("Content-Type", "application/json")
                                .addHeader("Cache-Control", "no-cache");
                        return chain.proceed(builder.build());
                    })
                    .connectTimeout(5, TimeUnit.MINUTES)
                    .readTimeout(5, TimeUnit.MINUTES)
                    .writeTimeout(5, TimeUnit.MINUTES)
                    // 连接池中最大的空闲连接数及连接的存活时间(长连接)
                    .connectionPool(new ConnectionPool(5, 1, TimeUnit.SECONDS))
                    .build();
            instance.setOkHttpClient(client);
        }

    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public void setOkHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

}
