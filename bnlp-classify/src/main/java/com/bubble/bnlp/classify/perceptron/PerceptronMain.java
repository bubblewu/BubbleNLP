package com.bubble.bnlp.classify.perceptron;

import com.bubble.bnlp.classify.perceptron.entity.DataSet;
import com.bubble.bnlp.classify.perceptron.service.DualFormPerceptron;
import com.bubble.bnlp.classify.perceptron.service.PrimalFormPerceptron;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 感知机
 *
 * @author wugang
 * date: 2019-01-04 15:05
 **/
public class PerceptronMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(PerceptronMain.class);

    public static void main(String[] args) {
        String basePath = "bnlp-classify/src/main/resources/data/perceptron/sonar/";
        String trainFile = basePath + "train.txt";

        List<DataSet> trainDataSet = loadData(trainFile);
        int featureDimension = trainDataSet.get(0).getFeature().length;

        // 原始形式
        Perceptron perceptron = new PrimalFormPerceptron(trainDataSet, featureDimension);
        perceptron.train();
        String primalModelFile = basePath + "primal-form-model.txt";
        perceptron.saveModel(primalModelFile);

        // 对偶形式
        Perceptron dualFormPerceptron = new DualFormPerceptron(trainDataSet, featureDimension);
        dualFormPerceptron.train();
        String dualModelFile = basePath + "dual-form-model.txt";
        dualFormPerceptron.saveModel(dualModelFile);
    }

    private static List<DataSet> loadData(String dataFile) {
        List<DataSet> trainDataSet = Lists.newArrayList();
        try {
            List<String> lines = Files.readAllLines(Paths.get(dataFile), Charset.defaultCharset());
            AtomicInteger index = new AtomicInteger(0);
            lines.forEach(line -> {
                String[] lineSplit = line.split(",");
                int featureDimension = lineSplit.length - 1;
                double[] feature = new double[featureDimension];
                for (int i = 0; i < featureDimension; i++) {
                    feature[i] = Double.valueOf(lineSplit[i]);
                }
                int label = Integer.valueOf(lineSplit[featureDimension]);
                trainDataSet.add(new DataSet(index.get(), feature, label));
                index.addAndGet(1);
            });
        } catch (IOException e) {
            LOGGER.error("load training data {} error. e", dataFile, e);
        }
        if (trainDataSet.isEmpty()) {
            LOGGER.warn("training data is empty!");
            System.exit(0);
        }
        return trainDataSet;
    }

}
