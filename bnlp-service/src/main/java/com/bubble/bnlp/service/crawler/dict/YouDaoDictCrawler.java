package com.bubble.bnlp.service.crawler.dict;

import com.bubble.bnlp.common.http.HttpUtil;
import com.bubble.bnlp.common.word.PartOfSpeech;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * 查询单词的词性（借助有道词典）
 *
 * @author wugang
 * date: 2018-09-27 18:50
 **/
public class YouDaoDictCrawler {
    private static final Logger LOGGER = LoggerFactory.getLogger(YouDaoDictCrawler.class);
    private static final String YOUDAO_URL_BASE = "http://dict.youdao.com/w/"; // "http://dict.youdao.com/search?le=eng&keyfrom=dict.top&q=";

    public static void main(String[] args) {
        Instant begin = Instant.now();
        String word = "配置"; // crawler 配置
        List<String> posList = queryWordPos(word, false);
        posList.forEach(System.out::println);
        LOGGER.info("costs {} ms.", Duration.between(begin, Instant.now()).toMillis());
    }

    /**
     * 获取单词的所有词性
     *
     * @param word 单词
     * @return 词性集合
     */
    private static List<String> queryWordPos(String word, boolean isEnglish) {
        String url = YOUDAO_URL_BASE + word;
        Document doc = HttpUtil.doGetDoc(url);
        Elements els;
        if (isEnglish) {
            Elements elements = doc.getElementsByClass("trans-container"); //trans-container
            Element element = elements.first();
            els = element.getElementsByTag("li");
        } else {
            els = doc.select("ul>p.wordGroup>span:not(.contentTitle)");
        }
        return fetchPos(els);
    }

    private static List<String> fetchPos(Elements elements) {
        List<String> posList = new ArrayList<>(8);
        for (Element li : elements) {
            String explain = li.text();
            int pos = explain.indexOf('.');
            if (pos > 0) {
                String partSpeech = explain.substring(0, pos);

                if ("vt".equals(partSpeech) || "vi".equals(partSpeech)) {
                    partSpeech = "v";
                }
                if (!PartOfSpeech.values.containsKey(partSpeech)) {
                    return new ArrayList<>();
                }
                posList.add(partSpeech);
            }
        }
        return posList;
    }

}
