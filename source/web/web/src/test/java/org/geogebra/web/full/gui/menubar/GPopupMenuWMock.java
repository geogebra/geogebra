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

package org.geogebra.web.full.gui.menubar;

import org.geogebra.web.full.javax.swing.GCheckmarkMenuItem;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.core.client.Scheduler;

public class GPopupMenuWMock extends GPopupMenuW {
	private GMenuBarMock menuBarMock;

	/**
	 * @param app application
	 */
	public GPopupMenuWMock(AppW app) {
		super(app);
		menuBarMock = new GMenuBarMock("menuMock", app);
	}

	@Override
	public void addSeparator() {
		menuBarMock.addSeparator();
	}

	@Override
	public GMenuBar getPopupMenu() {
		return menuBarMock;
	}

	@Override
	public void addItem(AriaMenuItem item) {
		menuBarMock.add(item.getText());
	}

	@Override
	public void addItem(String s, Scheduler.ScheduledCommand c) {
		menuBarMock.add(s);
	}

	@Override
	public void addItem(GCheckmarkMenuItem item) {
		menuBarMock.addItem(item);
	}

	@Override
	public void addItem(AriaMenuItem item, boolean autoHide) {
		addItem(item);
	}

	@Override
	public void clearItems() {
		menuBarMock.clearItems();
	}

}
