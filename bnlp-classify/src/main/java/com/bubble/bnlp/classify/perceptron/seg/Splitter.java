package com.bubble.bnlp.classify.perceptron.seg;

import com.bubble.bnlp.classify.perceptron.seg.Feature.FeatureType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wugang
 * date: 2019-01-07 16:53
 **/
public class Splitter {

    private static final char[] TAGCHAR = {'S', 'B', 'M', 'E'};
    private static final char[] SYMBOLS = {//to check '-' is not always right
            '。', '，', '、', '？', '！', '：', '…', '；', //'-',
            '“', '”', '‘', '’', '《', '》', '（', '）', '『', '』'};
    private static final double T0 = 0.3;
    private static final double ETA = 1;
    private static final int MAXEPOCH = 30;

    private static final int MINPOPULARITY = 3;
    public static final int MAXDICTSIZE = 900000;
    private Map<Feature, Integer> stat = new HashMap<>(2400000);
    private Map<Feature, Integer> dict = new HashMap<>(MAXDICTSIZE);
    private int dictSize = 0;
    private ArrayList<SparseVec> example = new ArrayList<>(5000000);

    private double[] theta = new double[4 * MAXDICTSIZE + 1];//dimensions

    Splitter() {
        Arrays.fill(theta, T0);
    }

    public final boolean tooManyFeatures() {//inline
        System.out.println("dictSize: " + dictSize);
        return dictSize >= MAXDICTSIZE;
    }

    private static final char tagToChar(int tag) {//inline
        return TAGCHAR[tag];
    }

    private static final boolean isSymbol(char c) {//inline
        for (char symbol : SYMBOLS) {
            if (c == symbol) {
                return true;
            }
        }
        return false;
    }

    private void addToStat(Feature feature) {//get popularity(frequency)
        if (!stat.containsKey(feature)) {
            stat.put(feature, 1);
        } else {
            stat.put(feature, stat.get(feature).intValue() + 1);
        }
    }

    private int getIndex(Feature feature) {
        if (dict.containsKey(feature)) {
            return dict.get(feature);
        } else {
            return -1;
        }
    }

    public void makeStat(String line) {
        char[] sentense = line.replaceAll("  ", "").toCharArray();
        int[] tags = makeTags(line, sentense.length);
        for (int i = 0; i < sentense.length; ++i) {
            addToStat(new Feature(FeatureType.U0, sentense[i]));
            if (i >= 1) {
                addToStat(new Feature(FeatureType.U_1, sentense[i - 1]));
                addToStat(new Feature(FeatureType.B_1, sentense[i - 1], sentense[i]));
                addToStat(new Feature(FeatureType.Tag_1, tagToChar(tags[i - 1])));
            }
            if (i >= 2) {
                addToStat(new Feature(FeatureType.U_2, sentense[i - 2]));
                addToStat(new Feature(FeatureType.B_2, sentense[i - 2], sentense[i - 1]));
                addToStat(new Feature(FeatureType.Tag_2, tagToChar(tags[i - 1])));
            }
            if (i < sentense.length - 1) {
                addToStat(new Feature(FeatureType.U1, sentense[i + 1]));
                addToStat(new Feature(FeatureType.B1, sentense[i], sentense[i + 1]));
            }
            if (i < sentense.length - 2) {
                addToStat(new Feature(FeatureType.U2, sentense[i + 2]));
                addToStat(new Feature(FeatureType.B2, sentense[i + 1], sentense[i + 2]));
            }
            if (i >= 1 && i < sentense.length - 1) {
                addToStat(new Feature(FeatureType.B0, sentense[i - 1], sentense[i + 1]));
            }
        }
    }

    public void makeDict() {
        for (Map.Entry<Feature, Integer> entry : stat.entrySet()) {
            if (entry.getValue() >= MINPOPULARITY) {
                dict.put(entry.getKey(), dictSize);
                dictSize++;
            }
        }
    }

    private int[] makeTags(String line, int len) {
        String[] words = line.split("  ");//2 spaces
        int[] tags = new int[len];
        int top = 0;
        for (String word : words) {
            if (word.length() == 0) {
                continue;
            } else if (word.length() == 1) {
                tags[top] = 0;//S;
            } else {
                tags[top] = 1;//B;
                for (int i = 1; i < word.length() - 1; ++i) {
                    tags[top + i] = 2;//M;
                }
                tags[top + word.length() - 1] = 3;//E;
            }
            top += word.length();
        }
        return tags;
    }

    public void markFragment(String line) {
        char[] sentense = line.replaceAll("  ", "").toCharArray();
        int[] tags = makeTags(line, sentense.length);
        for (int i = 0; i < sentense.length; ++i) {//add example
            example.add(makeSparseVec(i, sentense, tags));
            if (example.size() % 100000 == 0) {
                System.out.print(example.size() + "...");
            }
        }
    }

    public void mark(String line) {
        String regEx = "";
        for (int i = 0; i < SYMBOLS.length - 1; ++i) {
            regEx = regEx + String.valueOf(SYMBOLS[i]) + '|';
        }
        regEx += String.valueOf(SYMBOLS[SYMBOLS.length - 1]);
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(line);
        String[] fragment = line.split(regEx);
        for (int i = 0; i < fragment.length; ++i) {
            if (fragment[i].length() != 0) {
                markFragment(fragment[i]);
            }
        }
    }

    private SparseVec makeSparseVec(int index, char[] sentense, int[] tags) {
        ArrayList<Integer> temp = new ArrayList<Integer>();
        temp.add(getIndex(new Feature(FeatureType.U0, sentense[index])));
        if (index >= 1) {
            temp.add(getIndex(new Feature(FeatureType.U_1, sentense[index - 1])));
            temp.add(getIndex(new Feature(FeatureType.B_1, sentense[index - 1], sentense[index])));
            temp.add(getIndex(new Feature(FeatureType.Tag_1, tagToChar(tags[index - 1]))));
        }
        if (index >= 2) {
            temp.add(getIndex(new Feature(FeatureType.U_2, sentense[index - 2])));
            temp.add(getIndex(new Feature(FeatureType.B_2, sentense[index - 2], sentense[index - 1])));
            temp.add(getIndex(new Feature(FeatureType.Tag_2, tagToChar(tags[index - 2]))));
        }
        if (index < sentense.length - 1) {
            temp.add(getIndex(new Feature(FeatureType.U1, sentense[index + 1])));
            temp.add(getIndex(new Feature(FeatureType.B1, sentense[index], sentense[index + 1])));
        }
        if (index < sentense.length - 2) {
            temp.add(getIndex(new Feature(FeatureType.B2, sentense[index + 2])));
            temp.add(getIndex(new Feature(FeatureType.B2, sentense[index + 1], sentense[index + 2])));
        }
        if (index >= 1 && index < sentense.length - 1) {
            temp.add(getIndex(new Feature(FeatureType.B0, sentense[index - 1], sentense[index + 1])));
        }
        return (new SparseVec(temp, tags[index]));
        //only use correct current tag when training
        //it's okay to send an arbitrary tag when testing
    }

    private int predictWithoutTag(int index, char[] sentense, int[] tags, double[] theta) {
        double score, maxn = Double.NEGATIVE_INFINITY;
        int predictTag = -1;
        for (tags[index] = 0; tags[index] < 4; ++tags[index]) {//suppose the current tag
            SparseVec p = makeSparseVec(index, sentense, tags);
            score = p.multiply(tags[index], theta);
            if (score > maxn) {
                maxn = score;
                predictTag = tags[index];
            }
        }
        return predictTag;
    }

    public void percept() {//only use correct current tag here
        final int POINTNUM = example.size();
        for (int epoch = 0; epoch < MAXEPOCH; ++epoch) {
            int count = 0;
            for (SparseVec x : example) {
                int predictTag = x.predict(theta);
                if (x.tag != predictTag) {
                    x.addTo(x.tag, theta, ETA);
                    x.addTo(predictTag, theta, -ETA);
                    count++;
                }
            }
            System.out.print(count + " ");
        }
    }

    public void input(String filename) {
        try {
            File file = new File(filename);
            FileReader fr = new FileReader(file);
            BufferedReader in = new BufferedReader(fr);
            String line = null;

            line = in.readLine();
            final double inputT0 = Double.parseDouble(line);
            Arrays.fill(theta, inputT0);
            while ((line = in.readLine()) != null) {
                String[] fragment = line.split(" ");
                theta[Integer.parseInt(fragment[0])]
                        = Double.parseDouble(fragment[1]);
            }
            fr.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void output(String filename) {
        try {
            File file = new File(filename);
            FileWriter out = new FileWriter(file);
            out.write(T0 + "\r\n");
            for (int i = 0; i <= 4 * Splitter.MAXDICTSIZE; ++i) {
                if (theta[i] != T0) {
                    out.write(i + " " + theta[i] + "\r\n");
                }
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void segment(String line, FileWriter out) {
        String regEx = "";
        for (int i = 0; i < SYMBOLS.length - 1; ++i) {
            regEx = regEx + String.valueOf(SYMBOLS[i]) + '|';
        }
        regEx += String.valueOf(SYMBOLS[SYMBOLS.length - 1]);
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(line);
        String[] fragment = line.split(regEx);
        try {
            if (fragment.length == 0) {
                //the sentence is only a symbol: split cannot deal with that
                out.write(line);
            } else {
                for (int i = 0; i < fragment.length; ++i) {
                    if (fragment[i].length() != 0) {
                        segmentFragment(fragment[i].toCharArray(), out);
                    }
                    if (m.find()) {
                        out.write("  " + m.group() + "  ");
                    }
                }
            }
            out.write("\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void segmentFragment(char[] sentense, FileWriter out) {
        int[] tags = new int[sentense.length];
        try {
            for (int i = 0; i < sentense.length; ++i) {
                // tags[i] = predictWithoutTag(i, sentense, tags, theta);
                SparseVec x = makeSparseVec(i, sentense, tags);
                tags[i] = x.predict(theta);
                out.write(sentense[i]);
                if (i < sentense.length - 1) {
                    if (tags[i] == 0 || tags[i] == 3) {//end
                        out.write("  ");
                    } else if (isSymbol(sentense[i])) {//symbol
                        out.write("  ");//》的
                    } else if (isSymbol(sentense[i + 1])) {//the word before symbol
                        out.write("  ");//我）
                    }//special judge of symbols
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
