package frontend;

import backend.CanvasState;
import backend.OperationType;
import backend.OperationsHistory;
import backend.model.*;
import com.sun.javafx.scene.web.skin.HTMLEditorSkin;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
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
	CanvasState canvasState;

	// Canvas y relacionados
	Canvas canvas = new Canvas(800, 600);
	GraphicsContext gc = canvas.getGraphicsContext2D();
	Color lineColor = Color.BLACK;
	Color fillColor = Color.YELLOW;

	// Botones Barra Izquierda
	ToggleButton selectionButton = new ToggleButton("Seleccionar");
	FigureButton rectangleButton = new FigureButton("Rectángulo", FigureButton.FigureType.RECTANGLE);
	FigureButton circleButton = new FigureButton("Círculo", FigureButton.FigureType.CIRCLE);
	FigureButton squareButton = new FigureButton("Cuadrado", FigureButton.FigureType.SQUARE);
	FigureButton ellipseButton = new FigureButton("Elipse", FigureButton.FigureType.ELLIPSE);
	ToggleButton deleteButton = new ToggleButton("Borrar");
	ToggleButton copyFormatButton = new ToggleButton("Cop. Form.");
	Slider borderSlider = new Slider(1, 50, 25);
	ColorPicker lineColorPicker = new ColorPicker(lineColor);
	ColorPicker fillColorPicker = new ColorPicker(fillColor);
	Label sliderName = new Label("Borde");
	Label fillName = new Label("Relleno");

	//Botones para barra superior
	Button cutButton = new Button("Cortar", new ImageView(getIcon("cutIcon")));
	Button copyButton = new Button("Copiar", new ImageView(getIcon("copyIcon")));
	Button pasteButton = new Button("Pegar", new ImageView(getIcon("pasteIcon")));
	Button undoButton = new Button("Deshacer", new ImageView(getIcon("undoIcon")));
	Button redoButton = new Button("Rehacer", new ImageView(getIcon("redoIcon")));
	Label undoNext = new Label();
	Label redoNext = new Label();
	Label undoAmount = new Label("0");
	Label redoAmount = new Label("0");

	// Dibujar una figura
	Point startPoint;
	// Seleccionar una figura
	Figure selectedFigure;
	// StatusBar
	StatusPane statusPane;
	// Operations
	OperationsHistory operationsHistory;

	public PaintPane(CanvasState canvasState, StatusPane statusPane) {
		this.canvasState = canvasState;
		this.statusPane = statusPane;
		this.operationsHistory = new OperationsHistory(canvasState);

		copyFormatButton.setOnAction(event -> {
			if (selectedFigure != null) {
				addOperation(OperationType.COPYFORMAT, canvasState.getFormatFigure(), selectedFigure);
				canvasState.setFormatFigure(selectedFigure);
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

		buttonsTop2.getChildren().addAll(undoNext, undoAmount, undoButton, redoButton, redoAmount, redoNext);
		undoNext.setAlignment(Pos.CENTER_RIGHT);
		undoNext.setPrefWidth(300);
		redoNext.setPrefWidth(300);
		redoNext.setAlignment(Pos.CENTER_LEFT);
		undoAmount.setAlignment(Pos.CENTER);
		undoAmount.setPrefWidth(30);
		redoAmount.setPrefWidth(30);
		redoAmount.setAlignment(Pos.CENTER);
		buttonsTop2.setAlignment(Pos.CENTER);

		buttonsTop1.getChildren().addAll(cutButton,copyButton,pasteButton);
		buttonsTop.getChildren().addAll(buttonsTop1, buttonsTop2);
		buttonsTop.setStyle("-fx-background-color: #999");
		buttonsTop.setPadding(new Insets(5));
		buttonsTop.setPrefWidth(600);
		buttonsBox.getChildren().addAll(toolsArr);
		borderSlider.setShowTickMarks(true);
		borderSlider.setShowTickLabels(true);
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

		deleteButton.setOnAction(event -> {
			if (selectedFigure != null) {
				canvasState.deleteFigure(selectedFigure);
				addOperation(OperationType.DELETE, selectedFigure, null);
				selectedFigure = null;
				redrawCanvas();
			}
		});

		setOnKeyPressed( event -> {
			if (event.isControlDown()) {
				if (event.getCode() == KeyCode.X)
					cutButton.fire();
				else if (event.getCode() == KeyCode.C)
					copyButton.fire();
				else if (event.getCode() == KeyCode.V)
					pasteButton.fire();
				else if (event.getCode() == KeyCode.Z)
					undoButton.fire();
				else if (event.getCode() == KeyCode.Y)
					redoButton.fire();
			} else if (event.getCode() == KeyCode.DELETE)
				deleteButton.fire();
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

		setTop(buttonsTop);
		setLeft(buttonsBox);
		setRight(canvas);
	}

	private void redrawCanvas() {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		for(Figure figure : canvasState.figures()) {
			if(figure == selectedFigure) {
				gc.setStroke(Color.RED);
			} else {
				gc.setStroke(figure.getLineColor());
			}
			gc.setFill(figure.getFillColor());
			gc.setLineWidth(figure.getBorderSize());
			figure.draw(gc);
		}
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

}
