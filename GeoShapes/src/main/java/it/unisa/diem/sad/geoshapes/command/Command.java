package it.unisa.diem.sad.geoshapes.command;

/**
 * Represents a command in the Command design pattern.
 * This interface defines the contract for operations that can be executed and undone,
 * enabling features like undo/redo functionality within the application.
 */
public interface Command {
    /**
     * Executes the action encapsulated by this command.
     */
    void execute();

    /**
     * Undoes the action previously executed by this command.
     */
    void undo();
}