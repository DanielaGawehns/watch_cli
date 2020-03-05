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
                    continue;
                }
            }

            this.rest.add(arg);
        }
    }

    @Nullable
    public String getString(String key) {
        return this.args.getOrDefault(key, null);
    }

    @Nullable
    public Integer getInt(String key) {
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
