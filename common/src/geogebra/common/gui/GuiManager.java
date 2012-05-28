/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package geogebra.common.gui;

import geogebra.common.awt.Point;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.gui.dialog.DialogManager;
import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;

import java.util.ArrayList;

public abstract class GuiManager {

	public abstract void removeSpreadsheetTrace(GeoElement recordObject);

	public void updateMenubar() { } // temporarily nothing

	public abstract void updateMenubarSelection();

	public abstract DialogManager getDialogManager();

	public abstract void showPopupMenu(ArrayList<GeoElement> selectedGeos,
			EuclidianViewInterfaceCommon euclidianViewInterfaceCommon,
			Point mouseLoc);

	public abstract void setMode(int mode);

	public abstract void redo();
	public abstract void undo();

	public abstract void setFocusedPanel(AbstractEvent event, boolean updatePropertiesView);

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


	public abstract geogebra.common.javax.swing.JTextComponent getAlgebraInputTextField();

	public abstract void showDrawingPadPopup(EuclidianViewInterfaceCommon view,
			Point mouseLoc);

	public abstract boolean hasSpreadsheetView();

	public abstract void attachSpreadsheetView();

	public abstract void setShowView(boolean b, int viewSpreadsheet);

	public abstract boolean showView(int viewSpreadsheet);

	public abstract View getConstructionProtocolData();
	
	public abstract View getCasView();
	
	public abstract View getSpreadsheetView();
	
	public abstract View getProbabilityCalculator();
	
	public abstract View getPlotPanelView(int id);

	public boolean hasProbabilityCalculator() {
		// TODO Auto-generated method stub
		return false;
	}

	public void getProbabilityCalculatorXML(StringBuilder sb) {
		// TODO Auto-generated method stub
		
	}

	public void getSpreadsheetViewXML(StringBuilder sb, boolean asPreference) {
		// TODO Auto-generated method stub
		
	}

	public void updateActions() {
		// TODO Auto-generated method stub
		
	}

	abstract public void doAfterRedefine(GeoElement geo);

	public abstract void updateSpreadsheetColumnWidths();

	public void updateConstructionProtocol() {
		// TODO Auto-generated method stub
		
	}

	public abstract void updateAlgebraInput();

	public abstract void setShowAuxiliaryObjects(boolean flag);

	public abstract void updatePropertiesView();
	
	/**
	 * tells the properties view that mouse has been pressed
	 */
	public abstract void mousePressedForPropertiesView();	
	
	/**
	 * tells the properties view that mouse has been released
	 */
	public abstract void mouseReleasedForPropertiesView();
}
