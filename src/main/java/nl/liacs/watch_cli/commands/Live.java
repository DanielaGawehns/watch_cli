package nl.liacs.watch_cli.commands;

import java.io.IOException;

import nl.liacs.watch.protocol.types.MessageParameterLong;
import nl.liacs.watch_cli.WatchConnector;

public class Live implements Command {
    private boolean isRunning;

    public String getDescription() {
        return "Show a live view of all enabled sensors with the given interval of the given devices.\nDevice IDs are comma delimited.";
    }

    public boolean checkArguments(Arguments args) {
        return args.getRest().size() == 3;
    }

    public void run(Arguments args) {
        var deviceIds = args.getRest().get(0).split(",");
        var interval = Integer.parseInt(args.getRest().get(1));

        WatchConnector[] connectors = new WatchConnector[deviceIds.length];
        for (int i = 0; i < deviceIds.length; i++) {
            var connector = Utils.getConnector(deviceIds[i]);

            if (connector.isClosed()) {
                System.err.printf("watch with id %s not connected\n", deviceIds[i]);
                return;
            }

            connectors[i] = connector;

            try {
                connector.getConnection().setValues("live.interval", new MessageParameterLong(interval));
            } catch (IOException e) {
                System.out.println(e.getStackTrace());
                System.exit(1);
            }
        }

        this.isRunning = true;

        for (var connector : connectors) {
            connector.addIncrementConsumer(point -> {
                if (this.isRunning) {
                    System.out.printf("%s\n", point);
                }
            });
        }

        try {
            System.in.read();
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
            System.exit(1);
        }

        this.isRunning = false;
    }
}
