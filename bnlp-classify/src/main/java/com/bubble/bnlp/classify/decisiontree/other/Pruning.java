package com.bubble.bnlp.classify.decisiontree.other;

import com.bubble.bnlp.bean.tree.Tree;
import com.bubble.bnlp.bean.tree.structure.Decision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 剪枝
 *
 * @author wugang
 * date: 2018-12-14 13:56
 **/
public class Pruning {
    private static final Logger LOGGER = LoggerFactory.getLogger(Pruning.class);
    // 置信区间阈值CI，C4.5算法中默认的CI值为0.25
    private static final double ci = 0.25;

    /**
     * 剪枝：基于理想置信区间(Confidence Intervals, CI)的剪枝方法
     *
     * @param tree 决策树
     */
    public static void pruning(Tree<Decision> tree) {
        if (tree.isLeaf()) {
            return;
        }
        double treeErrorCount = 0;
        int total = tree.getData().getClassCountMap().values().stream().mapToInt(c -> c).sum();

        for (Tree<Decision> child : tree.getChildren()) {
            if (child.isLeaf()) {
                treeErrorCount += errorCount(child.getData().getClassCountMap()) + 0.5;
            }
        }
        double leafErrorCount = errorCount(tree.getData().getClassCountMap()) + 0.5;
        if (leafErrorCount / total < wilsonScoreInterval(treeErrorCount / total, total)) {
            tree.setChildren(null);
            LOGGER.info("pruning subtrees in {} ", tree);
        }
    }

    /**
     * Wilson score interval函数，参考http://blog.csdn.net/x454045816/article/details/44726921
     *
     * @param e 错误率
     * @param N 数据集大小
     * @return 期望误差的上界e_max
     */
    private static double wilsonScoreInterval(double e, int N) {
        return (e + ci * ci / 2 / N + ci * Math.sqrt(e / N - e * e / N + ci * ci / 4 / N / N)) / (1 + ci * ci / N);
    }

    /**
     * 计算分类错误的个数
     *
     * @param classCountMap 分类和数目映射表
     * @return 分类错误的个数
     */
    private static int errorCount(Map<String, Integer> classCountMap) {
        int total = 0;
        int max = Integer.MIN_VALUE;
        for (Integer i : classCountMap.values()) {
            total += i;
            if (i > max) {
                max = i;
            }
        }
        return total - max;
    }


}
