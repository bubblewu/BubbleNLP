package com.bubble.bnlp.bean.exception;

/**
 * 决策树相关异常
 *
 * @author wugang
 * date: 2018-11-08 14:54
 **/
public class DecisionTreeException extends RuntimeException {
    private static final long serialVersionUID = 6716336649902027690L;

    public DecisionTreeException() {
        super();
    }

    public DecisionTreeException(String message) {
        super(message);
    }

    public DecisionTreeException(String message, Throwable t) {
        super(message, t);
    }

}
