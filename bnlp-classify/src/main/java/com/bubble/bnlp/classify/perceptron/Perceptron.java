package com.bubble.bnlp.classify.perceptron;

/**
 * 感知机
 *
 * @author wugang
 * date: 2019-01-04 14:37
 **/
public interface Perceptron {

    void train();

    void saveModel(String modelFile);

}
