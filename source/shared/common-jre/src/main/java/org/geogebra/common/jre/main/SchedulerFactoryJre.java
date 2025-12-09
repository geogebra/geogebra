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
