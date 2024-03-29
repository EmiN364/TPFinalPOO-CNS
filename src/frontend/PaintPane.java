package frontend;

import backend.CanvasState;
import backend.OperationType;
import backend.OperationsHistory;
import backend.model.*;
import com.sun.javafx.scene.web.skin.HTMLEditorSkin;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyCode;
import java.util.Objects;
import java.util.ResourceBundle;


public class PaintPane extends BorderPane {

	// BackEnd
	private final CanvasState canvasState;

	// Canvas y relacionados
	private final Canvas canvas = new Canvas(800, 600);
	private final GraphicsContext gc = canvas.getGraphicsContext2D();
	private final Color lineColor = Color.BLACK;
	private final Color fillColor = Color.YELLOW;

	// Botones Barra Izquierda
	private final ToggleButton selectionButton = new ToggleButton("Seleccionar");
	private final FigureButton rectangleButton = new FigureButton("Rectángulo", FigureButton.FigureType.RECTANGLE);
	private final FigureButton circleButton = new FigureButton("Círculo", FigureButton.FigureType.CIRCLE);
	private final FigureButton squareButton = new FigureButton("Cuadrado", FigureButton.FigureType.SQUARE);
	private final FigureButton ellipseButton = new FigureButton("Elipse", FigureButton.FigureType.ELLIPSE);
	private final ToggleButton deleteButton = new ToggleButton("Borrar");
	private final ToggleButton copyFormatButton = new ToggleButton("Cop. Form.");
	private final Slider borderSlider = new Slider(1, 50, 25);
	private final ColorPicker lineColorPicker = new ColorPicker(lineColor);
	private final ColorPicker fillColorPicker = new ColorPicker(fillColor);
	private final Label undoNext = new Label();
	private final Label redoNext = new Label();
	private final Label undoAmount = new Label("0");
	private final Label redoAmount = new Label("0");

	// Dibujar una figura
	private Point startPoint;
	// Seleccionar una figura
	private Figure selectedFigure;
	// Operations
	private final OperationsHistory operationsHistory;

	public PaintPane(CanvasState canvasState, StatusPane statusPane) {
		this.canvasState = canvasState;
		// StatusBar
		this.operationsHistory = new OperationsHistory(canvasState);

		ToggleButton[] toolsArr = {selectionButton, rectangleButton, circleButton, squareButton, ellipseButton, deleteButton, copyFormatButton};
		ToggleGroup tools = new ToggleGroup();
		for (ToggleButton tool : toolsArr) {
			tool.setMinWidth(90);
			tool.setToggleGroup(tools);
			tool.setCursor(Cursor.HAND);
		}
		VBox buttonsBox = new VBox(10);
		VBox buttonsTop = new VBox(10);
		HBox buttonsTop1 = new HBox(10);
		HBox buttonsTop2 = new HBox(10);

		setStyle(undoNext, Pos.CENTER_RIGHT, 300);
		setStyle(redoNext, Pos.CENTER_LEFT, 300);
		setStyle(undoAmount, Pos.CENTER, 30);
		setStyle(redoAmount, Pos.CENTER, 30);
		ControlButton redoButton = new ControlButton("Rehacer", new ImageView(getIcon("redoIcon")), KeyCode.Y);
		ControlButton undoButton = new ControlButton("Deshacer", new ImageView(getIcon("undoIcon")), KeyCode.Z);
		buttonsTop2.getChildren().addAll(undoNext, undoAmount, undoButton, redoButton, redoAmount, redoNext);
		buttonsTop2.setAlignment(Pos.CENTER);

		//Botones para barra superior
		ControlButton cutButton = new ControlButton("Cortar", new ImageView(getIcon("cutIcon")), KeyCode.X);
		ControlButton pasteButton = new ControlButton("Pegar", new ImageView(getIcon("pasteIcon")), KeyCode.V);
		ControlButton copyButton = new ControlButton("Copiar", new ImageView(getIcon("copyIcon")), KeyCode.C);
		buttonsTop1.getChildren().addAll(cutButton, copyButton, pasteButton);

		buttonsTop.getChildren().addAll(buttonsTop1, buttonsTop2);
		buttonsTop.setStyle("-fx-background-color: #999");
		buttonsTop.setPadding(new Insets(5));
		buttonsTop.setPrefWidth(canvas.getWidth());

		borderSlider.setShowTickMarks(true);
		borderSlider.setShowTickLabels(true);
		buttonsBox.getChildren().addAll(toolsArr);
		Label sliderName = new Label("Borde");
		Label fillName = new Label("Relleno");
		buttonsBox.getChildren().addAll(sliderName, borderSlider, lineColorPicker, fillName, fillColorPicker);
		buttonsBox.setPadding(new Insets(5));
		buttonsBox.setStyle("-fx-background-color: #999");
		buttonsBox.setPrefWidth(100);
		gc.setLineWidth(1);

		canvas.setOnMousePressed(event -> startPoint = new Point(event.getX(), event.getY()));

		canvas.setOnMouseReleased(event -> {
			Point endPoint = new Point(event.getX(), event.getY());
			if (startPoint == null) {
				return ;
			}
			if (endPoint.getX() < startPoint.getX() || endPoint.getY() < startPoint.getY()) {
				return ;
			}
			FigureButton[] figureButtons = {rectangleButton, circleButton, squareButton, ellipseButton};
			Figure newFigure = null;
			for (FigureButton figureButton : figureButtons) {
				if (figureButton.isSelected())
					newFigure = figureButton.getFigureType().create(startPoint, endPoint, getCurrentStyle());
			}
			if (newFigure == null)
				return;
			canvasState.addFigure(newFigure);
			addOperation(OperationType.DRAW, null, newFigure);
			startPoint = null;
			redrawCanvas();
		});

		canvas.setOnMouseMoved(event -> {
			Point eventPoint = new Point(event.getX(), event.getY());
			boolean found = false;
			StringBuilder label = new StringBuilder();
			for (Figure figure : canvasState.figures()) {
				if (figure.belongs(eventPoint)) {
					found = true;
					label.append(figure);
				}
			}
			if (found) {
				statusPane.updateStatus(label.toString());
			} else {
				statusPane.updateStatus(eventPoint.toString());
			}
		});

		canvas.setOnMouseClicked(event -> {
			if (selectionButton.isSelected()) {
				Point eventPoint = new Point(event.getX(), event.getY());
				boolean found = false;
				StringBuilder label = new StringBuilder("Se seleccionó: ");
				for (Figure figure : canvasState.figures()) {
					if(figure.belongs(eventPoint)) {
						found = true;
						selectedFigure = figure;
						label.append(figure);
					}
				}
				if (found) {
					statusPane.updateStatus(label.toString());
				} else {
					selectedFigure = null;
					statusPane.updateStatus("Ninguna figura encontrada");
				}
				redrawCanvas();
			} else if (copyFormatButton.isSelected()) {
				Point eventPoint = new Point(event.getX(), event.getY());
				Figure clickedFigure = null;
				Figure formatFigure = canvasState.getFormatFigure();
				for (Figure figure : canvasState.figures()) {
					if (figure.belongs(eventPoint)) {
						clickedFigure = figure;
					}
				}
				if (clickedFigure != null && formatFigure != null) {
					Figure oldFigure = clickedFigure.clone();
					clickedFigure.copyFormat(formatFigure);
					addOperation(OperationType.PASTEFORMAT, oldFigure, clickedFigure);
					canvasState.clearFormatFigure();
					selectedFigure = null;
					redrawCanvas();
				}
			}
		});

		canvas.setOnMouseDragged(event -> {
			if (selectionButton.isSelected()) {
				Point eventPoint = new Point(event.getX(), event.getY());
				double diffX = (eventPoint.getX() - startPoint.getX()) / 100;
				double diffY = (eventPoint.getY() - startPoint.getY()) / 100;
				if (selectedFigure != null)
					selectedFigure.move(diffX, diffY);
				redrawCanvas();
			}
		});

		borderSlider.setOnMouseReleased( event -> {
			if (selectedFigure != null) {
				Figure auxFig = selectedFigure.clone();
				selectedFigure.setBorderSize(borderSlider.getValue());
				addOperation(OperationType.CHANGEBORDER, auxFig, selectedFigure);
				redrawCanvas();
			}
		});
		lineColorPicker.setOnAction( event -> {
			if (selectedFigure != null) {
				Figure auxFig = selectedFigure.clone();
				selectedFigure.setLineColor(lineColorPicker.getValue());
				addOperation(OperationType.CHANGECOLOR, auxFig, selectedFigure);
				redrawCanvas();
			}
		});
		fillColorPicker.setOnAction( event -> {
			if (selectedFigure != null) {
				Figure auxFig = selectedFigure.clone();
				selectedFigure.setFillColor(fillColorPicker.getValue());
				addOperation(OperationType.CHANGEFILL, auxFig, selectedFigure);
				redrawCanvas();
			}
		});

		deleteButton.setOnAction(event -> {
			if (selectedFigure != null) {
				canvasState.deleteFigure(selectedFigure);
				addOperation(OperationType.DELETE, selectedFigure, null);
				selectedFigure = null;
				redrawCanvas();
			}
		});

		undoButton.setOnAction(event -> {
			operationsHistory.undoOperation();
			updateUndosRedos();
			redrawCanvas();
		});
		redoButton.setOnAction(event -> {
			operationsHistory.redoOperation();
			updateUndosRedos();
			redrawCanvas();
		});

		copyFormatButton.setOnAction(event -> {
			if (selectedFigure != null) {
				addOperation(OperationType.COPYFORMAT, canvasState.getFormatFigure(), selectedFigure);
				canvasState.setFormatFigure(selectedFigure);
			}
		});
		cutButton.setOnAction(event -> {
			if (selectedFigure != null) {
				Figure clipBoardFig = canvasState.getClipBoardFigure();
				canvasState.setClipBoardFigure(selectedFigure);
				addOperation(OperationType.CUTFIGURE, clipBoardFig, selectedFigure);
				canvasState.deleteFigure(selectedFigure);
				selectedFigure = null;
				redrawCanvas();
			}
		});
		copyButton.setOnAction(event -> {
			if (selectedFigure != null){
				Figure clipBoardFig = canvasState.getClipBoardFigure();
				canvasState.setClipBoardFigure(selectedFigure);
				addOperation(OperationType.COPYFIGURE, clipBoardFig, selectedFigure);
			}
		});
		pasteButton.setOnAction(event -> {
			Figure clipBoardFig = canvasState.getClipBoardFigure();
			if(clipBoardFig != null) {
				Figure newFigure = clipBoardFig.getCenteredCopy(canvas.getWidth()/2, canvas.getHeight()/2);
				canvasState.addFigure(newFigure);
				redrawCanvas();
				canvasState.clearClipBoardFigure();
				addOperation(OperationType.PASTEFIGURE, clipBoardFig, newFigure);
			}
		});

		ControlButton[] controlButtons = {cutButton, copyButton, pasteButton, undoButton, redoButton};
		setOnKeyPressed( event -> {
			if (event.isControlDown()) {
				for (ControlButton controlButton : controlButtons) {
					if (event.getCode().equals(controlButton.keyCode))
						controlButton.fire();
				}
			} else if (event.getCode().equals(KeyCode.DELETE))
				deleteButton.fire();
		});

		setTop(buttonsTop);
		setLeft(buttonsBox);
		setRight(canvas);
	}

	private void redrawCanvas() {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		for (Figure figure : canvasState.figures()) {
			if (figure == selectedFigure) {
				gc.setStroke(Color.RED);
			} else {
				gc.setStroke(figure.getLineColor());
			}
			gc.setFill(figure.getFillColor());
			gc.setLineWidth(figure.getBorderSize());
			figure.draw(gc);
		}
	}

	private void setStyle(Label label, Pos pos, double width) {
		label.setAlignment(pos);
		label.setPrefWidth(width);
	}

	private void addOperation(OperationType operationType, Figure oldFigure, Figure newFigure) {
		operationsHistory.addOperation(operationType, oldFigure, newFigure);
		updateUndosRedos();
	}

	private void updateUndosRedos() {
		undoAmount.setText(operationsHistory.getAmountUndos());
		redoAmount.setText(operationsHistory.getAmountRedos());
		undoNext.setText(operationsHistory.getNextUndo());
		redoNext.setText(operationsHistory.getNextRedo());
	}

	private Image getIcon(String name) {
		String IconPath = ResourceBundle.getBundle(HTMLEditorSkin.class.getName()).getString(name);
		return new Image(Objects.requireNonNull(HTMLEditorSkin.class.getResource(IconPath)).toString());
	}

	private FigureStyle getCurrentStyle() {
		return new FigureStyle(fillColorPicker.getValue(), lineColorPicker.getValue(), borderSlider.getValue());
	}

	private static class ControlButton extends Button {
		private final KeyCode keyCode;

		public ControlButton(String text, Node graphic, KeyCode keyCode) {
			super(text, graphic);
			this.keyCode = keyCode;
		}
	}
}
