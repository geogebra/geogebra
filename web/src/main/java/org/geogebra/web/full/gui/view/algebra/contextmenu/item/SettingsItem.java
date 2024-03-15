package org.geogebra.web.full.gui.view.algebra.contextmenu.item;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.DialogManager;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.view.algebra.MenuItem;
import org.geogebra.web.full.gui.view.algebra.contextmenu.action.SettingsAction;

/**
 * Opens the settings for a geo
 */
public class SettingsItem extends MenuItem<GeoElement> {

	/**
	 * New settings action
	 */
	public SettingsItem(DialogManager dialogManager) {
		super("Settings",
				MaterialDesignResources.INSTANCE.gear(),
				new SettingsAction(dialogManager));
	}
}
