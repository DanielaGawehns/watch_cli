package nl.liacs.watch_cli.commands;

import java.util.ArrayList;

import com.github.freva.asciitable.AsciiTable;

import nl.liacs.watch_cli.Main;

public class Devices implements Command {
    public String getDescription() {
        return "Show all devices that are known";
    }

    public boolean checkArguments(Arguments args) {
        return args.isEmpty();
    }

    public void run(Arguments args) {
        String[] headers = {"ID", "Name", "Connected"};
        ArrayList<String[]> data = new ArrayList<>();

        for (var watch : Main.watches) {
            var name = watch.getName();
            if (name == null) {
                name = "";
            }

            var connected = watch.getConnector() != null ? "yes" : "no";

            String[] line = { watch.getID(), name, connected };
            data.add(line);
        }

        var table = AsciiTable.getTable(headers, data.toArray(new String[0][]));
        System.out.println(table);
    }
}
