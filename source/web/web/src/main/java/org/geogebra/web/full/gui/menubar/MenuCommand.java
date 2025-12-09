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

import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.Command;

/**
 * Command that also closes the menu
 */
public class MenuCommand implements Command {
	/** application */
	protected AppW app;
	
	/**
	 * @param app
	 *            application
	 */
	public MenuCommand(AppW app) {
		this.app = app;
	}
 
	@Override
	public void execute() {
		app.hideMenu();
		this.doExecute();
	}

	/**
	 * code that is executed if the menuEntry was clicked
	 */
	protected void doExecute() {
		// this may be not needed if execute is overridden
	}

}
