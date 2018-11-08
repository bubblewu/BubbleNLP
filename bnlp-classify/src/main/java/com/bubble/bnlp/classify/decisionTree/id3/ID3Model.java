package com.bubble.bnlp.classify.decisionTree.id3;

import com.bubble.bnlp.classify.decisionTree.DecisionTreeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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

    public static void main(String[] args) throws IOException {
        String fileName = "/Users/wugang/workspace/java/BubbleNLP/bnlp-classify/src/main/java/com/bubble/bnlp/classify/decisionTree/id3/weather.txt";
        List<List<String>> currentData = DecisionTreeUtils.getTrainingData(fileName);
        createDecisionTree(currentData);
    }

    private static void createDecisionTree(List<List<String>> dataSet) {
        Map<String, Double> maxIGMap = InformationGain.maxInformationGain(dataSet);
        maxIGMap.forEach((k, v) -> {
            System.out.println(k);
            System.out.println(v);
        });
        String optimalAttributeName = maxIGMap.keySet().iterator().next();
        TreeNode rootNode = new TreeNode(optimalAttributeName);
        setTreeNodeStatus(dataSet, rootNode);
        DecisionTreeUtils.showDecisionTree(rootNode, "");
    }

    /**
     * 设置特征结点的分支及子结点
     *
     * @param dataSet  数据集
     * @param rootNode 特征结点
     */
    private static void setTreeNodeStatus(List<List<String>> dataSet, TreeNode rootNode) {
        // 获取某特征下的所有特征值
        List<String> attributeCVList = DecisionTreeUtils.getAttributeValueList(dataSet, rootNode.getAttributeName());
        // 获得特征在原数据中所处的列索引
        int attributeIndex = DecisionTreeUtils.getAttributeIndex(dataSet.get(0), rootNode.getAttributeName());
        // 将这个最大信息增益对应的特征属性进行移除，并将数据集切分成N个部分，其中N为分支状态数，即特征值的个数）
        attributeCVList.forEach(attributeValue -> {
            // 未参与计算的特征和特征值的集合
            List<List<String>> splitAttributeDataList = DecisionTreeUtils.splitAttributeDataList(dataSet, attributeValue, attributeIndex);
            //把这N份数据分别当成上面的dataSet代入计算。通过这样循环地迭代，直到数据被全部计算完成。
            buildDecisionTree(attributeValue, splitAttributeDataList, rootNode);
        });
    }

    /**
     * 构建ID3决策树
     *
     * @param attributeValue         特征值
     * @param splitAttributeDataList 未参与计算的特征和特征值的集合
     * @param node                   特征结点
     */
    private static void buildDecisionTree(String attributeValue, List<List<String>> splitAttributeDataList, TreeNode node) {
        Map<String, Double> maxIGMap = InformationGain.maxInformationGain(splitAttributeDataList);
        String optimalAttributeName = maxIGMap.keySet().iterator().next();
        double maxIG = maxIGMap.get(optimalAttributeName);

        if (maxIG == 0.0) {
            List<String> singleLineData = splitAttributeDataList.get(splitAttributeDataList.size() - 1);
            TreeNode leafNode = new TreeNode(singleLineData.get(singleLineData.size() - 1));
            leafNode.setLeaf(true);
            leafNode.setParentStatus(attributeValue);
            node.addChildNodes(leafNode);
            return;
        }

        TreeNode attributeNode = getNewAttributeNode(optimalAttributeName, attributeValue, node);

        setAttributeNodeStatus(splitAttributeDataList, attributeNode);
    }

    /**
     * @param attributeName
     * @param attributeValue
     * @param node
     * @return
     */
    private static TreeNode getNewAttributeNode(String attributeName, String attributeValue, TreeNode node) {
        TreeNode attributeNode = new TreeNode(attributeName);
        attributeNode.setParentStatus(attributeValue);
        node.addChildNodes(attributeNode);
        return attributeNode;
    }

    /**
     * 设置特征属性结点的分支及子结点
     *
     * @param currentData 当前数据集
     * @param rootNode    特征结点
     */
    private static void setAttributeNodeStatus(List<List<String>> currentData, TreeNode rootNode) {
        List<String> attributeBranchList = DecisionTreeUtils.getAttributeValueList(currentData, rootNode.getAttributeName());
        int attributeIndex = DecisionTreeUtils.getAttributeIndex(currentData.get(0), rootNode.getAttributeName());

        for (String attributeBranch : attributeBranchList) {
            List<List<String>> splitAttributeDataList = DecisionTreeUtils.splitAttributeDataList(currentData, attributeBranch, attributeIndex);
            buildDecisionTree(attributeBranch, splitAttributeDataList, rootNode);
        }
    }


}
