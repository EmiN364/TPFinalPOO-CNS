package backend;

import backend.CanvasState;
import backend.model.Figure;
import frontend.PaintPane;

public enum Operation {
    DRAW("Dibujar una figura") {
        public void undo(Figure oldFigure, Figure newFigure, CanvasState canvasState) {
            canvasState.deleteFigure(oldFigure);
        }
        public void redo(Figure oldFigure, Figure newFigure, CanvasState canvasState) {
            canvasState.addFigure(oldFigure);
        }
    },
    DELETE("Borrar una figura") {
        public void undo(Figure oldFigure,Figure newFigure, CanvasState canvasState){
            canvasState.addFigure(oldFigure);
        }

        @Override
        public void redo(Figure oldFigure,Figure newFigure, CanvasState canvasState) {
            canvasState.deleteFigure(oldFigure);
        }
    },
    CHANGECOLOR("Cambiar el color de borde") {
        @Override
        public void undo(Figure oldFigure,Figure newFigure, CanvasState canvasState) {
            newFigure.setLineColor(oldFigure.getLineColor());
        }

        @Override
        public void redo(Figure oldFigure,Figure newFigure, CanvasState canvasState) {
            newFigure.setLineColor(oldFigure.getLineColor());
        }
    },
    CHANGEFILL("Cambiar el color de relleno") {
        @Override
        public void undo(Figure oldFigure,Figure newFigure, CanvasState canvasState) {
            newFigure.setFillColor(oldFigure.getFillColor());
        }

        @Override
        public void redo(Figure oldFigure,Figure newFigure, CanvasState canvasState) {
            newFigure.setFillColor(oldFigure.getFillColor());
        }

    },
    CHANGEBORDER("Cambiar el formato") {
        @Override
        public void undo(Figure oldFigure,Figure newFigure, CanvasState canvasState) {
            newFigure.setBorderSize(oldFigure.getBorderSize());
        }
        public void redo(Figure oldFigure,Figure newFigure, CanvasState canvasState) {
            newFigure.setBorderSize(oldFigure.getBorderSize());
        }
    },
    COPYFORMAT("Copiar el formato") {
        @Override
        public void undo(Figure oldFigure, Figure newFigure, CanvasState canvasState) {
            canvasState.setAuxFigure(newFigure);
        }

        @Override
        public void redo(Figure oldFigure, Figure newFigure, CanvasState canvasState) {
             canvasState.setAuxFigure(oldFigure);
        }
    },
    COPYFIGURE("Copiar una figura") {
        @Override
        public void undo(Figure oldFigure, Figure newFigure, CanvasState canvasState) {
            canvasState.setAuxFigure(newFigure);
        }

        @Override
        public void redo(Figure oldFigure, Figure newFigure, CanvasState canvasState) {
            canvasState.setAuxFigure(oldFigure);
        }
    },
    CUTFIGURE("Cortar una figura") {
        @Override
        public void undo(Figure oldFigure, Figure newFigure, CanvasState canvasState) {
            canvasState.setAuxFigure(newFigure);
            canvasState.addFigure(oldFigure);
        }

        @Override
        public void redo(Figure oldFigure, Figure newFigure, CanvasState canvasState) {
            canvasState.setAuxFigure(oldFigure);
            canvasState.deleteFigure(oldFigure);
        }
    },
    PASTEFIGURE("Pegar una figura") {
        @Override
        public void undo(Figure oldFigure, Figure newFigure, CanvasState canvasState) {
            canvasState.deleteFigure(newFigure);
            canvasState.setAuxFigure(oldFigure);
        }

        @Override
        public void redo(Figure oldFigure, Figure newFigure, CanvasState canvasState) {
            canvasState.addFigure(oldFigure);
            canvasState.setAuxFigure(newFigure);
        }
    };

    private final String name;
    public abstract void undo(Figure oldFigure,Figure newFigure, CanvasState canvasState);
    public abstract void redo(Figure oldFigure,Figure newFigure, CanvasState canvasState);
    Operation(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
