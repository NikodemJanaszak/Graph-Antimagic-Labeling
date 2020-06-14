package Solver;

import java.util.ArrayList;
import java.util.List;

import GraphStructures.*;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;


public class AntymagicLabelingSolverExample {

    public static void main(String[] args) {
        // The model is the main component of Choco Solver
        Model model = new Model("Graph Antymagic Labeling with Choco Solver");



        //Creating new graph
        Graph g = new Graph();

        List<Vertex> vertices_list = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            vertices_list.add(new Vertex(i, model));
        }

        g.setVertices(vertices_list);

        List<Edge> edges_list = new ArrayList<>();

        edges_list.add(new Edge(vertices_list.get(0), vertices_list.get(1), model));
        edges_list.add(new Edge(vertices_list.get(1), vertices_list.get(2), model));
        edges_list.add(new Edge(vertices_list.get(1), vertices_list.get(3), model));
        edges_list.add(new Edge(vertices_list.get(4), vertices_list.get(3), model));

        g.setEdges(edges_list);



        // Get all edges which are connected with selected vertex and parse them int IntVar[]
        for (Vertex v : g.getVertices()) {
            List<IntVar> edges_per_vertex_var_arraylist = new ArrayList<IntVar>();

            for (Edge e : g.getEdges()) {
                if (v.getId() == e.getV1().getId() || v.getId() == e.getV2().getId()) {
                    edges_per_vertex_var_arraylist.add(e.getSolverVar());
                }
            }

            IntVar[] edges_per_vertex_var_array = new IntVar[edges_per_vertex_var_arraylist.size()];
            edges_per_vertex_var_array = edges_per_vertex_var_arraylist.toArray(edges_per_vertex_var_array);

            model.allDifferent(edges_per_vertex_var_array).post();  // Comment this line to make labeling soft (edge values can repeat)
            model.sum(edges_per_vertex_var_array, "=", v.getSolverVar()).post(); // Makes sum constraint, exmp. e1+e2+e3=v1
        }



        //Get IntVar[] of vertices
        IntVar[] vertex_var_array = new IntVar[g.getVertices().size()];
        for (int i = 0; i < vertex_var_array.length; i++) {
            vertex_var_array[i] = g.getVertices().get(i).getSolverVar();
        }

        model.allDifferent(vertex_var_array).post(); // Makes vertex sum unique

        model.getSolver().solve(); // Repeating this function gives next solutions (if they exist)



        //Print solution
        for (Vertex v : g.getVertices()) {
            System.out.println("Vertex: " + v.getSolverVar());
        }

        System.out.print("\n");

        for (Edge e : g.getEdges()) {
            System.out.println("Edge: " + e.getSolverVar());
        }

    }
}