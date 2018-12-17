package com.bubble.bnlp.bean.tree;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author wugang
 * date: 2018-12-12 09:40
 **/
public class ResultBeen {
    private String attr;
    private String condition;
    private List<String> result;

    private List<ResultBeen> children;

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public List<String> getResult() {
        return result;
    }

    public void setResult(List<String> result) {
        this.result = result;
    }

    public void setResult(String value) {
        this.result = Lists.newArrayList(value);
    }

    public List<ResultBeen> getChildren() {
        return children;
    }

    public void setChildren(List<ResultBeen> children) {
        this.children = children;
    }

    public void addChildren(ResultBeen rb) {
        if (children == null) {
            children = Lists.newArrayList();
        }
        children.add(rb);
    }

}
