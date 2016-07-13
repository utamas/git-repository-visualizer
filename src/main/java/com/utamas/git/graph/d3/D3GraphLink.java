package com.utamas.git.graph.d3;

public class D3GraphLink {
    public static D3GraphLink of(String source, String target) {
        D3GraphLink link = new D3GraphLink();
        link.setSource(source);
        link.setTarget(target);
        return link;
    }

    private String source;
    private String target;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
