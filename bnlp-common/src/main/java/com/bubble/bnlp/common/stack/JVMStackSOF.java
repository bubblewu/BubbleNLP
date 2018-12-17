package com.bubble.bnlp.common.stack;

/**
 * Java栈溢出StackOverFlowError
 * JVM参数：-Xss160k
 *
 * @author wugang
 * date: 2018-11-23 15:03
 **/
public class JVMStackSOF {
    private int stackLength = -1;

    /**
     * 设置单个线程的虚拟机栈内存大小为160K，执行main方法后，抛出了StackOverflow异常
     */
    public static void main(String[] args) {
        JVMStackSOF oom = new JVMStackSOF();
        try {
            oom.stackLeak();
        } catch (Throwable e) {
            System.out.println("Stack length:" + oom.stackLength);
            e.printStackTrace();
        }
    }

    /**
     * 通过递归调用造成StackOverFlowError
     */
    private void stackLeak() {
        stackLength++;
        stackLeak();
    }

}
