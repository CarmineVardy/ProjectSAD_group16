package it.unisa.diem.sad.geoshapes.model.shapes;

import it.unisa.diem.sad.geoshapes.model.MyColor;

/**
 * Modello logico per un testo posizionato con coordinate normalizzate (startX/startY).
 * In questo caso startX/startY ed endX/endY coincidono (il testo è trattato come punto).
 */
public class MyText extends MyShape {
    private String text;
    private String fontFamily = "Arial";
    private double fontSize = 14;
    private boolean bold = false;
    private boolean italic = false;
    private boolean underline = false;
    private MyColor textColor;

    public MyText(String text, double startX, double startY) {
        // usa startX=startY=endX=endY iniziali
        super(startX, startY, startX, startY, null, null);
        this.text = text;
        this.textColor = new MyColor(0, 0, 0); // default: nero
    }

    @Override
    public String getShapeType() {
        return "Text";
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTextColor(MyColor color) {
        this.textColor = color;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public double getFontSize() {
        return fontSize;
    }

    public void setFontSize(double fontSize) {
        this.fontSize = fontSize;
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public boolean isItalic() {
        return italic;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
    }

    public boolean isUnderline() {
        return underline;
    }

    public void setUnderline(boolean underline) {
        this.underline = underline;
    }

    @Override
    public void moveBy(double dx, double dy) {
        setStartX(getStartX() + dx);
        setStartY(getStartY() + dy);
        setEndX(getEndX() + dx);
        setEndY(getEndY() + dy);
    }

    @Override
    public void flipHorizontal() {
        double centerX = (getStartX() + getEndX()) / 2;
        double newStartX = 2 * centerX - getEndX();
        double newEndX = 2 * centerX - getStartX();
        setStartX(newStartX);
        setEndX(newEndX);
    }

    @Override
    public void flipVertical() {
        double centerY = (getStartY() + getEndY()) / 2;
        double newStartY = 2 * centerY - getEndY();
        double newEndY = 2 * centerY - getStartY();
        setStartY(newStartY);
        setEndY(newEndY);
    }

    public MyColor getTextColor() {
        return textColor;
    }

    // ——————————————————————————————————————————————
    // Aggiungiamo i metodi getX()/getY() e setX()/setY()
    // per non dover in giro usare getStartX()/getStartY().
    // In questo modo chiunque scrive model.getX() ottiene startX.

    /** Restituisce X normalizzata (alias di getStartX()). */
    public double getX() {
        return getStartX();
    }

    /** Imposta X normalizzata e propaga anche su endX (così rimane “punto”). */
    public void setX(double x) {
        setStartX(x);
        setEndX(x);
    }

    /** Restituisce Y normalizzata (alias di getStartY()). */
    public double getY() {
        return getStartY();
    }

    /** Imposta Y normalizzata e propaga anche su endY. */
    public void setY(double y) {
        setStartY(y);
        setEndY(y);
    }
}