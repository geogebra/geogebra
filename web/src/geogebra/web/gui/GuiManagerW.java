package geogebra.web.gui;

import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.gui.GuiManager;
import geogebra.common.gui.dialog.DialogManager;
import geogebra.common.gui.view.properties.PropertiesView;
import geogebra.common.javax.swing.GTextComponent;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.App;
import geogebra.web.euclidian.EuclidianViewW;
import geogebra.web.gui.app.GGWMenuBar;
import geogebra.web.gui.app.GGWToolBar;
import geogebra.web.gui.dialog.ImageFileInputDialog;
import geogebra.web.gui.dialog.InputDialogOpenURL;
import geogebra.web.gui.inputbar.AlgebraInputW;
import geogebra.web.gui.inputbar.InputBarHelpPanelW;
import geogebra.web.gui.layout.LayoutW;
import geogebra.web.gui.menubar.GeoGebraMenubarW;
import geogebra.web.gui.properties.PropertiesViewW;
import geogebra.web.gui.util.GeoGebraFileChooser;
import geogebra.web.gui.view.algebra.AlgebraControllerW;
import geogebra.web.gui.view.algebra.AlgebraViewW;
import geogebra.web.main.AppW;
import geogebra.web.openjdk.awt.geom.Point;

import java.util.ArrayList;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class GuiManagerW extends GuiManager {

	private DialogManagerW dialogManager;

	protected Kernel kernel;

	private AlgebraControllerW algebraController;
	private AlgebraViewW algebraView;

	private AbsolutePanel main;

	private int width;
	private int height;

	private LayoutW layout;

	public GuiManagerW(App app) {
		this.app = app;
		this.kernel = app.getKernel();
		
		//AGdialogManagerFactory = new DialogManager.Factory();
	}

	public void redo() {
		app.setWaitCursor();
		kernel.redo();
		updateActions();
		app.setDefaultCursor();
	}

	public void undo() {
		app.setWaitCursor();
		kernel.undo();
		updateActions();
		app.setDefaultCursor();
	}

	@Override
	public void updateMenubarSelection() {
		GGWMenuBar.getMenubar().updateSelection();

	}
	
	@Override
	public void updateMenubar() {
		GeoGebraMenubarW menuBar = GGWMenuBar.getMenubar();
		if (menuBar != null)
			menuBar.updateMenubar();
	}
	
	@Override
	public void updateActions() {
		GGWMenuBar.getMenubar().updateSelection();

	}

	@Override
	public DialogManager getDialogManager() {
		if (dialogManager == null) {
			AppW.debug("unimplemented");
			// dialogManager = new DialogManagerWeb(app);
		}
		return dialogManager;
	}

	@Override
	public void showPopupMenu(ArrayList<GeoElement> selectedGeos,
			EuclidianViewInterfaceCommon view, GPoint mouseLoc) {
		showPopupMenu(selectedGeos, ((EuclidianViewW) view).g2p.getCanvas(), mouseLoc);

	}
	

private void showPopupMenu(ArrayList<GeoElement> geos,
            Canvas invoker, GPoint p) {
		if (geos == null || !app.letShowPopupMenu())
			return;
		if (app.getKernel().isAxis(geos.get(0))) {
			showDrawingPadPopup(invoker, p);
		} else {
			// clear highlighting and selections in views
			app.getActiveEuclidianView().resetMode();
			getPopupMenu(geos, p).show(invoker, p.x, p.y);
		}
    }

	ContextMenuGeoElementW popupMenu;

	public ContextMenuGeoElementW getPopupMenu(ArrayList<GeoElement> geos, GPoint location) {
		if (popupMenu != null) {
			popupMenu.getWrappedPopup().removeFromParent();
		}
		popupMenu = new ContextMenuGeoElementW((AppW) app, geos, location);
		return popupMenu;
	}

	@Override
	public void showPopupChooseGeo(ArrayList<GeoElement> selectedGeos,
			ArrayList<GeoElement> geos, EuclidianViewInterfaceCommon view,
			geogebra.common.awt.GPoint p) {
		showPopupChooseGeo(selectedGeos, geos, (EuclidianViewW) view, p);
	}

	private void showPopupChooseGeo(ArrayList<GeoElement> selectedGeos,
            ArrayList<GeoElement> geos, EuclidianViewW view, GPoint p) {
		if (geos == null || !app.letShowPopupMenu())
			return;
		
		if (app.getKernel().isAxis(geos.get(0))) {
			showDrawingPadPopup(view, p);
		} else {
			
			Canvas invoker = view.g2p.getCanvas();
			// clear highlighting and selections in views
			GPoint screenPos = (invoker == null) ? new GPoint(0,0) : new GPoint(invoker.getAbsoluteLeft() + p.x, invoker.getAbsoluteTop() + p.y);
			
			
			app.getActiveEuclidianView().resetMode();
			getPopupMenu(app, view, selectedGeos, geos, screenPos, p).show(invoker,p.x,p.y);
		}
	    
    }

	private ContextMenuGeoElementW getPopupMenu(App app, EuclidianView view,
            ArrayList<GeoElement> selectedGeos, ArrayList<GeoElement> geos,
            GPoint screenPos, GPoint p) {
		if (popupMenu != null) {
			popupMenu.getWrappedPopup().removeFromParent();
		}
	    popupMenu = new ContextMenuChooseGeoW((AppW) app, view, selectedGeos, geos, screenPos, p);
	    return popupMenu;
    }

	@Override
	public void setFocusedPanel(AbstractEvent event, boolean updatePropertiesView) {
		// TODO Auto-generated method stub
		App.debug("unimplemented method");

	}

	@Override
	public void loadImage(GeoPoint loc, Object object, boolean altDown) {
		// TODO Auto-generated method stub
		App.debug("unimplemented method");

		app.setWaitCursor();

		ImageFileInputDialog dialog = new ImageFileInputDialog((AppW) app, loc);
		dialog.setVisible(true);
	}

	@Override
	public void updateFonts() {
		// TODO Auto-generated method stub
		App.debug("unimplemented method");

	}

	@Override
	public boolean isInputFieldSelectionListener() {
		// TODO Auto-generated method stub
		App.debug("unimplemented method");
		return false;
	}


	@Override
	public GTextComponent getAlgebraInputTextField() {
		App.debug("unimplemented method");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void showDrawingPadPopup(EuclidianViewInterfaceCommon view,
			GPoint mouseLoc) {
		showDrawingPadPopup(((EuclidianViewW)view).g2p.getCanvas(), mouseLoc);
	}

	ContextMenuGraphicsWindowW drawingPadpopupMenu;
	
	private void showDrawingPadPopup(Canvas invoker, GPoint p) {
		// clear highlighting and selections in views
		app.getActiveEuclidianView().resetMode();
		getDrawingPadpopupMenu(p.x,p.y).show(invoker, p.x, p.y);
    }

	private ContextMenuGeoElementW getDrawingPadpopupMenu(int x, int y) {
	    if (drawingPadpopupMenu != null) {
	    	drawingPadpopupMenu.wrappedPopup.removeFromParent();
	    }
	    drawingPadpopupMenu = new ContextMenuGraphicsWindowW((AppW)app, x, y);
		return drawingPadpopupMenu;
    }

	@Override
	public boolean hasSpreadsheetView() {
		// TODO Auto-generated method stub
		App.debug("unimplemented method");
		return false;
	}

	@Override
	public void attachSpreadsheetView() {
		// TODO Auto-generated method stub
		App.debug("unimplemented method");

	}

	@Override
	public void setShowView(boolean b, int viewSpreadsheet) {
		// TODO Auto-generated method stub
		App.debug("unimplemented method");

	}
	
	@Override
	public void setShowView(boolean b, int viewSpreadsheet, boolean isPermanent) {
		// TODO Auto-generated method stub
		App.debug("unimplemented method");

	}
	


	@Override
	public boolean showView(int viewId) {
		Element e = Document.get().getElementById("View_"+viewId);
		if (e != null) {
			return !(e.getStyle().getDisplay().equals("none") || e.getStyle().getVisibility().equals("hidden"));
		}
		return false;
	}

	@Override
	public View getConstructionProtocolData() {
		App.debug("unimplemented method");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getCasView() {
		App.debug("unimplemented method");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getSpreadsheetView() {
		App.debug("unimplemented method");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getProbabilityCalculator() {
		App.debug("unimplemented method");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getPlotPanelView(int id) {
		App.debug("unimplemented method");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateSpreadsheetColumnWidths() {
		// TODO Auto-generated method stub
		AppW.debug("unimplemented");
	}

	public void resize(int width, int height) {
		this.width = width;
		this.height = height;

		// experimental resize of canvas
		//app.getEuclidianView1().setPreferredSize(width, height);
		App.debug("why not use Settings for that?");
	}

	public void setToolBarDefinition(String toolBarDefinition) {
		strCustomToolbarDefinition = toolBarDefinition;
	}

	public String getToolbarDefinition() {
		if (strCustomToolbarDefinition == null) {
			return geogebra.web.gui.toolbar.ToolBar.getAllTools((AppW) app);
		}
		return strCustomToolbarDefinition;
	}

	/**
	 * Initializes GuiManager for web
	 */
	public void initialize() {
		initAlgebraController(); // ? needed for keyboard input in EuclidianView in Desktop
		
		layout.initialize(app);
		//do nothing yet
	    // TODO Auto-generated method stub
	    
    }
	
	public void setLayout(LayoutW layout) {
		this.layout = layout;
	}

	public LayoutW getLayout() {
		return layout;
	}
	
	private GGWToolBar toolbarPanel = null;

	private InputBarHelpPanelW inputHelpPanel;

	private AlgebraInputW algebraInput;
	
	public GGWToolBar getToolbarPanel() {
		if (toolbarPanel == null) {
			toolbarPanel = ((AppW)app).getAppFrame().getGGWToolbar();
			toolbarPanel.init(app);
		}

		return toolbarPanel;
	}
	
	public void updateToolbar() {
		if (toolbarPanel != null) {
			toolbarPanel.buildGui();
		}

		if (layout != null) {
			//AGlayout.getDockManager().updateToolbars();
			getToolbarPanel().updateToolbarPanel();
		}
	}

	@Override
    public void updateAlgebraInput() {
	   App.debug("Implementation needed...");
    }
	
	

	public InputBarHelpPanelW getInputHelpPanel() {
		if (inputHelpPanel == null)
			inputHelpPanel = new InputBarHelpPanelW(app);
		return inputHelpPanel;
    }

	public void openCommandHelp(String command) {
	   App.debug("Implementation needed...");
    }

	public void openHelp(String wikiManual) {
		App.debug("Implementation needed...");
    }

	public void setShowAuxiliaryObjects(boolean flag) {
		// TODO: auto-generated method stub
		if (!hasAlgebraView())
			return;
		//getAlgebraView();
		//algebraView.setShowAuxiliaryObjects(flag);
	}

	public AlgebraViewW getAlgebraView() {
		if (algebraView == null) {
			initAlgebraController();
			algebraView = newAlgebraView(algebraController);
			//if (!app.isApplet()) {
				// allow drag & drop of files on algebraView
			//	algebraView.setDropTarget(new DropTarget(algebraView,
			//			new FileDropTargetListener(app)));
			//}
		}

		return algebraView;
	}

	private void initAlgebraController() {
		if (algebraController == null) {
			algebraController = new AlgebraControllerW(app.getKernel());
		}
	}

	/**
	 * 
	 * @param algc
	 * @return new algebra view
	 */
	protected AlgebraViewW newAlgebraView(AlgebraControllerW algc) {
		//if (USE_COMPRESSED_VIEW) {
		//	return new CompressedAlgebraView(algc, CV_UPDATES_PER_SECOND);
		//}
		return new AlgebraViewW(algc);
	}

	public void attachAlgebraView() {
		getAlgebraView();
		algebraView.attachView();
	}

	public void detachAlgebraView() {
		if (algebraView != null)
			algebraView.detachView();
	}
	
	public void setMode(int mode) {

		// can't move this after otherwise Object Properties doesn't work
		kernel.notifyModeChanged(mode);

		// select toolbar button, returns *actual* mode selected
//		int newMode = setToolbarMode(mode);
//		
//		if (mode != EuclidianConstants.MODE_SELECTION_LISTENER && newMode != mode) {
//			mode = newMode;
//			kernel.notifyModeChanged(mode);
//		}
	}
	
	private PropertiesView propertiesView;

	@Override
	public View getPropertiesView() {

		if (propertiesView == null) {
			// initPropertiesDialog();
			propertiesView = new PropertiesViewW((AppW) app);
		}

		return propertiesView;
	}

	@Override
    public void updatePropertiesView() {
		if(propertiesView !=null){
			propertiesView.updatePropertiesView();
		}
    }
	

	@Override
	public void mousePressedForPropertiesView(){
		if(propertiesView !=null){
			propertiesView.mousePressedForPropertiesView();
		} else {
			((PropertiesViewW) getPropertiesView()).mousePressedForPropertiesView();
		}
	}

	@Override
    public void mouseReleasedForPropertiesView(boolean creatorMode) {
	    // TODO Auto-generated method stub
	    
    }

	public void addAlgebraInput(AlgebraInputW ai) {
	    this.algebraInput = ai;
    }

	public AlgebraInputW getAlgebraInput() {
	    return algebraInput;
    }

	@Override
    public boolean save() {
		app.setWaitCursor();
		//String fileName = Window.prompt("File name", "Bunny");
		//do saving here if getBase64 will be good
		GeoGebraFileChooser fileChooser = ((DialogManagerW)app.getDialogManager()).getFileChooser();
		if (((AppW) app).getFileName() != null) {
			fileChooser.setFileName(((AppW) app).getFileName());
		} else {
			fileChooser.setFileName("");
		}
		
		if (((AppW)app).getFileDescription() != null) {
			fileChooser.setDescription(((AppW)app).getFileDescription());
		} else {
			fileChooser.setDescription("");
		}
		fileChooser.show();
	    return true;
    }

	@Override
	public void showPropertiesViewSliderTab(){
		// TODO Auto-generated method stub
	}

	@Override
    public void openURL() {
		InputDialogOpenURL id = new InputDialogOpenURL((AppW)app);
		id.setVisible(true);	    
    }
	
	@Override
    protected boolean loadURL_GGB(String url){
		((AppW)app).getAppFrame().fileLoader.getView().processFileName(url);
		return true;
	}
	
	@Override
    protected boolean loadURL_base64(String url){
    	// TODO Auto-generated method stub
		App.debug("implementation needed");  
		return true;
	}

	@Override
    protected boolean loadFromApplet(String url) throws Exception {
	    // TODO Auto-generated method stub
		App.debug("implementation needed");
	    return false;
    }

	@Override
    public void updateGUIafterLoadFile(boolean success, boolean isMacroFile) {
	    App.debug("unimplemented");
	    
    }

	@Override
    public void startEditing(GeoElement geoElement) {
	    App.debug("unimplemented");
	    
    }

	@Override
    public boolean noMenusOpen() {
	    App.debug("unimplemented");
	    return true;
    }

	@Override
    public void openFile() {
	    App.debug("unimplemented");
    }

	@Override
    public void showGraphicExport() {
		App.debug("unimplemented");
	    
    }

	@Override
    public void showPSTricksExport() {
		App.debug("unimplemented");
	    
    }

	@Override
    public void showWebpageExport() {
		App.debug("unimplemented");
	    
    }

	@Override
    public void detachPropertiesView() {
		if (propertiesView != null)
			propertiesView.detachView();
    }

	@Override
    public boolean hasPropertiesView() {
	    // TODO Auto-generated method stub
	    return false;
    }

	@Override
    public void attachPropertiesView() {
		getPropertiesView();
		propertiesView.attachView();
    }

	@Override
    public void attachCasView() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void attachConstructionProtocolView() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void attachProbabilityCalculatorView() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void attachAssignmentView() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public EuclidianView getActiveEuclidianView() {
	    return app.getEuclidianView1();
    }

	public Command getShowAxesAction() {
	    return new Command() {
			
			public void execute() {
				showAxesCmd();
			}
		};
    }

	public Command getShowGridAction() {
	   return new Command() {
		
		public void execute() {
			showGridCmd();
		}
	};
    }

	@Override
    public View getDataAnalysisView() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public void attachDataAnalysisView() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void detachDataAnalysisView() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public boolean hasDataAnalysisView() {
	    // TODO Auto-generated method stub
	    return false;
    }
}
