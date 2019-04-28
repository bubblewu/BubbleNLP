package com.bubble.bnlp.classify.perceptron.seg;

import java.io.*;

/**
 * 分词
 *
 * @author wugang
 * date: 2019-01-07 16:58
 **/
public class Segment {

    public static Splitter design(String filename) {//design features
        String line = null;
        Splitter splitter = new Splitter();
        try {
            FileInputStream fis = new FileInputStream(filename);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                splitter.makeStat(line);
            }
            System.out.println("Stat Done.");
            fis.close();
            isr.close();
            br.close();
            splitter.makeDict();
            System.out.println("Dict Done.");
            if (splitter.tooManyFeatures()) {
                System.out.println("Too many features!!!");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(line);
        }
        return splitter;
    }

    public static void train(Splitter splitter, String filename, String outfilename) {
        String line = null;
        try {
            FileInputStream fis = new FileInputStream(filename);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                // splitter.mark(line);
                splitter.markFragment(line);//don't deal with symbols
            }
            System.out.println("Mark Done.");
            splitter.percept();
            System.out.println("Percept Done.");
            splitter.output(outfilename);
            System.out.println("Output Done.");
            fis.close();
            isr.close();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(line);
        }
    }

    public static void test(Splitter splitter, String filename, String outfilename) {
        String line = null;
        try {
            FileInputStream fis = new FileInputStream(filename);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(isr);

            File file = new File(outfilename);
            FileWriter out = new FileWriter(file);
            while ((line = br.readLine()) != null) {
                // splitter.segment(line, out);
                splitter.segmentFragment(line.toCharArray(), out);
                out.write("\r\n");
            }
            out.flush();
            out.close();

            fis.close();
            isr.close();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(line);
        }
    }

    public static void main(String[] args) {
        String basePath = "bnlp-classify/src/main/resources/data/perceptron/seg/";

        Splitter splitter = design(basePath + "train.txt");
        if (splitter != null) {
            train(splitter, basePath + "train.txt", basePath + "theta.txt");
            test(splitter, basePath + "test.txt", basePath + "test_output.txt");
        }

		/*
		Splitter splitter = design("train.txt");
		splitter.input();
		test(splitter, "test.txt", "test_output.txt");
		*/
    }

}
