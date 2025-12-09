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

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.menubar.GMenuBarMock;
import org.geogebra.web.html5.main.AppW;

public class ContextMenuMock {
	private final AppW app;
	private final ContextMenuItemFactory factory;

	/**
	 * @param app application
	 */
	public ContextMenuMock(AppW app) {
		this.app = app;
		this.factory = new MenuItemFactory(app);
	}

	/**
	 * @param geos elements
	 * @return menu entries
	 */
	public List<String> getEntriesFor(ArrayList<GeoElement> geos) {
		GMenuBarMock menu = getMenu(geos);
		return menu.getTitles();
	}

	protected GMenuBarMock getMenu(ArrayList<GeoElement> geos) {
		ContextMenuGeoElementW contextMenu =
				new ContextMenuGeoElementW(app, geos, factory);
		contextMenu.addOtherItems();
		return (GMenuBarMock) contextMenu.getWrappedPopup().getPopupMenu();
	}

	/**
	 * @param geos selected geos
	 * @param title menu title
	 * @return whether menu item is checked for given geos
	 */
	public boolean isMenuChecked(ArrayList<GeoElement> geos, String title) {
		GMenuBarMock menu = getMenu(geos);
		return menu.isChecked(title);

	}
}