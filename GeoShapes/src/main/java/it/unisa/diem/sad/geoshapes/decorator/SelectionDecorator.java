package it.unisa.diem.sad.geoshapes.decorator;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Decorator per le forme JavaFX che aggiunge funzionalità di selezione,
 * inclusi handle di ridimensionamento e un handle di rotazione.
 * Gestisce l'aspetto visivo della selezione e il posizionamento degli handle.
 */
public class SelectionDecorator implements ShapeDecorator {

    private final Shape shape; // La forma JavaFX da decorare
    private Color originalStrokeColor; // Colore originale del bordo della forma
    private double originalStrokeWidth; // Spessore originale del bordo della forma
    private StrokeType originalStrokeType; // Tipo di linea originale del bordo della forma
    private Paint originalFill; // Colore di riempimento originale della forma
    private Circle rotationHandle; // L'handle specifico per la rotazione
    private static final double ROTATION_HANDLE_OFFSET = 30; // Distanza dell'handle di rotazione dalla forma
    private final Pane drawingArea; // Il Pane su cui sono disegnate le forme e gli handle
    private final List<Circle> resizeHandles; // Lista che contiene tutti gli handle (ridimensionamento e rotazione)
    private static final double HANDLE_SIZE = 8; // Dimensione degli handle

    /**
     * Costruttore per il SelectionDecorator.
     * @param shape La forma JavaFX da decorare. Il suo genitore deve essere un Pane.
     * @throws IllegalArgumentException se il genitore della forma non è un Pane.
     */
    public SelectionDecorator(Shape shape) {
        this.shape = shape;
        this.resizeHandles = new ArrayList<>();
        // Si assicura che la forma sia già stata aggiunta a un Pane prima di creare il decoratore.
        // Questo è fondamentale perché gli handle verranno aggiunti a questo Pane.
        if (shape.getParent() instanceof Pane) {
            this.drawingArea = (Pane) shape.getParent();
        } else {
            throw new IllegalArgumentException("The parent of the shape must be a Pane when creating SelectionDecorator.");
        }
        createHandles(); // Crea gli handle una sola volta alla costruzione del decoratore
    }

    /**
     * Memorizza le proprietà originali della forma prima di applicare la decorazione.
     * Questo è necessario per poter ripristinare l'aspetto originale quando la decorazione viene rimossa.
     */
    private void storeOriginalProperties() {
        originalStrokeColor = (Color) shape.getStroke();
        originalStrokeWidth = shape.getStrokeWidth();
        originalStrokeType = shape.getStrokeType();
        originalFill = shape.getFill();
    }

    /**
     * Applica la decorazione alla forma: cambia l'aspetto del bordo,
     * aggiunge un leggero effetto di trasparenza al riempimento
     * e posiziona gli handle di ridimensionamento e rotazione.
     */
    @Override
    public void applyDecoration() {
        storeOriginalProperties(); // Memorizza le proprietà attuali della forma

        // Modifica l'aspetto del bordo per indicare la selezione
        shape.setStroke(Color.BLUE); // Bordo blu per la selezione
        shape.setStrokeWidth(originalStrokeWidth + 0.5); // Leggermente più spesso
        shape.setStrokeType(StrokeType.OUTSIDE); // Bordo all'esterno della forma

        // Rende la forma leggermente trasparente se ha un riempimento, per non oscurare ciò che sta sotto
        if (originalFill instanceof Color originalColor) {
            if (originalColor.getOpacity() > 0.0) {
                Color newColor = new Color(originalColor.getRed(), originalColor.getGreen(),
                        originalColor.getBlue(), 0.7); // 0.7 di opacità
                shape.setFill(newColor);
            }
        }

        addHandlesToDrawingArea(); // Aggiunge gli handle al Pane di disegno
        updateHandlePositions();   // Aggiorna le posizioni degli handle in base alla forma
        showHandles();             // Assicura che gli handle siano visibili
    }

    /**
     * Rimuove la decorazione dalla forma: ripristina le proprietà originali
     * e rimuove gli handle di ridimensionamento e rotazione dal Pane di disegno.
     */
    @Override
    public void removeDecoration() {
        // Ripristina le proprietà originali della forma
        shape.setStroke(originalStrokeColor);
        shape.setStrokeWidth(originalStrokeWidth);
        shape.setStrokeType(originalStrokeType);
        shape.setFill(originalFill);

        removeHandlesFromDrawingArea(); // Rimuove gli handle dal Pane
    }

    /**
     * Restituisce la forma JavaFX che è stata decorata.
     * @return La forma JavaFX decorata.
     */
    @Override
    public Shape getDecoratedShape() {
        return shape;
    }

    /**
     * Restituisce la lista di tutti gli handle (ridimensionamento e rotazione).
     * Questa lista viene utilizzata dalla strategia di interazione per rilevare i click.
     * @return Una lista di oggetti Circle che rappresentano gli handle.
     */
    public List<Circle> getResizeHandles() {
        return resizeHandles;
    }

    /**
     * Crea gli oggetti Circle che fungeranno da handle di ridimensionamento e rotazione.
     * Questi handle vengono creati una sola volta e aggiunti alla lista `resizeHandles`.
     * Non vengono aggiunti al `drawingArea` in questo metodo, ma in `addHandlesToDrawingArea()`.
     */
    private void createHandles() {
        double circleRadius = HANDLE_SIZE / 2;

        // Crea l'handle di rotazione
        rotationHandle = new Circle(circleRadius, Color.DARKORANGE); // Colore distintivo per la rotazione
        rotationHandle.setStroke(Color.WHITE);
        rotationHandle.setStrokeWidth(1);
        rotationHandle.setUserData("ROTATION"); // UserData per identificarlo nella strategia
        resizeHandles.add(rotationHandle); // Aggiunge anche l'handle di rotazione alla lista generale

        // Determina la categoria della forma per creare gli handle appropriati
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
                // Le linee hanno handle solo ai loro estremi
                Circle handleStart = new Circle(circleRadius, Color.BLUE);
                handleStart.setStroke(Color.WHITE);
                handleStart.setStrokeWidth(1);
                handleStart.setUserData("LINE_START");
                resizeHandles.add(handleStart);

                Circle handleEnd = new Circle(circleRadius, Color.BLUE);
                handleEnd.setStroke(Color.WHITE);
                handleEnd.setStrokeWidth(1);
                handleEnd.setUserData("LINE_END");
                resizeHandles.add(handleEnd);
                break;

            case "RECT_ELLIPSE":
                // Rettangoli ed ellissi hanno 8 handle sui bordi e angoli
                String[] handleTypes = {
                        "TOP_LEFT", "TOP_CENTER", "TOP_RIGHT",
                        "MIDDLE_LEFT", "MIDDLE_RIGHT",
                        "BOTTOM_LEFT", "BOTTOM_CENTER", "BOTTOM_RIGHT"
                };

                for (String type : handleTypes) {
                    Circle handle = new Circle(circleRadius, Color.BLUE);
                    handle.setStroke(Color.WHITE);
                    handle.setStrokeWidth(1);
                    handle.setUserData(type); // UserData per identificare il tipo di ridimensionamento
                    resizeHandles.add(handle);
                }
                break;

            default:
                System.err.println("Unsupported shape type for resize handles: " + shape.getClass().getSimpleName());
                // Nessun handle di ridimensionamento creato per tipi di forma non supportati
                break;
        }
    }

    /**
     * Aggiunge tutti gli handle alla lista dei figli del `drawingArea` (Pane).
     * Si assicura che un handle non venga aggiunto più volte.
     */
    private void addHandlesToDrawingArea() {
        if (drawingArea == null) {
            System.err.println("drawingArea is null. Cannot add handles.");
            return;
        }
        for (Circle handle : resizeHandles) {
            if (!drawingArea.getChildren().contains(handle)) {
                drawingArea.getChildren().add(handle);
            }
        }
    }

    /**
     * Rimuove tutti gli handle dalla lista dei figli del `drawingArea`.
     */
    private void removeHandlesFromDrawingArea() {
        if (drawingArea != null) {
            drawingArea.getChildren().removeAll(resizeHandles);
        }
        resizeHandles.clear(); // Pulisce la lista degli handle dopo averli rimossi dal Pane
    }

    /**
     * Aggiorna le posizioni di tutti gli handle (ridimensionamento e rotazione)
     * in base ai bounds attuali della forma decorata e alla sua rotazione.
     * Questo metodo è cruciale per mantenere gli handle allineati alla forma
     * dopo qualsiasi trasformazione (movimento, ridimensionamento, rotazione).
     */
    public void updateHandlePositions() {
        // Ottiene i bounds della forma nel suo sistema di coordinate locale (non ruotato)
        Bounds localBounds = shape.getBoundsInLocal();

        // Aggiorna la posizione dell'handle di rotazione
        if (rotationHandle != null) {
            // Posizione dell'handle di rotazione nel sistema di coordinate locale della forma
            // (sopra il centro della forma, non ruotato)
            double baseRotationHandleLocalX = localBounds.getMinX() + localBounds.getWidth() / 2;
            double baseRotationHandleLocalY = localBounds.getMinY() - ROTATION_HANDLE_OFFSET;

            // Trasforma questo punto locale nel sistema di coordinate del parent
            Point2D rotatedRotationHandlePos = shape.localToParent(baseRotationHandleLocalX, baseRotationHandleLocalY);
            rotationHandle.setCenterX(rotatedRotationHandlePos.getX());
            rotationHandle.setCenterY(rotatedRotationHandlePos.getY());
        }

        // Aggiorna le posizioni degli handle di ridimensionamento
        if (shape instanceof Line fxLine) {
            // Per le linee, gli handle sono posizionati direttamente sui punti di inizio e fine.
            // Questi punti devono essere trasformati dal sistema locale della linea a quello del parent.
            for (Circle handle : resizeHandles) {
                if ("LINE_START".equals(handle.getUserData())) {
                    Point2D startPointInParent = fxLine.localToParent(fxLine.getStartX(), fxLine.getStartY());
                    handle.setCenterX(startPointInParent.getX());
                    handle.setCenterY(startPointInParent.getY());
                } else if ("LINE_END".equals(handle.getUserData())) {
                    Point2D endPointInParent = fxLine.localToParent(fxLine.getEndX(), fxLine.getEndY());
                    handle.setCenterX(endPointInParent.getX());
                    handle.setCenterY(endPointInParent.getY());
                }
            }
        } else if (shape instanceof Rectangle || shape instanceof Ellipse) {
            // Per rettangoli ed ellissi, calcola le posizioni degli 8 handle usando i bounds locali
            String[] handleTypes = {
                    "TOP_LEFT", "TOP_CENTER", "TOP_RIGHT",
                    "MIDDLE_LEFT", "MIDDLE_RIGHT",
                    "BOTTOM_LEFT", "BOTTOM_CENTER", "BOTTOM_RIGHT"
            };

            // Coordinate base (nel sistema di coordinate locale della forma, non ruotato)
            double minXLocal = localBounds.getMinX();
            double maxXLocal = localBounds.getMaxX();
            double minYLocal = localBounds.getMinY();
            double maxYLocal = localBounds.getMaxY();
            double centerXLocal = localBounds.getMinX() + localBounds.getWidth() / 2;
            double centerYLocal = localBounds.getMinY() + localBounds.getHeight() / 2;

            double[] xLocalPositions = {
                    minXLocal, centerXLocal, maxXLocal,    // TOP_LEFT, TOP_CENTER, TOP_RIGHT
                    minXLocal, maxXLocal,             // MIDDLE_LEFT, MIDDLE_RIGHT
                    minXLocal, centerXLocal, maxXLocal     // BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT
            };

            double[] yLocalPositions = {
                    minYLocal, minYLocal, minYLocal,       // TOP_LEFT, TOP_CENTER, TOP_RIGHT
                    centerYLocal, centerYLocal,       // MIDDLE_LEFT, MIDDLE_RIGHT
                    maxYLocal, maxYLocal, maxYLocal        // BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT
            };

            // Posiziona ogni handle
            for (int i = 0; i < handleTypes.length; i++) {
                Circle handle = findHandleByUserData(handleTypes[i]);
                if (handle != null) {
                    // Posizione base dell'handle nel sistema di coordinate locale della forma
                    double baseHandleLocalX = xLocalPositions[i];
                    double baseHandleLocalY = yLocalPositions[i];

                    // Trasforma questo punto locale nel sistema di coordinate del parent
                    Point2D handlePosInParent = shape.localToParent(baseHandleLocalX, baseHandleLocalY);
                    handle.setCenterX(handlePosInParent.getX());
                    handle.setCenterY(handlePosInParent.getY());
                }
            }
        }
    }

    /**
     * Trova un handle specifico nella lista `resizeHandles` in base al suo UserData.
     * @param userData Il valore di UserData (String) da cercare.
     * @return L'oggetto Circle trovato, o `null` se nessun handle corrisponde.
     */
    private Circle findHandleByUserData(String userData) {
        for (Circle handle : resizeHandles) {
            if (userData.equals(handle.getUserData())) {
                return handle;
            }
        }
        return null;
    }

    /**
     * Rende visibili tutti gli handle impostando la loro proprietà `visible` a `true`.
     */
    public void showHandles() {
        for (Circle handle : resizeHandles) {
            handle.setVisible(true);
        }
    }

    /**
     * Rende invisibili tutti gli handle impostando la loro proprietà `visible` a `false`.
     */
    public void hideHandles() {
        for (Circle handle : resizeHandles) {
            handle.setVisible(false);
        }
    }
}
