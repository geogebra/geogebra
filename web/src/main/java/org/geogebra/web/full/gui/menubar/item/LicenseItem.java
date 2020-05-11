package org.geogebra.web.full.gui.menubar.item;

import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.menubar.action.ShowLicenseAction;
import org.geogebra.web.full.gui.view.algebra.MenuItem;

/**
 * License menu item.
 */
public class LicenseItem extends MenuItem<Void> {

	/**
	 * Creates a new LicenseItem.
	 */
	public LicenseItem() {
		super("AboutLicense",
				MaterialDesignResources.INSTANCE.info_black(),
				new ShowLicenseAction());
	}
}
