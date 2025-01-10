package org.geogebra.common.jre.main;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.geogebra.common.main.SchedulerFactory;

/**
 * Factory for creating schedulers (JRE)
 */
public class SchedulerFactoryJre extends SchedulerFactory {

	/**
	 * Scheduler (JRE)
	 */
	static public class SchedulerJre implements Scheduler {
        private final ScheduledExecutorService scheduledExecutorService = Executors
                .newScheduledThreadPool(1);

        private ScheduledFuture<?> handler;

		@Override
        public void schedule(Runnable runnable, int delay) {
            handler = scheduledExecutorService.schedule(runnable,
                    delay,
                    TimeUnit.MILLISECONDS);
        }

		@Override
        public void cancel() {
			if (handler != null) {
                handler.cancel(false);
            }
        }
    }

    @Override
    public Scheduler createScheduler() {
        return new SchedulerJre();
    }
}
