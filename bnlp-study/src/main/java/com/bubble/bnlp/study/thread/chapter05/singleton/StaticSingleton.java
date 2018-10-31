package com.bubble.bnlp.study.thread.chapter05.singleton;

import java.util.ArrayList;
import java.util.List;

/**
 * 利用虚拟机的类初始化机制创建单例
 *
 * @author wugang
 * date: 2018-10-30 23:00
 **/
public class StaticSingleton {

    private List<String> list;

    private StaticSingleton() {
        System.out.println("StaticSingleton is create.");
    }

    private static class StaticSingletonHolder {
        private static StaticSingleton instance = new StaticSingleton();

        private StaticSingletonHolder() {
            System.out.println("add value");
            instance.list.add("test-01");
            instance.list.add("test-02");
        }

        // static静态代码块只会执行一次
        static {
            System.out.println("add value");
            List<String> tempList = new ArrayList<>();
            tempList.add("test-01");
//            tempList.add("test-02");

            instance.setList(tempList);
        }

    }

    public static StaticSingleton getInstance() {
        return StaticSingletonHolder.instance;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}
