package com.bubble.bnlp.classify.decisionTree;

import com.google.common.collect.Lists;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 根据生成的决策树模型进行预测
 *
 * @author wugang
 * date: 2018-11-15 17:38
 **/
public class TreePredicate {
    private static final Logger LOGGER = LoggerFactory.getLogger(TreePredicate.class);

    public List<String> predicate(Map<String, String> dataMap, String model) {
        List<String> classifyList = Lists.newArrayList();
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(new File(model));
            Element root = document.getRootElement();
            Element tree = root.element("DecisionTree");
            predicate(tree, dataMap, classifyList);
            if (classifyList.isEmpty()) {
                genNegative(tree, dataMap, classifyList);
            }
        } catch (DocumentException e) {
            LOGGER.error("read model file error.", e);
        }

        return classifyList;
    }

    /**
     * 攻略推荐，只要满足一个结点条件，就可以将该结点下的所有类值返回。
     * 注意：只适用于攻略推荐；
     */
    private void genNegative(Element tree, Map<String, String> dataMap, List<String> classifyList) {
        Iterator it = tree.elementIterator();
        while (it.hasNext()) {
            Element element = (Element) it.next();
            String feature = element.getName();
            List<String> attributeList = Arrays.stream(element.attribute("value").getValue().split(",")).collect(Collectors.toList());
            for (String attribute : attributeList) {
                if (dataMap.get(feature).equals(attribute)) {
                    for (Object a : element.elements()) {
                        Element ele = (Element) a;
                        Attribute ab = ele.attribute("value");
                        if ("Negative".equals(ab.getValue())) {
                            String classify = ele.getTextTrim();
                            List<String> tempClassifyList = Arrays.stream(classify.split(",")).collect(Collectors.toList());
                            classifyList.addAll(tempClassifyList);
                        }

                    }

                }
            }
        }
    }

    private boolean stop = false;

    private boolean isStop() {
        return stop;
    }

    private void setStop(boolean st) {
        this.stop = st;
    }

    private void predicate(Element tree, Map<String, String> dataMap, List<String> classifyList) {
        for (Iterator it = tree.elementIterator(); it.hasNext(); ) {
            if (isStop()) {
                break;
            }
            Element element = (Element) it.next();

            String feature = element.getName();
            List<String> attributeList = Arrays.stream(element.attribute("value").getValue().split(",")).collect(Collectors.toList());
            List<String> tempClassifyList = Arrays.stream(element.getTextTrim().split(",")).collect(Collectors.toList());
            tempClassifyList = tempClassifyList.stream().filter(classify -> !classify.isEmpty()).collect(Collectors.toList());
            for (String attribute : attributeList) {
                if (attribute.contains(">")) {
                    int threshold = Integer.parseInt(attribute.replace(">", ""));
                    int nowValue = Integer.parseInt(dataMap.get(feature));
                    if (nowValue > threshold) {
                        if (tempClassifyList.isEmpty()) {
                            predicate(element, dataMap, classifyList);
                        } else {
                            setStop(true);
                            classifyList.addAll(tempClassifyList);
                            break;
                        }
                    }
                } else if (attribute.contains("<=")) {
                    int threshold = Integer.parseInt(attribute.replace("<=", ""));
                    int nowValue = Integer.parseInt(dataMap.get(feature));
                    if (nowValue <= threshold) {
                        if (tempClassifyList.isEmpty()) {
                            predicate(element, dataMap, classifyList);
                        } else {
                            setStop(true);
                            classifyList.addAll(tempClassifyList);
                            break;
                        }
                    }
                } else if (dataMap.get(feature).equals(attribute)) {
                    if (tempClassifyList.isEmpty()) {
                        predicate(element, dataMap, classifyList);
                    } else {
                        setStop(true);
                        classifyList.addAll(tempClassifyList);
                        break;
                    }
                }
            }
        }
    }


    /**
     * 加载通用类别数据
     *
     * @param commonFile 通用数据文件
     * @return 类集合
     */
    public List<String> loadCommonClassify(String commonFile) {
        List<String> classifyList = Lists.newArrayList();
        try {
            List<String> commonDataList = Files.readAllLines(Paths.get(commonFile), Charset.defaultCharset());
            commonDataList.stream().skip(1).forEach(line -> {
                String[] datas = line.split(" ");
                classifyList.add(datas[datas.length - 1]);
            });
        } catch (NoSuchFileException nfe) {
            LOGGER.warn("no common classify data.");
        } catch (IOException e) {
            LOGGER.error("load common classify data error.", e);
        }
        return classifyList;
    }

}
