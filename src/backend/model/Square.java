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

    private double getSize() {
        return getBottomRight().getX() - getTopLeft().getX();
    }

    @Override
    public Figure clone() {
        return new Square(getTopLeft(), getSize(), getFigureStyleCopy());
    }

    @Override
    public Figure getCenteredCopy(double widthC, double heightC) {
        double height = getBottomRight().getX() - getTopLeft().getX();
        double width = getBottomRight().getY() - getTopLeft().getY();
        Point topLeft = new Point(widthC - height/2, heightC - width/2);
        return new Square(topLeft, getSize(), getFigureStyleCopy());
    }
}
