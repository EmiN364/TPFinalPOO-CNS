package backend.model;

import javafx.scene.paint.Color;

public class Square extends Rectangle {
    private static final String NAME = "Cuadrado";

    public Square(Point topLeft, double size, Color lineColor, Color fillColor, double borderSize) {
        super(topLeft, new Point(topLeft.getX() + size, topLeft.getY() + size), lineColor, fillColor, borderSize);
    }
    @Override
    public String getName() {
        return NAME;
    }
    @Override
    public String toString() {
        return String.format("Cuadrado [ %s , %s ]", getTopLeft(), getBottomRight());
    }

}
