package com.bubble.bnlp.job;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wugang
 * date: 2018-09-27 18:37
 **/
public class LogTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogTest.class);


    @Test
    public void testLog() {
        LOGGER.info("test log");
    }


}
