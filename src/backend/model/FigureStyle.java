package backend.model;

import javafx.scene.paint.Color;

public class FigureStyle {
    private Color fillColor, lineColor;
    private double borderSize;

    public FigureStyle(Color fillColor, Color lineColor, double borderSize) {
        this.fillColor = fillColor;
        this.lineColor = lineColor;
        this.borderSize = borderSize;
    }

    public Color getFillColor() {
        return fillColor;
    }
    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }
    public Color getLineColor() {
        return lineColor;
    }
    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
    }
    public double getBorderSize() {
        return borderSize;
    }
    public void setBorderSize(double borderSize) {
        this.borderSize = borderSize;
    }

    public void copyFormat(Figure figure) {
        setFillColor(figure.getFillColor());
        setLineColor(figure.getLineColor());
        setBorderSize(figure.getBorderSize());
    }

    public FigureStyle getCopy() {
        return new FigureStyle(getFillColor(), getLineColor(), getBorderSize());
    }
}
