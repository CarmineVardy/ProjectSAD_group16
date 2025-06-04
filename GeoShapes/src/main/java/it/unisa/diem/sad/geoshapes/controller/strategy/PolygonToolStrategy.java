package it.unisa.diem.sad.geoshapes.controller.strategy;

import it.unisa.diem.sad.geoshapes.controller.InteractionCallback;
import it.unisa.diem.sad.geoshapes.controller.decorator.PreviewShapeDecorator;
import it.unisa.diem.sad.geoshapes.controller.decorator.ShapeDecorator;
import javafx.event.Event;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PolygonToolStrategy implements ToolStrategy{

    private final Pane drawingArea;
    private final InteractionCallback callback;

    private Color borderColor;
    private Color fillColor;
    private int polygonVertices;
    private boolean regularPolygon;

    private Point2D firstPoint;
    private Point2D secondPoint;
    private Circle firstPointPreview;
    private ShapeDecorator firstPointDecorator;

    // Per poligoni irregolari
    private List<Point2D> vertices;
    private List<Circle> vertexPreviews;
    private List<Line> edgePreviews;
    private List<ShapeDecorator> previewDecorators;

    private static final double POINT_RADIUS = 3.0;
    private static final double MIN_SIDE_LENGTH = 10.0;


    public PolygonToolStrategy(Pane drawingArea, InteractionCallback callback) {
        this.drawingArea = drawingArea;
        this.callback = callback;
        this.vertices = new ArrayList<>();
        this.vertexPreviews = new ArrayList<>();
        this.edgePreviews = new ArrayList<>();
        this.previewDecorators = new ArrayList<>();
    }

    @Override
    public void activate(Color lineBorderColor, Color rectangleBorderColor, Color rectangleFillColor, Color ellipseBorderColor, Color ellipseFillColor, Color polygonBorderColor, Color polygonFillColor, Color textBorderColor, Color textFillColor, Color textColor, int polygonVertices, boolean regularPolygon, int fontSize) {
        this.borderColor = polygonBorderColor;
        this.fillColor = polygonFillColor;
        this.polygonVertices = polygonVertices;
        this.regularPolygon = regularPolygon;
    }

    @Override
    public void handleMousePressed(MouseEvent event) {
        drawingArea.setCursor(Cursor.CROSSHAIR);
        Point2D localPoint = drawingArea.sceneToLocal(event.getSceneX(), event.getSceneY());

        if (regularPolygon) {
            handleRegularPolygonClick(localPoint);
        } else {
            handleIrregularPolygonClick(localPoint);
        }

    }

    private void handleRegularPolygonClick(Point2D point) {
        if (firstPoint == null) {
            // Primo punto
            firstPoint = point;
            createFirstPointPreview(point);
        } else {
            // Secondo punto - crea il poligono regolare
            secondPoint = point;
            double sideLength = firstPoint.distance(secondPoint);

            if (sideLength >= MIN_SIDE_LENGTH) {
                Polygon polygon = createRegularPolygon(firstPoint, secondPoint, polygonVertices);
                polygon.setStroke(borderColor);
                polygon.setFill(fillColor);
                polygon.setStrokeWidth(2.0);

                callback.onCreateShape(polygon);
                reset();
            } else {
                // Lato troppo piccolo, reset
                reset();
            }
        }
    }

    private void handleIrregularPolygonClick(Point2D point) {
        // Controlla se l'utente ha cliccato vicino al primo punto per chiudere (solo se ha almeno 3 vertici)
        if (vertices.size() >= 3 && isNearFirstVertex(point)) {
            // Chiudi il poligono
            createIrregularPolygon();
            return;
        }

        // Aggiungi nuovo vertice se non hai ancora raggiunto il numero desiderato
        if (vertices.size() < polygonVertices) {
            vertices.add(point);
            createVertexPreview(point);

            // Crea lato se non è il primo punto
            if (vertices.size() > 1) {
                Point2D prevPoint = vertices.get(vertices.size() - 2);
                createEdgePreview(prevPoint, point);
            }

            // Se hai raggiunto il numero di vertici desiderato, chiudi automaticamente il poligono
            if (vertices.size() == polygonVertices) {
                createIrregularPolygon();
            }
        }
    }

    private void createFirstPointPreview(Point2D point) {
        Circle circle = new Circle(point.getX(), point.getY(), POINT_RADIUS);
        circle.setStroke(borderColor);
        circle.setFill(borderColor);
        circle.setStrokeWidth(1.0);

        firstPointPreview = circle;
        firstPointDecorator = new PreviewShapeDecorator(circle);
        firstPointDecorator.applyDecoration();

        drawingArea.getChildren().add(circle);
    }

    private void createVertexPreview(Point2D point) {
        Circle circle = new Circle(point.getX(), point.getY(), POINT_RADIUS);
        circle.setStroke(borderColor);
        circle.setFill(borderColor);
        circle.setStrokeWidth(1.0);

        ShapeDecorator decorator = new PreviewShapeDecorator(circle);
        decorator.applyDecoration();

        vertexPreviews.add(circle);
        previewDecorators.add(decorator);
        drawingArea.getChildren().add(circle);
    }

    private void createEdgePreview(Point2D start, Point2D end) {
        Line line = new Line(start.getX(), start.getY(), end.getX(), end.getY());
        line.setStroke(borderColor);
        line.setStrokeWidth(1.0);

        ShapeDecorator decorator = new PreviewShapeDecorator(line);
        decorator.applyDecoration();

        edgePreviews.add(line);
        previewDecorators.add(decorator);
        drawingArea.getChildren().add(line);
    }

    private boolean isNearFirstVertex(Point2D point) {
        if (vertices.isEmpty()) return false;
        Point2D firstVertex = vertices.get(0);
        return point.distance(firstVertex) <= POINT_RADIUS * 2;
    }

    private void createIrregularPolygon() {
        if (vertices.size() < 3) return;

        Polygon polygon = new Polygon();
        for (Point2D vertex : vertices) {
            polygon.getPoints().addAll(vertex.getX(), vertex.getY());
        }

        polygon.setStroke(borderColor);
        polygon.setFill(fillColor);
        polygon.setStrokeWidth(2.0);

        callback.onCreateShape(polygon);
        reset();
    }

    private Polygon createRegularPolygon(Point2D firstPoint, Point2D secondPoint, int sides) {
        Polygon polygon = new Polygon();

        // Il primo lato è definito dai due punti cliccati
        polygon.getPoints().addAll(firstPoint.getX(), firstPoint.getY());
        polygon.getPoints().addAll(secondPoint.getX(), secondPoint.getY());

        // Calcola lunghezza del lato
        double sideLength = firstPoint.distance(secondPoint);

        // Calcola l'angolo del primo lato
        double sideAngle = Math.atan2(secondPoint.getY() - firstPoint.getY(),
                secondPoint.getX() - firstPoint.getX());

        // Calcola l'angolo interno del poligono regolare
        double interiorAngle = Math.PI * (sides - 2) / sides;

        // Genera i vertici rimanenti
        Point2D currentPoint = secondPoint;
        double currentAngle = sideAngle;

        for (int i = 2; i < sides; i++) {
            // Ruota l'angolo per il prossimo lato
            currentAngle += Math.PI - interiorAngle;

            // Calcola il prossimo vertice
            double nextX = currentPoint.getX() + sideLength * Math.cos(currentAngle);
            double nextY = currentPoint.getY() + sideLength * Math.sin(currentAngle);

            currentPoint = new Point2D(nextX, nextY);
            polygon.getPoints().addAll(nextX, nextY);
        }

        return polygon;
    }

    @Override
    public void handleMouseDragged(MouseEvent event) {

    }

    @Override
    public void handleMouseReleased(MouseEvent event) {
        drawingArea.setCursor(Cursor.DEFAULT);

    }

    @Override
    public void handleMouseMoved(MouseEvent event) {

    }

    @Override
    public void handleLineBorderColorChange(Color color) {

    }

    @Override
    public void handleRectangleBorderColorChange(Color color) {

    }

    @Override
    public void handleRectangleFillColorChange(Color color) {

    }

    @Override
    public void handleEllipseBorderColorChange(Color color) {

    }

    @Override
    public void handleEllipseFillColorChange(Color color) {

    }

    @Override
    public void handlePolygonBorderColorChange(Color color) {
        this.borderColor = color;
    }

    @Override
    public void handlePolygonFillColorChange(Color color) {
        this.fillColor = color;
    }

    @Override
    public void handleTextBorderColorChange(Color color) {

    }

    @Override
    public void handleTextFillColorChange(Color color) {

    }

    @Override
    public void handleTextColorChange(Color color) {

    }

    @Override
    public void handlePolygonVerticesChange(int polygonVertices) {
        this.polygonVertices = polygonVertices;
        reset();
    }

    @Override
    public void handleRegularPolygon(boolean regularPolygon) {
        this.regularPolygon = regularPolygon;
        reset();
    }

    @Override
    public void handleFontSizeChange(int fontSize) {

    }

    @Override
    public void handleKeyPressed(KeyEvent event) {

    }

    @Override
    public void handleKeyTyped(KeyEvent event) {

    }

    @Override
    public void handleBorderColorChange(Color color) {

    }

    @Override
    public void handleFillColorChange(Color color) {

    }

    @Override
    public void handleTextColorMenuChange(Color color) {

    }

    @Override
    public void handleFontSizeMenuChange(int fontSize) {

    }

    @Override
    public List<Shape> getSelectedShapes() {
        return Collections.emptyList();
    }

    @Override
    public void reset() {
        // Reset per poligoni regolari
        if (firstPointDecorator != null) {
            firstPointDecorator.removeDecoration();
            firstPointDecorator = null;
        }
        if (firstPointPreview != null) {
            drawingArea.getChildren().remove(firstPointPreview);
            firstPointPreview = null;
        }
        firstPoint = null;
        secondPoint = null;

        // Reset per poligoni irregolari
        for (ShapeDecorator decorator : previewDecorators) {
            decorator.removeDecoration();
        }
        previewDecorators.clear();

        for (Circle circle : vertexPreviews) {
            drawingArea.getChildren().remove(circle);
        }
        vertexPreviews.clear();

        for (Line line : edgePreviews) {
            drawingArea.getChildren().remove(line);
        }
        edgePreviews.clear();

        vertices.clear();

    }
}
