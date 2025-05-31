package it.unisa.diem.sad.geoshapes.decorator;

import javafx.geometry.Bounds; // Import Bounds
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;

import java.util.ArrayList;
import java.util.List;


public class SelectionDecorator implements ShapeDecorator {


    private final Shape shape;
    private Color originalStrokeColor;
    private double originalStrokeWidth;
    private StrokeType originalStrokeType;
    private double originalOpacity;
    private Paint originalFill;
    private Circle rotationHandle;
    private static final double ROTATION_HANDLE_OFFSET = 30;

    //dichiarazioni che mi servono per il resize il resto lo metto tutto sotto cosi da vedere facilmente le modifiche relative
    private final Pane drawingArea;
    private final List<Circle> resizeHandles;
    private static final double HANDLE_SIZE = 8;
    private static final double HANDLE_OFFSET = HANDLE_SIZE / 2;


    public SelectionDecorator(Shape shape) {
        this.shape = shape;
        this.resizeHandles = new ArrayList<Circle>();
        if (shape.getParent() instanceof Pane) {
            this.drawingArea = (Pane) shape.getParent();
        } else {

            throw new IllegalArgumentException("The parent of the shape must be a Pane for SelectionDecorator.");
        }
    }


    //QUA TENIAMO TUTTA LA ROBA VECCHIA
    @Override
    public void applyDecoration() {
        storeOriginalProperties();

        shape.setStroke(Color.GREEN);
        shape.setStrokeWidth(originalStrokeWidth + 0.5);
        shape.setStrokeType(StrokeType.OUTSIDE);
        createAndAddResizeHandles(); //QUESTO MI INIZIALIZZA I CERCHIETTI

        if (originalFill instanceof Color originalColor) {
            if (originalColor.getOpacity() > 0.0) {
                Color newColor = new Color(originalColor.getRed(), originalColor.getGreen(), originalColor.getBlue(), 0.7);
                shape.setFill(newColor);
            }
        }
    }


    private void storeOriginalProperties() {
        originalStrokeColor = (Color) shape.getStroke();
        originalStrokeWidth = shape.getStrokeWidth();
        originalStrokeType = shape.getStrokeType();
        originalOpacity = shape.getOpacity();
        originalFill = shape.getFill();
    }


    @Override
    public void removeDecoration() {

        shape.setStroke(originalStrokeColor);
        shape.setStrokeWidth(originalStrokeWidth);
        shape.setStrokeType(originalStrokeType);
        shape.setFill(originalFill);
        shape.setOpacity(originalOpacity);
        removeResizeHandles(); // QUESTO RIMUOVE I CERCHIETTI
    }


    @Override
    public Shape getDecoratedShape() {
        return shape;
    }

    //QUA CI METTO TUTTA LA ROBA NUOVA PER IL RESIZE


    //QUESTO RIMUOVE I CERCHIETTI ( L'IF FORSE è INUTILE DATO CHE IL CHECK GIA LO FACCIO NEL CONSTRUCTOR?)
    private void removeResizeHandles() {
        if (drawingArea != null) {
            drawingArea.getChildren().removeAll(resizeHandles);
        }
        resizeHandles.clear();
    }

    //QUESTO PERMETTE DI FAR VEDERE I CERCHIETTI ALLA STRATEGIA: è NECESSARIO PERCHE IL DECORATOR OPERA SOLO "GRAFICAMENTE
    //NELLA STRATEGIA INVECE MI GESTISCO GLI EVENTI SUL MOUSE QUANDO CLICCO SUI CERCHIETTI

    public List<Circle> getResizeHandles() {
        return resizeHandles;
    }

    /*QUESTA FUNZIONE MI CREA I CERCHIETTI DEL RESIZE, CONTROLLA CHE LA DRAWINGAREA NON è NULL
     * 1) CALCOLA LA POSIZIONE DELLA SHAPE ( BOUNDS)
     * 2)DEFINISCE LE POSIZIONI DEI CERCHIETTI
     * 3)CALCOLA LE COORDINATE E AGGIUNGE I CERCHIETTI ( NEL CICLO FOR ) AL PANE
     * */
    private void createAndAddResizeHandles() {
        if (drawingArea == null) {
            System.err.println("Parent Pane is null for shape: " + shape + ". Cannot add resize handles.");
            return;
        }

        Bounds bounds = shape.getBoundsInParent();
        double circleRadius = HANDLE_SIZE / 2;
        rotationHandle = new Circle(circleRadius, Color.DARKORANGE); // Colore distintivo per la rotazione
        rotationHandle.setStroke(Color.WHITE);
        rotationHandle.setStrokeWidth(1);
        rotationHandle.setUserData("ROTATION"); // UserData per identificarlo nella strategia
        resizeHandles.add(rotationHandle);
        double centerX = bounds.getMinX() + bounds.getWidth() / 2;
        double centerY = bounds.getMinY() - ROTATION_HANDLE_OFFSET; // Posiziona sopra la forma

        rotationHandle.setCenterX(centerX);
        rotationHandle.setCenterY(centerY);
        drawingArea.getChildren().add(rotationHandle);

        String shapeCategory;
        if (shape instanceof Line) {
            shapeCategory = "LINE";
        } else if (shape instanceof Rectangle || shape instanceof Ellipse) {
            shapeCategory = "RECT_ELLIPSE";
        } else {
            shapeCategory = "OTHER";
        }

        switch (shapeCategory) {
            case "LINE":
                Line fxLine = (Line) shape; // Cast esplicito

                // Maniglia per il punto iniziale della linea
                Circle handleStart = new Circle(circleRadius, Color.BLUE);
                handleStart.setStroke(Color.WHITE);
                handleStart.setStrokeWidth(1);
                handleStart.setCenterX(fxLine.getStartX());
                handleStart.setCenterY(fxLine.getStartY());
                handleStart.setUserData("LINE_START");
                resizeHandles.add(handleStart);
                drawingArea.getChildren().add(handleStart);

                // Maniglia per il punto finale della linea
                Circle handleEnd = new Circle(circleRadius, Color.BLUE);
                handleEnd.setStroke(Color.WHITE);
                handleEnd.setStrokeWidth(1);
                handleEnd.setCenterX(fxLine.getEndX());
                handleEnd.setCenterY(fxLine.getEndY());
                handleEnd.setUserData("LINE_END");
                resizeHandles.add(handleEnd);
                drawingArea.getChildren().add(handleEnd);
                break;

            case "RECT_ELLIPSE":
                // Non abbiamo bisogno di castare a Rectangle o Ellipse qui
                // perché le maniglie si basano sui Bounds generici
                String[] handleTypes = {
                        "TOP_LEFT", "TOP_CENTER", "TOP_RIGHT",
                        "MIDDLE_LEFT", "MIDDLE_RIGHT",
                        "BOTTOM_LEFT", "BOTTOM_CENTER", "BOTTOM_RIGHT"
                };

                double[] xCoords = {
                        bounds.getMinX(), bounds.getMinX() + bounds.getWidth() / 2, bounds.getMaxX(),
                        bounds.getMinX(), bounds.getMaxX(),
                        bounds.getMinX(), bounds.getMinX() + bounds.getWidth() / 2, bounds.getMaxX()
                };

                double[] yCoords = {
                        bounds.getMinY(), bounds.getMinY(), bounds.getMinY(),
                        bounds.getMinY() + bounds.getHeight() / 2, bounds.getMinY() + bounds.getHeight() / 2,
                        bounds.getMaxY(), bounds.getMaxY(), bounds.getMaxY()
                };

                for (int i = 0; i < handleTypes.length; i++) {
                    Circle handle = new Circle(circleRadius, Color.BLUE);
                    handle.setStroke(Color.WHITE);
                    handle.setStrokeWidth(1);
                    handle.setCenterX(xCoords[i]);
                    handle.setCenterY(yCoords[i]);
                    handle.setUserData(handleTypes[i]);
                    resizeHandles.add(handle);
                    drawingArea.getChildren().add(handle);
                }
                break;

            default:
                System.err.println("Unsupported shape type for resize handles: " + shape.getClass().getSimpleName());
                break;
        }
    }
}
