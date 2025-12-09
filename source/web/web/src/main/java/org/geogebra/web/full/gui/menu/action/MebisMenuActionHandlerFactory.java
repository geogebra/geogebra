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

import org.geogebra.common.gui.menu.Action;
import org.geogebra.web.full.gui.menubar.action.OpenFileActionMebis;
import org.geogebra.web.full.gui.menubar.action.OpenOfflineFileAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Builds MenuActionHandler instances for Mebis Board.
 */
public class MebisMenuActionHandlerFactory implements MenuActionHandlerFactory {

	private DefaultMenuActionHandlerFactory defaultMenuActionHandlerFactory;

	public MebisMenuActionHandlerFactory(AppWFull app) {
		defaultMenuActionHandlerFactory = new DefaultMenuActionHandlerFactory(app);
	}

	@Override
	public DefaultMenuActionHandler create() {
		DefaultMenuActionHandler actionHandler = defaultMenuActionHandlerFactory.create();
		actionHandler.setMenuAction(Action.SHOW_SEARCH_VIEW, new OpenFileActionMebis());
		actionHandler.setMenuAction(Action.OPEN_OFFLINE_FILE, new OpenOfflineFileAction());

		return actionHandler;
	}
}
