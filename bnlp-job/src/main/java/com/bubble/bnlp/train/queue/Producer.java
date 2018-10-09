package com.bubble.bnlp.train.queue;

import java.util.concurrent.BlockingQueue;

/**
 * 生产者
 *
 * @author wugang
 * date: 2018-10-08 14:07
 **/
public class Producer implements Runnable {
    private BlockingQueue<String> queue;

    public Producer(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        System.out.println("I have made a product:" + Thread.currentThread().getName());
        for (int i = 0; i < 10; i++) {
            try {
                queue.put(String.valueOf(i));//如果队列是满的话，会阻塞当前线程
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            queue.put("END");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("producer done. " + Thread.currentThread().getName());
    }

}
