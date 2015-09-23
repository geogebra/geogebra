package org.geogebra.desktop.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.export.pstricks.GeoGebraToPstricks;
import org.geogebra.common.gui.GuiManager;
import org.geogebra.common.gui.Layout;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.VirtualKeyboardListener;
import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolNavigation;
import org.geogebra.common.gui.view.data.DataAnalysisModel;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.App;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.settings.KeyboardSettings;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.Base64;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.Unicode;
import org.geogebra.desktop.CommandLineArguments;
import org.geogebra.desktop.cas.view.CASViewD;
import org.geogebra.desktop.euclidian.EuclidianControllerD;
import org.geogebra.desktop.euclidian.EuclidianViewD;
import org.geogebra.desktop.euclidian.event.MouseEventND;
import org.geogebra.desktop.euclidianND.EuclidianViewInterfaceDesktop;
import org.geogebra.desktop.export.pstricks.GeoGebraToPstricksD;
import org.geogebra.desktop.export.pstricks.PstricksFrame;
import org.geogebra.desktop.gui.app.GeoGebraFrame;
import org.geogebra.desktop.gui.app.MyFileFilter;
import org.geogebra.desktop.gui.color.GeoGebraColorChooser;
import org.geogebra.desktop.gui.dialog.DialogManagerD;
import org.geogebra.desktop.gui.dialog.InputDialogD;
import org.geogebra.desktop.gui.dialog.InputDialogOpenURL;
import org.geogebra.desktop.gui.dialog.ToolCreationDialog;
import org.geogebra.desktop.gui.inputbar.AlgebraInput;
import org.geogebra.desktop.gui.inputbar.InputBarHelpPanel;
import org.geogebra.desktop.gui.layout.DockPanel;
import org.geogebra.desktop.gui.layout.LayoutD;
import org.geogebra.desktop.gui.layout.panels.AlgebraDockPanel;
import org.geogebra.desktop.gui.layout.panels.CasDockPanel;
import org.geogebra.desktop.gui.layout.panels.ConstructionProtocolDockPanel;
import org.geogebra.desktop.gui.layout.panels.DataAnalysisViewDockPanel;
import org.geogebra.desktop.gui.layout.panels.Euclidian2DockPanel;
import org.geogebra.desktop.gui.layout.panels.EuclidianDockPanel;
import org.geogebra.desktop.gui.layout.panels.EuclidianDockPanelAbstract;
import org.geogebra.desktop.gui.layout.panels.ProbabilityCalculatorDockPanel;
import org.geogebra.desktop.gui.layout.panels.PropertiesDockPanel;
import org.geogebra.desktop.gui.layout.panels.SpreadsheetDockPanel;
import org.geogebra.desktop.gui.menubar.GeoGebraMenuBar;
import org.geogebra.desktop.gui.nssavepanel.NSSavePanel;
import org.geogebra.desktop.gui.toolbar.ToolbarContainer;
import org.geogebra.desktop.gui.toolbar.ToolbarD;
import org.geogebra.desktop.gui.util.BrowserLauncher;
import org.geogebra.desktop.gui.util.GeoGebraFileChooser;
import org.geogebra.desktop.gui.view.CompressedAlgebraView;
import org.geogebra.desktop.gui.view.algebra.AlgebraControllerD;
import org.geogebra.desktop.gui.view.algebra.AlgebraViewD;
import org.geogebra.desktop.gui.view.assignment.AssignmentView;
import org.geogebra.desktop.gui.view.consprotocol.ConstructionProtocolNavigationD;
import org.geogebra.desktop.gui.view.consprotocol.ConstructionProtocolViewD;
import org.geogebra.desktop.gui.view.consprotocol.ConstructionProtocolViewD.ConstructionTableData;
import org.geogebra.desktop.gui.view.data.DataAnalysisViewD;
import org.geogebra.desktop.gui.view.probcalculator.ProbabilityCalculatorViewD;
import org.geogebra.desktop.gui.view.properties.PropertiesViewD;
import org.geogebra.desktop.gui.view.spreadsheet.SpreadsheetView;
import org.geogebra.desktop.gui.virtualkeyboard.VirtualKeyboard;
import org.geogebra.desktop.gui.virtualkeyboard.WindowsUnicodeKeyboard;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.GeoGebraPreferencesD;
import org.geogebra.desktop.main.GuiManagerInterfaceD;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.util.ImageManagerD;
import org.geogebra.desktop.util.Util;
/**
 * Handles all geogebra.gui package related objects and methods for Application.
 * This is done to be able to put class files of geogebra.gui.* packages into a
 * separate gui jar file.
 */
public class GuiManagerD extends GuiManager implements GuiManagerInterfaceD {

	private final static boolean USE_COMPRESSED_VIEW = true;
	private final static int CV_UPDATES_PER_SECOND = 3;

	protected DialogManagerD dialogManager;
	protected DialogManagerD.Factory dialogManagerFactory;

	private AlgebraInput algebraInput;
	private AlgebraControllerD algebraController;
	private AlgebraViewD algebraView;
	private CASViewD casView;
	private SpreadsheetView spreadsheetView;
	private ArrayList<EuclidianViewD> euclidianView2 = new ArrayList<EuclidianViewD>();
	private ConstructionProtocolViewD constructionProtocolView;
	private AssignmentView assignmentView;
	private GeoGebraMenuBar menuBar;
	private JMenuBar menuBar2;
	private String strCustomToolbarDefinition;

	private ToolbarContainer toolbarPanel;
	private boolean htmlLoaded;// see #126

	private LayoutD layout;

	private DataAnalysisViewD dataView;

	private String lastFilenameOfSaveDialog;

	/**
	 * Returns last filename that was used in save dialog (may be for .png,
	 * .ggb, ...) See #665
	 * 
	 * @return last filename including extension
	 */
	public String getLastFileNameOfSaveDialog() {
		return lastFilenameOfSaveDialog;
	}

	public static DataFlavor urlFlavor, uriListFlavor;
	static {
		try {
			urlFlavor = new DataFlavor(
					"application/x-java-url; class=java.net.URL");
			uriListFlavor = new DataFlavor(
					"text/uri-list; class=java.lang.String");
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
	}

	// Actions
	private AbstractAction showAxesAction, showGridAction, undoAction,
			redoAction;

	public GuiManagerD(AppD app) {
		this.app = app;
		this.kernel = app.getKernel();

		// this flag prevents closing opened webpage without save (see #126)
		htmlLoaded = false;
		lastFilenameOfSaveDialog = null;
		dialogManagerFactory = new DialogManagerD.Factory();
	}

	/**
	 * Initialize the GUI manager.
	 */
	public void initialize() {
		initAlgebraController(); // needed for keyboard input in EuclidianView

		// init layout related stuff
		layout.initialize((AppD) app);
		initLayoutPanels();

		// init dialog manager
		dialogManager = dialogManagerFactory.create((AppD) app);
	}

	/**
	 * Performs a couple of actions required if the user is switching between
	 * frame and applet: - Make the title bar visible if the user is using an
	 * applet. - Active the glass pane if the application is changing from
	 * applet to frame mode.
	 */
	public void updateLayout() {
		// update the glass pane (add it for frame, remove it for applet)
		layout.getDockManager().updateGlassPane();

		// we now need to make sure that the relative dimensions of views
		// are kept, therefore we update the dividers
		Dimension oldCenterSize = ((AppD) app).getCenterPanel().getSize();
		Dimension newCenterSize;

		// frame -> applet
		if (app.isApplet()) {
			newCenterSize = ((AppD) app).getApplet().getJApplet().getSize();
		}

		// applet -> frame
		else {
			// TODO redo this, guessing dimensions is bad
			if (((AppD) app).getFrame().getPreferredSize().width <= 0) {
				newCenterSize = new Dimension(700, 500);
			} else {
				newCenterSize = ((AppD) app).getFrame().getPreferredSize();
				newCenterSize.width -= 10;
				newCenterSize.height -= 100;
			}
		}

		layout.getDockManager().scale(
				newCenterSize.width / (float) oldCenterSize.width,
				newCenterSize.height / (float) oldCenterSize.height);
	}

	/**
	 * Register panels for the layout manager.
	 */
	protected void initLayoutPanels() {
		// register euclidian view
		layout.registerPanel(newEuclidianDockPanel());

		// register spreadsheet view
		layout.registerPanel(new SpreadsheetDockPanel((AppD) app));

		// register algebra view
		layout.registerPanel(new AlgebraDockPanel((AppD) app));

		// register CAS view
		if (GeoGebraConstants.CAS_VIEW_ENABLED
				&& app.supportsView(App.VIEW_CAS))
			layout.registerPanel(new CasDockPanel((AppD) app));

		// register EuclidianView2
		layout.registerPanel(newEuclidian2DockPanel(1));

		// register ConstructionProtocol view
		layout.registerPanel(new ConstructionProtocolDockPanel((AppD) app));

		// register ProbabilityCalculator view
		layout.registerPanel(new ProbabilityCalculatorDockPanel((AppD) app));

		// register Properties view
		propertiesDockPanel = new PropertiesDockPanel((AppD) app);
		layout.registerPanel(propertiesDockPanel);

		// register data analysis view
		layout.registerPanel(new DataAnalysisViewDockPanel((AppD) app));

		/*
		 * if (!app.isWebstart() || app.is3D()) { // register Assignment view
		 * layout.registerPanel(new AssignmentDockPanel(app)); }
		 */

	}

	private PropertiesDockPanel propertiesDockPanel = null;

	/**
	 * 
	 * @return the properties dock panel
	 */
	public PropertiesDockPanel getPropertiesDockPanel() {
		return propertiesDockPanel;
	}

	/**
	 * @return new euclidian view
	 */
	protected EuclidianDockPanel newEuclidianDockPanel() {
		return new EuclidianDockPanel((AppD) app, null);
	}

	protected Euclidian2DockPanel newEuclidian2DockPanel(int idx) {
		return new Euclidian2DockPanel((AppD) app, null, idx);
	}

	public boolean isInputFieldSelectionListener() {
		return app.getCurrentSelectionListener() == algebraInput.getTextField();
	}

	public void clearPreferences() {
		if ((app).isSaved() || ((AppD) app).saveCurrentFile()) {
			app.setWaitCursor();
			GeoGebraPreferencesD.getPref().clearPreferences();

			// clear custom toolbar definition
			strCustomToolbarDefinition = null;

			GeoGebraPreferencesD.getPref().loadXMLPreferences((AppD) app); // this
																			// will
			// load the
			// default
			// settings
			((AppD) app).setLanguage(((AppD) app).getMainComponent()
					.getLocale());
			((AppD) app).updateContentPaneAndSize();
			app.setDefaultCursor();
			app.setUndoActive(true);
		}
	}

	public synchronized CASViewD getCasView() {
		if (casView == null) {
			casView = new CASViewD((AppD) app);
		}

		return casView;
	}

	public boolean hasCasView() {
		return casView != null;
	}

	public AlgebraViewD getAlgebraView() {
		if (algebraView == null) {
			initAlgebraController();
			algebraView = newAlgebraView(algebraController);
			if (!app.isApplet()) {
				// allow drag & drop of files on algebraView
				algebraView.setDropTarget(new DropTarget(algebraView,
						new FileDropTargetListener((AppD) app)));
			}
		}

		return algebraView;
	}

	public void applyAlgebraViewSettings() {
		if (algebraView != null)
			algebraView.applySettings();
	}

	private PropertiesViewD propertiesView;

	public View getPropertiesView() {

		if (propertiesView == null) {
			// initPropertiesDialog();
			propertiesView = newPropertiesViewD((AppD) app);
		}

		return propertiesView;
	}

	/**
	 * @param appD
	 *            Application
	 * @return new PropertiesViewD
	 */
	protected PropertiesViewD newPropertiesViewD(AppD appD) {
		return new PropertiesViewD(appD);
	}

	public boolean hasPropertiesView() {
		return propertiesView != null;
	}

	/**
	 * 
	 * @param algc
	 * @return new algebra view
	 */
	protected AlgebraViewD newAlgebraView(AlgebraControllerD algc) {
		if (USE_COMPRESSED_VIEW) {
			return new CompressedAlgebraView(algc, CV_UPDATES_PER_SECOND);
		}
		return new AlgebraViewD(algc);
	}

	public org.geogebra.common.gui.view.consprotocol.ConstructionProtocolView getConstructionProtocolView() {
		if (constructionProtocolView == null) {
			constructionProtocolView = new ConstructionProtocolViewD((AppD) app);
		}

		return constructionProtocolView;
	}

	public View getConstructionProtocolData() {

		return ((ConstructionProtocolViewD) getConstructionProtocolView())
				.getData();
	}

	public AssignmentView getAssignmentView() {
		if (assignmentView == null) {
			assignmentView = new AssignmentView((AppD) app);
		}

		return assignmentView;
	}

	public void startEditing(GeoElement geo) {
		getAlgebraView().startEditing(geo);
	}

	public void setScrollToShow(boolean scrollToShow) {
		if (spreadsheetView != null)
			spreadsheetView.setScrollToShow(scrollToShow);
	}

	public void resetSpreadsheet() {
		if (spreadsheetView != null)
			spreadsheetView.restart();
	}

	public boolean hasSpreadsheetView() {
		if (spreadsheetView == null)
			return false;
		if (!spreadsheetView.isShowing())
			return false;
		return true;
	}

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
	public boolean hasProbabilityCalculator() {
		if (probCalculator == null)
			return false;
		if (!probCalculator.isShowing())
			return false;
		return true;
	}

	public ProbabilityCalculatorViewD getProbabilityCalculator() {

		if (probCalculator == null)
			probCalculator = new ProbabilityCalculatorViewD((AppD) app);
		return (ProbabilityCalculatorViewD) probCalculator;
	}

	public boolean hasDataAnalysisView() {
		if (dataView == null)
			return false;
		if (!dataView.isShowing())
			return false;
		return true;
	}

	public DataAnalysisViewD getDataAnalysisView() {
		if (dataView == null)
			dataView = new DataAnalysisViewD((AppD) app,
					DataAnalysisModel.MODE_ONEVAR);
		return dataView;
	}

	public SpreadsheetView getSpreadsheetView() {
		// init spreadsheet view
		if (spreadsheetView == null) {
			spreadsheetView = new SpreadsheetView((AppD) app);
		}

		return spreadsheetView;
	}

	public void updateSpreadsheetColumnWidths() {
		if (spreadsheetView != null) {
			spreadsheetView.updateColumnWidths();
		}
	}

	// XML
	// =====================================================

	@Override
	public void getSpreadsheetViewXML(StringBuilder sb, boolean asPreference) {
		app.getSettings().getSpreadsheet().getXML(sb, asPreference);
	}

	public void getAlgebraViewXML(StringBuilder sb, boolean asPreference) {
		if (algebraView != null)
			algebraView.getXML(sb, asPreference);
	}

	// public void getAlgebraViewXML(StringBuilder sb) {
	// if (algebraView != null)
	// algebraView.getXML(sb);
	// }

	// ==================================
	// End XML

	public EuclidianViewD getEuclidianView2(int idx) {
		for (int i = euclidianView2.size(); i <= idx; i++) {
			euclidianView2.add(null);
		}
		if (euclidianView2.get(idx) == null) {
			boolean[] showAxis = { true, true };
			boolean showGrid = false;
			App.debug("Creating 2nd Euclidian View");
			EuclidianViewD ev = newEuclidianView(showAxis, showGrid, 2);
			// euclidianView2.setEuclidianViewNo(2);
			ev.setAntialiasing(true);
			ev.updateFonts();
			euclidianView2.set(idx, ev);
		}
		return euclidianView2.get(idx);
	}

	protected EuclidianViewD newEuclidianView(boolean[] showAxis,
			boolean showGrid, int id) {
		return new EuclidianViewD(new EuclidianControllerD(kernel), showAxis,
				showGrid, id, app.getSettings().getEuclidian(id));
	}

	public boolean hasEuclidianView2(int idx) {
		if (euclidianView2.size() <= idx || euclidianView2.get(idx) == null)
			return false;
		if (!euclidianView2.get(idx).isShowing())
			return false;
		return true;
	}

	public boolean hasEuclidianView2EitherShowingOrNot(int idx) {
		if (euclidianView2.size() <= idx || euclidianView2.get(idx) == null)
			return false;
		return true;
	}

	/**
	 * @todo Do not just use the default euclidian view if no EV has focus, but
	 *       determine if maybe just one EV is visible etc.
	 * 
	 * @return The euclidian view to which new geo elements should be added by
	 *         default (if the user uses this mode). This is the focused
	 *         euclidian view or the first euclidian view at the moment.
	 */
	public EuclidianView getActiveEuclidianView() {

		EuclidianDockPanelAbstract focusedEuclidianPanel = layout
				.getDockManager().getFocusedEuclidianPanel();

		if (focusedEuclidianPanel != null) {
			return focusedEuclidianPanel.getEuclidianView();
		}
		return (app).getEuclidianView1();
	}

	public void attachSpreadsheetView() {
		getSpreadsheetView();
		spreadsheetView.attachView();
	}

	public void detachSpreadsheetView() {
		if (spreadsheetView != null)
			spreadsheetView.detachView();
	}

	public void attachAlgebraView() {
		getAlgebraView();
		algebraView.attachView();
	}

	public void detachAlgebraView() {
		if (algebraView != null)
			algebraView.detachView();
	}

	public void attachCasView() {
		getCasView();
		casView.attachView();
	}

	public void detachCasView() {
		if (casView != null)
			casView.detachView();
	}

	public void attachConstructionProtocolView() {
		getConstructionProtocolView();
		((ConstructionTableData) (constructionProtocolView.getData()))
				.attachView();
	}

	public void detachConstructionProtocolView() {
		if (constructionProtocolView != null)
			((ConstructionTableData) (constructionProtocolView.getData()))
					.detachView();
	}

	public void attachProbabilityCalculatorView() {
		getProbabilityCalculator();
		probCalculator.attachView();
	}

	public void detachProbabilityCalculatorView() {
		getProbabilityCalculator();
		probCalculator.detachView();
	}

	public void attachDataAnalysisView() {
		getDataAnalysisView().attachView();
	}

	public void detachDataAnalysisView() {
		getDataAnalysisView().detachView();
	}

	public void attachAssignmentView() {
		getAssignmentView();
		assignmentView.attachView();
	}

	public void detachAssignmentView() {
		if (assignmentView != null)
			assignmentView.detachView();
	}

	public void attachPropertiesView() {
		getPropertiesView();
		propertiesView.attachView();
	}

	public void detachPropertiesView() {
		if (propertiesView != null)
			propertiesView.detachView();
	}

	public void setShowAuxiliaryObjects(boolean flag) {
		if (!hasAlgebraViewShowing())
			return;
		getAlgebraView();
		algebraView.setShowAuxiliaryObjects(flag);
		app.getSettings().getAlgebra().setShowAuxiliaryObjects(flag);
	}

	private void initAlgebraController() {
		if (algebraController == null) {
			algebraController = new AlgebraControllerD(app.getKernel());
		}
	}

	public JComponent getAlgebraInput() {
		if (algebraInput == null)
			algebraInput = new AlgebraInput((AppD) app);

		return algebraInput;
	}

	public org.geogebra.common.javax.swing.GTextComponent getAlgebraInputTextField() {
		getAlgebraInput();
		return org.geogebra.desktop.javax.swing.GTextComponentD.wrap(algebraInput
				.getTextField());
	}

	/**
	 * use Application.getDialogManager() instead
	 */
	@Deprecated
	public DialogManager getDialogManager() {
		return dialogManager;
	}

	public void setLayout(Layout layout) {
		this.layout = (LayoutD) layout;
	}

	public LayoutD getLayout() {
		return layout;
	}

	public Container getToolbarPanelContainer() {

		return getToolbarPanel();
	}

	public ToolbarContainer getToolbarPanel() {
		if (toolbarPanel == null) {
			toolbarPanel = new ToolbarContainer((AppD) app, true);
		}

		return toolbarPanel;
	}

	public void updateToolbar() {
		if (toolbarPanel != null) {
			toolbarPanel.buildGui();
			// toolbarPanel.updateToolbarPanel();
			toolbarPanel.updateHelpText();
		}

		if (layout != null) {
			layout.getDockManager().updateToolbars();
		}
	}

	public void setShowView(boolean flag, int viewId) {
		setShowView(flag, viewId, true);
	}

	public void setShowView(boolean flag, int viewId, boolean isPermanent) {
		if (flag) {
			if (!showView(viewId)) {
				layout.getDockManager().show(viewId);
			}

			if (viewId == App.VIEW_SPREADSHEET) {
				getSpreadsheetView().requestFocus();
			}
		} else {
			if (showView(viewId)) {
				layout.getDockManager().hide(viewId, isPermanent);
			}

			if (viewId == App.VIEW_SPREADSHEET) {
				(app).getActiveEuclidianView().requestFocus();
			}
		}

		toolbarPanel.validate();
		toolbarPanel.updateHelpText();
	}

	public boolean showView(int viewId) {
		try {
			return layout.getDockManager().getPanel(viewId).isVisible();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void setShowToolBarHelp(boolean flag) {
		ToolbarContainer.setShowHelp(flag);
	}

	public boolean isConsProtNavigationPlayButtonVisible() {
		return getConstructionProtocolNavigation().isPlayButtonVisible();
	}

	public boolean isConsProtNavigationProtButtonVisible() {
		return getConstructionProtocolNavigation().isConsProtButtonVisible();
	}

	/**
	 * Displays the construction protocol dialog
	 */
	public void showConstructionProtocol() {
		app.getActiveEuclidianView().resetMode();
		getConstructionProtocolView();
		constructionProtocolView.setVisible(true);
	}

	/**
	 * Displays the construction protocol dialog
	 */
	/*
	 * public void hideConstructionProtocol() { if (constructionProtocolView ==
	 * null) return; app.getEuclidianView().resetMode();
	 * constructionProtocolView.setVisible(false); }
	 */

	/**
	 * returns whether the construction protocol is visible
	 */
	/*
	 * public boolean isConstructionProtocolVisible() { if
	 * (constructionProtocolView == null) return false; return
	 * constructionProtocolView.isVisible(); }
	 */
	/*
	 * public JPanel getConstructionProtocol() { if (constProtocol == null) {
	 * constProtocol = new ConstructionProtocolView(app); } return
	 * constProtocol; }
	 */
	public void setConstructionStep(int step) {
		if (constructionProtocolView != null)
			constructionProtocolView.setConstructionStep(step);
	}

	@Override
	public void updateConstructionProtocol() {
		if (constructionProtocolView != null)
			constructionProtocolView.update();
	}

	@Override
	public boolean isUsingConstructionProtocol() {
		return constructionProtocolView != null;
	}

	public int getToolBarHeight() {
		if ((app).showToolBar() && toolbarPanel != null) {
			return toolbarPanel.getHeight();
		}
		return 0;
	}

	public String getDefaultToolbarString() {
		if (toolbarPanel == null)
			return "";

		return getGeneralToolbar().getDefaultToolbarString();
	}

	public void updateFonts() {
		if (algebraView != null)
			algebraView.updateFonts();
		if (spreadsheetView != null)
			spreadsheetView.updateFonts();
		if (algebraInput != null)
			algebraInput.updateFonts();

		if (toolbarPanel != null) {
			toolbarPanel.buildGui();
		}

		if (menuBar != null) {
			menuBar.updateFonts();
		}

		if (constructionProtocolView != null)
			constructionProtocolView.initGUI();
		((org.geogebra.desktop.gui.view.consprotocol.ConstructionProtocolNavigationD) getConstructionProtocolNavigation())
				.initGUI();

		if (casView != null)
			casView.updateFonts();

		if (layout.getDockManager() != null)
			layout.getDockManager().updateFonts();

		if (probCalculator != null)
			((ProbabilityCalculatorViewD) probCalculator).updateFonts();

		if (dataView != null)
			dataView.updateFonts();

		if (propertiesView != null)
			propertiesView.updateFonts();

		dialogManager.updateFonts();

		SwingUtilities.updateComponentTreeUI(((AppD) app).getMainComponent());
	}

	public void setLabels() {
		// reinit actions to update labels
		showAxesAction = null;
		initActions();

		if ((app).showMenuBar()) {
			initMenubar();
			// updateMenubar();

			Component comp = ((AppD) app).getMainComponent();
			if (comp instanceof JApplet)
				((JApplet) comp).setJMenuBar(menuBar);
			else if (comp instanceof JFrame)
				((JFrame) comp).setJMenuBar(menuBar);
		}

		if (inputHelpPanel != null)
			inputHelpPanel.setLabels();
		// update views
		if (algebraView != null)
			algebraView.setLabels();
		if (algebraInput != null)
			algebraInput.setLabels();

		if (toolbarPanel != null) {
			toolbarPanel.buildGui();
			toolbarPanel.updateHelpText();
		}

		if (constructionProtocolView != null)
			constructionProtocolView.initGUI();

		getConstructionProtocolNavigation().setLabels();

		if (virtualKeyboard != null)
			virtualKeyboard.setLabels();

		layout.getDockManager().setLabels();

		dialogManager.setLabels();

		if (((AppD) app).getDockBar() != null)
			((AppD) app).getDockBar().setLabels();

	}

	public void initMenubar() {
		if (menuBar == null) {
			menuBar = new GeoGebraMenuBar((AppD) app, layout);

			menuBar2 = new JMenuBar();
			String country = ((AppD) app).getLocale().getCountry();
			if (country.equals("")) {
				// TODO: hack
				country = ((AppD) app).getLocale().getLanguage();
			}

			String flag = StringUtil.toLowerCase(country) + ".png";
			JMenuItem jj = new JMenuItem(((AppD) app).getFlagIcon(flag));
			jj.setAlignmentX(100);
			menuBar2.add(jj, ((AppD) app).getLocalization().borderEast());

		}
		// ((GeoGebraMenuBar) menuBar).setFont(app.getPlainFont());
		menuBar.initMenubar();
	}

	@Override
	public void updateMenubar() {
		if (menuBar != null)
			menuBar.updateMenubar();
	}

	public void updateMenubarSelection() {
		if (menuBar != null)
			menuBar.updateSelection();
	}

	public void updateMenuWindow() {
		if (menuBar != null)
			menuBar.updateMenuWindow();
	}

	public void updateMenuFile() {
		if (menuBar != null)
			menuBar.updateMenuFile();
	}

	public JMenuBar getMenuBar() {
		return menuBar;
	}

	public void setMenubar(JMenuBar newMenuBar) {
		menuBar = (GeoGebraMenuBar) newMenuBar;
	}

	public void updateMenuBarLayout() {
		if ((app).showMenuBar()) {
			Component comp = ((AppD) app).getMainComponent();
			if (comp instanceof JApplet)
				((JApplet) comp).setJMenuBar(menuBar);
			else if (comp instanceof JFrame) {
				((JFrame) comp).setJMenuBar(menuBar);
				((JFrame) comp).validate();
			}
		} else {
			Component comp = ((AppD) app).getMainComponent();
			if (comp instanceof JApplet)
				((JApplet) comp).setJMenuBar(null);
			else if (comp instanceof JFrame) {
				((JFrame) comp).setJMenuBar(null);
				((JFrame) comp).validate();
			}
		}
	}

	public void showAboutDialog() {
		GeoGebraMenuBar.showAboutDialog((AppD) app);
	}

	public void showPrintPreview() {
		GeoGebraMenuBar.showPrintPreview((AppD) app);
	}

	ContextMenuGraphicsWindowD drawingPadpopupMenu;

	/**
	 * Displays the Graphics View menu at the position p in the coordinate space
	 * of euclidianView
	 */
	public void showDrawingPadPopup(Component invoker,
			org.geogebra.common.awt.GPoint p) {
		// clear highlighting and selections in views
		app.getActiveEuclidianView().resetMode();

		// menu for drawing pane context menu
		drawingPadpopupMenu = new ContextMenuGraphicsWindowD((AppD) app, p.x,
				p.y);
		drawingPadpopupMenu.getWrappedPopup().show(invoker, p.x, p.y);
	}

	/**
	 * Toggles the Graphics View menu at the position p in the coordinate space
	 * of euclidianView
	 */
	public void toggleDrawingPadPopup(Component invoker, Point p) {
		org.geogebra.common.awt.GPoint loc = new org.geogebra.common.awt.GPoint(p.x,
				p.y);
		if (drawingPadpopupMenu == null
				|| !drawingPadpopupMenu.getWrappedPopup().isVisible()) {
			showDrawingPadPopup(invoker, loc);
			return;
		}

		drawingPadpopupMenu.getWrappedPopup().setVisible(false);
	}

	ContextMenuGeoElementD popupMenu;

	/**
	 * Displays the popup menu for geo at the position p in the coordinate space
	 * of the component invoker
	 */
	public void showPopupMenu(ArrayList<GeoElement> geos, Component invoker,
			org.geogebra.common.awt.GPoint p) {

		if (geos == null || geos.size() == 0 || !app.letShowPopupMenu())
			return;
		if (app.getKernel().isAxis(geos.get(0))) {
			showDrawingPadPopup(invoker, p);
		} else {
			// clear highlighting and selections in views
			app.getActiveEuclidianView().resetMode();

			Point screenPos = (invoker == null) ? new Point(0, 0) : invoker
					.getLocationOnScreen();
			screenPos.translate(p.x, p.y);

			popupMenu = new ContextMenuGeoElementD((AppD) app, geos, screenPos);
			popupMenu.getWrappedPopup().show(invoker, p.x, p.y);
		}

	}

	/**
	 * Displays the popup menu for geo at the position p in the coordinate space
	 * of the component invoker
	 */
	public void showPopupChooseGeo(ArrayList<GeoElement> selectedGeos,
			ArrayList<GeoElement> geos, EuclidianView view,
			org.geogebra.common.awt.GPoint p) {

		if (geos == null || !app.letShowPopupMenu())
			return;

		Component invoker = ((EuclidianViewInterfaceDesktop) view).getJPanel();

		if (!geos.isEmpty() && app.getKernel().isAxis(geos.get(0))) {
			showDrawingPadPopup(invoker, p);
		} else {
			// clear highlighting and selections in views
			app.getActiveEuclidianView().resetMode();

			Point screenPos = (invoker == null) ? new Point(0, 0) : invoker
					.getLocationOnScreen();
			screenPos.translate(p.x, p.y);

			popupMenu = new ContextMenuChooseGeoD((AppD) app, view,
					selectedGeos, geos, screenPos, p);
			// popupMenu = new ContextMenuGeoElement(app, geos, screenPos);
			popupMenu.getWrappedPopup().show(invoker, p.x, p.y);
		}

	}

	/**
	 * Toggles the popup menu for geo at the position p in the coordinate space
	 * of the component invoker
	 */
	public void togglePopupMenu(ArrayList<GeoElement> geos, Component invoker,
			Point p) {
		org.geogebra.common.awt.GPoint loc = new org.geogebra.common.awt.GPoint(p.x,
				p.y);
		if (popupMenu == null || !popupMenu.getWrappedPopup().isVisible()) {
			showPopupMenu(geos, invoker, loc);
			return;
		}

		popupMenu.getWrappedPopup().setVisible(false);

	}

	/**
	 * Creates a new GeoImage, using an image provided by either a Transferable
	 * object or the clipboard contents, then places it at the given location
	 * (real world coords). If the transfer content is a list of images, then
	 * multiple GeoImages will be created.
	 * 
	 * @return whether a new image was created or not
	 */
	public boolean loadImage(Transferable transfer, boolean fromClipboard) {
		app.setWaitCursor();

		String[] fileName = null;

		if (fromClipboard)
			fileName = getImageFromTransferable(null);
		else if (transfer != null) {
			fileName = getImageFromTransferable(transfer);
		} else {
			fileName = new String[1];
			fileName[0] = getImageFromFile(); // opens file chooser dialog
		}

		boolean ret;
		if (fileName.length == 0 || fileName[0] == null) {
			ret = false;
		} else {

			EuclidianView ev = ((AppD) app).getActiveEuclidianView();
			Construction cons = ev.getApplication().getKernel()
					.getConstruction();
			// Point mousePos = ((EuclidianViewInterfaceDesktop) ev)
			// .getMousePosition();

			// create GeoImage object(s) for this fileName
			GeoImage geoImage = null;

			GeoPoint loc = new GeoPoint(cons);
			GeoPoint loc2 = new GeoPoint(cons);

			for (int i = 0; i < fileName.length; i++) {
				// create corner points (bottom right/left)
				loc = new GeoPoint(cons);
				loc2 = new GeoPoint(cons);

				loc.setCoords(ev.getXmin() + (ev.getXmax() - ev.getXmin()) / 4,
						ev.getYmin() + (ev.getYmax() - ev.getYmin()) / 4, 1.0);
				loc.setLabel(null);
				loc.setLabelVisible(false);
				loc.update();

				loc2.setCoords(
						ev.getXmax() - (ev.getXmax() - ev.getXmin()) / 4,
						ev.getYmin() + (ev.getYmax() - ev.getYmin()) / 4, 1.0);
				loc2.setLabel(null);
				loc2.setLabelVisible(false);
				loc2.update();

				geoImage = new GeoImage(app.getKernel().getConstruction());
				geoImage.setImageFileName(fileName[i]);
				geoImage.setCorner(loc, 0);
				geoImage.setCorner(loc2, 1);
				geoImage.setLabel(null);

				GeoImage.updateInstances(app);
			}
			// make sure only the last image will be selected
			GeoElement[] geos = { geoImage, loc, loc2 };
			app.getActiveEuclidianView().getEuclidianController()
					.clearSelections();
			app.getActiveEuclidianView().getEuclidianController()
					.memorizeJustCreatedGeos(geos);
			ret = true;
		}

		app.setDefaultCursor();
		return ret;
	}

	public Color showColorChooser(org.geogebra.common.awt.GColor currentColor) {

		try {
			GeoGebraColorChooser chooser = new GeoGebraColorChooser((AppD) app);
			chooser.setColor(org.geogebra.desktop.awt.GColorD.getAwtColor(currentColor));
			JDialog dialog = JColorChooser.createDialog(
					((AppD) app).getMainComponent(),
					app.getPlain("ChooseColor"), true, chooser, null, null);
			dialog.setVisible(true);

			return chooser.getColor();

		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * gets String from clipboard Michael Borcherds 2008-04-09
	 */
	public String getStringFromClipboard() {
		String selection = null;

		Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable transfer = clip.getContents(null);

		try {

				StringBuilder sbuf = new StringBuilder();

				char readBuf[] = new char[1024 * 64];
				int numChars;
			DataFlavor[] df = transfer.getTransferDataFlavors();
			DataFlavor html = null;
			for (int i = 0; i < df.length; i++) {
				if (df[i].getMimeType().startsWith("text/html")) {
					html = df[i];
					break;
				}

			}
			InputStreamReader reader;
			Object data = transfer.getTransferData(html);
			reader = data instanceof InputStreamReader ? (InputStreamReader) data
					: new InputStreamReader((InputStream) data,
						"UNICODE");

				while (true) {
					numChars = reader.read(readBuf);
					if (numChars == -1)
						break;
					sbuf.append(readBuf, 0, numChars);
				}

				selection = new String(sbuf);
			reader.close();
		} catch (Exception e) {
			// e.printStackTrace();
		}

		return selection;
	}

	/**
	 * /** Tries to gets an image from a transferable object or the clipboard
	 * (if transfer is null). If an image is found, then it is loaded and stored
	 * in this application's imageManager.
	 * 
	 * @param transfer
	 * @return fileName of image stored in imageManager
	 */
	public String[] getImageFromTransferable(Transferable transfer) {

		BufferedImage img = null;
		String fileName = null;
		ArrayList<String> nameList = new ArrayList<String>();
		boolean imageFound = false;

		app.setWaitCursor();

		// if transfer is null then get it from the clipboard
		if (transfer == null) {
			try {
				Clipboard clip = Toolkit.getDefaultToolkit()
						.getSystemClipboard();
				transfer = clip.getContents(null);
				fileName = "clipboard.png"; // extension determines what format
				// it will be in ggb file

			} catch (Exception e) {
				app.setDefaultCursor();
				e.printStackTrace();
				app.showError("PasteImageFailed");
				return null;
			}
		}

		// load image from transfer
		try {

			DataFlavor[] df = transfer.getTransferDataFlavors();
			for (int i = 0; i < df.length; i++) {
				// System.out.println(df[i].getMimeType());
			}

			if (transfer.isDataFlavorSupported(DataFlavor.imageFlavor)) {
				img = (BufferedImage) transfer
						.getTransferData(DataFlavor.imageFlavor);
				if (img != null) {
					fileName = "transferImage.png";
					nameList.add(((AppD) app).createImage(new MyImageD(img),
							fileName));
					imageFound = true;
				}
				// System.out.println(nameList.toString());

			}

			if (!imageFound
					&& transfer
							.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				// java.util.List list = null;

				// list = (java.util.List)
				// transfer.getTransferData(DataFlavor.javaFileListFlavor);

				List<File> list = (List<File>) transfer
						.getTransferData(DataFlavor.javaFileListFlavor);
				ListIterator<File> it = list.listIterator();
				while (it.hasNext()) {
					File f = it.next();
					fileName = f.getName();
					img = ImageIO.read(f);
					if (img != null) {
						nameList.add(((AppD) app).createImage(
								new MyImageD(img), fileName));
						imageFound = true;
					}
				}
				// System.out.println(nameList.toString());

			}

			if (!imageFound && transfer.isDataFlavorSupported(uriListFlavor)) {

				String uris = (String) transfer.getTransferData(uriListFlavor);
				StringTokenizer st = new StringTokenizer(uris, "\r\n");
				while (st.hasMoreTokens()) {
					URI uri = new URI(st.nextToken());
					File f = new File(uri.toString());
					fileName = f.getName();
					img = ImageIO.read(uri.toURL());
					if (img != null) {
						nameList.add(((AppD) app).createImage(
								new MyImageD(img), fileName));
						imageFound = true;
					}
				}
				// System.out.println(nameList.toString());
			}

			if (!imageFound && transfer.isDataFlavorSupported(urlFlavor)) {

				URL url = (URL) transfer.getTransferData(urlFlavor);
				ImageIcon ic = new ImageIcon(url);
				if (ic.getIconHeight() > -1 && ic.getIconWidth() > -1) {
					File f = new File(url.toString());
					fileName = f.getName();
					img = (BufferedImage) ic.getImage();
					if (img != null) {
						nameList.add(((AppD) app).createImage(
								new MyImageD(img), fileName));
						imageFound = true;
					}
				}
				// System.out.println(nameList.toString());

			}

		} catch (UnsupportedFlavorException ufe) {
			app.setDefaultCursor();
			// ufe.printStackTrace();
			return null;

		} catch (IOException ioe) {
			app.setDefaultCursor();
			// ioe.printStackTrace();
			return null;

		} catch (Exception e) {
			app.setDefaultCursor();
			e.printStackTrace();
			return null;
		}

		app.setDefaultCursor();
		String[] f = new String[nameList.size()];
		return nameList.toArray(f);

	}

	/**
	 * Shows a file open dialog to choose an image file, Then the image file is
	 * loaded and stored in this application's imageManager.
	 * 
	 * @return fileName of image stored in imageManager
	 */
	public String getImageFromFile() {
		return getImageFromFile(null);
	}

	/**
	 * Loads and stores an image file is in this application's imageManager. If
	 * a null image file is passed, then a file dialog is opened to choose a
	 * file.
	 * 
	 * @return fileName of image stored in imageManager
	 */
	public String getImageFromFile(File imageFile) {

		MyImageD img = new MyImageD();
		String fileName = null;
		try {
			app.setWaitCursor();
			// else
			{
				if (imageFile == null) {

					/**************************************************************
					 * Mac OS X related code to work around JFileChooser problem
					 * on sandboxing. See
					 * http://intransitione.com/blog/take-java-to-app-store/
					 **************************************************************/
					if (((AppD) app).macsandbox) {

						FileDialog fd = new FileDialog(((AppD) app).getFrame());
						fd.setModal(true);
						File currentPath = ((AppD) app).getCurrentPath();
						fd.setMode(FileDialog.LOAD);
						if (currentPath != null) {
							fd.setDirectory(currentPath.toString());
						}
						fd.setFilenameFilter(new FilenameFilter() {
							public boolean accept(File dir, String name) {
								return (name.endsWith(".jpg")
										|| name.endsWith(".jpeg")
										|| name.endsWith(".png")
										|| name.endsWith(".bmp") || name
										.endsWith(".gif"));
							}
						});
						fd.setTitle(app.getMenu("Load"));

						fd.toFront();
						fd.setVisible(true);
						// FIXME: find a better place for this, we need to
						// change the
						// cursor back before NPE when file loading was
						// unsuccessful:
						app.setDefaultCursor();

						if (fd.getFile() != null) {
							imageFile = new File(fd.getDirectory() + "/"
									+ fd.getFile());
						}

						((AppD) app)
								.setCurrentPath(new File(fd.getDirectory()));

					} else {
						/**************************************************************
						 * End of Mac OS X related code.
						 **************************************************************/

						((DialogManagerD) getDialogManager()).initFileChooser();
						GeoGebraFileChooser fileChooser = ((DialogManagerD) getDialogManager())
								.getFileChooser();

						fileChooser.setMode(GeoGebraFileChooser.MODE_IMAGES);
						fileChooser.setCurrentDirectory(((AppD) app)
								.getCurrentImagePath());

						MyFileFilter fileFilter = new MyFileFilter();
						fileFilter.addExtension("jpg");
						fileFilter.addExtension("jpeg");
						fileFilter.addExtension("png");
						fileFilter.addExtension("gif");
						fileFilter.addExtension("bmp");
						fileFilter.addExtension("svg");
						fileFilter.setDescription(app.getPlain("Image"));
						fileChooser.resetChoosableFileFilters();
						fileChooser.setFileFilter(fileFilter);

						int returnVal = fileChooser.showOpenDialog(((AppD) app)
								.getMainComponent());
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							imageFile = fileChooser.getSelectedFile();
							if (imageFile != null) {
								((AppD) app).setCurrentImagePath(imageFile
										.getParentFile());
								if (!app.isApplet()) {
									GeoGebraPreferencesD
											.getPref()
											.saveDefaultImagePath(
													((AppD) app)
															.getCurrentImagePath());
								}
							}
						}

						if (imageFile == null) {
							app.setDefaultCursor();
							return null;
						}
					}
				}

				// get file name
				fileName = imageFile.getCanonicalPath();

				// load image
				img.load(imageFile);
			}

			return ((AppD) app).createImage(img, fileName);

		} catch (Exception e) {
			app.setDefaultCursor();
			e.printStackTrace();
			app.showError("LoadFileFailed");
			return null;
		}

	}

	/**
	 * Opens file chooser and returns a data file for the spreadsheet G.Sturr
	 * 2010-2-5
	 */
	public File getDataFile() {

		// TODO -- create MODE_DATA that shows preview of text file (or no
		// preview?)

		File dataFile = null;

		try {
			app.setWaitCursor();

			/**************************************************************
			 * Mac OS X related code to work around JFileChooser problem on
			 * sandboxing. See
			 * http://intransitione.com/blog/take-java-to-app-store/
			 **************************************************************/
			if (((AppD) app).macsandbox) {

				FileDialog fd = new FileDialog(((AppD) app).getFrame());
				fd.setModal(true);
				File currentPath = ((AppD) app).getCurrentPath();
				fd.setMode(FileDialog.LOAD);
				if (currentPath != null) {
					fd.setDirectory(currentPath.toString());
				}
				fd.setFilenameFilter(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return (name.endsWith(".txt") || name.endsWith(".csv") || name
								.endsWith(".dat"));
					}
				});

				fd.setTitle(app.getMenu("Load"));

				fd.toFront();
				fd.setVisible(true);
				// FIXME: find a better place for this, we need to change the
				// cursor back before NPE when file loading was unsuccessful:
				app.setDefaultCursor();

				if (fd.getFile() != null) {
					dataFile = new File(fd.getDirectory() + "/" + fd.getFile());
				}

				((AppD) app).setCurrentPath(new File(fd.getDirectory()));

				return dataFile;
			}
			/**************************************************************
			 * End of Mac OS X related code.
			 **************************************************************/

			((DialogManagerD) getDialogManager()).initFileChooser();
			GeoGebraFileChooser fileChooser = ((DialogManagerD) getDialogManager())
					.getFileChooser();

			fileChooser.setMode(GeoGebraFileChooser.MODE_DATA);
			fileChooser.setCurrentDirectory(((AppD) app).getCurrentImagePath());

			MyFileFilter fileFilter = new MyFileFilter();
			fileFilter.addExtension("txt");
			fileFilter.addExtension("csv");
			fileFilter.addExtension("dat");

			// fileFilter.setDescription(app.getPlain("Image"));
			fileChooser.resetChoosableFileFilters();
			fileChooser.setFileFilter(fileFilter);

			int returnVal = fileChooser.showOpenDialog(((AppD) app)
					.getMainComponent());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				dataFile = fileChooser.getSelectedFile();
				if (dataFile != null) {
					((AppD) app).setCurrentImagePath(dataFile.getParentFile());
					if (!app.isApplet()) {
						GeoGebraPreferencesD.getPref().saveDefaultImagePath(
								((AppD) app).getCurrentImagePath());
					}
				}
			}

		} catch (Exception e) {
			app.setDefaultCursor();
			e.printStackTrace();
			app.showError("LoadFileFailed");
			return null;
		}

		app.setDefaultCursor();
		return dataFile;

	}

	// returns true for YES or NO and false for CANCEL
	public boolean saveCurrentFile() {

		app.getEuclidianView1().reset();
		if (app.hasEuclidianView2(1)) {
			app.getEuclidianView2(1).reset();
		}
		// use null component for iconified frame
		Component comp = ((AppD) app).getMainComponent();
		if (((AppD) app).getFrame() instanceof GeoGebraFrame) {
			GeoGebraFrame frame = (GeoGebraFrame) ((AppD) app).getFrame();
			comp = frame != null && !frame.isIconified() ? frame : null;
		}

		// Michael Borcherds 2008-05-04
		Object[] options = { app.getMenu("Save"), app.getMenu("DontSave"),
				app.getMenu("Cancel") };
		int returnVal = JOptionPane.showOptionDialog(comp,
				app.getMenu("DoYouWantToSaveYourChanges"),
				app.getMenu("CloseFile"), JOptionPane.DEFAULT_OPTION,
				JOptionPane.WARNING_MESSAGE,

				null, options, options[0]);

		/*
		 * int returnVal = JOptionPane.showConfirmDialog( comp,
		 * getMenu("SaveCurrentFileQuestion"),
		 * GeoGebraConstants.APPLICATION_NAME + " - " +
		 * app.getPlain("Question"), JOptionPane.YES_NO_CANCEL_OPTION,
		 * JOptionPane.QUESTION_MESSAGE);
		 */

		switch (returnVal) {
		case 0:
			return save();

		case 1:
			return true;

		default:
			return false;
		}
	}

	public boolean save() {
		// app.getFrame().getJMenuBar()
		app.setWaitCursor();

		// close properties dialog if open
		getDialogManager().closeAll();

		boolean success = false;
		if (((AppD) app).getCurrentFile() != null) {
			// Mathieu Blossier - 2008-01-04
			// if the file is read-only, open save as
			if (!((AppD) app).getCurrentFile().canWrite()) {
				success = saveAs();
			} else {
				success = ((AppD) app).saveGeoGebraFile(((AppD) app)
						.getCurrentFile());
			}
		} else {
			success = saveAs();
		}

		app.setDefaultCursor();
		return success;
	}

	public boolean saveAs() {

		// Mathieu Blossier - 2008-01-04
		// if the file is hidden, set current file to null
		if (((AppD) app).getCurrentFile() != null) {
			if (!((AppD) app).getCurrentFile().canWrite()
					&& ((AppD) app).getCurrentFile().isHidden()) {
				((AppD) app).setCurrentFile(null);
				((AppD) app).setCurrentPath(null);
			}
		}

		String[] fileExtensions;
		String[] fileDescriptions;
		fileExtensions = new String[] { AppD.FILE_EXT_GEOGEBRA };
		fileDescriptions = new String[] { GeoGebraConstants.APPLICATION_NAME
				+ " " + app.getMenu("Files") };
		File file = showSaveDialog(fileExtensions,
				((AppD) app).getCurrentFile(), fileDescriptions, true, false);
		if (file == null)
			return false;

		boolean success = ((AppD) app).saveGeoGebraFile(file);
		if (success)
			((AppD) app).setCurrentFile(file);
		return success;
	}

	public File showSaveDialog(String fileExtension, File selectedFile,
			String fileDescription, boolean promptOverwrite, boolean dirsOnly) {

		if (selectedFile == null) {
			selectedFile = removeExtension(((AppD) app).getCurrentFile());
		}

		String[] fileExtensions = { fileExtension };
		String[] fileDescriptions = { fileDescription };
		return showSaveDialog(fileExtensions, selectedFile, fileDescriptions,
				promptOverwrite, dirsOnly);
	}

	public File showSaveDialog(final String[] fileExtensions,
			File selectedFile, String[] fileDescriptions,
			boolean promptOverwrite, boolean dirsOnly) {
		boolean done = false;
		File file = null;

		if (fileExtensions == null || fileExtensions.length == 0
				|| fileDescriptions == null) {
			return null;
		}
		String fileExtension = fileExtensions[0];

		/**************************************************************
		 * Mac OS X related code to work around JFileChooser problem on
		 * sandboxing. See http://intransitione.com/blog/take-java-to-app-store/
		 **************************************************************/
		if (((AppD) app).macsandbox) {
			while (!done) {

				NSSavePanel panel = new NSSavePanel();
				String result = panel.saveDialog(app.getMenu("Save"),
						fileExtension);
				file = new File(result);
				done = true;

				/*
				 * FileDialog fd = new FileDialog(((AppD) app).getFrame());
				 * fd.setModal(true); File currentPath = ((AppD)
				 * app).getCurrentPath(); fd.setMode(FileDialog.SAVE); if
				 * (currentPath != null) {
				 * fd.setDirectory(currentPath.toString()); }
				 * fd.setFilenameFilter(new FilenameFilter() { public boolean
				 * accept(File dir, String name) { for (String s :
				 * fileExtensions) { if (name.endsWith("." + s)) { return true;
				 * } } return false; } }); fd.setTitle(app.getMenu("Save")); if
				 * (selectedFile == null) { String str =
				 * app.getPlain("UntitledConstruction"); int length =
				 * str.length(); // Sandbox (when running the application
				 * directly from Finder) // may return with a filename which is
				 * incompatible, // thus we try to ensure that the filename does
				 * not contain // accented letters: str =
				 * Normalizer.normalize(str, Normalizer.Form.NFKD); str =
				 * str.replaceAll( "[^\\x20-\\x7E]", ""); if (str.length() * 2 <
				 * length) { // This normalization can filter out all or almost
				 * all // non-Latin characters. If this happens: str =
				 * "Untitled Construction"; // using the English version }
				 * fd.setFile(str + "." + fileExtension); } else {
				 * fd.setFile(selectedFile.getName()); }
				 * 
				 * fd.toFront(); fd.setVisible(true);
				 * 
				 * if (fd.getFile() == null) { // cancel pressed return null; }
				 * 
				 * file = new File(fd.getDirectory() + "/" + fd.getFile());
				 * ((AppD) app).setCurrentPath(new File(fd.getDirectory()));
				 * 
				 * // Don't add file extension since it will be disallowed on
				 * Mac in sandbox: // file = addExtension(file, fileExtension);
				 * lastFilenameOfSaveDialog = file.getName();
				 * 
				 * // Don't ask overwrite question again. Mac will do it
				 * already. done = true;
				 */
			}
			return file;
		}
		/**************************************************************
		 * End of Mac OS X related code.
		 **************************************************************/

		((DialogManagerD) getDialogManager()).initFileChooser();
		GeoGebraFileChooser fileChooser = ((DialogManagerD) getDialogManager())
				.getFileChooser();

		fileChooser.setMode(GeoGebraFileChooser.MODE_GEOGEBRA_SAVE);
		fileChooser.setCurrentDirectory(((AppD) app).getCurrentPath());

		if (dirsOnly)
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		// set selected file
		if (selectedFile != null) {
			fileExtension = AppD.getExtension(selectedFile);
			int i = 0;
			while (i < fileExtensions.length
					&& !fileExtension.equals(fileExtensions[i])) {
				i++;
			}
			if (i >= fileExtensions.length) {
				fileExtension = fileExtensions[0];
			}
			selectedFile = addExtension(selectedFile, fileExtension);
			fileChooser.setSelectedFile(selectedFile);
		} else
			fileChooser.setSelectedFile(null);
		fileChooser.resetChoosableFileFilters();
		MyFileFilter fileFilter;
		MyFileFilter mainFilter = null;
		for (int i = 0; i < fileExtensions.length; i++) {
			fileFilter = new MyFileFilter(fileExtensions[i]);
			if (fileDescriptions.length >= i && fileDescriptions[i] != null)
				fileFilter.setDescription(fileDescriptions[i]);
			fileChooser.addChoosableFileFilter(fileFilter);
			if (fileExtension.equals(fileExtensions[i])) {
				mainFilter = fileFilter;
			}
		}
		fileChooser.setFileFilter(mainFilter);

		while (!done) {
			// show save dialog
			int returnVal = fileChooser.showSaveDialog(((AppD) app)
					.getMainComponent());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = fileChooser.getSelectedFile();

				if (fileChooser.getFileFilter() instanceof org.geogebra.desktop.gui.app.MyFileFilter) {
					fileFilter = (MyFileFilter) fileChooser.getFileFilter();
					fileExtension = fileFilter.getExtension();
				} else {
					fileExtension = fileExtensions[0];
				}

				// remove all special characters from HTML filename
				if (fileExtension.equals(AppD.FILE_EXT_HTML)) {
					file = removeExtension(file);
					file = new File(file.getParent(),
							Util.keepOnlyLettersAndDigits(file.getName()));
				}

				// remove "*<>/\?|:
				file = new File(file.getParent(), Util.processFilename(file
						.getName())); // Michael
										// Borcherds
										// 2007-11-23

				// add file extension
				file = addExtension(file, fileExtension);
				fileChooser.setSelectedFile(file);
				lastFilenameOfSaveDialog = file.getName();

				if (promptOverwrite && file.exists()) {
					// ask overwrite question

					// Michael Borcherds 2008-05-04
					Object[] options = { app.getMenu("Overwrite"),
							app.getMenu("DontOverwrite") };
					int n = JOptionPane.showOptionDialog(
							((AppD) app).getMainComponent(),
							app.getPlain("OverwriteFile") + "\n"
									+ file.getName(), app.getPlain("Question"),
							JOptionPane.DEFAULT_OPTION,
							JOptionPane.WARNING_MESSAGE, null, options,
							options[1]);

					done = (n == 0);

					/*
					 * int n = JOptionPane.showConfirmDialog(
					 * app.getMainComponent(), app.getPlain("OverwriteFile") +
					 * "\n" + file.getAbsolutePath(), app.getPlain("Question"),
					 * JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					 * done = (n == JOptionPane.YES_OPTION);
					 */
				} else {
					done = true;
				}
			} else {
				// } else
				// return null;
				file = null;
				break;
			}
		}

		return file;
	}

	public static File addExtension(File file, String fileExtension) {
		if (file == null)
			return null;
		if (AppD.getExtension(file).equals(fileExtension))
			return file;
		return new File(file.getParentFile(), // path
				file.getName() + '.' + fileExtension); // filename
	}

	public static File removeExtension(File file) {
		if (file == null)
			return null;
		String fileName = file.getName();
		int dotPos = fileName.lastIndexOf('.');

		if (dotPos <= 0)
			return file;
		return new File(file.getParentFile(), // path
				fileName.substring(0, dotPos));
	}

	public void openURL() {
		InputDialogD id = new InputDialogOpenURL((AppD) app);
		id.setVisible(true);

	}

	public void openFromGGT() {
		if ((app).isSaved() || saveCurrentFile()) {
			((DialogManagerD) app.getDialogManager()).showOpenFromGGTDialog();
		}
	}

	public void openFile() {
		openFile(null);
	}

	/**
	 * Points to the given file in the file dialog popup window and offers to
	 * choose that file --- or a different one.
	 * 
	 * @param file
	 */
	public void openFile(File file) {

		if ((app).isSaved() || saveCurrentFile()) {
			app.setWaitCursor();

			/**************************************************************
			 * Mac OS X related code to work around JFileChooser problem on
			 * sandboxing. See
			 * http://intransitione.com/blog/take-java-to-app-store/
			 **************************************************************/
			if (((AppD) app).macsandbox) {

				FileDialog fd = new FileDialog(((AppD) app).getFrame());
				fd.setModal(true);
				File currentPath = null;
				if (file == null) {
					currentPath = ((AppD) app).getCurrentPath();
				} else {
					currentPath = file.getParentFile();
					fd.setFile(file.getName());
				}
				fd.setMode(FileDialog.LOAD);
				if (currentPath != null) {
					fd.setDirectory(currentPath.toString());
				}
				fd.setFilenameFilter(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return (name.endsWith("." + AppD.FILE_EXT_GEOGEBRA)
								|| name.endsWith("."
										+ AppD.FILE_EXT_GEOGEBRA_TOOL)
								|| name.endsWith("." + AppD.FILE_EXT_HTM)
								|| name.endsWith("." + AppD.FILE_EXT_HTML) || name
								.endsWith("." + AppD.FILE_EXT_OFF));
					}
				});
				fd.setTitle(app.getMenu("Load"));

				fd.toFront();
				fd.setVisible(true);
				// FIXME: find a better place for this, we need to change the
				// cursor back before NPE when file loading was unsuccessful:
				app.setDefaultCursor();

				File[] files = new File[1];
				if (fd.getFile() != null) {
					files[0] = new File(fd.getDirectory() + "/" + fd.getFile());
				}

				((AppD) app).setCurrentPath(new File(fd.getDirectory()));

				app.setDefaultCursor();
				doOpenFiles(files, true);
				return;
			}
			/**************************************************************
			 * End of Mac OS X related code.
			 **************************************************************/

			File oldCurrentFile = ((AppD) app).getCurrentFile();
			((DialogManagerD) getDialogManager()).initFileChooser();
			GeoGebraFileChooser fileChooser = ((DialogManagerD) getDialogManager())
					.getFileChooser();

			fileChooser.setMode(GeoGebraFileChooser.MODE_GEOGEBRA);
			fileChooser.setCurrentDirectory(((AppD) app).getCurrentPath());
			fileChooser.setMultiSelectionEnabled(true);
			fileChooser.setSelectedFile(oldCurrentFile);

			// GeoGebra File Filter
			MyFileFilter fileFilter = new MyFileFilter();
			fileFilter.addExtension(AppD.FILE_EXT_GEOGEBRA);
			fileFilter.addExtension(AppD.FILE_EXT_GEOGEBRA_TOOL);
			fileFilter.addExtension(AppD.FILE_EXT_HTML);
			fileFilter.addExtension(AppD.FILE_EXT_HTM);
			fileFilter.setDescription(GeoGebraConstants.APPLICATION_NAME
					+ app.getMenu("Files"));
			fileChooser.resetChoosableFileFilters();
			fileChooser.addChoosableFileFilter(fileFilter);

			MyFileFilter insertFilter = new MyFileFilter();
			insertFilter.addExtension(AppD.FILE_EXT_GEOGEBRA);
			insertFilter.setDescription(app.getMenu("InsertFile"));
			fileChooser.addChoosableFileFilter(insertFilter);

			MyFileFilter templateFilter = new MyFileFilter();
			templateFilter.addExtension(AppD.FILE_EXT_GEOGEBRA);
			templateFilter.setDescription(app.getMenu("ApplyTemplate"));
			fileChooser.addChoosableFileFilter(templateFilter);

			MyFileFilter offFilter = new MyFileFilter(AppD.FILE_EXT_OFF);
			// TODO: Localization
			offFilter.setDescription("OFF file");
			fileChooser.addChoosableFileFilter(offFilter);

			if (oldCurrentFile == null
					|| AppD.getExtension(oldCurrentFile).equals(
							AppD.FILE_EXT_GEOGEBRA)
					|| AppD.getExtension(oldCurrentFile).equals(
							AppD.FILE_EXT_GEOGEBRA_TOOL)) {
				fileChooser.setFileFilter(fileFilter);
			}

			app.setDefaultCursor();
			int returnVal = fileChooser.showOpenDialog(((AppD) app)
					.getMainComponent());

			File[] files = null;
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				files = fileChooser.getSelectedFiles();
			}

			FileFilter filter = fileChooser.getFileFilter();

			if (filter == fileFilter) {
				fileFilter = (MyFileFilter) fileChooser.getFileFilter();
				doOpenFiles(files, true, fileFilter.getExtension());
			} else if (filter == templateFilter) {
				// #4403
				app.setWaitCursor();
				app.setMoveMode();

				for (int i = 0; i < files.length; i++) {

					File file0 = files[i];

					if (!file0.exists()) {
						file0 = addExtension(file0, AppD.FILE_EXT_GEOGEBRA);
					}

					((AppD) app).applyTemplate(file0);

				}

				app.setDefaultCursor();

			} else if (filter == insertFilter) {

				app.setWaitCursor();
				app.setMoveMode();

				for (int i = 0; i < files.length; i++) {

					File file0 = files[i];

					if (!file0.exists()) {
						file0 = addExtension(file0, AppD.FILE_EXT_GEOGEBRA);
					}

					((AppD) app).insertFile(file0);
				}

				app.setDefaultCursor();

			} else if (filter == offFilter) {

				app.setWaitCursor();
				app.setMoveMode();

				for (int i = 0; i < files.length; i++) {
					
					File file0 = files[i];
					
					if (!file0.exists()) {
						file0 = addExtension(file0, AppD.FILE_EXT_OFF);
					}

				}

				doOpenFiles(files, true);
				app.setDefaultCursor();

			} else {
				doOpenFiles(files, true);
			}

			fileChooser.setMultiSelectionEnabled(false);
		}
	}

	public synchronized void doOpenFiles(File[] files,
			boolean allowOpeningInThisInstance) {
		doOpenFiles(files, allowOpeningInThisInstance, AppD.FILE_EXT_GEOGEBRA);
	}

	public synchronized void doOpenFiles(File[] files,
			boolean allowOpeningInThisInstance, String extension) {
		htmlLoaded = false;
		// there are selected files
		if (files != null) {
			File file;
			int counter = 0;
			for (int i = 0; i < files.length; i++) {
				file = files[i];

				if (!file.exists()) {
					file = addExtension(file, extension);
					if (extension.equals(AppD.FILE_EXT_GEOGEBRA)
							&& !file.exists()) {
						file = addExtension(removeExtension(file),
								AppD.FILE_EXT_GEOGEBRA_TOOL);
					}
					if (extension.equals(AppD.FILE_EXT_GEOGEBRA)
							&& !file.exists()) {
						file = addExtension(removeExtension(file),
								AppD.FILE_EXT_HTML);
					}
					if (extension.equals(AppD.FILE_EXT_GEOGEBRA)
							&& !file.exists()) {
						file = addExtension(removeExtension(file),
								AppD.FILE_EXT_HTM);
					}

					if (extension.equals(AppD.FILE_EXT_GEOGEBRA)
							&& !file.exists()) {
						file = addExtension(removeExtension(file),
								AppD.FILE_EXT_OFF);
					}

					if (!file.exists()) {
						// Put the correct extension back on for the error
						// message
						file = addExtension(removeExtension(file), extension);

						JOptionPane.showConfirmDialog(((AppD) app)
								.getMainComponent(),
								app.getLocalization().getError("FileNotFound")
										+ ":\n" + file.getAbsolutePath(), app
										.getLocalization().getError("Error"),
								JOptionPane.DEFAULT_OPTION,
								JOptionPane.WARNING_MESSAGE);

					}
				}

				String ext = AppD.getExtension(file).toLowerCase(Locale.US);

				if (file.exists()) {
					if (AppD.FILE_EXT_GEOGEBRA_TOOL.equals(ext)) {
						// load macro file
						loadFile(file, true);
					} else if (AppD.FILE_EXT_HTML.equals(ext)
							|| AppD.FILE_EXT_HTM.equals(ext)) {
						// load HTML file with applet param ggbBase64
						// if we loaded from GGB, we don't want to overwrite old
						// file
						htmlLoaded = loadBase64File(file);
					} else if (AppD.FILE_EXT_OFF.equals(ext)) {
						loadOffFile(file);
					} else {
						// standard GeoGebra file
						GeoGebraFrame inst = GeoGebraFrame
								.getInstanceWithFile(file);
						if (inst == null) {
							counter++;
							if (counter == 1 && allowOpeningInThisInstance) {
								// open first file in current window
								loadFile(file, false);
							} else {
								// create new window for file
								try {
									String[] args = { file.getCanonicalPath() };
									GeoGebraFrame wnd = GeoGebraFrame
											.createNewWindow(new CommandLineArguments(
													args));
									wnd.toFront();
									wnd.requestFocus();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						} else if (counter == 0) {
							// there is an instance with this file opened
							inst.toFront();
							inst.requestFocus();
						}
					}
				}
			}
		}

	}

	public void allowGUIToRefresh() {
		if (!SwingUtilities.isEventDispatchThread())
			return;
	}

	/**
	 * Passes a transferable object to the application's dropTargetListener.
	 * Returns true if a ggb file was dropped succesfully. This is a utility
	 * method for component transfer handlers that need to pass potential ggb
	 * file drops on to the top level drop handler.
	 * 
	 * @param t
	 * @return
	 */
	public boolean handleGGBFileDrop(Transferable t) {
		FileDropTargetListener dtl = ((GeoGebraFrame) ((AppD) app).getFrame())
				.getDropTargetListener();
		boolean isGGBFileDrop = dtl.handleFileDrop(t);
		return (isGGBFileDrop);
	}

	public boolean loadFile(final File file, final boolean isMacroFile) {
		boolean success = ((AppD) app).loadFile(file, isMacroFile);

		updateGUIafterLoadFile(success, isMacroFile);
		app.setDefaultCursor();
		return success;
	}

	// See http://stackoverflow.com/questions/6198894/java-encode-url for an
	// explanation
	public static URL getEscapedUrl(String url) throws Exception {
		if (url.startsWith("www")) {
			url = "http://" + url;
		}
		URL u = new URL(url);
		return new URI(u.getProtocol(), u.getAuthority(), u.getPath(),
				u.getQuery(), u.getRef()).toURL();
	}

	/*
	 * loads an html file with <param name="ggbBase64" value="UEsDBBQACAAI...
	 */
	public boolean loadBase64File(final File file) {
		boolean success = ((AppD) app).loadBase64File(file);
		updateGUIafterLoadFile(success, false);
		return success;

	}

	/**
	 * Load off files to current view
	 * 
	 * @param file
	 *            off file
	 * @return status
	 */
	public boolean loadOffFile(final File file) {

		boolean success = ((AppD) app).loadOffFile(file);
		updateGUIafterLoadFile(success, false);

		return success;
	}

	@Override
	protected boolean loadURL_GGB(String urlString) throws Exception {
		URL url = getEscapedUrl(urlString);
		return ((AppD) app).loadXML(url, false);
	}

	@Override
	protected boolean loadURL_base64(String urlString) throws IOException {
		byte[] zipFile = Base64.decode(urlString);
		return ((AppD) app).loadXML(zipFile);
	}

	@Override
	protected boolean loadFromApplet(String urlString) throws Exception {
		URL url = getEscapedUrl(urlString);
		boolean success = ((AppD) app).loadFromHtml(url);

		// fallback: maybe some address like download.php?file=1234,
		// e.g. the forum
		if (!success) {
			boolean isMacroFile = urlString.contains(".ggt");
			success = ((AppD) app).loadXML(url, isMacroFile);
		}

		return success;
	}

	public void updateGUIafterLoadFile(boolean success, boolean isMacroFile) {
		if (success && !isMacroFile
				&& !app.getSettings().getLayout().isIgnoringDocumentLayout()) {
			getLayout().setPerspectives(app.getTmpPerspectives(), null);
			SwingUtilities
					.updateComponentTreeUI(getLayout().getRootComponent());

			if (!app.isIniting()) {
				updateFrameSize(); // checks internally if frame is available
				if (app.needsSpreadsheetTableModel())
					(app).getSpreadsheetTableModel(); // ensure create one if
														// not already done
			}
		} else if (isMacroFile && success) {
			refreshCustomToolsInToolBar();
			((AppD) app).updateToolBar();
			((AppD) app).updateContentPane();
		}

		if (app.isEuclidianView3Dinited()) {
			EuclidianView ev = (EuclidianView) app.getEuclidianView3D();
			ev.updateFonts();
			((EuclidianView3DInterface) ev).updateAllDrawables();
		}
		// force JavaScript ggbOnInit(); to be called
		if (!app.isApplet())
			app.getScriptManager().ggbOnInit();
	}


	protected boolean initActions() {
		if (showAxesAction != null)
			return false;

		showAxesAction = new AbstractAction(app.getMenu("Axes"),
				((AppD) app).getScaledIcon("axes.gif")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				showAxesCmd();

			}
		};

		showGridAction = new AbstractAction(app.getMenu("Grid"),
				((AppD) app).getScaledIcon("grid.gif")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				showGridCmd();

			}
		};

		undoAction = new AbstractAction(app.getMenu("Undo"),
				((AppD) app).getScaledIcon("menu-edit-undo.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				undo();

			}
		};

		redoAction = new AbstractAction(app.getMenu("Redo"),
				((AppD) app).getScaledIcon("menu-edit-redo.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {

				redo();
			}
		};

		updateActions();

		return true;
	}

	@Override
	public void updateCheckBoxesForShowConstructinProtocolNavigation(int id) {
		if (propertiesView != null) {
			propertiesView.updatePanelGUI(id);
		}
	}

	@Override
	public void updateActions() {
		if (undoAction != null) {
			if (app.isUndoActive()) {
				undoAction.setEnabled(kernel.undoPossible());
			} else {
				// eg --enableUndo=false
				undoAction.setEnabled(false);
			}
		}
		if (redoAction != null) {
			if (app.isUndoActive()) {
				redoAction.setEnabled(kernel.redoPossible());
			} else {
				// eg --enableUndo=false
				redoAction.setEnabled(false);
			}
		}

	}

	public int getMenuBarHeight() {
		if (menuBar == null)
			return 0;
		else
			return ((JMenuBar) menuBar).getHeight();
	}

	public int getAlgebraInputHeight() {
		if (app.showAlgebraInput() && algebraInput != null)
			return algebraInput.getHeight();
		else
			return 0;
	}

	public AbstractAction getShowAxesAction() {
		initActions();
		return showAxesAction;
	}

	public AbstractAction getShowGridAction() {
		initActions();
		return showGridAction;
	}

	public ToolbarD getGeneralToolbar() {
		return toolbarPanel.getFirstToolbar();
	}

	public String getToolbarDefinition() {
		// "null" may appear in files created using some buggy versions of Touch
		if (("null".equals(strCustomToolbarDefinition) || strCustomToolbarDefinition == null)
				&& toolbarPanel != null)
			return getGeneralToolbar().getDefaultToolbarString();
		return strCustomToolbarDefinition;
	}

	public void removeFromToolbarDefinition(int mode) {
		if (strCustomToolbarDefinition != null) {
			// Application.debug("before: " + strCustomToolbarDefinition +
			// ",  delete " + mode);

			strCustomToolbarDefinition = strCustomToolbarDefinition.replaceAll(
					Integer.toString(mode), "");
		}
	}

	public void addToToolbarDefinition(int mode) {
		if (this.getActiveEuclidianView().getDimension() > 2) {
			DockPanel panel = this.getLayout().getDockManager()
					.getPanel(this.getActiveEuclidianView().getViewID());
			panel.addToToolbar(mode);
			panel.updateToolbar();
			return;
		}
		if (strCustomToolbarDefinition != null) {
			int macroNum = kernel.getMacroNumber();
			strCustomToolbarDefinition = strCustomToolbarDefinition + " | "
					+ mode;
			for (int i = 1; i < macroNum; i++) {
				int m = kernel.getMacroID(kernel.getMacro(i));
				strCustomToolbarDefinition += ", " + mode;
			}
		}
	}

	public void showURLinBrowser(URL url) {
		App.debug("opening URL:" + url);
		if (AppD.getJApplet() != null) {
			AppD.getJApplet().getAppletContext().showDocument(url, "_blank");
		} else {
			App.debug("opening URL:" + url.toExternalForm());
			BrowserLauncher.openURL(url.toExternalForm());
		}
	}

	public void openToolHelp() {
		openToolHelp(app.getMode());

	}

	public void openToolHelp(int mode) {

		String toolName = app.getToolNameOrHelp(mode, true);
		String helpText = app.getToolNameOrHelp(mode, false);
		ImageIcon icon;
		String modeTextInternal = null;

		if (mode >= EuclidianConstants.MACRO_MODE_ID_OFFSET) {

			Macro macro = kernel.getMacro(mode
					- EuclidianConstants.MACRO_MODE_ID_OFFSET);

			String iconName = macro.getIconFileName();
			MyImageD img = ((AppD) app).getExternalImage(iconName);
			Color border = Color.lightGray;

			if (img == null || img.isSVG()) {
				// default icon
				icon = ((AppD) app).getToolBarImage("mode_tool.png", border);
			} else {
				// use image as icon
				icon = new ImageIcon(ImageManagerD.addBorder(img.getImage(),
						border));
			}

		} else {

			modeTextInternal = EuclidianConstants.getModeText(mode);
			icon = ((AppD) app).getToolBarImage("mode_" + modeTextInternal
					+ ".png", Color.BLACK);
		}

		Object[] options = { app.getPlain("ShowOnlineHelp"),
				app.getPlain("Cancel") };
		int n = JOptionPane.showOptionDialog(((AppD) app).getMainComponent(),
				helpText, app.getMenu("ToolHelp") + " - " + toolName,
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, icon,
				options, // the titles of buttons
				options[0]); // default button title

		if (n == 0) {
			if (modeTextInternal == null) {
				// show help for custom tools?
				openHelp("Custom_Tools", Help.GENERIC);
			} else {
				openHelp(modeTextInternal, Help.TOOL);
			}
		}
	}

	@Override
	public void openHelp(String page, Help type) {
		try {
			URL helpURL = getEscapedUrl(getHelpURL(type, page));
			showURLinBrowser(helpURL);
		} catch (MyError e) {
			app.showError(e);
		} catch (Exception e) {
			App.debug("openHelp error: " + e.toString() + " " + e.getMessage()
					+ " " + page + " " + type);
			app.showError(e.getMessage());
			e.printStackTrace();
		}
	}

	public void showURLinBrowser(String strURL) {
		try {
			URL url = getEscapedUrl(strURL);
			showURLinBrowser(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns text "Created with <ApplicationName>" and link to application
	 * homepage in html.
	 */
	public String getCreatedWithHTML(boolean JSXGraph) {
		String ret;

		ret = StringUtil.toHTMLString(app.getPlain("CreatedWithGeoGebra"));

		if (ret.toLowerCase(Locale.US).indexOf("geogebr") == -1)
			ret = "Created with GeoGebra";

		String[] words = ret.split(" ");

		ret = "";

		for (int i = 0; i < words.length; i++) {
			// deliberate 'a' missing
			if (words[i].toLowerCase(Locale.US).startsWith("geogebr")) {
				// wrap transletion of GeoGebra to make a link
				words[i] = "<a href=\"" + GeoGebraConstants.GEOGEBRA_WEBSITE
						+ "\" target=\"_blank\" >" + words[i] + "</a>";
			}
			ret += words[i] + ((i == words.length - 1) ? "" : " ");
		}

		return ret;
	}

	@Override
	public int setToolbarMode(int mode, ModeSetter m) {
		if (toolbarPanel == null) {
			return 0;
		}

		int ret = toolbarPanel.setMode(mode);
		layout.getDockManager().setToolbarMode(mode);
		return ret;
	}

	/**
	 * Exports construction protocol as html
	 */
	/*
	 * final public void exportConstructionProtocolHTML() {
	 * constructionProtocolView.initProtocol();
	 * constructionProtocolView.showHTMLExportDialog(); }
	 */

	public final String getCustomToolbarDefinition() {
		return strCustomToolbarDefinition;
	}

	public AbstractAction getRedoAction() {
		initActions();
		return redoAction;
	}

	public AbstractAction getUndoAction() {
		initActions();
		return undoAction;
	}

	public void updateFrameSize() {
		JFrame fr = ((AppD) app).getFrame();
		if (fr != null) {
			((GeoGebraFrame) fr).updateSize();
			((AppD) app).validateComponent();
		}
	}

	public void updateFrameTitle() {
		if (!(((AppD) app).getFrame() instanceof GeoGebraFrame))
			return;

		GeoGebraFrame frame = (GeoGebraFrame) ((AppD) app).getFrame();

		StringBuilder sb = new StringBuilder();
		if (((AppD) app).getCurrentFile() != null) {
			sb.append(((AppD) app).getCurrentFile().getName());
		} else {
			sb.append(GeoGebraConstants.APPLICATION_NAME);
			if (GeoGebraFrame.getInstanceCount() > 1) {
				int nr = frame.getInstanceNumber();
				sb.append(" (");
				sb.append(nr + 1);
				sb.append(')');
			}
		}
		frame.setTitle(sb.toString());
	}

	public Object createFrame() {
		GeoGebraFrame wnd = new GeoGebraFrame();
		wnd.setGlassPane(layout.getDockManager().getGlassPane());
		wnd.setApplication((AppD) app);

		return wnd;
	}

	public synchronized void exitAll() {
		ArrayList<GeoGebraFrame> insts = GeoGebraFrame.getInstances();
		GeoGebraFrame[] instsCopy = new GeoGebraFrame[insts.size()];
		for (int i = 0; i < instsCopy.length; i++) {
			instsCopy[i] = insts.get(i);
		}

		for (int i = 0; i < instsCopy.length; i++) {
			instsCopy[i].getApplication().exit();
		}
	}

	public void exitAllCurrent() {
		if (layout != null) {
			layout.getDockManager().exitAllCurrent();
		}
	}

	VirtualKeyboardListener currentKeyboardListener = null;

	public VirtualKeyboardListener getCurrentKeyboardListener() {
		return currentKeyboardListener;
	}

	public void setCurrentTextfield(VirtualKeyboardListener keyboardListener,
			boolean autoClose) {
		currentKeyboardListener = keyboardListener;
		if (virtualKeyboard != null)
			if (currentKeyboardListener == null) {
				// close virtual keyboard when focus lost
				// ... unless we've lost focus because we've just opened it!
				if (autoClose)
					toggleKeyboard(false);
			} else {
				// open virtual keyboard when focus gained
				if (AppD.isVirtualKeyboardActive())
					toggleKeyboard(true);
			}
	}

	WindowsUnicodeKeyboard kb = null;

	public void insertStringIntoTextfield(String text, boolean altPressed,
			boolean ctrlPressed, boolean shiftPressed) {

		if (currentKeyboardListener != null && !text.equals("\n")
				&& (!text.startsWith("<") || !text.endsWith(">"))
				&& !altPressed && !ctrlPressed) {
			currentKeyboardListener.insertString(text);
		} else {
			// use Robot if no TextField currently active
			// or for special keys eg Enter
			if (kb == null) {
				try {
					kb = new WindowsUnicodeKeyboard();
				} catch (Exception e) {
				}
			}

			kb.doType(altPressed, ctrlPressed, shiftPressed, text);

		}
	}

	VirtualKeyboard virtualKeyboard = null;

	public void toggleKeyboard(boolean show) {
		getVirtualKeyboard().setVisible(show);
	}

	/**
	 * @return The virtual keyboard (initializes it if necessary)
	 */
	public VirtualKeyboard getVirtualKeyboard() {
		if (virtualKeyboard == null) {
			KeyboardSettings settings = app.getSettings().getKeyboard();
			virtualKeyboard = new VirtualKeyboard(((AppD) app),
					settings.getKeyboardWidth(), settings.getKeyboardHeight(),
					settings.getKeyboardOpacity());
			settings.addListener(virtualKeyboard);
		}

		return virtualKeyboard;
	}

	public boolean hasVirtualKeyboard() {
		return virtualKeyboard != null;
	}

	/*
	 * HandwritingRecognitionTool handwritingRecognition = null;
	 * 
	 * public Component getHandwriting() {
	 * 
	 * if (handwritingRecognition == null) { handwritingRecognition = new
	 * HandwritingRecognitionTool(app); } return handwritingRecognition;
	 * 
	 * }
	 * 
	 * public void toggleHandwriting(boolean show) {
	 * 
	 * if (handwritingRecognition == null) { handwritingRecognition = new
	 * HandwritingRecognitionTool(app); }
	 * handwritingRecognition.setVisible(show);
	 * handwritingRecognition.repaint();
	 * 
	 * }
	 * 
	 * public boolean showHandwritingRecognition() { if (handwritingRecognition
	 * == null) return false;
	 * 
	 * return handwritingRecognition.isVisible(); }
	 */

	public boolean showVirtualKeyboard() {
		if (virtualKeyboard == null)
			return false;

		return virtualKeyboard.isVisible();
	}

	public boolean noMenusOpen() {
		if (popupMenu != null && popupMenu.getWrappedPopup().isVisible()) {
			// Application.debug("menus open");
			return false;
		}
		if (drawingPadpopupMenu != null
				&& drawingPadpopupMenu.getWrappedPopup().isVisible()) {
			// Application.debug("menus open");
			return false;
		}

		// Application.debug("no menus open");
		return true;
	}

	// TextInputDialog recent symbol list
	private ArrayList<String> recentSymbolList;

	public ArrayList<String> getRecentSymbolList() {
		if (recentSymbolList == null) {
			recentSymbolList = new ArrayList<String>();
			recentSymbolList.add(Unicode.PI_STRING);
			for (int i = 0; i < 9; i++) {
				recentSymbolList.add("");
			}
		}
		return recentSymbolList;
	}

	public static void setFontRecursive(Container c, Font font) {
		Component[] components = c.getComponents();
		for (Component com : components) {
			com.setFont(font);
			if (com instanceof Container)
				setFontRecursive((Container) com, font);
		}
	}

	public static void setLabelsRecursive(Container c) {
		Component[] components = c.getComponents();
		for (Component com : components) {
			// com.setl(font);
			// ((Panel)com).setLabels();
			if (com instanceof Container) {
				// Application.debug("container"+com.getClass());
				setLabelsRecursive((Container) com);
			}

			if (com instanceof SetLabels) {
				// Application.debug("container"+com.getClass());
				((SetLabels) com).setLabels();
			}

			/*
			 * for debugging, to show classes that might benefit from
			 * implementing SetLabels if (com instanceof JPanel && !(com
			 * instanceof SetLabels)
			 * &&!(com.getClass().toString().startsWith("class java"))) {
			 * //((JPanel)com).setla
			 * System.err.println(com.getClass().toString()+" panel "+com); }//
			 */

		}
	}

	private InputBarHelpPanel inputHelpPanel;

	public boolean hasInputHelpPanel() {
		if (inputHelpPanel == null)
			return false;
		return true;
	}

	public void reInitHelpPanel() {

		if (inputHelpPanel == null)
			inputHelpPanel = new InputBarHelpPanel((AppD) app);
		else {
			inputHelpPanel.setLabels();
		}
	}

	public Component getInputHelpPanel() {

		if (inputHelpPanel == null)
			inputHelpPanel = new InputBarHelpPanel((AppD) app);
		return inputHelpPanel;
	}

	public void setFocusedPanel(MouseEventND event, boolean updatePropertiesView) {
		// determine parent panel to change focus
		EuclidianDockPanelAbstract panel = (EuclidianDockPanelAbstract) SwingUtilities
				.getAncestorOfClass(EuclidianDockPanelAbstract.class,
						event.getComponent());

		setFocusedPanel(panel, updatePropertiesView);

	}

	public void setFocusedPanel(int viewId, boolean updatePropertiesView) {
		setFocusedPanel(getLayout().getDockManager().getPanel(viewId),
				updatePropertiesView);

	}

	public void setFocusedPanel(DockPanel panel, boolean updatePropertiesView) {

		if (panel != null) {
			getLayout().getDockManager().setFocusedPanel(panel,
					updatePropertiesView);

			// notify the properties view
			if (updatePropertiesView)
				updatePropertiesView();

		}

	}

	public void updateAlgebraInput() {
		if (algebraInput != null)
			algebraInput.initGUI();
	}

	public void updatePropertiesView() {
		if (propertiesView != null) {
			propertiesView.updatePropertiesView();
		}
	}

	/**
	 * close properties view
	 * 
	 */
	public void updatePropertiesViewStylebar() {
		if (propertiesView != null) {
			propertiesView.updateStyleBar();
		}
	}

	public void mouseReleasedForPropertiesView(boolean creatorMode) {
		if (propertiesView != null) {
			propertiesView.mouseReleasedForPropertiesView(creatorMode);
		}
	}

	public void mousePressedForPropertiesView() {
		if (propertiesView != null) {
			propertiesView.mousePressedForPropertiesView();
		}
	}

	public void showPopupMenu(ArrayList<GeoElement> selectedGeos,
			EuclidianViewInterfaceCommon view,
			org.geogebra.common.awt.GPoint mouseLoc) {
		showPopupMenu(selectedGeos,
				((EuclidianViewInterfaceDesktop) view).getJPanel(), mouseLoc);

	}

	public void showPopupChooseGeo(ArrayList<GeoElement> selectedGeos,
			ArrayList<GeoElement> geos, EuclidianViewInterfaceCommon view,
			org.geogebra.common.awt.GPoint p) {

		showPopupChooseGeo(selectedGeos, geos, (EuclidianView) view, p);

	}

	public void setFocusedPanel(AbstractEvent event,
			boolean updatePropertiesView) {
		setFocusedPanel((MouseEventND) event, updatePropertiesView);
	}

	public void loadImage(GeoPoint loc, Object transfer, boolean fromClipboard) {
		loadImage(loc, fromClipboard, (Transferable) transfer);

	}

	/**
	 * Creates a new GeoImage, using an image provided by either a Transferable
	 * object or the clipboard contents, then places it at the given location
	 * (real world coords). If the transfer content is a list of images, then
	 * multiple GeoImages will be created.
	 * 
	 * @return whether a new image was created or not
	 */
	public boolean loadImage(GeoPoint loc, boolean fromClipboard,
			Transferable transfer) {
		app.setWaitCursor();

		String[] fileName = null;

		if (fromClipboard) {
			fileName = getImageFromTransferable(null);
		} else if (transfer != null) {
			fileName = getImageFromTransferable(transfer);
		} else {
			fileName = new String[1];
			fileName[0] = getImageFromFile(); // opens file chooser dialog
		}

		boolean ret;
		if (fileName.length == 0 || fileName[0] == null) {
			ret = false;
		} else {
			// create GeoImage object(s) for this fileName
			GeoImage geoImage = null;

			if (!loc.isLabelSet()) {
				loc.setLabel(null);
			}

			for (int i = 0; i < fileName.length; i++) {
				GeoPoint point1;
				if (i == 0) {
					point1 = loc;
				} else {
					point1 = new GeoPoint(app.getKernel().getConstruction());
					point1.setCoordsFromPoint(loc);
					point1.setLabel(null);
				}

				geoImage = new GeoImage(app.getKernel().getConstruction());
				geoImage.setImageFileName(fileName[i]);
				App.debug("filename = " + fileName[i]);
				geoImage.setCorner(point1, 0);

				GeoPoint point2 = new GeoPoint(app.getKernel()
						.getConstruction());
				geoImage.calculateCornerPoint(point2, 2);
				geoImage.setCorner(point2, 1);
				point2.setLabel(null);

				geoImage.setLabel(null);

				GeoImage.updateInstances(app);
			}
			// make sure only the last image will be selected
			GeoElement[] geos = { geoImage };
			app.getActiveEuclidianView().getEuclidianController()
					.clearSelections();
			app.getActiveEuclidianView().getEuclidianController()
					.memorizeJustCreatedGeos(geos);
			ret = true;
		}

		app.setDefaultCursor();
		return ret;
	}

	public void showDrawingPadPopup(EuclidianViewInterfaceCommon view,
			org.geogebra.common.awt.GPoint mouseLoc) {
		showDrawingPadPopup(((EuclidianViewD) view).getJPanel(), mouseLoc);
	}

	public void showDrawingPadPopup3D(EuclidianViewInterfaceCommon view,
			org.geogebra.common.awt.GPoint mouseLoc) {
		// 3D stuff
	}

	public void showPropertiesViewSliderTab() {
		propertiesView.showSliderTab();
	}

	public void showGraphicExport() {
		app.getSelectionManager().clearSelectedGeos(true, false);
		app.updateSelection(false);

		JDialog d = new org.geogebra.desktop.export.GraphicExportDialog((AppD) app);

		d.setVisible(true);

	}

	public void showPSTricksExport() {
		GeoGebraToPstricks export = new GeoGebraToPstricksD(app);
		new PstricksFrame(export);

	}

	public void showWebpageExport() {
		app.getSelectionManager().clearSelectedGeos(true, false);
		app.updateSelection(false);
		org.geogebra.desktop.export.WorksheetExportDialog d = new org.geogebra.desktop.export.WorksheetExportDialog(
				(AppD) app);

		d.setVisible(true);
	}

	public void clearInputbar() {
		((AlgebraInput) getAlgebraInput()).clear();
	}

	public int getInputHelpPanelMinimumWidth() {
		return getInputHelpPanel().getMinimumSize().width;
	}

	public int getActiveToolbarId() {
		if (toolbarPanel == null)
			return -1;
		return toolbarPanel.getActiveToolbar();
	}

	/**
	 * Tells if the 3D View is shown in the current window
	 * 
	 * @return whether 3D View is switched on
	 */
	public boolean is3DViewShown() {
		return menuBar.is3DViewShown();
	}

	@Override
	public AppD getApp() {
		return (AppD) app;
	}

	public void setToolBarDefinition(String toolBarDefinition) {
		strCustomToolbarDefinition = toolBarDefinition;
	}

	public void clearAbsolutePanels() {
		// TODO Auto-generated method stub

	}

	public boolean checkAutoCreateSliders(String s, AsyncOperation callback) {
		Component comp = ((AppD) app).getMainComponent();
		if (((AppD) app).getFrame() instanceof GeoGebraFrame) {
			GeoGebraFrame frame = (GeoGebraFrame) ((AppD) app).getFrame();
			comp = frame != null && !frame.isIconified() ? frame : null;
		}

		LocalizationD loc = ((AppD) app).getLocalization();

		// Michael Borcherds 2008-05-04
		Object[] options = { app.getPlain("CreateSliders"),
				app.getMenu("Cancel") };
		int returnVal = JOptionPane.showOptionDialog(comp,
				loc.getPlain("CreateSlidersForA", s),
				loc.getPlain("CreateSliders"), JOptionPane.DEFAULT_OPTION,
				JOptionPane.WARNING_MESSAGE,

				((AppD) app).getModeIcon(EuclidianConstants.MODE_SLIDER),
				options, options[0]);

		return returnVal == 0;

	}

	public boolean belongsToToolCreator(ListCellRenderer renderer) {
		return ToolCreationDialog.isMyCellRenderer(renderer);
	}


	protected ConstructionProtocolNavigation newConstructionProtocolNavigation() {
		ConstructionProtocolNavigationD cpn = new ConstructionProtocolNavigationD(
				this.getApp());
		if (constructionProtocolView != null) {
			cpn.register(constructionProtocolView);
		}
		return cpn;
	}




	public void login() {
		app.getDialogManager().showLogInDialog();
	}

	public void logout() {
		app.getDialogManager().showLogOutDialog();
	}

	@Override
	protected void setCallerApp() {
		caller_APP = DESKTOP;
	}

	@Override
	public void invokeLater(Runnable runnable) {
		SwingUtilities.invokeLater(runnable);

	}

	@Override
	public int getEuclidianViewCount() {
		return euclidianView2.size();
	}

	public void resetCasView() {
		if (casView != null) {
			casView.resetCursor();
		}
	}

	public String oomlToMathml(String ooml) {
		TransformerFactory factory = TransformerFactory.newInstance();
		ooml = ooml.replaceAll("<i[^>]*>", "").replaceAll("<span[^>]*>",
 "").replace("</i>", "")
.replace("</span>", "")
				.replace("<m:r>", "<m:r><m:t>")
				.replace("</m:r>", "</m:t></m:r>");
		App.debug(ooml);
		Source xmlFile = new StreamSource(new StringReader(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"+
 "<w:document xmlns:m=\"http://schemas.openxmlformats.org/officeDocument/2006/math\" "
						  +"xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\""
								+ "><w:body><w:p>" + ooml
								+ "</w:p></w:body></w:document>"));

		try {
			File ssFile = new File(
					"C:\\Program Files\\Microsoft Office 15\\root\\office15\\OMML2MML.XSL");
			if (!ssFile.exists()) {
				ssFile = new File(
						"C:\\Program Files\\Microsoft Office\\Office14\\OMML2MML.XSL");
			}
			Source stylesheet = new StreamSource(
					new File(
							"C:\\Program Files\\Microsoft Office 15\\root\\office15\\OMML2MML.XSL"));
			Transformer transformer = factory.newTransformer(stylesheet);
			StringWriter writer = new StringWriter();
			Result output = new StreamResult(writer);
			transformer.transform(xmlFile, output);
			String xml = writer.toString();
			return xml.substring(xml.indexOf('>') + 1).replace("mml:", "");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean hasDataCollectionView() {
		// not available in desktop
		return false;
	}

	public void getDataCollectionViewXML(StringBuilder sb, boolean asPreference) {
		// not available in desktop
	}

	public String getToolImageURL(int mode, GeoImage gi) {
		String modeStr = StringUtil.toLowerCase(EuclidianConstants
				.getModeText(mode));
		return app.getImageManager().createImage(
				"/org/geogebra/common/icons_toolbar/p64/mode_" + modeStr
						+ ".png", app);
	}

	public EuclidianViewInterfaceCommon getPlotPanelEuclidanView() {
		return null;
	}

}