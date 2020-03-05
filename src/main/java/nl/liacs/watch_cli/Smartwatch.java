package nl.liacs.watch_cli;

import java.io.IOException;

import org.jetbrains.annotations.Nullable;

import nl.liacs.watch.protocol.server.WrappedConnection;

public class Smartwatch {
    private String id;
    private String name;
    private WatchConnector connector = null;

    public String getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public WatchConnector getConnector() {
        return this.connector;
    }

    Smartwatch(String id, @Nullable WrappedConnection conn) {
        this.id = id;
        if (conn != null) {
            try {
                this.addConnection(conn);
            } catch (IOException e) {
                // cannot happen
            }
        }
    }

    public void addConnection(WrappedConnection connection) throws IOException {
        if (this.connector != null) {
            this.connector.close();
        }
        this.connector = new WatchConnector(this, connection);
    }
}
