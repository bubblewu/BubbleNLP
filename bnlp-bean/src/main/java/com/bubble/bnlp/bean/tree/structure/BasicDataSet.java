package com.bubble.bnlp.bean.tree.structure;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 基本数据集:
 * 继承ArrayList,每一个List中的元素是一条数据
 *
 * @author wugang
 * date: 2018-12-13 15:03
 **/
public class BasicDataSet extends ArrayList<Object[]> {
    private static final long serialVersionUID = 2660880093390311301L;

    public BasicDataSet() {
        super();
    }

    public BasicDataSet(Collection<? extends Object[]> c) {
        super(c);
    }

    public BasicDataSet(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * 根据rowIndexArray定义的序号列表，返回传入数据集的子集，
     *
     * @param dataSet       传入数据集
     * @param rowIndexArray 序号列表
     * @return 数据子集
     */
    public BasicDataSet subDataSet(BasicDataSet dataSet, int[] rowIndexArray) {
        BasicDataSet result = new BasicDataSet(dataSet.size());
        for (int i = 0; i < rowIndexArray.length; i++) {
            result.add(dataSet.get(rowIndexArray[i]));
        }
        return result;
    }

}
