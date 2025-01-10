package org.geogebra.common;

/**
 * Simple class to measure time.
 */
public class Stopwatch {

    private long start;
    private boolean started;

    /**
     * Create a stopwatch.
     */
    public Stopwatch() {
        started = false;
    }

    /**
     * Starts the stopwatch.
     */
    public void start() {
        start = System.currentTimeMillis();
        started = true;
    }

    /**
     * Stops the stopwatch.
     *
     * @return the elapsed time in milliseconds
     */
    public long stop() {
        if (!started) {
            throw new RuntimeException("Stopwatch was not started");
        }
        long millis = System.currentTimeMillis() - start;
        started = false;
        return millis;
    }
}
