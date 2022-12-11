package backend;

import backend.model.Figure;
import javafx.scene.paint.Color;

public enum OperationType {
    DRAW("Dibujar un ") {
        public void undo(Figure oldFigure, Figure newFigure, CanvasState canvasState) {
            canvasState.deleteFigure(newFigure);
        }
        public void redo(Figure oldFigure, Figure newFigure, CanvasState canvasState) {
            canvasState.addFigure(newFigure);
        }
    },
    DELETE("Borrar un ") {
        public void undo(Figure oldFigure, Figure newFigure, CanvasState canvasState){
            canvasState.addFigure(oldFigure);
        }

        @Override
        public void redo(Figure oldFigure, Figure newFigure, CanvasState canvasState) {
            canvasState.deleteFigure(oldFigure);
        }
    },
    CHANGECOLOR("Cambiar color del borde de ") {
        @Override
        public void undo(Figure oldFigure, Figure newFigure, CanvasState canvasState) {
            Color aux = newFigure.getLineColor();
            newFigure.setLineColor(oldFigure.getLineColor());
            oldFigure.setLineColor(aux);
        }

        @Override
        public void redo(Figure oldFigure, Figure newFigure, CanvasState canvasState) {
            Color aux = newFigure.getLineColor();
            newFigure.setLineColor(oldFigure.getLineColor());
            oldFigure.setLineColor(aux);
        }
    },
    CHANGEFILL("Cambiar color de relleno de ") {
        @Override
        public void undo(Figure oldFigure, Figure newFigure, CanvasState canvasState) {
            Color aux = newFigure.getFillColor();
            newFigure.setFillColor(oldFigure.getFillColor());
            oldFigure.setFillColor(aux);
        }

        @Override
        public void redo(Figure oldFigure, Figure newFigure, CanvasState canvasState) {
            Color aux = newFigure.getFillColor();
            newFigure.setFillColor(oldFigure.getFillColor());
            oldFigure.setFillColor(aux);
        }

    },
    CHANGEBORDER("Cambiar tamaño del borde de ") {
        @Override
        public void undo(Figure oldFigure, Figure newFigure, CanvasState canvasState) {
            double aux = newFigure.getBorderSize();
            newFigure.setBorderSize(oldFigure.getBorderSize());
            oldFigure.setBorderSize(aux);
        }
        public void redo(Figure oldFigure, Figure newFigure, CanvasState canvasState) {
            Color aux = newFigure.getFillColor();
            newFigure.setFillColor(oldFigure.getFillColor());
            oldFigure.setFillColor(aux);
        }
    },
    COPYFORMAT("Copiar formato de ") {
        @Override
        public void undo(Figure oldFigure, Figure newFigure, CanvasState canvasState) {
            canvasState.setAuxFigure(newFigure.clone());
            newFigure.setFormat(oldFigure);
        }

        @Override
        public void redo(Figure oldFigure, Figure newFigure, CanvasState canvasState) {
            newFigure.setFormat(canvasState.getAuxFigure());
            canvasState.setAuxFigure(null);
        }
    },
    COPYFIGURE("Copiar un ") {
        @Override
        public void undo(Figure oldFigure, Figure newFigure, CanvasState canvasState) {
            canvasState.setAuxFigure(oldFigure);
        }

        @Override
        public void redo(Figure oldFigure, Figure newFigure, CanvasState canvasState) {
            canvasState.setAuxFigure(newFigure);
        }
    },
    CUTFIGURE("Cortar un ") {
        @Override
        public void undo(Figure oldFigure, Figure newFigure, CanvasState canvasState) {
            canvasState.setAuxFigure(oldFigure);
            canvasState.addFigure(newFigure);
        }

        @Override
        public void redo(Figure oldFigure, Figure newFigure, CanvasState canvasState) {
            canvasState.setAuxFigure(newFigure);
            canvasState.deleteFigure(oldFigure);
        }
    },
    PASTEFIGURE("Pegar un ") {
        @Override
        public void undo(Figure oldFigure, Figure newFigure, CanvasState canvasState) {
            canvasState.deleteFigure(newFigure);
            canvasState.setAuxFigure(oldFigure);
        }

        @Override
        public void redo(Figure oldFigure, Figure newFigure, CanvasState canvasState) {
            canvasState.addFigure(newFigure);
            canvasState.setAuxFigure(null);
        }
    };

    private final String name;
    public abstract void undo(Figure oldFigure, Figure newFigure, CanvasState canvasState);
    public abstract void redo(Figure oldFigure, Figure newFigure, CanvasState canvasState);
    OperationType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
