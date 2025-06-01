package it.unisa.diem.sad.geoshapes.adapter.reverse;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.model.shapes.MyText;
import it.unisa.diem.sad.geoshapes.model.MyColor;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

public class ReverseTextAdapter implements ReverseShapeAdapter {

    private static final ReverseTextAdapter INSTANCE = new ReverseTextAdapter();

    private ReverseTextAdapter() {}

    public static ReverseTextAdapter getInstance() {
        return INSTANCE;
    }

    @Override
    public MyShape getModelShape(Shape fxShape, double width, double height) {
        Text fxText = (Text) fxShape;

        double normX = fxText.getX() / width;
        double normY = fxText.getY() / height;

        MyText myText = new MyText(fxText.getText(), normX, normY);

        myText.setFontFamily(fxText.getFont().getFamily());
        myText.setFontSize(fxText.getFont().getSize());

        String style = fxText.getFont().getStyle().toLowerCase();
        myText.setBold(style.contains("bold"));
        myText.setItalic(style.contains("italic"));

        myText.setUnderline(fxText.isUnderline());

        // Aggiunta conversione colore del testo
        myText.setTextColor(convertToModelColor((Color) fxText.getFill()));

        return myText;
    }

    public MyColor convertToModelColor(Color fxColor) {
        return new MyColor(
                fxColor.getRed(),
                fxColor.getGreen(),
                fxColor.getBlue(),
                fxColor.getOpacity()
        );
    }
}