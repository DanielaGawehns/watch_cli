package nl.liacs.watch_cli;

import java.util.ArrayList;

public class WatchList extends ArrayList<Smartwatch> {
    public WatchList() {
        super();
    }
    /**
     * Find the index of the watch per its ID.
     * @param id The ID of the watch to find.
     * @return -1 if the watch is not found, it's index in the list othewise.
     */
    private int findWithID(String id) {
        for (int i = 0; i < this.size(); i++) {
            if (this.get(i).getID().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Get from list
     * @param id Watch ID
     */
    public Smartwatch getWithID(String id){
        int index = this.findWithID(id);
        if (index == -1) {
            return null;
        }

        return this.get(index);
    }

    /**
     * Removes the watch with the given ID from the list.
     * @param id The ID of the watch to remove.
     * @throws IllegalArgumentException Throws illegal argument exception when there is no watch found with the given ID.
     */
    public void removeWithID(String id) throws IllegalArgumentException {
        int index = this.findWithID(id);
        if (index == -1) {
            throw new IllegalArgumentException("no watch found with given id");
        }

        this.remove(index);
    }
}
