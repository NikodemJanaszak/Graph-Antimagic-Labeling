package Solver;

import java.util.ArrayList;
import java.util.List;

import GraphStructures.*;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;


public class AntymagicLabelingSolver {

    public static Graph solve_graph(Graph g, boolean hard_solving) {
        return solve_graph(g, hard_solving, 0, 1000, 0, 100);
    }

    public static Graph solve_graph(Graph g, boolean hard_solving, int vertexMinValue, int vertexMaxValue, int edgeMinValue,
                                    int edgeMaxValue) {

        Model model = new Model("Graph Antymagic Labeling with Choco Solver");

        for (Vertex v : g.getVertices()) {
            v.setSolverVar(model.intVar(String.valueOf(v.getId()), vertexMinValue, vertexMaxValue));
        }

        for (Edge e : g.getEdges()) {
//            if (!(g.getVertices().contains(e.getV1()) || g.getEdges().contains(e.getV2()))) {
//                throw new IllegalArgumentException("One of vertices does not belong to the graph!");
//            } else if (e.getV1() == e.getV2()) {
//                throw new IllegalArgumentException("Both vertices: \n" + e.getV1().toString() + " and \n" +
//                        e.getV2().toString() + " are the same!");
//            }
            e.setSolverVar(model.intVar(e.getV1().getId() + "_" + e.getV2().getId(), edgeMinValue, edgeMaxValue));
        }

        for (Vertex v : g.getVertices()) {
            List<IntVar> edges_per_vertex_var_arraylist = new ArrayList<IntVar>();

            for (Edge e : g.getEdges()) {
                if (v.getId() == e.getV1().getId() || v.getId() == e.getV2().getId()) {
                    edges_per_vertex_var_arraylist.add(e.getSolverVar());
                }
            }

            IntVar[] edges_per_vertex_var_array = new IntVar[edges_per_vertex_var_arraylist.size()];
            edges_per_vertex_var_array = edges_per_vertex_var_arraylist.toArray(edges_per_vertex_var_array);

            if (hard_solving) {
                model.allDifferent(edges_per_vertex_var_array).post();
            }

            model.sum(edges_per_vertex_var_array, "=", v.getSolverVar()).post();
        }

        // Get IntVar[] of vertices
        IntVar[] vertex_var_array = new IntVar[g.getVertices().size()];
        for (int i = 0; i < vertex_var_array.length; i++) {
            vertex_var_array[i] = g.getVertices().get(i).getSolverVar();
        }

        model.allDifferent(vertex_var_array).post(); // Makes vertex sum unique

        model.getSolver().solve(); // Repeating this function gives next solutions (if they exist)
        return g;
    }

}
