package com.bubble.bnlp.classify.perceptron.entity;

import java.util.Arrays;

/**
 * 数据集
 *
 * @author wugang
 * date: 2019-01-04 14:41
 **/
public class DataSet {
    private int id;
    private double[] feature; //输入特征集合
    private int label; // 类

    public DataSet(double[] feature, int label) {
        this.feature = feature;
        this.label = label;
    }

    public DataSet(int id, double[] feature, int label) {
        this.id = id;
        this.feature = feature;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double[] getFeature() {
        return feature;
    }

    public void setFeature(double[] feature) {
        this.feature = feature;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "DataSet{" +
                "id=" + id +
                ", feature=" + Arrays.toString(feature) +
                ", label=" + label +
                '}';
    }
}
