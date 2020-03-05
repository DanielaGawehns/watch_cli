package nl.liacs.watch_cli;

import java.util.ArrayList;
import java.util.List;

public class Tree<T> {
    private ArrayList<Tree<T>> children;
    private final T data;

    public List<Tree<T>> getChildren() {
        return this.children;
    }
    public T getData() {
        return this.data;
    }

    public Tree(T data) {
        this.data = data;
    }

    public void addChild(Tree<T> child) {
        this.children.add(child);
    }
}
