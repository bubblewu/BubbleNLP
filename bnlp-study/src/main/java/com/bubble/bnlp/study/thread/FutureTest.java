package com.bubble.bnlp.study.thread;

import java.util.concurrent.*;

/**
 * @author wugang
 * date: 2018-11-02 09:19
 **/
public class FutureTest {
    /**
     * Runnable任务不返回任何值，如果你希望在任务完成时能够返回一个值，那么可以实现Callable接口而不是Runnable接口，
     * Callable是一种具有类型参数的泛型，它的类型参数表示的是从方法call()中返回的值，并且必须使用ExecutorService.submit()方法调用它。
     * submit()方法会产生Future对象，你可以用isDone()来查询Future是否已经完成，
     * 任务完成时，可以用get()方法获取任务的返回值，如果任务没有完成，调用get()方法会阻塞主线程。
     */

    public static void main(String[] args) {
        ExecutorService service = Executors.newCachedThreadPool();
        TaskWithResult task = new TaskWithResult();
        Future<String> future = service.submit(task);
        try {
            System.out.println("后台取值ing");
            System.out.println(future.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println("主线程结束");

    }

    private static class TaskWithResult implements Callable<String> {
        @Override
        public String call() {
            return "result";
        }
    }

}
