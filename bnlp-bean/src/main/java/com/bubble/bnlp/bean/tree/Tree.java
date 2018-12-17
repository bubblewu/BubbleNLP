package com.bubble.bnlp.bean.tree;

import java.util.LinkedList;
import java.util.List;

/**
 * 树结构
 *
 * @author wugang
 * date: 2018-12-13 16:23
 **/
public class Tree<T> {
    private T data;
    private List<Tree<T>> children;

    public Tree() {
    }

    public Tree(T data, List<Tree<T>> children) {
        this.data = data;
        this.children = children;
    }

    public boolean isLeaf() {
        if (children == null) {
            return true;
        }
        return false;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<Tree<T>> getChildren() {
        return children;
    }

    public void setChildren(List<Tree<T>> children) {
        this.children = children;
    }

    public void addChild(Tree<T> child) {
        if (children == null) {
            children = new LinkedList<>();
        }
        children.add(child);
    }

    public void removeChild(Tree<T> child) {
        children.remove(child);
        if (children.isEmpty()) {
            children = null;
        }
    }

    @Override
    public String toString() {
        return String.format("{\"data\":%s, \"children\":%s}", data, children);
    }

}
