package nl.liacs.watch_cli.commands;

import java.io.IOException;

import nl.liacs.watch.protocol.types.MessageParameter;

public class SetKey implements Command {
    public String getDescription() {
        return "Set the value(s) for the given key for the given device";
    }

    public boolean checkArguments(Arguments args) {
        // deviceId, key, args...
        return args.getRest().size() >= 3;
    }

    public void run(Arguments args) {
        var rest = args.getRest();
        var deviceId = rest.get(0);
        var key = rest.get(1);
        var values = rest.subList(2, rest.size());

        var conn = Utils.getWatchConnection(deviceId);
        if (conn == null) {
            System.err.println("watch not found or not connected");
            return;
        }

        if (values.size() % 2 == 1) {
            System.err.println("uneven amount of value arguments");
            return;
        }

        MessageParameter[] params = new MessageParameter[values.size() / 2];
        for (int i = 1; i < values.size(); i += 2) {
            var type = values.get(i - 1);
            var valueRaw = values.get(i);

            var param = Utils.parseParameter(type, valueRaw);
            if (param == null) {
                System.err.printf("unknown parameter type '%s'\n", type);
                return;
            }

            params[i / 2] = param;
        }

        try {
            conn.setValues(key, params);
        } catch (IOException e) {
            System.out.println(e.toString());
            System.exit(1);
        }
    }
}
