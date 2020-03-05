package nl.liacs.watch_cli.commands;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import com.github.freva.asciitable.AsciiTable;

import nl.liacs.watch.protocol.types.MessageParameter;

public class GetKey implements Command {
    public String getDescription() {
        return "Retrieve the value(s) of the given key on the given device";
    }

    public boolean checkArguments(Arguments args) {
        return args.getRest().size() == 2;
    }

    public void run(Arguments args) {
        var deviceId = args.getRest().get(0);
        var key = args.getRest().get(1);

        var conn = Utils.getWatchConnection(deviceId);

        CompletableFuture<MessageParameter[]> fut;
        try {
            fut = conn.getValues(key);
        } catch (IOException e) {
            System.out.println(e.toString());
            System.exit(1);
            return;
        }

        var params = fut.join();

        String[] headers = new String[params.length];
        String[][] data = new String[1][params.length];
        for (int i = 0; i < params.length; i++) {
            headers[i] = Integer.toString(i);
            data[0][i] = params[i].toString();
        }

        var table = AsciiTable.getTable(headers, data);
        System.out.println(table);
    }
}
