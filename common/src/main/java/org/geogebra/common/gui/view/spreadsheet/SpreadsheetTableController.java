package org.geogebra.common.gui.view.spreadsheet;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.EventType;

public class SpreadsheetTableController {
	private final App app;

	public SpreadsheetTableController(App app) {
		this.app = app;
	}

	/**
	 * Show redefine dialog if inline editing is not possible
	 * 
	 * @param geo
	 *            construction element
	 * @return whether dialog is shown
	 */
	public boolean redefineIfNeeded(GeoElement geo) {
		if (!geo.isProtected(EventType.UPDATE)) {
			if (!geo.isGeoText() && !geo.isIndependent()
					&& getEditorInitString(geo).length() > 20) {
				app.getDialogManager().showRedefineDialog(geo, false);
				return true;
			}

			if (geo.isGeoText() && ((GeoText) geo).isLaTeX()) {
				app.getDialogManager().showRedefineDialog(geo, true);
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the definition of geo used to init the editor when editing is
	 * started.
	 * 
	 * @param geo
	 *            construction element
	 * @return editor string
	 */
	public String getEditorInitString(GeoElementND geo) {
		return geo.getRedefineString(true, false);
	}
}
