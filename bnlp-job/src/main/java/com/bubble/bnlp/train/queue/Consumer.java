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
        while (!Thread.currentThread().isInterrupted()) {
            try {
                String value = queue.take();//如果队列为空，会阻塞当前线程
                System.out.println(value);
                if ("END".equals(value)) {
                    queue.put("END");
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("consumer done." +  Thread.currentThread().getName());

    }

}
