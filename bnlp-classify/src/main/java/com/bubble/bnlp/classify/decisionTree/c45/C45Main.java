package com.bubble.bnlp.classify.decisionTree.c45;

import com.bubble.bnlp.classify.decisionTree.DecisionTreeUtils;
import com.bubble.bnlp.classify.decisionTree.TreeNode;

import java.util.List;

/**
 * @author wugang
 * date: 2018-11-09 10:14
 **/
public class C45Main {

    public static void main(String[] args) {
        String basePath = "/Users/wugang/workspace/java/BubbleNLP/bnlp-classify/src/main/resources/training/tree/";
        String fileName = "tips";
        String file = basePath + fileName + ".txt";
        List<List<String>> dataSet = DecisionTreeUtils.getTrainingData(file);
        DecisionTreeUtils.transformContinuouslyVariables(dataSet);
        TreeNode treeNode = C45Model.createDecisionTree(dataSet);
        DecisionTreeUtils.showDecisionTree(treeNode, "");
        DecisionTreeUtils.saveTree2XML(treeNode, "/Users/wugang/workspace/java/BubbleNLP/bnlp-classify/src/main/resources/model/tree/" + fileName + "-c45-tree.xml");
    }

}
