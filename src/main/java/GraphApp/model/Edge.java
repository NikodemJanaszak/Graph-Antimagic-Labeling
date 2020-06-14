package GraphApp.model;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Edge extends Group {

    protected Cell source;
    protected Cell target;

    Line line;
    Text text;

    public Edge(Cell source, Cell target) {

        this.source = source;
        this.target = target;

        source.addCellChild(target);
        target.addCellParent(source);

        line = new Line();

        line.startXProperty().bind(source.layoutXProperty().add(source.getBoundsInParent().getWidth() / 2.0));
        line.startYProperty().bind(source.layoutYProperty().add(source.getBoundsInParent().getHeight() / 2.0));

        line.endXProperty().bind(target.layoutXProperty().add(target.getBoundsInParent().getWidth() / 2.0));
        line.endYProperty().bind(target.layoutYProperty().add(target.getBoundsInParent().getHeight() / 2.0));

        line.setStroke(Color.GRAY);
        line.setStrokeWidth(5);

        text = new Text();
        text.setFont(Font.font("Verdana", 20));
        relocateText();

        getChildren().addAll(line, text);
    }

    public Cell getSource() {
        return source;
    }

    public Cell getTarget() {
        return target;
    }

    public void setText(String str) {
        this.text.setText(str);
    }

    public void relocateText() {
        double positionX = (line.getStartX() + line.getEndX()) / 2.0;
        double positionY = (line.getStartY() + line.getEndY()) / 2.0;
        text.relocate(positionX, positionY);
    }
}
