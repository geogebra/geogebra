package geogebra.web.gui;

import geogebra.common.awt.Point;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.gui.dialog.DialogManager;
import geogebra.common.gui.toolbar.ToolBar;
import geogebra.common.javax.swing.JTextComponent;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.main.AbstractApplication;
import geogebra.web.euclidian.EuclidianView;
import geogebra.web.gui.layout.Layout;
import geogebra.web.main.Application;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class GuiManager extends geogebra.common.gui.GuiManager {

	private DialogManagerWeb dialogManager;

	public AbstractApplication app;
	protected Kernel kernel;

	private AbsolutePanel main;

	private int width;
	private int height;

	private String strCustomToolbarDefinition;

	private Layout layout;

	public GuiManager(AbstractApplication app) {
		this.app = app;
		this.kernel = app.getKernel();
		
		//AGdialogManagerFactory = new DialogManager.Factory();
	}

	public void redo() {
		app.setWaitCursor();
		kernel.redo();
		updateActions();
		app.setDefaultCursor();
		System.gc();
	}

	public void undo() {
		app.setWaitCursor();
		kernel.undo();
		updateActions();
		app.setDefaultCursor();
		System.gc();
	}

	@Override
	public void removeSpreadsheetTrace(GeoElement recordObject) {
		// TODO Auto-generated method stub
		AbstractApplication.debug("unimplemented method");

	}

	@Override
	public void updateMenubarSelection() {
		// TODO Auto-generated method stub
		AbstractApplication.debug("unimplemented method");

	}

	@Override
	public DialogManager getDialogManager() {
		if (dialogManager == null) {
			Application.debug("unimplemented");
			// dialogManager = new DialogManagerWeb(app);
		}
		return dialogManager;
	}

	@Override
	public void showPopupMenu(ArrayList<GeoElement> selectedGeos,
			EuclidianViewInterfaceCommon euclidianViewInterfaceCommon, Point mouseLoc) {
		// TODO Auto-generated method stub
		AbstractApplication.debug("unimplemented method");

	}

	@Override
	public void setFocusedPanel(AbstractEvent event) {
		// TODO Auto-generated method stub
		AbstractApplication.debug("unimplemented method");

	}

	@Override
	public void loadImage(GeoPoint2 loc, Object object, boolean altDown) {
		// TODO Auto-generated method stub
		AbstractApplication.debug("unimplemented method");

	}

	@Override
	public void updateFonts() {
		// TODO Auto-generated method stub
		AbstractApplication.debug("unimplemented method");

	}

	@Override
	public boolean isInputFieldSelectionListener() {
		// TODO Auto-generated method stub
		AbstractApplication.debug("unimplemented method");
		return false;
	}

	@Override
	public void addSpreadsheetTrace(GeoElement tracegeo) {
		// TODO Auto-generated method stub
		AbstractApplication.debug("unimplemented method");

	}

	@Override
	public boolean isPropertiesDialogSelectionListener() {
		AbstractApplication.debug("unimplemented method");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public JTextComponent getAlgebraInputTextField() {
		AbstractApplication.debug("unimplemented method");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void showDrawingPadPopup(EuclidianViewInterfaceCommon view,
			Point mouseLoc) {
		// TODO Auto-generated method stub
		AbstractApplication.debug("unimplemented method");

	}

	@Override
	public boolean hasSpreadsheetView() {
		// TODO Auto-generated method stub
		AbstractApplication.debug("unimplemented method");
		return false;
	}

	@Override
	public void attachSpreadsheetView() {
		// TODO Auto-generated method stub
		AbstractApplication.debug("unimplemented method");

	}

	@Override
	public void setShowView(boolean b, int viewSpreadsheet) {
		// TODO Auto-generated method stub
		AbstractApplication.debug("unimplemented method");

	}

	@Override
	public boolean showView(int viewSpreadsheet) {
		AbstractApplication.debug("unimplemented method");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getConstructionProtocolData() {
		AbstractApplication.debug("unimplemented method");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getCasView() {
		AbstractApplication.debug("unimplemented method");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getSpreadsheetView() {
		AbstractApplication.debug("unimplemented method");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getProbabilityCalculator() {
		AbstractApplication.debug("unimplemented method");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getPlotPanelView(int id) {
		AbstractApplication.debug("unimplemented method");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void doAfterRedefine(GeoElement geo) {
		// TODO Auto-generated method stub
		AbstractApplication.debug("unimplemented method");

	}

	@Override
	public void updateSpreadsheetColumnWidths() {
		// TODO Auto-generated method stub
		Application.debug("unimplemented");
	}

	public void resize(int width, int height) {
		this.width = width;
		this.height = height;

		// experimental resize of canvas
		//app.getEuclidianView1().setPreferredSize(width, height);
		GWT.log("why not use Settigns for that?");
	}

	public void setToolBarDefinition(String toolBarDefinition) {
		strCustomToolbarDefinition = toolBarDefinition;
	}

	public String getToolbarDefinition() {
		if (strCustomToolbarDefinition == null) {
			return geogebra.web.gui.toolbar.ToolBar.getAllTools((Application) app);
		}
		return strCustomToolbarDefinition;
	}

	/**
	 * Initializes GuiManager for web
	 */
	public void initialize() {
		//do nothing yet
	    // TODO Auto-generated method stub
	    
    }
	
	public void setLayout(Layout layout) {
		this.layout = layout;
	}

	public Layout getLayout() {
		return layout;
	}
	
	
}
