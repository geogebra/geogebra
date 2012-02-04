package geogebra.web.gui;

import geogebra.common.awt.Point;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.gui.dialog.DialogManager;
import geogebra.common.javax.swing.JTextComponent;
import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.main.AbstractApplication;
import geogebra.web.main.Application;

import java.util.ArrayList;

public class GuiManager extends geogebra.common.gui.GuiManager {

	private DialogManagerWeb dialogManager;

	@Override
	public void removeSpreadsheetTrace(GeoElement recordObject) {
		// TODO Auto-generated method stub
		AbstractApplication.debug("implemented method");

	}

	@Override
	public void updateMenubarSelection() {
		// TODO Auto-generated method stub
		AbstractApplication.debug("implemented method");

	}

	@Override
	public DialogManager getDialogManager() {
		if (dialogManager == null) {
			Application.debug("unimplemented");
			//dialogManager = new DialogManagerWeb(app);
		}
		return dialogManager;
	}

	@Override
	public void showPopupMenu(ArrayList<GeoElement> selectedGeos,
	        EuclidianViewInterfaceCommon euclidianViewInterfaceCommon,
	        Point mouseLoc) {
		// TODO Auto-generated method stub
		AbstractApplication.debug("implemented method");

	}

	@Override
	public void setFocusedPanel(AbstractEvent event) {
		// TODO Auto-generated method stub
		AbstractApplication.debug("implemented method");

	}

	@Override
	public void loadImage(GeoPoint2 loc, Object object, boolean altDown) {
		// TODO Auto-generated method stub
		AbstractApplication.debug("implemented method");

	}

	@Override
	public void updateFonts() {
		// TODO Auto-generated method stub
		AbstractApplication.debug("implemented method");

	}

	@Override
	public boolean isInputFieldSelectionListener() {
		// TODO Auto-generated method stub
		AbstractApplication.debug("implemented method");
		return false;
	}

	@Override
	public void addSpreadsheetTrace(GeoElement tracegeo) {
		// TODO Auto-generated method stub
		AbstractApplication.debug("implemented method");

	}

	@Override
	public boolean isPropertiesDialogSelectionListener() {
		AbstractApplication.debug("implemented method");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public JTextComponent getAlgebraInputTextField() {
		AbstractApplication.debug("implemented method");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void showDrawingPadPopup(EuclidianViewInterfaceCommon view,
	        Point mouseLoc) {
		// TODO Auto-generated method stub
		AbstractApplication.debug("implemented method");

	}

	@Override
	public boolean hasSpreadsheetView() {
		// TODO Auto-generated method stub
		AbstractApplication.debug("implemented method");
		return false;
	}

	@Override
	public void attachSpreadsheetView() {
		// TODO Auto-generated method stub
		AbstractApplication.debug("implemented method");

	}

	@Override
	public void setShowView(boolean b, int viewSpreadsheet) {
		// TODO Auto-generated method stub
		AbstractApplication.debug("implemented method");

	}

	@Override
	public boolean showView(int viewSpreadsheet) {
		AbstractApplication.debug("implemented method");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getConstructionProtocolData() {
		AbstractApplication.debug("implemented method");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getCasView() {
		AbstractApplication.debug("implemented method");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getSpreadsheetView() {
		AbstractApplication.debug("implemented method");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getProbabilityCalculator() {
		AbstractApplication.debug("implemented method");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getPlotPanelView(int id) {
		AbstractApplication.debug("implemented method");
		// TODO Auto-generated method stub
		return null;
	}

}
