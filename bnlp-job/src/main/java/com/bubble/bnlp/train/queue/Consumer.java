package com.bubble.bnlp.train.queue;

import java.util.concurrent.BlockingQueue;

/**
 * 消费者
 *
 * @author wugang
 * date: 2018-10-08 14:08
 **/
public class Consumer implements Runnable {
    private BlockingQueue<String> queue;

    public Consumer(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            String temp = queue.take();//如果队列为空，会阻塞当前线程
            System.out.println(temp);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
