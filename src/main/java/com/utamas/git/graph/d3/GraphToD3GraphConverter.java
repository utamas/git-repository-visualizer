package com.utamas.git.graph.d3;

import com.utamas.git.graph.GraphBuilder.Graph;

import java.util.List;

public class GraphToD3GraphConverter {
    public D3Graph convert(Graph graph) {
        D3Graph d3Graph = new D3Graph();

        addNodes(graph, d3Graph);
        addLinks(graph, d3Graph);

        return d3Graph;
    }

    private void addLinks(Graph graph, D3Graph d3Graph) {
        List<D3GraphLink> links = d3Graph.getLinks();
        graph.getEdges().stream().forEach(edge -> {
            links.add(D3GraphLink.of(edge.getFrom(), edge.getTo()));
        });
    }

    private void addNodes(Graph graph, D3Graph d3Graph) {
        List<D3GraphNode> nodes = d3Graph.getNodes();
        graph.getNodes().stream().forEach(graphNode -> nodes.add(D3GraphNode.of(graphNode.getCommitHash(), 1)));
    }
}
