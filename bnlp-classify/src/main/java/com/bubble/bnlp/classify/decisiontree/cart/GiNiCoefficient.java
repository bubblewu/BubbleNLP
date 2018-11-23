package com.bubble.bnlp.classify.decisiontree.cart;

import com.bubble.bnlp.bean.exception.DecisionTreeException;
import com.bubble.bnlp.classify.decisiontree.ContinuouslyVariable;
import com.bubble.bnlp.classify.decisiontree.DecisionTreeUtils;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 基尼指数
 *
 * @author wugang
 * date: 2018-11-12 11:34
 **/
public class GiNiCoefficient {
    private static final Logger LOGGER = LoggerFactory.getLogger(GiNiCoefficient.class);

    /**
     * 利用基尼指数（极小化原则）选择最优特征
     *
     * @param dataSet 训练数据集D
     * @return "最优特征-属性-基尼指数" 三元组
     */
    public static GiNiTuple<String, String, Double> minGiNiTuple(List<List<String>> dataSet) {
        if (null == dataSet) {
            LOGGER.error("data set is null, get attribute name error.");
            throw new DecisionTreeException("data set is null, get feature name error.");
        }
        List<String> featureNameList = dataSet.get(0);
        List<String> featureList = DecisionTreeUtils.getFeatureNames(featureNameList);
        GiNiTuple<String, String, Double> minGiNiTuple = new GiNiTuple<>();
        String feature = "";
        String attribute = "";
        double minGiNi = Double.MAX_VALUE;
        for (String featureName : featureList) {
            Map<String, Double> giniMap = currentFeatureGiNi(dataSet, featureName);
            for (String key : giniMap.keySet()) {
                double giniTemp = giniMap.get(key);
                if (giniTemp < minGiNi) {
                    minGiNi = giniTemp;
                    attribute = key;
                    feature = featureName;
                }
            }
        }
        minGiNiTuple.setFeature(feature);
        minGiNiTuple.setAttribute(attribute);
        minGiNiTuple.setGini(minGiNi);
        return minGiNiTuple;
    }

    /**
     * 计算当前特征的基尼指数
     *
     * @param dataSet     数据集D
     * @param featureName 特征
     * @return 基尼指数
     */
    private static Map<String, Double> currentFeatureGiNi(List<List<String>> dataSet, String featureName) {
        int featureIndex = DecisionTreeUtils.getFeatureIndex(dataSet.get(0), featureName);
        Map<String, Map<String, Integer>> attributeClassifyMap = DecisionTreeUtils.getAttributeClassifyMap(dataSet, featureIndex);

        if (NumberUtils.isDigits(attributeClassifyMap.keySet().iterator().next())) {
            // 处理连续变量
            return continuouslyVariable(attributeClassifyMap);
        } else {
            // 处理离散变量
            return discreteVariable(attributeClassifyMap);
        }
    }

    /**
     * 处理离散变量：获得最佳的分割属性和对应最小基尼值
     *
     * @param attributeClassifyMap 某特征的各属性对应的类值分布
     * @return 离散变量的分割阈值："属性 - 最小基尼值"二元组
     */
    private static Map<String, Double> discreteVariable(Map<String, Map<String, Integer>> attributeClassifyMap) {
        String minAttribute = "";
        double miniGiNi = Double.MAX_VALUE;
        Map<String, Double> attributeMinGiNiMap = Maps.newHashMap();
        Set<String> attributeSet = attributeClassifyMap.keySet();
        for (String attribute : attributeSet) {
            double gini = currentAttributeDichotomyGINI(attributeClassifyMap, attribute, attributeSet);
            System.out.println(gini);
            if (miniGiNi >= gini) {
                miniGiNi = gini;
                minAttribute = attribute;
            }
        }
        attributeMinGiNiMap.put(minAttribute, miniGiNi);
        return attributeMinGiNiMap;
    }

    /**
     * 计算某一特征下，被某一属性二分化后的基尼指数
     *
     * @param attributeClassifyMap 某特征的各属性对应的类值分布
     * @param attribute            属性
     * @param attributeSet         某特征的所有属性集合
     * @return 基尼指数
     */
    private static double currentAttributeDichotomyGINI(Map<String, Map<String, Integer>> attributeClassifyMap, String attribute, Set<String> attributeSet) {
        Map<String, Map<String, Integer>> attributeDichotomyMap = getAttributeDichotomyMap(attributeClassifyMap, attribute, attributeSet);
        int totalCount = 0;
        int positiveTotalCount = 0; // 正面的数据总数，比如：冷血={爬行类=3, 两栖类=2, 鱼类=3} 的总数为 8
        int negativeTotalCount = 0; // 反面的数据总数，比如：Negative={哺乳类=5, 鸟类=2} 的总数为 7

        Set<String> dichotomyKeySet = attributeDichotomyMap.keySet();
        for (String dichotomyKey : dichotomyKeySet) {
            Set<String> subDichotomyKeySet = attributeDichotomyMap.get(dichotomyKey).keySet();
            if (dichotomyKey.equals(attribute)) {
                for (String subDichotomyKey : subDichotomyKeySet) {
                    positiveTotalCount += attributeDichotomyMap.get(dichotomyKey).get(subDichotomyKey);
                }
            } else {
                for (String subDichotomyKey : subDichotomyKeySet) {
                    negativeTotalCount += attributeDichotomyMap.get(dichotomyKey).get(subDichotomyKey);
                }
            }
        }
        totalCount = positiveTotalCount + negativeTotalCount;

        double positiveGiNi = 0.0;
        double negativeGiNi = 0.0;

        for (String dichotomyKey : dichotomyKeySet) {
            Set<String> subDichotomyKeySet = attributeDichotomyMap.get(dichotomyKey).keySet();
            if (dichotomyKey.equals(attribute)) {
                for (String subDichotomyKey : subDichotomyKeySet) {
                    positiveGiNi += (1.0 * attributeDichotomyMap.get(dichotomyKey).get(subDichotomyKey) / positiveTotalCount) * (1.0 * attributeDichotomyMap.get(dichotomyKey).get(subDichotomyKey) / positiveTotalCount);
                }
            } else {
                for (String subDichotomyKey : subDichotomyKeySet) {
                    negativeGiNi += (1.0 * attributeDichotomyMap.get(dichotomyKey).get(subDichotomyKey) / negativeTotalCount) * (1.0 * attributeDichotomyMap.get(dichotomyKey).get(subDichotomyKey) / negativeTotalCount);
                }
            }
        }

        positiveGiNi = 1 - positiveGiNi;
        negativeGiNi = 1 - negativeGiNi;
        return (1.0 * positiveTotalCount / totalCount) * positiveGiNi + (1.0 * negativeTotalCount / totalCount) * negativeGiNi;
    }

    /**
     * 获得某一特征下，某一个状态的二分化
     *
     * @param attributeClassifyMap 某特征的各属性对应的类值分布
     * @param attribute            属性
     * @param attributeSet         某特征的所有属性集合
     * @return 某特征二分化后的结果
     */
    private static Map<String, Map<String, Integer>> getAttributeDichotomyMap(Map<String, Map<String, Integer>> attributeClassifyMap, String attribute, Set<String> attributeSet) {
        Map<String, Map<String, Integer>> attributeDichotomyMap = new HashMap<>();
        attributeDichotomyMap.put(attribute, attributeClassifyMap.get(attribute));
        String negativeAttribute = "Negative"; // 反面状态信息
        attributeDichotomyMap.put(negativeAttribute, getAttributeDichotomyNegativeMap(attributeClassifyMap, attribute, negativeAttribute, attributeSet));
        return attributeDichotomyMap;
    }

    /**
     * 获得某一特征属性下，某一个状态的反面状态信息
     *
     * @param attributeClassifyMap 某特征的各属性对应的类值分布
     * @param attribute            属性
     * @param negativeAttribute    另一个属性，反面状态信息"Negative"
     * @param attributeSet         某特征的所有属性集合
     * @return 反面状态信息:"类值 - 数量"
     */
    private static Map<String, Integer> getAttributeDichotomyNegativeMap(Map<String, Map<String, Integer>> attributeClassifyMap, String attribute, String negativeAttribute, Set<String> attributeSet) {
        Map<String, Integer> negativeClassifyCountMap = Maps.newHashMap();
        attributeSet.stream().filter(na -> !na.equals(attribute)).forEach(ngAttribute -> {
            attributeClassifyMap.get(ngAttribute).forEach((classify, count) -> {
                if (negativeClassifyCountMap.containsKey(classify)) {
                    negativeClassifyCountMap.put(classify, count + negativeClassifyCountMap.get(classify));
                } else {
                    negativeClassifyCountMap.put(classify, count);
                }
            });
        });
        return negativeClassifyCountMap;
    }

    /**
     * 处理连续变量：获得最佳的分割属性和对应最小基尼值
     *
     * @param attributeClassifyMap 某特征的各属性对应的类值分布
     * @return 连续变量的分割阈值："属性 - 最小基尼值"二元组
     */
    private static Map<String, Double> continuouslyVariable(Map<String, Map<String, Integer>> attributeClassifyMap) {
        // 某特征下的"属性 - 类值"元组集合
        List<ContinuouslyVariable> continuouslyVariableList = DecisionTreeUtils.extractContinuouslyVariable(attributeClassifyMap);
        // 计算连续变量的分割阈值
        return DecisionTreeUtils.getAttributeThreshold(continuouslyVariableList);
    }


}
