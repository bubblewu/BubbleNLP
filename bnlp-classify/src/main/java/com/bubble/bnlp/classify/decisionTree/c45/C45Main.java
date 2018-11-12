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
        String file = "/Users/wugang/workspace/java/BubbleNLP/bnlp-classify/src/main/java/com/bubble/bnlp/classify/decisionTree/c45/PlayTennis.txt";
        List<List<String>> dataSet = DecisionTreeUtils.getTrainingData(file);
        DecisionTreeUtils.transformContinuouslyVariables(dataSet);
        TreeNode treeNode = C45Model.createDecisionTree(dataSet);
        DecisionTreeUtils.showDecisionTree(treeNode, "");
    }

}
