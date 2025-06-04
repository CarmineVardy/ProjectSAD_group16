package it.unisa.diem.sad.geoshapes.command;

public interface Command {
    void execute();
    void undo();
}