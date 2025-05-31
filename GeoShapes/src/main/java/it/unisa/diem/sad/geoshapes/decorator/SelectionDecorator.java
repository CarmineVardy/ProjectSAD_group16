package it.unisa.diem.sad.geoshapes.decorator;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate; // Importa Rotate

import java.util.ArrayList;
import java.util.List;

public class SelectionDecorator implements ShapeDecorator {

    private final Shape decoratedShape; // Rinominato da 'shape' per chiarezza
    private Color originalStrokeColor;
    private double originalStrokeWidth;
    private StrokeType originalStrokeType;
    private double originalOpacity;
    private Paint originalFill;
    private Circle rotationHandle;
    private static final double ROTATION_HANDLE_OFFSET = 30;

    private final Pane drawingArea;
    private final List<Circle> resizeHandles;
    private final List<Shape> selectionBorders; // Aggiunto per il bordo di selezione
    private static final double HANDLE_SIZE = 8;
    // HANDLE_OFFSET non è più strettamente necessario per i calcoli di posizione assoluta

    public SelectionDecorator(Shape shape) {
        this.decoratedShape = shape; // Assegna alla nuova variabile
        this.resizeHandles = new ArrayList<>();
        this.selectionBorders = new ArrayList<>(); // Inizializza la lista per i bordi

        if (shape.getParent() instanceof Pane) {
            this.drawingArea = (Pane) shape.getParent();
        } else {
            // Se il parent non è un Pane, possiamo non aggiungere gli handle,
            // oppure lanciare un'eccezione come prima, dipende dalla tolleranza.
            // Per ora, manteniamo l'eccezione.
            throw new IllegalArgumentException("The parent of the shape must be a Pane for SelectionDecorator.");
        }
    }

    @Override
    public void applyDecoration() {
        storeOriginalProperties(); // Salva le proprietà originali prima di modificarle

        decoratedShape.setStroke(Color.GREEN);
        decoratedShape.setStrokeWidth(originalStrokeWidth + 0.5);
        decoratedShape.setStrokeType(StrokeType.OUTSIDE);

        if (originalFill instanceof Color originalColor) {
            if (originalColor.getOpacity() > 0.0) {
                Color newColor = new Color(originalColor.getRed(), originalColor.getGreen(), originalColor.getBlue(), 0.7);
                decoratedShape.setFill(newColor);
            }
        }

        // Crea e aggiungi handle e bordo di selezione
        createAndAddDecorations();
    }

    private void storeOriginalProperties() {
        originalStrokeColor = (Color) decoratedShape.getStroke();
        originalStrokeWidth = decoratedShape.getStrokeWidth();
        originalStrokeType = decoratedShape.getStrokeType();
        originalOpacity = decoratedShape.getOpacity();
        originalFill = decoratedShape.getFill();
    }

    @Override
    public void removeDecoration() {
        // Ripristina le proprietà originali della forma
        decoratedShape.setStroke(originalStrokeColor);
        decoratedShape.setStrokeWidth(originalStrokeWidth);
        decoratedShape.setStrokeType(originalStrokeType);
        decoratedShape.setFill(originalFill);
        decoratedShape.setOpacity(originalOpacity);

        // Rimuove gli handle di ridimensionamento e il bordo di selezione
        removeDecorations();
    }

    @Override
    public Shape getDecoratedShape() {
        return decoratedShape;
    }

    public List<Circle> getResizeHandles() {
        return resizeHandles;
    }

    // Nuovo metodo unificato per rimuovere tutte le decorazioni
    private void removeDecorations() {
        if (drawingArea != null) {
            drawingArea.getChildren().removeAll(resizeHandles);
            drawingArea.getChildren().removeAll(selectionBorders); // Rimuovi anche i bordi
        }
        resizeHandles.clear();
        selectionBorders.clear();
    }

    // Nuovo metodo unificato per creare e aggiungere tutte le decorazioni
    private void createAndAddDecorations() {
        if (drawingArea == null) {
            System.err.println("Parent Pane is null for shape: " + decoratedShape + ". Cannot add decorations.");
            return;
        }

        // STEP 1: Ottieni i limiti "locali" e la rotazione della forma
        // getLayoutBounds() fornisce i limiti della forma prima delle trasformazioni (rotate, translate, scale)
        Bounds localBounds = decoratedShape.getLayoutBounds();
        double shapeRotateAngle = decoratedShape.getRotate();
        double translateX = decoratedShape.getTranslateX();
        double translateY = decoratedShape.getTranslateY();

        double shapeLocalX = localBounds.getMinX();
        double shapeLocalY = localBounds.getMinY();
        double shapeLocalWidth = localBounds.getWidth();
        double shapeLocalHeight = localBounds.getHeight();

        // Calcola il centro della forma nel suo sistema di coordinate locale (non ruotato)
        double shapeLocalCenterX = shapeLocalX + shapeLocalWidth / 2;
        double shapeLocalCenterY = shapeLocalY + shapeLocalHeight / 2;

        // Crea una trasformazione di rotazione che useremo per posizionare gli handle
        Rotate rotateTransform = new Rotate(shapeRotateAngle, shapeLocalCenterX, shapeLocalCenterY);

        // STEP 2: Crea e posiziona il bordo di selezione (il rettangolo tratteggiato)
        if (!(decoratedShape instanceof Line)) { // Le linee spesso hanno bordi di selezione diversi o non ne hanno
            Rectangle selectionRect = new Rectangle(shapeLocalX, shapeLocalY, shapeLocalWidth, shapeLocalHeight);
            selectionRect.setStroke(Color.DODGERBLUE);
            selectionRect.setStrokeWidth(2);
            selectionRect.getStrokeDashArray().addAll(5.0, 5.0);
            selectionRect.setFill(Color.TRANSPARENT);

            // Applica la rotazione al rettangolo di selezione
            selectionRect.setRotate(shapeRotateAngle);
            // Il pivot di rotazione per il rettangolo deve essere il suo centro locale
            // Dopo aver impostato il rotate, devi spostare il rettangolo per allinearlo.
            // Il centro della forma (shapeLocalCenterX, shapeLocalCenterY) è il pivot.
            // Quando setti X/Y, il rettangolo viene disegnato da quel punto.
            // Per ruotare intorno al suo centro, devi prima impostare X/Y in modo che il suo (0,0) coincida con il pivot
            // e poi aggiungere il translateX/Y globale della forma.
            selectionRect.setX(shapeLocalX);
            selectionRect.setY(shapeLocalY);
            selectionRect.setTranslateX(translateX); // Applica la traslazione globale della forma
            selectionRect.setTranslateY(translateY); // Applica la traslazione globale della forma

            selectionBorders.add(selectionRect);
            drawingArea.getChildren().add(selectionRect);
            selectionRect.toBack(); // Metti il bordo dietro la forma
        }


        // STEP 3: Crea e posiziona gli handle di ridimensionamento e rotazione
        double circleRadius = HANDLE_SIZE / 2;

        // Handle di Rotazione
        // Posizione locale dell'handle di rotazione (es. sopra il centro del bordo superiore)
        Point2D rotationHandleLocalPos = new Point2D(shapeLocalCenterX, shapeLocalY - ROTATION_HANDLE_OFFSET);
        // Trasforma il punto locale nella posizione finale ruotata
        Point2D finalRotationHandlePos = rotateTransform.transform(rotationHandleLocalPos);

        rotationHandle = new Circle(finalRotationHandlePos.getX() + translateX, finalRotationHandlePos.getY() + translateY, circleRadius, Color.DARKORANGE);
        rotationHandle.setStroke(Color.WHITE);
        rotationHandle.setStrokeWidth(1);
        rotationHandle.setUserData("ROTATION");
        resizeHandles.add(rotationHandle);
        drawingArea.getChildren().add(rotationHandle);


        // Handle per Linee (logica specifica)
        if (decoratedShape instanceof Line fxLine) {
            // Calcola gli endpoint reali della linea dopo le trasformazioni
            // In questo caso, gli endpoint sono già in coordinate "parent" (incluse translate/rotate)
            // Se bakeTranslation sposta startX/Y in X/Y e resetta translateX/Y, allora qui fxLine.getStartX/Y etc.
            // sono già le coordinate globali.
            // Se invece fxLine.getStartX/Y sono le "native" e translateX/Y è applicato, allora dobbiamo fare un transform.
            // Assumiamo per ora che getStartX/Y siano le coordinate globali dopo la bakeTranslation, quindi non serve transform.
            // Se le linee ruotano con setRotate(), allora i loro start/end non cambiano, ma la loro visualizzazione sì.
            // In questo caso, le maniglie devono ruotare.

            // Ottieni gli endpoint in coordinate locali (prima della rotazione, se applicabile)
            // Se la rotazione è applicata via setRotate() alla Line, allora i startX/Y, endX/Y sono i punti "locali".
            // Non c'è un getLayoutBounds per Line in modo significativo per x,y,width,height.
            // Qui dobbiamo usare i valori della linea stessa e poi applicare la traslazione e rotazione.

            // Per una linea, startX/Y e endX/Y sono già i suoi "punti locali".
            // Dobbiamo trasformarli con la rotazione e la traslazione globale.
            Point2D startPointLocal = new Point2D(fxLine.getStartX(), fxLine.getStartY());
            Point2D endPointLocal = new Point2D(fxLine.getEndX(), fxLine.getEndY());

            // Il centro di rotazione per le linee è spesso il punto medio
            double lineCenterX = (fxLine.getStartX() + fxLine.getEndX()) / 2;
            double lineCenterY = (fxLine.getStartY() + fxLine.getEndY()) / 2;
            Rotate lineRotateTransform = new Rotate(shapeRotateAngle, lineCenterX, lineCenterY);

            Point2D transformedStart = lineRotateTransform.transform(startPointLocal);
            Point2D transformedEnd = lineRotateTransform.transform(endPointLocal);

            // Handle per il punto iniziale della linea
            Circle handleStart = new Circle(transformedStart.getX() + translateX, transformedStart.getY() + translateY, circleRadius, Color.BLUE);
            handleStart.setStroke(Color.WHITE);
            handleStart.setStrokeWidth(1);
            handleStart.setUserData("LINE_START");
            resizeHandles.add(handleStart);
            drawingArea.getChildren().add(handleStart);

            // Handle per il punto finale della linea
            Circle handleEnd = new Circle(transformedEnd.getX() + translateX, transformedEnd.getY() + translateY, circleRadius, Color.BLUE);
            handleEnd.setStroke(Color.WHITE);
            handleEnd.setStrokeWidth(1);
            handleEnd.setUserData("LINE_END");
            resizeHandles.add(handleEnd);
            drawingArea.getChildren().add(handleEnd);

        } else if (decoratedShape instanceof Rectangle || decoratedShape instanceof Ellipse) {
            String[] handleTypes = {
                    "TOP_LEFT", "TOP_CENTER", "TOP_RIGHT",
                    "MIDDLE_LEFT", "MIDDLE_RIGHT",
                    "BOTTOM_LEFT", "BOTTOM_CENTER", "BOTTOM_RIGHT"
            };

            // Punti degli handle nel sistema di coordinate locali della forma (non ruotati)
            Point2D[] localHandlePositions = {
                    new Point2D(shapeLocalX, shapeLocalY),                                  // TOP_LEFT
                    new Point2D(shapeLocalX + shapeLocalWidth / 2, shapeLocalY),           // TOP_CENTER
                    new Point2D(shapeLocalX + shapeLocalWidth, shapeLocalY),               // TOP_RIGHT
                    new Point2D(shapeLocalX, shapeLocalY + shapeLocalHeight / 2),           // MIDDLE_LEFT
                    new Point2D(shapeLocalX + shapeLocalWidth, shapeLocalY + shapeLocalHeight / 2),// MIDDLE_RIGHT
                    new Point2D(shapeLocalX, shapeLocalY + shapeLocalHeight),               // BOTTOM_LEFT
                    new Point2D(shapeLocalX + shapeLocalWidth / 2, shapeLocalY + shapeLocalHeight),// BOTTOM_CENTER
                    new Point2D(shapeLocalX + shapeLocalWidth, shapeLocalY + shapeLocalHeight)  // BOTTOM_RIGHT
            };

            for (int i = 0; i < handleTypes.length; i++) {
                Point2D localPos = localHandlePositions[i];
                // Trasforma il punto locale nella posizione finale ruotata
                Point2D finalPos = rotateTransform.transform(localPos);

                // Crea l'handle e aggiungi la traslazione globale della forma
                Circle handle = new Circle(finalPos.getX() + translateX, finalPos.getY() + translateY, circleRadius, Color.BLUE);
                handle.setStroke(Color.WHITE);
                handle.setStrokeWidth(1);
                handle.setUserData(handleTypes[i]);
                resizeHandles.add(handle);
                drawingArea.getChildren().add(handle);
            }
        } else {
            System.err.println("Unsupported shape type for resize handles: " + decoratedShape.getClass().getSimpleName());
        }

        // Assicurati che tutti gli handle siano sopra la forma (e il bordo di selezione)
        for (Circle handle : resizeHandles) {
            handle.toFront();
        }
        decoratedShape.toFront(); // La forma deve essere davanti al bordo di selezione
    }
}