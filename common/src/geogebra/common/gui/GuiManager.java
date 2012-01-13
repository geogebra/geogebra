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

	public abstract void updateFonts();

	public boolean isUsingConstructionProtocol() {
		// TODO Auto-generated method stub
		return false;
	}

	public void getConsProtocolXML(StringBuilder sb) {
		// TODO Auto-generated method stub
		
	}

	public abstract boolean isInputFieldSelectionListener();

	public abstract void addSpreadsheetTrace(GeoElement tracegeo);

	public abstract boolean isPropertiesDialogSelectionListener();

	public abstract geogebra.common.javax.swing.JTextComponent getAlgebraInputTextField();

	public abstract void showDrawingPadPopup(EuclidianViewInterfaceCommon view,
			Point mouseLoc);

}
