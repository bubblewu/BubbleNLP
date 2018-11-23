package com.bubble.bnlp.classify.decisiontree.c45;

import com.bubble.bnlp.classify.decisiontree.TreePredicate;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * @author wugang
 * date: 2018-11-09 10:14
 **/
public class C45Main {
    private static final String DATA_BASE_PATH = "/Users/wugang/workspace/java/BubbleNLP/bnlp-classify/src/main/resources/data/";
    private static final String MODEL_BASE_PATH = "/Users/wugang/workspace/java/BubbleNLP/bnlp-classify/src/main/resources/model/";

    public static void main(String[] args) {
        String fileName = "PlayTennis";
        String inputFile = DATA_BASE_PATH + "training/tree/" + fileName + ".txt";
        String outputFile = MODEL_BASE_PATH + "tree/" + fileName + "-c45-tree.xml";

        C45Model.train(inputFile, outputFile);
//        predicateTips(outputFile);
//        predicatePlayTennis(outputFile);
    }

    private static void predicateTips(String modelFile) {
        Map<String, String> tipMap = Maps.newHashMap();
//        locations airCompanies airline bct fn positions
        tipMap.put("locations", "NYC");
        tipMap.put("airCompanies", "AC1");
        tipMap.put("airline", "-1");
        tipMap.put("bct", "-1");
        tipMap.put("fn", "JD460");
        tipMap.put("positions", "-1");
        TreePredicate treePredicate = new TreePredicate();
        List<String> items = treePredicate.predicate(tipMap, modelFile);
        String commonFile = DATA_BASE_PATH + "common/tree/common-tips.txt";
        System.out.println("predicate classify: ");
        items.forEach(System.out::println);
        System.out.println("common classify: ");
        treePredicate.loadCommonClassify(commonFile).forEach(System.out::println);
    }

    private static void predicatePlayTennis(String modelFile) {
        Map<String, String> dataMap = Maps.newHashMap();
//        dataMap.put("Day", "1");
        dataMap.put("OutLook", "Sunny");
        dataMap.put("Temperature", "60");
        dataMap.put("Humidity", "90");
        dataMap.put("Wind", "YES");
        TreePredicate treePredicate = new TreePredicate();
        List<String> items = treePredicate.predicate(dataMap, modelFile);
        items.forEach(System.out::println);
    }

}
