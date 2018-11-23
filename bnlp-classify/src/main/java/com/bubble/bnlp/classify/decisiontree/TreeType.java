package com.bubble.bnlp.classify.decisiontree;

/**
 * 决策树类型
 */
public enum TreeType {
    ID3("id3"),
    C45("c4.5"),
    CART("cart");

    private String type;

    private TreeType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
