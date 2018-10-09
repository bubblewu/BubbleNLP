package com.bubble.bnlp.train.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 多线程下的生产者-消费者实现demo
 *
 * @author wugang
 * date: 2018-10-09 11:47
 **/
public class ProducerConsumerDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerConsumerDemo.class);

    // 日志打印间隔
    public static int DEFAULT_LOG_INTERVAL = 1000 * 10;
    // 队列默认处理容量，为避免队列扩容造成额外性能损耗，默认不扩容，达到当前大小，进入等待，消费者处理一部分数据之后，生产者继续生产
    public static int DEFAULT_QUEUE_DISPOSE_SIZE = 1000 * 10;
    // 队列默认容量
    public static int DEFAULT_QUEUE_SIZE = DEFAULT_QUEUE_DISPOSE_SIZE + 200;
    // 每个队列满时默认休眠时间
    public static int DEFAULT_SLEEP_TIME = 100;
    // 线程池默认消费者数量
    public static int DEFAULT_CONSUMER_NUM = 2;
    // 用户的phoneId队列
    private LinkedBlockingQueue<Long> phoneIdQueue = new LinkedBlockingQueue<>(DEFAULT_QUEUE_SIZE);
    private Long STOP_FLAG = -1L;


    public static void main(String[] args) {
        ProducerConsumerDemo producerConsumerDemo = new ProducerConsumerDemo();
        producerConsumerDemo.execute();
    }

    public void execute() {
        UserProducer userProducer = new UserProducer();
        userProducer.setName("producer");
        userProducer.start();
        UserConsumer userConsumer = new UserConsumer();
        ThreadPoolExecutor executor = userConsumer.consumer();
        System.out.println("=====");

        threadMonitor(executor);
    }

    private void threadMonitor(ThreadPoolExecutor executor) {
        Runnable runnable = () -> {
            try {
                TimeUnit.SECONDS.sleep(2);
                while (executor.getActiveCount() > 0) {
                    System.out.println("sleep 1000");
                    TimeUnit.SECONDS.sleep(1);
                }
                executor.shutdown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        new Thread(runnable).start();
//        new Thread() {
//            @Override
//            public void run() {
//                try {
//                    sleep(2000);
//
//                    while (executor.getActiveCount() > 0) {
//                        System.out.println("sleep 1000");
//                        sleep(1000);
//                    }
//                    executor.shutdown();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }.start();
    }


    /**
     * 用户数据消费者
     */
    class UserConsumer {

        public ThreadPoolExecutor consumer() {
            ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(DEFAULT_CONSUMER_NUM,
                    DEFAULT_CONSUMER_NUM << 1,
                    60 * 60 * 4,
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(50),
                    new ThreadPoolExecutor.CallerRunsPolicy());
            LOGGER.info("用户数据消费者已启动.");

            for (int i = 0; i < DEFAULT_CONSUMER_NUM; i++) {
                Thread t = new Thread() {
                    int step = 0;
                    int consumerNum = 0;

                    @Override
                    public void run() {
                        while (!this.isInterrupted()) {
                            Long phoneId;
                            try {
                                phoneId = phoneIdQueue.take();
                                if (phoneId.equals(STOP_FLAG)) {
                                    // 该线程取到-1，说明产品队列中已无产品，可以结束线程，但是可能还有其他线程存活，需要通知其他线程已无数据
                                    // 因此，插入-1，同时跳出循环，线程归还线程池，等待监控线程发现其处于非活动状态，将其销毁，最终销毁线程池
                                    phoneIdQueue.put(STOP_FLAG);
                                    LOGGER.info("[{}]线程消费完成.", this.getName());
                                    break;
                                }
                                consumerNum++;
                                step++;
//                                //当产品队列内的数据达到一定数量，等待消费者处理
//                                while (orderInfoQueue.size() >= DEFAULT_QUEUE_DISPOSE_SIZE) {
//                                    Thread.sleep(DEFAULT_SLEEP_TIME);
//                                }
                                if (step == DEFAULT_LOG_INTERVAL) {
                                    step = 0;
                                    LOGGER.info("[{}]线程已消费数据{}条", this.getName(), consumerNum);
                                }

                            } catch (InterruptedException e) {
                                LOGGER.error("从phoneIdQueue中取数据错误.", e);
                            }
                        }

                        LOGGER.info("[{}]线程共消费数据{}条", this.getName(), consumerNum);
                        LOGGER.info("数据消费结束.");
                    }
                };
                t.setName("consumer-" + i);
                poolExecutor.execute(t);
            }
            return poolExecutor;
        }

    }

    /**
     * 用户数据生产者
     */
    class UserProducer extends Thread {
        int i = 0;

        @Override
        public void run() {
            add();
            try {
                // 给每个线程都在队列（FIFO）末尾添加标识
                phoneIdQueue.put(STOP_FLAG);
            } catch (InterruptedException e) {
            }
            LOGGER.info("[{}]线程共生产数据{}条", this.getName(), i);
            LOGGER.info("[{}]线程执行结束.", this.getName());
        }

        private void add() {
            int step = 0;
            for (int j = 0; j < 100000; j++) {
                try {
                    phoneIdQueue.put(Long.parseLong(String.valueOf(j)));
                    step++;
                    while (phoneIdQueue.size() > DEFAULT_QUEUE_DISPOSE_SIZE) {
                        Thread.sleep(DEFAULT_SLEEP_TIME);
                    }
                    if (step == DEFAULT_LOG_INTERVAL) {
                        i += step;
                        step = 0;
                        LOGGER.info("[{}]线程已生产数据{}条", this.getName(), i);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            i += step;
        }

    }


}
