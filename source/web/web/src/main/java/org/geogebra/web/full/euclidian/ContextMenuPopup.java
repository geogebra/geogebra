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

package org.geogebra.web.full.euclidian;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.event.logical.shared.CloseEvent;
import org.gwtproject.event.logical.shared.CloseHandler;
import org.gwtproject.user.client.ui.RequiresResize;

/**
 * context menu
 */
public class ContextMenuPopup extends StandardButton
		implements CloseHandler<GPopupPanel>, RequiresResize {

	private final AppW app;
	private GPopupMenuW popup;

	/**
	 * @param app - application
	 * @param popup - context menu popup
	 */
	public ContextMenuPopup(AppW app, GPopupMenuW popup) {
		super(MaterialDesignResources.INSTANCE.more_vert_black(), 24);
		this.app = app;
		this.popup = popup;
		initPopup();
		addStyleName("IconButton");
		addStyleName("IconButton-borderless");
		app.addWindowResizeListener(this);
	}

	@Override
	public void onResize() {
		if (!popup.isMenuShown()) {
			return;
		}
		popup.show(this, 0, getOffsetHeight());
	}

	private void initPopup() {
		popup.getPopupPanel().addAutoHidePartner(getElement());
		popup.getPopupPanel().addCloseHandler(this);

		ClickStartHandler.init(this, new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				if (isMenuShown()) {
					hideMenu();
				} else {
					showMenu();
				}
				app.hideKeyboard();
			}
		});
	}

	/**
	 * show the menu
	 */
	public void showMenu() {
		updatePopup();
		popup.show(this, 0, getOffsetHeight());
	}

	/**
	 * Update the popup.
	 */
	public void updatePopup() {
		// override if the menu needs updating when opened
	}

	/**
	 * hide the menu
	 */
	public void hideMenu() {
		popup.hide();
	}

	@Override
	public void onClose(CloseEvent<GPopupPanel> event) {
		hideMenu();
	}
	
	/**
	 * @return in the menu open
	 */
	public boolean isMenuShown() {
		return popup.isMenuShown();
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		hideMenu();
	}
}