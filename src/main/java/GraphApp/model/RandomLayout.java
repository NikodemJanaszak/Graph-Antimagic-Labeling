package GraphApp.model;

import java.util.List;
import java.util.Random;


public class RandomLayout extends Layout {

    GraphDraw graph;

    Random rnd = new Random();

    public RandomLayout(GraphDraw graph) {

        this.graph = graph;

    }

    public void execute() {

        List<Cell> cells = graph.getModel().getAllCells();

        for (Cell cell : cells) {

            double x = rnd.nextDouble() * 500;
            double y = rnd.nextDouble() * 500;

            cell.relocate(x, y);

        }

    }

}