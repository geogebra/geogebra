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

package org.geogebra.common.main;

/**
 * factory for creating schedulers
 *
 */
abstract public class SchedulerFactory {

	private static volatile SchedulerFactory prototype;

	/**
	 * scheduler interface
	 *
	 */
	static public interface Scheduler {
		/**
		 * schedule a runnable to run after a delay
		 * 
		 * @param runnable
		 *            runnable
		 * @param delay
		 *            delay
		 */
        void schedule(Runnable runnable, int delay);

		/**
		 * cancel the scheduled action
		 */
        void cancel();
    }

    /**
     * @param factory prototype
     */
    public static void setPrototypeIfNull(SchedulerFactory factory) {
        if (prototype == null) {
            prototype = factory;
        }
    }

    /**
     * @return might return null
     */
    public static SchedulerFactory getPrototype() {
        return prototype;
    }

	/**
	 * 
	 * @return a new scheduler
	 */
    abstract public Scheduler createScheduler();

}
