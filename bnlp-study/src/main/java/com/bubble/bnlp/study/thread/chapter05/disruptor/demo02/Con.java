package com.bubble.bnlp.study.thread.chapter05.disruptor.demo02;

import com.lmax.disruptor.WorkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wugang
 * date: 2018-10-30 10:58
 **/
public class Con<T> implements WorkHandler<CommonData<T>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Con.class);

    @Override
    public void onEvent(CommonData<T> event) throws Exception {
//        LOGGER.info("start rec for user {}", event.getUserId());
//        rec(event.getUserId());
        LOGGER.info("thread {}, id {}, user {}, data size {}",
                Thread.currentThread().getName(),
                Thread.currentThread().getId(),
                event.getUid(),
                event.getT()
        );

    }
}
