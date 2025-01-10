package org.geogebra.web.full.gui;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.html5.GMenuBarMock;
import org.geogebra.web.full.html5.MenuItemFactory;
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