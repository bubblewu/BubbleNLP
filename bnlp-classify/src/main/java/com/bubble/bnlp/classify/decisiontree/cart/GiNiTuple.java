package com.bubble.bnlp.classify.decisiontree.cart;

/**
 * "特征-属性-基尼指数" 三元组
 * 某特征下，某属性和其对应的GiNi值，eg：<体温, 恒温, 0.5404761904761904>
 *
 * @author wugang
 * date: 2018-11-12 11:39
 **/
public class GiNiTuple<T, M, N> {
    private T feature; // 特征
    private M attribute; // 属性
    private N gini; // 基尼值

    public T getFeature() {
        return feature;
    }

    public void setFeature(T feature) {
        this.feature = feature;
    }

    public M getAttribute() {
        return attribute;
    }

    public void setAttribute(M attribute) {
        this.attribute = attribute;
    }

    public N getGini() {
        return gini;
    }

    public void setGini(N gini) {
        this.gini = gini;
    }

    @Override
    public String toString() {
        return String.join(", ", String.valueOf(feature), String.valueOf(attribute), String.valueOf(gini));
    }

}
