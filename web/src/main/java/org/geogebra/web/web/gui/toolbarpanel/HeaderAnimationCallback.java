package org.geogebra.web.web.gui.toolbarpanel;

import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.layout.client.Layout.Layer;

/**
 * Callback that prevents header to be resized during animation.
 * 
 * @author laszlo
 */
public abstract class HeaderAnimationCallback implements AnimationCallback {

	protected final Header header;
	private int expandFrom;
	private int expandTo;
	private Double diff;
	private boolean closing;
	
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
		closing = expandFrom > expandTo;
	}

	public void onLayout(Layer layer, double progress) {
		if (diff == null) {
			return;
		}
		if (progress == 0) {
			onStart();
		}

		tick(progress);
	}

	/**
	 * Called when animation starts.
	 */
	protected abstract void onStart();

	/**
	 * Called when animation ends.
	 */
	protected abstract void onEnd();

	/**
	 * Called during animation.
	 * 
	 * @param progress
	 *            the indicator in 0..1
	 */
	public abstract void tick(double progress);

	public void onAnimationComplete() {
		header.setAnimating(false);
		header.updateStyle();
		onEnd();
	}



	public int getExpandFrom() {
		return expandFrom;
	}

	public void setExpandFrom(int expandFrom) {
		this.expandFrom = expandFrom;
	}

	public int getExpandTo() {
		return expandTo;
	}

	public void setExpandTo(int expandTo) {
		this.expandTo = expandTo;
	}

	public Double getDiff() {
		return diff;
	}

	public void setDiff(Double diff) {
		this.diff = diff;
	}

	public boolean isClosing() {
		return closing;
	}

}