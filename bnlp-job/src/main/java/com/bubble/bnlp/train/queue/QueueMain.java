package com.bubble.bnlp.train.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 测试：BlockingQueue实现生产者-消费者
 *
 * @author wugang
 * date: 2018-10-08 14:08
 **/
public class QueueMain {

    public static void main(String[] args) {
        //LinkedBlockingQueue默认大小为Integer.MAX_VALUE; FIFO先进先出
        BlockingQueue<String> queue = new LinkedBlockingDeque<>(2);
        Producer producer = new Producer(queue);

        Consumer consumer = new Consumer(queue);
        for (int i = 0; i < 5; i++) {
            new Thread(producer, "Producer" + (i + 1)).start();
            new Thread(consumer, "Consumer" + (i + 1)).start();
        }
    }

}
