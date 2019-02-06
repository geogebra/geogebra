package org.geogebra.web.full.gui.menubar;

import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

public class ClearAllMenuitem extends Submenu {

	private FileNewAction action;

	public ClearAllMenuitem(AppW app) {
		super("clear", app);
		action = new FileNewAction(app);
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
		// no "do you want to save" for now in scientific
		action.callback(true);
	}

}
