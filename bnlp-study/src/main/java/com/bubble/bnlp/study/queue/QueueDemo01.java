package com.bubble.bnlp.study.queue;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * 开启两个线程，一个模拟数据提交；另一个模拟数据读取（取出来插入数据库）
 *
 * @author wugang
 * date: 2018-10-31 17:39
 **/
public class QueueDemo01 {

    public static void main(String[] args) {
        Queue<String> queue = new LinkedList<>();
        Input input = new Input(queue);// input线程 模拟随机添加元素
        Parse parse = new Parse(queue);// parse线程 模拟每隔1秒每次提交10个元素

        input.setName("input线程");
        parse.setName("parse线程");

        input.start();
        parse.start();
    }

    private static class Input extends Thread {
        Queue<String> queue;
        private int m = 1;
        private int totalCount = 0;

        public Input(Queue<String> queue) {
            this.queue = queue;
        }

        @Override
        public synchronized void run() {
            for (int i = 0; i < 10; i++) {
                Random random = new Random();
                int len = random.nextInt(10);// 每次随机生产10个以内的元素加入队列
                for (int j = 0; j < len; j++) {
                    // 往队列尾部插入元素，不同的时候，当超出队列界限的时候，add（）方法是抛出异常让你处理，而offer（）方法是直接返回false
                    queue.offer("element-" + (m++));
                }
                totalCount += len; // 生产的元素总数
                System.out.println("当前运行的线程是：" + Thread.currentThread().getName() + "\t 第" + (i + 1)+ "次新加入元素数：" + len + "\t 队列中元素总数是："
                        + queue.size());

                try {
                    Thread.sleep(random.nextInt(1000));// 随机休眠1秒之内的时间
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

//            queue.offer("end"); // 添加结束标识
            System.out.println("总共生产了 " + totalCount + " 条数据");

        }

    }


    private static class Parse extends Thread {
        private Queue<String> queue;
        private int j = 1;

        public Parse(Queue<String> queue) {
            this.queue = queue;
        }

        @Override
        public synchronized void run() {
            String str;
            while (!queue.isEmpty()) {

//                String head = queue.peek(); // 从头部取数据
//                if ("end".equals(head)) {
//                    System.out.println("数据已经处理完成。");
//                    break;
//                }

                int k = 10; // 每次提交数设置为10，可以根据需要修改
                // remove() 和 poll() 方法都是从队列中删除第一个元素。如果队列元素为空，调用remove() 的行为与 Collection 接口的版本相似会抛出异常，
                // 但是新的 poll() 方法在用空集合调用时只是返回 null。因此新的方法更适合容易出现异常条件的情况。
                while ((str = queue.poll()) != null) {
                    --k;
                    System.out.println(k + "\t" + str);
                    if (k == 0) {
                        break;
                    }
                }
                System.out.println("当前运行的线程是：" + Thread.currentThread().getName() + "\t 第" + j + "次提交！队列还剩元素数："
                        + queue.size());

                j++;

                try {
                    Thread.sleep(1000);// 每次提交以后休眠1秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }

    }


}
