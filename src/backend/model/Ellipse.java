package backend.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Ellipse extends Figure {
    private static final String NAME = "Elipse";
    private final Point centerPoint;
    private final double sMayorAxis, sMinorAxis;

    public Ellipse(Point centerPoint, double sMayorAxis, double sMinorAxis, Color lineColor, Color fillColor, double borderSize) {
        super(lineColor, fillColor, borderSize);
        this.centerPoint = centerPoint;
        this.sMayorAxis = sMayorAxis;
        this.sMinorAxis = sMinorAxis;
    }
    @Override
    public String getName() {
        return NAME;
    }
    @Override
    public String toString() {
        return String.format("Elipse [Centro: %s, DMayor: %.2f, DMenor: %.2f]", centerPoint, sMayorAxis, sMinorAxis);
    }

    public Point getCenterPoint() {
        return centerPoint;
    }

    public double getsMayorAxis() {
        return sMayorAxis;
    }

    public double getsMinorAxis() {
        return sMinorAxis;
    }

    @Override
    public void move(double diffX, double diffY) {
        getCenterPoint().moveX(diffX);
        getCenterPoint().moveY(diffY);
    }

    @Override
    public boolean belongs(Point eventPoint) {
        // Nota: Fórmula aproximada. No es necesario corregirla.
        return ((Math.pow(eventPoint.getX() - getCenterPoint().getX(), 2) / Math.pow(getsMayorAxis(), 2)) +
                (Math.pow(eventPoint.getY() - getCenterPoint().getY(), 2) / Math.pow(getsMinorAxis(), 2))) <= 0.30;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.strokeOval(getCenterPoint().getX() - (getsMayorAxis() / 2), getCenterPoint().getY() - (getsMinorAxis() / 2), getsMayorAxis(), getsMinorAxis());
        gc.fillOval(getCenterPoint().getX() - (getsMayorAxis() / 2), getCenterPoint().getY() - (getsMinorAxis() / 2), getsMayorAxis(), getsMinorAxis());
    }

    @Override
    public Figure clone() {
        return new Ellipse(getCenterPoint(), getsMayorAxis(), getsMinorAxis(), getLineColor(), getFillColor(), getBorderSize());
    }

    @Override
    public Figure getCenteredCopy(double widthC, double heightC) {
        Point centerPoint = new Point(widthC, heightC);
        return new Ellipse(centerPoint, getsMayorAxis(), getsMinorAxis(), getLineColor(), getFillColor(), getBorderSize());
    }
}
