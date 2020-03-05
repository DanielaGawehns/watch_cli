package nl.liacs.watch_cli.commands;

import java.util.NoSuchElementException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import nl.liacs.watch.protocol.server.WrappedConnection;
import nl.liacs.watch.protocol.types.MessageParameter;
import nl.liacs.watch.protocol.types.MessageParameterDouble;
import nl.liacs.watch.protocol.types.MessageParameterInteger;
import nl.liacs.watch.protocol.types.MessageParameterLong;
import nl.liacs.watch.protocol.types.MessageParameterString;
import nl.liacs.watch_cli.Main;

class Utils {
    @NotNull
    static WrappedConnection getWatchConnection(String deviceId) {
        var watch = Main.watches.getWithID(deviceId);
        if (watch == null) {
            var msg = String.format("no watch with ID '%s' found", deviceId);
            throw new NoSuchElementException(msg);
        }

        var connector = watch.getConnector();
        if (connector == null) {
            var msg = String.format("no connection with watch '%s'", deviceId);
            throw new IllegalStateException(msg);
        }

        return connector.getConnection();
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
}
