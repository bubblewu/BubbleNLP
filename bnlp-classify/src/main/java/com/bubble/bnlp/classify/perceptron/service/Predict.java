package com.bubble.bnlp.classify.perceptron.service;

import com.bubble.bnlp.classify.perceptron.entity.DataSet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

/**
 * 利用计算出的感知机来预测分类
 *
 * @author wugang
 * date: 2019-01-07 18:40
 **/
public class Predict {
    private final int featureDimension;
    private List<DataSet> testDataSet;
    private double[] weights;
    private double bias;

    public Predict(List<DataSet> testDataSet, int featureDimension, String modelFile) {
        this.testDataSet = testDataSet;
        this.featureDimension = featureDimension;
        loadModel(modelFile);
    }

    public void predict() {
        int wrongCount = 0;
        for (DataSet data : testDataSet) {
            int goldLabel = data.getLabel();
            int predictLabel = predictEachInstance(data);
            if (goldLabel != predictLabel) {
                wrongCount += 1;
            }
            System.out.println("feature: " + Arrays.toString(data.getFeature()));
            System.out.println("label: " + goldLabel);
            System.out.println("predict: " + predictLabel);
            System.out.println();
        }
        System.out.println("wrong count: " + wrongCount);
    }

    /**
     * 利用感知机公式得：y = wi * xi + b; 当y>=0,sign(y) = 1;当y<0, sign(y) = -1.
     *
     * @param data 数据实例
     * @return 二分类结果1或-1
     */
    private int predictEachInstance(DataSet data) {
        double y = 0;
        for (int i = 0; i < featureDimension; i++) {
            y += data.getFeature()[i] * weights[i];
        }
        y += bias;
        if (y >= 0) {
            return 1;
        }
        return -1;
    }

    private void loadModel(String modelFile) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(modelFile));
            String weightLine = br.readLine().trim();
            String[] weightLineSplit = weightLine.split(",");
            weights = new double[featureDimension];
            for (int i = 0; i < featureDimension; i++) {
                weights[i] = Double.valueOf(weightLineSplit[i]);
            }
            String biasLine = br.readLine().trim();
            bias = Double.valueOf(biasLine);
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
