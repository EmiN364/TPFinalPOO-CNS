package backend.model;

public class Square extends Rectangle {
    private static final String NAME="Cuadrado";

    public String getName(){return NAME;}
    public Square(Point topLeft, double size) {
        super(topLeft, new Point(topLeft.getX() + size, topLeft.getY() + size));
    }
    @Override
    public String toString() {
        return String.format("Cuadrado [ %s , %s ]", getTopLeft(), getBottomRight());
    }

}
