package org.geogebra.common.main;

abstract public class SchedulerFactory {

    public interface Scheduler {
        void schedule(Runnable runnable, int delay);
        void cancel();
    }

    private static volatile SchedulerFactory prototype;

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

    abstract public Scheduler createScheduler();

}
