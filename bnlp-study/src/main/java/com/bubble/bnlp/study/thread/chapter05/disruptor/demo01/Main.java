package com.bubble.bnlp.study.thread.chapter05.disruptor.demo01;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 基于Disruptor无锁缓存框架的生产者-消费者实现dmeo
 * (Disruptor的性能要比BlockingQueue至少高一个数量级以上）
 *
 * @author wugang
 * date: 2018-10-30 09:41
 **/
public class Main {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        PCDataFactory factory = new PCDataFactory();
        int bufferSize = 1024 * 1024; // 缓冲区的大小，必须为2的整数次幂。
        Disruptor<PCData> disruptor = new Disruptor<>(factory,
                bufferSize,
                executor,
                ProducerType.MULTI,
                new BlockingWaitStrategy());// 监控缓冲区中信息的默认策略。和使用BlockingQueue类似，使用锁和条件(Condition)进行数据监控和线程的唤醒。
        // 设置四个消费者，系统会把每个消费者映射到一个线程中，下面提供了四个消费者线程。(需小于指定的线程池中线程数量)
        disruptor.handleEventsWithWorkerPool(
                new Consumer(),
                new Consumer(),
                new Consumer(),
                new Consumer());
        // 启动并初始化Disruptor系统
        disruptor.start();

        RingBuffer<PCData> ringBuffer = disruptor.getRingBuffer();
        Producer producer = new Producer(ringBuffer);
        // ByteBuffer.allocate方法创建并分配一个私有的空间来储存指定容量大小的数据元素。
        // ByteBuffer.allocate(8) 创建一个容量为8字节的ByteBuffer，如果发现创建的缓冲区容量太小，那么只能重新创建。
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        for (long i = 0; i < 20; i++) {
            byteBuffer.putLong(0, i);
            producer.pushData(byteBuffer);
            Thread.sleep(100);
            System.out.println(Thread.currentThread().getName() + " add data: " + i);
        }
        System.out.println("job done.");

        disruptor.shutdown(); //关闭 disruptor，方法会堵塞，直至所有的事件都得到处理；
        executor.shutdown(); //关闭 disruptor 使用的线程池；如果需要的话，必须手动关闭，disruptor在shutdown时不会自动关闭；
    }

}
