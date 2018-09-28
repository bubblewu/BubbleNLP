package com.bubble.bnlp.common.word;

import java.util.HashMap;

/**
 * 词性POS
 * @author wugang
 * date: 2018-09-27 19:04
 **/
public class PartOfSpeech {

    public final static byte start = 0;// 开始
    public final static byte end = 1;// 结束
    public final static byte adj = 2;// 形容词
    public final static byte adv = 3;// 副词
    public final static byte art = 4;// 冠词
    public final static byte pos = 5;// 所有格
    public final static byte pron = 6;// 代词
    public final static byte aux = 7;// 情态助动词
    public final static byte conj = 8;// 连接词
    public final static byte v = 9;// 动词
    public final static byte num = 10;// 数词
    public final static byte prep = 11;// 介词
    public final static byte punct = 12;// 标点
    public final static byte n = 13;// 名词
    public final static byte unknow = 14; // 未知

    public static String[] names = { "start", "end", "adj", "adv", "art",
            "pos", "pron", "aux", "conj", "v", "num", "prep", "punct", "n",
            "unknow" };

    public static HashMap<String, Byte> values;

    static {
        values = new HashMap<>();
        values.put("start", start);// 开始
        values.put("end", end);// 结束
        values.put("adj", adj);// 形容词
        values.put("adv", adv);// 副形词
        values.put("art", art);// 形语素
        values.put("pos", pos);// 名形词
        values.put("pron", pron);// 区别词
        values.put("aux", aux);// 连词
        values.put("conj", conj);// 副词
        values.put("v", v);// 副语素
        values.put("num", num);// 叹词
        values.put("prep", prep);// 方位词
        values.put("punct", punct);// 语素
        values.put("n", n);// 前接成分
        values.put("unknow", unknow);// 成语

    }

    public static String getName(byte b) {
        return names[b];
    }

    public static byte getId(String name) {
        return values.get(name);
    }

}
