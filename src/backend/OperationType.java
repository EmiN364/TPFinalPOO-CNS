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
            CHANGECOLOR.undo(oldFigure, newFigure, canvasState);
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
            CHANGEFILL.undo(oldFigure, newFigure, canvasState);
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
            CHANGEBORDER.undo(oldFigure, newFigure, canvasState);
        }
    },
    COPYFORMAT("Copiar formato de ") {
        @Override
        public void undo(Figure oldFigure, Figure newFigure, CanvasState canvasState) {
            canvasState.setFormatFigure(oldFigure);
        }
        @Override
        public void redo(Figure oldFigure, Figure newFigure, CanvasState canvasState) {
            canvasState.setFormatFigure(newFigure);
        }
    },
    PASTEFORMAT("Pegar formato a ") {
        @Override
        public void undo(Figure oldFigure, Figure newFigure, CanvasState canvasState) {
            canvasState.setFormatFigure(newFigure.clone());
            newFigure.copyFormat(oldFigure);
        }
        @Override
        public void redo(Figure oldFigure, Figure newFigure, CanvasState canvasState) {
            newFigure.copyFormat(canvasState.getFormatFigure());
            canvasState.clearFormatFigure();
        }
    },
    COPYFIGURE("Copiar un ") {
        @Override
        public void undo(Figure oldFigure, Figure newFigure, CanvasState canvasState) {
            canvasState.setClipBoardFigure(oldFigure);
        }

        @Override
        public void redo(Figure oldFigure, Figure newFigure, CanvasState canvasState) {
            canvasState.setClipBoardFigure(newFigure);
        }
    },
    CUTFIGURE("Cortar un ") {
        @Override
        public void undo(Figure oldFigure, Figure newFigure, CanvasState canvasState) {
            canvasState.setClipBoardFigure(oldFigure);
            canvasState.addFigure(newFigure);
        }

        @Override
        public void redo(Figure oldFigure, Figure newFigure, CanvasState canvasState) {
            canvasState.setClipBoardFigure(newFigure);
            canvasState.deleteFigure(newFigure);
        }
    },
    PASTEFIGURE("Pegar un ") {
        @Override
        public void undo(Figure oldFigure, Figure newFigure, CanvasState canvasState) {
            canvasState.deleteFigure(newFigure);
            canvasState.setClipBoardFigure(oldFigure);
        }

        @Override
        public void redo(Figure oldFigure, Figure newFigure, CanvasState canvasState) {
            canvasState.addFigure(newFigure);
            canvasState.clearClipBoardFigure();
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
