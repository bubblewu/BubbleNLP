package com.bubble.bnlp.classify.decisionTree.id3;

import com.bubble.bnlp.classify.decisionTree.DecisionTreeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 决策树生成之ID3算法（计算信息增益（最大值）来进行最优特征选择）
 * ID3算法只有树的生成，会产生模型与训练数据的过拟合。
 *
 * @author wugang
 * date: 2018-11-07 16:06
 **/
public class ID3Model {
    private static final Logger LOGGER = LoggerFactory.getLogger(ID3Model.class);

    /**
     * 构造ID3决策树
     *
     * @param dataSet 训练数据集D
     * @return 决策树模型
     */
    public TreeNode createDecisionTree(List<List<String>> dataSet) {
        Map<String, Double> maxIGMap = InformationGain.maxInformationGain(dataSet);
        String optimalFeatureName = maxIGMap.keySet().iterator().next();
        double maxIG = maxIGMap.get(optimalFeatureName);
        LOGGER.info("optimal feature is [{}], information gain value is [{}].", optimalFeatureName, maxIG);
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
    private void genTreeNode(List<List<String>> dataSet, TreeNode treeNode) {
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
     * 构建ID3决策树
     *
     * @param attributeValue            特征值
     * @param splitFeatureAttributeList 未参与计算的特征和特征值的集合
     * @param treeNode                  决策树
     */
    private void buildDecisionTree(String attributeValue, List<List<String>> splitFeatureAttributeList, TreeNode treeNode) {
        Map<String, Double> maxIGMap = InformationGain.maxInformationGain(splitFeatureAttributeList);
        String optimalFeatureName = maxIGMap.keySet().iterator().next();
        double maxIG = maxIGMap.get(optimalFeatureName);

        if (maxIG == 0.0) {
            List<String> singleLineData = splitFeatureAttributeList.get(splitFeatureAttributeList.size() - 1);
            TreeNode leafNode = new TreeNode();
            String classValue = singleLineData.get(singleLineData.size() - 1);
            leafNode.setLeafValue(classValue);
            leafNode.setLeaf(true);
            leafNode.setDirectedEdgeValue(attributeValue);
            treeNode.addChildNodes(leafNode);
            return;
        }

        // 构建子树结点
        TreeNode childNode = new TreeNode();
        childNode.setNode(optimalFeatureName);
        childNode.setDirectedEdgeValue(attributeValue);
        treeNode.addChildNodes(childNode);

        setAttributeNodeStatus(splitFeatureAttributeList, childNode);
    }

    /**
     * 设置特征属性结点的分支及子结点
     *
     * @param currentData 当前数据集
     * @param node        特征结点
     */
    private void setAttributeNodeStatus(List<List<String>> currentData, TreeNode node) {
        // 获取某特征下的所有特征值
        List<String> attributeList = DecisionTreeUtils.getFeatureValueList(currentData, node.getNode());
        int featureIndex = DecisionTreeUtils.getFeatureIndex(currentData.get(0), node.getNode());

        attributeList.forEach(attribute -> {
            // 切分数据集D中的数据
            List<List<String>> splitAttributeDataList = DecisionTreeUtils.splitAttributeDataList(currentData, attribute, featureIndex);
            buildDecisionTree(attribute, splitAttributeDataList, node);
        });
    }


}
