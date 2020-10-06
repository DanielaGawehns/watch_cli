package nl.liacs.watch_cli.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.List;

import org.jetbrains.annotations.Nullable;

public class Arguments {
    private final HashMap<String, String> args;
    private final List<String> rest;
    private final List<String> raw;

    public Arguments(List<String> raw) {
        this.raw = raw;
        this.args = new HashMap<>();
        this.rest = new ArrayList<>();

        // if skipping is true, all words will be regarded as non-flag
        // arugments.  This happens after a `--`.
        boolean skipping = false;

        for (int i = 0; i < raw.size(); i++) {
            var arg = raw.get(i);
            if (arg.isBlank()) {
                continue;
            }

            if (!skipping && arg.equals("--")) {
                skipping = true;
                continue;
            } else if (!skipping && arg.startsWith("--")) {
                if (arg.contains("=")) {
                    // delimited by a '='

                    String[] splitted = arg.split("=");
                    String key = splitted[0].substring(2);
                    String value = splitted[1];
                    this.args.put(key, value);
                } else {
                    // delimited by whitespace

                    String key = arg.substring(2);
                    String value = raw.get(i + 1);
                    this.args.put(key, value);
                    i++;
                }

                continue;
            }

            this.rest.add(arg);
        }
    }

    /**
     * Get the string value with the given {@code key}.
     * @param key The key of the argument to return.
     * @return The value of the argument with the given {@code key}. Returns
     * {@code null} when the given key was not found.
     */
    @Nullable
    public String getString(String key) {
        return this.args.getOrDefault(key, null);
    }

    /**
     * Get the value with the given {@code key} as an integer.
     * @param key The key of the argument to return.
     * @return The value of the argument with the given {@code key} converted to
     * an integer. Returns {@code null} when the given key was not found.
     * @throws NumberFormatException When failing to convert the argument value
     * to an integer.
     */
    @Nullable
    public Integer getInt(String key) throws NumberFormatException {
        var str = this.getString(key);
        if (str == null) {
            return null;
        }

        return Integer.parseInt(key);
    }

    /**
     * Get the value with the given {@code key} as a boolean value.
     * @param key The key of the argument to return.
     * @return The value of the argument with the given {@code key} converted to
     * a boolean. Returns {@code null} when the given key was not found.
     * @throws IllegalArgumentException When failing to convert the argument
     * value to a boolean.
     */
    @Nullable
    public Boolean getBoolean(String key) throws IllegalArgumentException {
        var str = this.getString(key);

        if (str == null) {
            return null;
        } else if (str.equals("true") || str.equals("1") || str.equals("yes") || str.equals("t") || str.equals("y")) {
            return true;
        } else if (str.equals("false") || str.equals("0") || str.equals("no") || str.equals("f") || str.equals("n")) {
            return false;
        }

        throw new IllegalArgumentException();
    }

    public List<String> getRest() {
        return this.rest;
    }

    public List<String> getRaw() {
        return this.raw;
    }

    public boolean isEmpty() {
        return this.raw.isEmpty();
    }
}
