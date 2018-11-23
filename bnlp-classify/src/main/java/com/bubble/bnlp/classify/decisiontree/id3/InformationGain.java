package com.bubble.bnlp.classify.decisiontree.id3;

import com.bubble.bnlp.bean.exception.DecisionTreeException;
import com.bubble.bnlp.classify.decisiontree.DecisionTreeUtils;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 信息增益
 *
 * @author wugang
 * date: 2018-11-07 18:54
 **/
public class InformationGain {
    private static final Logger LOGGER = LoggerFactory.getLogger(InformationGain.class);

    /**
     * 计算各个特征的信息增益(Information Gain)，取IG最大值为最优特征
     * 信息增益：表示得知特征X的信息而使类Y的信息不确定性减小。
     * 公式：g(D|A) = H(D) - H(D|A)
     * 其中H(D)为数据集合D的经验熵；H(D|A)为在特征A给定的条件下D的经验条件熵。
     *
     * @param dataSet 数据集
     * @return 最大信息增益值和对应特征（最优特征）
     */
    public static Map<String, Double> maxInformationGain(List<List<String>> dataSet) {
        if (null == dataSet) {
            LOGGER.error("data set is null, get attribute name error.");
            throw new DecisionTreeException("data set is null, get feature name error.");
        }
        List<String> featureNameList = dataSet.get(0);
        featureNameList = DecisionTreeUtils.getFeatureNames(featureNameList);

        Map<String, Double> maxIGMap = Maps.newHashMap();
        double maxIG = -1;
        String maxIGFeatureName = "";
        // 计算每个特征的信息增益, 选择IG最大的特征
        for (String featureName : featureNameList) {
            double currentIG = currentFeatureInformationGain(dataSet, featureName);
            LOGGER.info("current information gain value: {}", currentIG);
            if (maxIG < currentIG) {
                maxIG = currentIG;
                maxIGFeatureName = featureName;
            }
        }
        maxIGMap.put(maxIGFeatureName, maxIG);
        return maxIGMap;
    }

    /**
     * 计算当前特征的信息增益: g(D|A) = H(D) - H(D|A)
     *
     * @param dataSet       数据集
     * @param featureName 当前特征名
     * @return 信息增益值
     */
    public static double currentFeatureInformationGain(List<List<String>> dataSet, String featureName) {
        return empiricalEntropy(dataSet) - conditionalEntropy(dataSet, featureName);
    }

    /**
     * 计算当前状态下某一个属性的信息熵，即H(D|A)为在特征A给定的条件下D的经验条件熵。
     *
     * @param dataSet       数据集D
     * @param featureName 特征名
     * @return 经验条件熵
     */
    private static double conditionalEntropy(List<List<String>> dataSet, String featureName) {
        List<String> attributeNames = dataSet.get(0);
        int totalCount = dataSet.size() - 1; // 集合D中的样本总数
        // 获得目标特征在原数据中所处的列索引
        int featureIndex = DecisionTreeUtils.getFeatureIndex(attributeNames, featureName);
        // 获取某特征下各个特征值所对应的类值分布
        Map<String, Map<String, Integer>> attributeClassifyMap = DecisionTreeUtils.getAttributeClassifyMap(dataSet, featureIndex);

        // 计算在特征属性A的条件下样本的条件熵
        double conditionalEntropy = 0.0;
        for (String attribute : attributeClassifyMap.keySet()) {
            // 当前特征下某区间值的信息熵
            double attributeEntropy = currentEmpiricalEntropy(attributeClassifyMap.get(attribute));
            // 计算当前特征下某区间值的条件概率
            double attributeProbability = currentAttributeProbability(attributeClassifyMap.get(attribute), totalCount);
            // 计算 H(D|A)的值
            conditionalEntropy += (attributeEntropy * attributeProbability);
        }
        return conditionalEntropy;
    }

    /**
     * 计算当前特征下某区间值的条件概率
     *
     * @param classifyMap A特征下某值所对应的类值分布
     * @param totalCount    集合D中的样本总数
     * @return 条件概率
     */
    public static double currentAttributeProbability(Map<String, Integer> classifyMap, int totalCount) {
        int totalCVCount = 0; // 特征A中某区间的样本个数，即某区间对应类值的个数
        for (String classify : classifyMap.keySet()) {
            totalCVCount += classifyMap.get(classify);
        }
        return 1.0 * totalCVCount / totalCount;
    }

    /**
     * 计算A特征下某区间值的经验熵（信息熵）
     *
     * @param classifyMap A特征下某值所对应的类值分布
     * @return A特征下某区间值的经验熵 H(Di)
     */
    public static double currentEmpiricalEntropy(Map<String, Integer> classifyMap) {
        Set<String> cvSet = classifyMap.keySet();
        int totalClassifyCount = 0; // 特征A中某区间的样本个数，即某区间对应类值的个数
        for (String classify : cvSet) {
            totalClassifyCount += classifyMap.get(classify);
        }

        double entropy = 0.0;
        for (String classify : cvSet) {
            double probability = 1.0 * classifyMap.get(classify) / totalClassifyCount;
            entropy -= probability * log2(probability);
        }
        return entropy;
    }

    /**
     * 计算当前状态下的总的信息熵，即H(D)数据集合D的经验熵
     *
     * @param dataSet 数据集D
     * @return 数据集D的经验熵
     */
    private static double empiricalEntropy(List<List<String>> dataSet) {
        // 类值和对应的样本个数
        Map<String, Integer> classResultDistributionMap = classResultDistribution(dataSet);
        // 数据集D的总样本个数
        int totalCount = dataSet.size() - 1;
        double entropy = 0.0;
        for (String cv : classResultDistributionMap.keySet()) {
            int nowCVCount = classResultDistributionMap.get(cv);
            // 某类在数据集D中出现的概率
            double probability = 1.0 * nowCVCount / totalCount;
            entropy -= probability * log2(probability);
        }
        return entropy;
    }

    /**
     * 计算以 2 为底的对数
     */
    private static double log2(double x) {
        return Math.log(x) / Math.log(2.0);
    }

    /**
     * 集合D中类的结果分布：在集合D下的每个类对应的出现次数
     *
     * @param dataSet 数据集合D
     * @return 类的结果分布键值对
     */
    private static Map<String, Integer> classResultDistribution(List<List<String>> dataSet) {
        Map<String, Integer> classResultDistributionMap = Maps.newHashMap();
        dataSet.stream().skip(1).forEach(lineData -> {
            String classResult = lineData.get(lineData.size() - 1);
            if (classResultDistributionMap.containsKey(classResult)) {
                classResultDistributionMap.put(classResult, classResultDistributionMap.get(classResult) + 1);
            } else {
                classResultDistributionMap.put(classResult, 1);
            }
        });
        return classResultDistributionMap;
    }

}
