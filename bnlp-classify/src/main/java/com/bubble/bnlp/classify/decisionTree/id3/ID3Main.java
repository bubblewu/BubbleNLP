package com.bubble.bnlp.classify.decisionTree.id3;

import com.bubble.bnlp.classify.decisionTree.DecisionTreeUtils;
import com.bubble.bnlp.classify.decisionTree.id3.client.ID3Client;
import com.bubble.bnlp.classify.decisionTree.id3.core.ID3Core;

import java.io.IOException;
import java.util.List;

/**
 * @author wugang
 * date: 2018-11-07 15:45
 **/
public class ID3Main {

    public static void main(String[] args) throws IOException {
        String fileName = "/Users/wugang/workspace/java/BubbleNLP/bnlp-classify/src/main/java/com/bubble/bnlp/classify/decisionTree/id3/weather.txt";
        List<List<String>> currentData = DecisionTreeUtils.getTrainingData(fileName);
        ID3Core core = new ID3Core();
        ID3Client id3Client = new ID3Client();
        id3Client.createDecisionTree(core, currentData);
    }

}
