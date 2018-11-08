package com.bubble.bnlp.study.queue;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 参考：https://www.cnblogs.com/dragonsuc/p/5167285.html
 *
 * @author wugang
 * date: 2018-11-02 13:23
 **/
public class BlockingQueueTest {

    /**
     * 注意：生产者和消费者目前实现版本不能多对一或多对多
     *
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        // 声明一个容量为10的缓存队列
        BlockingQueue<String> queue = new LinkedBlockingQueue<>(10);

        Producer producer1 = new Producer(queue);
        Producer producer2 = new Producer(queue);
        Producer producer3 = new Producer(queue);
        Consumer consumer = new Consumer(queue);
//        Consumer consumer2 = new Consumer(queue);

        // 借助Executors
        ExecutorService service = Executors.newCachedThreadPool();
        // 启动线程
        service.execute(producer1);
        service.execute(producer2);
        service.execute(producer3);
        service.execute(consumer);
//        service.execute(consumer2);

        Thread.sleep(2000);
        // 退出Executor
        service.shutdown();

    }


    /**
     * 生产者
     */
    private static class Producer implements Runnable {
        private BlockingQueue<String> queue;

        private volatile boolean isRunning = true;
        private static AtomicInteger count = new AtomicInteger();
        private static final int DEFAULT_RANGE_FOR_SLEEP = 1000;

        public Producer(BlockingQueue<String> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            String data;
            Random random = new Random();
            System.out.println("启动生产者线程: " + Thread.currentThread().getName());
            // 注意：只适用于多个生产者对应单个消费者的情况；
            // 因为后面每个消费者结束后都加了"-1"特殊值标识结束。所以需判断防止新的线程开始值就是-1。
            if ("-1".equals(queue.peek())) {
                queue.poll();
            }
            try {
                for (int i = 0; i < 10; i++) {
//                    System.out.println(Thread.currentThread().getName() + " 正在生产数据...");
                    Thread.sleep(random.nextInt(DEFAULT_RANGE_FOR_SLEEP));
                    int nowCount = count.incrementAndGet();
                    if (nowCount == 5) {
                        System.err.println("sleep 10s");
                        Thread.sleep(10000);
                    }
                    data = "data: " + nowCount;
                    System.out.println(Thread.currentThread().getName() + " 将数据：" + data + "放入队列...");
                    if (!queue.offer(data, 2, TimeUnit.SECONDS)) {
                        System.out.println(Thread.currentThread().getName() + " 放入数据失败：" + data);
                    }
                }
                queue.offer("-1");

            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            } finally {
                System.out.println(Thread.currentThread().getName() + " 退出生产线程。");
            }

        }

    }


    /**
     * 消费者
     */
    private static class Consumer implements Runnable {
        private BlockingQueue<String> queue;
        private static final int DEFAULT_RANGE_FOR_SLEEP = 1000;

        public Consumer(BlockingQueue<String> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            System.out.println("启动消费者线程:" + Thread.currentThread().getName());
            Random r = new Random();
            boolean isRunning = true;
            if ("-1".equals(queue.peek())) {
                queue.poll();
            }
            if (!"-1".equals(queue.peek())) {
                try {
                    while (isRunning) {
                        String data = queue.poll();
                        if (null != data && !"-1".equals(data)) {
                            System.out.println(Thread.currentThread().getName() + " 拿到数据：" + data);
                            System.out.println(Thread.currentThread().getName() + " 正在消费数据：" + data);
                            Thread.sleep(r.nextInt(DEFAULT_RANGE_FOR_SLEEP));
                        } else if ("-1".equals(data)) {
                            System.out.println(Thread.currentThread().getName() + " 消费线程：" + data);
                            queue.offer("-1");
                            isRunning = false;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                } finally {
                    System.out.println("退出消费者线程:" + Thread.currentThread().getName());
                }
            }

        }

    }


}
