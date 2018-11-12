package com.bubble.bnlp.common;

import java.util.Map;
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

}
