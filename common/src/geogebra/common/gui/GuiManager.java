package geogebra.common.gui;

import geogebra.common.gui.dialog.DialogManager;
import geogebra.common.kernel.geos.GeoElement;

public abstract class GuiManager {

	public abstract void removeSpreadsheetTrace(GeoElement recordObject);

	public abstract void updateMenubarSelection();

	public abstract DialogManager getDialogManager();

}
