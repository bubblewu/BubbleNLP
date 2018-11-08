package com.bubble.bnlp.classify.decisionTree.id3;

import com.bubble.bnlp.classify.decisionTree.DecisionTreeUtils;

import java.util.List;

/**
 * @author wugang
 * date: 2018-11-07 15:45
 **/
public class ID3Main {

    public static void main(String[] args) {
        ID3Model id3Model = new ID3Model();
        String fileName = "/Users/wugang/workspace/java/BubbleNLP/bnlp-classify/src/main/resources/training/weather.txt";
        List<List<String>> currentData = DecisionTreeUtils.getTrainingData(fileName);
        TreeNode treeNode = id3Model.createDecisionTree(currentData);
        DecisionTreeUtils.showDecisionTree(treeNode, "");

        DecisionTreeUtils.saveTree2XML(treeNode, "/Users/wugang/workspace/java/BubbleNLP/bnlp-classify/src/main/resources/model/weather-tree.xml");
    }

}
