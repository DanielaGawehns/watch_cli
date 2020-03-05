package nl.liacs.watch_cli.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import nl.liacs.watch_cli.Main;
import nl.liacs.watch.protocol.server.WrappedConnection;
import nl.liacs.watch.protocol.types.MessageParameter;

public class Help implements Command {
    public String getDescription() {
        return "Show program help";
    }

    public boolean checkArguments(Arguments args) {
        // REVIEW: 0 || 1?
        return args.getRest().size() == 0;
    }
    public void run(Arguments args) {
        System.out.println("commands");

        for (var key : Main.commands.keySet()) {
            var cmd = Main.commands.get(key);
            System.out.printf("  %s: %s\n", key, cmd.getDescription());
        }
    }
}
