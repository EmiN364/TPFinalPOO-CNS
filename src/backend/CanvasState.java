package backend;

import backend.model.Figure;

import java.util.ArrayList;
import java.util.List;

public class CanvasState {
    private Figure clipBoardFigure, formatFigure;

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

    public void setClipBoardFigure(Figure figure) {
        this.clipBoardFigure = figure;
    }

    public void clearClipBoardFigure() {
        this.clipBoardFigure = null;
    }

    public Figure getClipBoardFigure() {
        return clipBoardFigure;
    }
    public void setFormatFigure(Figure figure) {
        this.formatFigure = figure;
    }
    public void clearFormatFigure() {
        this.formatFigure = null;
    }
    public Figure getFormatFigure() {
        return formatFigure;
    }
}
