package org.geogebra.web.full.gui.menubar;

import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.menubar.action.ClearAllAction;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

/**
 * Simple main menu item to clear all
 */
public class ClearAllMenuitem extends Submenu {

	private ClearAllAction action;
	private AppWFull app;

	/**
	 * @param app
	 *            application
	 */
	public ClearAllMenuitem(AppW app) {
		super("clear", app);
		this.app = (AppWFull) app;
		action = new ClearAllAction(false);
	}

	@Override
	public SVGResource getImage() {
		return MaterialDesignResources.INSTANCE.clear();
	}

	@Override
	protected String getTitleTranslationKey() {
		return "Clear";
	}

	@Override
	public void handleHeaderClick() {
		// no "Do you want to save?" for now in scientific
		action.execute(null, app);
	}
}
