package nl.liacs.watch_cli;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import nl.liacs.watch.protocol.server.WrappedConnection;

/**
 * Smartwatch represents a watch device.
 */
public class Smartwatch {
    private String id;
    private String name;
    private WatchConnector connector = null;
    private final ArrayList<Datapoint> datapoints = new ArrayList<>();

    /**
     * @return The ID of the current watch.
     */
    public String getID() {
        return this.id;
    }

    /**
     * @return The human friendly name of the current watch.
     */
    public String getName() {
        return this.name;
    }
    /**
     * Set the name of the current watch.
     * @param name A human friendly name to use for the current watch.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The watch connector for the current watch, if any.
     */
    @Nullable
    public WatchConnector getConnector() {
        return this.connector;
    }

    /**
     * Create a new instance with the given {@code id} and {@code conn}
     * @param id The ID of the watch to use.
     * @param conn The {@link WrappedConnection} for the watch to use, can be
     * null.
     */
    Smartwatch(@NotNull String id, @Nullable WrappedConnection conn) {
        this.id = id;
        if (conn != null) {
            try {
                this.addConnection(conn);
            } catch (IOException e) {
                // cannot happen, since the connector is null.
            }
        }
    }

    /**
     * Add a new connection to the current watch.
     * Closes and removes the old connection, if there was any.
     * @param connection The connection to use.
     * @throws IOException IO error when failing to close the previous
     * connection.
     */
    public void addConnection(WrappedConnection connection) throws IOException {
        if (this.connector != null) {
            this.connector.close();
        }
        this.connector = new WatchConnector(this, connection);
    }

    /**
     * @return Whether or not the connection of the current watch is closed.
     * This will return {@code true} if there is no connector or connection.
     */
    public boolean isClosed() {
        if (this.connector == null) {
            return true;
        }

        return this.connector.isClosed();
    }

    public void addDatapoints(Collection<Datapoint> datapoint) {
        this.datapoints.addAll(datapoint);
    }

    public List<Datapoint> getDatapoints() {
        return Collections.unmodifiableList(this.datapoints);
    }
}
