package com.bubble.bnlp.classify.decisiontree.test;

/**
 * @author wugang
 * date: 2018-11-07 11:48
 **/
public class DTMain {

    public static void main(String[] args) throws Exception {
        String fileName = "/Users/wugang/workspace/java/BubbleNLP/bnlp-classify/src/main/java/com/bubble/bnlp/classify/decisionTree/test/weather.txt";
//        fileName = "/Users/wugang/workspace/java/BubbleNLP/bnlp-classify/src/main/java/com/bubble/bnlp/classify/decisionTree/test/test.csv";
        TextProcessing textProcessing = new TextProcessing();
        textProcessing.readText(fileName);
        textProcessing.show();

        BuildTree DecisionTree = new BuildTree();
        BuildTree.TreeNode root = DecisionTree.creat(textProcessing, null, null);
        System.out.println("------------show--------------------");
        DecisionTree.show(root);
        SaveTree objSave = new SaveTree();
        objSave.save(root);
    }

}
