package com.utamas.git.graph.d3;

import java.util.ArrayList;
import java.util.List;

public class D3Graph {
    private List<D3GraphNode> nodes = new ArrayList<>();
    private List<D3GraphLink> links = new ArrayList<>();

    public List<D3GraphNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<D3GraphNode> nodes) {
        this.nodes = nodes;
    }

    public List<D3GraphLink> getLinks() {
        return links;
    }

    public void setLinks(List<D3GraphLink> links) {
        this.links = links;
    }
}
