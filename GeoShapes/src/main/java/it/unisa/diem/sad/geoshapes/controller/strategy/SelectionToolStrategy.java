package it.unisa.diem.sad.geoshapes.controller.strategy;

import it.unisa.diem.sad.geoshapes.controller.InteractionCallback;
import it.unisa.diem.sad.geoshapes.controller.ShapeMapping;
import it.unisa.diem.sad.geoshapes.decorator.SelectionDecorator;
import it.unisa.diem.sad.geoshapes.decorator.ShapeDecorator;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.input.MouseEvent;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;

import java.util.List;

public class SelectionToolStrategy implements ToolStrategy {

    private final Pane drawingArea;
    private final ShapeMapping shapeMapping;
    private ShapeDecorator currentDecorator;
    private MyShape selectedModelShape;
    private Shape selectedJavaFxShape;
    private InteractionCallback callback;


    //QUESTE MI DEFINISCONO SE STO FACENDO RESIZE, LE COORDINATE DEL PUNTO DI CLICK E LA POSIZIONE INIZIALE DELLA SHAPE
    private boolean isResizing = false;
    private Point2D initialMousePress;
    private Bounds initialShapeBounds;



    private ResizeHandleType activeHandleType = ResizeHandleType.NONE; // Tipo di maniglia che viene trascinata

    //QUA HO AGGIUNTO UN ENUM CHE MI SERVE A CAPIRE CHE CERCHIETTO HO CLICCATO E QUINDI CHE CALCOLI EFFETTUARE
    private enum ResizeHandleType {
        TOP_LEFT, TOP_CENTER, TOP_RIGHT,
        MIDDLE_LEFT, MIDDLE_RIGHT,
        BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT,LINE_START, LINE_END,
        NONE
    }


    public SelectionToolStrategy(Pane drawingArea, ShapeMapping shapeMapping, InteractionCallback callback) {
        this.drawingArea = drawingArea;
        this.shapeMapping = shapeMapping;
        this.callback = callback;
    }

    @Override
    public void handleMousePressed(MouseEvent event) {
       // if(isResizing){ return;} // potrei torglierlo

        double x = event.getX();
        double y = event.getY();


        //prima di controllare se ho cliccato una forma controllo se è un cerchietto
        Circle handleAtPosition=findHandleAt(x,y);
        if (handleAtPosition != null && selectedJavaFxShape != null) {
            isResizing = true;
            initialMousePress = new Point2D(x, y);
            initialShapeBounds = selectedJavaFxShape.getBoundsInParent();
            activeHandleType = ResizeHandleType.valueOf((String) handleAtPosition.getUserData());
            event.consume();
            return;
        }

        Shape shapeAtPosition = findShapeAt(x, y);

        if (shapeAtPosition == null) {
            reset();
            return;
        }
        //callback.setLineSelected(shapeAtPosition instanceof Line);
        drawingArea.setCursor(Cursor.HAND);

        if (shapeAtPosition != selectedJavaFxShape) {
            reset();
            selectedJavaFxShape = shapeAtPosition;
            selectedModelShape = shapeMapping.getModelShape(selectedJavaFxShape);

            currentDecorator = new SelectionDecorator(selectedJavaFxShape);
            currentDecorator.applyDecoration();

           // callback.setLineSelected(selectedJavaFxShape instanceof Line);
        }

        if (event.getButton() == MouseButton.SECONDARY && selectedJavaFxShape != null) {
            callback.onSelectionMenuOpened(selectedJavaFxShape, selectedModelShape, event.getX(), event.getY());
        }
    }


    @Override
    /*questa mi calcola le coordinate man mano che alalrgo la forma e aggiorna la java shape*/
    public void handleMouseDragged(MouseEvent event) {
        if (isResizing && selectedJavaFxShape != null) {
            double deltaX = event.getX() - initialMousePress.getX();
            double deltaY = event.getY() - initialMousePress.getY();
            updateJavaFxShapeDimensions(deltaX, deltaY, activeHandleType);
            event.consume();
        }
    }

        /*QUANDO LASCIO IL MOUSE MI PRENDO LA POSIZIONE FINALE DELLA FORMA E INVIO LA CALLBACK AL CONTROLLER
        CON LA POSIZIONE E LA FORMA. ESSA SI OCCUPERA' DI CHIAMARE IL COMANDO E AGGIORNARE IL MODELLO*/

    @Override
    public void handleMouseReleased(MouseEvent event) {
            if (isResizing && selectedJavaFxShape != null) {
                isResizing = false;
                drawingArea.setCursor(Cursor.DEFAULT);
                activeHandleType = ResizeHandleType.NONE;
                Bounds finalBounds = selectedJavaFxShape.getBoundsInParent();


                callback.onResizeShape(selectedJavaFxShape,initialShapeBounds, finalBounds);

                event.consume();
        }
    }



    /*questa è da rivedere*/
    @Override
    public void handleMouseMoved(MouseEvent event){}

    @Override
    public void handleBorderColorChange(Color color) {
        if (selectedModelShape != null){
            this.callback.onChangeBorderColor(selectedModelShape, color);
        }
    }

    @Override
    public void handleFillColorChange(Color color) {
        if (selectedModelShape != null){
            this.callback.onChangeFillColor(selectedModelShape, color);
        }
    }

    @Override
    public void reset() {
        if (currentDecorator != null) {
            currentDecorator.removeDecoration();
            currentDecorator = null;
        }
        selectedModelShape = null;
        selectedJavaFxShape = null;

       // callback.setLineSelected(false);

        //callback.onSelectionMenuClosed();
    }

    @Override
    public void activate(Color borderColor, Color fillColor){}

    private Shape findShapeAt(double x, double y) {
        List<javafx.scene.Node> children = drawingArea.getChildren();
        for (int i = children.size() - 1; i >= 0; i--) {
            javafx.scene.Node node = children.get(i);
            if (node instanceof Shape) {
                Shape shape = (Shape) node;
                if (shape.isVisible() && shape.contains(x, y)) {
                    return shape;
                }
            }
        }
        return null;
    }

    private Circle findHandleAt(double x, double y) {
        if (currentDecorator == null) return null;

        for (Circle handle : currentDecorator.getResizeHandles()) {
            if (handle.getBoundsInParent().contains(x, y)) {
                return handle;
            }
        }
        return null;
    }

    /*QUESTO METODO CONTROLLA CHE SIA SELEZIONATO EFFETTIVAMENTE SOLO UN "ANGOLO" E CALCOLA LA DIMENSIONE
    PORPORZIONALE RISPETTO A QUELLO OPPOSTO*/

    private void updateJavaFxShapeDimensions(double deltaX, double deltaY, ResizeHandleType handleType) {
        if (selectedJavaFxShape == null || initialShapeBounds == null) return;
        Point2D pivot = calculatePivotPoint(handleType, initialShapeBounds);
        if (pivot == null) return;
        ResizeCalculations calculations = calculateProportionalResize(deltaX, deltaY, handleType, initialShapeBounds, initialMousePress, pivot);
        applyDimensionsToFxShape(selectedJavaFxShape, calculations);

        // 4. Ridisegna le maniglie nella nuova posizione
        if (currentDecorator != null) {
            currentDecorator.removeDecoration();
            currentDecorator.applyDecoration();
            attachHandleListeners();
        }
    }


    //IL RECORD LO USO PER PASSARE QUESTO INSIEME DI VALORI ALLE VARIE FUNZIONI CHE MIAPPLICANO LE DIMENSIONI ALLE FORME
    private record ResizeCalculations(double newX, double newY, double newWidth, double newHeight) {}

    private Point2D calculatePivotPoint(ResizeHandleType handleType, Bounds initialBounds) {
        double pivotX = initialBounds.getMinX();
        double pivotY = initialBounds.getMinY();

        switch (handleType) {
            case TOP_LEFT:
                pivotX = initialBounds.getMaxX();
                pivotY = initialBounds.getMaxY();
                break;
            case TOP_RIGHT:
                pivotX = initialBounds.getMinX();
                pivotY = initialBounds.getMaxY();
                break;
            case BOTTOM_LEFT:
                pivotX = initialBounds.getMaxX();
                pivotY = initialBounds.getMinY();
                break;
            case BOTTOM_RIGHT:
                pivotX = initialBounds.getMinX();
                pivotY = initialBounds.getMinY();
                break;
            default:
                return null;
        }
        return new Point2D(pivotX, pivotY);
    }

    //QUESTO EFFETTUA I CALCOLI DI RIDIMENSIONAMENTO PROPORZIONALI ( NOTA CHE SE è UNA LINEA DEVE ESSERE GESTITA DIVERSAMENTE IN QUANTO LA DEVI SOLO "ALLUNGARE " O "STIRNGERE"
        private ResizeCalculations calculateProportionalResize(double deltaX, double deltaY, ResizeHandleType handleType, Bounds initialBounds, Point2D initialMouse, Point2D pivot) {
            if (selectedJavaFxShape instanceof Line fxLine) {
                double newStartX = fxLine.getStartX();
                double newStartY = fxLine.getStartY();
                double newEndX = fxLine.getEndX();
                double newEndY = fxLine.getEndY();

                if (handleType == ResizeHandleType.LINE_START) {
                    newStartX = initialMouse.getX() + deltaX;
                    newStartY = initialMouse.getY() + deltaY;
                } else if (handleType == ResizeHandleType.LINE_END) {
                    newEndX = initialMouse.getX() + deltaX;
                    newEndY = initialMouse.getY() + deltaY;
                }

                double minX = Math.min(newStartX, newEndX);
                double maxX = Math.max(newStartX, newEndX);
                double minY = Math.min(newStartY, newEndY);
                double maxY = Math.max(newStartY, newEndY);

                return new ResizeCalculations(minX, minY, maxX - minX, maxY - minY);

            } else {

                double originalWidth = initialBounds.getWidth();
                double originalHeight = initialBounds.getHeight();
                final double MIN_SIZE = 5.0;

                double currentMouseX = initialMouse.getX() + deltaX;
                double currentMouseY = initialMouse.getY() + deltaY;

                double distInitialToPivot = initialMouse.distance(pivot.getX(), pivot.getY());
                double distMouseToPivot = new Point2D(currentMouseX, currentMouseY).distance(pivot.getX(), pivot.getY());

                double scaleFactor = (distInitialToPivot == 0) ? 1.0 : distMouseToPivot / distInitialToPivot;

                double newWidth = originalWidth * scaleFactor;
                double newHeight = originalHeight * scaleFactor;

                if (newWidth < MIN_SIZE) {
                    newWidth = MIN_SIZE;
                    newHeight = originalHeight * (MIN_SIZE / originalWidth);
                }
                if (newHeight < MIN_SIZE) {
                    newHeight = MIN_SIZE;
                    newWidth = originalWidth * (MIN_SIZE / originalHeight);
                }

                double newX = pivot.getX();
                double newY = pivot.getY();

                if (handleType == ResizeHandleType.TOP_LEFT || handleType == ResizeHandleType.BOTTOM_LEFT) {
                    newX -= newWidth;
                }

                if (handleType == ResizeHandleType.TOP_LEFT || handleType == ResizeHandleType.TOP_RIGHT) {
                    newY -= newHeight;
                }

                return new ResizeCalculations(newX, newY, newWidth, newHeight);
            }
        }



    private void applyDimensionsToFxShape(Shape fxShape, ResizeCalculations calc) {
        switch (fxShape) {
            case Rectangle fxRect -> applyDimensionsToRectangle(fxRect, calc);
            case Ellipse fxEllipse -> applyDimensionsToEllipse(fxEllipse, calc);
            case Line fxLine -> applyDimensionsToLine(fxLine, calc);
            default -> System.err.println("Resizing Error");
        }
    }

    // Applica dimensioni a un Rectangle JavaFX
    private void applyDimensionsToRectangle(Rectangle fxRect, ResizeCalculations calc) {
        fxRect.setX(calc.newX);
        fxRect.setY(calc.newY);
        fxRect.setWidth(calc.newWidth);
        fxRect.setHeight(calc.newHeight);
    }

    // Applica dimensioni a un Ellipse JavaFX
    private void applyDimensionsToEllipse(javafx.scene.shape.Ellipse fxEllipse, ResizeCalculations calc) {
        double centerX = calc.newX + calc.newWidth / 2;
        double centerY = calc.newY + calc.newHeight / 2;
        double radiusX = calc.newWidth / 2;
        double radiusY = calc.newHeight / 2;
        fxEllipse.setCenterX(centerX);
        fxEllipse.setCenterY(centerY);
        fxEllipse.setRadiusX(radiusX);
        fxEllipse.setRadiusY(radiusY);
    }

    private void applyDimensionsToLine(Line fxLine, ResizeCalculations calc) {
        double originalStartX_rel = fxLine.getStartX() - initialShapeBounds.getMinX();
        double originalStartY_rel = fxLine.getStartY() - initialShapeBounds.getMinY();
        double originalEndX_rel = fxLine.getEndX() - initialShapeBounds.getMinX();
        double originalEndY_rel = fxLine.getEndY() - initialShapeBounds.getMinY();

        // Evita divisione per zero se la dimensione iniziale era 0
        double scaleX = initialShapeBounds.getWidth() == 0 ? 1.0 : calc.newWidth / initialShapeBounds.getWidth();
        double scaleY = initialShapeBounds.getHeight() == 0 ? 1.0 : calc.newHeight / initialShapeBounds.getHeight();

        double scaledStartX_rel = originalStartX_rel * scaleX;
        double scaledStartY_rel = originalStartY_rel * scaleY;
        double scaledEndX_rel = originalEndX_rel * scaleX;
        double scaledEndY_rel = originalEndY_rel * scaleY;

        fxLine.setStartX(calc.newX + scaledStartX_rel);
        fxLine.setStartY(calc.newY + scaledStartY_rel);
        fxLine.setEndX(calc.newX + scaledEndX_rel);
        fxLine.setEndY(calc.newY + scaledEndY_rel);
    }


    private void attachHandleListeners() {
        if (currentDecorator != null) {
            for (Circle handle : currentDecorator.getResizeHandles()) {
                handle.setOnMousePressed(this::handleMousePressed);
                handle.setOnMouseDragged(this::handleMouseDragged);
                handle.setOnMouseReleased(this::handleMouseReleased);
            }
        }
    }
}
