package org.geogebra.web.full.gui.toolbarpanel;

import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.layout.client.Layout.Layer;

/**
 * Callback that prevents header to be resized during animation.
 * 
 * @author laszlo
 */
public abstract class NavRailAnimationCallback implements AnimationCallback {
	/** header panel */
	protected final NavigationRail navRail;

	/**
	 * @param navRail
	 *            header
	 */
	public NavRailAnimationCallback(NavigationRail navRail) {
		this.navRail = navRail;
	}

	@Override
	public void onLayout(Layer layer, double progress) {
		if (progress == 0) {
			onStart();
		}
	}

	/**
	 * Called when animation starts.
	 */
	protected abstract void onStart();

	/**
	 * Called when animation ends.
	 */
	protected abstract void onEnd();

	@Override
	public void onAnimationComplete() {
		navRail.setAnimating(false);
		navRail.updateStyle();
		onEnd();
	}

}