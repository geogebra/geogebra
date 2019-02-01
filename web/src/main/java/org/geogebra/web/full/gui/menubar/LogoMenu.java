package org.geogebra.web.full.gui.menubar;

import org.geogebra.common.gui.toolcategorization.ToolCategorization.AppType;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

/**
 * Top of main menu with a logo; technically not a submenu
 *
 */
public class LogoMenu extends Submenu {

	/**
	 * @param app
	 *            application
	 */
	public LogoMenu(AppW app) {
		super("", app);
		setStyleName("logoMenu");
	}

	@Override
	public SVGResource getImage() {
		AppType appType = getApp().getSettings().getToolbarSettings().getType();
		return appType.equals(AppType.GRAPHING_CALCULATOR)
				? MaterialDesignResources.INSTANCE.graphing()
				: (appType.equals(AppType.GRAPHER_3D)
						? MaterialDesignResources.INSTANCE.graphing3D()
						: MaterialDesignResources.INSTANCE.geometry());
	}

	@Override
	protected String getTitleTranslationKey() {
		return getApp().getConfig().getAppName();
	}

}
