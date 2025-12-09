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

package org.geogebra.web.full.gui.menu.action;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.gui.menu.Action;
import org.geogebra.common.gui.view.algebra.contextmenu.MenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Implements handling of the menu actions.
 */
class DefaultMenuActionHandler implements MenuActionHandler {

	private AppWFull app;
	private Map<Action, MenuAction<AppWFull>> actionMap = new HashMap<>();

	/**
	 * Create a DefaultMenuActionHandler
	 * @param app app
	 */
	DefaultMenuActionHandler(AppWFull app) {
		this.app = app;
	}

	@Override
	public void executeMenuAction(Action action) {
		actionMap.get(action).execute(app);
	}

	void setMenuAction(Action action, MenuAction<AppWFull> menuAction) {
		actionMap.put(action, menuAction);
	}
}
