package com.bubble.bnlp.study.thread.chapter05.disruptor.demo02;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wugang
 * date: 2018-10-30 10:54
 **/
public class Main<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Instant begin = Instant.now();
        ExecutorService executor = Executors.newFixedThreadPool(3);
        CommonFactory dataFactory = new CommonFactory();
        int bufferSize = 1024 * 1024; // 缓冲区的大小，必须为2的整数次幂。
        Disruptor<CommonData> disruptor = new Disruptor<>(dataFactory,
                bufferSize,
                executor,
                ProducerType.MULTI,
                new BlockingWaitStrategy());
        disruptor.handleEventsWithWorkerPool(
                new Con(),
                new Con(),
                new Con());
        disruptor.start();

        RingBuffer<CommonData> ringBuffer = disruptor.getRingBuffer();
        Pro producer = new Pro(ringBuffer);
//        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        for (long i = 0; i < 100; i++) {
            String t = "add-" + i;
            producer.pushData(i, t);
        }

        LOGGER.info("rec job finished. costs {}ms.", Duration.between(begin, Instant.now()).toMillis());

        disruptor.shutdown(); //关闭 disruptor，方法会堵塞，直至所有的事件都得到处理；
        executor.shutdown();
    }

}
