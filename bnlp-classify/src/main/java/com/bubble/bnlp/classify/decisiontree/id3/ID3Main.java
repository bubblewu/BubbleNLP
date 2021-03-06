package com.bubble.bnlp.classify.decisiontree.id3;

import com.bubble.bnlp.classify.decisiontree.DecisionTreeUtils;
import com.bubble.bnlp.classify.decisiontree.TreeNode;

import java.util.List;

/**
 * @author wugang
 * date: 2018-11-07 15:45
 **/
public class ID3Main {

    public static void main(String[] args) {
        create();
    }

    private static void create (){
        String basePath = "/Users/wugang/workspace/java/BubbleNLP/bnlp-classify/src/main/resources/training/tree/";
        String fileName = "tips";
        String file = basePath + fileName + ".txt";
        List<List<String>> currentData = DecisionTreeUtils.getTrainingData(file);
        ID3Model id3Model = new ID3Model();
        TreeNode treeNode = id3Model.createDecisionTree(currentData);
        DecisionTreeUtils.showDecisionTree(treeNode, "");

        DecisionTreeUtils.saveTree2XML(treeNode, "/Users/wugang/workspace/java/BubbleNLP/bnlp-classify/src/main/resources/model/tree/" + fileName + "-id3-tree.xml");

    }


}
