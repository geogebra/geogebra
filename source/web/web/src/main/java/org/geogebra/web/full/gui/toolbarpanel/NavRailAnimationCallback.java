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

package org.geogebra.web.full.gui.toolbarpanel;

import org.gwtproject.layout.client.Layout.AnimationCallback;
import org.gwtproject.layout.client.Layout.Layer;

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