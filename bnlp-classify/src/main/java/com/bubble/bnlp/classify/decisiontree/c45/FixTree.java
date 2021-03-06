package com.bubble.bnlp.classify.decisiontree.c45;

import com.bubble.bnlp.classify.decisiontree.TreeNode;
import com.bubble.bnlp.common.ToolKits;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 对树结构进行修正
 *
 * @author wugang
 * date: 2018-11-22 16:10
 **/
public class FixTree {

    public static void fix(TreeNode treeNode) {
        fixChildNodes(treeNode, false);
        fixParentNodes(treeNode);
    }
    private static boolean clean = false;

    /**
     * 根据具体业务场景，对tree的子结点（即第二个特征开始）进行修正：
     * - 某父结点的子结点的属性值全部相同，直接归为叶结点;
     * - 父结点下无符合子结点属性的情况下，返回所有子结点的叶子结点数据;
     *
     * @param treeNode 决策树
     */
    private static void fixChildNodes(TreeNode treeNode, boolean clear) {
        if (!treeNode.isLeaf()) {
            List<TreeNode> childNodes = Optional.ofNullable(treeNode.getChildNodes()).orElse(Lists.newArrayListWithCapacity(1));//.stream().filter(node -> !node.isLeaf()).collect(Collectors.toList());
            Map<String, Set<String>> leafEdgeMap = Maps.newHashMap();
            TreeNode node;
            Iterator iterator = childNodes.iterator();
            while (iterator.hasNext()) {
                node = (TreeNode) iterator.next();
                if (node.isLeaf()) {
                    addEdgeForLeafMap(leafEdgeMap, node.getLeafValue(), node.getDirectedEdgeValue());
                } else {
                    clean = true;
                    fixChildNodes(node, true);
                }
            }

//            // 兄弟结点是否有为叶结点的情况
//            boolean isBrotherBranchLeaf = treeNode.getChildNodes().stream().allMatch(TreeNode::isLeaf);
//            if (!isBrotherBranchLeaf) {
//                if (!clear || !clean) { // 只有子结点递归时才取执行下面对清除逻辑
//                    return;
//                }
//            }

            if (!clear || !clean) { // 只有子结点递归时才取执行下面对清除逻辑
                return;
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
            // 有向边是否为连续变量
            boolean isContinuouslyVariable = tempChildNodes.stream().anyMatch(n -> n.getDirectedEdgeValue().startsWith(">") || n.getDirectedEdgeValue().startsWith("<="));
            if (!tempChildNodes.isEmpty()) {
                if (tempChildNodes.size() == 1) {
//                    if (nowCount != totalCount) {
//                        treeNode.clearChildNodes();
//                        // 某父结点的子结点的属性值全部相同，直接归为叶结点；
//                        treeNode.setLeaf(true);
//                        treeNode.setLeafValue(tempChildNodes.get(0).getLeafValue());
//                    }
                    treeNode.clearChildNodes();
                    // 某父结点的子结点的属性值全部相同，直接归为叶结点；
                    treeNode.setLeaf(true);
                    treeNode.setLeafValue(tempChildNodes.get(0).getLeafValue());
                } else if (!isContinuouslyVariable) {
                    // add Negative node 父结点下无符合子结点属性的情况下，返回所有子结点的叶子结点数据；
                    treeNode.clearChildNodes();
                    treeNode.addChildNodes(tempChildNodes);

                    TreeNode negativeTree = new TreeNode();
                    negativeTree.setLeaf(true);
                    negativeTree.setNode("");
                    negativeTree.setDirectedEdgeValue("Negative");
                    String leafValue = ToolKits.setToString(leafEdgeMap.keySet());
                    negativeTree.setLeafValue(leafValue);
                    treeNode.addChildNodes(negativeTree);
                }
                clean = false;
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
            if (childNodes.size() == 1) {
                return;
            }
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
