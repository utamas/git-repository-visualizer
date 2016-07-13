package com.utamas.git.graph;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GraphBuilder {
    private final Map<GraphNode, Set<GraphNode>> edges = new HashMap<>();
    private final Map<String, Set<String>> labels = new HashMap<>();
    private final Map<String, Set<String>> groups = new HashMap<>();

    public GraphBuilder assignNodeToGroup(String node, String group) {

        groups.merge(group, ImmutableSet.of(node), Sets::union);

        return this;
    }

    public GraphBuilder withEdge(GraphNode from, GraphNode to) {

        edges.merge(from, ImmutableSet.of(to), Sets::union);

        return this;
    }

    public GraphBuilder withNodeWithLabels(String node, Set<String> labels) {

        this.labels.merge(node, labels, Sets::union);

        return this;
    }

    public Graph build() {

        Set<GraphNode> graphNodes = new HashSet<>();
        Set<GraphEdge> graphEdges = new HashSet<>();

        edges.keySet().stream().forEach(fromNode -> {
            putIfAbsent(graphNodes, fromNode);

            edges.get(fromNode).stream().forEach(toNode -> {
                putIfAbsent(graphNodes, toNode);
                graphEdges.add(GraphEdge.of(fromNode.getCommitHash(), toNode.getCommitHash()));
            });
        });

        return new Graph(graphNodes, graphEdges, groups);
    }

    private void putIfAbsent(Set<GraphNode> graphNodes, GraphNode fromNode) {
        if (!graphNodes.contains(fromNode)) {
            graphNodes.add(fromNode);
        }
    }

    public static class Graph {
        private ImmutableSet<GraphNode> nodes;
        private ImmutableSet<GraphEdge> edges;
        private ImmutableMap<String, Set<String>> subGroups;

        private Graph(Set<GraphNode> nodes, Set<GraphEdge> edges, Map<String, Set<String>> subGroups) {
            this.nodes = ImmutableSet.copyOf(nodes);
            this.edges = ImmutableSet.copyOf(edges);
            this.subGroups = ImmutableMap.copyOf(subGroups);
        }

        public Set<GraphNode> getNodes() {
            return nodes;
        }

        public Set<GraphEdge> getEdges() {
            return edges;
        }

        public Map<String, Set<String>> getSubGroups() {
            return subGroups;
        }
    }

    public static class GraphNode {
        private final String commitHash;
        private final String authorEmail;

        public static GraphNode of(String commitHash, String authorEmail) {
            return new GraphNode(commitHash, authorEmail);
        }

        public GraphNode(String commitHash, String authorEmail) {
            this.commitHash = commitHash;
            this.authorEmail = authorEmail;
        }

        public String getCommitHash() {
            return commitHash;
        }

        public String getAuthorEmail() {
            return authorEmail;
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                    .append(commitHash)
                    .append(authorEmail)
                    .hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (obj.getClass() != getClass()) {
                return false;
            }

            GraphNode rhs = (GraphNode) obj;

            return new EqualsBuilder()
//                    .appendSuper(super.equals(obj))
                    .append(commitHash, rhs.commitHash)
                    .append(authorEmail, rhs.authorEmail)
                    .isEquals();
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("commit", commitHash)
                    .append("author", authorEmail)
                    .toString();
        }
    }

    public static class GraphEdge {
        private final String from;
        private final String to;

        public GraphEdge(String from, String to) {
            this.from = from;
            this.to = to;
        }

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }

        public static GraphEdge of(String from, String to) {
            return new GraphEdge(from, to);
        }
    }
}
