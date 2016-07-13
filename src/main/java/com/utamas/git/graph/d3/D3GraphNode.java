package com.utamas.git.graph.d3;

public class D3GraphNode {
    public static D3GraphNode of(String id, int group) {
        D3GraphNode node = new D3GraphNode();
        node.setId(id);
        node.setGroup(group);
        return node;
    }

    private String id;
    private int group;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }
}
