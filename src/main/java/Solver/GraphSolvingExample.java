package Solver;

import org.chocosolver.graphsolver.GraphModel;
import org.chocosolver.graphsolver.variables.DirectedGraphVar;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.objects.graphs.DirectedGraph;
import org.chocosolver.util.objects.setDataStructures.SetType;

public class GraphSolvingExample {
    public static void main(String[] args) {
        int n = 5;
        GraphModel model = new GraphModel();

        // VARIABLE COUNTING THE NUMBER OF ARCS
        IntVar nbArcs = model.intVar("arcCount", 0, n * n, true);

        // GRAPH VARIABLE : initial domain (every node belongs to the solution)
        DirectedGraph GLB = new DirectedGraph(model, n, SetType.BITSET, true);
        DirectedGraph GUB = new DirectedGraph(model, n, SetType.BITSET, true);
        GLB.addArc(0, 1); // some arbitrary mandatory arcs
        GLB.addArc(1, 2);
        GLB.addArc(3, 1);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                GUB.addArc(i, j); // potential edge
            }
        }

        DirectedGraphVar dag = model.digraphVar("dag", GLB, GUB);

        // CONSTRAINTS
        model.noCircuit(dag).post();
        model.nbArcs(dag, nbArcs).post();

        // SOLVING AND PRINTS
        System.out.println(dag.graphVizExport()); // displays initial graph domain
        try {
            model.getSolver().propagate(); // propagates constraints (without branching)
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
        System.out.println(dag.graphVizExport()); // displays graph domain after propagation
        if (model.getSolver().solve()) {
            System.out.println("solution found : " + nbArcs);
            System.out.println(dag.graphVizExport()); // displays solution graph
        }
        model.getSolver().printStatistics();
    }
}