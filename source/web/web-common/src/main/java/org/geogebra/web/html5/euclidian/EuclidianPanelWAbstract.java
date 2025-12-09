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

package org.geogebra.web.html5.euclidian;

import org.geogebra.common.euclidian.EuclidianView;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.ui.AbsolutePanel;
import org.gwtproject.user.client.ui.Panel;

/**
 * Panel containing a graphics view.
 */
public interface EuclidianPanelWAbstract {

	/**
	 * @return panel that hosts euclidian view and floating widgets
	 */
	AbsolutePanel getAbsolutePanel();

	/**
	 * @return euclidian view panel
	 */
	Panel getEuclidianPanel();

	/**
	 * TODO view should create the canvas
	 * @return main canvas for the euclidian view
	 */
	Canvas getCanvas();

	/**
	 * @return the wrapped euclidian view
	 */
	EuclidianView getEuclidianView();

	/**
	 * Set size in pixels
	 * @param width width
	 * @param height height
	 */
	void setPixelSize(int width, int height);

	/**
	 * @return width in pixels
	 */
	int getOffsetWidth();

	/**
	 * @return height in pixels
	 */
	int getOffsetHeight();

	/**
	 * Resize the content.
	 */
	void onResize();

	/**
	 * Schedule resizing the content.
	 */
	void deferredOnResize();

	/**
	 * Update navigation bar.
	 */
	void updateNavigationBar();

	/**
	 * @return the DOM element
	 */
	Element getElement();

	/**
	 * Reset old size.
	 */
	void reset();

	/**
	 * @return whether this is attached to DOM
	 */
	boolean isAttached();

	/**
	 * Enable or disable pointer events in zoom panel.
	 * @param enable whether to enable
	 */
	void enableZoomPanelEvents(boolean enable);

}
