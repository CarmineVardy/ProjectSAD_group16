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
import javafx.geometry.Point2D; // Già importato, ma lo indico per chiarezza

import java.util.List;

public class SelectionToolStrategy implements ToolStrategy {

    private final Pane drawingArea;
    private final ShapeMapping shapeMapping;
    private SelectionDecorator currentDecorator; // Deve essere SelectionDecorator per accedere a getResizeHandles()
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
        BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT, LINE_START, LINE_END,
        NONE
    }


    public SelectionToolStrategy(Pane drawingArea, ShapeMapping shapeMapping, InteractionCallback callback) {
        this.drawingArea = drawingArea;
        this.shapeMapping = shapeMapping;
        this.callback = callback;
    }

    @Override
    public void handleMousePressed(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();

        Circle handleAtPosition = findHandleAt(x, y);
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
            reset(); // Se clicco fuori, resetta la selezione
            return;
        }
        drawingArea.setCursor(Cursor.HAND);

        if (shapeAtPosition != selectedJavaFxShape) {
            reset(); // Prima di selezionare una nuova forma, resetta la precedente
            selectedJavaFxShape = shapeAtPosition;
            selectedModelShape = shapeMapping.getModelShape(selectedJavaFxShape);

            currentDecorator = new SelectionDecorator(selectedJavaFxShape);
            currentDecorator.applyDecoration();
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

            callback.onResizeShape(selectedJavaFxShape, initialShapeBounds, finalBounds);

            // Dopo il ridimensionamento, ricarica le maniglie
            if (currentDecorator != null) {
                currentDecorator.removeDecoration();
                currentDecorator.applyDecoration();
            }

            event.consume();
        }
    }


    @Override
    public void handleMouseMoved(MouseEvent event) {
        // Se stiamo già ridimensionando, non cambiare il cursore
        if (isResizing) {
            return;
        }

        // Trova se il mouse è sopra una maniglia
        Circle handleAtPosition = findHandleAt(event.getX(), event.getY());

        if (handleAtPosition != null) {
            // Se il mouse è su una maniglia, cambia il cursore
            drawingArea.setCursor(Cursor.CROSSHAIR); // O un cursore più specifico
        } else {
            // Se il mouse non è su una maniglia, imposta il cursore di default
            if (selectedJavaFxShape != null && selectedJavaFxShape.contains(event.getX(), event.getY())) {
                drawingArea.setCursor(Cursor.HAND); // Se è sulla forma selezionata, metti HAND
            } else {
                drawingArea.setCursor(Cursor.DEFAULT); // Altrimenti, default
            }
        }
    }


    @Override
    public void handleBorderColorChange(Color color) {
        if (selectedModelShape != null) {
            this.callback.onChangeBorderColor(selectedModelShape, color);
        }
    }

    @Override
    public void handleFillColorChange(Color color) {
        if (selectedModelShape != null) {
            this.callback.onChangeFillColor(selectedModelShape, color);
        }
    }

    @Override
    public void reset() {
        if (currentDecorator != null) {
            currentDecorator.removeDecoration();
            // IMPORTANTE: Resetta il cursore di default quando non c'è selezione
            drawingArea.setCursor(Cursor.DEFAULT);
            currentDecorator = null;
        }
        selectedModelShape = null;
        selectedJavaFxShape = null;
    }

    @Override
    public void activate(Color borderColor, Color fillColor) {
    }

    private Shape findShapeAt(double x, double y) {
        List<javafx.scene.Node> children = drawingArea.getChildren();
        for (int i = children.size() - 1; i >= 0; i--) {
            javafx.scene.Node node = children.get(i);
            // Ignora le maniglie di ridimensionamento dal trovare forme sotto
            if (currentDecorator != null && node instanceof Circle && currentDecorator.getResizeHandles().contains(node)) {
                continue;
            }
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
            if (handle.isVisible() && handle.getBoundsInParent().contains(x, y)) {
                return handle;
            }
        }
        return null;
    }

    private void updateJavaFxShapeDimensions(double deltaX, double deltaY, ResizeHandleType handleType) {
        if (selectedJavaFxShape == null || initialShapeBounds == null) return;
        Point2D pivot = calculatePivotPoint(handleType, initialShapeBounds);
        if (pivot == null) return;
        ResizeCalculations calculations = calculateProportionalResize(deltaX, deltaY, handleType, initialShapeBounds, initialMousePress, pivot);
        applyDimensionsToFxShape(selectedJavaFxShape, calculations);

        // Rimuove e riapplica il decoratore per aggiornare la posizione delle maniglie
        if (currentDecorator != null) {
            currentDecorator.removeDecoration();
            currentDecorator.applyDecoration();
        }
    }


    //IL RECORD LO USO PER PASSARE QUESTO INSIEME DI VALORI ALLE VARIE FUNZIONI CHE MIAPPLICANO LE DIMENSIONI ALLE FORME
    private record ResizeCalculations(double newX, double newY, double newWidth, double newHeight) {
    }

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
            case LINE_START: // Il pivot per LINE_START è l'altro capo della linea
                // Per le linee, il pivot è l'altro estremo della linea
                if (selectedJavaFxShape instanceof Line fxLine) {
                    pivotX = fxLine.getEndX();
                    pivotY = fxLine.getEndY();
                }
                break;
            case LINE_END: // Il pivot per LINE_END è l'altro capo della linea
                if (selectedJavaFxShape instanceof Line fxLine) {
                    pivotX = fxLine.getStartX();
                    pivotY = fxLine.getStartY();
                }
                break;
            default:
                return null;
        }
        return new Point2D(pivotX, pivotY);
    }

    private ResizeCalculations calculateProportionalResize(double deltaX, double deltaY, ResizeHandleType handleType, Bounds initialBounds, Point2D initialMouse, Point2D pivot) {
        if (selectedJavaFxShape instanceof Line fxLine) {
            double newStartX = fxLine.getStartX();
            double newStartY = fxLine.getStartY();
            double newEndX = fxLine.getEndX();
            double newEndY = fxLine.getEndY();

            // Calcola il punto finale della maniglia (dove il mouse è ora)
            double currentHandleX = initialMouse.getX() + deltaX;
            double currentHandleY = initialMouse.getY() + deltaY;

            if (handleType == ResizeHandleType.LINE_START) {
                newStartX = currentHandleX;
                newStartY = currentHandleY;
                newEndX = pivot.getX(); // L'altro punto è il pivot
                newEndY = pivot.getY();
            } else if (handleType == ResizeHandleType.LINE_END) {
                newStartX = pivot.getX(); // L'altro punto è il pivot
                newStartY = pivot.getY();
                newEndX = currentHandleX;
                newEndY = currentHandleY;
            }

            // Restituiamo direttamente i nuovi startX, startY, endX, endY.
            // applyDimensionsToLine li interpreterà correttamente.
            return new ResizeCalculations(newStartX, newStartY, newEndX, newEndY);

        } else {
            double originalWidth = initialBounds.getWidth();
            double originalHeight = initialBounds.getHeight();
            final double MIN_SIZE = 5.0;

            double currentMouseX = initialMouse.getX() + deltaX;
            double currentMouseY = initialMouse.getY() + deltaY;

            // Per la ridimensionamento, calcoliamo le nuove dimensioni basandoci sulla distanza dal pivot
            double newWidth = originalWidth;
            double newHeight = originalHeight;

            switch (handleType) {
                case TOP_LEFT:
                case BOTTOM_RIGHT:
                case TOP_RIGHT:
                case BOTTOM_LEFT:
                    // Ridimensionamento proporzionale da un angolo
                    double deltaFromPivotX = currentMouseX - pivot.getX();
                    double deltaFromPivotY = currentMouseY - pivot.getY();

                    // Mantiene la proporzione
                    double ratio = originalWidth / originalHeight;
                    newWidth = Math.abs(deltaFromPivotX);
                    newHeight = newWidth / ratio; // Calcola l'altezza in base alla nuova larghezza e al ratio

                    if (newHeight < MIN_SIZE) { // Se l'altezza scende troppo, la fissiamo e ricalcoliamo la larghezza
                        newHeight = MIN_SIZE;
                        newWidth = MIN_SIZE * ratio;
                    }
                    if (newWidth < MIN_SIZE) { // Se la larghezza scende troppo, la fissiamo e ricalcoliamo l'altezza
                        newWidth = MIN_SIZE;
                        newHeight = MIN_SIZE / ratio;
                    }
                    break;
                case TOP_CENTER:
                case BOTTOM_CENTER:
                    newHeight = Math.max(MIN_SIZE, Math.abs(currentMouseY - pivot.getY()));
                    newWidth = originalWidth; // Larghezza non cambia per maniglie centrali verticali
                    break;
                case MIDDLE_LEFT:
                case MIDDLE_RIGHT:
                    newWidth = Math.max(MIN_SIZE, Math.abs(currentMouseX - pivot.getX()));
                    newHeight = originalHeight; // Altezza non cambia per maniglie centrali orizzontali
                    break;
                default:
                    // Non dovremmo arrivare qui
                    return new ResizeCalculations(initialBounds.getMinX(), initialBounds.getMinY(), originalWidth, originalHeight);
            }

            // Calcola le nuove coordinate X e Y in base al pivot e alle nuove dimensioni
            double newX = pivot.getX();
            double newY = pivot.getY();

            // Aggiusta X e Y in base al tipo di maniglia per mantenere il pivot fisso
            if (handleType == ResizeHandleType.TOP_LEFT || handleType == ResizeHandleType.BOTTOM_LEFT || handleType == ResizeHandleType.MIDDLE_LEFT) {
                newX = pivot.getX() - newWidth;
            }
            if (handleType == ResizeHandleType.TOP_LEFT || handleType == ResizeHandleType.TOP_RIGHT || handleType == ResizeHandleType.TOP_CENTER) {
                newY = pivot.getY() - newHeight;
            }

            return new ResizeCalculations(newX, newY, newWidth, newHeight);
        }
    }


    private void applyDimensionsToFxShape(Shape fxShape, ResizeCalculations calc) {
        // Conviene usare if-else if per evitare il workaround dello switch su oggetto in Java 8/11
        if (fxShape instanceof Rectangle fxRect) {
            applyDimensionsToRectangle(fxRect, calc);
        } else if (fxShape instanceof Ellipse fxEllipse) {
            applyDimensionsToEllipse(fxEllipse, calc);
        } else if (fxShape instanceof Line fxLine) {
            applyDimensionsToLine(fxLine, calc);
        } else {
            System.err.println("Resizing Error: Unsupported JavaFX shape type for applying dimensions.");
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
        // PER LE LINEE, calc.newX, calc.newY sono startX, startY.
        // E calc.newWidth, calc.newHeight sono endX, endY.
        fxLine.setStartX(calc.newX);
        fxLine.setStartY(calc.newY);
        fxLine.setEndX(calc.newWidth);
        fxLine.setEndY(calc.newHeight);
    }

    private void attachHandleListeners() {
        // Questo metodo non è più necessario, la sua logica è stata spostata e semplificata.
        // Puoi anche rimuoverlo completamente se non viene chiamato da nessun'altra parte.
    }
}