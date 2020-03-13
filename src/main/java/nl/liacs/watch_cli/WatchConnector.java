package nl.liacs.watch_cli;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import nl.liacs.watch.protocol.server.WrappedConnection;
import nl.liacs.watch.protocol.types.Message;
import nl.liacs.watch.protocol.types.MessageParameter;
import nl.liacs.watch.protocol.types.MessageParameterString;
import nl.liacs.watch.protocol.types.MessageType;

/**
 * Connects a {@link Smartwatch} with a {@link WrappedConnection}.
 */
public class WatchConnector implements Closeable {
    private final Smartwatch watch;
    private final WrappedConnection connection;
    private final Thread thread;
    private final Thread batteryThread;
    private final ArrayList<Consumer<Datapoint>> incrementConsumers = new ArrayList<>();

    /**
     * Create a new {@link WatchConnector} and start the update loop.
     *
     * @param watch The watch to bind
     * @param connection The connection to bind
     * @param controller The controlle to bind
     */
    WatchConnector(@NotNull Smartwatch watch, @NotNull WrappedConnection connection) {
        this.watch = watch;
        this.connection = connection;

        this.thread = new Thread(() -> {
            try {
                this.updateLoop();
            } catch (InterruptedException e) {
                return;
            }
        });
        this.thread.start();

        /*
        this.batteryThread = new Thread(() -> {
            try {
                this.batteryLoop();
            } catch (IOException e) {
                System.out.println(e);
            } catch (InterruptedException e) {
                return;
            }
        });
        this.batteryThread.start();
        */
        this.batteryThread = null;
    }

    /**
     * The main update loop, should be run in a thread.
     *
     * @throws InterruptedException Interrupted error when the thread is interrupted.
     */
    private void updateLoop() throws InterruptedException {
        while (!this.connection.isClosed()) {
            Message item = this.connection.receive();

            switch (item.type) {
                case GET_VALUES:
                    var key = item.parameters[0].asString().getValue();
                    var res = getKeyValue(key);
                    if (res != null) {
                        item.makeReply(0, null, res);
                    } else { // key not available
                        item.makeReply(404, "key not found");
                    }
                    break;

                case SET_VALUES:
                    throw new IllegalArgumentException("TODO");

                case INCREMENT:
                case PLAYBACK: {
                    var sensor = item.parameters[0].asString().getValue();

                    var dateValue = item.parameters[1].asLong().getValue();
                    var date = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateValue), ZoneOffset.UTC);

                    var values = Arrays.stream(item.parameters)
                        .skip(2)
                        .mapToDouble((param) -> param.asDouble().getValue())
                        .toArray();

                    var dataPoint = new Datapoint(sensor, date, values);

                    if (item.type == MessageType.PLAYBACK) {
                        var list = Collections.singletonList(dataPoint);
                        watch.addDatapoints(list);
                    } else {
                        for (var consumer : this.incrementConsumers) {
                            consumer.accept(dataPoint);
                        }
                    }

                    break;
                }

                case PING:
                case REPLY:
                    throw new IllegalStateException("PING or REPLY leaked out of WrappedConnection");
            }
        }
    }

    /**
     * Stop the update loop.
     *
     * @throws IOException IO error when failing to close the connection.
     */
    @Override
    public void close() throws IOException {
        this.connection.close();
        this.thread.interrupt();
    }

    /**
     * Gets the value associated with {@code key}.
     * Returns {@code null} when the key couldn't be found.
     *
     * @param key The key to get the value of.
     * @return The value, or {@code null} if the key wasn't found.
     */
    @Nullable
    private MessageParameter[] getKeyValue(@NotNull String key) {
        if (key.equals("system.name")) { // just to test
            return new MessageParameter[]{
                new MessageParameterString(this.getClass().getPackageName()),
            };
        }

        return null;
    }

    /**
     * @return The connection of the current connector, or {@code null} if there
     * is none.
     */
    @Nullable
    public WrappedConnection getConnection() {
        if (this.isClosed()) {
            return null;
        }
        return this.connection;
    }

    /**
     * @return Whether or not the connection of the current connector is closed.
     * This will return {@code true} if there is no connection.
     */
    public boolean isClosed() {
        if (this.connection == null) {
            return true;
        }

        return this.connection.isClosed();
    }

    public void addIncrementConsumer(Consumer<Datapoint> consumer) {
        this.incrementConsumers.add(consumer);
    }
}
