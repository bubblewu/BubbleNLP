package com.bubble.bnlp.classify.decisionTree.id3;

import java.util.ArrayList;
import java.util.List;

/**
 * 构建ID3决策树的结点
 *
 * @author wugang
 * date: 2018-11-08 09:17
 **/
public class TreeNode {
    private String node; // 结点，即特征
    private String directedEdgeValue; // 有向边的值，即特征值
    private String leafValue; //叶结点的值，即类值
    private List<TreeNode> childNodes;
    private boolean isLeaf = false; // 是否是叶结点

    public List<TreeNode> getChildNodes() {
        return childNodes;
    }

    public void addChildNodes(TreeNode node) {
        if (childNodes == null) {
            childNodes = new ArrayList<>();
        }
        childNodes.add(node);
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getDirectedEdgeValue() {
        return directedEdgeValue;
    }

    public void setDirectedEdgeValue(String directedEdgeValue) {
        this.directedEdgeValue = directedEdgeValue;
    }

    public String getLeafValue() {
        return leafValue;
    }

    public void setLeafValue(String leafValue) {
        this.leafValue = leafValue;
    }

}
