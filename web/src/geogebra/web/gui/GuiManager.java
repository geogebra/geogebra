package geogebra.web.gui;

import geogebra.common.awt.Point;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.gui.dialog.DialogManager;
import geogebra.common.javax.swing.JTextComponent;
import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;

import java.util.ArrayList;

public class GuiManager extends geogebra.common.gui.GuiManager {

	@Override
	public void removeSpreadsheetTrace(GeoElement recordObject) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateMenubarSelection() {
		// TODO Auto-generated method stub

	}

	@Override
	public DialogManager getDialogManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void showPopupMenu(ArrayList<GeoElement> selectedGeos,
	        EuclidianViewInterfaceCommon euclidianViewInterfaceCommon,
	        Point mouseLoc) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFocusedPanel(AbstractEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadImage(GeoPoint2 loc, Object object, boolean altDown) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateFonts() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isInputFieldSelectionListener() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addSpreadsheetTrace(GeoElement tracegeo) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isPropertiesDialogSelectionListener() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public JTextComponent getAlgebraInputTextField() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void showDrawingPadPopup(EuclidianViewInterfaceCommon view,
	        Point mouseLoc) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasSpreadsheetView() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void attachSpreadsheetView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setShowView(boolean b, int viewSpreadsheet) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean showView(int viewSpreadsheet) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getConstructionProtocolData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getCasView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getSpreadsheetView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getProbabilityCalculator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getPlotPanelView(int id) {
		// TODO Auto-generated method stub
		return null;
	}

}
