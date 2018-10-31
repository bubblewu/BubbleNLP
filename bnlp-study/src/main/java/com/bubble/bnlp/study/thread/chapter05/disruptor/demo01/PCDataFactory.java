package com.bubble.bnlp.study.thread.chapter05.disruptor.demo01;

import com.lmax.disruptor.EventFactory;

/**
 * 一个产生PCData的工厂类。
 * 它会在Disruptor（无锁的缓存框架，核心就是环形队列RingBuffer）系统初始化时，构造所有的缓冲区中的实例对象。
 * 也就是说Disruptor会预先分配空间。
 *
 * @author wugang
 * date: 2018-10-30 09:00
 **/
public class PCDataFactory implements EventFactory<PCData> {
    @Override
    public PCData newInstance() {
        return new PCData();
    }
}
