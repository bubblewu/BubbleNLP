package com.bubble.bnlp.bean.tree.structure;

import org.apache.commons.beanutils.ConvertUtils;

import java.util.Collection;

/**
 * 定义数据集的类型
 *
 * @author wugang
 * date: 2018-12-13 15:10
 **/
public class TypedDataSet extends BasicDataSet {
    private static final long serialVersionUID = -4951133295480534964L;

    private Class<?>[] typeClasses;

    public TypedDataSet(Class<?>... typeClasses) {
        this.typeClasses = typeClasses;
    }

    public TypedDataSet(int initialCapacity, Class<?>... typeClasses) {
        super(initialCapacity);
        this.typeClasses = typeClasses;
    }

    public TypedDataSet(Collection<? extends Object[]> collection, Class<?>... typeClasses) {
        super(collection.size());
        collection.forEach(this::add);
        this.typeClasses = typeClasses;
    }

    @Override
    public boolean add(Object[] objects) {
        if (null == objects) {
            return false;
        }
        if (objects.length != typeClasses.length) {
            return false;
        }
        Object[] data = new Object[objects.length];

        for (int i = 0; i < typeClasses.length; i++) {
            if (objects[i].getClass() != typeClasses[i]) {
                data[i] = ConvertUtils.convert(objects[i], typeClasses[i]);
            } else {
                data[i] = objects[i];
            }
        }

        return super.add(data);
    }

    public Class<?>[] getTypes() {
        return typeClasses;
    }

    /**
     * 获取第i列数据的类型
     *
     * @param i 数据列的索引号
     * @return 第i列数据的类型
     */
    public Class<?> getType(int i) {
        return typeClasses[i];
    }

}
