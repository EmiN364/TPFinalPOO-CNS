package backend;

import backend.model.Figure;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CanvasState {
    private Figure auxFigure;

    private final List<Figure> list = new ArrayList<>();

    public void addFigure(Figure figure) {
        list.add(figure);
    }

    public void deleteFigure(Figure figure) {
        list.remove(figure);
    }

    public Iterable<Figure> figures() {
        return new ArrayList<>(list);
    }

    public void setAuxFigure(Figure figure) {
        this.auxFigure = figure;
    }

    public Figure getAuxFigure() {
        return auxFigure;
    }
}
