package nl.liacs.watch_cli.commands;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import nl.liacs.watch.protocol.types.Datapoint;
import nl.liacs.watch_cli.Main;

public class Retrieve implements Command {
    public String getDescription() {
        return "Retrieve data points from the given watch(es) between the start and end";
    }

    public String getUsage() {
        return "[--start=date] [--end=date] <device ids...>";
    }

    public boolean checkArguments(Arguments args) {
        return args.getRest().size() > 0;
    }

    public void run(Arguments args) {
        var deviceIds = args.getRest();
        var startRaw = args.getString("start");
        var endRaw = args.getString("end");

        var begin
            = startRaw == null
            ? Instant.ofEpochMilli(Long.MIN_VALUE)
            : Instant.parse(startRaw);
        var end
            = endRaw == null
            ? Instant.ofEpochMilli(Long.MAX_VALUE)
            : Instant.parse(endRaw);

        var futures = deviceIds.stream()
            .map(id -> {
                var conn = Utils.getWatchConnection(id);
                try {
                    return conn.getPlayback(begin, end);
                } catch (IOException e) {
                    Main.logger.error("error while getting playback for watch " + id);
                }

                return CompletableFuture.completedFuture(new ArrayList<>(0));
            })
            .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(futures).join();

        for (int i = 0; i < futures.length; i++) {
            var watch = Utils.getWatch(deviceIds.get(i));
            var datapoints = (ArrayList<Datapoint>) futures[i].join();

            watch.addDatapoints(datapoints);
        }
    }
}
