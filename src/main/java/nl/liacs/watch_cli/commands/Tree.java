package nl.liacs.watch_cli.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import nl.liacs.watch.protocol.server.WrappedConnection;
import nl.liacs.watch.protocol.types.MessageParameter;

public class Tree implements Command {
    public String getDescription() {
        return "Show the full key-value store for the given device";
    }

    private static class Node {
        String key;
        MessageParameter[] values;
    }

    public boolean checkArguments(Arguments args) {
        // REVIEW: 1 || 2?
        return args.getRest().size() == 1;
    }

    @Nullable
    private static CompletableFuture<MessageParameter[]> getListing(WrappedConnection conn, String namespace) {
        String key = namespace + ".list";
        if (namespace.isEmpty()) {
            key = "list";
        }

        try {
            return conn.getValues(key);
        } catch (IOException e) {
            return null;
        }
    }

    @Nullable
    private static CompletableFuture<MessageParameter[]> getValues(WrappedConnection conn, String key) {
        try {
            return conn.getValues(key);
        } catch (IOException e) {
            return null;
        }
    }

    @Nullable
    private static CompletableFuture<nl.liacs.watch_cli.Tree<Node>> getTree(WrappedConnection conn, String namespace) {
        var listFut = Tree.getListing(conn, namespace);
        var valuesFut = Tree.getValues(conn, namespace);
        if (listFut == null || valuesFut == null) {
            return null;
        }


        return CompletableFuture.allOf(listFut, valuesFut).thenCompose(x -> {
            var items = listFut.join();
            var vals = valuesFut.join();

            var node = new Node();
            node.key = namespace;
            node.values = vals;
            var resTree = new nl.liacs.watch_cli.Tree<Node>(node);

            var treeFuts = new ArrayList<CompletableFuture<Void>>();
            for (var item : items) {
                var key = item.asString().toString();

                var treeFut = Tree.getTree(conn, key);
                if (treeFut == null) {
                    continue;
                }

                var newFut = treeFut.thenAccept(tree -> resTree.addChild(tree));
                treeFuts.add(newFut);
            }

            var fut = CompletableFuture.allOf(treeFuts.toArray(new CompletableFuture[treeFuts.size()]));
            return fut.thenApply(y -> resTree);
        });
    }

    public static void printTree(nl.liacs.watch_cli.Tree<Node> tree, String prefix) {
        var data = tree.getData();
        var val = String.join(", ", Arrays.stream(data.values).map(v -> v.toString()).toArray(String[]::new));
        System.out.printf("%s%s : %s", prefix, data.key, val);

        for (var child : tree.getChildren()) {
            Tree.printTree(child, prefix + "  ");
        }
    }

    public void run(Arguments args) {
        var deviceId = args.getRest().get(0);

        var conn = Utils.getWatchConnection(deviceId);

        var fut = Tree.getTree(conn, "");
        if (fut == null) {
            System.out.println("error while retrieving tree");
            return;
        }

        var tree = fut.join();
        Tree.printTree(tree, "");
    }
}
