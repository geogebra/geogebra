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

package org.geogebra.web.full.gui.properties;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.gwtproject.resources.client.ResourcePrototype;
import org.gwtproject.user.client.Command;

/**
 * Button for properties stylebar
 *
 */
public class PropertiesButton extends AriaMenuItem {

	private App app;

	/**
	 * @param app
	 *            application
	 * @param icon
	 *            content
	 * @param cmd
	 *            action
	 */
	public PropertiesButton(App app, ResourcePrototype icon, Command cmd) {
		super("", icon, cmd);
		setApp(app);
	}

	/**
	 * @return application
	 */
	public App getApp() {
		return app;
	}

	/**
	 * @param app
	 *            application
	 */
	public void setApp(App app) {
		this.app = app;
	}

	@Override
	public void setTitle(String title) {
		AriaHelper.setTitle(this, title);
	}
}
