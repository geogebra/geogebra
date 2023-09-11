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
