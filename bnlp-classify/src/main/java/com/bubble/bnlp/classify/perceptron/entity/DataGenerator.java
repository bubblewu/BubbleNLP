package com.bubble.bnlp.classify.perceptron.entity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

/**
 * 训练数据生成
 *
 * @author wugang
 * date: 2019-01-04 14:42
 **/
public class DataGenerator {

    public static void main(String[] args) {
        String basePath = "bnlp-classify/src/main/resources/data/perceptron/";
        String trainFile = basePath + "train/train.txt";
        int trainSize = 7000;
        generate(trainFile, trainSize);

        String testFile = basePath + "test/test.txt";
        int testSize = 3000;
        generate(testFile, testSize);

    }

    private static void generate(String file, int size) {
        try {
            FileOutputStream fos = new FileOutputStream(new File(file));
            Random random = new Random();
            for (int i = 0; i < size / 2; i++) {
                String line = "";
                line += getRandomNumber(random);
                line += ",";
                line += getRandomNumber(random);
                line += ",";
                line += "1";
                line += "\n";
                fos.write(line.getBytes());
            }
            for (int i = 0; i < size / 2; i++) {
                String line = "";
                line += -getRandomNumber(random);
                line += ",";
                line += -getRandomNumber(random);
                line += ",";
                line += "-1";
                line += "\n";
                fos.write(line.getBytes());
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int getRandomNumber(Random random) {
        int randomNumber = random.nextInt(100);
        while (randomNumber == 0) {
            randomNumber = random.nextInt(100);
        }
        return randomNumber;
    }

}
