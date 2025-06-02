package it.unisa.diem.sad.geoshapes.controller.strategy;

import it.unisa.diem.sad.geoshapes.controller.InteractionCallback;
import it.unisa.diem.sad.geoshapes.decorator.PreviewDecorator;
import it.unisa.diem.sad.geoshapes.decorator.ShapeDecorator;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PolygonToolStrategy implements ToolStrategy {

    private final Pane drawingPane;
    private final InteractionCallback callback;
    private Group zoomGroup;
    private Color borderColor;
    private Color fillColor;
    private int polygonVertices;
    private boolean regularPolygon;

    // Per poligoni regolari
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

    public PolygonToolStrategy(Pane drawingPane, InteractionCallback callback, Group zoomGroup) {
        this.drawingPane = drawingPane;
        this.callback = callback;
        this.zoomGroup = zoomGroup;
        this.vertices = new ArrayList<>();
        this.vertexPreviews = new ArrayList<>();
        this.edgePreviews = new ArrayList<>();
        this.previewDecorators = new ArrayList<>();
    }

    @Override
    public void activate(Color borderColor, Color fillColor, int polygonVertices, boolean regularPolygon) {
        this.borderColor = borderColor;
        this.fillColor = fillColor;
        this.polygonVertices = polygonVertices;
        this.regularPolygon = regularPolygon;
        callback.onLineSelected(false);
    }

    @Override
    public void handleBorderColorChange(Color color) {
        this.borderColor = color;
    }

    @Override
    public void handleFillColorChange(Color color) {
        this.fillColor = color;
    }

    @Override
    public void handleChangePolygonVertices(int polygonVertices) {
        this.polygonVertices = polygonVertices;
        reset(); // Reset quando cambiano i vertici
    }

    @Override
    public void handleRegularPolygon(boolean regularPolygon) {
        this.regularPolygon = regularPolygon;
        reset(); // Reset quando cambia il tipo di poligono
    }

    @Override
    public void handleMousePressed(MouseEvent event) {
        drawingPane.setCursor(Cursor.CROSSHAIR);
        Point2D localPoint = getTransformedCoordinates(event, drawingPane);

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
        firstPointDecorator = new PreviewDecorator(circle);
        firstPointDecorator.applyDecoration();

        drawingPane.getChildren().add(circle);
    }

    private void createVertexPreview(Point2D point) {
        Circle circle = new Circle(point.getX(), point.getY(), POINT_RADIUS);
        circle.setStroke(borderColor);
        circle.setFill(borderColor);
        circle.setStrokeWidth(1.0);

        ShapeDecorator decorator = new PreviewDecorator(circle);
        decorator.applyDecoration();

        vertexPreviews.add(circle);
        previewDecorators.add(decorator);
        drawingPane.getChildren().add(circle);
    }

    private void createEdgePreview(Point2D start, Point2D end) {
        Line line = new Line(start.getX(), start.getY(), end.getX(), end.getY());
        line.setStroke(borderColor);
        line.setStrokeWidth(1.0);

        ShapeDecorator decorator = new PreviewDecorator(line);
        decorator.applyDecoration();

        edgePreviews.add(line);
        previewDecorators.add(decorator);
        drawingPane.getChildren().add(line);
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
        // Non necessario per i poligoni
    }

    @Override
    public void handleMouseReleased(MouseEvent event) {
        drawingPane.setCursor(Cursor.DEFAULT);
    }

    @Override
    public void reset() {
        // Reset per poligoni regolari
        if (firstPointDecorator != null) {
            firstPointDecorator.removeDecoration();
            firstPointDecorator = null;
        }
        if (firstPointPreview != null) {
            drawingPane.getChildren().remove(firstPointPreview);
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
            drawingPane.getChildren().remove(circle);
        }
        vertexPreviews.clear();

        for (Line line : edgePreviews) {
            drawingPane.getChildren().remove(line);
        }
        edgePreviews.clear();

        vertices.clear();
    }

    @Override
    public void handleMouseMoved(MouseEvent event) {
        // NO IMPLEMENTATION HERE
    }

    @Override
    public List<MyShape> getSelectedShapes() {
        return Collections.emptyList();
    }

    @Override
    public void handleBringToFront(ActionEvent actionEvent) {
        // NO IMPLEMENTATION HERE
    }

    @Override
    public void handleBringToTop(ActionEvent actionEvent) {
        // NO IMPLEMENTATION HERE
    }

    @Override
    public void handleSendToBack(ActionEvent actionEvent) {
        // NO IMPLEMENTATION HERE
    }

    @Override
    public void handleSendToBottom(ActionEvent actionEvent) {
        // NO IMPLEMENTATION HERE
    }

    @Override
    public void handleCopy(Event event) {
        // NO IMPLEMENTATION HERE
    }

    @Override
    public void handleCut(Event event) {
        // NO IMPLEMENTATION HERE
    }

    @Override
    public void handleDelete(Event event) {
        // NO IMPLEMENTATION HERE
    }
}