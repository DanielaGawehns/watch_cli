package nl.liacs.watch_cli.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import nl.liacs.watch.protocol.server.WrappedConnection;
import nl.liacs.watch.protocol.types.MessageParameter;

public class Tree implements Command {
    public String getDescription() {
        return "Show the full key-value store for the given device";
    }

    public String getUsage() {
        return "<device id>";
    }

    private static class Node {
        String key;
        String type;
        MessageParameter[] values;
    }

    public boolean checkArguments(Arguments args) {
        // REVIEW: 1 || 2?
        return args.getRest().size() == 1;
    }

    @Nullable
    private static CompletableFuture<String[]> getListing(@NotNull WrappedConnection conn, @NotNull String namespace) {
        String key = namespace + ".list";
        if (namespace.isEmpty()) {
            key = "list";
        }

        try {
            return conn.getValues(key).thenApply(items ->
                Arrays.stream(items)
                    .map(i -> i.asString().getValue())
                    .toArray(String[]::new)
            );
        } catch (IOException e) {
            return null;
        }
    }

    @NotNull
    private static CompletableFuture<String> getType(@NotNull WrappedConnection conn, @NotNull String key) {
        if (key.isEmpty()) {
            key = "type";
        } else {
            key += ".type";
        }

        try {
            return conn.getValues(key).thenApply(param -> param[0].asString().getValue());
        } catch (IOException e) {
            return null;
        }
    }

    @Nullable
    private static CompletableFuture<MessageParameter[]> getValues(@NotNull WrappedConnection conn, @NotNull String key) {
        try {
            return conn.getValues(key);
        } catch (IOException e) {
            return null;
        }
    }

    @Nullable
    private static CompletableFuture<nl.liacs.watch_cli.Tree<Node>> getTree(@NotNull WrappedConnection conn, @NotNull String namespace) {
        var typeFut = Tree.getType(conn, namespace);
        if (typeFut == null) {
            return null;
        }
        var type = typeFut.join();

        String[] items = new String[0];
        MessageParameter[] values = new MessageParameter[0];

        if (!type.equals("namespace") && !type.equals("root")) {
            var valuesFut = Tree.getValues(conn, namespace);
            if (valuesFut == null) {
                return null;
            }
            values = valuesFut.join();
        } else {
            var listFut = Tree.getListing(conn, namespace);
            if (listFut == null) {
                return null;
            }
            items = listFut.join();
        }

        var node = new Node();
        node.key = namespace;
        node.type = type;
        node.values = values;
        var resTree = new nl.liacs.watch_cli.Tree<Node>(node);

        var treeFuts = new ArrayList<CompletableFuture<Void>>();
        System.out.printf("len(items): %d\n", items.length);
        for (var key : items) {
            System.out.printf("%s\n", key);

            String prefix = "";
            if (!namespace.isEmpty()) {
                prefix = namespace + ".";
            }

            var treeFut = Tree.getTree(conn, prefix+key);
            if (treeFut == null) {
                continue;
            }

            var newFut = treeFut.thenAccept(tree -> resTree.addChild(tree));
            treeFuts.add(newFut);
        }

        var fut = CompletableFuture.allOf(treeFuts.toArray(new CompletableFuture[treeFuts.size()]));
        return fut.thenApply(y -> resTree);
    }

    public static void printTree(@NotNull nl.liacs.watch_cli.Tree<Node> tree, @NotNull String prefix) {
        var data = tree.getData();
        var val = String.join(", ", Arrays.stream(data.values).map(v -> v.getValue()).toArray(String[]::new));
        System.out.printf("%s%s : %s\n", prefix, data.key, val);

        for (var child : tree.getChildren()) {
            Tree.printTree(child, prefix + "  ");
        }
    }

    public void run(Arguments args) {
        var deviceId = args.getRest().get(0);

        var conn = Utils.getWatchConnection(deviceId);
        if (conn == null) {
            System.err.println("watch not found or not connected");
            return;
        }

        var fut = Tree.getTree(conn, "");
        if (fut == null) {
            System.out.println("error while retrieving tree");
            return;
        }

        var tree = fut.join();
        Tree.printTree(tree, "");
    }
}
