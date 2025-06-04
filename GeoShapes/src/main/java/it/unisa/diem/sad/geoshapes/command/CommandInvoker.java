package it.unisa.diem.sad.geoshapes.command;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.Stack;

public class CommandInvoker {

    private Stack<Command> commandHistory;
    private final BooleanProperty canUndoProperty = new SimpleBooleanProperty(false);


    public CommandInvoker() {
        this.commandHistory = new Stack<>();
    }

    public void executeCommand(Command command) {
        if (command != null) {
            command.execute();
            commandHistory.push(command);
            canUndoProperty.set(canUndo());
        }
    }

    public void undo() {
        if (!commandHistory.isEmpty()) {
            Command lastCommand = commandHistory.pop();
            lastCommand.undo();
            canUndoProperty.set(canUndo());
        }
    }

    public boolean canUndo() {
        return !commandHistory.isEmpty();
    }

    public void clearHistory() {
        commandHistory.clear();
    }

    public BooleanProperty canUndoProperty() {
        return canUndoProperty;
    }
}

