package nl.liacs.watch_cli;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class Utils {
    /**
     * Get the path to the data directory.
     * This method will create the directory if it does not exist yet.
     * @return The path to the data directory.
     * @throws IOException IO error when failing to create the directory.
     * @throws FileAlreadyExistsException If there is a file at the data
     * directory.
     */
    public static Path getDataDir() throws IOException, FileAlreadyExistsException {
        Path path;
        var pathString = System.getProperty("data.path");
        if (pathString != null) {
            path = Paths.get(pathString);
        } else {
            var home = System.getProperty("user.home");
            path = Paths.get(home, "watch_data");
        }

        if (!Files.exists(path)) {
            Files.createDirectory(path);
        } else if (!Files.isDirectory(path)) {
            throw new FileAlreadyExistsException(path.toString());
        }

        return path;
    }
}
