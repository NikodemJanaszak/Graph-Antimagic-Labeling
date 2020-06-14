package GraphStructures;

import java.util.List;

public class Graph {
    private List<Vertex> vertices;
    private List<Edge> edges;

    public Graph() {
    }

    public Graph(List<Vertex> vertices, List<Edge> edges) {
        this.edges = edges;
        this.vertices = vertices;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setVertices(List<Vertex> vertices) {
        this.vertices = vertices;
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public void printGraph() {
        for (Vertex v : vertices) {
            System.out.println(v);
        }

        for (Edge e : edges) {
            System.out.println(e);
        }
    }
}