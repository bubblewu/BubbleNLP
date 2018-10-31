package com.bubble.bnlp.study.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步调用demo
 *
 * @author wugang
 * date: 2018-10-31 17:20
 **/
public class AsyThread {

    public static void main(String[] args) {
        MyExecutor executor = new MyExecutor();
        System.out.println("领导来分发今天的任务:");
        executor.doSomething();
        System.out.println("领导走了，小兵们正在加班加点ing");
    }

    private static class MyExecutor {
        private ExecutorService executor = Executors.newCachedThreadPool();

        /**
         * 异步执行的任务不需要继续等待，主线程的任务不受影响会继续执行。
         */
        public void doSomething() {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 10; i++) {
                        try {
                            Thread.sleep(100);
                            System.out.println("小兵 " + i +" 正在埋头干活...");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("小兵们已干完，泪奔下班.");

                }
            });
        }

    }

}
