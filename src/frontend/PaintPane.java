package frontend;

import backend.CanvasState;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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

	// Dibujar una figura
	Point startPoint;

	// Seleccionar una figura
	Figure selectedFigure;
	Figure auxFigure;

	// StatusBar
	StatusPane statusPane;
	Colors format;


	public PaintPane(CanvasState canvasState, StatusPane statusPane) {
		this.canvasState = canvasState;
		this.statusPane = statusPane;

		copyFormatButton.setOnAction(event -> {
			if (selectedFigure != null) {
				format = new Colors(selectedFigure.getLineColor(), selectedFigure.getFillColor(), selectedFigure.getBorderSize());
			}
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
		HBox buttonsTop1= new HBox(10);
		HBox buttonsTop2 = new HBox(10);
		buttonsTop2.getChildren().addAll(undoButton, redoButton);
		buttonsTop2.setPadding(new Insets(5));
		buttonsTop2.setStyle("-fx-background-color: #999");
		buttonsTop2.setAlignment(Pos.CENTER);
		buttonsTop1.getChildren().addAll(cutButton,copyButton,pasteButton);
		buttonsTop1.setPadding(new Insets(5));
		buttonsTop1.setStyle("-fx-background-color: #999");
		buttonsTop.getChildren().addAll(buttonsTop1, buttonsTop2);
		buttonsTop.setStyle("-fx-background-color: #999");
		buttonsTop.setPrefWidth(600);
		buttonsBox.getChildren().addAll(toolsArr);
		borderSlider.setShowTickMarks(true);
		borderSlider.setShowTickLabels(true);
		borderSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (selectedFigure != null) {
				selectedFigure.setBorderSize(newValue.doubleValue());
				redrawCanvas();
			}
		});
		lineColorPicker.setOnAction( event -> {
			if (selectedFigure != null) {
				selectedFigure.setLineColor(lineColorPicker.getValue());
				redrawCanvas();
			}
		});
		fillColorPicker.setOnAction( event -> {
			if (selectedFigure != null) {
				selectedFigure.setFillColor(fillColorPicker.getValue());
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
				newFigure = new Rectangle(startPoint, endPoint);
			}
			else if(circleButton.isSelected()) {
				double circleRadius = Math.abs(endPoint.getX() - startPoint.getX());
				newFigure = new Circle(startPoint, circleRadius);
			} else if(squareButton.isSelected()) {
				double size = Math.abs(endPoint.getX() - startPoint.getX());
				newFigure = new Square(startPoint, size);
			} else if(ellipseButton.isSelected()) {
				Point centerPoint = new Point(Math.abs(endPoint.getX() + startPoint.getX()) / 2, (Math.abs((endPoint.getY() + startPoint.getY())) / 2));
				double sMayorAxis = Math.abs(endPoint.getX() - startPoint.getX());
				double sMinorAxis = Math.abs(endPoint.getY() - startPoint.getY());
				newFigure = new Ellipse(centerPoint, sMayorAxis, sMinorAxis);
			} else {
				return ;
			}
			newFigure.setFillColor(fillColorPicker.getValue());
			newFigure.setLineColor(lineColorPicker.getValue());
			newFigure.setBorderSize(borderSlider.getValue());
			canvasState.addFigure(newFigure);
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
				for (Figure figure : canvasState.figures()) {
					if(figureBelongs(figure, eventPoint)) {
						clickedFigure = figure;
					}
				}
				if (clickedFigure != null && format != null) {
					clickedFigure.setFillColor(format.fill);
					clickedFigure.setLineColor(format.border);
					clickedFigure.setBorderSize(format.size);
					format = null;
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
			}
		});

		cutButton.setOnAction(event -> {
			if (selectedFigure != null) {
				auxFigure = selectedFigure;
				deleteButton.getOnAction().handle(event);
			}
		});

		copyButton.setOnAction(event -> {
			if (selectedFigure != null){
				auxFigure = selectedFigure;
				selectedFigure = null;
			}
		});
		pasteButton.setOnAction(event -> {
			if(auxFigure != null) {
				Figure newFigure;
				if(auxFigure instanceof Rectangle) {
					Rectangle aux = (Rectangle) auxFigure;
					double height = aux.getBottomRight().getX() - aux.getTopLeft().getX();
					double width = aux.getBottomRight().getY() - aux.getTopLeft().getY();
					Point topLeft = new Point(canvas.getWidth()/2 - height/2, canvas.getHeight()/2 - width/2);
					Point bottomRight = new Point(canvas.getWidth()/2 + height/2, canvas.getHeight()/2 + width/2);
					newFigure = new Rectangle(topLeft, bottomRight);
				} else if(auxFigure instanceof Ellipse) {
					Ellipse aux = (Ellipse) auxFigure;
					Point centerPoint = new Point( canvas.getWidth()/2, canvas.getHeight()/2);
					newFigure = new Ellipse(centerPoint, aux.getsMayorAxis(), aux.getsMinorAxis());
				} else {
					return ;
				}
				newFigure.setFillColor(auxFigure.getFillColor());
				newFigure.setLineColor(auxFigure.getLineColor());
				newFigure.setBorderSize(auxFigure.getBorderSize());
				canvasState.addFigure(newFigure);
				redrawCanvas();
				auxFigure = null;
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

	boolean figureBelongs(Figure figure, Point eventPoint) {
		return figure.belongs(eventPoint);
	}

	private static class Colors {
		Color border, fill;
		double size;
		public Colors(Color border, Color fill, double size) {
			this.border = border;
			this.fill = fill;
			this.size = size;
		}
	}

}
