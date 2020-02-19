package org.geogebra.web.full.gui.menubar.item;

import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.menubar.action.LicenseAction;
import org.geogebra.web.full.gui.view.algebra.MenuItem;
import org.geogebra.web.html5.main.AppW;

/**
 * Clears construction and initializes a new one
 *
 */
public class LicenseItem extends MenuItem<Void> {

	public LicenseItem() {
		super("AboutLicense",
				MaterialDesignResources.INSTANCE.info_black(),
				new LicenseAction());
	}

}
