package nl.liacs.watch_cli.commands;

import java.util.ArrayList;
import java.util.HashMap;
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

        boolean skipping = false;
        for (int i = 0; i < raw.size(); i++) {
            var arg = raw.get(i);

            if (!skipping) {
                if (arg.equals("--")) {
                    skipping = true;
                    continue;
                } else if (arg.startsWith("--")) {
                    String key = arg.substring(2);
                    this.args.put(key, raw.get(i + 1));
                    i++;
                    continue;
                }
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
