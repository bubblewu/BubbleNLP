package com.bubble.bnlp.common.stack;

/**
 * Java栈溢出OutOfMemoryError
 * JVM参数：-Xss2m
 *
 * @author wugang
 * date: 2018-11-23 15:09
 **/
public class JVMStackOOM {

    private void notStop() {
        while (true) {
        }
    }

    /**
     * 通过不断的创建新的线程使整个虚拟机栈内存耗尽
     */
    private void stackLeakByThread() {
        while (true) {
            Thread thread = new Thread(() -> notStop());
            thread.start();
        }
    }

    /**
     * 设置单个线程虚拟机栈的占用内存为2m并不断生成新的线程，最终虚拟机栈无法申请到新的内存，抛出异常：
     */
    public static void main(String[] args) {
        JVMStackOOM oom = new JVMStackOOM();
        oom.stackLeakByThread();
    }

}
