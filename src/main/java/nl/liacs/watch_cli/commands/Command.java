package nl.liacs.watch_cli.commands;

public interface Command {
    String getDescription();
    String getUsage();
    boolean checkArguments(Arguments args);
    void run(Arguments args);
}
