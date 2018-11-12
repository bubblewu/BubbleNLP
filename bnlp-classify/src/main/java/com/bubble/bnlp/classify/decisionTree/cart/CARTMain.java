package com.bubble.bnlp.classify.decisionTree.cart;

import com.bubble.bnlp.classify.decisionTree.DecisionTreeUtils;
import com.bubble.bnlp.classify.decisionTree.TreeNode;

import java.util.List;

/**
 * @author wugang
 * date: 2018-11-12 11:29
 **/
public class CARTMain {

    public static void main(String[] args) {
        String basePath = "/Users/wugang/workspace/java/BubbleNLP/bnlp-classify/src/main/java/com/bubble/bnlp/classify/decisionTree/cart/";
        String fileName = "loan";
        String file = basePath + fileName + ".txt";
        List<List<String>> dataSet = DecisionTreeUtils.getTrainingData(file);
        TreeNode treeNode = CARTModel.createDecisionTree(dataSet);
        DecisionTreeUtils.showDecisionTree(treeNode, "");
        DecisionTreeUtils.saveTree2XML(treeNode, "/Users/wugang/workspace/java/BubbleNLP/bnlp-classify/src/main/resources/model/" + fileName + "-tree.xml");
    }

}
