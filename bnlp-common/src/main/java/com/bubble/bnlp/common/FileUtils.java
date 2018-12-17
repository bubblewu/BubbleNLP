package com.bubble.bnlp.common;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * 文件操作工具类
 *
 * @author wugang
 * date: 2018-12-17 15:46
 **/
public class FileUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

    public static List<String> readFile(String file) {
        List<String> dataList = Lists.newArrayList();
        try {
            dataList = Files.readAllLines(Paths.get(file), Charset.defaultCharset());
        } catch (IOException e) {
            LOGGER.error("read {} error.", file);
        }
        return dataList;
    }

}
