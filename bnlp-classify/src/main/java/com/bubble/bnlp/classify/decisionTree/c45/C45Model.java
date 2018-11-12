package com.bubble.bnlp.classify.decisionTree.c45;

import com.bubble.bnlp.classify.decisionTree.TreeNode;
import com.bubble.bnlp.classify.decisionTree.id3.ID3Model;
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


    /**
     * 构造C4.5决策树
     *
     * @param dataSet 数据集D
     * @return C4.5决策树模型
     */
    public static TreeNode createDecisionTree(List<List<String>> dataSet) {
        Map<String, Double> maxIGRMap = InformationGainRatio.maxInformationGainRatio(dataSet);
        String optimalFeatureName = maxIGRMap.keySet().iterator().next();
        double maxIG = maxIGRMap.get(optimalFeatureName);
        LOGGER.info("optimal feature is [{}], information gain ratio value is [{}].", optimalFeatureName, maxIG);
        TreeNode treeNode = new TreeNode();
        treeNode.setNode(optimalFeatureName);
        genTreeNode(dataSet, treeNode);
        return treeNode;
    }

    private static void genTreeNode(List<List<String>> dataSet, TreeNode treeNode) {
        ID3Model id3Model = new ID3Model();
        id3Model.genTreeNode(dataSet, treeNode);
    }

}
