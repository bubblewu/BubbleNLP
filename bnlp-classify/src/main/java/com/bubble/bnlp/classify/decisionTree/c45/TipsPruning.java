package com.bubble.bnlp.classify.decisionTree.c45;

import com.bubble.bnlp.classify.decisionTree.TreeNode;
import com.bubble.bnlp.common.ToolKits;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 针对Tips数据的业务剪枝
 *
 * @author wugang
 * date: 2018-11-22 16:10
 **/
public class TipsPruning {

    public static void pruning(TreeNode treeNode) {
        fixChildNodes(treeNode);
        fixParentNodes(treeNode);
    }

    /**
     * 根据具体业务场景，对tree的子结点（即第二个特征开始）进行修正：
     * - 某父结点的子结点的属性值全部相同，直接归为叶结点;
     * - 父结点下无符合子结点属性的情况下，返回所有子结点的叶子结点数据;
     *
     * @param treeNode 决策树
     */
    private static void fixChildNodes(TreeNode treeNode) {
        if (!treeNode.isLeaf()) {
            List<TreeNode> childNodes = Optional.ofNullable(treeNode.getChildNodes()).orElse(Lists.newArrayListWithCapacity(1));//.stream().filter(node -> !node.isLeaf()).collect(Collectors.toList());
            Map<String, Set<String>> leafEdgeMap = Maps.newHashMap();
            TreeNode node;
            for (TreeNode childNode : childNodes) {
                node = childNode;
                if (node.isLeaf()) {
                    addEdgeForLeafMap(leafEdgeMap, node.getLeafValue(), node.getDirectedEdgeValue());
                } else {
                    fixChildNodes(node);
                }
            }

            List<TreeNode> tempChildNodes = Lists.newArrayList();
            leafEdgeMap.forEach((leafValue, edgeValues) -> {
                String edgeValue = ToolKits.setToString(edgeValues);
                TreeNode childTree = new TreeNode();
                childTree.setLeaf(true);
                childTree.setNode("");
                childTree.setDirectedEdgeValue(edgeValue);
                childTree.setLeafValue(leafValue);
                tempChildNodes.add(childTree);

            });
            if (!tempChildNodes.isEmpty()) {
                treeNode.clearChildNodes();
                if (tempChildNodes.size() == 1) {
                    // 某父结点的子结点的属性值全部相同，直接归为叶结点；
                    treeNode.setLeaf(true);
                    treeNode.setLeafValue(tempChildNodes.get(0).getLeafValue());
                } else {
                    // add Negative node 父结点下无符合子结点属性的情况下，返回所有子结点的叶子结点数据；
                    TreeNode negativeTree = new TreeNode();
                    negativeTree.setLeaf(true);
                    negativeTree.setNode("");
                    negativeTree.setDirectedEdgeValue("Negative");
                    String leafValue = ToolKits.setToString(leafEdgeMap.keySet());
                    negativeTree.setLeafValue(leafValue);
                    tempChildNodes.add(negativeTree);

                    treeNode.addChildNodes(tempChildNodes);
                }
            }

        }
    }

    /**
     * 对父结点的类值重复项的属性归纳
     *
     * @param fixedTree 修正过子结点的树
     */
    private static void fixParentNodes(TreeNode fixedTree) {
        if (!fixedTree.isLeaf()) {
            List<TreeNode> childNodes = fixedTree.getChildNodes().stream().filter(TreeNode::isLeaf).collect(Collectors.toList());
            Map<String, Set<String>> leafEdgeMap = Maps.newHashMap();
            TreeNode node;
            for (TreeNode childNode : childNodes) {
                node = childNode;
                addEdgeForLeafMap(leafEdgeMap, node.getLeafValue(), node.getDirectedEdgeValue());
            }

            List<TreeNode> tempChildNodes = Lists.newArrayList();
            leafEdgeMap.forEach((leafValue, edgeValues) -> {
                String edgeValue = ToolKits.setToString(edgeValues);
                TreeNode childTree = new TreeNode();
                childTree.setLeaf(true);
                childTree.setDirectedEdgeValue(edgeValue);
                childTree.setLeafValue(leafValue);
                tempChildNodes.add(childTree);
            });
            Set<String> repetitionSet = leafEdgeMap.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
            clean(fixedTree, repetitionSet);
            fixedTree.addChildNodes(tempChildNodes);
        }
    }

    /**
     * 删除对父结点的有向边值（属性）重复的结点
     *
     * @param fixedTree  树
     * @param edgeValues 重复的有向边集合
     */
    private static void clean(TreeNode fixedTree, Set<String> edgeValues) {
        List<TreeNode> childNodes = fixedTree.getChildNodes().stream().filter(node -> !edgeValues.contains(node.getDirectedEdgeValue())).collect(Collectors.toList());
        fixedTree.clearChildNodes();
        fixedTree.setChildNodes(childNodes);
    }

    private static void addEdgeForLeafMap(Map<String, Set<String>> leafEdgeMap, String leaf, String edge) {
        if (leafEdgeMap.containsKey(leaf)) {
            Set<String> oldEdges = leafEdgeMap.get(leaf);
            oldEdges.add(edge);
        } else {
            leafEdgeMap.put(leaf, Sets.newHashSet(edge));
        }
    }

}
