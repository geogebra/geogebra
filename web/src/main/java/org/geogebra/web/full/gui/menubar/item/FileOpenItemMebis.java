package org.geogebra.web.full.gui.menubar.item;

import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.events.StayLoggedOutEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.web.full.gui.menubar.action.FileOpenActionMebis;
import org.geogebra.web.full.gui.view.algebra.MenuItem;
import org.geogebra.web.full.main.AppWFull;
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
				new FileOpenActionMebis());
	}

}
