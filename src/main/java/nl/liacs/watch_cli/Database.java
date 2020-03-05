package nl.liacs.watch_cli;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class Database {
    static String getDefaultDir() {
        var home = System.getProperty("user.home");
        // TODO
        return Paths.get(home, "watches", "database.db").toString();
    }

    private final Connection connection;

    public Database(@Nullable String dbFile) throws SQLException, IOException {
        if (dbFile == null) {
            dbFile = Database.getDefaultDir();
        }

        String url
            = "jdbc:sqlite:"
            + dbFile;

        this.connection = DriverManager.getConnection(url);
        connection.createStatement().execute("PRAGMA foreign_keys = ON"); // Make sure foreign keys are enforced

        this.connection.setAutoCommit(true);

        this.createTables();
    }

    private void createTables() throws IOException, SQLException {
        var stream = this.getClass().getResourceAsStream("/tables.sql");
        var bytes = stream.readAllBytes();
        var statements = new String(bytes).split(";");

        // HACK
        for (var text : statements) {
            text = text.trim();
            if (text.isEmpty()) {
                continue;
            }

            var stmt = this.connection.prepareStatement(text);
            stmt.execute();
        }
    }

    public void addWatch(@NotNull Smartwatch watch) throws SQLException {
        var command = "INSERT INTO smartwatch(id, name) VALUES(?, ?)";

        var stmt = this.connection.prepareStatement(command);
        stmt.setString(1, watch.getID());
        stmt.setString(2, watch.getName());
        stmt.executeUpdate();
    }

    private Smartwatch watchFromResult(ResultSet rs) throws SQLException {
        var id = rs.getString(1);
        var name = rs.getString(2);

        var watch = new Smartwatch(id, null);
        watch.setName(name);

        return watch;
    }

    public WatchList getAllWatches() throws SQLException {
        var res = new WatchList();
        var stmt = this.connection.prepareStatement("SELECT * FROM smartwatch");

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            var watch = this.watchFromResult(rs);
            res.add(watch);
        }
        rs.close();

        return res;
    }
}
