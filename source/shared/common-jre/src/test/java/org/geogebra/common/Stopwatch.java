/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
