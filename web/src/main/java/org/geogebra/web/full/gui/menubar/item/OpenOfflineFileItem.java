package org.geogebra.web.full.gui.menubar.item;

import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.menubar.action.OpenOfflineFileAction;
import org.geogebra.web.full.gui.view.algebra.MenuItem;
import org.geogebra.web.html5.main.AppW;

public class OpenOfflineFileItem extends MenuItem<Void> {

	/**
	 * @param app application
	 */
	public OpenOfflineFileItem(AppW app) {
		super(app.getLocalization().getMenu("mow.offlineMyFiles"),
				MaterialDesignResources.INSTANCE.mow_pdf_open_folder(),
				new OpenOfflineFileAction());
	}
}
