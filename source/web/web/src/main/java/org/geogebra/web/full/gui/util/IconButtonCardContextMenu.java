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

package org.geogebra.web.full.gui.util;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.gui.view.IconSpec;
import org.geogebra.web.html5.main.general.GeneralIcon;
import org.gwtproject.core.client.Scheduler.ScheduledCommand;
import org.gwtproject.event.logical.shared.CloseEvent;
import org.gwtproject.event.logical.shared.CloseHandler;

/**
 * Context menu that can be opened by an icon button.
 */
public class IconButtonCardContextMenu
		implements SetLabels, CloseHandler<GPopupPanel> {
	protected AppWFull appW;
	protected Localization loc;
	protected GPopupMenuW wrappedPopup;
	private final IconButton button;

	/**
	 * @param appW application
	 */
	public IconButtonCardContextMenu(AppWFull appW) {
		button = new IconButton(appW, null, appW.getGeneralIconResource()
				.getImageResource(GeneralIcon.MORE));
		this.appW = appW;
		loc = appW.getLocalization();
		button.addFastClickHandler((event) -> {
			button.setActive(!isShowing());
			if (isShowing()) {
				hide();
			} else {
				show();
			}
		});
		button.addStyleName("cardContextMenuIconButton");
	}

	/**
	 * adds a menu item
	 * @param img icon image
	 * @param text menu item text
	 * @param cmd command to execute
	 */
	protected AriaMenuItem addItem(IconSpec img, String text,
			ScheduledCommand cmd) {
		AriaMenuItem mi = MainMenu.getMenuBarItem(img, text, cmd);
		wrappedPopup.addItem(mi);
		return mi;
	}

	/**
	 * init the popup
	 */
	protected void initPopup() {
		wrappedPopup = new GPopupMenuW(appW);
		wrappedPopup.getPopupPanel().addCloseHandler(this);
		wrappedPopup.getPopupPanel().addAutoHidePartner(button.getElement());
	}

	@Override
	public void setLabels() {
		initPopup();
		button.setAltText(loc.getMenu("Options"));
	}

	/**
	 * @return true if context menu is showing
	 */
	protected boolean isShowing() {
		return wrappedPopup != null && wrappedPopup.isMenuShown();
	}

	/**
	 * show the context menu
	 */
	protected void show() {
		if (wrappedPopup == null) {
			initPopup();
		}
		wrappedPopup.getPopupMenu().focusDeferred();
		wrappedPopup.show(button, -122, 36);
	}

	/**
	 * hide the context menu
	 */
	public void hide() {
		wrappedPopup.hide();
	}

	@Override
	public void onClose(CloseEvent<GPopupPanel> event) {
		button.setActive(false);
	}

	public IconButton getTriggerButton() {
		return button;
	}
}