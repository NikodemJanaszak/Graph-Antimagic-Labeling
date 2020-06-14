package GraphApp.model;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class Cell extends Pane {

    String cellId;

    String solveLabel = "";

    List<Cell> children = new ArrayList<>();
    List<Cell> parents = new ArrayList<>();

    Node view;

    public Cell(String cellId) {
        this.cellId = cellId;
    }

    public void addCellChild(Cell cell) {
        children.add(cell);
    }

    public List<Cell> getCellChildren() {
        return children;
    }

    public void addCellParent(Cell cell) {
        parents.add(cell);
    }

    public List<Cell> getCellParents() {
        return parents;
    }

    public void removeCellChild(Cell cell) {
        children.remove(cell);
    }

    public void setView(Node view) {
        Text text = new Text(cellId);
        text.setFont(Font.font("Verdana", 15));
        Text solved = new Text(solveLabel);
        solved.setFont(Font.font("Verdana", 15));
        StackPane stack = new StackPane();
        stack.getChildren().addAll(view, text, solved);
        stack.setAlignment(solved, Pos.BOTTOM_RIGHT);
        this.view = view;
        getChildren().add(stack);
    }

    public Node getView() {
        return this.view;
    }

    public String getCellId() {
        return cellId;
    }


    public String getSolveLabel() {
        return solveLabel;
    }

    public void setSolveLabel(String solveLabel) {
        this.solveLabel = solveLabel;
    }
}
