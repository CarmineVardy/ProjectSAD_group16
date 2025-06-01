package it.unisa.diem.sad.geoshapes.adapter.forward;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.model.shapes.MyText;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

public class TextAdapter implements ShapeAdapter {

    private static final TextAdapter INSTANCE = new TextAdapter();

    private TextAdapter() {} // costruttore privato

    public static TextAdapter getInstance() {
        return INSTANCE;
    }

    @Override
    public Shape getFxShape(MyShape modelShape, double width, double height) {
        MyText myText = (MyText) modelShape;

        double x = myText.getStartX() * width;
        double y = myText.getStartY() * height;

        Text fxText = new Text(x, y, myText.getText());

        fxText.setFont(Font.font(
                myText.getFontFamily(),
                myText.isBold() ? FontWeight.BOLD : FontWeight.NORMAL,
                myText.isItalic() ? FontPosture.ITALIC : FontPosture.REGULAR,
                myText.getFontSize()
        ));

        fxText.setUnderline(myText.isUnderline());

        fxText.setFill(new Color(
                myText.getTextColor().getRed(),
                myText.getTextColor().getGreen(),
                myText.getTextColor().getBlue(),
                myText.getTextColor().getOpacity()
        ));

        fxText.setPickOnBounds(true);

        return fxText;
    }
}
