package backend.model;

public class Square extends Rectangle {
    private static final String NAME = "Cuadrado";

    public Square(Point topLeft, double size, FigureStyle figureStyle) {
        super(topLeft, new Point(topLeft.getX() + size, topLeft.getY() + size), figureStyle);
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
