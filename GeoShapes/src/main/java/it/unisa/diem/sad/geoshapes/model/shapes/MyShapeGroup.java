package it.unisa.diem.sad.geoshapes.model.shapes;

import it.unisa.diem.sad.geoshapes.model.MyColor;
import java.util.ArrayList;
import java.util.List;

public class MyShapeGroup extends MyShape {
    private List<MyShape> children;

    public MyShapeGroup(MyColor borderColor, MyColor fillColor) {
        super(0, 0, 0, 0, 0, borderColor, fillColor);
        this.children = new ArrayList<>();
    }

    public void add(MyShape shape) {
        children.add(shape);
        updateBounds();
    }

    public void remove(MyShape shape) {
        children.remove(shape);
        updateBounds();
    }

    public MyShape getChild(int index) {
        if (index >= 0 && index < children.size()) {
            return children.get(index);
        }
        return null;
    }

    public List<MyShape> getChildren() {
        return new ArrayList<>(children);
    }

    public int getChildCount() {
        return children.size();
    }

    private void updateBounds() {
        if (children.isEmpty()) {
            startX = startY = endX = endY = 0;
            return;
        }

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;

        for (MyShape child : children) {
            minX = Math.min(minX, Math.min(child.getStartX(), child.getEndX()));
            minY = Math.min(minY, Math.min(child.getStartY(), child.getEndY()));
            maxX = Math.max(maxX, Math.max(child.getStartX(), child.getEndX()));
            maxY = Math.max(maxY, Math.max(child.getStartY(), child.getEndY()));
        }

        startX = minX;
        startY = minY;
        endX = maxX;
        endY = maxY;
    }

    @Override
    public String getShapeType() {
        return "Group";
    }

    @Override
    public void flipHorizontal() {

    }

    @Override
    public void flipVertical() {

    }

    @Override
    public MyShapeGroup clone() {
        MyShapeGroup cloned = (MyShapeGroup) super.clone();
        cloned.children = new ArrayList<>();
        for (MyShape child : this.children) {
            cloned.children.add(child.clone());
        }
        return cloned;
    }
}