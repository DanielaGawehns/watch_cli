package nl.liacs.watch_cli;

import java.time.LocalDateTime;

/**
 * Datapoint represents a data point of a sensor recording.
 */
public class Datapoint {
    private final String sensor;
    private final LocalDateTime date;
    private final double[] data;

    Datapoint(String sensor, LocalDateTime date, double[] data) {
        this.sensor = sensor;
        this.date = date;
        this.data = data;
    }

    /**
     * @return The sensor of this data point.
     */
    public String getSensor() {
        return this.sensor;
    }

    /**
     * @return The date of this data point.
     */
    public LocalDateTime getDate() {
        return this.date;
    }

    /**
     * @return The data of this data point.
     */
    public double[] getData() {
        return this.data;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        for (var data : this.getData()) {
            sb.append(data);
            sb.append(", ");
        }
        return String.format("[%s] (%s) { %s }", this.getSensor(), this.getDate(), sb.toString());
    }
}
