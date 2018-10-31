package com.bubble.bnlp.study.thread.chapter05.disruptor.demo01;

/**
 * 定义数据格式
 *
 * @author wugang
 * date: 2018-10-30 08:59
 **/
public class PCData {

    private long value;

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
