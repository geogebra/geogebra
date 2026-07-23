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

package org.geogebra.web.full.gui.components.sideSheet;

import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.CSSEvents;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Widget;

public class ComponentSideSheet extends FlowPanel {

	private SideSheetPanel sideSheet;
	private final AppW app;

	/**
	 * @param app application
	 * @param data initial description
	 */
	public ComponentSideSheet(AppW app, SideSheetData data) {
		this.app = app;
		sideSheet = new SideSheetPanel(app, data, this::close);
		add(sideSheet);
	}

	/**
	 * Close and remove side sheet after closing animation done.
	 */
	public void close(Runnable callback) {
		removeStyleName("animateIn");
		addStyleName("animateOut");
		CSSEvents.runOnAnimation(() -> {
			this.removeFromParent();
			callback.run();
		},
		getElement(), "animateOut");
	}

	/**
	 * Close and remove side sheet after closing animation done.
	 */
	public void close() {
		close(() -> {});
	}

	/**
	 * Show the side sheet with animation.
	 */
	public void show() {
		GeoGebraFrameW frame = app.getAppletFrame();
		for (int i = 0; i < frame.getWidgetCount(); i++) {
			if (frame.getWidget(i) instanceof ComponentSideSheet) {
				frame.getWidget(i).removeFromParent();
				break; // assume there's at most one side sheet attached
			}
		}
		frame.add(this);
		addStyleName("floatingSettings");
		addStyleName("animateIn");
	}

	/**
	 * Replace content.
	 * @param data side sheet descriptor
	 */
	public void update(SideSheetData data) {
		sideSheet.update(data);
	}

	/**
	 * @param widget widget to add
	 */
	public void addToContent(Widget widget) {
		sideSheet.addToContent(widget);
	}

	/**
	 * Focus the first element (close button).
	 */
	public void focus() {
		sideSheet.focus();
	}

	/**
	 * Attach positive action handler.
	 * @param positiveHandler handler
	 */
	public void addPositiveButtonRunnable(Runnable positiveHandler) {
		sideSheet.addPositiveButtonRunnable(positiveHandler);
	}
}
