package com.bubble.bnlp.classify.decisionTree;

import com.bubble.bnlp.classify.decisionTree.id3.TreeNode;
import com.google.common.collect.Maps;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

/**
 * 决策树处理相关工具
 *
 * @author wugang
 * date: 2018-11-07 15:50
 **/
public class DecisionTreeUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(DecisionTreeUtils.class);

    /**
     * 读取训练数据
     *
     * @param fileName 训练数据文件
     * @return 训练数据集合
     */
    public static List<List<String>> getTrainingData(String fileName) {
        List<List<String>> dataList = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(fileName), Charset.defaultCharset());
            lines.forEach(line -> {
                StringTokenizer tokenizer = new StringTokenizer(line);
                List<String> lineData = new ArrayList<>();
                while (tokenizer.hasMoreTokens()) {
                    lineData.add(tokenizer.nextToken());
                }
                dataList.add(lineData);
            });
        } catch (IOException e) {
            LOGGER.error("read training data error.", e);
        }
        return dataList;
    }

    /**
     * 获得目标属性在原数据中所处的列索引
     *
     * @param attributeNames 数据的所有特征属性
     * @param attribute      目标特征属性
     * @return 列索引
     */
    public static int getAttributeIndex(List<String> attributeNames, String attribute) {
        int attributeIndex = 0;
        for (String attributeName : attributeNames) {
            if (attribute.equals(attributeName)) {
                break;
            }
            attributeIndex++;
        }
        return attributeIndex;
    }

    /**
     * 获取某特征下各个特征值所对应的类值分布
     *
     * @param dataSet        数据集D
     * @param attributeIndex 目标特征的列索引
     * @return eg: {Rainy={No=2, Yes=3}, Sunny={No=3, Yes=2}}
     */
    public static Map<String, Map<String, Integer>> getAttributeClassValueMap(List<List<String>> dataSet, int attributeIndex) {
        Map<String, Map<String, Integer>> attributeCVMap = Maps.newHashMap();
        for (int rowIndex = 1; rowIndex < dataSet.size(); rowIndex++) {
            // 每行的特征值数据
            List<String> rowData = dataSet.get(rowIndex);
            // 获取当前特征的值
            String attributeValue = rowData.get(attributeIndex);
            // 获取当前特征值对应的类值
            String classValue = rowData.get(rowData.size() - 1);

            Map<String, Integer> cvMap;
            if (attributeCVMap.containsKey(attributeValue)) {
                cvMap = attributeCVMap.get(attributeValue);
                if (cvMap.containsKey(classValue)) {
                    cvMap.put(classValue, cvMap.get(classValue) + 1);
                } else {
                    cvMap.put(classValue, 1);
                }
            } else {
                cvMap = Maps.newHashMap();
                cvMap.put(classValue, 1);
            }
            attributeCVMap.put(attributeValue, cvMap);
        }
        return attributeCVMap;
    }


    /**
     * 获取某特征下的所有特征值
     *
     * @param dataSet       数据集D
     * @param attributeName 特征名
     * @return 特征分支列表
     */
    public static List<String> getAttributeValueList(List<List<String>> dataSet, String attributeName) {
        List<String> attributeValueList = new ArrayList<>();
        int attributeIndex = getAttributeIndex(dataSet.get(0), attributeName);
        dataSet.stream().skip(1).filter(row -> !attributeValueList.contains(row.get(attributeIndex)))
                .forEach(rowData -> attributeValueList.add(rowData.get(attributeIndex)));
        return attributeValueList;
    }

    /**
     * 切分数据集D中的数据：
     * 针对每个用户（每行数据）过滤掉D中为attributeIndex列的特征和该特征值为attributeValue的，选择出这些用户在剩下的其他特征空间下的数据；
     *
     * @param dataSet        数据集D
     * @param attributeValue 特征的某个值分支
     * @param attributeIndex 该特征在D中的列索引
     * @return 未参与计算的特征和特征值的集合
     */
    public static List<List<String>> splitAttributeDataList(List<List<String>> dataSet, String attributeValue, int attributeIndex) {
        List<List<String>> notUsedFeatureOrValueList = new ArrayList<>();
        List<String> featureNames = dataSet.get(0);
        notUsedFeatureOrValueList.add(getNotUsedFeatureOrValueList(featureNames, attributeIndex));
        // 遍历数据集中的每行训练数据，过滤已经计算的最优特征结点的值attributeValue，存储每行的未计算的特征值到集合
        dataSet.stream().skip(1).filter(rd -> rd.get(attributeIndex).equals(attributeValue))
                .forEach(rowData -> notUsedFeatureOrValueList.add(getNotUsedFeatureOrValueList(rowData, attributeIndex)));
        return notUsedFeatureOrValueList;
    }

    /**
     * 获取数据集D中的还未参与计算的特征名或特征值集合，过滤指定列索引的特征或特征值（移除已经计算的特征或特征值）。
     *
     * @param lineDataList   数据集中的特征或特征值集合
     * @param attributeIndex 已计算过的特征在D中的列索引
     * @return 未计算的特征或特征值集合
     */
    private static List<String> getNotUsedFeatureOrValueList(List<String> lineDataList, int attributeIndex) {
        return lineDataList.stream().filter(attributeName -> !attributeName.equals(lineDataList.get(attributeIndex)))
                .collect(Collectors.toList());
    }


    /**
     * 打印ID3决策树
     *
     * @param node   结点
     * @param prefix 定义输出前缀
     */
    public static void showDecisionTree(TreeNode node, String prefix) {
        if (node == null) {
            return;
        }
        System.out.println(prefix + (node.getParentStatus() == null ? "" : node.getParentStatus() + "->") + node.getAttributeName());

        List<TreeNode> children = node.getChildNodes();
        if (children == null) {
            return;
        }
        prefix += "\t";
        for (TreeNode child : children) {
            showDecisionTree(child, prefix);
        }
    }

    public static void saveTree2XML(TreeNode treeNode, String modelFile) {
        Document xml = DocumentHelper.createDocument();
        Element root = xml.addElement("root");
        Element nextNode = root.addElement("DecisionTree");
        toXML(treeNode, nextNode);
        System.out.println(xml.asXML());
        OutputFormat format = OutputFormat.createPrettyPrint();
        try {
            Writer fileWriter = new FileWriter(modelFile);
            XMLWriter output = new XMLWriter(fileWriter, format);
            output.write(xml);
            output.close();
        } catch (IOException e) {
            LOGGER.error("save decision tree as xml error.", e);
        }
    }

    private static void toXML(TreeNode treeNode, Element node) {
        if (null == treeNode.getChildNodes()) {
            return;
        }

        treeNode.getChildNodes().forEach(childNode -> {
            Element nextNode = node.addElement(treeNode.getAttributeName());
            System.out.println(treeNode.getAttributeName());
            System.out.println("ps -> " + childNode.getParentStatus());
            nextNode.addAttribute("value", childNode.getParentStatus());
            if (childNode.isLeaf()) {
                System.out.println("an = " + childNode.getAttributeName());
                nextNode.setText(childNode.getAttributeName());
            }
            toXML(childNode, nextNode);
        });


    }


}
