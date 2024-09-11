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
