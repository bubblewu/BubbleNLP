package com.bubble.bnlp.classify.decisiontree.c45;

import com.bubble.bnlp.classify.decisiontree.DecisionTreeUtils;
import com.bubble.bnlp.classify.decisiontree.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 决策树之C45算法
 *
 * @author wugang
 * date: 2018-11-09 11:03
 **/
public class C45Model {
    private static final Logger LOGGER = LoggerFactory.getLogger(C45Model.class);

    static void train(String inputFile, String outputFile) {
        List<List<String>> dataSet = DecisionTreeUtils.getTrainingData(inputFile);
        DecisionTreeUtils.transformContinuouslyVariables(dataSet);
        TreeNode treeNode = C45Model.createDecisionTree(dataSet);
        DecisionTreeUtils.showDecisionTree(treeNode, "");

        FixTree.fix(treeNode);
        DecisionTreeUtils.showDecisionTree(treeNode, "");
        DecisionTreeUtils.saveTree2XML(treeNode, outputFile);
    }

    /**
     * 构造C4.5决策树
     *
     * @param dataSet 数据集D
     * @return C4.5决策树模型
     */
    private static TreeNode createDecisionTree(List<List<String>> dataSet) {
        Map<String, Double> maxIGRMap = InformationGainRatio.maxInformationGainRatio(dataSet);
        String optimalFeatureName = maxIGRMap.keySet().iterator().next();
        double maxIG = maxIGRMap.get(optimalFeatureName);
        LOGGER.info("optimal feature is [{}], information gain ratio value is [{}].", optimalFeatureName, maxIG);
        TreeNode treeNode = new TreeNode();
        treeNode.setNode(optimalFeatureName);
        genTreeNode(dataSet, treeNode);
        return treeNode;
    }

    /**
     * 生成树的各结点、有向边和叶子结点
     *
     * @param dataSet  数据集
     * @param treeNode 特征结点
     */
    private static void genTreeNode(List<List<String>> dataSet, TreeNode treeNode) {
        // 获取某特征下的所有特征值
        List<String> attributeList = DecisionTreeUtils.getFeatureValueList(dataSet, treeNode.getNode());
        // 获得特征在原数据中所处的列索引
        int featureIndex = DecisionTreeUtils.getFeatureIndex(dataSet.get(0), treeNode.getNode());
        // 将这个最大信息增益对应的特征属性进行移除，并将数据集切分成N个部分，其中N为分支状态数，即特征值的个数）
        attributeList.forEach(attribute -> {
            // 未参与计算的特征和特征值的集合
            List<List<String>> splitFeatureAttributeList = DecisionTreeUtils.splitAttributeDataList(dataSet, attribute, featureIndex);
            //把这N份数据分别当成上面的dataSet代入计算。通过这样循环地迭代，直到数据被全部计算完成。
            buildDecisionTree(attribute, splitFeatureAttributeList, treeNode);
        });
    }

    /**
     * 构建C4.5决策树
     *
     * @param attributeValue            特征值
     * @param splitFeatureAttributeList 未参与计算的特征和特征值的集合
     * @param treeNode                  决策树
     */
    private static void buildDecisionTree(String attributeValue, List<List<String>> splitFeatureAttributeList, TreeNode treeNode) {
        Map<String, Double> maxIGRMap = InformationGainRatio.maxInformationGainRatio(splitFeatureAttributeList);
        String optimalFeatureName = maxIGRMap.keySet().iterator().next();
        double maxIGR = maxIGRMap.get(optimalFeatureName);

        if (maxIGR == 0.0) {
            List<String> singleLineData = splitFeatureAttributeList.get(splitFeatureAttributeList.size() - 1);
            TreeNode leafNode = new TreeNode();
            leafNode.setLeaf(true);
            String classify = singleLineData.get(singleLineData.size() - 1);
            leafNode.setLeafValue(classify);
            leafNode.setDirectedEdgeValue(attributeValue);
            treeNode.addChildNodes(leafNode);
            return;
        }

        // 构建子树结点
        TreeNode childNode = new TreeNode();
        childNode.setNode(optimalFeatureName);
        childNode.setDirectedEdgeValue(attributeValue);
        treeNode.addChildNodes(childNode);

        genTreeNode(splitFeatureAttributeList, childNode);
    }

}
