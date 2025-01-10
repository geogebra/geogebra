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
