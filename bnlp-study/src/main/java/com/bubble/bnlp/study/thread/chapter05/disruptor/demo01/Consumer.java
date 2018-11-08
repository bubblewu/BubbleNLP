package com.bubble.bnlp.study.thread.chapter05.disruptor.demo01;

import com.lmax.disruptor.WorkHandler;

/**
 * 消费者。实现为disruptor框架的WorkHandler接口。
 * 读取生产的数据并进行处理。
 *
 * @author wugang
 * date: 2018-10-30 09:06
 **/
public class Consumer implements WorkHandler<PCData> {
    /**
     * 数据的读取已经由disruptor进行了封装，onEvent方法是框架的回调方法。
     * 所以，这里只需要简单的进行数据处理即可。
     *
     * @param pcData 数据
     * @throws Exception 异常
     */
    @Override
    public void onEvent(PCData pcData) throws Exception {
        long result = pcData.getValue() * pcData.getValue(); // 求整数的平方
        System.out.println(Thread.currentThread().getName() + " " + Thread.currentThread().getId()
                + "data: " + pcData.getValue()
                + " Event: " + result);
    }

}
