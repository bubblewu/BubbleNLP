package com.bubble.bnlp.classify.decisionTree.c45;

import com.bubble.bnlp.classify.decisionTree.DecisionTreeUtils;
import com.bubble.bnlp.classify.decisionTree.TreeNode;
import com.bubble.bnlp.common.ToolKits;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 决策树之C45算法
 *
 * @author wugang
 * date: 2018-11-09 11:03
 **/
public class C45Model {
    private static final Logger LOGGER = LoggerFactory.getLogger(C45Model.class);

    static void train(String inputFile, String outputFile) {
        List<List<String>> dataSet = DecisionTreeUtils.getTrainingData(inputFile);
        DecisionTreeUtils.transformContinuouslyVariables(dataSet);
        TreeNode treeNode = C45Model.createDecisionTree(dataSet);
        fixChildNodes(treeNode);
        fixParentNodes(treeNode);
        DecisionTreeUtils.showDecisionTree(treeNode, "");
        DecisionTreeUtils.saveTree2XML(treeNode, outputFile);
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

    private static void addEdgeForLeafMap(Map<String, Set<String>> leafEdgeMap, String leaf, String edge) {
        if (leafEdgeMap.containsKey(leaf)) {
            Set<String> oldEdges = leafEdgeMap.get(leaf);
            oldEdges.add(edge);
        } else {
            leafEdgeMap.put(leaf, Sets.newHashSet(edge));
        }
    }

    /**
     * 构造C4.5决策树
     *
     * @param dataSet 数据集D
     * @return C4.5决策树模型
     */
    private static TreeNode createDecisionTree(List<List<String>> dataSet) {
        Map<String, Double> maxIGRMap = InformationGainRatio.maxInformationGainRatio(dataSet);
        String optimalFeatureName = maxIGRMap.keySet().iterator().next();
        double maxIG = maxIGRMap.get(optimalFeatureName);
        LOGGER.info("optimal feature is [{}], information gain ratio value is [{}].", optimalFeatureName, maxIG);
        TreeNode treeNode = new TreeNode();
        treeNode.setNode(optimalFeatureName);
        genTreeNode(dataSet, treeNode);
        return treeNode;
    }

    /**
     * 生成树的各结点、有向边和叶子结点
     *
     * @param dataSet  数据集
     * @param treeNode 特征结点
     */
    private static void genTreeNode(List<List<String>> dataSet, TreeNode treeNode) {
        // 获取某特征下的所有特征值
        List<String> attributeList = DecisionTreeUtils.getFeatureValueList(dataSet, treeNode.getNode());
        // 获得特征在原数据中所处的列索引
        int featureIndex = DecisionTreeUtils.getFeatureIndex(dataSet.get(0), treeNode.getNode());
        // 将这个最大信息增益对应的特征属性进行移除，并将数据集切分成N个部分，其中N为分支状态数，即特征值的个数）
        attributeList.forEach(attribute -> {
            // 未参与计算的特征和特征值的集合
            List<List<String>> splitFeatureAttributeList = DecisionTreeUtils.splitAttributeDataList(dataSet, attribute, featureIndex);
            //把这N份数据分别当成上面的dataSet代入计算。通过这样循环地迭代，直到数据被全部计算完成。
            buildDecisionTree(attribute, splitFeatureAttributeList, treeNode);
        });
    }

    /**
     * 构建C4.5决策树
     *
     * @param attributeValue            特征值
     * @param splitFeatureAttributeList 未参与计算的特征和特征值的集合
     * @param treeNode                  决策树
     */
    private static void buildDecisionTree(String attributeValue, List<List<String>> splitFeatureAttributeList, TreeNode treeNode) {
        Map<String, Double> maxIGRMap = InformationGainRatio.maxInformationGainRatio(splitFeatureAttributeList);
        String optimalFeatureName = maxIGRMap.keySet().iterator().next();
        double maxIGR = maxIGRMap.get(optimalFeatureName);

        if (maxIGR == 0.0) {
            List<String> singleLineData = splitFeatureAttributeList.get(splitFeatureAttributeList.size() - 1);
            TreeNode leafNode = new TreeNode();
            leafNode.setLeaf(true);
            String classify = singleLineData.get(singleLineData.size() - 1);
            leafNode.setLeafValue(classify);
            leafNode.setDirectedEdgeValue(attributeValue);
            treeNode.addChildNodes(leafNode);
            return;
        }

        // 构建子树结点
        TreeNode childNode = new TreeNode();
        childNode.setNode(optimalFeatureName);
        childNode.setDirectedEdgeValue(attributeValue);
        treeNode.addChildNodes(childNode);

        genTreeNode(splitFeatureAttributeList, childNode);
    }

}
