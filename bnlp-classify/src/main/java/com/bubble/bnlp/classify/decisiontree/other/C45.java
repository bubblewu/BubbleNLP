package com.bubble.bnlp.classify.decisiontree.other;

import com.bubble.bnlp.bean.tree.Tree;
import com.bubble.bnlp.bean.tree.structure.BasicDataSet;
import com.bubble.bnlp.bean.tree.structure.Decision;
import com.bubble.bnlp.bean.tree.structure.TypedDataSet;
import com.bubble.bnlp.common.ToolKits;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.map.LazyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * C4.5算法
 *
 * @author wugang
 * date: 2018-12-13 14:48
 **/
public class C45 {
    private static final Logger LOGGER = LoggerFactory.getLogger(C45.class);

    private Transformer classMapTransformer;
    private Transformer attributeTransformer;
    // 存储当前路径信息
    private LinkedList<Decision> stack = null;

    public C45() {
        classMapTransformer = arg0 -> (Object) 0;
        attributeTransformer = arg0 -> new BasicDataSet();
    }

    public void process(BasicDataSet dataSet, int classColumn, List<Integer> enableInputColumns, List<String> featureNames) {
        stack = new LinkedList<>();
        Tree<Decision> decisionTree = c45(dataSet, classColumn, enableInputColumns, featureNames);
        System.out.println(decisionTree);
    }

    /**
     * C4.5算法
     *
     * @param dataSet            数据集
     * @param classColumn        类的列索引
     * @param enableInputColumns 有效特征的列索引（特征索引）
     * @param featureNames       特征名称
     */
    @SuppressWarnings("unchecked")
    private Tree<Decision> c45(BasicDataSet dataSet, int classColumn, List<Integer> enableInputColumns, List<String> featureNames) {
        Tree<Decision> result = new Tree<>(new Decision(), null);

        // C4.5算法需要数据类型信息，以确定使用离散属性划分还是数值划分
        TypedDataSet typedDataSet = (TypedDataSet) dataSet;
        final Class<?>[] types = typedDataSet.getTypes();
        Map<String, Integer> classCountMap = LazyMap.decorate(Maps.newTreeMap(), getClassMapTransformer());
        // 将输入的特征数据根据列号建立列索引集合，结构为：[特征列索引，该特征某个属性，包含该属性的原数据]
        List<Map<Object, TypedDataSet>> attributeMapList = Lists.newArrayListWithCapacity(enableInputColumns.size());

        initAttributeMapList(enableInputColumns, types, attributeMapList);
        genAttributeMapList(dataSet, classColumn, enableInputColumns, classCountMap, attributeMapList);

        List<Integer> newFeatureIndexList = Lists.newArrayList(enableInputColumns);
        filterFeature(attributeMapList, newFeatureIndexList);

        result.getData().setClassCountMap(classCountMap);
        if (newFeatureIndexList.size() == 0 || classCountMap.size() <= 1) {
            LOGGER.info("Branch finished: {}", stack);
            return result;
        }

        List<Integer> classCountList = Lists.newArrayListWithCapacity(classCountMap.size());
        classCountList.addAll(classCountMap.values());
        int classTotalCount = classCountMap.values().stream().mapToInt(c -> c).sum();
        // 当前状态下的总的信息熵
        double totalEntropy = Entropy.entropy(classTotalCount, classCountList);
        LOGGER.info("totalEntropy = {}", totalEntropy);
        double bestIGR = Double.MIN_VALUE;
        int selectedFeatureIndex = -1;
        Object splitValue = null;
        TypedDataSet selectedHeadDataSet = null;
        TypedDataSet selectedTailDataSet = null;

        // 遍历计算各特征的属性值
        for (int i = 0; i < attributeMapList.size(); i++) {
            Map<Object, TypedDataSet> attributeMap = attributeMapList.get(i);
            // 每个属性计算E(A)
            double totalE = 0.0;
            double totalSplitInfo = 0.0;
            TypedDataSet bestHeadDataSet = null;
            TypedDataSet bestTailDataSet = null;
            Object bestSplitValue = null;
            // 属性为数值型
            if (ToolKits.isNumeric(typedDataSet.getType(newFeatureIndexList.get(i)))) {
                double bestGainRatio = -Double.MAX_VALUE;
                TreeMap<Object, TypedDataSet> valueMap = new TreeMap<>(attributeMap);
                for (Object key : valueMap.keySet()) {
                    Map<Object, TypedDataSet> headMap = valueMap.headMap(key);
                    if (headMap.isEmpty()) {
                        continue;
                    }
                    Map<Object, TypedDataSet> tailMap = valueMap.tailMap(key);
                    TypedDataSet headDataSet = convertDataSet(headMap.values());
                    TypedDataSet tailDataSet = convertDataSet(tailMap.values());

                    double conditionalEntropy = (Entropy.conditionalEntropy(headDataSet, classColumn, (SortedSet<String>) classCountMap.keySet())
                            + Entropy.conditionalEntropy(tailDataSet, classColumn, (SortedSet<String>) classCountMap.keySet())) / classTotalCount;
                    double splitInfo = (splitInfoPart(headDataSet.size(), classTotalCount) + splitInfoPart(tailDataSet.size(), classTotalCount)) / classTotalCount;
                    double gainRatio = (totalEntropy - conditionalEntropy) / splitInfo;

                    // 计算最大信息增益率
                    if (gainRatio > bestGainRatio) {
                        bestGainRatio = gainRatio;
                        totalE = conditionalEntropy;
                        totalSplitInfo = splitInfo;
                        bestSplitValue = key;
                        bestHeadDataSet = headDataSet;
                        bestTailDataSet = tailDataSet;
                    }
                }
            } else {
                for (Map.Entry<Object, TypedDataSet> entry : attributeMap.entrySet()) {
                    Object attrValue = entry.getKey();
                    BasicDataSet subDataSet = entry.getValue();
                    double ePart = Entropy.conditionalEntropy(subDataSet, classColumn, (SortedSet) classCountMap.keySet());
                    double splitInfoPart = splitInfoPart(subDataSet.size(), classTotalCount);
                    totalE += ePart;
                    totalSplitInfo += splitInfoPart;
                    LOGGER.info("conditionalEntropy {} = {}", attrValue, ePart);
                }
                totalE /= classTotalCount;
                totalSplitInfo /= classTotalCount;
            }

            double gainRatio = (totalEntropy - totalE) / totalSplitInfo;
            LOGGER.info("GainRatio({}) = {}", i, gainRatio);
            if (gainRatio > bestIGR) {
                bestIGR = gainRatio;
                selectedFeatureIndex = i;
                selectedHeadDataSet = bestHeadDataSet;
                selectedTailDataSet = bestTailDataSet;
                splitValue = bestSplitValue;
            }
        }

        if (selectedFeatureIndex < 0) {
            LOGGER.info("Branch finished: {}", stack);
            return result;
        }
        Map<Object, TypedDataSet> attrMap = attributeMapList.get(selectedFeatureIndex);
        if (ToolKits.isNumeric(typedDataSet.getType(newFeatureIndexList.get(selectedFeatureIndex)))) {
            String attribute = featureNames.get(newFeatureIndexList.get(selectedFeatureIndex));
            Decision decision = new Decision(attribute, "<" + splitValue);
            stack.addLast(decision);
            Tree<Decision> leftTree = c45(selectedHeadDataSet, classColumn, newFeatureIndexList, featureNames);
            leftTree.getData().setFeature(decision.getFeature());
            leftTree.getData().setAttribute(decision.getAttribute());
            stack.removeLast();

            decision = new Decision(featureNames.get(newFeatureIndexList.get(selectedFeatureIndex)), ">=" + splitValue);
            stack.addLast(decision);
            Tree<Decision> rightTree = c45(selectedTailDataSet, classColumn, newFeatureIndexList, featureNames);
            rightTree.getData().setFeature(decision.getFeature());
            rightTree.getData().setAttribute(decision.getAttribute());
            stack.removeLast();
            result.addChild(leftTree);
            result.addChild(rightTree);
        } else {
            newFeatureIndexList.remove(selectedFeatureIndex);
            for (Map.Entry<Object, TypedDataSet> entry : attrMap.entrySet()) {
                String feature = featureNames.get(enableInputColumns.get(selectedFeatureIndex));
                Object attribute = entry.getKey();
                Decision decision = new Decision(feature, attribute);
                stack.addLast(decision);
                Tree<Decision> child = c45(entry.getValue(), classColumn, newFeatureIndexList, featureNames);
                child.getData().setFeature(decision.getFeature());
                child.getData().setAttribute(decision.getAttribute());
                result.addChild(child);
                stack.removeLast();
            }
        }
        Pruning.pruning(result);
        return result;
    }

    /**
     * 用于计算属性划分后的信息增益率Ha(D)，参考http://blog.csdn.net/x454045816/article/details/44726921中SplitInfo的公式
     * 即训练集D关于特征A的熵Ha(D)，
     *
     * @param thisAttrSize    当前属性取值数据子集的大小
     * @param classTotalCount 总数据集大小
     * @return Ha(D)
     */
    private double splitInfoPart(int thisAttrSize, int classTotalCount) {
        return -thisAttrSize * Math.log(1.0 * thisAttrSize / classTotalCount);
    }

    private TypedDataSet convertDataSet(Collection<TypedDataSet> dataSets) {
        TypedDataSet result = null;
        for (TypedDataSet tds : dataSets) {
            if (result == null) {
                result = new TypedDataSet(dataSets.size(), tds.getTypes());
            }
            result.addAll(tds);
        }
        return result;
    }

    /**
     * 如某特征的属性无二义性，则删除此特征
     *
     * @param attributeMapList    列索引数据
     * @param newFeatureIndexList 新的可用特征索引集合
     */
    private void filterFeature(List<Map<Object, TypedDataSet>> attributeMapList, List<Integer> newFeatureIndexList) {
        for (int i = 0; i < attributeMapList.size(); i++) {
            if (attributeMapList.get(i).size() <= 1) {
                newFeatureIndexList.remove(i);
                attributeMapList.remove(i);
                i--;
            }
        }
    }

    /**
     * 构造列索引数据集：[特征列索引，该特征某个属性，包含该属性的原数据]
     *
     * @param dataSet            数据集
     * @param classColumn        类的列索引
     * @param enableInputColumns 有效特征的列索引
     * @param classCountMap      [类，数目]映射集合
     * @param attributeMapList   列索引数据
     */
    private void genAttributeMapList(BasicDataSet dataSet, int classColumn, List<Integer> enableInputColumns, Map<String, Integer> classCountMap, List<Map<Object, TypedDataSet>> attributeMapList) {
        for (Object[] parts : dataSet) {
            String clz = String.valueOf(parts[classColumn]);
            classCountMap.put(clz, classCountMap.get(clz) + 1);
            for (int k = 0; k < enableInputColumns.size(); k++) {
                attributeMapList.get(k).get(parts[enableInputColumns.get(k)]).add(parts);
            }
        }
    }

    /**
     * 初始化特征索引集合
     *
     * @param enableInputColumns 有效特征的列索引
     * @param types              各特征的数据类型
     * @param attributeMapList   列索引集合：[特征列索引，该特征某个属性，包含该属性的原数据]
     */
    @SuppressWarnings("unchecked")
    private void initAttributeMapList(List<Integer> enableInputColumns, Class<?>[] types, List<Map<Object, TypedDataSet>> attributeMapList) {
        enableInputColumns.forEach(index -> {
            Map<Object, TypedDataSet> attributeMap = LazyMap.decorate(Maps.newTreeMap(), () -> new TypedDataSet(types));
            attributeMapList.add(attributeMap);
        });
    }


    public Transformer getClassMapTransformer() {
        return classMapTransformer;
    }

    public void setClassMapTransformer(Transformer classMapTransformer) {
        this.classMapTransformer = classMapTransformer;
    }

    public Transformer getAttributeTransformer() {
        return attributeTransformer;
    }

    public void setAttributeTransformer(Transformer attributeTransformer) {
        this.attributeTransformer = attributeTransformer;
    }

}
