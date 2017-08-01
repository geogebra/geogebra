package org.geogebra.web.web.gui.toolbarpanel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

public class LandscapeAnimationCallback extends HeaderAnimationCallback {

	private static final int OPEN_HEIGHT = 56;

	public LandscapeAnimationCallback(Header header, int expandFrom, int expandTo) {
		super(header, expandFrom, expandTo);
	}

	@Override
	protected void onStart() {
		header.hideUndoRedoPanel();
		if (header.isOpen()) {
			header.setHeight(OPEN_HEIGHT + "px");
		}
	}

	@Override
	public void tick(double progress) {
		double p = header.isOpen() ? progress : 1 - progress;
		double w = getDiff() * p;
		header.expandWidth(getExpandTo() + Math.abs(w));

	}

	@Override
	protected void onEnd() {
		if (!header.isOpen()) {
			header.expandWidth(getExpandFrom());
			header.setHeight("100%");
			header.toolbarPanel.updateUndoRedoPosition();
		} else {
			header.expandWidth(getExpandTo());
			header.toolbarPanel.onOpen();
		}
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			public void execute() {
				header.updateCenterSize();
				header.showUndoRedoPanel();
				header.updateUndoRedoPosition();
			}
		});

	}
}
