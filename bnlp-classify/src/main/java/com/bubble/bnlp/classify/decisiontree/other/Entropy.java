package com.bubble.bnlp.classify.decisiontree.other;

import com.bubble.bnlp.bean.tree.structure.BasicDataSet;
import com.bubble.bnlp.common.ToolKits;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 熵值计算
 *
 * @author wugang
 * date: 2018-12-17 15:51
 **/
public class Entropy {

    /**
     * 熵计算，计算当前状态下的总的信息熵，即H(D)数据集合D的经验熵
     *
     * @param totalCount          分类总数
     * @param childClassCountList 每个分类的数目集合
     * @return 熵H(D)：I(s1, s2, ..., sn) = -p1*log2(p1) - p2*log2(p2) - ... - pn*log2(pn); 其中pi=si/(s1+s2+...+sn)
     */
    public static double entropy(int totalCount, List<Integer> childClassCountList) {
        AtomicReference<Double> entropy = new AtomicReference<>(0.0);
        childClassCountList.stream().filter(c -> c > 0).forEach(count -> {
            double probability = 1.0 * count / totalCount;
            entropy.updateAndGet(v -> v - probability * ToolKits.log2(probability));
        });
        return entropy.get();
    }

    /**
     * 通过属性A划分后的熵值，即H(D|A)为在特征A给定的条件下D的经验条件熵。
     *
     * @param dataSet     属性切分后的左/右数据集
     * @param classColumn 类值的列索引
     * @param classValues 类值集合
     * @return H(D | A) 经验条件熵
     */
    public static double conditionalEntropy(BasicDataSet dataSet, int classColumn, SortedSet<String> classValues) {
        // 统计切分后数据集中的类值分布
        Map<String, Integer> classCountMap = Maps.newTreeMap();
        classValues.forEach(clz -> classCountMap.put(clz, 0));
        dataSet.forEach(data -> {
            String clz = String.valueOf(data[classColumn]);
//            classCountMap.merge(clz, 1, (a, b) -> a + b);
            classCountMap.put(clz, classCountMap.get(clz) + 1);
        });
        // 计算经验条件熵
        List<Integer> childClassCountList = Lists.newArrayList(classCountMap.values());
        int totalCount = classCountMap.values().stream().mapToInt(c -> c).sum();
        double entropy = entropy(totalCount, childClassCountList);
        return totalCount * entropy;
    }

}
