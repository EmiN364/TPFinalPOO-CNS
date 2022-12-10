package backend;

import backend.model.Figure;

public class Operation {
    private Figure oldFigure, newFigure;
    private OperationType operationType;

    public Operation(Figure oldFigure, Figure newFigure, OperationType operationType) {
        this.oldFigure = oldFigure;
        this.newFigure = newFigure;
        this.operationType = operationType;
    }
}
