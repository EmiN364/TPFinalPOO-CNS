package backend.model;

import javafx.scene.canvas.GraphicsContext;

public class Rectangle extends Figure {
    private static final String NAME = "Rectángulo";
    private final Point topLeft, bottomRight;
    public Rectangle(Point topLeft, Point bottomRight, FigureStyle figureStyle) {
        super(figureStyle);
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
    }
    @Override
    public String getName() {
        return NAME;
    }
    public Point getTopLeft() {
        return topLeft;
    }

    public Point getBottomRight() {
        return bottomRight;
    }

    @Override
    public String toString() {
        return String.format("Rectángulo [ %s , %s ]", topLeft, bottomRight);
    }

    @Override
    public void move(double diffX, double diffY) {
        getTopLeft().moveX(diffX);
        getBottomRight().moveX(diffX);
        getTopLeft().moveY(diffY);
        getBottomRight().moveY(diffY);
    }

    @Override
    public boolean belongs(Point eventPoint) {
        return eventPoint.getX() > getTopLeft().getX() && eventPoint.getX() < getBottomRight().getX() &&
                eventPoint.getY() > getTopLeft().getY() && eventPoint.getY() < getBottomRight().getY();
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.fillRect(getTopLeft().getX(), getTopLeft().getY(),
                Math.abs(getTopLeft().getX() - getBottomRight().getX()), Math.abs(getTopLeft().getY() - getBottomRight().getY()));
        gc.strokeRect(getTopLeft().getX(), getTopLeft().getY(),
                Math.abs(getTopLeft().getX() - getBottomRight().getX()), Math.abs(getTopLeft().getY() - getBottomRight().getY()));
    }

    @Override
    public Figure clone() {
        return new Rectangle(getTopLeft(), getBottomRight(), getFigureStyleCopy());
    }

    @Override
    public Figure getCenteredCopy(double widthC, double heightC) {
        double height = getBottomRight().getX() - getTopLeft().getX();
        double width = getBottomRight().getY() - getTopLeft().getY();
        Point topLeft = new Point(widthC - height/2, heightC - width/2);
        Point bottomRight = new Point(widthC + height/2, heightC + width/2);
        return new Rectangle(topLeft, bottomRight, getFigureStyleCopy());
    }
}
