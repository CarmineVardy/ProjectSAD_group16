package it.unisa.diem.sad.geoshapes.command;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.Stack;

/**
 * Manages the execution and undoing of {@link Command} objects.
 * This invoker maintains a history of executed commands, allowing for
 * undo functionality. It also provides a property to observe the
 * undo availability.
 */
public class CommandInvoker {

    // Private instance variables
    private Stack<Command> commandHistory;
    private final BooleanProperty canUndoProperty = new SimpleBooleanProperty(false);

    /**
     * Constructs a new {@code CommandInvoker}.
     * Initializes an empty command history stack.
     */
    public CommandInvoker() {
        this.commandHistory = new Stack<>();
    }

    /**
     * Executes the given command and adds it to the command history.
     * The {@code canUndoProperty} is updated after execution.
     *
     * @param command The {@link Command} to be executed. If {@code null}, no action is performed.
     */
    public void executeCommand(Command command) {
        if (command != null) {
            command.execute();
            commandHistory.push(command);
            canUndoProperty.set(canUndo());
        }
    }

    /**
     * Undoes the last executed command from the history.
     * If the history is empty, no action is performed.
     * The {@code canUndoProperty} is updated after undoing.
     */
    public void undo() {
        if (!commandHistory.isEmpty()) {
            Command lastCommand = commandHistory.pop();
            lastCommand.undo();
            canUndoProperty.set(canUndo());
        }
    }

    /**
     * Checks if there are any commands in the history that can be undone.
     *
     * @return {@code true} if the command history is not empty, {@code false} otherwise.
     */
    public boolean canUndo() {
        return !commandHistory.isEmpty();
    }



    /**
     * Returns a {@link BooleanProperty} that indicates whether an undo operation is possible.
     * This property can be used for UI binding to enable/disable an undo button.
     *
     * @return The {@code BooleanProperty} representing the undo capability.
     */
    public BooleanProperty canUndoProperty() {
        return canUndoProperty;
    }
}