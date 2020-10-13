package nl.liacs.watch_cli;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.logging.Level;

/**
 * Logger is a generic logger system with a maximum capacity.
 * @param <T> The type of every item content.
 */
public class Logger<T> {
    /**
     * Item is an entry in the log.
     * @param <T> The type of the content.
     */
    public static class Item<T> {
        public Level level;
        public LocalDateTime date;
        public T contents;
    }

    int capacity = 5000;
    final ArrayDeque<Item<T>> items;

    /**
     * Create a new instance
     */
    public Logger() {
        this.items = new ArrayDeque<>();
    }

    /**
     * Logs the given contents with the given level.
     * @param level The level to use for the entry.
     * @param contents The contents of the entry.
     */
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

    /**
     * Shortcut for logging with an "info" level.
     * @param contents The coennts of the entry.
     */
    public void info(T contents) {
        this.log(Level.INFO, contents);
    }

    /**
     * Shortcut for logging with a "warning" level.
     * @param contents The coennts of the entry.
     */
    public void warning(T contents) {
        this.log(Level.WARNING, contents);
    }

    /**
     * Shortcut for logging with a "severe" level.
     * @param contents The coennts of the entry.
     */
    public void error(T contents) {
        this.log(Level.SEVERE, contents);
    }

    /**
     * @return All the items in the log.
     */
    public Queue<Item<T>> getItems() {
        return this.items;
    }

    /**
     * Clear the log contents.
     */
    public void clear() {
        this.items.clear();
    }

    /**
     * @return The maximum amount of entries that this instance is able to hold.
     */
    public int getCapacity() {
        return this.capacity;
    }
}
