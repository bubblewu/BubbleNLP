package com.bubble.bnlp.classify.decisionTree;

import com.bubble.bnlp.classify.decisionTree.id3.ID3Model;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * @author wugang
 * date: 2018-11-15 17:38
 **/
public class TreePredicate {

    public static void main(String[] args) {
        String model = "/Users/wugang/workspace/java/BubbleNLP/bnlp-classify/src/main/resources/model/tree/";
        String modelFile = model + "tips-c45-tree.xml";
        Map<String, String> tipMap = Maps.newHashMap();
//        locations airCompanies airline bct fn positions
        tipMap.put("locations", "AMM");
        tipMap.put("airCompanies", "AC");
        tipMap.put("airline", "-1");
        tipMap.put("bct", "-1");
        tipMap.put("fn", "JD460");
        tipMap.put("positions", "-1");

        ID3Model id3Model = new ID3Model();
        List<String> items = id3Model.predicate(tipMap, modelFile);
        items.forEach(System.out::println);
    }

}
