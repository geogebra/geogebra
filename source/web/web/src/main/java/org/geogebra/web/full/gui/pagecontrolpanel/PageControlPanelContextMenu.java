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

package org.geogebra.web.full.gui.pagecontrolpanel;

import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.event.logical.shared.CloseEvent;
import org.gwtproject.event.logical.shared.CloseHandler;

public class PageControlPanelContextMenu extends GPopupMenuW implements CloseHandler<GPopupPanel> {
	protected AppWFull appW;
	private final PageListController pageController;
	protected GeoGebraFrameFull frame;
	private AriaMenuItem paste;
	private final MenuItemController menuItemController;

	/**
	 * Context menu shown on right click on white area of page panel.
	 * @param appW application
	 * @param pageController {@link PageListController}
	 */
	public PageControlPanelContextMenu(AppWFull appW, PageListController pageController) {
		super(appW);
		this.appW = appW;
		this.pageController = pageController;
		this.menuItemController = new MenuItemController(appW, popupPanel, null);
		frame = appW.getAppletFrame();
		popupPanel.addCloseHandler(this);
		buildPopup();
	}

	private void buildPopup() {
		addPasteItem();
		addNewPage();
	}

	private void addPasteItem() {
		paste = addItem(MaterialDesignResources.INSTANCE.paste_black(),
				appW.getLocalization().getMenu("Paste"),
				menuItemController.onPaste(pageController.getLastCard(), paste));
	}

	private void addNewPage() {
		addItem(MaterialDesignResources.INSTANCE.add_black(),
				appW.getLocalization().getMenu("ContextMenu.NewPage"),
				menuItemController.addNewPage(pageController.getSlideCount()));
	}

	/**
	 * Add menu item with given image, text and command.
	 * @param img icon image
	 * @param text menu item text
	 * @param cmd command to execute
	 */
	protected AriaMenuItem addItem(SVGResource img, String text,
			Scheduler.ScheduledCommand cmd) {
		AriaMenuItem mi = MainMenu.getMenuBarItem(img, text, cmd);
		addItem(mi);
		return mi;
	}

	@Override
	public void hideMenu() {
		super.hideMenu();
		frame.getPageControlPanel().hideIndicator();
	}

	/**
	 * Show the context menu at given x,y position.
	 * @param x horizontal position
	 * @param y vertical position
	 */
	public void show(int x, int y) {
		showAtPoint(0, 0);
		int popupWidth = getPopupPanel().getOffsetWidth();
		int popupHeight = getPopupPanel().getOffsetHeight();

		int horPos = x - appW.getAppletFrame().getAbsoluteLeft();
		if (horPos + popupWidth > appW.getAppletWidth()) {
			horPos = appW.getAppletWidth() - popupWidth;
		}

		int vertPos = y - appW.getAppletFrame().getAbsoluteTop();
		if (vertPos + popupHeight > appW.getAppletHeight()) {
			vertPos = appW.getAppletHeight() - popupHeight;
		}

		menuItemController.updatePasteVisibility(paste);
		showAtPoint(horPos, vertPos);
	}

	@Override
	public void onClose(CloseEvent event) {
		hideMenu();
	}
}
