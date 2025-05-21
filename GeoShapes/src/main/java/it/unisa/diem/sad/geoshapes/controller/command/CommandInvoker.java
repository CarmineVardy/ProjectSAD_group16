package it.unisa.diem.sad.geoshapes.controller.command;


public class CommandInvoker {

    private Command command;

    public void setCommand(Command command) {
        this.command = command;
    }

    public void executeCommand() {
        if (command != null) {
            command.execute();
        }
    }
}