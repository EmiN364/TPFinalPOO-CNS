package backend.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class Figure implements Cloneable {
    private Color lineColor, fillColor;
    private double borderSize;

    public Figure(Color lineColor, Color fillColor, double borderSize) {
        this.lineColor = lineColor;
        this.fillColor = fillColor;
        this.borderSize = borderSize;
    }

    public abstract String getName();

    public double getBorderSize() {
        return borderSize;
    }

    public void setBorderSize(double borderSize) {
        this.borderSize = borderSize;
    }

    public Color getLineColor() {
        return lineColor;
    }

    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }
    public void setFormat(Figure figure) {
        setFillColor(figure.getFillColor());
        setLineColor(figure.getLineColor());
        setBorderSize(figure.getBorderSize());
    }

    public abstract void move(double diffX, double diffY);
    public abstract boolean belongs(Point eventPoint);
    public abstract void draw(GraphicsContext gc);

    @Override
    public abstract Figure clone();
}
