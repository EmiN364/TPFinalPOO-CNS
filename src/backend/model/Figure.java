package backend.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class Figure implements Cloneable {
    private final FigureStyle figureStyle;

    public Figure(FigureStyle figureStyle) {
        this.figureStyle = figureStyle;
    }

    public abstract String getName();

    public FigureStyle getFigureStyleCopy() {
        return figureStyle.getCopy();
    }
    public Color getFillColor() {
        return figureStyle.getFillColor();
    }
    public void setFillColor(Color fillColor) {
        figureStyle.setFillColor(fillColor);
    }
    public Color getLineColor() {
        return figureStyle.getLineColor();
    }
    public void setLineColor(Color lineColor) {
        figureStyle.setLineColor(lineColor);
    }
    public double getBorderSize() {
        return figureStyle.getBorderSize();
    }
    public void setBorderSize(double borderSize) {
        figureStyle.setBorderSize(borderSize);
    }

    public void copyFormat(Figure figure) {
        figureStyle.copyFormat(figure);
    }

    public abstract void move(double diffX, double diffY);
    public abstract boolean belongs(Point eventPoint);
    public abstract void draw(GraphicsContext gc);

    @Override
    public abstract Figure clone();

    public abstract Figure getCenteredCopy(double widthC, double heightC);
}
