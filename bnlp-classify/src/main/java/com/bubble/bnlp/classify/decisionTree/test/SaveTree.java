package com.bubble.bnlp.classify.decisionTree.test;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.FileWriter;
import java.io.Writer;

/**
 * @author wugang
 * date: 2018-11-07 13:43
 **/
public class SaveTree {

    Document xmlDoc;
    Element root;

    public void save( BuildTree.TreeNode treeRoot ) throws Exception {
        xmlDoc = DocumentHelper.createDocument();
        root = xmlDoc.addElement("root");
        Element nextNode = root.addElement("Decision");
        toXML( treeRoot, nextNode );
        System.out.println(xmlDoc.asXML());
        OutputFormat format = OutputFormat.createPrettyPrint();
        Writer fileWriter = new FileWriter("/Users/wugang/workspace/java/BubbleNLP/bnlp-classify/src/main/java/com/bubble/bnlp/classify/decisionTree/output-weather.xml");
        XMLWriter output = new XMLWriter( fileWriter, format );
        output.write(xmlDoc);
        output.close();

        //FileUtils.writeStringToFile(new File("output.xml"), xmlDoc.asXML());
    }

    public void toXML(BuildTree.TreeNode treeRoot, Element node ) {

        if ( treeRoot.ChildNodes == null  ) {
            return ;
        }
        for ( String SituName:treeRoot.ChildNodes.keySet() ) {
            Element nextNode = node.addElement(treeRoot.AttributeName);
            BuildTree.TreeNode childNode = treeRoot.ChildNodes.get(SituName);
            nextNode.addAttribute("value", childNode.SituationName);
            if ( childNode.isLeafNode ) {
                nextNode.setText(childNode.AttributeName);
            }
            toXML( childNode, nextNode );
        }
    }

}
