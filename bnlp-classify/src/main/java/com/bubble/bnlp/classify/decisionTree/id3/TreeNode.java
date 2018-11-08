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

    private String attributeName;
    private List<TreeNode> childNodes;
    private String parentStatus; // 父节点的状态（表示的是从父节点过度到当前节点时的状态）
    private boolean isLeaf = false; // 是否是叶子节点

    public TreeNode(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public List<TreeNode> getChildNodes() {
        return childNodes;
    }

    public void addChildNodes(TreeNode node) {
        if (childNodes == null) {
            childNodes = new ArrayList<>();
        }
        childNodes.add(node);
    }

    public String getParentStatus() {
        return parentStatus;
    }

    public void setParentStatus(String parentStatus) {
        this.parentStatus = parentStatus;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }
}
