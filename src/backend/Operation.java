package backend;

import backend.model.Figure;

public class Operation {
    private final Figure oldFigure, newFigure;
    private final OperationType operationType;

    public Operation(Figure oldFigure, Figure newFigure, OperationType operationType) {
        this.oldFigure = oldFigure;
        this.newFigure = newFigure;
        this.operationType = operationType;
    }

    public void undo(CanvasState canvasState) {
        operationType.undo(oldFigure, newFigure, canvasState);
    }

    public void redo(CanvasState canvasState) {
        operationType.redo(oldFigure, newFigure, canvasState);
    }

    @Override
    public String toString() {
        return operationType + (oldFigure != null ? oldFigure.getName() : newFigure.getName());
    }
}
