package geogebra.web.gui;

import geogebra.common.GeoGebraConstants;
import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.gui.GuiManager;
import geogebra.common.gui.Layout;
import geogebra.common.main.DialogManager;
import geogebra.common.gui.view.properties.PropertiesView;
import geogebra.common.javax.swing.GTextComponent;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.ModeSetter;
import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.App;
import geogebra.common.main.MyError;
import geogebra.web.euclidian.EuclidianViewW;
import geogebra.web.gui.app.GGWMenuBar;
import geogebra.web.gui.app.GGWToolBar;
import geogebra.web.gui.dialog.DialogManagerW;
import geogebra.web.gui.dialog.ImageFileInputDialog;
import geogebra.web.gui.dialog.InputDialogOpenURL;
import geogebra.web.gui.dialog.InputDialogDownloadGGB;
import geogebra.web.gui.inputbar.AlgebraInputW;
import geogebra.web.gui.inputbar.InputBarHelpPanelW;
import geogebra.web.gui.layout.LayoutW;
import geogebra.web.gui.menubar.GeoGebraMenubarW;
import geogebra.web.gui.properties.PropertiesViewW;
import geogebra.web.gui.util.GeoGebraFileChooser;
import geogebra.web.gui.view.algebra.AlgebraControllerW;
import geogebra.web.gui.view.algebra.AlgebraViewW;
import geogebra.web.gui.view.spreadsheet.SpreadsheetViewW;
import geogebra.web.html5.AttachedToDOM;
import geogebra.web.main.AppW;
import geogebra.web.main.TimerSystemW;

import java.util.ArrayList;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class GuiManagerW extends GuiManager {
	
	/**
	 * container for the Popup that only one exist for a given type
	 */
	public static AttachedToDOM currentPopup;

	private DialogManagerW dialogManager;

	protected Kernel kernel;

	private AlgebraControllerW algebraController;
	private AlgebraViewW algebraView;
	private SpreadsheetViewW spreadsheetView;

	private TimerSystemW timers;

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
		if (GGWMenuBar.getMenubar() != null) {
			GGWMenuBar.getMenubar().updateSelection();
		}

	}
	
	@Override
	public void updateMenubar() {
		GeoGebraMenubarW menuBar = GGWMenuBar.getMenubar();
		if (menuBar != null)
			menuBar.updateMenubar();
	}
	
	@Override
	public void updateActions() {
		if (GGWMenuBar.getMenubar() != null) {
			GGWMenuBar.getMenubar().updateSelection();
		}
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

	public ContextMenuGeoElementW getPopupMenu(ArrayList<GeoElement> geos, GPoint location) {
		currentPopup = new ContextMenuGeoElementW((AppW) app, geos, location);
		return (ContextMenuGeoElementW) currentPopup;
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
	    currentPopup = new ContextMenuChooseGeoW((AppW) app, view, selectedGeos, geos, screenPos, p);
	    return (ContextMenuGeoElementW) currentPopup;
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
;
	
	private void showDrawingPadPopup(Canvas invoker, GPoint p) {
		// clear highlighting and selections in views
		app.getActiveEuclidianView().resetMode();
		getDrawingPadpopupMenu(p.x,p.y).show(invoker, p.x, p.y);
    }

	private ContextMenuGeoElementW getDrawingPadpopupMenu(int x, int y) {
	    currentPopup = new ContextMenuGraphicsWindowW((AppW)app, x, y);
		return (ContextMenuGeoElementW) currentPopup;
    }

	@Override
	public boolean hasSpreadsheetView() {
		if (spreadsheetView == null)
			return false;
		if (!spreadsheetView.isShowing())
			return false;
		return true;
	}

	@Override
	public void attachSpreadsheetView() {
		getSpreadsheetView();
		spreadsheetView.attachView();
	}

	@Override
	public void setShowView(boolean flag, int viewId) {
		setShowView( flag, viewId, true);
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
	public SpreadsheetViewW getSpreadsheetView() {
		// init spreadsheet view
		if (spreadsheetView == null) {
			spreadsheetView = new SpreadsheetViewW((AppW)app);
		}

		return spreadsheetView;
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
		//if (spreadsheetView != null) {
		//	spreadsheetView.updateColumnWidths();
		//}
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
	
	public void setLayout(Layout layout) {
		this.layout = (LayoutW) layout;
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

	@Override
	public void setShowAuxiliaryObjects(boolean flag) {
		if (!hasAlgebraViewShowing())
			return;
		getAlgebraView();
		algebraView.setShowAuxiliaryObjects(flag);
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
	
	public void setMode(int mode,ModeSetter m) {

		// can't move this after otherwise Object Properties doesn't work
		kernel.notifyModeChanged(mode,m);

		// select toolbar button, returns *actual* mode selected
//		int newMode = setToolbarMode(mode);
//		
//		if (mode != EuclidianConstants.MODE_SELECTION_LISTENER && newMode != mode) {
//			mode = newMode;
//			kernel.notifyModeChanged(mode);
//		}
	}


	@Override 
	public void applyAlgebraViewSettings(){ 
		if (algebraView!=null) 
			algebraView.applySettings(); 
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
			// TODO: @Gabor: do we want this? I don't think we want to initialize the view unnecessarily.
			//((PropertiesViewW) getPropertiesView()).mousePressedForPropertiesView();
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
		GeoGebraFileChooser fileChooser = ((DialogManagerW) app
		        .getDialogManager())
		        .getFileChooser(GeoGebraFileChooser.FILE_SAVE);
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

	/**
	 * Download the worksheet into a .ggb file
	 * @return 
	 * 
	 */
	public boolean download() {
		app.setWaitCursor();
		GeoGebraFileChooser fileChooser = ((DialogManagerW) app
		        .getDialogManager())
		        .getFileChooser(GeoGebraFileChooser.FILE_DOWNLOAD);
		fileChooser.setFileName("ggb");
		fileChooser.setDescription("desc");
		fileChooser.show();
		return true;
	}
	
	@Override
	public void showPropertiesViewSliderTab(){
		App.debug("unimplemented");
	}

	@Override
    public void openURL() {
		InputDialogOpenURL id = new InputDialogOpenURL((AppW)app);
		id.setVisible(true);	    
    }
	
    public void downloadGGB() {
		InputDialogDownloadGGB id = new InputDialogDownloadGGB((AppW)app);
		id.setVisible(true);	    
    }
	
	@Override
    protected boolean loadURL_GGB(String url){
		((AppW)app).getAppFrame().fileLoader.getView().processFileName(url);
		return true;
	}
	
	@Override
    protected boolean loadURL_base64(String url){
		App.debug("implementation needed");  
		return true;
	}

	@Override
    protected boolean loadFromApplet(String url) throws Exception {
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
		App.debug("unimplemented");
	    return false;
    }

	@Override
    public void attachPropertiesView() {
		getPropertiesView();
		propertiesView.attachView();
    }

	@Override
    public void attachCasView() {
		App.debug("unimplemented");
    }

	@Override
    public void attachConstructionProtocolView() {
		App.debug("unimplemented");
    }

	@Override
    public void attachProbabilityCalculatorView() {
		App.debug("unimplemented");
    }

	@Override
    public void attachAssignmentView() {
		App.debug("unimplemented");
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
		App.debug("unimplemented");
	    return null;
    }

	@Override
    public void attachDataAnalysisView() {
		App.debug("unimplemented");
    }

	@Override
    public void detachDataAnalysisView() {
		App.debug("unimplemented");
    }

	@Override
    public boolean hasDataAnalysisView() {
		App.debug("unimplemented");
	    return false;
    }

	@Override
    public void detachAssignmentView() {
		App.debug("unimplemented");
    }

	@Override
    public void detachProbabilityCalculatorView() {
		App.debug("unimplemented");
    }

	@Override
    public void detachCasView() {
		App.debug("unimplemented");
    }

	@Override
    public void detachConstructionProtocolView() {
		App.debug("unimplemented");
    }

	@Override
    public void detachSpreadsheetView() {
		if (spreadsheetView != null)
			spreadsheetView.detachView();
    }

	@Override
    protected void openHelp(String page, Help type) {
		try {
			String helpURL = getHelpURL(type, page);
			Window.open(helpURL, "", "");
		} catch (MyError e) {
			app.showError(e);
		} catch (Exception e) {
			App.debug("openHelp error: " + e.toString() + " "
					+ e.getMessage() + " " + page + " " + type);
			app.showError(e.getMessage());
			e.printStackTrace();
		}   
    }
	
	private String getHelpURL(Help type, String pageName) {
		// try to get help for given language
		// eg http://www.geogebra.org/help/en_GB/FitLogistic

		StringBuilder urlSB = new StringBuilder();
//		StringBuilder urlOffline = new StringBuilder();
//
//		urlOffline.append(AppD.getCodeBaseFolder());
//		urlOffline.append("help/");
//		urlOffline.append(((AppD)app).getLocale().getLanguage()); // eg en
//		urlOffline.append('/');

		urlSB.append(GeoGebraConstants.GEOGEBRA_WEBSITE);
		urlSB.append("help/");
		urlSB.append(((AppW)app).getLocaleStr()); // eg en_GB

		switch (type) {
		case COMMAND:
			pageName = ((AppW)app).getEnglishCommand(pageName);
//			String pageNameOffline = pageName.replace(":", "%3A").replace(" ",
//					"_");
			urlSB.append("/cmd/");
			urlSB.append(pageName);

//			urlOffline.append(pageNameOffline);
//			urlOffline.append("_Command.html");
			break;
		case TOOL:
//			pageNameOffline = pageName.replace(":", "%3A").replace(" ", "_");
			urlSB.append("/tool/");
			urlSB.append(pageName);

//			urlOffline.append(pageNameOffline);
//			urlOffline.append("_Tool.html");
			break;
		case GENERIC:
//			pageNameOffline = pageName.replace(":", "%3A").replace(" ", "_");
			urlSB.append("/article/");
			urlSB.append(pageName);

//			urlOffline.append(pageNameOffline);
//			urlOffline.append(".html");
			break;
		default:
			App.printStacktrace("Bad getHelpURL call");
		}
		try {
			// Application.debug(urlOffline.toString());
			// Application.debug(urlSB.toString());

//			String offlineStr = urlOffline.toString();

//			File file = new File(AppD.WINDOWS ? offlineStr.replaceAll(
//					"[/\\\\]+", "\\" + "\\") : offlineStr); // replace slashes
//															// with
//															// backslashes
//
//			if (file.exists())
//				return getEscapedUrl("file:///" + offlineStr);
//			else
//				return getEscapedUrl(urlSB.toString());
			
			return urlSB.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
    public void resetSpreadsheet() {
		if (spreadsheetView != null)
			spreadsheetView.restart();
    }

	@Override
    public void setScrollToShow(boolean b) {
		if (spreadsheetView != null)
			spreadsheetView.setScrollToShow(b);
    }

	@Override
    public void setShowConstructionProtocolNavigation(boolean show,
            boolean playButton, double playDelay, boolean showProtButton) {
		App.debug("unimplemented");
    }

	@Override
    public void showURLinBrowser(String strURL) {
		App.debug("unimplemented");
    }

	@Override
    public void updateMenuWindow() {
    }

	@Override
    public void updateMenuFile() {
    }

	@Override
    public void clearInputbar() {
		App.debug("unimplemented");
   }

	@Override
    public Object createFrame() {
	    return null;
    }

	@Override
    public int getInputHelpPanelMinimumWidth() {
		App.debug("unimplemented");
	    return 0;
    }

	@Override
    public void exitAll() {
		App.debug("unimplemented");
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public boolean saveCurrentFile() {
	    // TODO Auto-generated method stub
		App.debug("unimplemented");
	    return false;
    }

	@Override
    public boolean hasEuclidianView2() {
	    // TODO Auto-generated method stub
		App.debug("unimplemented");
	    return false;
    }

	@Override
    public void allowGUIToRefresh() {
		App.debug("unimplemented");
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void updateFrameTitle() {
	    // TODO Auto-generated method stub
		App.debug("unimplemented");
	    
    }

	@Override
    public void setLabels() {
		if (algebraInput != null)
			algebraInput.setLabels();
		
		if (GGWToolBar.getToolBar() != null) {
			GGWToolBar.getToolBar().buildGui();
		}
		
		if (GGWMenuBar.getMenubar() != null){
			GGWMenuBar.removeMenus();
			GGWMenuBar.init(app);
		}
		

    }

	@Override
    public void setShowToolBarHelp(boolean showToolBarHelp) {
		App.debug("unimplemented");
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setShowConstructionProtocolNavigation(boolean flag) {
		App.debug("unimplemented");
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public View getEuclidianView2() {
		App.debug("unimplemented");
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public boolean hasEuclidianView2EitherShowingOrNot() {
	    // TODO Auto-generated method stub
		App.debug("unimplemented");
	    return false;
    }

	@Override
    public void updateFrameSize() {
	    // TODO Auto-generated method stub
		App.debug("unimplemented");
	    
    }

	public void getSpreadsheetViewXML(StringBuilder sb, boolean asPreference) {
		if (spreadsheetView != null)
			spreadsheetView.getXML(sb, asPreference);
	}

	@Override
	public boolean hasAlgebraViewShowing() {
		if (algebraView == null)
			return false;
		if (!algebraView.isShowing())
			return false;
		return true;
	}

	@Override
	public boolean hasAlgebraView() {
		if (algebraView == null)
			return false;
		return true;
	}

	@Override
	public void getAlgebraViewXML(StringBuilder sb, boolean asPreference) {
		if (algebraView != null)
			algebraView.getXML(sb, asPreference);
	}

	public TimerSystemW getTimerSystem() {
		if (timers == null) {
			timers = new TimerSystemW((AppW)app);
		}
		return timers;
	}
}
