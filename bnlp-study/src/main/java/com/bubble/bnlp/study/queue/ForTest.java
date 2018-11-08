package com.bubble.bnlp.study.queue;

import org.omg.PortableInterceptor.INACTIVE;

import java.time.Duration;
import java.time.Instant;

/**
 * @author wugang
 * date: 2018-11-05 18:58
 **/
public class ForTest {

    public static void main(String[] args) {

        for (int i = 0; i < 10; i++) {

            if (i > 5) {
                System.out.println(i);
            }
            Instant begin = Instant.now();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("end: " + Duration.between(begin, Instant.now()).toMillis());


        }

    }


}
