package com.bubble.bnlp.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 常用工具包
 *
 * @author wugang
 * date: 2018-11-09 22:45
 **/
public class ToolKits {
    private static final Logger LOGGER = LoggerFactory.getLogger(ToolKits.class);

    /**
     * 加载resources目录下的config.properties配置文件
     *
     * @return 配置数据
     */
    public static Properties getProperties() {
        return getProperties("config.properties");
    }

    public static Properties getProperties(String fileName) {
        Properties properties = new Properties();
        try {
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            properties.load(inputStream);
        } catch (IOException e) {
            LOGGER.error("加载配置文件[{}] error.", fileName);
        }
        return properties;
    }

    /**
     * 计算以 2 为底的对数
     */
    public static double log2(double x) {
        return Math.log(x) / Math.log(2.0);
    }

    /**
     * distinct 工具
     * 用法：eg：distinct by _id
     * List<TipsUserLinkBean> topItems = oneUserRecItems.stream().filter(distinctByKey(TipsUserLinkBean::get_id)).collect(Collectors.toList());
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    private static final String SPLIT = ",";

    public static String append(String... datas) {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(datas).filter(Objects::nonNull).forEach(data -> sb.append(data).append(SPLIT));
        if (sb.toString().trim().length() > 0) {
            int index = sb.lastIndexOf(SPLIT);
            return sb.substring(0, index);
        }
        return sb.toString();
    }

    public static String setToString(Set<String> set) {
        StringBuilder sb = new StringBuilder();
        set.stream().filter(Objects::nonNull).forEach(data -> sb.append(data).append(SPLIT));
        if (sb.toString().trim().length() > 0) {
            int index = sb.lastIndexOf(SPLIT);
            return sb.substring(0, index);
        }
        return sb.toString();
    }

    public static boolean isNumeric(Class<?> type) {
        if (type == Integer.class || type == Integer.TYPE || type == Float.class || type == Float.TYPE || type == Double.class || type == Double.TYPE) {
            return true;
        }
        return false;
    }

}
