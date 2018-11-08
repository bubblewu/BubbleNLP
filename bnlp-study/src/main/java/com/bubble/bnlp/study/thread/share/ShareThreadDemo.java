package com.bubble.bnlp.study.thread.share;

/**
 * 多线程下数据共享与不共享的Demo：
 * 自定义线程类中的实例变量针对其他线程有共享和不共享两种。
 *
 * @author wugang
 * date: 2018-11-02 17:57
 **/
public class ShareThreadDemo {

    /**
     * # 线程安全和非线程安全：
     * （非线程安全：指多个线程对同一个对象中的实例变量进行操作时，会出现值被更改或值不同步的情况，影响程序的执行。也就是所谓的"脏读"）
     * （线程安全：指在相同的情况下，获得的实例变量时经过同步处理的，不会出现脏读的情况。）
     * 非线程安全问题存在于实例变量中，如果是方法内部的私有变量，则不存在线程不安全问题。
     * <p>
     * # 原因：3个线程同时对count进行处理，产生了非线程安全的问题。
     * 在某些Jvm中，i--的操作分为3个步骤：
     * - 取得原有的i值；
     * - 计算i-1；
     * - 对i进行赋值；
     * 在上面3个步骤中，如果有多个线程同时访问，就一定会出现线程不安全问题。
     * <p>
     * # 解决：可以在run方法前加上synchronized关键字，使多个线程在执行run方法对时候进行排队。
     * 原理：当一个线程调用run前，先判断run有没有上锁，如有锁则等其他线程调用结束之后才可以执行run方法。
     * 这样不断对去尝试获得控制权，所以会比较消耗性能。
     * <p>
     * synchronized关键字可以在任意对象和方法上加锁，加锁对这块代码称为"互斥区"或"临界区"。
     * （互斥：指同一时刻只能有一个线程访问某资源）
     */

    public static void main(String[] args) {
        /* 数据不共享：一个线程操作一个类，多线程下count值正常。*/
//        DataThread unShareThread = new DataThread();
//        unShareThread.setName("unShareThread-01");
//        unShareThread.start();
//
//        DataThread unShareThread2 = new DataThread();
//        unShareThread2.setName("unShareThread-02");
//        unShareThread2.start();
//
//        DataThread unShareThread3 = new DataThread();
//        unShareThread3.setName("unShareThread-03");
//        unShareThread3.start();


        /* 数据共享：一个类被多个线程同时操作，多线程下count值错误*/
        DataThread shareThread = new DataThread();
        Thread thread1 = new Thread(shareThread, "shareThread-01");
        Thread thread2 = new Thread(shareThread, "shareThread-02");
        Thread thread3 = new Thread(shareThread, "shareThread-03");
        thread1.start();
        thread2.start();
        thread3.start();
    }

    private static class DataThread extends Thread {
        private int count = 10;

        @Override
        public void run() {
            super.run();
            while (count > 0) {
                count--;
                System.out.println(Thread.currentThread().getName() + ", count = " + count);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    }


}
