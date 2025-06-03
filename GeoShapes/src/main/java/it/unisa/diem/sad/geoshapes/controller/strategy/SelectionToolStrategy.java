package it.unisa.diem.sad.geoshapes.controller.strategy;

import it.unisa.diem.sad.geoshapes.controller.InteractionCallback;
import it.unisa.diem.sad.geoshapes.controller.ShapeMapping;
import it.unisa.diem.sad.geoshapes.decorator.SelectionDecorator;
import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import javafx.event.ActionEvent;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.event.Event;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectionToolStrategy implements ToolStrategy {

    private final Pane drawingPane;
    private final ShapeMapping shapeMapping;
    private SelectionDecorator currentDecorator;
    private Shape selectedJavaFxShape;
    private InteractionCallback callback;
    private boolean isRotating;
    private double lastAngle;
    private Shape currentShapeBeingRotated;
    private final Map<Shape, SelectionDecorator> decorators = new HashMap<>();

    private Shape primarySelectedShape;
    private boolean isMoving = false;
    private Point2D initialMousePress;
    private final Map<Shape, Point2D> initialTranslations = new HashMap<>();
    private final Map<Line, Point2D[]> initialLinePositions = new HashMap<>();
    private final List<MyShape> selectedModelShapes = new ArrayList<>();
    private final List<Shape> selectedJavaFxShapes = new ArrayList<>();

    private double lastX;
    private double lastY;


    private final Group zoomGroup;
    private ResizeHandleType activeHandleType = ResizeHandleType.NONE;
    private DrawingModel model;
    private boolean isResizing;

    private enum ResizeHandleType {
        ROTATION,
        NORTH_WEST, NORTH_EAST, SOUTH_WEST, SOUTH_EAST,
        NORTH, SOUTH, EAST, WEST,
        NONE
    }

    public SelectionToolStrategy(Pane drawingPane, Group zoomGroup, ShapeMapping shapeMapping, InteractionCallback callback) {
        this.drawingPane = drawingPane;
        this.shapeMapping = shapeMapping;
        this.callback = callback;
        this.zoomGroup = zoomGroup;
    }

    @Override
    public void activate(Color borderColor, Color fillColor) {
        callback.onLineSelected(false);
    }

    @Override
    public void handleBorderColorChange(Color color) {
        if (selectedJavaFxShape != null) {
            if (currentDecorator != null) currentDecorator.removeDecoration();
            this.selectedJavaFxShape.setStroke(color);
            this.callback.onModifyShape(selectedJavaFxShape);
            if (currentDecorator != null) currentDecorator.applyDecoration();
        }
    }

    @Override
    public void handleFillColorChange(Color color) {
        if (selectedJavaFxShape != null) {
            if (currentDecorator != null) currentDecorator.removeDecoration();
            this.selectedJavaFxShape.setFill(color);
            this.callback.onModifyShape(selectedJavaFxShape);
            if (currentDecorator != null) currentDecorator.applyDecoration();
        }
    }

    @Override
    public void handleMousePressed(MouseEvent event) {
        Point2D localPoint = getTransformedCoordinates(event, drawingPane);
        double x = localPoint.getX();
        double y = localPoint.getY();

        // --- Gestione del Click Destro (Menu Contestuale) ---
        if (event.getButton() == MouseButton.SECONDARY) {
            Shape shapeAtPosition = findShapeAt(x, y);
            if (shapeAtPosition != null && !selectedJavaFxShapes.contains(shapeAtPosition)) {
                // Se clicco con il destro su una forma non selezionata, la seleziono
                // Se Ctrl non è premuto, svuoto prima la selezione corrente
                if (!event.isControlDown()) {
                    clearSelection();
                }
                addShapeToSelection(shapeAtPosition); // Aggiungo la forma alla selezione
            }
            callback.onSelectionMenuOpened(event.getScreenX(), event.getScreenY()); // Apre il menu contestuale
            event.consume(); // Consuma l'evento per evitare propagazione indesiderata
            return; // Esci dal metodo
        }

        // --- Gestione del Click Primario (Sinistro) ---
        if (event.getButton() == MouseButton.PRIMARY) {

            // 1. Controllo se clicco su un HANDLE (di qualsiasi forma selezionata)
            Circle handleAtPosition = findHandleAt(x, y);
            if (handleAtPosition != null && !selectedJavaFxShapes.isEmpty()) { // Solo se ci sono forme selezionate
                String handleTypeStr = (String) handleAtPosition.getUserData();
                try {
                    activeHandleType = ResizeHandleType.valueOf(handleTypeStr);
                } catch (IllegalArgumentException e) {
                    activeHandleType = ResizeHandleType.NONE;
                }

                if (activeHandleType == ResizeHandleType.ROTATION) {
                    isRotating = true;
                    // Uso la forma primaria come riferimento per calcolare l'angolo di rotazione
                    this.currentShapeBeingRotated = primarySelectedShape != null ? primarySelectedShape : selectedJavaFxShapes.get(0);
                    lastAngle = calculateAngle(x, y, this.currentShapeBeingRotated);
                    isMoving = false; // Non sto muovendo
                    drawingPane.setCursor(Cursor.CROSSHAIR);
                    event.consume();
                    return; // Esci
                } else if (activeHandleType != ResizeHandleType.NONE) { // È un handle di ridimensionamento
                    isMoving = false;    // Non sto muovendo
                    isRotating = false;  // Non sto ruotando
                    isResizing = true;   // Sto ridimensionando
                    lastX = x;           // Salvo le coordinate iniziali per il resize
                    lastY = y;
                    event.consume();
                    return; // Esci
                }
            }

            // 2. Controllo se clicco su una FORMA
            Shape shapeAtPosition = findShapeAt(x, y);
            if (shapeAtPosition != null) {
                // Gestione selezione multipla con Ctrl
                if (event.isControlDown()) {
                    if (selectedJavaFxShapes.contains(shapeAtPosition)) {
                        // Se la forma è già selezionata e premo Ctrl, la deseleziono
                        removeShapeFromSelection(shapeAtPosition);
                    } else {
                        // Se la forma non è selezionata e premo Ctrl, la aggiungo alla selezione
                        addShapeToSelection(shapeAtPosition);
                    }
                } else {
                    // Selezione singola: se non è già selezionata, svuoto e seleziono solo questa
                    if (!selectedJavaFxShapes.contains(shapeAtPosition)) {
                        clearSelection(); // Svuoto la selezione precedente
                        addShapeToSelection(shapeAtPosition); // Seleziono solo questa forma
                    }
                    // Imposto la forma cliccata come primaria (anche se era già selezionata)
                    setPrimarySelectedShape(shapeAtPosition);
                }

                // Preparo il movimento per TUTTE le forme selezionate, se ce ne sono
                if (!selectedJavaFxShapes.isEmpty()) {
                    isMoving = true;
                    isRotating = false; // Assicurati che non si stia ruotando
                    isResizing = false; // Assicurati che non si stia ridimensionando
                    activeHandleType = ResizeHandleType.NONE; // Nessun handle attivo

                    initialMousePress = new Point2D(x, y); // Punto di partenza del drag per il movimento

                    // Salvo le posizioni iniziali di TUTTE le forme selezionate
                    initialTranslations.clear();    // Pulisco le mappe
                    initialLinePositions.clear();

                    for (Shape shape : selectedJavaFxShapes) {
                        initialTranslations.put(shape, new Point2D(shape.getTranslateX(), shape.getTranslateY()));
                        if (shape instanceof Line line) {
                            Point2D[] linePos = {
                                    new Point2D(line.getStartX(), line.getStartY()),
                                    new Point2D(line.getEndX(), line.getEndY())
                            };
                            initialLinePositions.put(line, linePos);
                        }
                    }
                    drawingPane.setCursor(Cursor.MOVE); // Imposta il cursore di movimento
                }
                event.consume();
                return; // Esci
            }

            // 3. Clicco su uno SPAZIO VUOTO
            if (!event.isControlDown()) { // Se Ctrl non è premuto, deseleziona tutto
                clearSelection();
            }
            event.consume();
        }
    }

    @Override
    public void handleMouseDragged(MouseEvent event) {
        Point2D localPoint = getTransformedCoordinates(event, drawingPane);
        double x = localPoint.getX();
        double y = localPoint.getY();

        // Se non ci sono forme selezionate, non fare nulla
        if (selectedJavaFxShapes.isEmpty()) return;

        // --- Gestione Rotazione (per tutte le forme selezionate) ---
        // La rotazione si basa sulla forma primaria (o quella su cui è stato cliccato l'handle di rotazione)
        if (isRotating && currentShapeBeingRotated != null) {
            double currentAngle = calculateAngle(x, y, currentShapeBeingRotated);
            double deltaAngle = currentAngle - lastAngle;

            // Applica la rotazione a TUTTE le forme selezionate
            for (Shape shape : selectedJavaFxShapes) {
                SelectionDecorator decorator = decorators.get(shape);
                if (decorator != null) decorator.removeDecoration(); // Rimuovi temporaneamente il decoratore
                shape.setRotate(shape.getRotate() + deltaAngle); // Applica la rotazione
                if (decorator != null) decorator.applyDecoration(); // Riapplica il decoratore per aggiornare gli handle
            }

            lastAngle = currentAngle; // Aggiorna l'ultimo angolo per il prossimo drag
            event.consume();
            return; // Esci
        }

        // --- Gestione Resize (per tutte le forme selezionate) ---
        // Il resize si applica in base all'handle attivo
        if (isResizing && activeHandleType != ResizeHandleType.NONE && activeHandleType != ResizeHandleType.ROTATION) {
            double deltaX = x - lastX;
            double deltaY = y - lastY;

            // Applica il resize a TUTTE le forme selezionate
            for (Shape shape : selectedJavaFxShapes) {
                SelectionDecorator decorator = decorators.get(shape);
                if (decorator != null) decorator.deactivateDecoration(); // Disattiva il decoratore per non interferire con il resize
                performResize(shape, deltaX, deltaY); // Chiama il metodo performResize (che aggiorneremo dopo)
                if (decorator != null) decorator.activateDecoration(); // Riapplica il decoratore dopo il resize
            }

            lastX = x; // Aggiorna l'ultima posizione del mouse per il prossimo drag
            lastY = y;
            event.consume();
            return; // Esci
        }

        // --- Gestione Movimento (per tutte le forme selezionate) ---
        if (isMoving && !selectedJavaFxShapes.isEmpty()) {
            double deltaX = x - initialMousePress.getX();
            double deltaY = y - initialMousePress.getY();

            // Muovi TUTTE le forme selezionate
            for (Shape shape : selectedJavaFxShapes) {
                Point2D initialTranslation = initialTranslations.get(shape);
                if (shape instanceof Line line) {
                    Point2D[] initialPos = initialLinePositions.get(line);
                    if (initialPos != null) {
                        line.setStartX(initialPos[0].getX() + deltaX);
                        line.setStartY(initialPos[0].getY() + deltaY);
                        line.setEndX(initialPos[1].getX() + deltaX);
                        line.setEndY(initialPos[1].getY() + deltaY);
                    }
                } else if (initialTranslation != null) {
                    shape.setTranslateX(initialTranslation.getX() + deltaX);
                    shape.setTranslateY(initialTranslation.getY() + deltaY);
                }
            }

            // Aggiorna tutti i decoratori delle forme selezionate (li sposta insieme alle forme)
            for (SelectionDecorator decorator : decorators.values()) {
                decorator.deactivateDecoration(); // Disattiva per muovere
                decorator.activateDecoration();   // Riapplica nella nuova posizione
            }
            event.consume();
        }
    }


    @Override
    public void handleMouseReleased(MouseEvent event) {
        System.out.println("--- STS.handleMouseReleased START ---");
        System.out.println("  Event source: " + event.getSource().getClass().getSimpleName() + " @" + System.identityHashCode(event.getSource()));
        System.out.println("  SelectedJavaFxShapes size: " + selectedJavaFxShapes.size());
        System.out.println("  Shapes in selectedJavaFxShapes before processing:");
        for (Shape s : selectedJavaFxShapes) {
            System.out.println("    - From list: " + s.getClass().getSimpleName() + " (ID: " + s.getId() + ") @" + System.identityHashCode(s));
        }
        boolean wasMoving = isMoving;
        boolean wasRotating = isRotating;
        boolean wasResizing = isResizing;

        // Reset degli stati di interazione
        isMoving = false;
        isRotating = false;
        isResizing = false;
        ResizeHandleType previousActiveHandleType = activeHandleType;
        activeHandleType = ResizeHandleType.NONE;

        if (selectedJavaFxShapes.isEmpty()) {
            handleMouseMoved(event);
            return;
        }

        // --- CREA UNA COPIA DELLA LISTA PER EVITARE ConcurrentModificationException ---
        // Questo è il CAMBIAMENTO FONDAMENTALE.
        List<Shape> shapesToProcess = new ArrayList<>(selectedJavaFxShapes);
        // Anche se la lista esterna non viene modificata, il tuo callback potrebbe
        // rimuovere/riaggiungere forme dal DrawingPane o dal Mapping, causando problemi.
        // Processare una copia è sempre più sicuro.

        // --- Gestione Rotazione ---
        if (wasRotating) {
            for (Shape shape : shapesToProcess) { // <--- Ora itero sulla COPIA
                SelectionDecorator decorator = decorators.get(shape);
                if (decorator != null) decorator.removeDecoration();

                callback.onModifyShape(shape); // Chiamo il callback

                // Nota: Rimuovi e riapplica il decoratore dopo il callback.
                // Se il callback rimuove e riaggiunge la JavaFX shape, il vecchio decoratore
                // potrebbe essere "orfano". Se il decoratore gestisce la riapplicazione
                // anche in caso di rimozione dal DrawingPane, puoi tenerlo.
                // Se i decoratori non si riattivano correttamente dopo il callback,
                // significa che la tua logica di "onModifyShape" ricrea/sostituisce la JavaFX Shape.
                // In quel caso, il decoratore deve essere ricreato o la logica di callback rivista.
                // Per ora, teniamo la rimozione/applicazione del decoratore qui.
                if (decorator != null) decorator.applyDecoration();
            }
        }
        // --- Gestione Resize ---
        else if (wasResizing && previousActiveHandleType != ResizeHandleType.NONE) {
            for (Shape shape : shapesToProcess) { // <--- Ora itero sulla COPIA
                SelectionDecorator decorator = decorators.get(shape);
                if (decorator != null) decorator.removeDecoration();

                callback.onModifyShape(shape);

                if (decorator != null) decorator.applyDecoration();
            }
        }
        // --- Gestione Movimento ---
        else if (wasMoving) {
            Point2D localPoint = getTransformedCoordinates(event, drawingPane);
            double dx = localPoint.getX() - initialMousePress.getX();
            double dy = localPoint.getY() - initialMousePress.getY();
            boolean significantChange = (dx * dx + dy * dy) > 4;

            if (significantChange) {
                for (Shape shape : shapesToProcess) { // <--- Ora itero sulla COPIA
                    bakeTranslation(shape);
                    SelectionDecorator decorator = decorators.get(shape);
                    if (decorator != null) decorator.removeDecoration();

                    callback.onModifyShape(shape);

                    if (decorator != null) decorator.applyDecoration();
                }
            } else {
                // Se il movimento non è significativo (es. un semplice click senza drag),
                // riattiva semplicemente i decoratori per assicurare la loro visibilità.
                // Questa parte è un po' più delicata se il callback modifica la lista.
                // In questo caso, i decoratori vanno comunque riattivati sulle forme attuali.
                // Se la lista `decorators` viene modificata da `onModifyShape` (che non viene chiamato qui),
                // potresti ancora avere un problema. Ma la logica di non-significant-change non chiama il callback.
                // Quindi, il loop su `decorators.values()` qui dovrebbe essere sicuro.
                for (SelectionDecorator decorator : decorators.values()) {
                    decorator.deactivateDecoration();
                    decorator.activateDecoration();
                }
            }
        }

        handleMouseMoved(event);
        event.consume();
    }
    public void handleCopy(Event event) {
        // Itera su tutte le forme selezionate e chiama il callback per ognuna
        for (Shape shape : selectedJavaFxShapes) {
            callback.onCopyShape(shape); // Passa la forma JavaFX da copiare
        }
    }

    @Override
    public void handleCut(Event event) {
        // Itera su tutte le forme selezionate e chiama il callback per ognuna
        // Il taglio implica anche la rimozione dalla selezione corrente
        // Copio la lista per evitare ConcurrentModificationException mentre rimuovo
        List<Shape> shapesToCut = new ArrayList<>(selectedJavaFxShapes);
        for (Shape shape : shapesToCut) {
            callback.onCutShape(shape); // Passa la forma JavaFX da tagliare
        }
        clearSelection(); // Dopo aver tagliato, deseleziona tutto
    }

    @Override
    public void handleDelete(Event event) {
        // Itera su tutte le forme selezionate e chiama il callback per ognuna
        // La cancellazione implica anche la rimozione dalla selezione corrente
        // Copio la lista per evitare ConcurrentModificationException mentre rimuovo
        List<Shape> shapesToDelete = new ArrayList<>(selectedJavaFxShapes);
        for (Shape shape : shapesToDelete) {
            callback.onDeleteShape(shape); // Passa la forma JavaFX da cancellare
        }
        clearSelection(); // Dopo aver cancellato, deseleziona tutto
    }

    @Override
    public void handleBringToFront(ActionEvent actionEvent) {
        // Itera su tutte le forme selezionate e chiama il callback per ognuna
        // Ordina la lista per assicurarsi che l'ordine sia consistente, se necessario.
        // Ad esempio, puoi ordinarle per la loro posizione attuale nel pannello o per un ID
        // per un comportamento prevedibile. Qui, iteriamo semplicemente.
        for (Shape shape : selectedJavaFxShapes) {
            callback.onBringToFront(shape);
        }
    }
    @Override
    public void handleBringToTop(ActionEvent actionEvent) {
        // Itera su tutte le forme selezionate
        for (Shape shape : selectedJavaFxShapes) {
            callback.onBringToTop(shape);
        }
    }

    @Override
    public void handleSendToBack(ActionEvent actionEvent) {
        // Itera su tutte le forme selezionate
        // Per un comportamento prevedibile di "send to back", potrebbe essere utile
        // processare le forme in ordine inverso (dal fondo alla cima) per evitare
        // che una forma inviata indietro si scontri con una già "indietro" e non si muova
        // abbastanza. Tuttavia, dipende dall'implementazione specifica del callback.
        for (Shape shape : selectedJavaFxShapes) {
            callback.onSendToBack(shape);
        }
    }

    @Override
    public void handleSendToBottom(ActionEvent actionEvent) {
        // Itera su tutte le forme selezionate
        for (Shape shape : selectedJavaFxShapes) {
            callback.onSendToBottom(shape);
        }
    }

    @Override

    public void reset() {
        clearSelection(); // Usa il nuovo metodo clearSelection per una pulizia completa
        isMoving = false;
        isRotating = false;
        isResizing = false;
        activeHandleType = ResizeHandleType.NONE;
        drawingPane.setCursor(Cursor.DEFAULT);
        // callback.onShapeDeselected(); // Questa chiamata è ora gestita dentro clearSelection
    }
    private void bakeTranslation(Shape shape) {
        if (shape == null) return;
        double tx = shape.getTranslateX();
        double ty = shape.getTranslateY();
        if (tx == 0 && ty == 0) return;

        if (shape instanceof Rectangle r) {
            r.setX(r.getX() + tx); r.setY(r.getY() + ty);
        } else if (shape instanceof Ellipse e) {
            e.setCenterX(e.getCenterX() + tx); e.setCenterY(e.getCenterY() + ty);
        } else if (shape instanceof Line l) {
            l.setStartX(l.getStartX() + tx); l.setStartY(l.getStartY() + ty);
            l.setEndX(l.getEndX() + tx); l.setEndY(l.getEndY() + ty);
        }
        shape.setTranslateX(0); shape.setTranslateY(0);
    }

    private void selectShape(Shape shapeToSelect) {
        if (currentDecorator != null) currentDecorator.removeDecoration();

        selectedJavaFxShapes.clear();
        selectedModelShapes.clear();

        if (shapeToSelect != null) {
            bakeTranslation(shapeToSelect);
            this.selectedJavaFxShape = shapeToSelect;
            selectedJavaFxShapes.add(shapeToSelect);
            MyShape modelShape = shapeMapping.getModelShape(shapeToSelect);
            if (modelShape != null) selectedModelShapes.add(modelShape);

            currentDecorator = new SelectionDecorator(shapeToSelect);
            currentDecorator.applyDecoration();
            drawingPane.setCursor(Cursor.MOVE);
            callback.onShapeSelected(shapeToSelect);
        } else {
            reset();
        }
    }
    private Shape findShapeAt(double x, double y) {
        List<javafx.scene.Node> children = drawingPane.getChildren();

        // Iteriamo al contrario per dare priorità alle forme più in alto (disegnate per ultime)
        for (int i = children.size() - 1; i >= 0; i--) {
            javafx.scene.Node node = children.get(i);

            // Controlliamo se il nodo fa parte di QUALSIASI decoratore esistente
            boolean isDecoratorElement = false;
            for (SelectionDecorator decorator : decorators.values()) { // Itera su TUTTI i decoratori attivi
                if (decorator != null) {
                    // 1. Controlla se è una maniglia (Circle)
                    if (node instanceof Circle && decorator.getResizeHandles().contains(node)) {
                        isDecoratorElement = true;
                        break; // Trovato, non serve controllare gli altri decoratori per questo nodo
                    }
                    // 2. Controlla se è un bordo di selezione (Shape, come il Rectangle del bounding box)
                    // Anche se hai commentato la creazione del Rectangle di selezione, è buona norma
                    // mantenere questo controllo per robustezza e future modifiche.
                    if (node instanceof Shape && decorator.getSelectionBorders().contains(node)) {
                        isDecoratorElement = true;
                        break; // Trovato, non serve controllare gli altri decoratori per questo nodo
                    }
                    // Aggiungi qui altri tipi di elementi decorativi se ne dovessi aggiungere in futuro.
                }
            }

            if (isDecoratorElement) {
                continue; // Se è un elemento del decoratore, salta questo nodo e passa al prossimo
            }

            // Se non è un elemento del decoratore, controlla se è una forma selezionabile
            if (node instanceof Shape shape) {
                // Filtro aggiuntivo: assicurati che sia uno dei tipi di forma che l'utente può disegnare
                // (Rectangle, Ellipse, Line). Questo evita di selezionare altri Shape che potrebbero
                // essere stati aggiunti al pane ma non sono forme utente.
                if (!(shape instanceof Rectangle || shape instanceof Ellipse || shape instanceof Line)) {
                    continue; // Salta se non è un tipo di forma utente supportato
                }

                // Se la forma è visibile e contiene il punto del mouse
                if (shape.isVisible() && shape.contains(x, y)) {
                    return shape; // Restituisce la forma utente trovata
                }
            }
        }
        return null; // Nessuna forma utente selezionabile trovata
    }

    private Circle findHandleAt(double x, double y) {
        // Controllo gli handle di tutte le forme selezionate
        for (SelectionDecorator decorator : decorators.values()) {
            if (decorator != null) { // Aggiunto controllo null safety
                for (Circle handle : decorator.getResizeHandles()) {
                    if (handle.isVisible() && handle.getBoundsInParent().contains(x, y)) {
                        return handle;
                    }
                }
            }
        }
        return null;
    }
    public void selectShapeByModel(MyShape shape) {
        Shape javafxShape = shapeMapping.getViewShape(shape);
        if (javafxShape != null) {
            clearSelection(); // Deseleziona tutto il resto
            addShapeToSelection(javafxShape); // Aggiungi solo questa forma alla selezione
        }
    }

    public void clearSelection() {
        // Rimuovo tutti i decoratori
        for (SelectionDecorator decorator : decorators.values()) {
            decorator.removeDecoration();
        }
        decorators.clear(); // Svuota la mappa dei decoratori

        selectedJavaFxShapes.clear(); // Svuota la lista delle forme JavaFX selezionate
        selectedModelShapes.clear();  // Svuota la lista delle forme Model selezionate
        primarySelectedShape = null;  // Resetta la forma primaria

        drawingPane.setCursor(Cursor.DEFAULT);
        callback.onShapeDeselected(); // Notifica che nessuna forma è più selezionata
    }
    private void addShapeToSelection(Shape shape) {
        if (shape == null || selectedJavaFxShapes.contains(shape)) {
            return; // Non aggiungiamo forme nulle o già selezionate
        }


        bakeTranslation(shape); // "Bake" le trasformazioni correnti prima di selezionare
        selectedJavaFxShapes.add(shape); // Aggiungi la forma JavaFX alla lista

        MyShape modelShape = shapeMapping.getModelShape(shape);
        if (modelShape != null) {
            selectedModelShapes.add(modelShape); // Aggiungi la forma del modello alla lista
        }

        // Creo e applico il decoratore per questa forma
        SelectionDecorator decorator = new SelectionDecorator(shape);
        decorators.put(shape, decorator); // Aggiungi il decoratore alla mappa
        decorator.applyDecoration(); // Applica il decoratore (mostra handle)

        // Se è la prima forma selezionata, diventa quella primaria
        if (primarySelectedShape == null) {
            setPrimarySelectedShape(shape);
        }
        debugSelectionState();
        callback.onShapeSelected(shape); // Notifica che la forma è stata selezionata
    }
    @Override
    public void handleMouseMoved(MouseEvent event) {
        // Se un'operazione di drag è in corso (movimento o rotazione), mantieni il cursore appropriato
        // Queste condizioni prevengono lo sfarfallio del cursore durante un drag attivo.
        if (isMoving) {
            drawingPane.setCursor(Cursor.MOVE);
            return;
        }
        if (isRotating) {
            drawingPane.setCursor(Cursor.CROSSHAIR);
            return;
        }
        // Se un handle di resize è attivo, non cambiare il cursore qui (lo fa handleMousePressed e viene mantenuto)
        if (activeHandleType != ResizeHandleType.NONE) {
            // Potresti voler settare il cursore specifico per il resize handle qui se vuoi che persista anche se il mouse si sposta un po' fuori dall'handle.
            // Per ora, lo lasciamo in modo che il cursore sia impostato solo in handleMousePressed e rimanga tale finché isResizing è true.
            // Oppure, se vuoi che cambi al default appena esce dall'handle, puoi rimuovere questo 'return'.
            // La versione più semplice è che il cursore è impostato solo se non sei già in un drag, altrimenti prevale il cursore del drag.
            return;
        }

        Point2D localPoint = getTransformedCoordinates(event, drawingPane);
        double x = localPoint.getX();
        double y = localPoint.getY();

        // 1. Controlla se il mouse è sopra un HANDLE (di qualsiasi forma selezionata)
        // Questo metodo deve iterare su TUTTI i decoratori delle forme selezionate.
        Circle handleAtPosition = findHandleAt(x, y);
        if (handleAtPosition != null) {
            String handleType = (String) handleAtPosition.getUserData();
            switch (handleType) {
                case "ROTATION": drawingPane.setCursor(Cursor.CROSSHAIR); break;
                case "NORTH_WEST": case "SOUTH_EAST": drawingPane.setCursor(Cursor.NW_RESIZE); break;
                case "NORTH_EAST": case "SOUTH_WEST": drawingPane.setCursor(Cursor.NE_RESIZE); break;
                case "NORTH": case "SOUTH": drawingPane.setCursor(Cursor.V_RESIZE); break;
                case "EAST": case "WEST": drawingPane.setCursor(Cursor.H_RESIZE); break;
                default: drawingPane.setCursor(Cursor.HAND); break; // Cursore di default per handle generici
            }
        } else {
            // 2. Se non è sopra un handle, controlla se è sopra una FORMA selezionabile
            // Questo metodo deve trovare la forma (non-handle) più in alto che contiene il punto.
            Shape shapeAtPos = findShapeAt(x, y);
            // Imposta il cursore a MOVE se è sopra una forma, altrimenti DEFAULT
            drawingPane.setCursor(shapeAtPos != null ? Cursor.MOVE : Cursor.DEFAULT);
        }
    }

    private void removeShapeFromSelection(Shape shape) {
        if (shape == null || !selectedJavaFxShapes.contains(shape)) {
            return; // Non rimuoviamo forme nulle o non selezionate
        }

        selectedJavaFxShapes.remove(shape); // Rimuovi la forma JavaFX dalla lista

        MyShape modelShape = shapeMapping.getModelShape(shape);
        if (modelShape != null) {
            selectedModelShapes.remove(modelShape); // Rimuovi la forma del modello dalla lista
        }

        SelectionDecorator decorator = decorators.get(shape);
        if (decorator != null) {
            decorators.clear();
            decorator.removeDecoration(); // Rimuovi il decoratore dalla vista
            decorators.remove(shape); // Rimuovi il decoratore dalla mappa
        }

        // Se la forma rimossa era quella primaria, ne scegliamo una nuova
        if (primarySelectedShape == shape) {
            primarySelectedShape = selectedJavaFxShapes.isEmpty() ? null : selectedJavaFxShapes.get(0);
        }

        if (selectedJavaFxShapes.isEmpty()) {
            callback.onShapeDeselected(); // Se la selezione è vuota, notifica la deselezione
        }
        debugSelectionState();
    }

    private void setPrimarySelectedShape(Shape shape) {
        if (selectedJavaFxShapes.contains(shape)) {
            primarySelectedShape = shape;
        }
    }

    private void performResize(Shape shape, double deltaX, double deltaY) {
        double minSize = 5; // Dimensione minima per le forme, evita che diventino troppo piccole

        // Gestione del ridimensionamento per i Rettangoli
        if (shape instanceof Rectangle rect) {
            double currentX = rect.getX();
            double currentY = rect.getY();
            double currentWidth = rect.getWidth();
            double currentHeight = rect.getHeight();
            double newX = currentX, newY = currentY, newWidth = currentWidth, newHeight = currentHeight;

            // Logica per calcolare le nuove dimensioni e posizioni in base all'handle attivo
            switch (activeHandleType) {
                case NORTH_WEST: newX += deltaX; newY += deltaY; newWidth -= deltaX; newHeight -= deltaY; break;
                case NORTH_EAST: newY += deltaY; newWidth += deltaX; newHeight -= deltaY; break;
                case SOUTH_WEST: newX += deltaX; newWidth -= deltaX; newHeight += deltaY; break;
                case SOUTH_EAST: newWidth += deltaX; newHeight += deltaY; break;
                case NORTH: newY += deltaY; newHeight -= deltaY; break;
                case SOUTH: newHeight += deltaY; break;
                case EAST: newWidth += deltaX; break;
                case WEST: newX += deltaX; newWidth -= deltaX; break;
                default: break;
            }

            // Applica limiti minimi di dimensione
            if (newWidth < minSize) {
                // Se la larghezza diventa troppo piccola, riposiziona la X per mantenere la forma
                if (activeHandleType == ResizeHandleType.NORTH_WEST || activeHandleType == ResizeHandleType.SOUTH_WEST || activeHandleType == ResizeHandleType.WEST) {
                    newX += (newWidth - minSize);
                }
                newWidth = minSize;
            }
            if (newHeight < minSize) {
                // Se l'altezza diventa troppo piccola, riposiziona la Y per mantenere la forma
                if (activeHandleType == ResizeHandleType.NORTH_WEST || activeHandleType == ResizeHandleType.NORTH_EAST || activeHandleType == ResizeHandleType.NORTH) {
                    newY += (newHeight - minSize);
                }
                newHeight = minSize;
            }

            // Applica le nuove proprietà al rettangolo
            rect.setX(newX);
            rect.setY(newY);
            rect.setWidth(newWidth);
            rect.setHeight(newHeight);

        }
        // Gestione del ridimensionamento per le Ellissi
        else if (shape instanceof Ellipse ellipse) {
            // Un'ellisse è definita dal centro e dai raggi. Per il resize è più facile trattarla come un rettangolo
            // circoscritto e poi convertirne i valori.
            double currentCenterX = ellipse.getCenterX();
            double currentCenterY = ellipse.getCenterY();
            double currentRadiusX = ellipse.getRadiusX();
            double currentRadiusY = ellipse.getRadiusY();

            // Calcola il rettangolo circoscritto (x, y, width, height)
            double newX = currentCenterX - currentRadiusX;
            double newY = currentCenterY - currentRadiusY;
            double newWidth = currentRadiusX * 2;
            double newHeight = currentRadiusY * 2;

            // Applica la logica di ridimensionamento come per un rettangolo
            switch (activeHandleType) {
                case NORTH_WEST: newX += deltaX; newY += deltaY; newWidth -= deltaX; newHeight -= deltaY; break;
                case NORTH_EAST: newY += deltaY; newWidth += deltaX; newHeight -= deltaY; break;
                case SOUTH_WEST: newX += deltaX; newWidth -= deltaX; newHeight += deltaY; break;
                case SOUTH_EAST: newWidth += deltaX; newHeight += deltaY; break;
                case NORTH: newY += deltaY; newHeight -= deltaY; break;
                case SOUTH: newHeight += deltaY; break;
                case EAST: newWidth += deltaX; break;
                case WEST: newX += deltaX; newWidth -= deltaX; break;
                default: break;
            }

            // Applica limiti minimi di dimensione
            if (newWidth < minSize) {
                if (activeHandleType == ResizeHandleType.NORTH_WEST || activeHandleType == ResizeHandleType.SOUTH_WEST || activeHandleType == ResizeHandleType.WEST) newX += (newWidth - minSize);
                newWidth = minSize;
            }
            if (newHeight < minSize) {
                if (activeHandleType == ResizeHandleType.NORTH_WEST || activeHandleType == ResizeHandleType.NORTH_EAST || activeHandleType == ResizeHandleType.NORTH) newY += (newHeight - minSize);
                newHeight = minSize;
            }

            // Converti le nuove dimensioni e posizioni del rettangolo in centro e raggi dell'ellisse
            ellipse.setCenterX(newX + newWidth / 2);
            ellipse.setCenterY(newY + newHeight / 2);
            ellipse.setRadiusX(newWidth / 2);
            ellipse.setRadiusY(newHeight / 2);

        }
        // Gestione del ridimensionamento per le Linee
        else if (shape instanceof Line line) {
            // Per le linee, ogni handle corrisponde a uno dei punti di inizio/fine
            switch (activeHandleType) {
                case NORTH_WEST: line.setStartX(line.getStartX() + deltaX); line.setStartY(line.getStartY() + deltaY); break;
                case NORTH_EAST: line.setEndX(line.getEndX() + deltaX); line.setStartY(line.getStartY() + deltaY); break;
                case SOUTH_WEST: line.setStartX(line.getStartX() + deltaX); line.setEndY(line.getEndY() + deltaY); break;
                case SOUTH_EAST: line.setEndX(line.getEndX() + deltaX); line.setEndY(line.getEndY() + deltaY); break;
                // I casi NORTH, SOUTH, EAST, WEST per le linee devono gestire i punti in modo logico
                // Se l'handle è NORTH, significa che stiamo trascinando il punto più in alto della linea.
                // Questo è un'interpretazione più complessa per le linee, potresti voler semplificare.
                // Per ora, replico la logica degli handle di angoli per i punti estremi.
                // Se la tua linea non ha handle intermedi, questi casi potrebbero non essere raggiunti o avere un comportamento inatteso.
                case NORTH: line.setStartY(line.getStartY() + deltaY); break; // Modifica Y del punto iniziale (o punto più alto)
                case SOUTH: line.setEndY(line.getEndY() + deltaY); break;   // Modifica Y del punto finale (o punto più basso)
                case EAST: line.setEndX(line.getEndX() + deltaX); break;    // Modifica X del punto finale (o punto più a destra)
                case WEST: line.setStartX(line.getStartX() + deltaX); break; // Modifica X del punto iniziale (o punto più a sinistra)
                default: break;
            }
        }
        // Puoi aggiungere altri tipi di forme qui (es. Polyline, Polygon, etc.)
    }



    @Override
    public List<MyShape> getSelectedShapes() {
        return new ArrayList<>(this.selectedModelShapes); // Restituisce una copia per sicurezza
    }

    private double calculateAngle(double x, double y, Shape shape) {
        Bounds bounds = shape.getBoundsInParent();
        double centerX = bounds.getMinX() + bounds.getWidth() / 2;
        double centerY = bounds.getMinY() + bounds.getHeight() / 2;
        return Math.toDegrees(Math.atan2(y - centerY, x - centerX));
    }

    private void debugSelectionState() {
        System.out.println("=== DEBUG SELEZIONE ===");
        System.out.println("selectedJavaFxShapes: " + selectedJavaFxShapes.size());
        for (Shape s : selectedJavaFxShapes) {
            System.out.println(" -> " + s + " | modello: " + shapeMapping.getModelShape(s));
        }
        System.out.println("decorators: " + decorators.size());
        System.out.println("shapeMapping size: " + shapeMapping.size());
        debugDrawingPaneChildren();
    }

    private void debugDrawingPaneChildren() {
        System.out.println("== CHILDREN DEL PANE ==");
        for (Node node : drawingPane.getChildren()) {
            System.out.println(" - " + node + " | " + node.getClass());
        }
    }
}