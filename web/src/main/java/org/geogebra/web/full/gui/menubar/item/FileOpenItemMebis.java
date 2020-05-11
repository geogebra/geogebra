package org.geogebra.web.full.gui.menubar.item;

import org.geogebra.web.full.gui.menubar.action.OpenFileActionMebis;
import org.geogebra.web.full.gui.view.algebra.MenuItem;
import org.geogebra.web.full.main.activity.GeoGebraActivity;
import org.geogebra.web.html5.main.AppW;

/**
 * Open local file if logged out or online file if logged in
 */
public class FileOpenItemMebis extends MenuItem<Void> {

	/**
	 * @param app
	 *            app
	 * @param activity
	 *            activity
	 */
	public FileOpenItemMebis(AppW app, GeoGebraActivity activity) {
		super(app.getVendorSettings().getMenuLocalizationKey("Open"),
				activity.getResourceIconProvider().openFileMenu(),
				new OpenFileActionMebis());
	}
}
