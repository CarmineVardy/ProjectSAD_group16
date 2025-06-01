package it.unisa.diem.sad.geoshapes.controller.strategy;

import it.unisa.diem.sad.geoshapes.adapter.AdapterFactory;
import it.unisa.diem.sad.geoshapes.controller.InteractionCallback;
import it.unisa.diem.sad.geoshapes.decorator.ShapeDecorator;
import it.unisa.diem.sad.geoshapes.model.shapes.MyText;
import it.unisa.diem.sad.geoshapes.model.MyColor;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class TextToolStrategy implements ToolStrategy {

    private final InteractionCallback callback;
    private final Pane drawingPane;
    private final AdapterFactory adapterFactory = new AdapterFactory();

    // Font defaults
    private String fontFamily = "Arial";
    private double fontSize = 18;
    private boolean bold = false;
    private boolean italic = false;
    private boolean underline = false;
    private Color textColor = Color.BLACK;

    public TextToolStrategy(Pane drawingPane, InteractionCallback callback) {
        this.drawingPane = drawingPane;
        this.callback = callback;
    }

    @Override
    public void activate(Color borderColor, Color fillColor) {
        // quando attivo lo strumento testo, imposto il colore di default
        this.textColor = borderColor != null ? borderColor : Color.BLACK;
        callback.onLineSelected(false);
    }

    @Override
    public void handleMousePressed(MouseEvent event) {
        // 1) Creo un TextField provvisorio per inserire il testo:
        TextField input = new TextField();
        input.setFont(buildFont());

        // Trovo le coordinate assolute nella drawingPane
        Point2D localPoint = drawingPane.sceneToLocal(event.getSceneX(), event.getSceneY());
        double x = localPoint.getX();
        double y = localPoint.getY();

        input.setLayoutX(x);
        input.setLayoutY(y);
        input.setPrefWidth(150);

        drawingPane.getChildren().add(input);
        Platform.runLater(input::requestFocus);

        // 2) Quando premo INVIO sul TextField, creo sia il modello che il Text node
        input.setOnAction(e -> {
            String content = input.getText();
            if (content != null && !content.isBlank()) {
                // a) calcolo coordinate normalizzate per MyText
                Point2D p = new Point2D(input.getLayoutX(), input.getLayoutY());
                double normX = p.getX() / drawingPane.getWidth();
                double normY = p.getY() / drawingPane.getHeight();

                // b) creo il modello MyText
                MyText myText = new MyText(content, normX, normY);
                myText.setFontFamily(fontFamily);
                myText.setFontSize(fontSize);
                myText.setBold(bold);
                myText.setItalic(italic);
                myText.setUnderline(underline);
                myText.setTextColor(new MyColor(
                        textColor.getRed(), textColor.getGreen(), textColor.getBlue(), textColor.getOpacity()));

                // c) converto in nodo JavaFX (Text)
                Text textNode = (Text) adapterFactory.convertToJavaFx(
                        myText, drawingPane.getWidth(), drawingPane.getHeight());

                // d) associo il modello al nodo
                textNode.setUserData(myText);

                // e) notifico il controller dell’arrivo di una nuova shape
                callback.onCreateShape(textNode);

                // f) installo i listener per drag/release di textNode:
                textNode.setOnMousePressed(me -> {
                    // salvo in userData sia il punto del mouse che il modello:
                    Point2D pressPoint = getTransformedCoordinates(me, drawingPane);
                    textNode.setUserData(new Object[]{pressPoint, myText});
                });

                textNode.setOnMouseDragged(me -> {
                    Object[] data = (Object[]) textNode.getUserData();
                    Point2D lastPoint = (Point2D) data[0];
                    // MyText model = (MyText) data[1]; // non usato qui

                    Point2D current = getTransformedCoordinates(me, drawingPane);
                    double dx = current.getX() - lastPoint.getX();
                    double dy = current.getY() - lastPoint.getY();

                    textNode.setX(textNode.getX() + dx);
                    textNode.setY(textNode.getY() + dy);

                    // aggiorno il punto di riferimento con il modello
                    textNode.setUserData(new Object[]{current, myText});
                });

                textNode.setOnMouseReleased(me -> {
                    Object[] data = (Object[]) textNode.getUserData();
                    MyText myTextModel = (MyText) data[1];

                    double finalNormX = textNode.getX() / drawingPane.getWidth();
                    double finalNormY = textNode.getY() / drawingPane.getHeight();

                    // chiamo il controller con il nodo Text e le nuove coord normalizzate
                    callback.onMoveText(textNode, finalNormX, finalNormY);
                });
            }
            // rimuovo il TextField provvisorio
            drawingPane.getChildren().remove(input);
        });
    }

    private Font buildFont() {
        FontWeight w = bold ? FontWeight.BOLD : FontWeight.NORMAL;
        FontPosture p = italic ? FontPosture.ITALIC : FontPosture.REGULAR;
        return Font.font(fontFamily, w, p, fontSize);
    }

    @Override
    public void handleMouseDragged(MouseEvent event) {
        // non usato per TextTool
    }

    @Override
    public void handleMouseReleased(MouseEvent event) {
        // non usato per TextTool
    }

    @Override public void handleMouseMoved(MouseEvent event) {}
    @Override public void handleBorderColorChange(Color color) {}
    @Override public void handleFillColorChange(Color color) {}
    @Override public void handleBringToFront(javafx.event.ActionEvent e) {}
    @Override public void handleBringToTop(javafx.event.ActionEvent e) {}
    @Override public void handleSendToBack(javafx.event.ActionEvent e) {}
    @Override public void handleSendToBottom(javafx.event.ActionEvent e) {}
    @Override public void handleCopy(javafx.event.Event e) {}
    @Override public void handleCut(javafx.event.Event e) {}
    @Override public void handleDelete(javafx.event.Event e) {}

    @Override
    public void reset() {}

    @Override
    public java.util.List<it.unisa.diem.sad.geoshapes.model.shapes.MyShape> getSelectedShapes() {
        return java.util.List.of();
    }
}