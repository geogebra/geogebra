package org.geogebra.web.full.gui.toolbarpanel;

import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.layout.client.Layout.Layer;

/**
 * Callback that prevents header to be resized during animation.
 * 
 * @author laszlo
 */
public abstract class HeaderAnimationCallback implements AnimationCallback {
	/** header panel */
	protected final Header header;
	private int expandFrom;
	private int expandTo;
	private Double diff;
	
	/**
	 * @param header
	 *            to set.
	 */
	public HeaderAnimationCallback(Header header) {
		this.header = header;
		diff = null;
	}

	/**
	 * @param header
	 *            header
	 * @param expandFrom
	 *            original width
	 * @param expandTo
	 *            target width
	 */
	public HeaderAnimationCallback(Header header, int expandFrom,
			int expandTo) {
		this.header = header;
		this.expandFrom = expandFrom;
		this.expandTo = expandTo;
		diff = new Double(expandTo - expandFrom);
	}

	@Override
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

	@Override
	public void onAnimationComplete() {
		header.setAnimating(false);
		header.updateStyle();
		onEnd();
	}

	/**
	 * @return original width
	 */
	public int getExpandFrom() {
		return expandFrom;
	}

	/**
	 * @return target width
	 */
	public int getExpandTo() {
		return expandTo;
	}

	/**
	 * @return width difference
	 */
	public Double getDiff() {
		return diff;
	}

}