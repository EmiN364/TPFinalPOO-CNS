package frontend;

import backend.model.*;
import javafx.scene.control.ToggleButton;

public class FigureButton extends ToggleButton {
    private final FigureType figureType;

    public FigureButton(String text, FigureType figureType) {
        super(text);
        this.figureType = figureType;
    }

    public FigureType getFigureType() {
        return figureType;
    }

    public enum FigureType {
        RECTANGLE() {
            @Override
            public Figure create(Point startPoint, Point endPoint, FigureStyle figureStyle) {
               return new Rectangle(startPoint, endPoint, figureStyle);
            }
        },
        CIRCLE() {
            @Override
            public Figure create(Point startPoint, Point endPoint, FigureStyle figureStyle) {
                double circleRadius = Math.abs(endPoint.getX() - startPoint.getX());
                return new Circle(startPoint, circleRadius, figureStyle);
            }
        },
        SQUARE() {
            @Override
            public Figure create(Point startPoint, Point endPoint, FigureStyle figureStyle) {
                double size = Math.abs(endPoint.getX() - startPoint.getX());
                return new Square(startPoint, size, figureStyle);
            }
        },
        ELLIPSE() {
            @Override
            public Figure create(Point startPoint, Point endPoint, FigureStyle figureStyle) {
                Point centerPoint = new Point(Math.abs(endPoint.getX() + startPoint.getX()) / 2, (Math.abs((endPoint.getY() + startPoint.getY())) / 2));
                double sMayorAxis = Math.abs(endPoint.getX() - startPoint.getX());
                double sMinorAxis = Math.abs(endPoint.getY() - startPoint.getY());
                return new Ellipse(centerPoint, sMayorAxis, sMinorAxis, figureStyle);
            }
        };

        public abstract Figure create(Point startPoint, Point endPoint, FigureStyle figureStyle);
    }
}

