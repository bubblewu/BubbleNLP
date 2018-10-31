package com.bubble.bnlp.study.thread.chapter05.disruptor.demo02;

import com.lmax.disruptor.EventFactory;

/**
 * @author wugang
 * date: 2018-10-30 18:09
 **/
public class CommonFactory implements EventFactory<CommonData> {
    @Override
    public CommonData newInstance() {
        return new CommonData();
    }
}
