package com.bubble.bnlp.classify.decisionTree;

import com.bubble.bnlp.common.ToolKits;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.math.NumberUtils;
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
import java.util.*;
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
     * 获取数据集中的特征名称
     * 数据集第一行特征的属性名,过滤列名和类名
     *
     * @param featureNameList 特征名称集合
     * @return 所有的特征名集合
     */
    public static List<String> getFeatureNames(List<String> featureNameList) {
        return featureNameList.stream().skip(1).limit(featureNameList.size() - 2)
                .collect(Collectors.toList());
    }

    /**
     * 获得目标特征在原数据中所处的列索引
     *
     * @param featureNames 数据的所有特征属性
     * @param featureName  目标特征
     * @return 列索引
     */
    public static int getFeatureIndex(List<String> featureNames, String featureName) {
        int featureIndex = 0;
        for (String feature : featureNames) {
            if (featureName.equals(feature)) {
                break;
            }
            featureIndex++;
        }
        return featureIndex;
    }

    /**
     * 获取某特征下各个特征值所对应的类值分布
     *
     * @param dataSet      数据集D
     * @param featureIndex 目标特征的列索引
     * @return eg: {Rainy={No=2, Yes=3}, Sunny={No=3, Yes=2}}
     */
    public static Map<String, Map<String, Integer>> getAttributeClassifyMap(List<List<String>> dataSet, int featureIndex) {
        Map<String, Map<String, Integer>> attributeClassifyMap = Maps.newHashMap();
        for (int rowIndex = 1; rowIndex < dataSet.size(); rowIndex++) {
            // 每行的特征值数据
            List<String> rowData = dataSet.get(rowIndex);
            // 获取当前特征的值
            String attributeValue = rowData.get(featureIndex);
            // 获取当前特征值对应的类值
            String classify = rowData.get(rowData.size() - 1);

            Map<String, Integer> classifyMap;
            if (attributeClassifyMap.containsKey(attributeValue)) {
                classifyMap = attributeClassifyMap.get(attributeValue);
                if (classifyMap.containsKey(classify)) {
                    classifyMap.put(classify, classifyMap.get(classify) + 1);
                } else {
                    classifyMap.put(classify, 1);
                }
            } else {
                classifyMap = Maps.newHashMap();
                classifyMap.put(classify, 1);
            }
            attributeClassifyMap.put(attributeValue, classifyMap);
        }
        return attributeClassifyMap;
    }


    /**
     * 获取某特征下的所有特征值
     *
     * @param dataSet     数据集D
     * @param featureName 特征名
     * @return 特征分支列表
     */
    public static List<String> getFeatureValueList(List<List<String>> dataSet, String featureName) {
        List<String> attributeList = new ArrayList<>();
        int featureIndex = getFeatureIndex(dataSet.get(0), featureName);
        dataSet.stream().skip(1).filter(row -> !attributeList.contains(row.get(featureIndex)))
                .forEach(rowData -> attributeList.add(rowData.get(featureIndex)));
        return attributeList;
    }

    /**
     * 切分数据集D中的数据：
     * 针对每个用户（每行数据）过滤掉D中为attributeIndex列的特征和该特征值为attributeValue的，选择出这些用户在剩下的其他特征空间下的数据；
     *
     * @param dataSet        数据集D
     * @param attributeValue 特征的某个区间值
     * @param featureIndex   该特征在D中的列索引
     * @return 未参与计算的特征和特征值的集合
     */
    public static List<List<String>> splitAttributeDataList(List<List<String>> dataSet, String attributeValue, int featureIndex) {
        List<List<String>> notUsedFeatureOrValueList = new ArrayList<>();
        List<String> featureNames = dataSet.get(0);
        notUsedFeatureOrValueList.add(getNotUsedFeatureOrValueList(featureNames, featureIndex));
        // 遍历数据集中的每行训练数据，过滤已经计算的最优特征结点的值attributeValue，存储每行的未计算的特征值到集合
        dataSet.stream().skip(1).filter(rd -> rd.get(featureIndex).equals(attributeValue))
                .forEach(rowData -> notUsedFeatureOrValueList.add(getNotUsedFeatureOrValueList(rowData, featureIndex)));
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
        String value;
        if (node.isLeaf()) {
            value = node.getLeafValue();
        } else {
            value = node.getNode();
        }
        System.out.println(prefix + (node.getDirectedEdgeValue() == null ? "" : node.getDirectedEdgeValue() + "->") + value);

        List<TreeNode> children = node.getChildNodes();
        if (children == null) {
            return;
        }
        prefix += "\t";
        for (TreeNode child : children) {
            showDecisionTree(child, prefix);
        }
    }

    /**
     * 将决策树模型保存为xml文件
     *
     * @param treeNode  决策树模型
     * @param modelFile 输出文件
     */
    public static void saveTree2XML(TreeNode treeNode, String modelFile) {
        Document xml = DocumentHelper.createDocument();
        Element root = xml.addElement("root");
        Element nextNode = root.addElement("DecisionTree");
        generateXML(treeNode, nextNode);
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            Writer fileWriter = new FileWriter(modelFile);
            XMLWriter output = new XMLWriter(fileWriter, format);
            output.write(xml);
            output.close();
        } catch (IOException e) {
            LOGGER.error("save decision tree as xml error.", e);
        }
    }

    private static void generateXML(TreeNode treeNode, Element node) {
        if (null == treeNode.getChildNodes()) {
            return;
        }
        treeNode.getChildNodes().forEach(childNode -> {
            Element nextNode = node.addElement(treeNode.getNode());

            nextNode.addAttribute("value", childNode.getDirectedEdgeValue());
            if (childNode.isLeaf()) {
                nextNode.setText(childNode.getLeafValue());
            }
            generateXML(childNode, nextNode);
        });
    }


    /**
     * 将原始数据中的连续变量为离散变量
     *
     * @param trainingData 训练数据集合
     */
    public static void transformContinuouslyVariables(List<List<String>> trainingData) {
        List<Integer> indexList = getContinuouslyVariableIndex(trainingData);
        indexList.forEach(colIndex -> {
            List<ContinuouslyVariable> continuouslyVariableList = extractContinuouslyVariable(trainingData, colIndex);
            continuouslyVariableList.sort(Comparator.comparing(ContinuouslyVariable::getValue)); // 升序排列
            int threshold = getAttributeSplitThreshold(continuouslyVariableList);
            int thresholdAttribute = continuouslyVariableList.get(threshold).getValue();

            transformContinuouslyVariablesByThreshold(trainingData, colIndex, thresholdAttribute);
        });
    }

    /**
     * 根据阈值修改原始数据的某特征列，将连续变量修改为离散变量
     *
     * @param trainingData       训练数据集
     * @param colIndex           特征列索引
     * @param thresholdAttribute 特征中用来进行属性值分割的阈值
     */
    private static void transformContinuouslyVariablesByThreshold(List<List<String>> trainingData, Integer colIndex, int thresholdAttribute) {
        trainingData.stream().skip(1).forEach(rowData -> {
            if (Integer.parseInt(rowData.get(colIndex)) <= thresholdAttribute) {
                rowData.set(colIndex, "<=" + thresholdAttribute);
            } else {
                rowData.set(colIndex, ">" + thresholdAttribute);
            }
        });
    }

    /**
     * 计算连续变量的分割阈值
     *
     * @param continuouslyVariableList 某列的连续变量集合
     * @return 最佳分割阈值
     */
    private static int getAttributeSplitThreshold(List<ContinuouslyVariable> continuouslyVariableList) {
        double maxInfoEntropy = 0.0; // 分割过程中的最大信息熵
        int optimalSplitIndex = 0; // 最佳的分割位置
        for (int index = 0; index < continuouslyVariableList.size() - 1; index++) {
            // 计算每次分割的信息熵（信息量）
            double infoEntropy = getInfoEntropyByThreshold(continuouslyVariableList, index);
            if (maxInfoEntropy < infoEntropy) {
                maxInfoEntropy = infoEntropy;
                optimalSplitIndex = index;
            }
        }
        return optimalSplitIndex;
    }

    /**
     * 计算本次分割的信息熵
     *
     * @param continuouslyVariableList 某列的连续变量集合
     * @param splitIndex               某列的连续变量集合的索引下标
     * @return 信息熵
     */
    private static double getInfoEntropyByThreshold(List<ContinuouslyVariable> continuouslyVariableList, int splitIndex) {
        // 获取该列连续变量值对应的类值
        Set<String> classifySet = continuouslyVariableList.stream()
                .filter(ToolKits.distinctByKey(ContinuouslyVariable::getClassify))
                .collect(Collectors.toList()).stream().map(ContinuouslyVariable::getClassify).collect(Collectors.toSet());

        // 利用某列的连续变量集合的下标，从左到右不断的移动分割集合为两个部分，分别获取类对应的出现数目
        List<Integer> leftClassifyCountList = getContinuouslyVariableMap(continuouslyVariableList, 0, splitIndex, classifySet);
        List<Integer> rightClassifyCountList = getContinuouslyVariableMap(continuouslyVariableList, splitIndex + 1, continuouslyVariableList.size() - 1, classifySet);

        return infoEntropy(leftClassifyCountList, rightClassifyCountList);
    }

    /**
     * 计算特征分割后值的信息熵
     *
     * @param leftClassifyCountList  被某一个阈值分隔后的左半部分结果分布数组
     * @param rightClassifyCountList 被某一个阈值分隔后的右半部分结果分布数组
     * @return 属性值的信息熵
     */
    private static double infoEntropy(List<Integer> leftClassifyCountList, List<Integer> rightClassifyCountList) {
        double totalCount;
        double leftCount = 0d;
        double rightCount = 0d;

        for (int count : leftClassifyCountList) {
            if (0 == count) {
                return 0d;
            }
            leftCount += count;
        }

        for (int count : rightClassifyCountList) {
            if (0 == count) {
                return 0d;
            }
            rightCount += count;
        }
        totalCount = leftCount + rightCount;

        return (leftCount / totalCount) * infoEntropy(leftClassifyCountList) + (rightCount / totalCount) * infoEntropy(rightClassifyCountList);
    }

    /**
     * N分类时，计算信息熵
     *
     * @param classifyCountList 被某一个阈值分割后的某部分结果分布数组
     * @return 信息熵
     */
    private static double infoEntropy(List<Integer> classifyCountList) {
        double entropy = 0d;
        int totalCount = classifyCountList.stream().mapToInt(Integer::intValue).sum();
        for (int count : classifyCountList) {
            entropy += -1.0 * (1.0 * count / totalCount) * ToolKits.log2(1.0 * count / totalCount);
        }
        return entropy;
//        将二分类结果计算
//        int a = classifyCountList.get(0);
//        int b = classifyCountList.get(1);
//        int totalCount = a + b;
//        return -1.0 * (1.0 * a / totalCount) * ToolKits.log2(1.0 * a / totalCount) - (1.0 * b / totalCount) * ToolKits.log2(1.0 * b / totalCount);
    }

    /**
     * 统计值为连续变量的某列的类值的结果分布（出现次数）
     *
     * @param continuouslyVariableList 某列的连续变量集合
     * @param startIndex               开始索引
     * @param endIndex                 结束索引
     * @param classifySet              某列中连续变量值对应的类值
     * @return 类值的出现次数
     */
    private static List<Integer> getContinuouslyVariableMap(List<ContinuouslyVariable> continuouslyVariableList, int startIndex, int endIndex, Set<String> classifySet) {
        // 统计值为连续变量的某列的类值的结果分布（出现次数）
        Map<String, Integer> classifyCountMap = Maps.newHashMap();
        classifySet.forEach(classify -> classifyCountMap.put(classify, 0));

        for (int index = startIndex; index <= endIndex; index++) {
            String classify = continuouslyVariableList.get(index).getClassify();
            if (classifyCountMap.containsKey(classify)) {
                classifyCountMap.put(classify, classifyCountMap.get(classify) + 1);
            } else {
                classifyCountMap.put(classify, 1);
            }
        }
        return classifyCountMap.keySet().stream().map(classifyCountMap::get).collect(Collectors.toList());
    }


    /**
     * 获取训练集中的某列的全部的连续变量值
     *
     * @param trainingData 训练集数据D
     * @param colIndex     连续变量的列索引
     * @return 某列的全部的连续变量值集合
     */
    private static List<ContinuouslyVariable> extractContinuouslyVariable(List<List<String>> trainingData, int colIndex) {
        return trainingData.stream().skip(1).map(rowData -> {
            ContinuouslyVariable cv = new ContinuouslyVariable();
            cv.setValue(Integer.parseInt(rowData.get(colIndex)));
            int lastRowIndex = rowData.size() - 1;
            cv.setClassify(rowData.get(lastRowIndex));
            return cv;
        }).collect(Collectors.toList());
    }

    /**
     * 获得训练集中特征属性值为连续变量的列索引（特征索引）
     *
     * @param trainingData 训练数据集合
     * @return 连续变量的列索引
     */
    private static List<Integer> getContinuouslyVariableIndex(List<List<String>> trainingData) {
        List<Integer> indexList = new ArrayList<>();
        List<String> lastRowData = trainingData.get(trainingData.size() - 1);
        for (int index = 1; index < lastRowData.size() - 1; index++) {
            if (NumberUtils.isDigits(lastRowData.get(index))) {
                indexList.add(index);
            }
        }
        return indexList;
    }


}
