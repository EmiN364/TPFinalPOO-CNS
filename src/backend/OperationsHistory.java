package backend;

import backend.model.Figure;
import java.util.ArrayDeque;
import java.util.Deque;

public class OperationsHistory {
    private final Deque<Operation> undoQueue, redoQueue;
    private final CanvasState canvasState;

    public OperationsHistory(CanvasState canvasState) {
        this.canvasState = canvasState;
        undoQueue = new ArrayDeque<>();
        redoQueue = new ArrayDeque<>();
    }

    public void addOperation(OperationType operationType, Figure oldFigure, Figure newFigure) {
        if (!redoQueue.isEmpty())
            clearRedoQueue();
        undoQueue.push(new Operation(oldFigure, newFigure, operationType));
    }

    public void undoOperation() {
        if (undoQueue.isEmpty())
            return;
//            throw new IllegalStateException("No hay operaciones para deshacer");
        Operation op = undoQueue.pop();
        redoQueue.push(op);
        op.undo(canvasState);
    }

    public void redoOperation() {
        if (redoQueue.isEmpty())
            return;
            //throw new IllegalStateException("No hay operaciones para rehacer");
        Operation op = redoQueue.pop();
        undoQueue.push(op);
        op.redo(canvasState);
    }

    private void clearRedoQueue() {
        redoQueue.clear();
    }

    public String getNextUndo() {
        if (undoQueue.isEmpty())
            return "";
        return undoQueue.peek().toString();
    }

    public String getNextRedo() {
        if (redoQueue.isEmpty())
            return "";
        return redoQueue.peek().toString();
    }

    public String getAmountUndos() {
        return Integer.valueOf(undoQueue.size()).toString();
    }

    public String getAmountRedos() {
        return Integer.valueOf(redoQueue.size()).toString();
    }
}
