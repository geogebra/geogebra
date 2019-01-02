package org.geogebra.common.jre.main;

import org.geogebra.common.main.SchedulerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SchedulerFactoryJre extends SchedulerFactory {

    public class SchedulerJre implements Scheduler {
        private final ScheduledExecutorService scheduledExecutorService = Executors
                .newScheduledThreadPool(1);

        private ScheduledFuture<?> handler;

        public void schedule(Runnable runnable, int delay) {
            handler = scheduledExecutorService.schedule(runnable,
                    delay,
                    TimeUnit.MILLISECONDS);
        }

        public void cancel() {
            if (handler!=null) {
                handler.cancel(false);
            }
        }
    }

    @Override
    public Scheduler createScheduler() {
        return new SchedulerJre();
    }
}
