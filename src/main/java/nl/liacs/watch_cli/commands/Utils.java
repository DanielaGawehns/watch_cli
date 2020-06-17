package nl.liacs.watch_cli.commands;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import nl.liacs.watch.protocol.server.WrappedConnection;
import nl.liacs.watch.protocol.types.MessageParameter;
import nl.liacs.watch.protocol.types.MessageParameterDouble;
import nl.liacs.watch.protocol.types.MessageParameterInteger;
import nl.liacs.watch.protocol.types.MessageParameterLong;
import nl.liacs.watch.protocol.types.MessageParameterString;
import nl.liacs.watch_cli.Main;
import nl.liacs.watch_cli.Smartwatch;
import nl.liacs.watch_cli.WatchConnector;

class Utils {
    @NotNull
    static Smartwatch getWatch(String deviceIndex) {
        var index = Integer.parseInt(deviceIndex);
        if (index >= Main.watches.size()) {
            var msg = String.format("watch index %d is out of bounds", index);
            throw new NoSuchElementException(msg);
        }
        return Main.watches.get(index);
    }

    @NotNull
    static WatchConnector getConnector(String deviceIndex) {
        var watch = getWatch(deviceIndex);

        var connector = watch.getConnector();
        if (connector == null) {
            var msg = String.format("no connector to watch with ID %s", watch.getID());
            throw new IllegalStateException(msg);
        }
        return connector;
    }

    @NotNull
    static WrappedConnection getWatchConnection(String deviceIndex) {
        var connector = getConnector(deviceIndex);

        var conn = connector.getConnection();
        if (conn == null) {
            var watchId = getWatch(deviceIndex).getID();
            var msg = String.format("no connection with watch with ID %s", watchId);
            throw new IllegalStateException(msg);
        }
        return conn;
    }

    @Nullable
    static MessageParameter parseParameter(String type, String valueRaw) {
        if ("string".startsWith(type)) {
            return new MessageParameterString(valueRaw);
        } else if ("int".startsWith(type)) {
            var value = Integer.parseInt(valueRaw);
            return new MessageParameterInteger(value);
        } else if ("long".startsWith(type)) {
            var value = Long.parseLong(valueRaw);
            return new MessageParameterLong(value);
        } else if ("double".startsWith(type)) {
            var value = Double.parseDouble(valueRaw);
            return new MessageParameterDouble(value);
        }

        return null;
    }

    /**
     * Returns the watches with the given watch indices.
     * If the indices list is empty, all watches known are returned.
     * @param ids The list of watch indices.
     * @return The watches that match the watch indices, or all if no indices
     * are given.
     */
    static List<Smartwatch> getWatchesFromIndicesOrAll(Collection<String> ids) {
        var watches = ids
            .stream()
            .map(i -> Utils.getWatch(i))
            .collect(Collectors.toList());

        if (watches.isEmpty()) {
            watches = Main.watches;
        }

        return watches;
    }
}
