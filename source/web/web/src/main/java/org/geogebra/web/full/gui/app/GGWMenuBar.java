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

package org.geogebra.web.full.gui.app;

import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.main.AppWFull;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.user.client.ui.Composite;
import org.gwtproject.user.client.ui.FlowPanel;

/**
 * Wrap MainMenu in composite
 *
 */
public class GGWMenuBar extends Composite {

	private FlowPanel ggbmenubarwrapper;
	private MainMenu menubar;

	/**
	 * Create new menu wrapper
	 */
	public GGWMenuBar() {
		ggbmenubarwrapper = new FlowPanel();
		ggbmenubarwrapper.addStyleName("ggbmenubarwrapper");
		initWidget(ggbmenubarwrapper);

	}

	/**
	 * @param app
	 *            application to init menus
	 */
	public void init(AppWFull app) {
		menubar = new MainMenu(app);
		ggbmenubarwrapper.add(menubar);
	}

	/**
	 * @return wrapped menu
	 */
	public MainMenu getMenubar() {
		return menubar;
	}

	/**
	 * Clear the menu
	 */
	public void removeMenus() {
		ggbmenubarwrapper.clear();
	}

	@Override
	public void setPixelSize(int w, int h) {
		super.setPixelSize(w, h);
		if (menubar != null && menubar.getElement().getParentElement() != null) {
			menubar.getElement().getParentElement().getStyle()
				.setHeight(h, Unit.PX);
		}
	}

	/**
	 * Focus the menu
	 */
	public void focus() {
		menubar.focusFirst();
	}

	/**
	 * Focus menu in the deferred way.
	 */
	public void focusDeferred() {
		Scheduler.get().scheduleDeferred(this::focus);
	}

	@Override
	public void setVisible(boolean b) {
		menubar.setVisible(b);
	}

}
