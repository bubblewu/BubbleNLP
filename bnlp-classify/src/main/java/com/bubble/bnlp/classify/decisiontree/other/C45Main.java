package com.bubble.bnlp.classify.decisiontree.other;

import com.bubble.bnlp.bean.tree.structure.TypedDataSet;
import com.bubble.bnlp.common.FileUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * C4.5测试
 *
 * @author wugang
 * date: 2018-12-17 15:56
 **/
public class C45Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(C45Main.class);

    public static void main(String[] args) {
        String dataFile = "/Users/wugang/workspace/java/BubbleNLP/bnlp-classify/src/main/java/com/bubble/bnlp/classify/decisiontree/other/product.data";
        List<String> lines = FileUtils.readFile(dataFile);
        List<String> featureNames = Lists.newArrayList();
        lines.stream().limit(1).forEach(line -> featureNames.addAll(Arrays.stream(StringUtils.split(line, " ")).collect(Collectors.toList())));
        TypedDataSet dataSet = new TypedDataSet(String.class, String.class, String.class, String.class, String.class, String.class);
        lines.stream().skip(1).forEach(line -> dataSet.add(StringUtils.split(line, " ")));
        LOGGER.info("Data records: {}, Valid: {}.", lines.size(), dataSet.size());
        C45 job = new C45();
        job.process(dataSet, 5, Arrays.asList(0, 1, 2, 3, 4), featureNames);
    }

}
