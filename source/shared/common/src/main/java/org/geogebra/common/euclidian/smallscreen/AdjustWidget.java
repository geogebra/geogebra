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

package org.geogebra.common.euclidian.smallscreen;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.App;

/**
 * 
 * @author lac
 *
 */
public abstract class AdjustWidget {
	protected EuclidianView view;
	protected double ratioX;
	protected double ratioY;

	/**
	 * @param view
	 *            view
	 */
	public AdjustWidget(EuclidianView view) {
		this.view = view;
		App app = view.getApplication();
		int fileWidth = app.getSettings()
				.getEuclidian(view.getEuclidianViewNo()).getFileWidth();
		int fileHeight = app.getSettings()
				.getEuclidian(view.getEuclidianViewNo()).getFileHeight();

		ratioX = fileWidth == 0 ? 1 : (double) view.getViewWidth() / fileWidth;
		ratioY = fileHeight == 0 ? 1
				: (double) view.getViewHeight() / fileHeight;

		// Log.debug("[ADJUST] ratioX: " + ratioX + " ratioY: " + ratioY);

	}

	/**
	 * @return if the entire widget is visible on the screen.
	 */
	public abstract boolean isOnScreen();

	/**
	 * Adjust the widget: if it is offscreen, this method repositions it to be
	 * on screen entirely.
	 */
	public abstract void apply();

}