package org.geogebra.web.full.gui.toolbarpanel.spreadsheet;

import org.gwtproject.core.client.Scheduler;

import elemental2.dom.Element;

public class FocusCommand implements Scheduler.ScheduledCommand {
	private final Element element;
	private boolean canceled;

	public FocusCommand(Element element) {
		this.element = element;
	}

	@Override
	public void execute() {
		if (!canceled) {
			element.focus();
		}
	}

	/**
	 * Cancel this command.
	 */
	public void cancel() {
		canceled = true;
	}
}
