package backend.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Rectangle extends Figure {
    private static final String NAME = "Rectángulo";
    private final Point topLeft, bottomRight;
    public Rectangle(Point topLeft, Point bottomRight, Color lineColor, Color fillColor, double borderSize) {
        super(lineColor, fillColor, borderSize);
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
        return new Rectangle(getTopLeft(), getBottomRight(), getLineColor(), getFillColor(), getBorderSize());
    }
}
