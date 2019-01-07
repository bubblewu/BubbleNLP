package com.bubble.bnlp.classify.perceptron.service;

import com.bubble.bnlp.classify.perceptron.Perceptron;
import com.bubble.bnlp.classify.perceptron.entity.DataSet;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;

/**
 * 感知机：对偶形式
 *
 * @author wugang
 * date: 2019-01-04 14:34
 **/
public class DualFormPerceptron implements Perceptron {
    private static final Logger LOGGER = LoggerFactory.getLogger(DualFormPerceptron.class);

    private final int epochs = 10000;
    private final double eta = 1.0; //步长OR学习率
    private double[] alphas;
    private double[] weights; // 权重向量
    private double bias; // 偏置
    private List<DataSet> trainDataSet;
    private int featureDimension;


    public DualFormPerceptron(List<DataSet> trainDataSet, int featureDimension) {
        this.alphas = new double[trainDataSet.size()];
        this.bias = 0;
        this.trainDataSet = trainDataSet;
        this.featureDimension = featureDimension;
    }

    @Override
    public void train() {
//        double[] beatAlphas = new double[trainDataSet.size()];
//        double beatBias = 0;
//        int wrongCount = 0;
        for (int epoch = 0; epoch < epochs; epoch++) {
            for (DataSet data : trainDataSet) {
                if (isWrong(data)) { // 处理误分类点
                    alphas[data.getId()] += eta; // 更新alphas：alphas = alphas[i] + eta
                    bias += eta * data.getLabel(); // 更新偏置b：b = b + eta * yi
                }
            }
            List<DataSet> wrongClassifyList = getWrong();
//            if (epoch == 0) {
//                wrongCount = wrongClassifyList.size();
//                beatAlphas = alphas;
//                beatBias = bias;
//            } else if (wrongCount > wrongClassifyList.size()) {
//                wrongCount = wrongClassifyList.size();
//                beatAlphas = alphas;
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
//                alphas = beatAlphas;
//                bias = beatBias;
//                LOGGER.info("best wrong classify count: {}", wrongCount);
//            }
        }

        weights = new double[featureDimension];
        for (int i = 0; i < featureDimension; i++) {
            for (int j = 0; j < trainDataSet.size(); j++) {
                // 计算权重向量w为：w = sum(alphas[i] * yi * xi)
                weights[i] += alphas[j] * trainDataSet.get(j).getLabel() * trainDataSet.get(j).getFeature()[i];
            }
        }
        LOGGER.info("weights: {}", Arrays.toString(weights));
        LOGGER.info("bias: {}", bias);
    }

    /**
     * 当前epoch下参数拟合后的数据集中的误分类实例集合
     *
     * @return 误分类实例集合
     */
    private List<DataSet> getWrong() {
        List<DataSet> wrongClassifyList = Lists.newArrayList();
        for (DataSet data : trainDataSet) {
            if (isWrong(data)) {
                wrongClassifyList.add(data);
            }
        }
        return wrongClassifyList;
    }

    /**
     * 判断误分类
     * 公式：yi * (sum(alphas[j] * yj * xj * xi) + b) <= 0
     *
     * @param data 当前实例数据
     * @return true or false
     */
    private boolean isWrong(DataSet data) {
        int label = data.getLabel();
        double predict = 0;
        for (int i = 0; i < trainDataSet.size(); i++) {
            DataSet eachData = trainDataSet.get(i);
            predict += alphas[i] * eachData.getLabel() * calculateInnerProduct(data, eachData);
        }
        predict += bias;
        return label * predict <= 0;
    }

    /**
     * Gram矩阵 G=[xi*xj]n*n：数据集中实例间的内积计算xj*xi
     *
     * @param currentData 当前实例数据xi
     * @param eachData    数据集中的某个实例xj
     * @return Gram矩阵
     */
    private double calculateInnerProduct(DataSet currentData, DataSet eachData) {
        double predict = 0;
        for (int i = 0; i < featureDimension; i++) {
            predict += eachData.getFeature()[i] * currentData.getFeature()[i];
        }
        return predict;
    }

    @Override
    public void saveModel(String modelFile) {
        writeAlphaFile(modelFile + ".alpha");
        writeModelFile(modelFile);
    }

    /**
     * 存储求解的迭代过程：alphas[i], Yi, Xi
     *
     * @param alphaFile 求解过程存储路径
     */
    private void writeAlphaFile(String alphaFile) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(alphaFile));
            for (int i = 0; i < trainDataSet.size(); i++) {
                if (alphas[i] != 0) {
                    StringBuilder alphaLine = new StringBuilder();
                    alphaLine.append(alphas[i]);
                    alphaLine.append("\t");
                    alphaLine.append(trainDataSet.get(i).getLabel());
                    alphaLine.append("\t");
                    double[] feature = trainDataSet.get(i).getFeature();
                    for (double featureEachDimension : feature) {
                        alphaLine.append(featureEachDimension);
                        alphaLine.append(",");
                    }
                    bw.write(alphaLine.substring(0, alphaLine.length() - 1) + "\n");
                }
            }
            bw.write(String.valueOf(bias));
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeModelFile(String modelFile) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(modelFile));
            StringBuilder weightsLine = new StringBuilder();
            for (double weight : weights) {
                weightsLine.append(weight);
                weightsLine.append(",");
            }
            bw.write(weightsLine.substring(0, weightsLine.length() - 1));
            bw.write("\n");
            bw.write(String.valueOf(bias));
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
