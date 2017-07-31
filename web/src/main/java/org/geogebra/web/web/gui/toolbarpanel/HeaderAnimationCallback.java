package org.geogebra.web.web.gui.toolbarpanel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.layout.client.Layout.Layer;

/**
 * Callback that prevents header to be resized during animation.
 * 
 * @author laszlo
 */
public class HeaderAnimationCallback implements AnimationCallback {

	private final Header header;
	private int expandFrom;
	private int expandTo;
	private Double diff;
	private int diffY;
	private boolean forward;
	
	/**
	 * @param header
	 *            to set.
	 * @param width
	 */
	public HeaderAnimationCallback(Header header) {
		this.header = header;
		diff = null;
	}

	public HeaderAnimationCallback(Header header, int expandFrom,
			int expandTo) {
		this.header = header;
		this.expandFrom = expandFrom;
		this.expandTo = expandTo;
		diff = new Double(expandTo - expandFrom);
		forward = expandFrom < expandTo;
	}

	public void onLayout(Layer layer, double progress) {
		if (diff == null) {
			return;
		}
		double p = forward ? progress : 1 - progress;
		double w = diff * p;
		header.expandWidth(expandTo + Math.abs(w));
	}

	public void onAnimationComplete() {
		this.header.setAnimating(false);
		this.header.updateStyle();
		if (forward) {
			header.expandWidth(expandTo);
		} else {
			header.expandWidth(expandFrom);

		}
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			public void execute() {
				header.updateCenterSize();

			}
		});

		// header.getElement().getStyle().setWidth(expandTo, Unit.PX);
	}
}