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
import Solver.AntymagicLabelingSolver;

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
        graph = AntymagicLabelingSolver.solve_graph(graph, true);
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
            vertexListToAdd.add(new Vertex(i, c.getCellId()));
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
            edgesListToAdd.add(new Edge(sourceVertex, targetVertex));
        }
        graph.setEdges(edgesListToAdd);
        System.out.println(graph.getEdges().toString());
        System.out.println(graph.getVertices().toString());
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

    private void labeling(Graph g, boolean hard_labeling) {
        g = AntymagicLabelingSolver.solve_graph(g, hard_labeling);

        for (Vertex v : g.getVertices()) {
            System.out.println("Vertex: " + v.getSolverVar());

            for (Cell c : graphDraw.getModel().getAllCells()) {
                if (c.getCellId().equals(v.getName())) {
                    c.setSolveLabel(Integer.toString(v.getSolverVar().getValue()));
                    c.setView(c.getView());
                }
            }
        }

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

    @FXML
    private void hardSolving() {
        Graph g = mapGraphDrawToGraph(graphDraw);
        labeling(g, true);
    }

    @FXML
    private void softSolving() {
        Graph g = mapGraphDrawToGraph(graphDraw);
        labeling(g, false);
    }
}

