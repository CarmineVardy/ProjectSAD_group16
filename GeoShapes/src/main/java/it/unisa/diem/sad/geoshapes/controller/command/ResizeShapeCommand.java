package it.unisa.diem.sad.geoshapes.controller.command;

import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyEllipse;
import it.unisa.diem.sad.geoshapes.model.shapes.MyLine;
import it.unisa.diem.sad.geoshapes.model.shapes.MyRectangle;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.geometry.Bounds;
import javafx.scene.shape.Shape;


public class ResizeShapeCommand implements Command {

    private final DrawingModel model;


    //private final Bounds initialFxBounds;
    //private final Bounds finalFxBounds;

    private final MyShape oldShape;
    private final MyShape newShape;



    /*
    public ResizeShapeCommand(DrawingModel model, MyShape modelShapeToResize, Shape fxShape, Bounds initialFxBounds, Bounds finalFxBounds) {
        this.model = model;
        this.modelShapeToResize = modelShapeToResize;
        this.initialFxBounds = initialFxBounds;
        this.finalFxBounds = finalFxBounds;
    }*/

    public ResizeShapeCommand( DrawingModel model, MyShape oldShape, MyShape newShape){
        this.model = model;
        this.oldShape = oldShape;
        this.newShape = newShape;

    }


    @Override
    public void execute() {
        model.modifyShape(oldShape, newShape);
        //applyDimensionsToModel(modelShapeToResize, initialFxBounds, finalFxBounds);
        //model.notifyObservers("MODIFY_SHAPE_PROPERTIES", modelShapeToResize);
    }



    private void applyDimensionsToModel(MyShape modelShape, Bounds initialBounds, Bounds finalBounds) {
        switch (modelShape.getClass().getSimpleName()) {
            case "MyRectangle":
                resizeRectangle((MyRectangle) modelShape, finalBounds);
                break;
            case "MyEllipse":
                resizeEllipse((MyEllipse) modelShape, finalBounds);
                break;
            case "MyLine":
                resizeLine((MyLine) modelShape, initialBounds, finalBounds);
                break;
            default:
                System.err.println("ResizeShapeCommand: Unsupported model shape type for resize: " + modelShape.getClass().getSimpleName());
                break;
        }
    }

    private void resizeRectangle(MyRectangle myRect, Bounds finalBounds) {
        myRect.setX(finalBounds.getMinX());
        myRect.setY(finalBounds.getMinY());
        myRect.setWidth(finalBounds.getWidth());
        myRect.setHeight(finalBounds.getHeight());
    }

    private void resizeEllipse(MyEllipse myEllipse, Bounds finalBounds) {
        myEllipse.setCenterX(finalBounds.getMinX() + finalBounds.getWidth() / 2);
        myEllipse.setCenterY(finalBounds.getMinY() + finalBounds.getHeight() / 2);
        myEllipse.setRadiusX(finalBounds.getWidth() / 2);
        myEllipse.setRadiusY(finalBounds.getHeight() / 2);
    }

    private void resizeLine(MyLine myLine, Bounds initialBounds, Bounds finalBounds) {
        double originalStartX_rel = myLine.getStartX() - initialBounds.getMinX();
        double originalStartY_rel = myLine.getStartY() - initialBounds.getMinY();
        double originalEndX_rel = myLine.getEndX() - initialBounds.getMinX();
        double originalEndY_rel = myLine.getEndY() - initialBounds.getMinY();

        double scaleX = (initialBounds.getWidth() == 0) ? 1.0 : finalBounds.getWidth() / initialBounds.getWidth();
        double scaleY = (initialBounds.getHeight() == 0) ? 1.0 : finalBounds.getHeight() / initialBounds.getHeight();

        double scaledStartX_rel = originalStartX_rel * scaleX;
        double scaledStartY_rel = originalStartY_rel * scaleY;
        double scaledEndX_rel = originalEndX_rel * scaleX;
        double scaledEndY_rel = originalEndY_rel * scaleY;

        myLine.setStartX(finalBounds.getMinX() + scaledStartX_rel);
        myLine.setStartY(finalBounds.getMinY() + scaledStartY_rel);
        myLine.setEndX(finalBounds.getMinX() + scaledEndX_rel);
        myLine.setEndY(finalBounds.getMinY() + scaledEndY_rel);
    }
}