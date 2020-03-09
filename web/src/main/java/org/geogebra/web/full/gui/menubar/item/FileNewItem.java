package org.geogebra.web.full.gui.menubar.item;

import org.geogebra.web.full.gui.menubar.action.ClearAllAction;
import org.geogebra.web.full.gui.view.algebra.MenuItem;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.main.AppW;

/**
 * Clears construction and initializes a new one
 */
public class FileNewItem extends MenuItem<Void> {

	/**
	 * @param app application
	 */
	public FileNewItem(AppW app) {
		super(app.getVendorSettings().getMenuLocalizationKey("New"),
				((AppWFull) app).getActivity().getResourceIconProvider().newFileMenu(),
				new ClearAllAction(true));
	}
}
