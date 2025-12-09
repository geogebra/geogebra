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

package org.geogebra.web.full.gui;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.HeaderPanel;

/**
 * Common code for whole screen GUIs such as material browser
 */
public abstract class MyHeaderPanel extends HeaderPanel implements SetLabels {

	private HeaderPanelDeck frame;

	/**
	 * @param frame
	 *            app frame
	 */
	public void setFrame(HeaderPanelDeck frame) {
		this.frame = frame;
	}

	/**
	 * Hide the panel and notify app frame
	 */
	public void close() {
		if (frame != null) {
			this.getApp().onBrowserClose();
			frame.hidePanel(this);
		}
	}

	/**
	 * @return application
	 */
	public abstract AppW getApp();

	/**
	 * @param width
	 *            new width (pixels)
	 * @param height
	 *            new height (pixels)
	 */
	public abstract void resizeTo(int width, int height);

}
