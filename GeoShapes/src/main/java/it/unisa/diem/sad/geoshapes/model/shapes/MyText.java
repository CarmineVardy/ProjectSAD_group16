package it.unisa.diem.sad.geoshapes.model.shapes;

import it.unisa.diem.sad.geoshapes.model.MyColor;

public class MyText extends MyShape {
    private String text;
    private MyColor textColor;
    private double fontSize;

    public MyText(double startX, double startY, double endX, double endY, double rotation,
                  MyColor borderColor, MyColor fillColor, String text, MyColor textColor, double fontSize) {
        super(startX, startY, endX, endY, rotation, borderColor, fillColor);
        this.text = text;
        this.textColor = textColor;
        this.fontSize = fontSize;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public MyColor getTextColor() {
        return textColor;
    }

    public void setTextColor(MyColor textColor) {
        this.textColor = textColor;
    }

    public double getFontSize() {
        return fontSize;
    }

    public void setFontSize(double fontSize) {
        this.fontSize = fontSize;
    }

    @Override
    public String getShapeType() {
        return "Text";
    }

    @Override
    public void flipHorizontal() {

    }

    @Override
    public void flipVertical() {

    }

    @Override
    public MyText clone() {
        MyText cloned = (MyText) super.clone();
        return cloned;
    }
}