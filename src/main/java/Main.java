import GraphApp.GraphUtilities;
import GraphStructures.*;

public class Main {

    public static void main(String[] args) {

        Graph g = GraphUtilities.read_from_file(
                System.getProperty("user.dir") + "/src/main/java/graph_antymagic_labeling/input_graph.txt");
        GraphUtilities.write_to_file(g, "output_graph.txt");

    }
}