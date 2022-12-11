package frontend;

import backend.model.*;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;

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
            public Figure create(Point startPoint, Point endPoint, Color line, Color fill, double borderSize) {
               return new Rectangle(startPoint, endPoint, line, fill, borderSize);
            }
        },
        CIRCLE() {
            @Override
            public Figure create(Point startPoint, Point endPoint, Color line, Color fill, double borderSize) {
                double circleRadius = Math.abs(endPoint.getX() - startPoint.getX());
                return new Circle(startPoint, circleRadius, line, fill, borderSize);
            }
        },
        SQUARE() {
            @Override
            public Figure create(Point startPoint, Point endPoint, Color line, Color fill, double borderSize) {
                double size = Math.abs(endPoint.getX() - startPoint.getX());
                return new Square(startPoint, size, line, fill, borderSize);
            }
        },
        ELLIPSE() {
            @Override
            public Figure create(Point startPoint, Point endPoint, Color line, Color fill, double borderSize) {
                Point centerPoint = new Point(Math.abs(endPoint.getX() + startPoint.getX()) / 2, (Math.abs((endPoint.getY() + startPoint.getY())) / 2));
                double sMayorAxis = Math.abs(endPoint.getX() - startPoint.getX());
                double sMinorAxis = Math.abs(endPoint.getY() - startPoint.getY());
                return new Ellipse(centerPoint, sMayorAxis, sMinorAxis, line, fill, borderSize);
            }
        };

        public abstract Figure create(Point startPoint, Point endPoint, Color line, Color fill, double borderSize);
    }
}

