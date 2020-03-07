package nl.liacs.watch_cli;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic tree class representing a tree node.
 * @param <T> The type of the data on every node.
 */
public class Tree<T> {
    private ArrayList<Tree<T>> children;
    private final T data;

    /**
     * @return The children of the current node.
     */
    public List<Tree<T>> getChildren() {
        return this.children;
    }
    /**
     * @return The data of the current node.
     */
    public T getData() {
        return this.data;
    }

    /**
     * Create a new tree node with the given data.
     * @param data The data of the node.
     */
    public Tree(T data) {
        this.data = data;
    }

    /**
     * Add a child to the curent node.
     * @param child The child tree to add.
     */
    public void addChild(Tree<T> child) {
        this.children.add(child);
    }
}
