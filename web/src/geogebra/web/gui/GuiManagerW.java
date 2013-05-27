package geogebra.web.gui;

import geogebra.common.GeoGebraConstants;
import geogebra.common.awt.GPoint;
import geogebra.common.cas.view.CASView;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.gui.GuiManager;
import geogebra.common.gui.Layout;
import geogebra.common.gui.layout.DockPanel;
import geogebra.common.gui.view.algebra.AlgebraView;
import geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
import geogebra.common.gui.view.properties.PropertiesView;
import geogebra.common.javax.swing.GTextComponent;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.ModeSetter;
import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.App;
import geogebra.common.main.DialogManager;
import geogebra.common.main.MyError;
import geogebra.html5.main.ViewManager;
import geogebra.web.Web;
import geogebra.web.Web.GuiToLoad;
import geogebra.web.cas.view.CASViewW;
import geogebra.web.euclidian.EuclidianControllerW;
import geogebra.web.euclidian.EuclidianViewW;
import geogebra.web.gui.app.GGWMenuBar;
import geogebra.web.gui.app.GGWToolBar;
import geogebra.web.gui.dialog.DialogManagerW;
import geogebra.web.gui.dialog.ImageFileInputDialog;
import geogebra.web.gui.dialog.InputDialogOpenURL;
import geogebra.web.gui.inputbar.AlgebraInputW;
import geogebra.web.gui.inputbar.InputBarHelpPanelW;
import geogebra.web.gui.layout.LayoutW;
import geogebra.web.gui.layout.panels.AlgebraDockPanelW;
import geogebra.web.gui.layout.panels.CASDockPanelW;
import geogebra.web.gui.layout.panels.Euclidian2DockPanelW;
import geogebra.web.gui.layout.panels.EuclidianDockPanelWAbstract;
import geogebra.web.gui.layout.panels.SpreadsheetDockPanelW;
import geogebra.web.gui.menubar.GeoGebraMenubarW;
import geogebra.web.gui.properties.PropertiesViewW;
import geogebra.web.gui.toolbar.ToolBarW;
import geogebra.web.gui.util.GeoGebraFileChooser;
import geogebra.web.gui.util.GoogleDriveFileChooser;
import geogebra.web.gui.util.SignInDialog;
import geogebra.web.gui.util.SkyDriveFileChooser;
import geogebra.web.gui.view.algebra.AlgebraControllerW;
import geogebra.web.gui.view.algebra.AlgebraViewW;
import geogebra.web.gui.view.consprotocol.ConstructionProtocolViewW;
import geogebra.web.gui.view.spreadsheet.SpreadsheetViewW;
import geogebra.web.html5.AttachedToDOM;
import geogebra.web.main.AppW;

import java.util.ArrayList;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class GuiManagerW extends GuiManager implements ViewManager {

	/**
	 * container for the Popup that only one exist for a given type
	 */
	public AttachedToDOM currentPopup;

	private DialogManagerW dialogManager;

	protected Kernel kernel;

	private AlgebraControllerW algebraController;
	private AlgebraViewW algebraView;
	private SpreadsheetViewW spreadsheetView;
	private EuclidianViewW euclidianView2;

	private final AppW app;
	private AbsolutePanel main;

	private LayoutW layout;

	private CASViewW casView;

	private Euclidian2DockPanelW euclidianView2DockPanel;

	
	private String strCustomToolbarDefinition;
	
	
	public GuiManagerW(AppW app) {
		this.app = app;
		this.kernel = app.getKernel();
		// AGdialogManagerFactory = new DialogManager.Factory();
	}

	@Override
	public void redo() {
		app.setWaitCursor();
		kernel.redo();
		updateActions();
		app.resetPen();
		app.setDefaultCursor();
	}

	@Override
	public void undo() {
		app.setWaitCursor();
		kernel.undo();
		updateActions();
		app.resetPen();
		app.setDefaultCursor();
	}

	@Override
	public void updateMenubarSelection() {
		if (app.getObjectPool().getGgwMenubar() != null) {
			app.getObjectPool().getGgwMenubar().getMenubar().updateSelection();
		}

	}

	@Override
	public void updateMenubar() {
		GGWMenuBar ggwMenuBar = app.getObjectPool().getGgwMenubar();
		if (ggwMenuBar != null) {
			GeoGebraMenubarW menuBar = app.getObjectPool().getGgwMenubar().getMenubar();
			if (menuBar != null) {
				menuBar.updateMenubar();
			}
		}
		
	}

	@Override
	public void updateActions() {
		GGWMenuBar ggwMenuBar = app.getObjectPool().getGgwMenubar();
		if (ggwMenuBar != null) {
			ggwMenuBar.getMenubar().updateSelection();
		}
	}

	@Override
	public DialogManager getDialogManager() {
		if (dialogManager == null) {
			App.debug("unimplemented");
			// dialogManager = new DialogManagerWeb(app);
		}
		return dialogManager;
	}

	@Override
	public void showPopupMenu(ArrayList<GeoElement> selectedGeos,
	        EuclidianViewInterfaceCommon view, GPoint mouseLoc) {
		showPopupMenu(selectedGeos, ((EuclidianViewW) view).g2p.getCanvas(),
		        mouseLoc);

	}

	private void showPopupMenu(ArrayList<GeoElement> geos, Canvas invoker,
	        GPoint p) {
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
	
	public void showPopupMenu(ArrayList<GeoElement> geos, AlgebraView invoker,
	        GPoint p) {
		// clear highlighting and selections in views
		app.getActiveEuclidianView().resetMode();
		getPopupMenu(geos, p).show(p);
	}

	public ContextMenuGeoElementW getPopupMenu(ArrayList<GeoElement> geos,
	        GPoint location) {
		removePopup();
		currentPopup = new ContextMenuGeoElementW(app, geos, location);
		((ContextMenuGeoElementW)currentPopup).addOtherItems();
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
			GPoint screenPos = (invoker == null) ? new GPoint(0, 0)
			        : new GPoint(invoker.getAbsoluteLeft() + p.x,
			                invoker.getAbsoluteTop() + p.y);

			app.getActiveEuclidianView().resetMode();
			getPopupMenu(app, view, selectedGeos, geos, screenPos, p).show(
			        invoker, p.x, p.y);
		}

	}

	private ContextMenuGeoElementW getPopupMenu(App app, EuclidianView view,
	        ArrayList<GeoElement> selectedGeos, ArrayList<GeoElement> geos,
	        GPoint screenPos, GPoint p) {
		currentPopup = new ContextMenuChooseGeoW((AppW) app, view,
		        selectedGeos, geos, screenPos, p);
		return (ContextMenuGeoElementW) currentPopup;
	}

	@Override
	public void setFocusedPanel(AbstractEvent event,
	        boolean updatePropertiesView) {
		setFocusedPanel(geogebra.web.euclidian.event.MouseEventW.getEvent(event), updatePropertiesView);
	}

	public void setFocusedPanel(NativeEvent e, boolean updatePropertiesView) {
		// determine parent panel to change focus
		Element et = Element.as(e.getEventTarget());
		Element DOMancestor1 = app.getEuclidianViewpanel().getElement();
		Element DOMancestor2 = null;
		if (hasEuclidianView2())
			DOMancestor2 = getEuclidianView2DockPanel().getElement();

		while (et.hasParentElement() && et != DOMancestor1 && et != DOMancestor2)
			et = et.getParentElement();

		if (et == DOMancestor1)
			setFocusedPanel(app.getEuclidianViewpanel(), updatePropertiesView);
		else if (DOMancestor2 != null && et == DOMancestor2)
			setFocusedPanel(getEuclidianView2DockPanel(), updatePropertiesView);
	}

	public void setFocusedPanel(DockPanel panel, boolean updatePropertiesView) {
		if (panel != null) {
			getLayout().getDockManager()
					.setFocusedPanel(panel,updatePropertiesView);

			// notify the properties view
			if  (updatePropertiesView)
				updatePropertiesView();
		}
	}

	@Override
	public void loadImage(GeoPoint loc, Object object, boolean altDown) {
		// TODO Auto-generated method stub
		App.debug("unimplemented method");

		app.setWaitCursor();

		ImageFileInputDialog dialog = new ImageFileInputDialog(app, loc);
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
		showDrawingPadPopup(((EuclidianViewW) view).g2p.getCanvas(), mouseLoc);
	};

	private void showDrawingPadPopup(Canvas invoker, GPoint p) {
		// clear highlighting and selections in views
		app.getActiveEuclidianView().resetMode();
		getDrawingPadpopupMenu(p.x, p.y).show(invoker, p.x, p.y);
	}

	private ContextMenuGeoElementW getDrawingPadpopupMenu(int x, int y) {
		currentPopup = new ContextMenuGraphicsWindowW(app, x, y);
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
		setShowView(flag, viewId, true);
	}

	
	@Override
	public void setShowView(boolean flag, int viewId, boolean isPermanent) {
		if (flag) {
			if (!showView(viewId))
				layout.getDockManager().show(viewId);

			if (viewId == App.VIEW_SPREADSHEET) {
				getSpreadsheetView().requestFocus();
			}
		} else {
			if (showView(viewId))
				layout.getDockManager().hide(viewId, isPermanent);

			if (viewId == App.VIEW_SPREADSHEET) {
				(app).getActiveEuclidianView().requestFocus();
			}
		}
		
		//toolbarPanel.validate();
		//toolbarPanel.updateHelpText();
	}
	
	@Override
	public boolean showView(int viewId) {
		/*
		Element e = Document.get().getElementById("View_" + viewId);
		if (e != null) {
			return !(e.getStyle().getDisplay().equals("none") || e.getStyle()
			        .getVisibility().equals("hidden"));
		}
		return false;
		*/
		
		try {
			return layout.getDockManager().getPanel(viewId).isVisible();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}

	@Override
	public View getConstructionProtocolData() {
		App.debug("unimplemented method");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getCasView() {
		if (casView == null)
			casView = new CASViewW(app);
		return casView;
	}

	@Override
	public SpreadsheetViewW getSpreadsheetView() {
		// init spreadsheet view
		if (spreadsheetView == null) {
			spreadsheetView = new SpreadsheetViewW(app);
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
		App.debug("unimplemented");
		// if (spreadsheetView != null) {
		// spreadsheetView.updateColumnWidths();
		// }
	}

	public void resize(int width, int height) {
		//this.width = width;
		//this.height = height;

		// experimental resize of canvas
		// app.getEuclidianView1().setPreferredSize(width, height);
		App.debug("why not use Settings for that?");
	}

	public ToolBarW getGeneralToolbar() {
		return toolbarPanel.getToolBar();
	}

	public String getToolbarDefinition() {
		if (strCustomToolbarDefinition == null && getToolbarPanel() != null)
			return getGeneralToolbar().getDefaultToolbarString();
			//return geogebra.web.gui.toolbar.ToolBarW.getAllTools(app);
		return strCustomToolbarDefinition;
	}

	public String getToolbarDefinition(Integer viewId) {
		if (viewId == App.VIEW_CAS)
			return CASView.TOOLBAR_DEFINITION;
		return getToolbarDefinition();
	}

	public void removeFromToolbarDefinition(int mode) {
		if (strCustomToolbarDefinition != null) {
			// Application.debug("before: " + strCustomToolbarDefinition +
			// ",  delete " + mode);

			strCustomToolbarDefinition = strCustomToolbarDefinition.replaceAll(
					Integer.toString(mode), "");

			if (mode >= EuclidianConstants.MACRO_MODE_ID_OFFSET) {
				// if a macro mode is removed all higher macros get a new id
				// (i.e. id-1)
				int lastID = kernel.getMacroNumber()
						+ EuclidianConstants.MACRO_MODE_ID_OFFSET - 1;
				for (int id = mode + 1; id <= lastID; id++) {
					strCustomToolbarDefinition = strCustomToolbarDefinition
							.replaceAll(Integer.toString(id),
									Integer.toString(id - 1));
				}
			}

			// Application.debug("after: " + strCustomToolbarDefinition);
		}
	}
	
	public void addToToolbarDefinition(int mode) {
		if (strCustomToolbarDefinition != null) {
			strCustomToolbarDefinition = strCustomToolbarDefinition + " | "
					+ mode;
		}
	}
	
	public final String getCustomToolbarDefinition() {
		return strCustomToolbarDefinition;
	}

	

	/**
	 * Initializes GuiManager for web
	 */
	@Override
	public void initialize() {
		initAlgebraController(); // ? needed for keyboard input in EuclidianView
								 // in Desktop
		layout.initialize(app);
		initLayoutPanels();		
	}

	/**
	 * Register panels for the layout manager.
	 */
	protected void initLayoutPanels() {

		// register euclidian view
		// this is done earlier
		layout.registerPanel(app.getEuclidianViewpanel());

		// register spreadsheet view
		layout.registerPanel(new SpreadsheetDockPanelW(app));

		// register algebra view
		layout.registerPanel(new AlgebraDockPanelW());

		// register CAS view
		if (GeoGebraConstants.CAS_VIEW_ENABLED)
			layout.registerPanel(new CASDockPanelW(app));

		// register EuclidianView2
		layout.registerPanel(getEuclidianView2DockPanel());

		// register ConstructionProtocol view
	//	layout.registerPanel(new ConstructionProtocolDockPanel(app));

		// register ProbabilityCalculator view
	//	layout.registerPanel(new ProbabilityCalculatorDockPanel(app));

		// register Properties view
	//	propertiesDockPanel = new PropertiesDockPanel(app);
	//	layout.registerPanel(propertiesDockPanel);

		// register data analysis view
	//	layout.registerPanel(new DataAnalysisViewDockPanel(app));
		
		if (!app.isApplet()) {
			// register python view
		//	layout.registerPanel(new PythonDockPanel(app));
		}
		
		/*
		if (!app.isWebstart() || app.is3D()) {
			// register Assignment view
			layout.registerPanel(new AssignmentDockPanel(app));
		}*/

	}

	@Override
	public void setLayout(Layout layout) {
		this.layout = (LayoutW) layout;
	}

	@Override
	public LayoutW getLayout() {
		return layout;
	}

	private GGWToolBar toolbarPanel = null;

	private InputBarHelpPanelW inputHelpPanel;

	private AlgebraInputW algebraInput;

	public GGWToolBar getToolbarPanel() {
		if (toolbarPanel == null) {
			if (!Web.currentGUI.equals(GuiToLoad.APP)) {
				toolbarPanel = app.getAppletGGWToolbar();	// note: this may return null
			} else {
				toolbarPanel = app.getAppFrame().getGGWToolbar();
			}
			if (toolbarPanel != null && !toolbarPanel.isInited()) {
				toolbarPanel.init(app);
			}
		}

		return toolbarPanel;
	}

	@Override
	public void updateToolbar() {
		if (toolbarPanel != null) {
			toolbarPanel.buildGui();
		}

		if (layout != null) {
			// AG layout.getDockManager().updateToolbars();
			if (getToolbarPanel() != null) {
				getToolbarPanel().updateToolbarPanel();
			}
		}
	}

	@Override
	public void updateAlgebraInput() {
		App.debug("Implementation needed...");
	}

	@Override
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

	@Override
	public AlgebraViewW getAlgebraView() {
		if (algebraView == null) {
			initAlgebraController();
			algebraView = newAlgebraView(algebraController);
			// if (!app.isApplet()) {
			// allow drag & drop of files on algebraView
			// algebraView.setDropTarget(new DropTarget(algebraView,
			// new FileDropTargetListener(app)));
			// }
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
		// if (USE_COMPRESSED_VIEW) {
		// return new CompressedAlgebraView(algc, CV_UPDATES_PER_SECOND);
		// }
		return new AlgebraViewW(algc);
	}

	@Override
	public void attachAlgebraView() {
		getAlgebraView();
		algebraView.attachView();
	}

	@Override
	public void detachAlgebraView() {
		if (algebraView != null)
			algebraView.detachView();
	}

	@Override
	public void setMode(int mode, ModeSetter m) {

		// can't move this after otherwise Object Properties doesn't work
		kernel.notifyModeChanged(mode, m);

		// select toolbar button, returns *actual* mode selected
		// int newMode = setToolbarMode(mode);
		//
		// if (mode != EuclidianConstants.MODE_SELECTION_LISTENER && newMode !=
		// mode) {
		// mode = newMode;
		// kernel.notifyModeChanged(mode);
		// }
	}

	@Override
	public void applyAlgebraViewSettings() {
		if (algebraView != null)
			algebraView.applySettings();
	}

	private PropertiesView propertiesView;

	@Override
	public View getPropertiesView() {

		if (propertiesView == null) {
			// initPropertiesDialog();
			propertiesView = new PropertiesViewW(app);
		}

		return propertiesView;
	}

	@Override
	public void updatePropertiesView() {
		if (propertiesView != null) {
			propertiesView.updatePropertiesView();
		}
	}

	@Override
	public void mousePressedForPropertiesView() {
		if (propertiesView != null) {
			propertiesView.mousePressedForPropertiesView();
		} else {
			// TODO: @Gabor: do we want this? I don't think we want to
			// initialize the view unnecessarily.
			// ((PropertiesViewW)
			// getPropertiesView()).mousePressedForPropertiesView();
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
		// String fileName = Window.prompt("File name", "Bunny");
		// do saving here if getBase64 will be good
		GeoGebraFileChooser fileChooser = ((DialogManagerW) app
		        .getDialogManager()).getFileChooser();
		if (app.getFileName() != null) {
			fileChooser.setFileName(app.getFileName());
		} else {
			fileChooser.setFileName("");
		}

		if (app.getFileDescription() != null) {
			fileChooser.setDescription(app.getFileDescription());
		} else {
			fileChooser.setDescription("");
		}
		fileChooser.center();
		return true;
	}

	@Override
	public void showPropertiesViewSliderTab() {
		App.debug("unimplemented");
	}

	@Override
	public void openURL() {
		InputDialogOpenURL id = new InputDialogOpenURL(app);
		id.setVisible(true);
	}

	@Override
	protected boolean loadURL_GGB(String url) {
		app.getAppFrame().fileLoader.getView().processFileName(url);
		return true;
	}

	@Override
	protected boolean loadURL_base64(String url) {
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
		if (success && !isMacroFile
			&& !app.getSettings().getLayout().isIgnoringDocumentLayout()) {

			getLayout().setPerspectives(app.getTmpPerspectives());
			//SwingUtilities.updateComponentTreeUI(getLayout().getRootComponent());

			if (!app.isIniting()) {
				updateFrameSize(); // checks internally if frame is available
				if (app.needsSpreadsheetTableModel())
					(app).getSpreadsheetTableModel(); //ensure create one if not already done
			}
		} else if (isMacroFile && success) {
			//setToolBarDefinition(ToolbarW.getAllTools(app));
			//(app).updateToolBar();
			//(app).updateContentPane();
		}

		// force JavaScript ggbOnInit(); to be called
		if (!app.isApplet())
			app.getScriptManager().ggbOnInit();
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
		getCasView();
		casView.attachView();
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

		if (layout == null)
			return app.getEuclidianView1();

		EuclidianDockPanelWAbstract focusedEuclidianPanel = layout
				.getDockManager().getFocusedEuclidianPanel();

		if (focusedEuclidianPanel != null) {
			return focusedEuclidianPanel.getEuclidianView();
		}
		return (app).getEuclidianView1();
		//return app.getEuclidianView1();
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
			App.debug("openHelp error: " + e.toString() + " " + e.getMessage()
			        + " " + page + " " + type);
			app.showError(e.getMessage());
			e.printStackTrace();
		}
	}

	private String getHelpURL(Help type, String pageName) {
		// try to get help for given language
		// eg http://www.geogebra.org/help/en_GB/cmd/FitLogistic

		StringBuilder urlSB = new StringBuilder();
		// StringBuilder urlOffline = new StringBuilder();
		//
		// urlOffline.append(AppD.getCodeBaseFolder());
		// urlOffline.append("help/");
		// urlOffline.append(((AppD)app).getLocale().getLanguage()); // eg en
		// urlOffline.append('/');

		urlSB.append(GeoGebraConstants.GEOGEBRA_WEBSITE);
		urlSB.append("help/");
		urlSB.append(app.getLocalization().getLanguage()); // eg en_GB

		switch (type) {
		case COMMAND:
			pageName = app.getEnglishCommand(pageName);
			// String pageNameOffline = pageName.replace(":",
			// "%3A").replace(" ",
			// "_");
			urlSB.append("/cmd/");
			urlSB.append(pageName);

			// urlOffline.append(pageNameOffline);
			// urlOffline.append("_Command.html");
			break;
		case TOOL:
			// pageNameOffline = pageName.replace(":", "%3A").replace(" ", "_");
			urlSB.append("/tool/");
			urlSB.append(pageName);

			// urlOffline.append(pageNameOffline);
			// urlOffline.append("_Tool.html");
			break;
		case GENERIC:
			// pageNameOffline = pageName.replace(":", "%3A").replace(" ", "_");
			urlSB.append("/article/");
			urlSB.append(pageName);

			// urlOffline.append(pageNameOffline);
			// urlOffline.append(".html");
			break;
		default:
			App.printStacktrace("Bad getHelpURL call");
		}
		try {
			// Application.debug(urlOffline.toString());
			// Application.debug(urlSB.toString());

			// String offlineStr = urlOffline.toString();

			// File file = new File(AppD.WINDOWS ? offlineStr.replaceAll(
			// "[/\\\\]+", "\\" + "\\") : offlineStr); // replace slashes
			// // with
			// // backslashes
			//
			// if (file.exists())
			// return getEscapedUrl("file:///" + offlineStr);
			// else
			// return getEscapedUrl(urlSB.toString());

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
		if (euclidianView2 == null)
			return false;
		if (!euclidianView2.isShowing())
			return false;
		return true;
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

		if (app.getObjectPool().getGgwMenubar().getMenubar() != null) {
			app.getObjectPool().getGgwMenubar().removeMenus();
			app.getObjectPool().getGgwMenubar().init(app);
		}

		if (algebraView != null) {
			algebraView.setLabels();
		}

		if ((app.getEuclidianView1() != null)
		        && app.getEuclidianView1().hasStyleBar()) {
			app.getEuclidianView1().getStyleBar().setLabels();
		}

		// if (!Web.currentGUI.equals(GuiToLoad.VIEWER)) {
		// GeoGebraAppFrame.setCloseMessage(app);
		// }

	}

	@Override
	public void setShowToolBarHelp(boolean showToolBarHelp) {
		App.debug("unimplemented");
		// TODO Auto-generated method stub

	}

	@Override
	public View getEuclidianView2() {
		if (euclidianView2 == null) {
			boolean[] showAxis = { true, true };
			boolean showGrid = false;
			App.debug("Creating 2nd Euclidian View");
			euclidianView2 = newEuclidianView(showAxis, showGrid, 2);
			// euclidianView2.setEuclidianViewNo(2);
			euclidianView2.setAntialiasing(true);
			euclidianView2.updateFonts();
		}
		return euclidianView2;
	}

	public Euclidian2DockPanelW getEuclidianView2DockPanel() {
		if (euclidianView2DockPanel == null) {
			euclidianView2DockPanel = new Euclidian2DockPanelW(app.isFullAppGui());
		}
		return euclidianView2DockPanel;
	}

	protected EuclidianViewW newEuclidianView(boolean[] showAxis,
			boolean showGrid, int id) {
		if (id == 2) {
			return new EuclidianViewW(getEuclidianView2DockPanel(), new EuclidianControllerW(kernel), showAxis,
				showGrid, id, app.getSettings().getEuclidian(id));
		}
		return new EuclidianViewW(app.getEuclidianViewpanel(), new EuclidianControllerW(kernel), showAxis,
			showGrid, id, app.getSettings().getEuclidian(id));
	}

	@Override
	public boolean hasEuclidianView2EitherShowingOrNot() {
		if (euclidianView2 == null)
			return false;
		return true;
	}

	@Override
	public void updateFrameSize() {
		// TODO Auto-generated method stub
		App.debug("unimplemented");

	}

	@Override
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



	private int toolbarID = App.VIEW_EUCLIDIAN;

	private ConstructionProtocolViewW constructionProtocolView;

	@Override
	public int getActiveToolbarId() {
		return toolbarID;
	}

	public void setActiveToolbarId(int toolbarID) {

		// set the toolbar string directly from the panels
		// after closing some panels, this may need to be done
		// even if the following need not
		setToolBarDefinition(layout.getDockManager().getPanel(toolbarID).getToolbarString());


		if (this.toolbarID != toolbarID) {
			toolbarPanel.setActiveToolbar(new Integer(toolbarID));
			updateToolbar();			
		}
		this.toolbarID = toolbarID;
	}

	@Override
	protected AppW getApp() {
		return app;
	}

	public void openFromGoogleDrive() {
		app.setWaitCursor();
		// String fileName = Window.prompt("File name", "Bunny");
		// do saving here if getBase64 will be good
		GoogleDriveFileChooser fileChooser = ((DialogManagerW) app
		        .getDialogManager()).getGoogleDriveFileChooser();
		fileChooser.show();
    }

	public void openFromSkyDrive() {
		app.setWaitCursor();
		// String fileName = Window.prompt("File name", "Bunny");
		// do saving here if getBase64 will be good
		SkyDriveFileChooser fileChooser = ((DialogManagerW) app
		        .getDialogManager()).getSkyDriveFileChooser();
		fileChooser.show();
    }

	public void removePopup() {
		if (currentPopup != null) {
			currentPopup.removeFromDOM();
			currentPopup = null;
		}
	    
    }

	public void signIn() {
	    SignInDialog signIn = ((DialogManagerW) app
		        .getDialogManager()).getSignInDialog();
	    signIn.center();
    }

	public void setToolBarDefinition(String toolBarDefinition) {
		strCustomToolbarDefinition = toolBarDefinition;
	}

	@Override
    public ConstructionProtocolView getConstructionProtocolView() {
		if (constructionProtocolView == null) {
			constructionProtocolView = new ConstructionProtocolViewW(app);
		}

		return constructionProtocolView;
    }
}
