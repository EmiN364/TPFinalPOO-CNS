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
	ToggleButton rectangleButton = new ToggleButton("Rectángulo");
	ToggleButton circleButton = new ToggleButton("Círculo");
	ToggleButton squareButton = new ToggleButton("Cuadrado");
	ToggleButton ellipseButton = new ToggleButton("Elipse");
	ToggleButton deleteButton = new ToggleButton("Borrar");
	ToggleButton copyFormatButton = new ToggleButton("Cop. Form.");
	Slider borderSlider = new Slider(1, 50, 25);
	ColorPicker lineColorPicker = new ColorPicker(lineColor);
	ColorPicker fillColorPicker = new ColorPicker(fillColor);
	Label sliderName = new Label("Borde");
	Label fillName = new Label("Relleno");

	//Botones para barra superior
	private Image getIcon(String name) {
		String IconPath = ResourceBundle.getBundle(HTMLEditorSkin.class.getName()).getString(name);
		return new Image(Objects.requireNonNull(HTMLEditorSkin.class.getResource(IconPath)).toString());
	}

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
//				addOperation(OperationType.COPYFORMAT, canvasState.getAuxFigure(), selectedFigure);
				canvasState.setAuxFigure(selectedFigure);
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

		buttonsTop2.getChildren().addAll(undoNext, undoAmount);
		buttonsTop2.getChildren().addAll(undoButton, redoButton);
		buttonsTop2.getChildren().addAll(redoAmount, redoNext);
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
			if(startPoint == null) {
				return ;
			}
			if(endPoint.getX() < startPoint.getX() || endPoint.getY() < startPoint.getY()) {
				return ;
			}
			Figure newFigure;
			if(rectangleButton.isSelected()) {
				newFigure = new Rectangle(startPoint, endPoint, lineColorPicker.getValue(), fillColorPicker.getValue(), borderSlider.getValue());
			}
			else if(circleButton.isSelected()) {
				double circleRadius = Math.abs(endPoint.getX() - startPoint.getX());
				newFigure = new Circle(startPoint, circleRadius, lineColorPicker.getValue(), fillColorPicker.getValue(), borderSlider.getValue());
			} else if(squareButton.isSelected()) {
				double size = Math.abs(endPoint.getX() - startPoint.getX());
				newFigure = new Square(startPoint, size, lineColorPicker.getValue(), fillColorPicker.getValue(), borderSlider.getValue());
			} else if(ellipseButton.isSelected()) {
				Point centerPoint = new Point(Math.abs(endPoint.getX() + startPoint.getX()) / 2, (Math.abs((endPoint.getY() + startPoint.getY())) / 2));
				double sMayorAxis = Math.abs(endPoint.getX() - startPoint.getX());
				double sMinorAxis = Math.abs(endPoint.getY() - startPoint.getY());
				newFigure = new Ellipse(centerPoint, sMayorAxis, sMinorAxis, lineColorPicker.getValue(), fillColorPicker.getValue(), borderSlider.getValue());
			} else {
				return ;
			}
			canvasState.addFigure(newFigure);
			addOperation(OperationType.DRAW, null, newFigure);
			startPoint = null;
			redrawCanvas();
		});

		canvas.setOnMouseMoved(event -> {
			Point eventPoint = new Point(event.getX(), event.getY());
			boolean found = false;
			StringBuilder label = new StringBuilder();
			for(Figure figure : canvasState.figures()) {
				if(figureBelongs(figure, eventPoint)) {
					found = true;
					label.append(figure);
				}
			}
			if(found) {
				statusPane.updateStatus(label.toString());
			} else {
				statusPane.updateStatus(eventPoint.toString());
			}
		});

		canvas.setOnMouseClicked(event -> {
			if(selectionButton.isSelected()) {
				Point eventPoint = new Point(event.getX(), event.getY());
				boolean found = false;
				StringBuilder label = new StringBuilder("Se seleccionó: ");
				for (Figure figure : canvasState.figures()) {
					if(figureBelongs(figure, eventPoint)) {
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
			} else if(copyFormatButton.isSelected()) {
				Point eventPoint = new Point(event.getX(), event.getY());
				Figure clickedFigure = null;
				Figure auxFigure = canvasState.getAuxFigure();
				for (Figure figure : canvasState.figures()) {
					if(figureBelongs(figure, eventPoint)) {
						clickedFigure = figure;
					}
				}
				if (clickedFigure != null && auxFigure != null) {
					Figure oldFigure = clickedFigure.clone();
					clickedFigure.setFormat(auxFigure);
					addOperation(OperationType.COPYFORMAT, oldFigure, clickedFigure);
					canvasState.setAuxFigure(null);
					redrawCanvas();
				}
			}

		});

		canvas.setOnMouseDragged(event -> {
			if(selectionButton.isSelected()) {
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
				Figure auxFig = canvasState.getAuxFigure();
				canvasState.setAuxFigure(selectedFigure);
				addOperation(OperationType.CUTFIGURE, auxFig, selectedFigure);
				canvasState.deleteFigure(selectedFigure);
				selectedFigure = null;
				redrawCanvas();
			}
		});

		copyButton.setOnAction(event -> {
			if (selectedFigure != null){
				Figure auxFig = canvasState.getAuxFigure();
				canvasState.setAuxFigure(selectedFigure);
				addOperation(OperationType.COPYFIGURE, auxFig, selectedFigure);
			}
		});
		pasteButton.setOnAction(event -> {
			Figure auxFigure = canvasState.getAuxFigure();
			if(auxFigure != null) {
				Figure newFigure;
				if(auxFigure instanceof Rectangle) {
					Rectangle aux = (Rectangle) auxFigure;
					double height = aux.getBottomRight().getX() - aux.getTopLeft().getX();
					double width = aux.getBottomRight().getY() - aux.getTopLeft().getY();
					Point topLeft = new Point(canvas.getWidth()/2 - height/2, canvas.getHeight()/2 - width/2);
					Point bottomRight = new Point(canvas.getWidth()/2 + height/2, canvas.getHeight()/2 + width/2);
					newFigure = new Rectangle(topLeft, bottomRight, auxFigure.getLineColor(), auxFigure.getFillColor(), aux.getBorderSize());
				} else if(auxFigure instanceof Ellipse) {
					Ellipse aux = (Ellipse) auxFigure;
					Point centerPoint = new Point( canvas.getWidth()/2, canvas.getHeight()/2);
					newFigure = new Ellipse(centerPoint, aux.getsMayorAxis(), aux.getsMinorAxis(), auxFigure.getLineColor(), auxFigure.getFillColor(), aux.getBorderSize());
				} else {
					return ;
				}
				canvasState.addFigure(newFigure);
				redrawCanvas();
				canvasState.setAuxFigure(null);
				addOperation(OperationType.PASTEFIGURE, auxFigure, newFigure);
			}
		});

		setTop(buttonsTop);
		setLeft(buttonsBox);
		setRight(canvas);
	}

	void redrawCanvas() {
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

	boolean figureBelongs(Figure figure, Point eventPoint) {
		return figure.belongs(eventPoint);
	}



}
