package nl.liacs.watch_cli.commands;

public class Exit implements Command {
    public String getDescription() {
        return "Exit";
    }

    public boolean checkArguments(Arguments args) {
        return args.isEmpty();
    }

    public void run(Arguments args) {
        System.exit(0);
    }
}
