package com.bubble.bnlp.classify.decisionTree;

import java.util.Objects;

/**
 * 连续变量，如：里程和日期数据
 *
 * @author wugang
 * date: 2018-11-09 11:19
 **/
public class ContinuouslyVariable {

    private int attribute; //属性
    private String classify; // 类值

    public int getAttribute() {
        return attribute;
    }

    public void setAttribute(int attribute) {
        this.attribute = attribute;
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    @Override
    public String toString() {
        return "[" + attribute + ", " + classify + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContinuouslyVariable that = (ContinuouslyVariable) o;
        return attribute == that.attribute &&
                Objects.equals(classify, that.classify);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attribute, classify);
    }
}
