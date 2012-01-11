package geogebra.common.gui;

import java.util.ArrayList;

import geogebra.common.awt.Point;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.gui.dialog.DialogManager;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;

public abstract class GuiManager {

	public abstract void removeSpreadsheetTrace(GeoElement recordObject);

	public void updateMenubar() { } // temporarily nothing

	public abstract void updateMenubarSelection();

	public abstract DialogManager getDialogManager();

	public abstract void showPopupMenu(ArrayList<GeoElement> selectedGeos,
			EuclidianViewInterfaceCommon euclidianViewInterfaceCommon,
			Point mouseLoc);

	public void setMode(int mode) {
		// TODO Auto-generated method stub
		
	}

	public abstract void setFocusedPanel(AbstractEvent event);

	public abstract void loadImage(GeoPoint2 loc, Object object, boolean altDown);

	public boolean hasAlgebraView() {
		// TODO Auto-generated method stub
		return false;
	}

}
