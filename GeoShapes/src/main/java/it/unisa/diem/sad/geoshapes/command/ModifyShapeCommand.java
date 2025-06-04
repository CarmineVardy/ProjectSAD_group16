package it.unisa.diem.sad.geoshapes.command;
import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import java.util.List;

public class ModifyShapeCommand implements Command {
    private final DrawingModel model;
    private final List<MyShape> targetShapes;
    private final List<MyShape> oldShapes;
    private final List<MyShape> newShapes;

    public ModifyShapeCommand(DrawingModel model, List<MyShape> targetShapes,
                              List<MyShape> oldShapes, List<MyShape> newShapes) {
        this.model = model;
        this.targetShapes = targetShapes;
        this.oldShapes = oldShapes;
        this.newShapes = newShapes;
    }

    @Override
    public void execute() {
        for (int i = 0; i < targetShapes.size(); i++) {
            model.modifyShape(targetShapes.get(i), newShapes.get(i));
        }
    }

    @Override
    public void undo() {
        for (int i = 0; i < targetShapes.size(); i++) {
            model.modifyShape(targetShapes.get(i), oldShapes.get(i));
        }
    }
}