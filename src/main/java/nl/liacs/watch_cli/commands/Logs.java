package nl.liacs.watch_cli.commands;

import java.util.Queue;

import nl.liacs.watch_cli.Logger.Item;
import nl.liacs.watch_cli.Main;

public class Logs implements Command {
    public String getDescription() {
        return "Show all (or the last n) entries from the system logs";
    }

    public String getUsage() {
        return "[tail count]";
    }

    public boolean checkArguments(Arguments args) {
        var n = args.getRest().size();
        return n == 0 || n == 1;
    }

    public void run(Arguments args) {
        int tail = Main.logger.getCapacity();
        if (args.getRest().size() == 1) {
            tail = Integer.parseInt(args.getRest().get(0));
        }

        Queue<Item<String>> items = Main.logger.getItems();

        int toSkip = Math.max(0, items.size() - tail);

        for (var item : items) {
            if (toSkip-- > 0) {
                continue;
            }

            System.out.printf("[%s] [%s] %s\n", item.date, item.level, item.contents);
        }
    }
}
