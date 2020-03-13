package nl.liacs.watch_cli.commands;

import nl.liacs.watch_cli.Main;

public class Help implements Command {
    public String getDescription() {
        return "Show program help";
    }

    public boolean checkArguments(Arguments args) {
        int count = args.getRest().size();
        return count == 0 || count == 1;
    }

    public void run(Arguments args) {
        var rest = args.getRest();
        if (rest.size() == 0) {
            System.out.println("commands");

            for (var key : Main.commands.keySet()) {
                var cmd = Main.commands.get(key);
                var description = cmd.getDescription();
                System.out.printf("  %s: %s\n", key, description.split("\n")[0]);
            }
            return;
        }

        var cmd = Main.commands.get(rest.get(0));
        if (cmd == null) {
            System.err.printf("command '%s' not found\n", rest.get(0));
            return;
        }

        System.out.printf("%s\n\n", rest.get(0));
        System.out.println(cmd.getDescription());
    }
}
