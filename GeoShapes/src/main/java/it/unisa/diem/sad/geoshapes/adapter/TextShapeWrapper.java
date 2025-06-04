package it.unisa.diem.sad.geoshapes.adapter;

import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.Group;
import javafx.scene.paint.Paint;

public class TextShapeWrapper {
    private final Rectangle rectangle;
    private final Text textNode;
    private final Group fullGroup;

    public TextShapeWrapper(Rectangle rectangle, Text textNode) {
        this.rectangle = rectangle;
        this.textNode = textNode;
        this.fullGroup = new Group(rectangle, textNode);

        rectangle.getProperties().put("textNode", textNode);
        rectangle.getProperties().put("fullGroup", fullGroup);
        rectangle.getProperties().put("wrapper", this);
    }

    public Rectangle getMainShape() {
        return rectangle;
    }

    public Text getTextNode() {
        return textNode;
    }

    public Group getFullGroup() {
        return fullGroup;
    }

    public void setText(String text) {
        textNode.setText(text);
    }

    public String getText() {
        return textNode.getText();
    }

    public void setTextFill(Paint paint) {
        textNode.setFill(paint);
    }

    public void setRotate(double angle) {
        fullGroup.setRotate(angle);
    }

    public double getRotate() {
        return fullGroup.getRotate();
    }

    public static TextShapeWrapper fromRectangle(Rectangle rectangle) {
        return (TextShapeWrapper) rectangle.getProperties().get("wrapper");
    }

    public static boolean isTextRectangle(Rectangle rectangle) {
        return rectangle.getProperties().containsKey("wrapper");
    }
}