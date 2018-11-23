package com.bubble.bnlp.classify.decisiontree.c45;

import com.bubble.bnlp.bean.exception.DecisionTreeException;
import com.bubble.bnlp.classify.decisiontree.DecisionTreeUtils;
import com.bubble.bnlp.classify.decisiontree.id3.InformationGain;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * C4.5算法的特征选择：信息增益比
 *
 * @author wugang
 * date: 2018-11-09 11:46
 **/
public class InformationGainRatio {
    private static final Logger LOGGER = LoggerFactory.getLogger(InformationGainRatio.class);

    /**
     * C4.5算法采用的利用最大的信息增益比（Information Gain Ratio）来作为最优特征
     *
     * @param dataSet 数据集D
     * @return 最大信息增益值比和对应特征（最优特征）
     */
    public static Map<String, Double> maxInformationGainRatio(List<List<String>> dataSet) {
        if (null == dataSet) {
            LOGGER.error("data set is null, get attribute name error.");
            throw new DecisionTreeException("data set is null, get feature name error.");
        }
        List<String> featureNameList = dataSet.get(0);
        featureNameList = DecisionTreeUtils.getFeatureNames(featureNameList);
        Map<String, Double> maxIGRatioMap = Maps.newHashMap();
        double maxIGRatio = 0.0;
        String maxIGRFeatureName = "";
        for (String featureName : featureNameList) {
            double currentIGRatio = currentFeatureInformationGainRatio(dataSet, featureName);
            if (maxIGRatio <= currentIGRatio) {
                maxIGRatio = currentIGRatio;
                maxIGRFeatureName = featureName;
            }
        }
        maxIGRatioMap.put(maxIGRFeatureName, maxIGRatio);
        return maxIGRatioMap;
    }

    /**
     * 计算当前特征的信息增益比: Gr(D,A) = G(D,A) / Ha(D)
     * 其中信息增益为 G(D|A) = H(D) - H(D|A)
     *
     * @param dataSet     数据集
     * @param featureName 当前特征名
     * @return 信息增益值
     */
    private static double currentFeatureInformationGainRatio(List<List<String>> dataSet, String featureName) {
        return InformationGain.currentFeatureInformationGain(dataSet, featureName) / currentFeatureEntropy(dataSet, featureName);
    }

    /**
     * 计算 H(D|A)训练数据集关于特征A对区间值（属性）的熵
     *
     * @param dataSet     训练数据集D
     * @param featureName 特征
     * @return 熵
     */
    private static double currentFeatureEntropy(List<List<String>> dataSet, String featureName) {
        // 获得目标特征在原数据中所处的列索引
        int featureIndex = DecisionTreeUtils.getFeatureIndex(dataSet.get(0), featureName);
        // 获取某特征下各个特征值所对应的类值分布
        Map<String, Map<String, Integer>> attributeClassifyMap = DecisionTreeUtils.getAttributeClassifyMap(dataSet, featureIndex);
        double conditionalEntropy = 0.0;
        int totalCount = dataSet.size() - 1;
        for (String attribute : attributeClassifyMap.keySet()) {
            // 当前特征下某区间值的信息熵
            double attributeEntropy = InformationGain.currentEmpiricalEntropy(attributeClassifyMap.get(attribute));
            // 计算当前特征下某区间值的条件概率
            double attributeProbability = InformationGain.currentAttributeProbability(attributeClassifyMap.get(attribute), totalCount);
            // 计算 H(D|A)的值
            conditionalEntropy += (attributeEntropy * attributeProbability);
        }
        return conditionalEntropy;
    }

}
