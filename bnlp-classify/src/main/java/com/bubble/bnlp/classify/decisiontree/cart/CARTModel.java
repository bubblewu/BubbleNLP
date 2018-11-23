package com.bubble.bnlp.classify.decisiontree.cart;

import com.bubble.bnlp.bean.exception.DecisionTreeException;
import com.bubble.bnlp.classify.decisiontree.DecisionTreeUtils;
import com.bubble.bnlp.classify.decisiontree.TreeNode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 决策树之CART分类与回归算法
 *
 * @author wugang
 * date: 2018-11-12 11:35
 **/
public class CARTModel {
    private static final Logger LOGGER = LoggerFactory.getLogger(CARTModel.class);

    public static TreeNode createDecisionTree(List<List<String>> dataSet) {
        GiNiTuple<String, String, Double> minGiNiTuple = GiNiCoefficient.minGiNiTuple(dataSet);
        transformContinuouslyVariablesByThreshold(dataSet, minGiNiTuple);
        TreeNode treeNode = new TreeNode();
        String optimalFeatureName = minGiNiTuple.getFeature();
        treeNode.setNode(optimalFeatureName);
        genTreeNode(dataSet, treeNode);
        return treeNode;
    }


    /**
     * 根据最小基尼指数对原数据二分转换(将除最优特征外的其他属性改为"Negative"分支)
     *
     * @param dataSet      训练数据集D
     * @param minGiNiTuple 最小基尼指数对应的"特征-属性-基尼指数" 三元组
     */
    private static void transformContinuouslyVariablesByThreshold(List<List<String>> dataSet, GiNiTuple<String, String, Double> minGiNiTuple) {
        if (null == dataSet) {
            LOGGER.error("data set is null, get attribute name error.");
            throw new DecisionTreeException("data set is null, get feature name error.");
        }
        List<String> featureNameList = dataSet.get(0);
        featureNameList = DecisionTreeUtils.getFeatureNames(featureNameList);

        int findIndex = 0;
        for (int i = 0; i < featureNameList.size(); i++) {
            if (featureNameList.get(i).equals(minGiNiTuple.getFeature())) {
                findIndex = i + 1;
            }
        }

        for (int i = 1; i < dataSet.size(); i++) {
            List<String> rowData = dataSet.get(i);
            if (!rowData.get(findIndex).equals(minGiNiTuple.getAttribute())) {
                rowData.set(findIndex, "Negative");
            }
        }
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
     * 构建CART决策树
     *
     * @param attribute                 属性
     * @param splitFeatureAttributeList 未参与计算的特征和特征值的集合
     * @param treeNode                  决策树
     */
    private static void buildDecisionTree(String attribute, List<List<String>> splitFeatureAttributeList, TreeNode treeNode) {
        GiNiTuple<String, String, Double> minGiNiTuple = GiNiCoefficient.minGiNiTuple(splitFeatureAttributeList);
        transformContinuouslyVariablesByThreshold(splitFeatureAttributeList, minGiNiTuple);
        // 剪枝
        // 获取该列连续变量值对应的类值
        Set<String> classifySet = getClassifyList(splitFeatureAttributeList);
        if (classifySet.size() == 1) {
            TreeNode leafNode = new TreeNode();
            leafNode.setLeaf(true);
            leafNode.setLeafValue(classifySet.iterator().next());
            leafNode.setDirectedEdgeValue(attribute);
            treeNode.addChildNodes(leafNode);
            return;
        }

        String optimalFeatureName = minGiNiTuple.getFeature();
        if (StringUtils.isEmpty(optimalFeatureName)) {
            List<String> singleLineData = splitFeatureAttributeList.get(splitFeatureAttributeList.size() - 1);
            TreeNode leafNode = new TreeNode();
            leafNode.setLeaf(true);
            leafNode.setLeafValue(singleLineData.get(singleLineData.size() - 1));
            leafNode.setDirectedEdgeValue(attribute);
            treeNode.addChildNodes(leafNode);
            return;
        }
        // 构建子树结点
        TreeNode childNode = genChildTreeNode(optimalFeatureName, attribute, treeNode);
        genTreeNode(splitFeatureAttributeList, childNode);
    }

    private static TreeNode genChildTreeNode(String feature, String attribute, TreeNode treeNode) {
        TreeNode childNode = new TreeNode();
        childNode.setNode(feature);
        childNode.setDirectedEdgeValue(attribute);
        treeNode.addChildNodes(childNode);
        return childNode;
    }

    /**
     * 获得当前数据集下的所有类值列表
     *
     * @param currentData 当前数据集
     * @return 类值列表
     */
    private static Set<String> getClassifyList(List<List<String>> currentData) {
        Set<String> classifyList = new HashSet<>();
        for (int rowIndex = 1; rowIndex < currentData.size(); rowIndex++) {
            List<String> rowData = currentData.get(rowIndex);
            classifyList.add(rowData.get(rowData.size() - 1));
        }
        return classifyList;
    }

}