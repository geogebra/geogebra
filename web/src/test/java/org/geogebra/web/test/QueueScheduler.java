package org.geogebra.web.test;

import com.google.gwt.core.client.impl.SchedulerImpl;

public class QueueScheduler extends SchedulerImpl {
	@Override
	public void scheduleDeferred(ScheduledCommand cmd) {
		cmd.execute();
	}

	@Override
	public void scheduleEntry(RepeatingCommand cmd) {
	// implement this by demand.
	}

	@Override
	public void scheduleEntry(ScheduledCommand cmd) {
		// implement this by demand.
	}

	@Override
	public void scheduleFinally(RepeatingCommand cmd) {
		// implement this by demand.
	}

	@Override
	public void scheduleFinally(ScheduledCommand cmd) {
		// implement this by demand.
	}

	@Override
	public void scheduleFixedDelay(RepeatingCommand cmd, int delayMs) {
		// implement this by demand.
	}

	@Override
	public void scheduleFixedPeriod(RepeatingCommand cmd, int delayMs) {
		// implement this by demand.
	}

	@Override
	public void scheduleIncremental(RepeatingCommand cmd) {
		// implement this by demand.
	}
}
