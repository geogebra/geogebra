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

package org.geogebra.web.html5.main;

import org.geogebra.gwtutil.JsConsumer;

/**
 * Interface for GeoGebra applet frame
 * @author gabor
 */
public interface HasAppletProperties {

	/**
	 * @param width
	 * 
	 *            sets the geogebra-web applet width
	 */
	void setWidth(int width);

	/**
	 * @param height
	 * 
	 *            sets the geogebra-web applet height
	 */
	void setHeight(int height);

	/**
	 * sets the geogebra-web applet size (width, height)
	 * 
	 * @param width
	 *            width in px
	 * @param height
	 *            height in px
	 */
	void setSize(int width, int height);

	/**
	 * After loading a new GGB file, the size should be set to "auto"
	 */
	void resetAutoSize();

	/**
	 * @param show
	 * 
	 *            whether to show the reseticon in geogebra-web applets or not
	 */
	void showResetIcon(boolean show);

	/**
	 * @return callback passed to renderArticleElementWithFrame
	 */
	JsConsumer<Object> getOnLoadCallback();

	/**
	 * @return whether keyboard is visible
	 */
	boolean isKeyboardShowing();

	/**
	 * Flag keyboard to be shown next time applet is focused
	 */
	void showKeyboardOnFocus();

	/**
	 * Update layout for keyboard height change
	 */
	void updateKeyboardHeight();

	/**
	 * @return keyboard height in pixels (0 if not showing)
	 */
	double getKeyboardHeight();

	/**
	 * Remove from DOM and prepare for garbage collection
	 */
	void remove();

	/**
	 * Update the CSS height of the article
	 */
	void updateArticleHeight();

	/**
	 * @param appW
	 *            app
	 */
	void initPageControlPanel(AppW appW);
}
