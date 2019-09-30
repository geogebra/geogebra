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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
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
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.JTextComponent;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint;
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
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolNavigation;
import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
import org.geogebra.common.jre.util.Base64;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.App;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.Util;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.CommandLineArguments;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.cas.view.CASViewD;
import org.geogebra.desktop.euclidian.EuclidianControllerD;
import org.geogebra.desktop.euclidian.EuclidianViewD;
import org.geogebra.desktop.euclidian.event.MouseEventND;
import org.geogebra.desktop.euclidianND.EuclidianViewInterfaceD;
import org.geogebra.desktop.export.GraphicExportDialog;
import org.geogebra.desktop.export.WorksheetExportDialog;
import org.geogebra.desktop.export.pstricks.GeoGebraToPstricksD;
import org.geogebra.desktop.export.pstricks.PstricksFrame;
import org.geogebra.desktop.gui.app.GeoGebraFrame;
import org.geogebra.desktop.gui.app.MyFileFilter;
import org.geogebra.desktop.gui.color.GeoGebraColorChooser;
import org.geogebra.desktop.gui.dialog.DialogManagerD;
import org.geogebra.desktop.gui.dialog.InputDialogD;
import org.geogebra.desktop.gui.dialog.InputDialogOpenURL;
import org.geogebra.desktop.gui.dialog.ToolCreationDialogD;
import org.geogebra.desktop.gui.inputbar.AlgebraInputD;
import org.geogebra.desktop.gui.inputbar.InputBarHelpPanelD;
import org.geogebra.desktop.gui.layout.DockPanelD;
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
import org.geogebra.desktop.gui.view.consprotocol.ConstructionProtocolNavigationD;
import org.geogebra.desktop.gui.view.consprotocol.ConstructionProtocolViewD;
import org.geogebra.desktop.gui.view.consprotocol.ConstructionProtocolViewD.ConstructionTableDataD;
import org.geogebra.desktop.gui.view.data.DataAnalysisViewD;
import org.geogebra.desktop.gui.view.probcalculator.ProbabilityCalculatorViewD;
import org.geogebra.desktop.gui.view.properties.PropertiesViewD;
import org.geogebra.desktop.gui.view.spreadsheet.SpreadsheetViewD;
import org.geogebra.desktop.gui.virtualkeyboard.VirtualKeyboardD;
import org.geogebra.desktop.gui.virtualkeyboard.WindowsUnicodeKeyboard;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.GeoGebraPreferencesD;
import org.geogebra.desktop.main.GuiManagerInterfaceD;
import org.geogebra.desktop.main.KeyboardSettings;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.util.GuiResourcesD;
import org.geogebra.desktop.util.UtilD;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Handles all geogebra.gui package related objects and methods for Application.
 * This is done to be able to put class files of geogebra.gui.* packages into a
 * separate gui jar file.
 */
@SuppressWarnings("javadoc")
public class GuiManagerD extends GuiManager implements GuiManagerInterfaceD {

	private final static boolean USE_COMPRESSED_VIEW = true;
	private final static int CV_UPDATES_PER_SECOND = 3;

	protected DialogManagerD dialogManager;
	protected DialogManagerD.Factory dialogManagerFactory;

	private AlgebraInputD algebraInput;
	private AlgebraControllerD algebraController;
	private AlgebraViewD algebraView;
	private CASViewD casView;
	private SpreadsheetViewD spreadsheetView;
	private ArrayList<EuclidianViewD> euclidianView2 = new ArrayList<>();
	private ConstructionProtocolViewD constructionProtocolView;
	private GeoGebraMenuBar menuBar;
	private String strCustomToolbarDefinition;

	private ToolbarContainer toolbarPanel;

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

	private static DataFlavor getFlavor(String desc) {
		try {
			return new DataFlavor(desc);
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
		return null;
	}

	public static final DataFlavor urlFlavor = getFlavor(
			"application/x-java-url; class=java.net.URL");
	public static final DataFlavor uriListFlavor = getFlavor(
			"text/uri-list; class=java.lang.String");

	// Actions
	private AbstractAction showAxesAction, showGridAction, undoAction,
			redoAction;
	private LocalizationD loc;

	public GuiManagerD(AppD app) {
		super(app);
		this.loc = app.getLocalization();

		lastFilenameOfSaveDialog = null;
		dialogManagerFactory = new DialogManagerD.Factory();
	}

	/**
	 * Initialize the GUI manager.
	 */
	@Override
	public void initialize() {
		initAlgebraController(); // needed for keyboard input in EuclidianView

		// init layout related stuff
		layout.initialize(getApp());
		initLayoutPanels();

		// init dialog manager
		dialogManager = dialogManagerFactory.create(getApp());
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
		Dimension oldCenterSize = getApp().getCenterPanel().getSize();
		Dimension newCenterSize;

		// TODO redo this, guessing dimensions is bad
		if (getApp().getFrame().getPreferredSize().width <= 0) {
			newCenterSize = new Dimension(700, 500);
		} else {
			newCenterSize = getApp().getFrame().getPreferredSize();
			newCenterSize.width -= 10;
			newCenterSize.height -= 100;
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
		layout.registerPanel(new SpreadsheetDockPanel(getApp()));

		// register algebra view
		layout.registerPanel(new AlgebraDockPanel(getApp()));

		// register CAS view
		if (getApp().supportsView(App.VIEW_CAS)) {
			layout.registerPanel(new CasDockPanel(getApp()));
		}

		// register EuclidianView2
		layout.registerPanel(newEuclidian2DockPanel(1));

		// register ConstructionProtocol view
		layout.registerPanel(new ConstructionProtocolDockPanel(getApp()));

		// register ProbabilityCalculator view
		layout.registerPanel(new ProbabilityCalculatorDockPanel(getApp()));

		// register Properties view
		propertiesDockPanel = new PropertiesDockPanel(getApp());
		layout.registerPanel(propertiesDockPanel);

		// register data analysis view
		layout.registerPanel(new DataAnalysisViewDockPanel(getApp()));

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
		return new EuclidianDockPanel(getApp(), null);
	}

	protected Euclidian2DockPanel newEuclidian2DockPanel(int idx) {
		return new Euclidian2DockPanel(getApp(), null, idx);
	}

	public void clearPreferences() {
		if ((getApp()).isSaved() || getApp().saveCurrentFile()) {
			getApp().setWaitCursor();
			GeoGebraPreferencesD.getPref().clearPreferences(getApp());

			// clear custom toolbar definition
			strCustomToolbarDefinition = null;

			GeoGebraPreferencesD.getPref().loadXMLPreferences(getApp()); // this
																			// will
			// load the
			// default
			// settings
			getApp().setLanguage(getApp().getMainComponent().getLocale());
			getApp().updateContentPaneAndSize();
			getApp().setDefaultCursor();
			getApp().setUndoActive(true);
		}
	}

	@Override
	public synchronized CASViewD getCasView() {
		if (casView == null) {
			casView = new CASViewD(getApp());
		}

		return casView;
	}

	@Override
	public boolean hasCasView() {
		return casView != null;
	}

	@Override
	public AlgebraViewD getAlgebraView() {
		if (algebraView == null) {
			initAlgebraController();
			algebraView = newAlgebraView(algebraController);
			if (!getApp().isApplet()) {
				// allow drag & drop of files on algebraView
				algebraView.setDropTarget(new DropTarget(algebraView,
						new FileDropTargetListener(getApp())));
			}
		}

		return algebraView;
	}

	@Override
	public void applyAlgebraViewSettings() {
		if (algebraView != null) {
			algebraView.applySettings();
		}
	}

	private PropertiesViewD propertiesView;

	@Override
	public View getPropertiesView() {

		if (propertiesView == null) {
			// initPropertiesDialog();
			propertiesView = newPropertiesViewD(getApp());
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

	@Override
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

	@Override
	public ConstructionProtocolView getConstructionProtocolView() {
		if (constructionProtocolView == null) {
			constructionProtocolView = new ConstructionProtocolViewD(
					getApp());
		}

		return constructionProtocolView;
	}

	@Override
	public void startEditing(GeoElement geo) {
		getAlgebraView().startEditItem(geo);
	}

	@Override
	public void setScrollToShow(boolean scrollToShow) {
		if (spreadsheetView != null) {
			spreadsheetView.setScrollToShow(scrollToShow);
		}
	}

	@Override
	public void resetSpreadsheet() {
		if (spreadsheetView != null) {
			spreadsheetView.restart();
		}
	}

	@Override
	public boolean hasSpreadsheetView() {
		if (spreadsheetView == null) {
			return false;
		}
		if (!spreadsheetView.isShowing()) {
			return false;
		}
		return true;
	}

	@Override
	public boolean hasAlgebraViewShowing() {
		if (algebraView == null) {
			return false;
		}
		if (!algebraView.isShowing()) {
			return false;
		}
		return true;
	}

	@Override
	public boolean hasAlgebraView() {
		if (algebraView == null) {
			return false;
		}
		return true;
	}

	@Override
	public boolean hasProbabilityCalculator() {
		if (probCalculator == null) {
			return false;
		}
		if (!probCalculator.isShowing()) {
			return false;
		}
		return true;
	}

	@Override
	public ProbabilityCalculatorViewD getProbabilityCalculator() {

		if (probCalculator == null) {
			setProbCalculator(new ProbabilityCalculatorViewD(getApp()));
		}
		return (ProbabilityCalculatorViewD) probCalculator;
	}

	@Override
	public boolean hasDataAnalysisView() {
		if (dataView == null) {
			return false;
		}
		if (!dataView.isShowing()) {
			return false;
		}
		return true;
	}

	@Override
	public DataAnalysisViewD getDataAnalysisView() {
		if (dataView == null) {
			dataView = new DataAnalysisViewD(getApp(),
					getApp().getSettings().getDataAnalysis().getMode());
		}
		return dataView;
	}

	@Override
	public SpreadsheetViewD getSpreadsheetView() {
		// init spreadsheet view
		if (spreadsheetView == null) {
			spreadsheetView = new SpreadsheetViewD(getApp());
		}

		return spreadsheetView;
	}

	@Override
	public void updateSpreadsheetColumnWidths() {
		if (spreadsheetView != null) {
			spreadsheetView.updateColumnWidths();
		}
	}

	// XML
	// =====================================================

	@Override
	public void getSpreadsheetViewXML(StringBuilder sb, boolean asPreference) {
		getApp().getSettings().getSpreadsheet().getXML(sb, asPreference);
	}

	@Override
	public void getAlgebraViewXML(StringBuilder sb, boolean asPreference) {
		if (algebraView != null) {
			algebraView.getXML(sb, asPreference);
		}
	}

	// public void getAlgebraViewXML(StringBuilder sb) {
	// if (algebraView != null)
	// algebraView.getXML(sb);
	// }

	// ==================================
	// End XML

	@Override
	public EuclidianViewD getEuclidianView2(int idx) {
		for (int i = euclidianView2.size(); i <= idx; i++) {
			euclidianView2.add(null);
		}
		if (euclidianView2.get(idx) == null) {
			boolean[] showAxis = { true, true };
			boolean showGrid = false;
			Log.debug("Creating 2nd Euclidian View");
			EuclidianViewD ev = newEuclidianView(showAxis, showGrid, 2);
			// euclidianView2.setEuclidianViewNo(2);
			ev.updateFonts();
			euclidianView2.set(idx, ev);
		}
		return euclidianView2.get(idx);
	}

	protected EuclidianViewD newEuclidianView(boolean[] showAxis,
			boolean showGrid, int id) {
		return new EuclidianViewD(new EuclidianControllerD(kernel), showAxis,
				showGrid, id, getApp().getSettings().getEuclidian(id));
	}

	@Override
	public boolean hasEuclidianView2(int idx) {
		if (euclidianView2.size() <= idx || euclidianView2.get(idx) == null) {
			return false;
		}
		if (!euclidianView2.get(idx).isShowing()) {
			return false;
		}
		return true;
	}

	@Override
	public boolean hasEuclidianView2EitherShowingOrNot(int idx) {
		if (euclidianView2.size() <= idx || euclidianView2.get(idx) == null) {
			return false;
		}
		return true;
	}

	/**
	 * TODO: Do not just use the default euclidian view if no EV has focus, but
	 *       determine if maybe just one EV is visible etc.
	 * 
	 * @return The euclidian view to which new geo elements should be added by
	 *         default (if the user uses this mode). This is the focused
	 *         euclidian view or the first euclidian view at the moment.
	 */
	@Override
	public EuclidianView getActiveEuclidianView() {

		if (layout != null && layout.getDockManager() != null) {
			EuclidianDockPanelAbstract focusedEuclidianPanel = layout
					.getDockManager().getFocusedEuclidianPanel();

			if (focusedEuclidianPanel != null) {
				return focusedEuclidianPanel.getEuclidianView();
			}
		}
		return getApp().getEuclidianView1();
	}

	@Override
	public void attachSpreadsheetView() {
		getSpreadsheetView();
		spreadsheetView.attachView();
	}

	@Override
	public void detachSpreadsheetView() {
		if (spreadsheetView != null) {
			spreadsheetView.detachView();
		}
	}

	@Override
	public void attachAlgebraView() {
		getAlgebraView();
		algebraView.attachView();
	}

	@Override
	public void detachAlgebraView() {
		if (algebraView != null) {
			algebraView.detachView();
		}
	}

	@Override
	public void attachCasView() {
		getCasView();
		casView.attachView();
	}

	@Override
	public void detachCasView() {
		if (casView != null) {
			casView.detachView();
		}
	}

	@Override
	public void attachConstructionProtocolView() {
		getConstructionProtocolView();
		constructionProtocolView.getData().attachView();
	}

	@Override
	public void detachConstructionProtocolView() {
		if (constructionProtocolView != null) {
			((ConstructionTableDataD) (constructionProtocolView.getData()))
					.detachView();
		}
	}

	@Override
	public void attachProbabilityCalculatorView() {
		getProbabilityCalculator();
		probCalculator.attachView();
	}

	@Override
	public void detachProbabilityCalculatorView() {
		getProbabilityCalculator();
		probCalculator.detachView();
	}

	@Override
	public void attachDataAnalysisView() {
		getDataAnalysisView().attachView();
	}

	@Override
	public void detachDataAnalysisView() {
		getDataAnalysisView().detachView();
	}

	@Override
	public void attachPropertiesView() {
		getPropertiesView();
		propertiesView.attachView();
	}

	@Override
	public void detachPropertiesView() {
		if (propertiesView != null) {
			propertiesView.detachView();
		}
	}

	@Override
	public void setShowAuxiliaryObjects(boolean flag) {
		if (!hasAlgebraViewShowing()) {
			return;
		}
		getAlgebraView();
		algebraView.setShowAuxiliaryObjects(flag);
		getApp().getSettings().getAlgebra().setShowAuxiliaryObjects(flag);
	}

	private void initAlgebraController() {
		if (algebraController == null) {
			algebraController = new AlgebraControllerD(getApp().getKernel());
		}
	}

	public JComponent getAlgebraInput() {
		if (algebraInput == null) {
			algebraInput = new AlgebraInputD(getApp());
		}

		return algebraInput;
	}

	/**
	 * use Application.getDialogManager() instead
	 */
	@Override
	@Deprecated
	public DialogManager getDialogManager() {
		return dialogManager;
	}

	@Override
	public void setLayout(Layout layout) {
		this.layout = (LayoutD) layout;
	}

	@Override
	public LayoutD getLayout() {
		return layout;
	}

	public Container getToolbarPanelContainer() {

		return getToolbarPanel();
	}

	public ToolbarContainer getToolbarPanel() {
		if (toolbarPanel == null) {
			toolbarPanel = new ToolbarContainer(getApp(), true);
		}

		return toolbarPanel;
	}

	@Override
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

	@Override
	public void setShowView(boolean flag, int viewId) {
		setShowView(flag, viewId, true);
	}

	@Override
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
				(getApp()).getActiveEuclidianView().requestFocus();
			}
		}

		toolbarPanel.validate();
		toolbarPanel.updateHelpText();
		getApp().dispatchEvent(new Event(EventType.PERSPECTIVE_CHANGE));
	}

	@Override
	public boolean showView(int viewId) {
		try {
			if (layout.getDockManager().getPanel(viewId) == null) {
				return false;
			}
			return layout.getDockManager().getPanel(viewId).isVisible();
		} catch (RuntimeException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
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
		getApp().getActiveEuclidianView().resetMode();
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
		if (constructionProtocolView != null) {
			constructionProtocolView.setConstructionStep(step);
		}
	}

	@Override
	public void updateConstructionProtocol() {
		if (constructionProtocolView != null) {
			constructionProtocolView.update();
		}
	}

	@Override
	public boolean isUsingConstructionProtocol() {
		return constructionProtocolView != null;
	}

	public int getToolBarHeight() {
		if ((getApp()).showToolBar() && toolbarPanel != null) {
			return toolbarPanel.getHeight();
		}
		return 0;
	}

	public String getDefaultToolbarString() {
		if (toolbarPanel == null) {
			return "";
		}

		return getGeneralToolbar().getDefaultToolbarString();
	}

	@Override
	public void updateFonts() {
		if (algebraView != null) {
			algebraView.updateFonts();
		}
		if (spreadsheetView != null) {
			spreadsheetView.updateFonts();
		}
		if (algebraInput != null) {
			algebraInput.updateFonts();
		}

		if (toolbarPanel != null) {
			toolbarPanel.buildGui();
		}

		if (menuBar != null) {
			menuBar.updateFonts();
		}

		if (constructionProtocolView != null) {
			constructionProtocolView.initGUI();
		}
		if (getCPNavigationIfExists() != null) {
			((ConstructionProtocolNavigationD) getConstructionProtocolNavigation())
					.initGUI();
		}

		if (casView != null) {
			casView.updateFonts();
		}

		if (layout.getDockManager() != null) {
			layout.getDockManager().updateFonts();
		}

		if (probCalculator != null) {
			((ProbabilityCalculatorViewD) probCalculator).updateFonts();
		}

		if (dataView != null) {
			dataView.updateFonts();
		}

		if (propertiesView != null) {
			propertiesView.updateFonts();
		}

		dialogManager.updateFonts();

		SwingUtilities.updateComponentTreeUI(getApp().getMainComponent());
	}

	@Override
	public void setLabels() {
		// reinit actions to update labels
		showAxesAction = null;
		initActions();

		if ((getApp()).showMenuBar()) {
			initMenubar();
			// updateMenubar();

			Component comp = getApp().getMainComponent();
			if (comp instanceof JApplet) {
				((JApplet) comp).setJMenuBar(menuBar);
			} else if (comp instanceof JFrame) {
				((JFrame) comp).setJMenuBar(menuBar);
			}
		}

		if (inputHelpPanel != null) {
			inputHelpPanel.setLabels();
		}
		// update views
		if (algebraView != null) {
			algebraView.setLabels();
		}
		if (algebraInput != null) {
			algebraInput.setLabels();
		}

		if (toolbarPanel != null) {
			toolbarPanel.buildGui();
			toolbarPanel.updateHelpText();
		}

		if (constructionProtocolView != null) {
			constructionProtocolView.initGUI();
		}

		getConstructionProtocolNavigation().setLabels();

		if (virtualKeyboard != null) {
			virtualKeyboard.setLabels();
		}

		layout.getDockManager().setLabels();

		dialogManager.setLabels();

		if (getApp().getDockBar() != null) {
			getApp().getDockBar().setLabels();
		}

	}

	@Override
	public void initMenubar() {
		if (menuBar == null) {
			menuBar = new GeoGebraMenuBar(getApp(), layout);
		}
		// ((GeoGebraMenuBar) menuBar).setFont(app.getPlainFont());
		menuBar.initMenubar();
	}

	@Override
	public void updateMenubar() {
		if (menuBar != null) {
			menuBar.updateMenubar();
		}
	}

	@Override
	public void updateMenubarSelection() {
		if (menuBar != null) {
			menuBar.updateSelection();
		}
	}

	@Override
	public void updateMenuWindow() {
		if (menuBar != null) {
			menuBar.updateMenuWindow();
		}
	}

	@Override
	public void updateMenuFile() {
		if (menuBar != null) {
			menuBar.updateMenuFile();
		}
	}

	@Override
	public JMenuBar getMenuBar() {
		return menuBar;
	}

	@Override
	public void updateMenuBarLayout() {
		if ((getApp()).showMenuBar()) {
			Component comp = getApp().getMainComponent();
			if (comp instanceof JApplet) {
				((JApplet) comp).setJMenuBar(menuBar);
			} else if (comp instanceof JFrame) {
				((JFrame) comp).setJMenuBar(menuBar);
				((JFrame) comp).validate();
			}
		} else {
			Component comp = getApp().getMainComponent();
			if (comp instanceof JApplet) {
				((JApplet) comp).setJMenuBar(null);
			} else if (comp instanceof JFrame) {
				((JFrame) comp).setJMenuBar(null);
				((JFrame) comp).validate();
			}
		}
	}

	public void showAboutDialog() {
		GeoGebraMenuBar.showAboutDialog(getApp());
	}

	public void showPrintPreview() {
		GeoGebraMenuBar.showPrintPreview(getApp());
	}

	ContextMenuGraphicsWindowD drawingPadpopupMenu;

	/**
	 * Displays the Graphics View menu at the position p in the coordinate space
	 * of euclidianView
	 */
	public void showDrawingPadPopup(Component invoker, GPoint p) {
		// clear highlighting and selections in views
		getApp().getActiveEuclidianView().resetMode();

		// menu for drawing pane context menu
		drawingPadpopupMenu = new ContextMenuGraphicsWindowD(getApp(), p.x,
				p.y);
		drawingPadpopupMenu.getWrappedPopup().show(invoker, p.x, p.y);
	}

	/**
	 * Toggles the Graphics View menu at the position p in the coordinate space
	 * of euclidianView
	 */
	public void toggleDrawingPadPopup(Component invoker, Point p) {
		GPoint loc1 = new GPoint(p.x, p.y);
		if (drawingPadpopupMenu == null
				|| !drawingPadpopupMenu.getWrappedPopup().isVisible()) {
			showDrawingPadPopup(invoker, loc1);
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
			GPoint p) {

		if (geos == null || geos.size() == 0 || !getApp().letShowPopupMenu()) {
			return;
		}
		if (getApp().getKernel().isAxis(geos.get(0))) {
			showDrawingPadPopup(invoker, p);
		} else {
			// clear highlighting and selections in views
			getApp().getActiveEuclidianView().resetMode();

			Point screenPos = (invoker == null) ? new Point(0, 0)
					: invoker.getLocationOnScreen();
			screenPos.translate(p.x, p.y);

			popupMenu = new ContextMenuGeoElementD(getApp(), geos, screenPos);
			popupMenu.getWrappedPopup().show(invoker, p.x, p.y);
		}

	}

	/**
	 * Displays the popup menu for geo at the position p in the coordinate space
	 * of the component invoker
	 */
	public void showPopupChooseGeo(ArrayList<GeoElement> selectedGeos,
			ArrayList<GeoElement> geos, EuclidianView view, GPoint p) {

		if (geos == null || !getApp().letShowPopupMenu()) {
			return;
		}

		Component invoker = ((EuclidianViewInterfaceD) view).getJPanel();

		if (!geos.isEmpty() && getApp().getKernel().isAxis(geos.get(0))) {
			showDrawingPadPopup(invoker, p);
		} else {
			// clear highlighting and selections in views
			getApp().getActiveEuclidianView().resetMode();

			Point screenPos = (invoker == null) ? new Point(0, 0)
					: invoker.getLocationOnScreen();
			screenPos.translate(p.x, p.y);

			popupMenu = new ContextMenuChooseGeoD(getApp(), view,
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
		GPoint loc1 = new GPoint(p.x, p.y);
		if (popupMenu == null || !popupMenu.getWrappedPopup().isVisible()) {
			showPopupMenu(geos, invoker, loc1);
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
		getApp().setWaitCursor();

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

			EuclidianView ev = getApp().getActiveEuclidianView();
			Construction cons = ev.getApplication().getKernel()
					.getConstruction();
			// Point mousePos = ((EuclidianViewInterfaceDesktop) ev)
			// .getMousePosition();

			// create GeoImage object(s) for this fileName
			GeoImage geoImage = null;

			GeoPoint loc1 = new GeoPoint(cons);
			GeoPoint loc2 = new GeoPoint(cons);

			for (int i = 0; i < fileName.length; i++) {
				// create corner points (bottom right/left)
				loc1 = new GeoPoint(cons);
				loc2 = new GeoPoint(cons);

				loc1.setCoords(ev.getXmin() + (ev.getXmax() - ev.getXmin()) / 4,
						ev.getYmin() + (ev.getYmax() - ev.getYmin()) / 4, 1.0);
				loc1.setLabel(null);
				loc1.setLabelVisible(false);
				loc1.update();

				loc2.setCoords(ev.getXmax() - (ev.getXmax() - ev.getXmin()) / 4,
						ev.getYmin() + (ev.getYmax() - ev.getYmin()) / 4, 1.0);
				loc2.setLabel(null);
				loc2.setLabelVisible(false);
				loc2.update();

				geoImage = new GeoImage(getApp().getKernel().getConstruction());
				geoImage.setImageFileName(fileName[i]);
				geoImage.setCorner(loc1, 0);
				geoImage.setCorner(loc2, 1);
				geoImage.setLabel(null);

				GeoImage.updateInstances(getApp());
			}
			// make sure only the last image will be selected
			GeoElement[] geos = { geoImage, loc1, loc2 };
			getApp().getActiveEuclidianView().getEuclidianController()
					.clearSelections();
			getApp().getActiveEuclidianView().getEuclidianController()
					.memorizeJustCreatedGeos(geos);
			ret = true;
		}

		getApp().setDefaultCursor();
		return ret;
	}

	public Color showColorChooser(GColor currentColor) {

		try {
			GeoGebraColorChooser chooser = new GeoGebraColorChooser(getApp());
			chooser.setColor(GColorD.getAwtColor(currentColor));
			JDialog dialog = JColorChooser.createDialog(
					getApp().getMainComponent(),
					getApp().getLocalization().getMenu("ChooseColor"), true, chooser,
					null, null);
			dialog.setVisible(true);

			return chooser.getColor();

		} catch (RuntimeException e) {
			return null;
		}
	}

	/**
	 * gets String from clipboard
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
			reader = data instanceof InputStreamReader
					? (InputStreamReader) data
					: new InputStreamReader((InputStream) data, "UNICODE");

			while (true) {
				numChars = reader.read(readBuf);
				if (numChars == -1) {
					break;
				}
				sbuf.append(readBuf, 0, numChars);
			}

			selection = new String(sbuf);
			reader.close();
		} catch (RuntimeException e) {
			// e.printStackTrace();
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
	 * @param transfer0
	 * @return fileName of image stored in imageManager
	 */
	@SuppressWarnings("unchecked")
	public String[] getImageFromTransferable(Transferable transfer0) {
		Transferable transfer = transfer0;
		BufferedImage img = null;
		String fileName = null;
		ArrayList<String> nameList = new ArrayList<>();
		boolean imageFound = false;

		getApp().setWaitCursor();

		// if transfer is null then get it from the clipboard
		if (transfer == null) {
			try {
				Clipboard clip = Toolkit.getDefaultToolkit()
						.getSystemClipboard();
				transfer = clip.getContents(null);
				fileName = "clipboard.png"; // extension determines what format
				// it will be in ggb file

			} catch (Exception e) {
				getApp().setDefaultCursor();
				e.printStackTrace();
				getApp().showError(Errors.PasteImageFailed);
				return null;
			}
		}

		// load image from transfer
		try {

			transfer.getTransferDataFlavors();

			DataFlavor htmlFlavor = new DataFlavor(
					"text/html; document=all; class=java.lang.String; charset=Unicode");

			// PNG image copied in html format
			// eg http://jsfiddle.net/bvFNL/8/
			if (transfer.isDataFlavorSupported(htmlFlavor)) {
				String html = (String) transfer.getTransferData(htmlFlavor);

				int pngBase64index = html.indexOf(StringUtil.pngMarker);

				if (pngBase64index > -1) {
					int pngBase64end = html.indexOf("\"", pngBase64index);
					String base64 = html.substring(
							pngBase64index + StringUtil.pngMarker.length(),
							pngBase64end);
					byte[] bytes = Base64.decode(base64);

					InputStream in = new ByteArrayInputStream(bytes);
					img = ImageIO.read(in);
					fileName = "transferHTMLImage.png";
					nameList.add(getApp().createImage(new MyImageD(img),
							fileName));
					imageFound = true;
				}

			}

			if (!imageFound
					&& transfer.isDataFlavorSupported(DataFlavor.imageFlavor)) {
				img = (BufferedImage) transfer
						.getTransferData(DataFlavor.imageFlavor);
				if (img != null) {
					fileName = "transferImage.png";
					nameList.add(getApp().createImage(new MyImageD(img),
							fileName));
					imageFound = true;
				}
				// System.out.println(nameList.toString());

			}

			if (!imageFound && transfer
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
					MyImageD imgD = MyImageD.fromFile(f, fileName);
					if (imgD != null) {
						nameList.add(getApp().createImage(imgD, fileName));
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
						nameList.add(getApp().createImage(new MyImageD(img),
								fileName));
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
						nameList.add(getApp().createImage(new MyImageD(img),
								fileName));
						imageFound = true;
					}
				}
				// System.out.println(nameList.toString());

			}

		} catch (UnsupportedFlavorException ufe) {
			getApp().setDefaultCursor();
			ufe.printStackTrace();
			return null;

		} catch (IOException ioe) {
			getApp().setDefaultCursor();
			ioe.printStackTrace();
			return null;

		} catch (Exception e) {
			getApp().setDefaultCursor();
			e.printStackTrace();
			return null;
		}

		getApp().setDefaultCursor();
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

	public Localization getLocalization() {
		return getApp().getLocalization();
	}

	/**
	 * Loads and stores an image file is in this application's imageManager. If
	 * a null image file is passed, then a file dialog is opened to choose a
	 * file.
	 * 
	 * @return fileName of image stored in imageManager
	 */
	public String getImageFromFile(File imageFile0) {
		File imageFile = imageFile0;
		MyImageD img = new MyImageD();
		String fileName = null;
		try {
			getApp().setWaitCursor();
			// else
			{
				if (imageFile == null) {

					/**************************************************************
					 * Mac OS X related code to work around JFileChooser problem
					 * on sandboxing. See
					 * http://intransitione.com/blog/take-java-to-app-store/
					 **************************************************************/
					if (getApp().macsandbox) {

						FileDialog fd = new FileDialog(getApp().getFrame());
						fd.setModal(true);
						File currentPath = getApp().getCurrentPath();
						fd.setMode(FileDialog.LOAD);
						if (currentPath != null) {
							fd.setDirectory(currentPath.toString());
						}
						fd.setFilenameFilter(new FilenameFilter() {
							@Override
							public boolean accept(File dir, String name) {
								return (name.endsWith(".jpg")
										|| name.endsWith(".jpeg")
										|| name.endsWith(".png")
										|| name.endsWith(".bmp")
										|| name.endsWith(".gif"));
							}
						});
						fd.setTitle(loc.getMenu("Load"));

						fd.toFront();
						fd.setVisible(true);
						// FIXME: find a better place for this, we need to
						// change the
						// cursor back before NPE when file loading was
						// unsuccessful:
						getApp().setDefaultCursor();

						if (fd.getFile() != null) {
							imageFile = new File(
									fd.getDirectory() + "/" + fd.getFile());
						}

						getApp()
								.setCurrentPath(new File(fd.getDirectory()));

					} else {
						/**************************************************************
						 * End of Mac OS X related code.
						 **************************************************************/

						((DialogManagerD) getDialogManager()).initFileChooser();
						GeoGebraFileChooser fileChooser = ((DialogManagerD) getDialogManager())
								.getFileChooser();

						fileChooser.setMode(GeoGebraFileChooser.MODE_IMAGES);
						fileChooser.setCurrentDirectory(
								getApp().getCurrentImagePath());

						MyFileFilter fileFilter = new MyFileFilter();
						fileFilter.addExtension(FileExtensions.JPG);
						fileFilter.addExtension(FileExtensions.JPEG);
						fileFilter.addExtension(FileExtensions.PNG);
						fileFilter.addExtension(FileExtensions.GIF);
						fileFilter.addExtension(FileExtensions.BMP);
						fileFilter.addExtension(FileExtensions.SVG);
						fileFilter.setDescription(
								getLocalization().getMenu("Image"));
						fileChooser.resetChoosableFileFilters();
						fileChooser.setFileFilter(fileFilter);

						int returnVal = fileChooser.showOpenDialog(
								getApp().getMainComponent());
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							imageFile = fileChooser.getSelectedFile();
							if (imageFile != null) {
								getApp().setCurrentImagePath(
										imageFile.getParentFile());
								if (!getApp().isApplet()) {
									GeoGebraPreferencesD.getPref()
											.saveDefaultImagePath(getApp()
													.getCurrentImagePath());
								}
							}
						}

					}
				}

				if (imageFile == null) {
					getApp().setDefaultCursor();
					return null;
				}

				// get file name
				fileName = imageFile.getCanonicalPath();

				// load image
				img.load(imageFile);
			}

			return getApp().createImage(img, fileName);

		} catch (Exception e) {
			getApp().setDefaultCursor();
			e.printStackTrace();
			getApp().showError(Errors.LoadFileFailed);
			return null;
		}

	}

	/**
	 * Opens file chooser and returns a data file for the spreadsheet G.Sturr
	 * 2010-2-5
	 */
	@Override
	public File getDataFile() {

		// TODO -- create MODE_DATA that shows preview of text file (or no
		// preview?)

		File dataFile = null;

		try {
			getApp().setWaitCursor();

			/**************************************************************
			 * Mac OS X related code to work around JFileChooser problem on
			 * sandboxing. See
			 * http://intransitione.com/blog/take-java-to-app-store/
			 **************************************************************/
			if (getApp().macsandbox) {

				FileDialog fd = new FileDialog(getApp().getFrame());
				fd.setModal(true);
				File currentPath = getApp().getCurrentPath();
				fd.setMode(FileDialog.LOAD);
				if (currentPath != null) {
					fd.setDirectory(currentPath.toString());
				}
				fd.setFilenameFilter(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return (name.endsWith(".txt") || name.endsWith(".csv")
								|| name.endsWith(".dat"));
					}
				});

				fd.setTitle(loc.getMenu("Load"));

				fd.toFront();
				fd.setVisible(true);
				// FIXME: find a better place for this, we need to change the
				// cursor back before NPE when file loading was unsuccessful:
				getApp().setDefaultCursor();

				if (fd.getFile() != null) {
					dataFile = new File(fd.getDirectory() + "/" + fd.getFile());
				}

				getApp().setCurrentPath(new File(fd.getDirectory()));

				return dataFile;
			}
			/**************************************************************
			 * End of Mac OS X related code.
			 **************************************************************/

			((DialogManagerD) getDialogManager()).initFileChooser();
			GeoGebraFileChooser fileChooser = ((DialogManagerD) getDialogManager())
					.getFileChooser();

			fileChooser.setMode(GeoGebraFileChooser.MODE_DATA);
			fileChooser.setCurrentDirectory(getApp().getCurrentImagePath());

			MyFileFilter fileFilter = new MyFileFilter();
			fileFilter.addExtension(FileExtensions.TXT);
			fileFilter.addExtension(FileExtensions.CSV);
			fileFilter.addExtension(FileExtensions.DAT);

			// fileFilter.setDescription(app.getPlain("Image"));
			fileChooser.resetChoosableFileFilters();
			fileChooser.setFileFilter(fileFilter);

			int returnVal = fileChooser
					.showOpenDialog(getApp().getMainComponent());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				dataFile = fileChooser.getSelectedFile();
				if (dataFile != null) {
					getApp().setCurrentImagePath(dataFile.getParentFile());
					if (!getApp().isApplet()) {
						GeoGebraPreferencesD.getPref().saveDefaultImagePath(
								getApp().getCurrentImagePath());
					}
				}
			}

		} catch (Exception e) {
			getApp().setDefaultCursor();
			e.printStackTrace();
			getApp().showError(Errors.LoadFileFailed);
			return null;
		}

		getApp().setDefaultCursor();
		return dataFile;

	}

	// returns true for YES or NO and false for CANCEL
	@Override
	public boolean saveCurrentFile() {

		getApp().getEuclidianView1().reset();
		if (getApp().hasEuclidianView2(1)) {
			getApp().getEuclidianView2(1).reset();
		}
		// use null component for iconified frame
		Component comp = getApp().getMainComponent();
		if (getApp().getFrame() instanceof GeoGebraFrame) {
			GeoGebraFrame frame = (GeoGebraFrame) getApp().getFrame();
			comp = frame != null && !frame.isIconified() ? frame : null;
		}

		Object[] options = { loc.getMenu("Save"), loc.getMenu("DontSave"),
				loc.getMenu("Cancel") };
		int returnVal = JOptionPane.showOptionDialog(comp,
				loc.getMenu("DoYouWantToSaveYourChanges"),
				loc.getMenu("CloseFile"), JOptionPane.DEFAULT_OPTION,
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

	@Override
	public boolean save() {
		// app.getFrame().getJMenuBar()
		getApp().setWaitCursor();

		// close properties dialog if open
		getDialogManager().closeAll();

		boolean success = false;
		if (getApp().getCurrentFile() != null) {
			// Mathieu Blossier - 2008-01-04
			// if the file is read-only, open save as
			if (!getApp().getCurrentFile().canWrite()) {
				success = saveAs();
			} else {
				success = getApp().saveGeoGebraFile(getApp().getCurrentFile());
			}
		} else {
			success = saveAs();
		}

		getApp().setDefaultCursor();
		return success;
	}

	@Override
	public boolean saveAs() {

		// Mathieu Blossier - 2008-01-04
		// if the file is hidden, set current file to null
		if (getApp().getCurrentFile() != null) {
			if (!getApp().getCurrentFile().canWrite()
					&& getApp().getCurrentFile().isHidden()) {
				getApp().resetCurrentFile();
				getApp().setCurrentPath(null);
			}
		}

		FileExtensions[] fileExtensions;
		String[] fileDescriptions;
		fileExtensions = new FileExtensions[] { FileExtensions.GEOGEBRA };
		fileDescriptions = new String[] { GeoGebraConstants.APPLICATION_NAME
				+ " " + loc.getMenu("Files") };
		getApp().needThumbnailFor3D();
		File file = showSaveDialog(fileExtensions,
				getApp().getCurrentFile(), fileDescriptions, true, false);
		if (file == null) {
			return false;
		}

		boolean success = getApp().saveGeoGebraFile(file);
		if (success) {
			getApp().setCurrentFile(file);
		}
		return success;
	}

	@Override
	public File showSaveDialog(FileExtensions fileExtension, File selectedFile0,
			String fileDescription, boolean promptOverwrite, boolean dirsOnly) {
		File selectedFile = selectedFile0;
		if (selectedFile == null) {
			selectedFile = removeExtension(getApp().getCurrentFile());
		}

		FileExtensions[] fileExtensions = { fileExtension };
		String[] fileDescriptions = { fileDescription };
		return showSaveDialog(fileExtensions, selectedFile, fileDescriptions,
				promptOverwrite, dirsOnly);
	}

	public File showSaveDialog(final FileExtensions[] fileExtensions,
			File selectedFile0, String[] fileDescriptions,
			boolean promptOverwrite, boolean dirsOnly) {
		boolean done = false;
		File selectedFile = selectedFile0;
		File file = null;

		if (fileExtensions == null || fileExtensions.length == 0
				|| fileDescriptions == null) {
			return null;
		}
		FileExtensions fileExtension = fileExtensions[0];

		/**************************************************************
		 * Mac OS X related code to work around JFileChooser problem on
		 * sandboxing. See http://intransitione.com/blog/take-java-to-app-store/
		 **************************************************************/
		if (getApp().macsandbox) {
			while (!done) {

				NSSavePanel panel = new NSSavePanel();
				String result = panel.saveDialog(loc.getMenu("Save"),
						fileExtension.toString());
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
				 * } } return false; } }); fd.setTitle(loc.getMenu("Save")); if
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
		fileChooser.setCurrentDirectory(getApp().getCurrentPath());

		if (dirsOnly) {
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		}

		// set selected file
		if (selectedFile != null) {
			fileExtension = StringUtil.getFileExtension(selectedFile.getName());
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
		} else {
			fileChooser.setSelectedFile(null);
		}
		fileChooser.resetChoosableFileFilters();
		MyFileFilter fileFilter;
		MyFileFilter mainFilter = null;
		for (int i = 0; i < fileExtensions.length; i++) {
			fileFilter = new MyFileFilter(fileExtensions[i]);
			if (fileDescriptions.length >= i && fileDescriptions[i] != null) {
				fileFilter.setDescription(fileDescriptions[i]);
			}
			fileChooser.addChoosableFileFilter(fileFilter);
			if (fileExtension.equals(fileExtensions[i])) {
				mainFilter = fileFilter;
			}
		}
		fileChooser.setFileFilter(mainFilter);

		while (!done) {
			// show save dialog
			int returnVal = fileChooser
					.showSaveDialog(getApp().getMainComponent());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = fileChooser.getSelectedFile();

				if (fileChooser.getFileFilter() instanceof MyFileFilter) {
					fileFilter = (MyFileFilter) fileChooser.getFileFilter();
					fileExtension = fileFilter.getExtension();
				} else {
					fileExtension = fileExtensions[0];
				}

				// remove all special characters from HTML filename
				if (fileExtension.equals(FileExtensions.HTML)) {
					file = removeExtension(file);
					file = new File(file.getParent(),
							UtilD.keepOnlyLettersAndDigits(file.getName()));
				}

				// remove "*<>/\?|:
				file = new File(file.getParent(),
						Util.processFilename(file.getName()));

				// add file extension
				file = addExtension(file, fileExtension);
				fileChooser.setSelectedFile(file);
				lastFilenameOfSaveDialog = file.getName();

				if (promptOverwrite && file.exists()) {
					// ask overwrite question

					Object[] options = { getLocalization().getMenu("Overwrite"),
							loc.getMenu("DontOverwrite") };
					int n = JOptionPane.showOptionDialog(
							getApp().getMainComponent(),
							getLocalization().getMenu("OverwriteFile") + "\n"
									+ file.getName(),
							getLocalization().getMenu("Question"),
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

	public static File addExtension(File file, FileExtensions fileExtension) {
		return addExtension(file, fileExtension.toString());
	}

	public static File addExtension(File file, String fileExtension) {
		if (file == null) {
			return null;
		}
		if (StringUtil.getFileExtensionStr(file.getName())
				.equals(fileExtension)) {
			return file;
		}
		return new File(file.getParentFile(), // path
				file.getName() + '.' + fileExtension); // filename
	}

	public static File removeExtension(File file) {
		if (file == null) {
			return null;
		}
		String fileName = file.getName();
		int dotPos = fileName.lastIndexOf('.');

		if (dotPos <= 0) {
			return file;
		}
		return new File(file.getParentFile(), // path
				fileName.substring(0, dotPos));
	}

	@Override
	public void openURL() {
		app.clearConstruction();
		InputDialogD id = new InputDialogOpenURL(getApp());
		id.setVisible(true);

	}

	@Override
	public void openFromGGT() {
		if ((getApp()).isSaved() || saveCurrentFile()) {
			((DialogManagerD) getApp().getDialogManager()).showOpenFromGGTDialog();
		}
	}

	@Override
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

		if ((getApp()).isSaved() || saveCurrentFile()) {
			getApp().setWaitCursor();

			/**************************************************************
			 * Mac OS X related code to work around JFileChooser problem on
			 * sandboxing. See
			 * http://intransitione.com/blog/take-java-to-app-store/
			 **************************************************************/
			if (getApp().macsandbox) {

				FileDialog fd = new FileDialog(getApp().getFrame());
				fd.setModal(true);
				File currentPath = null;
				if (file == null) {
					currentPath = getApp().getCurrentPath();
				} else {
					currentPath = file.getParentFile();
					fd.setFile(file.getName());
				}
				fd.setMode(FileDialog.LOAD);
				if (currentPath != null) {
					fd.setDirectory(currentPath.toString());
				}
				fd.setFilenameFilter(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {

						FileExtensions ext = StringUtil.getFileExtension(name);

						return ext.equals(FileExtensions.GEOGEBRA)
								|| ext.equals(FileExtensions.GEOGEBRA_TOOL)
								|| ext.equals(FileExtensions.HTML)
								|| ext.equals(FileExtensions.HTM)
								|| ext.equals(FileExtensions.OFF);

					}
				});
				fd.setTitle(loc.getMenu("Load"));

				fd.toFront();
				fd.setVisible(true);
				// FIXME: find a better place for this, we need to change the
				// cursor back before NPE when file loading was unsuccessful:
				getApp().setDefaultCursor();

				File[] files = new File[1];
				if (fd.getFile() != null) {
					files[0] = new File(fd.getDirectory() + "/" + fd.getFile());
				}

				getApp().setCurrentPath(new File(fd.getDirectory()));

				getApp().setDefaultCursor();
				doOpenFiles(files, true);
				return;
			}
			/**************************************************************
			 * End of Mac OS X related code.
			 **************************************************************/

			File oldCurrentFile = getApp().getCurrentFile();
			((DialogManagerD) getDialogManager()).initFileChooser();
			GeoGebraFileChooser fileChooser = ((DialogManagerD) getDialogManager())
					.getFileChooser();

			fileChooser.setMode(GeoGebraFileChooser.MODE_GEOGEBRA);
			fileChooser.setCurrentDirectory(getApp().getCurrentPath());
			fileChooser.setMultiSelectionEnabled(true);
			fileChooser.setSelectedFile(oldCurrentFile);

			// GeoGebra File Filter
			MyFileFilter fileFilter = new MyFileFilter();
			fileFilter.addExtension(FileExtensions.GEOGEBRA);
			fileFilter.addExtension(FileExtensions.GEOGEBRA_TOOL);
			fileFilter.addExtension(FileExtensions.HTML);
			fileFilter.addExtension(FileExtensions.HTM);
			fileFilter.setDescription(
					GeoGebraConstants.APPLICATION_NAME + loc.getMenu("Files"));
			fileChooser.resetChoosableFileFilters();
			fileChooser.addChoosableFileFilter(fileFilter);

			MyFileFilter insertFilter = new MyFileFilter();
			insertFilter.addExtension(FileExtensions.GEOGEBRA);
			insertFilter.setDescription(loc.getMenu("InsertFile"));
			fileChooser.addChoosableFileFilter(insertFilter);

			MyFileFilter templateFilter = new MyFileFilter();
			templateFilter.addExtension(FileExtensions.GEOGEBRA);
			templateFilter.setDescription(loc.getMenu("ApplyTemplate"));
			fileChooser.addChoosableFileFilter(templateFilter);

			MyFileFilter offFilter = new MyFileFilter(FileExtensions.OFF);
			// TODO: Localization
			offFilter.setDescription("OFF file");
			fileChooser.addChoosableFileFilter(offFilter);

			if (oldCurrentFile == null
					|| StringUtil.getFileExtension(oldCurrentFile.getName())
							.equals(FileExtensions.GEOGEBRA)
					|| StringUtil.getFileExtension(oldCurrentFile.getName())
							.equals(FileExtensions.GEOGEBRA_TOOL)) {
				fileChooser.setFileFilter(fileFilter);
			}

			getApp().setDefaultCursor();
			int returnVal = fileChooser
					.showOpenDialog(getApp().getMainComponent());

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
				getApp().setWaitCursor();
				getApp().setMoveMode();

				for (int i = 0; i < files.length; i++) {

					File file0 = files[i];

					if (!file0.exists()) {
						file0 = addExtension(file0, FileExtensions.GEOGEBRA);
					}

					getApp().applyTemplate(file0);

				}

				getApp().setDefaultCursor();

			} else if (filter == insertFilter) {

				getApp().setWaitCursor();
				getApp().setMoveMode();

				for (int i = 0; i < files.length; i++) {

					File file0 = files[i];

					if (!file0.exists()) {
						file0 = addExtension(file0, FileExtensions.GEOGEBRA);
					}

					getApp().insertFile(file0);
				}

				getApp().setDefaultCursor();

			} else if (filter == offFilter) {

				getApp().setWaitCursor();
				getApp().setMoveMode();

				for (int i = 0; i < files.length; i++) {

					File file0 = files[i];

					if (!file0.exists()) {
						file0 = addExtension(file0, FileExtensions.OFF);
					}

				}

				doOpenFiles(files, true);
				getApp().setDefaultCursor();

			} else {
				doOpenFiles(files, true);
			}

			fileChooser.setMultiSelectionEnabled(false);
		}
	}

	public synchronized void doOpenFiles(File[] files,
			boolean allowOpeningInThisInstance) {
		doOpenFiles(files, allowOpeningInThisInstance, FileExtensions.GEOGEBRA);
	}

	public synchronized void doOpenFiles(File[] files,
			boolean allowOpeningInThisInstance, FileExtensions extension) {
		// there are selected files
		if (files != null) {
			File file;
			int counter = 0;
			for (int i = 0; i < files.length; i++) {
				file = files[i];

				if (!file.exists()) {
					file = addExtension(file, extension);
					if (extension.equals(FileExtensions.GEOGEBRA)
							&& !file.exists()) {
						file = addExtension(removeExtension(file),
								FileExtensions.GEOGEBRA_TOOL);
					}
					if (extension.equals(FileExtensions.GEOGEBRA)
							&& !file.exists()) {
						file = addExtension(removeExtension(file),
								FileExtensions.HTML);
					}
					if (extension.equals(FileExtensions.GEOGEBRA)
							&& !file.exists()) {
						file = addExtension(removeExtension(file),
								FileExtensions.HTM);
					}

					if (extension.equals(FileExtensions.GEOGEBRA)
							&& !file.exists()) {
						file = addExtension(removeExtension(file),
								FileExtensions.OFF);
					}

					if (!file.exists()) {
						// Put the correct extension back on for the error
						// message
						file = addExtension(removeExtension(file), extension);

						JOptionPane.showConfirmDialog(
								getApp().getMainComponent(),
								getLocalization().getError("FileNotFound")
										+ ":\n" + file.getAbsolutePath(),
								getApp().getLocalization().getError("Error"),
								JOptionPane.DEFAULT_OPTION,
								JOptionPane.WARNING_MESSAGE);

					}
				}

				FileExtensions ext = StringUtil
						.getFileExtension(file.getName());

				if (file.exists()) {
					if (FileExtensions.GEOGEBRA_TOOL.equals(ext)) {
						// load macro file
						loadFile(file, true);
					} else if (FileExtensions.HTML.equals(ext)
							|| FileExtensions.HTM.equals(ext)) {
						// load HTML file with applet param ggbBase64
						// if we loaded from GGB, we don't want to overwrite old
						// file
						loadBase64File(file);
					} else if (FileExtensions.OFF.equals(ext)) {
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
											.createNewWindow(
													new CommandLineArguments(
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

	@Override
	public void allowGUIToRefresh() {
		if (!SwingUtilities.isEventDispatchThread()) {
			return;
		}
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
		FileDropTargetListener dtl = ((GeoGebraFrame) getApp().getFrame())
				.getDropTargetListener();
		boolean isGGBFileDrop = dtl.handleFileDrop(t);
		return (isGGBFileDrop);
	}

	@Override
	public boolean loadFile(final File file, final boolean isMacroFile) {
		boolean success = getApp().loadFile(file, isMacroFile);

		updateGUIafterLoadFile(success, isMacroFile);
		getApp().setDefaultCursor();
		return success;
	}

	// See http://stackoverflow.com/questions/6198894/java-encode-url for an
	// explanation
	public static URL getEscapedUrl(String url0) throws Exception {
		String url;
		if (url0.startsWith("www")) {
			url = "http://" + url0;
		} else {
			url = url0;
		}
		URL u = new URL(url);
		return new URI(u.getProtocol(), u.getAuthority(), u.getPath(),
				u.getQuery(), u.getRef()).toURL();
	}

	/*
	 * loads an html file with <param name="ggbBase64" value="UEsDBBQACAAI...
	 */
	public boolean loadBase64File(final File file) {
		boolean success = getApp().loadBase64File(file);
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

		boolean success = getApp().loadOffFile(file);
		updateGUIafterLoadFile(success, false);

		return success;
	}

	@Override
	public void updateGUIafterLoadFile(boolean success, boolean isMacroFile) {
		if (success && !isMacroFile
				&& !getApp().getSettings().getLayout().isIgnoringDocumentLayout()) {
			getLayout().setPerspectives(getApp().getTmpPerspectives(), null);
			SwingUtilities
					.updateComponentTreeUI(getLayout().getRootComponent());
			if (!getApp().isIniting()) {
				updateFrameSize(); // checks internally if frame is available
				if (getApp().needsSpreadsheetTableModel())
				 {
					(getApp()).getSpreadsheetTableModel(); // ensure create one if
														// not already done
				}
			}
		} else if (isMacroFile && success) {
			refreshCustomToolsInToolBar();
			getApp().updateToolBar();
			getApp().updateContentPane();
		}
		if (kernel.wantAnimationStarted()) {
			kernel.getAnimatonManager().startAnimation();
			kernel.setWantAnimationStarted(false);
		}
		if (getApp().isEuclidianView3Dinited()) {
			EuclidianView ev = (EuclidianView) getApp().getEuclidianView3D();
			ev.updateFonts();
			((EuclidianView3DInterface) ev).updateAllDrawables();
		}
		// force JavaScript ggbOnInit(); to be called
		if (!getApp().isApplet()) {
			getApp().getScriptManager().ggbOnInit();
			getApp().centerFrame();
		}
	}

	protected boolean initActions() {
		if (showAxesAction != null) {
			return false;
		}

		showAxesAction = new AbstractAction(loc.getMenu("Axes"),
				getApp().getScaledIcon(GuiResourcesD.AXES)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				showAxesCmd();

			}
		};

		showGridAction = new AbstractAction(loc.getMenu("Grid"),
				getApp().getScaledIcon(GuiResourcesD.GRID)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				showGridCmd();

			}
		};

		undoAction = new AbstractAction(loc.getMenu("Undo"),
				getApp().getScaledIcon(GuiResourcesD.MENU_EDIT_UNDO)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				undo();

			}
		};

		redoAction = new AbstractAction(loc.getMenu("Redo"),
				getApp().getScaledIcon(GuiResourcesD.MENU_EDIT_REDO)) {
			private static final long serialVersionUID = 1L;

			@Override
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
			if (getApp().isUndoActive()) {
				undoAction.setEnabled(kernel.undoPossible());
			} else {
				// eg --enableUndo=false
				undoAction.setEnabled(false);
			}
		}
		if (redoAction != null) {
			if (getApp().isUndoActive()) {
				redoAction.setEnabled(kernel.redoPossible());
			} else {
				// eg --enableUndo=false
				redoAction.setEnabled(false);
			}
		}

	}

	public int getMenuBarHeight() {
		if (menuBar == null) {
			return 0;
		}
		return ((JMenuBar) menuBar).getHeight();
	}

	public int getAlgebraInputHeight() {
		if (getApp().showAlgebraInput() && algebraInput != null) {
			return algebraInput.getHeight();
		}
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

	@Override
	public String getToolbarDefinition() {
		// "null" may appear in files created using some buggy versions of Touch
		if (("null".equals(strCustomToolbarDefinition)
				|| strCustomToolbarDefinition == null) && toolbarPanel != null) {
			return getGeneralToolbar().getDefaultToolbarString();
		}
		return strCustomToolbarDefinition;
	}

	@Override
	public void removeFromToolbarDefinition(int mode) {
		if (strCustomToolbarDefinition != null) {
			// Application.debug("before: " + strCustomToolbarDefinition +
			// ", delete " + mode);

			strCustomToolbarDefinition = strCustomToolbarDefinition
					.replaceAll(Integer.toString(mode), "");
		}
	}

	@Override
	public void addToToolbarDefinition(int mode) {
		if (this.getActiveEuclidianView().getDimension() > 2) {
			DockPanelD panel = this.getLayout().getDockManager()
					.getPanel(this.getActiveEuclidianView().getViewID());
			panel.addToToolbar(mode);
			panel.updateToolbar();
			return;
		}
		strCustomToolbarDefinition = ToolBar.addMode(strCustomToolbarDefinition,
				mode);

	}

	public void showURLinBrowser(URL url) {
		if (AppD.getJApplet() != null) {
			Log.debug("opening URL (applet):" + url);
			AppD.getJApplet().getAppletContext().showDocument(url, "_blank");
		} else {
			Log.debug("opening URL:" + url.toExternalForm());
			BrowserLauncher.openURL(url.toExternalForm());
		}
	}

	@Override
	public void openHelp(String page, Help type) {
		try {
			URL helpURL = getEscapedUrl(getHelpURL(type, page));
			showURLinBrowser(helpURL);
		} catch (MyError e) {
			getApp().showError(e);
		} catch (Exception e) {
			Log.debug("openHelp error: " + e.toString() + " " + e.getMessage()
					+ " " + page + " " + type);
			getApp().showGenericError(e);
		}
	}

	@Override
	public void showURLinBrowser(String strURL) {
		try {
			URL url = getEscapedUrl(strURL);
			showURLinBrowser(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int setToolbarMode(int mode, ModeSetter m) {
		if (toolbarPanel == null) {
			if (layout != null && layout.getDockManager() != null) {
				layout.getDockManager().setToolbarMode(mode);
			}
			return mode;
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

	@Override
	public void updateFrameSize() {
		JFrame fr = getApp().getFrame();
		if (fr instanceof GeoGebraFrame) {
			((GeoGebraFrame) fr).updateSize();
			getApp().validateComponent();
		}
	}

	@Override
	public void updateFrameTitle() {
		if (!(getApp().getFrame() instanceof GeoGebraFrame)) {
			return;
		}

		GeoGebraFrame frame = (GeoGebraFrame) getApp().getFrame();

		StringBuilder sb = new StringBuilder();
		if (getApp().getCurrentFile() != null) {
			sb.append(getApp().getCurrentFile().getName());
		} else {
			sb.append(GeoGebraConstants.APPLICATION_NAME);
			sb.append(" Classic 5");
			if (GeoGebraFrame.getInstanceCount() > 1) {
				int nr = frame.getInstanceNumber();
				sb.append(" (");
				sb.append(nr + 1);
				sb.append(')');
			}
		}
		frame.setTitle(sb.toString());
	}

	@Override
	public Object createFrame() {
		GeoGebraFrame wnd = new GeoGebraFrame();
		wnd.setGlassPane(layout.getDockManager().getGlassPane());
		wnd.setApplication(getApp());

		return wnd;
	}

	public static synchronized void exitAll() {
		ArrayList<GeoGebraFrame> insts = GeoGebraFrame.getInstances();
		GeoGebraFrame[] instsCopy = new GeoGebraFrame[insts.size()];
		for (int i = 0; i < instsCopy.length; i++) {
			instsCopy[i] = insts.get(i);
		}

		for (int i = 0; i < instsCopy.length; i++) {
			instsCopy[i].getApplication().exit();
		}
	}

	@Override
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
		if (virtualKeyboard != null) {
			if (currentKeyboardListener == null) {
				// close virtual keyboard when focus lost
				// ... unless we've lost focus because we've just opened it!
				if (autoClose) {
					toggleKeyboard(false);
				}
			} else {
				// open virtual keyboard when focus gained
				if (AppD.isVirtualKeyboardActive()) {
					toggleKeyboard(true);
				}
			}
		}
	}

	WindowsUnicodeKeyboard kb = null;

	public void insertStringIntoTextfield(String text, boolean altPressed,
			boolean ctrlPressed, boolean shiftPressed) {

		if (currentKeyboardListener != null && !"\n".equals(text)
				&& (!text.startsWith("<") || !text.endsWith(">")) && !altPressed
				&& !ctrlPressed) {
			currentKeyboardListener.insertString(text);
		} else {
			// use Robot if no TextField currently active
			// or for special keys eg Enter
			if (kb == null) {
				try {
					kb = new WindowsUnicodeKeyboard();
				} catch (Exception e) {
					return;
				}
			}

			kb.doType(altPressed, ctrlPressed, shiftPressed, text);

		}
	}

	VirtualKeyboardD virtualKeyboard = null;

	public void toggleKeyboard(boolean show) {
		getVirtualKeyboard().setVisible(show);
	}

	/**
	 * @return The virtual keyboard (initializes it if necessary)
	 */
	public VirtualKeyboardD getVirtualKeyboard() {
		if (virtualKeyboard == null) {
			KeyboardSettings settings = (KeyboardSettings) getApp()
					.getSettings().getKeyboard();
			virtualKeyboard = new VirtualKeyboardD((getApp()),
					settings.getKeyboardWidth(), settings.getKeyboardHeight(),
					(float) settings.getKeyboardOpacity());
			settings.addListener(virtualKeyboard);
		}

		return virtualKeyboard;
	}

	public boolean hasVirtualKeyboard() {
		return virtualKeyboard != null;
	}

	public boolean showVirtualKeyboard() {
		if (virtualKeyboard == null) {
			return false;
		}

		return virtualKeyboard.isVisible();
	}

	@Override
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
			recentSymbolList = new ArrayList<>();
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
			if (com instanceof Container) {
				setFontRecursive((Container) com, font);
			}
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
		}
	}

	private InputBarHelpPanelD inputHelpPanel;

	public boolean hasInputHelpPanel() {
		if (inputHelpPanel == null) {
			return false;
		}
		return true;
	}

	public void reInitHelpPanel(boolean forCAS) {
		if (inputHelpPanel != null) {
			if (forCAS) {
				getApp().getCommandDictionaryCAS();
			}
			inputHelpPanel.setLabels();
		}
	}

	@Override
	public Component getInputHelpPanel() {

		if (inputHelpPanel == null) {
			if (getApp().showView(App.VIEW_CAS)) {
				getApp().getCommandDictionaryCAS();
			}
			inputHelpPanel = new InputBarHelpPanelD(getApp());
		}
		return inputHelpPanel;
	}

	public void setFocusedPanel(MouseEventND event,
			boolean updatePropertiesView) {
		// determine parent panel to change focus
		EuclidianDockPanelAbstract panel = (EuclidianDockPanelAbstract) SwingUtilities
				.getAncestorOfClass(EuclidianDockPanelAbstract.class,
						event.getComponent());

		setFocusedPanel(panel, updatePropertiesView);
	}

	@Override
	public void setFocusedPanel(int viewId, boolean updatePropertiesView) {
		setFocusedPanel(getLayout().getDockManager().getPanel(viewId),
				updatePropertiesView);

	}

	public void setFocusedPanel(DockPanelD panel,
			boolean updatePropertiesView) {

		if (panel != null) {
			getLayout().getDockManager().setFocusedPanel(panel,
					updatePropertiesView);

			// notify the properties view
			if (updatePropertiesView) {
				updatePropertiesView();
			}
		}
	}

	@Override
	public void updateAlgebraInput() {
		if (algebraInput != null) {
			algebraInput.initGUI();
		}
	}

	@Override
	public void updatePropertiesView() {
		if (propertiesView != null) {
			propertiesView.updatePropertiesView();
		}
	}

	/**
	 * close properties view
	 * 
	 */
	@Override
	public void updatePropertiesViewStylebar() {
		if (propertiesView != null) {
			propertiesView.updateStyleBar();
		}
	}

	@Override
	public void mouseReleasedForPropertiesView(boolean creatorMode) {
		if (propertiesView != null) {
			propertiesView.mouseReleasedForPropertiesView(creatorMode);
		}
	}

	@Override
	public void mousePressedForPropertiesView() {
		if (propertiesView != null) {
			propertiesView.mousePressedForPropertiesView();
		}
	}

	@Override
	public void showPopupMenu(ArrayList<GeoElement> selectedGeos,
			EuclidianViewInterfaceCommon view, GPoint mouseLoc) {
		showPopupMenu(selectedGeos,
				((EuclidianViewInterfaceD) view).getJPanel(), mouseLoc);

	}

	@Override
	public void showPopupChooseGeo(ArrayList<GeoElement> selectedGeos,
			ArrayList<GeoElement> geos, EuclidianViewInterfaceCommon view,
			GPoint p) {

		showPopupChooseGeo(selectedGeos, geos, (EuclidianView) view, p);
	}

	@Override
	public void setFocusedPanel(AbstractEvent event,
			boolean updatePropertiesView) {
		setFocusedPanel((MouseEventND) event, updatePropertiesView);
	}

	@Override
	public void loadImage(GeoPoint corner, Object transfer,
			boolean fromClipboard,
			EuclidianView ev) {
		loadImage(corner, fromClipboard, (Transferable) transfer, ev);

	}

	/**
	 * Creates a new GeoImage, using an image provided by either a Transferable
	 * object or the clipboard contents, then places it at the given location
	 * (real world coords). If the transfer content is a list of images, then
	 * multiple GeoImages will be created.
	 * 
	 * @return whether a new image was created or not
	 */
	public boolean loadImage(GeoPoint corner, boolean fromClipboard,
			Transferable transfer, EuclidianView ev) {
		getApp().setWaitCursor();

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
			getApp().setMoveMode();
		} else {
			// create GeoImage object(s) for this fileName
			GeoImage geoImage = null;
			if (fileName.length == 1) {
				geoImage = new GeoImage(getApp().getKernel().getConstruction());
				geoImage.setImageFileName(fileName[0]);
				setImageCornersFromSelection(geoImage);
				getApp().setDefaultCursor();
				return true;
			}
			if (!corner.isLabelSet()) {
				corner.setLabel(null);
			}

			for (int i = 0; i < fileName.length; i++) {
				GeoPoint point1;
				if (i == 0) {
					point1 = corner;
				} else {
					point1 = new GeoPoint(getApp().getKernel().getConstruction());
					point1.setCoordsFromPoint(corner);
					point1.setLabel(null);
				}

				geoImage = new GeoImage(getApp().getKernel().getConstruction());
				geoImage.setImageFileName(fileName[i]);
				// Log.debug("filename = " + fileName[i]);
				geoImage.setCorner(point1, 0);

				GeoPoint point2 = new GeoPoint(
						getApp().getKernel().getConstruction());
				geoImage.calculateCornerPoint(point2, 2);
				geoImage.setCorner(point2, 1);
				point2.setLabel(null);

				// make sure 2nd corner is on screen
				double x1 = point1.inhomX;
				double x2 = point2.inhomX;
				double xmax = ev
						.toRealWorldCoordX((double) (ev.getWidth()) + 1);
				if (x2 > xmax) {
					point2.setCoords((x1 + 9 * xmax) / 10, point2.inhomY, 1);
					point2.update();
				}

				geoImage.setLabel(null);

				GeoImage.updateInstances(getApp());
			}
			// make sure only the last image will be selected
			GeoElement[] geos = { geoImage };
			getApp().getActiveEuclidianView().getEuclidianController()
					.clearSelections();
			getApp().getActiveEuclidianView().getEuclidianController()
					.memorizeJustCreatedGeos(geos);
			ret = true;
		}

		getApp().setDefaultCursor();
		return ret;
	}

	@Override
	public void showDrawingPadPopup(EuclidianViewInterfaceCommon view,
			GPoint mouseLoc) {
		if (view instanceof EuclidianViewD) {
			// 2D
			showDrawingPadPopup(((EuclidianViewD) view).getJPanel(), mouseLoc);
		} else {
			// 3D
			showDrawingPadPopup3D(view, mouseLoc);
		}
	}

	@Override
	public void showDrawingPadPopup3D(EuclidianViewInterfaceCommon view,
			GPoint mouseLoc) {
		// 3D stuff
	}

	@Override
	public void showPropertiesViewSliderTab() {
		propertiesView.showSliderTab();
	}

	@Override
	public void showGraphicExport() {
		getApp().getSelectionManager().clearSelectedGeos(true, false);
		getApp().updateSelection(false);

		JDialog d = new GraphicExportDialog(getApp());

		d.setVisible(true);
	}

	@Override
	public void showPSTricksExport() {
		GeoGebraToPstricks export = new GeoGebraToPstricksD(getApp());
		new PstricksFrame(export).setVisible(true);

	}

	@Override
	public void showWebpageExport() {
		getApp().getSelectionManager().clearSelectedGeos(true, false);
		getApp().updateSelection(false);
		WorksheetExportDialog d = new WorksheetExportDialog(getApp());

		d.setVisible(true);
	}

	@Override
	public void clearInputbar() {
		((AlgebraInputD) getAlgebraInput()).clear();
	}

	public int getInputHelpPanelMinimumWidth() {
		return getInputHelpPanel().getMinimumSize().width;
	}

	@Override
	public int getActiveToolbarId() {
		if (toolbarPanel == null) {
			return -1;
		}
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

	@Override
	public void setToolBarDefinition(String toolBarDefinition) {
		strCustomToolbarDefinition = toolBarDefinition;
	}

	@Override
	public boolean checkAutoCreateSliders(String s,
			AsyncOperation<String[]> callback) {
		Component comp = getApp().getMainComponent();
		if (getApp().getFrame() instanceof GeoGebraFrame) {
			GeoGebraFrame frame = (GeoGebraFrame) getApp().getFrame();
			comp = frame != null && !frame.isIconified() ? frame : null;
		}

		Object[] options = { loc.getMenu("CreateSliders"),
				loc.getMenu("Cancel") };
		int returnVal = JOptionPane.showOptionDialog(comp,
				loc.getPlain("CreateSlidersForA", s),
				loc.getMenu("CreateSliders"), JOptionPane.DEFAULT_OPTION,
				JOptionPane.WARNING_MESSAGE,

				getApp().getModeIcon(EuclidianConstants.MODE_SLIDER),
				options, options[0]);
		if (callback != null) {
			Log.debug("callback" + returnVal);
			callback.callback(new String[] {
					returnVal == 0 ? AlgebraProcessor.CREATE_SLIDER : "0" });
		}
		return false;
	}

	@Override
	public boolean belongsToToolCreator(ListCellRenderer renderer) {
		return ToolCreationDialogD.isMyCellRenderer(renderer);
	}

	@Override
	protected ConstructionProtocolNavigation newConstructionProtocolNavigation(
			int viewID) {
		ConstructionProtocolNavigationD cpn = new ConstructionProtocolNavigationD(
				this.getApp(), viewID);
		if (constructionProtocolView != null) {
			cpn.register(constructionProtocolView);
		}
		return cpn;
	}

	@Override
	public void login() {
		getApp().getDialogManager().showLogInDialog();
	}

	@Override
	public void logout() {
		getApp().getDialogManager().showLogOutDialog();
	}

	@Override
	public int getEuclidianViewCount() {
		return euclidianView2.size();
	}

	@Override
	public void resetCasView() {
		if (casView != null) {
			casView.resetCursor();
		}
	}

	@Override
	public boolean hasDataCollectionView() {
		// not available in desktop
		return false;
	}

	@Override
	public void getDataCollectionViewXML(StringBuilder sb,
			boolean asPreference) {
		// not available in desktop
	}

	@Override
	public void getToolImageURL(int mode, GeoImage gi,
			AsyncOperation<String> callback) {
		String modeStr = StringUtil
				.toLowerCaseUS(EuclidianConstants.getModeTextSimple(mode));
		callback.callback(getApp().getImageManager().createImage(
				getApp()
						.getImageManager().getToolImageResource(modeStr),
				getApp()));
	}

	@Override
	public EuclidianViewInterfaceCommon getPlotPanelEuclidanView() {
		return null;
	}

	@Override
	public void replaceInputSelection(String string) {
		JTextComponent textComponent = ((AlgebraInputD) getAlgebraInput())
				.getTextField();
		textComponent.replaceSelection(string);
		textComponent.requestFocusInWindow();
	}

	@Override
	public void setInputText(String string) {
		JTextComponent textComponent = ((AlgebraInputD) getAlgebraInput())
				.getTextField();
		textComponent.setText(string);
		((AlgebraInputD) getAlgebraInput()).setAutoInput(string);
		textComponent.requestFocusInWindow();
	}

	@Override
	public void openMenuInAVFor(GeoElement geo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadWebcam() {
		// TODO Auto-generated method stub
	}

}