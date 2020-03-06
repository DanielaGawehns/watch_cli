package nl.liacs.watch_cli.commands;

import java.io.IOException;
import java.util.concurrent.Executors;

import nl.liacs.watch.protocol.server.WrappedConnection;
import nl.liacs.watch.protocol.types.Message;
import nl.liacs.watch.protocol.types.MessageParameterLong;

public class Live implements Command {
    public String getDescription() {
        return "Show a live view of all enabled sensors with the given interval of the given devices.  Device IDs are comma delimited";
    }

    public boolean checkArguments(Arguments args) {
        return args.getRest().size() == 3;
    }

    public void run(Arguments args) {
        var deviceIds = args.getRest().get(0).split(",");
        var interval = Integer.parseInt(args.getRest().get(1));

        WrappedConnection[] conns = new WrappedConnection[deviceIds.length];
        for (int i = 0; i < deviceIds.length; i++) {
            var conn =  Utils.getWatchConnection(deviceIds[i]);;

            if (conn == null) {
                System.err.printf("watch with id '%s' not found or not connected\n", deviceIds[i]);
                return;
            }
            conns[i] = conn;

            try {
                conn.setValues("live.interval", new MessageParameterLong(interval));
            } catch (IOException e) {
                System.out.println(e.getStackTrace());
                System.exit(1);
            }
        }

        var pool = Executors.newFixedThreadPool(conns.length);
        for (var conn : conns) {
            pool.submit(() -> {
                while (!conn.isClosed()) {
                    Message msg;
                    try {
                        msg = conn.receive();
                    } catch (InterruptedException e) {
                        return;
                    }

                    System.out.println(msg.toString());
                }
            });
        }

        try {
            System.in.read();
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
            System.exit(1);
        }

        pool.shutdownNow();
    }
}
