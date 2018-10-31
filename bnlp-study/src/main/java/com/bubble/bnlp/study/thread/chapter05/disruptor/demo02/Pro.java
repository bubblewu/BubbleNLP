package com.bubble.bnlp.study.thread.chapter05.disruptor.demo02;

import com.lmax.disruptor.RingBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wugang
 * date: 2018-10-30 10:54
 **/
public class Pro<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Pro.class);

    private final RingBuffer<CommonData> ringBuffer;

    public Pro(RingBuffer<CommonData> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void pushData(long uid, T t) {
        long sequence = ringBuffer.next();
        try {
            CommonData data = ringBuffer.get(sequence);
            data.setUid(uid);
            data.setT(t);
        } finally {
            ringBuffer.publish(sequence);
        }
    }

}
