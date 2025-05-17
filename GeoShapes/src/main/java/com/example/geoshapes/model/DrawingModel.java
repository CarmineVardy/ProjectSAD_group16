package com.example.geoshapes.model;

import com.example.geoshapes.model.factory.ShapeFactory;
import com.example.geoshapes.model.shapes.Shape;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class DrawingModel {

    // Lista inizializzata direttamente per evitare NullPointerException
    private List<Shape> shapes = new ArrayList<>();
    private Shape currentShape;
    private ShapeFactory currentFactory;

    // Colori correnti usati per il disegno
    private Color currentBorderColor = Color.BLACK;
    private Color currentFillColor = Color.TRANSPARENT;

    // Imposta la factory corrente (linea, ellisse, ecc.)
    public void setCurrentFactory(ShapeFactory currentFactory) {
        this.currentFactory = currentFactory;
    }

    // Imposta il colore del bordo corrente
    public void setBorderColor(Color borderColor) {
        this.currentBorderColor = borderColor;
    }

    // Imposta il colore di riempimento corrente
    public void setFillColor(Color fillColor) {
        this.currentFillColor = fillColor;
    }

    // Inizia a disegnare una forma (mouse premuto)
    public void startDrawing(double startX, double startY) {
        if (currentFactory != null) {
            currentShape = currentFactory.createShape(startX, startY, currentBorderColor, currentFillColor);
        }
    }

    // Aggiorna i dati della forma mentre si trascina il mouse
    public void updateDrawing(double x, double y) {
        if (currentShape != null) {
            currentShape.setEndPoint(x, y);
        }
    }

    // Completa la forma (mouse rilasciato)
    public void endDrawing(double x, double y) {
        if (currentShape != null) {
            currentShape.setEndPoint(x, y);
            shapes.add(currentShape);
            currentShape = null;
        }
    }

    // Restituisce la forma corrente in corso di disegno
    public Shape getCurrentShape() {
        return currentShape;
    }

    // Disegna tutte le forme più l'anteprima della forma corrente
    public void drawShapes(GraphicsContext gc) {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        for (Shape shape : shapes) {
            shape.draw(gc);
        }

        if (currentShape != null) {
            currentShape.drawPreview(gc);
        }
    }

    // Dice se si sta attualmente disegnando qualcosa
    public boolean isDrawing() {
        return currentShape != null;
    }
}
