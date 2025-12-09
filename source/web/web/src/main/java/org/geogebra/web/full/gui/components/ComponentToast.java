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

package org.geogebra.web.full.gui.components;

import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.Shades;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.user.client.ui.SimplePanel;

public class ComponentToast extends GPopupPanel {
	private SimplePanel content;
	public static final int TOAST_PADDING = 12;
	private static final int MIN_WIDTH = 300;

	/**
	 * constructor
	 * @param app - see {@link AppW}
	 * @param contentStr - content of the toast
	 */
	public ComponentToast(AppW app, String contentStr) {
		super(app.getAppletFrame(), app);
		addStyleName("toast");
		addStyleName(Shades.NEUTRAL_700.getName());
		buildGUI(contentStr);
		Dom.addEventListener(getElement(), "transitionend", evt -> {
			if (!getElement().hasClassName("fadeIn")) {
				removeFromParent();
			}
		});
	}

	private void buildGUI(String contentStr) {
		content = new SimplePanel();
		content.addStyleName("content");
		content.getElement().setInnerHTML(contentStr);
		add(content);
	}

	/**
	 * Update content.
	 * @param contentStr new content as HTML
	 */
	public void updateContent(String contentStr) {
		content.getElement().setInnerHTML(contentStr);
	}

	/**
	 * show toast animated and positioned
	 * @param left - left side of the editor
	 * @param top - top of the editor
	 * @param bottom - bottom of the editor
	 * @param width - distance from the left editor border to the right side of side panel
	 */
	public void show(int left, int top, int bottom, int width) {
		if (!isAttached()) {
			getRootPanel().add(this);
		}
		getElement().getStyle().clearWidth();
		int toastWidth = app.isPortrait() ? width - 16 : width;
		int distAVBottomKeyboardTop = (int) (app.getHeight() - bottom
				- ((AppW) app).getAppletFrame().getKeyboardHeight());
		int toastLeft = left;
		if (width < MIN_WIDTH && getOffsetWidth() > MIN_WIDTH) {
			toastWidth = Math.min(MIN_WIDTH, left + width);
			toastLeft = left + width - toastWidth;
		}
		getElement().getStyle().setWidth(toastWidth - 2 * TOAST_PADDING, Unit.PX);
		setPopupPosition(toastLeft, distAVBottomKeyboardTop >= getOffsetHeight()
				? bottom : top - getOffsetHeight());
		Scheduler.get().scheduleDeferred(() -> addStyleName("fadeIn"));
	}

	@Override
	public void hide() {
		removeStyleName("fadeIn");
	}
}
