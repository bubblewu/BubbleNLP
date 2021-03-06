package com.bubble.bnlp.classify.decisiontree.cart;

import com.bubble.bnlp.classify.decisiontree.DecisionTreeUtils;
import com.bubble.bnlp.classify.decisiontree.TreeNode;

import java.util.List;

/**
 * @author wugang
 * date: 2018-11-12 11:29
 **/
public class CARTMain {

    public static void main(String[] args) {
        String basePath = "/Users/wugang/workspace/java/BubbleNLP/bnlp-classify/src/main/resources/training/tree/cart/";
        String fileName = "tips";
        String file = basePath + fileName + ".txt";
        List<List<String>> dataSet = DecisionTreeUtils.getTrainingData(file);
        TreeNode treeNode = CARTModel.createDecisionTree(dataSet);
        DecisionTreeUtils.showDecisionTree(treeNode, "");
        DecisionTreeUtils.saveTree2XML(treeNode, "/Users/wugang/workspace/java/BubbleNLP/bnlp-classify/src/main/resources/model/tree/" + fileName + "-cart-tree.xml");
    }

}
