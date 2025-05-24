package it.unisa.diem.sad.geoshapes.controller.command;

public interface Command {
    void execute();
    void undo();
}