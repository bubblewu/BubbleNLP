package com.bubble.bnlp.study.thread.chapter05.disruptor.demo02;

/**
 *
 * @author wugang
 * date: 2018-10-30 18:07
 **/
public class CommonData<T> {

    private long uid;
    private T t;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }
}
