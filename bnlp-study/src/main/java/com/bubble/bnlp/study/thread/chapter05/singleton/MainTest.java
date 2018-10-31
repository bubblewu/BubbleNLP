package com.bubble.bnlp.study.thread.chapter05.singleton;

/**
 * @author wugang
 * date: 2018-10-30 23:04
 **/
public class MainTest implements Runnable{

    public static void main(String[] args) {
        MainTest mt = new MainTest();
        for (int i = 0; i < 10; i++) {
            new Thread(mt, "Thread-" + i).start();
        }

    }

    @Override
    public void run() {
        StaticSingleton.getInstance().getList().forEach(l -> {
            System.out.println(Thread.currentThread().getName() + " " + l);
        });
    }

}
