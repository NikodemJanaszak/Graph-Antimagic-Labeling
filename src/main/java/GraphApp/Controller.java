package GraphApp;

import GraphApp.model.*;
import GraphStructures.Edge;
import GraphStructures.Graph;
import GraphStructures.Vertex;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.chocosolver.solver.variables.IntVar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    @FXML
    private TextField randomNodes = new TextField();
    @FXML
    private TextField randomDegree = new TextField();
    @FXML
    private Pane drawingPane = new Pane();
    @FXML
    private Canvas drawingCanvas = new Canvas();
    @FXML
    private TextField edgeStart = new TextField();
    @FXML
    private TextField edgeEnd = new TextField();

    int cellsId = 0;
    protected GraphDraw graphDraw = new GraphDraw();

    @FXML
    public void initialize() {

    }

    public void showGraphCanvas(ActionEvent actionEvent) throws IOException {
        cellsId = 0;
        Stage graphStage = new Stage();
        BorderPane root = new BorderPane();
        graphDraw = new GraphDraw();
        root.setCenter(graphDraw.getScrollPane());
        Scene scene = new Scene(root, 1024, 768);
        graphStage.setScene(scene);
        graphStage.show();
    }

    public void addEdge() {
        Model model = graphDraw.getModel();
        graphDraw.beginUpdate();
        model.addEdge(edgeStart.getText(), edgeEnd.getText());
        graphDraw.endUpdate();
    }

    public void addCell() {
        Model model = graphDraw.getModel();
        graphDraw.beginUpdate();
        model.addCell("V" + cellsId, CellType.RECTANGLE);
        cellsId++;
        graphDraw.endUpdate();
    }

    @FXML
    private void loadFromFile() {
        Stage thirdStage = new Stage();
        thirdStage.setTitle("Load graph");
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(thirdStage);
        Graph graph = GraphUtilities.read_from_file(file.getAbsolutePath());
        Stage graphStage = new Stage();
        BorderPane root = new BorderPane();
        graphDraw = mapGraphToGraphDraw(graph);
        root.setCenter(graphDraw.getScrollPane());
        Scene scene = new Scene(root, 1024, 768);
        graphStage.setScene(scene);
        graphStage.show();
        Layout layout = new RandomLayout(graphDraw);
        layout.execute();
    }

    @FXML
    private void saveToFile() {
        Graph graph = mapGraphDrawToGraph(graphDraw);
        Stage thirdStage = new Stage();
        thirdStage.setTitle("Save graph");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("output_graph.txt");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(thirdStage);
        GraphUtilities.write_to_file(graph, file.getAbsolutePath());
    }

    private Graph mapGraphDrawToGraph(GraphDraw toMapGraph) {
        Graph graph = new Graph();
        int i = 0;
        List<Vertex> vertexListToAdd = new ArrayList<>();
        for (Cell c : toMapGraph.getModel().getAllCells()) {
            Vertex vertex = new Vertex(i, c.getCellId());
            vertexListToAdd.add(vertex);
            i++;
        }
        graph.setVertices(vertexListToAdd);

        List<Edge> edgesListToAdd = new ArrayList<>();
        for (GraphApp.model.Edge e : toMapGraph.getModel().getAllEdges()) {
            List<Vertex> vertexList = graph.getVertices();

            Vertex sourceVertex = new Vertex(0, "");
            Vertex targetVertex = new Vertex(0, "");

            for (Vertex v : vertexList) {
                if (e.getSource().getCellId().equals(v.getName())) {
                    sourceVertex = new Vertex(v.getId(), v.getName());
                }
                if (e.getTarget().getCellId().equals(v.getName())) {
                    targetVertex = new Vertex(v.getId(), v.getName());
                }
            }
            Edge edge = new Edge(sourceVertex, targetVertex);
            edgesListToAdd.add(edge);
        }
        graph.setEdges(edgesListToAdd);
        graph.getEdges().toString();
        graph.getVertices().toString();
        return graph;
    }

    private GraphDraw mapGraphToGraphDraw(Graph graph) {
        List<Vertex> vertexList = graph.getVertices();
        List<Edge> edgeList = graph.getEdges();
        GraphDraw mappedGraph = new GraphDraw();
        Model model = mappedGraph.getModel();
        mappedGraph.beginUpdate();
        for (Vertex v : vertexList) {
            model.addCell(v.getName(), CellType.RECTANGLE);
        }
        for (Edge e : edgeList) {
            model.addEdge(e.getV1().getName(), e.getV2().getName());
        }
        mappedGraph.endUpdate();
        return mappedGraph;
    }

    @FXML
    private void startLabeling() {

        // The model is the main component of Choco Solver
        org.chocosolver.solver.Model modelSolver = new org.chocosolver.solver.Model("Graph Antymagic Labeling with Choco Solver");

        //Creating new graph
        Graph g = mapGraphDrawToGraph(graphDraw);

        // Get all edges which are connected with selected vertex and parse them int IntVar[]
        for (Vertex v : g.getVertices()) {
            v.setInitSolver_var(modelSolver);
            List<IntVar> edges_per_vertex_var_arraylist = new ArrayList<IntVar>();

            for (Edge e : g.getEdges()) {
                e.setInitSolver_var(modelSolver);
                if (v.getId() == e.getV1().getId() || v.getId() == e.getV2().getId()) {
                    edges_per_vertex_var_arraylist.add(e.getSolverVar());
                }
            }

            IntVar[] edges_per_vertex_var_array = new IntVar[edges_per_vertex_var_arraylist.size()];
            edges_per_vertex_var_array = edges_per_vertex_var_arraylist.toArray(edges_per_vertex_var_array);

            modelSolver.allDifferent(edges_per_vertex_var_array).post();  // Comment this line to make labeling soft (edge values can repeat)
            modelSolver.sum(edges_per_vertex_var_array, "=", v.getSolverVar()).post(); // Makes sum constraint, exmp. e1+e2+e3=v1
        }

        //Get IntVar[] of vertices
        IntVar[] vertex_var_array = new IntVar[g.getVertices().size()];
        for (int i = 0; i < vertex_var_array.length; i++) {
            vertex_var_array[i] = g.getVertices().get(i).getSolverVar();
        }

        modelSolver.allDifferent(vertex_var_array).post(); // Makes vertex sum unique

        modelSolver.getSolver().solve(); // Repeating this function gives next solutions (if they exist)


        //Print solution
        for (Vertex v : g.getVertices()) {
            System.out.println("Vertex: " + v.getSolverVar());

            for (Cell c : graphDraw.getModel().getAllCells()) {
                if (c.getCellId().equals(v.getName())) {
                    c.setSolveLabel(Integer.toString(v.getSolverVar().getValue()));
                    c.setView(c.getView());
                }
            }
        }

        System.out.print("\n");

        for (Edge e : g.getEdges()) {
            System.out.println("Edge: " + e.getSolverVar());
            Cell sourceCell = new Cell("");
            Cell targetCell = new Cell("");

            for (Cell c : graphDraw.getModel().getAllCells()) {
                if (e.getV1().getName().equals(c.getCellId())) {
                    sourceCell = new Cell(c.getCellId());
                }
                if (e.getV2().getName().equals(c.getCellId())) {
                    targetCell = new Cell(c.getCellId());
                }
            }

            for (GraphApp.model.Edge drawEdge : graphDraw.getModel().getAllEdges()) {
                if ((drawEdge.getSource().getCellId().equals(e.getV1().getName()) && drawEdge.getTarget().getCellId().equals(e.getV2().getName())) ||
                        (drawEdge.getTarget().getCellId().equals(e.getV1().getName()) && drawEdge.getSource().getCellId().equals(e.getV2().getName()))) {
                    drawEdge.setText(Integer.toString(e.getSolverVar().getValue()));
                    drawEdge.relocateText();
                }
            }

        }
    }
}
