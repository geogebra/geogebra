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

package org.geogebra.web.full.gui.layout.scientific;

import org.geogebra.web.html5.main.AppW;
import org.gwtproject.event.dom.client.MouseDownEvent;
import org.gwtproject.event.dom.client.MouseDownHandler;
import org.gwtproject.event.dom.client.TouchStartEvent;
import org.gwtproject.event.dom.client.TouchStartHandler;
import org.gwtproject.user.client.ui.Panel;

/**
 * Handles pointer events to make sure scrolling does not interfere with
 * keyboard.
 */
public class ScientificScrollHandler
		implements MouseDownHandler, TouchStartHandler {
	/**
	 * Estimated scrollbar width in desktop browsers; can overestimate a bit
	 */
	private static final int SCROLLBAR_WIDTH = 20;
	private final AppW app;
	private final Panel panel;

	/**
	 * @param app
	 *            application
	 * @param panel
	 *            scrolled panel
	 */
	public ScientificScrollHandler(AppW app, Panel panel) {
		this.app = app;
		this.panel = panel;
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		if (event.getClientX() > panel.getOffsetWidth() - SCROLLBAR_WIDTH) {
			event.stopPropagation();
		}
		app.closePopups();
	}

	@Override
	public void onTouchStart(TouchStartEvent event) {
		event.stopPropagation();
		app.closePopups();
	}
}
