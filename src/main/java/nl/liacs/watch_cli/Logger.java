package nl.liacs.watch_cli;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.logging.Level;

public class Logger<T> {
    public static class Item<T> {
        public Level level;
        public LocalDateTime date;
        public T contents;
    }

    int capacity = 5000;
    final ArrayDeque<Item<T>> items;

    public Logger() {
        this.items = new ArrayDeque<>();
    }

    public void log(Level level, T contents) {
        if (this.items.size() >= capacity) {
            this.items.removeFirst();
        }

        Item<T> item = new Item<>();
        item.level = level;
        item.contents = contents;
        item.date = LocalDateTime.now();

        this.items.addLast(item);
    }

    public void info(T contents) {
        this.log(Level.INFO, contents);
    }

    public Queue<Item<T>> getItems() {
        return this.items;
    }

    public void clear() {
        this.items.clear();
    }

    public int getCapacity() {
        return this.capacity;
    }
}
