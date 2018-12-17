package com.bubble.bnlp.bean.tree.structure;

import java.util.Map;

/**
 * 决策树存储结构
 *
 * @author wugang
 * date: 2018-12-13 15:48
 **/
public class Decision {
    private String feature;
    private Object attribute;
    private Map<String, Integer> classCountMap;

    public Decision() {
        super();
    }

    public Decision(String feature, Object attribute) {
        super();
        this.feature = feature;
        this.attribute = attribute;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public Object getAttribute() {
        return attribute;
    }

    public void setAttribute(Object attribute) {
        this.attribute = attribute;
    }

    public Map<String, Integer> getClassCountMap() {
        return classCountMap;
    }

    public void setClassCountMap(Map<String, Integer> classCountMap) {
        this.classCountMap = classCountMap;
    }

    @Override
    public String toString() {
        return String.format("{\"attr\":\"%s\", \"condition\":\"%s\", \"result\":\"%s\"}", feature, attribute, classCountMap);
    }
}
