package com.bubble.bnlp.classify.perceptron.seg;

import java.util.ArrayList;

/**
 * @author wugang
 * date: 2019-01-07 16:54
 **/
public class SparseVec {

    private ArrayList<Integer> featureIndex;//record indexs of feature
    public int tag;

    SparseVec(ArrayList<Integer> featureIndex, int tag) {
        this.featureIndex = featureIndex;
        this.tag = tag;
    }

    public double multiply(int part, double[] vec) {
        //get ExtendedSparseVec*vec
        double sum = 0;
        for (int index : featureIndex) {
            if (index != -1) {
                sum += vec[part * Splitter.MAXDICTSIZE + index];//w
            } //Don't count new features that are not popular or from test set
        }
        sum += vec[4 * Splitter.MAXDICTSIZE];//b
        return sum;
    }

    public void addTo(int part, double[] vec, double coef) {
        //vec += coef * ExtendedSparseVec
        for (int index : featureIndex) {
            if (index != -1) {
                vec[part * Splitter.MAXDICTSIZE + index] += coef;//w
            } //Don't count new features that are not popular or from test set
        }
        vec[4 * Splitter.MAXDICTSIZE] += coef;//b
    }

    public int predict(double[] theta) {
        double score, maxn = Double.NEGATIVE_INFINITY;
        int predictTag = -1;
        for (int i = 0; i < 4; ++i) {
            score = this.multiply(i, theta);
            if (score > maxn) {
                maxn = score;
                predictTag = i;
            }
        }
        return predictTag;
    }

}
