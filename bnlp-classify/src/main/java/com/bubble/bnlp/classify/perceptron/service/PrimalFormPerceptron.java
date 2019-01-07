package com.bubble.bnlp.classify.perceptron.service;

import com.bubble.bnlp.classify.perceptron.Perceptron;
import com.bubble.bnlp.classify.perceptron.entity.DataSet;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;

/**
 * 感知机：原始形式
 *
 * @author wugang
 * date: 2019-01-04 14:24
 **/
public class PrimalFormPerceptron implements Perceptron {
    private static final Logger LOGGER = LoggerFactory.getLogger(PrimalFormPerceptron.class);
    private final int epochs = 10000;
    private final double eta = 1.0; // 步长OR学习率
    private double[] weights; // 权重向量
    private double bias; // 偏置
    private List<DataSet> trainDataSet;
    private int featureDimension;

    public PrimalFormPerceptron(List<DataSet> trainDataSet, int featureDimension) {
        this.trainDataSet = trainDataSet;
        this.weights = new double[featureDimension];
        this.bias = 0;
        this.featureDimension = featureDimension;
    }

    @Override
    public void train() {
//        double[] beatWeights = new double[featureDimension];
//        double beatBias = 0;
//        int wrongCount = 0;
        for (int epoch = 0; epoch < epochs; epoch++) {
            List<DataSet> wrongClassifyList = Lists.newArrayList();
            for (DataSet data : trainDataSet) {
                if (isWrong(data)) { // 处理误分类点
                    wrongClassifyList.add(data);

                    for (int i = 0; i < data.getFeature().length; i++) {
                        //更新权重向量w, w = w + eta * yi * xi
                        weights[i] += eta * data.getLabel() * data.getFeature()[i];
                    }
                    bias += eta * data.getLabel(); //更新偏置b，b = b + eta * yi
                }
            }

//            if (epoch == 0) {
//                wrongCount = wrongClassifyList.size();
//                beatWeights = weights;
//                beatBias = bias;
//            } else if (wrongCount > wrongClassifyList.size()) {
//                wrongCount = wrongClassifyList.size();
//                beatWeights = weights;
//                beatBias = bias;
//            }

            if (epoch % 100 == 0) {
                LOGGER.info("epoch: {}", epoch);
                LOGGER.info("wrong classify count: {}", wrongClassifyList.size());
            }
            if (wrongClassifyList.isEmpty()) {
                LOGGER.info("epoch: {}", epoch);
                LOGGER.info("wrong classify count: 0");
                break;
            }
//            } else if (epoch == epochs - 1) {
//                // 如迭代过程中误分类数不为0时，选择迭代中的最优拟合参数
//                LOGGER.info("best epoch: {}", epoch);
//                weights = beatWeights;
//                bias = beatBias;
//                LOGGER.info("best wrong classify count: {}", wrongCount);
//            }
        }

        LOGGER.info("weights: {}", Arrays.toString(weights));
        LOGGER.info("bias: {}", bias);
    }

    /**
     * 是否为误分类点：yi(w * xi + b) <= 0时为误分类
     *
     * @param data 数据Xi
     * @return true or false
     */
    private boolean isWrong(DataSet data) {
        int label = data.getLabel();
        double predict = 0;
        for (int i = 0; i < data.getFeature().length; i++) {
            predict += data.getFeature()[i] * weights[i];
        }
        predict += bias;
        return label * predict <= 0;
    }

    @Override
    public void saveModel(String modelFile) {
        try {
            FileOutputStream fos = new FileOutputStream(new File(modelFile));
            StringBuilder weightsLine = new StringBuilder();
            for (double weight : weights) {
                weightsLine.append(weight);
                weightsLine.append(",");
            }
            fos.write(weightsLine.substring(0, weightsLine.length() - 1).getBytes());
            fos.write("\n".getBytes());
            fos.write(String.valueOf(bias).getBytes());
            fos.close();
        } catch (Exception e) {
            LOGGER.error("save model error.", e);
        }
    }

}
