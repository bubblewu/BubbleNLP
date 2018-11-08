package com.bubble.bnlp.classify.decisionTree.cart.demo;

import com.bubble.bnlp.classify.decisionTree.cart.core.CARTCore;
import com.bubble.bnlp.classify.decisionTree.cart.model.MinGINITuple;
import com.bubble.bnlp.classify.decisionTree.cart.utils.DecisionTreeUtils;

import java.io.IOException;
import java.util.List;

public class Demos {

    public static void main(String[] args) throws IOException {
        String fileName = "/Users/wugang/workspace/java/BubbleNLP/bnlp-classify/src/main/java/com/bubble/bnlp/classify/decisionTree/cart/variety.txt";
        List<List<String>> currentData = DecisionTreeUtils.getTrainingData(fileName);
        CARTCore core = new CARTCore();
        MinGINITuple<String, String, Double> minGINITuple = core.minGiniMap(currentData);
        System.out.println(minGINITuple);
    }
}
