package com.bubble.bnlp.study.queue;

import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 任务队列来实时接收消息
 *
 * @author wugang
 * date: 2018-10-31 17:06
 **/
public class QueueTest {

    //定义一个队列来存储数据
    private static Queue<String> queue = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        new Input().start();
        new Output().start();
    }


    /**
     * 接收控制台输入
     */
    static class Input extends Thread {
        @Override
        public void run() {
            Scanner sc = new Scanner(System.in);
            System.out.println("请输入字符串：");

            while (true) {
                String name = sc.nextLine();
                // 如果立即可行且不违反容量限制，
                // 则将指定的元素插入此双端队列表示的队列中（即此双端队列的尾部），
                // 并在成功时返回 true；如果当前没有空间可用，则返回 false
                queue.offer(name);
                if ("exit".equals(name))
                    break;
                synchronized (queue) { //notify()是Object()中定义的方法所以只能用在synchronized()方法中。
                    queue.notify(); //唤醒在负责输出线程中的等待的告诉队列中有元素了它可以输出了
                }
            }
        }
    }

    /**
     * 实时输出输入的数据
     */
    static class Output extends Thread {
        @Override
        public void run() {
            while (true) {
                if (queue.size() > 0) {
                    //System.out.println(queue.size());
                    String name = queue.poll();
                    System.out.println(name);
                } else {
                    synchronized (queue) {
                        try {
                            queue.wait();//相当于queue.wait(0),队列中没有东西则默认无限等待直到队列中有东西并且通知他
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }//如果队列中没有东西则等待
                    }
                }

            }

        }
    }

}
