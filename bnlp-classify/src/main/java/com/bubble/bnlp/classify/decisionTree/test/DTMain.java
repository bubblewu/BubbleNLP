package com.bubble.bnlp.classify.decisionTree.test;

/**
 * @author wugang
 * date: 2018-11-07 11:48
 **/
public class DTMain {

    public static void main(String[] args) throws Exception {
        String fileName = "/Users/wugang/workspace/java/BubbleNLP/bnlp-classify/src/main/java/com/bubble/bnlp/classify/decisionTree/test.csv";
        TextProcessing TextPro = new TextProcessing();
        TextPro.readText(fileName);

        BuildTree DecisionTree = new BuildTree();
        BuildTree.TreeNode root = DecisionTree.creat(TextPro, null, null);
        System.out.println("------------show--------------------");
        DecisionTree.show(root);
        SaveTree objSave = new SaveTree();
        objSave.save(root);
    }

}
