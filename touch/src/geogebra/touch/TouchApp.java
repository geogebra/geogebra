package geogebra.touch;

import geogebra.common.awt.GFont;
import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.factories.AwtFactory;
import geogebra.common.factories.Factory;
import geogebra.common.gui.GuiManager;
import geogebra.common.gui.menubar.MenuInterface;
import geogebra.common.gui.toolbar.ToolBar;
import geogebra.common.gui.view.algebra.AlgebraView;
import geogebra.common.gui.view.consprotocol.ConstructionProtocolNavigation;
import geogebra.common.io.MyXMLio;
import geogebra.common.io.layout.DockPanelData;
import geogebra.common.io.layout.DockSplitPaneData;
import geogebra.common.io.layout.Perspective;
import geogebra.common.kernel.ConstructionDefaults;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.main.DialogManager;
import geogebra.common.main.FontManager;
import geogebra.common.main.SpreadsheetTableModel;
import geogebra.common.main.settings.Settings;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.common.plugin.jython.PythonBridge;
import geogebra.common.util.debug.GeoGebraLogger.LogDestination;
import geogebra.html5.main.AppWeb;
import geogebra.html5.main.FontManagerW;
import geogebra.html5.main.LocalizationW;
import geogebra.html5.main.ViewManager;
import geogebra.html5.util.debug.GeoGebraLogger;
import geogebra.touch.gui.GeoGebraTouchGUI;
import geogebra.touch.gui.InfoBarT;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.euclidian.EuclidianViewM;
import geogebra.touch.utils.GeoGebraLoggerM;
import geogebra.touch.utils.TitleChangedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Extends from class {@link App}.
 * 
 * @author Matthias Meisinger
 * 
 */
public class TouchApp extends AppWeb {
	private final List<TitleChangedListener> titleListeners;

	private final GeoGebraTouchGUI touchGUI;
	private final FontManagerW fontManager;
	/**
	 * static because it gets from server side, either "" or the set filename
	 */
	public static String currentFileId = null;

	// accepting range for hitting Geos (except for Points) is multiplied with
	// this factor
	// (for Points see EuclidianView)
	private final int selectionFactor = 3;

	private GuiManager guiManager;

	private boolean isDefaultFileName;

	private final Stack<ErrorHandler> errorHandlers;

	private FileManagerM fm;

	/**
	 * Initializes the factories, {@link FontManagerW} and {@link Settings}.
	 * 
	 * @param touchGUI
	 *            graphic user interface
	 * @see geogebra.common.factories.FormatFactory FormatFactory
	 * @see geogebra.common.factories.AwtFactory AwtFactory
	 */
	public TouchApp(GeoGebraTouchGUI touchGUI) {
		this.titleListeners = new ArrayList<TitleChangedListener>();

		super.initing = true;
		this.touchGUI = touchGUI;
		this.errorHandlers = new Stack<ErrorHandler>();

		this.setLabelDragsEnabled(false);

		initFactories();

		infobar = new InfoBarT();

		this.fontManager = new FontManagerW();

		this.settings = new Settings();

		this.setFontSize(12);

		this.capturingThreshold *= this.selectionFactor;

		if ("true".equals(RootPanel.getBodyElement().getAttribute(
				"data-param-showLogging"))) {
			logger = new GeoGebraLogger();
			logger.setLogDestination(LogDestination.CONSOLES);
			logger.setLogLevel("DEBUG");
		} else if ("onscreen".equals(RootPanel.getBodyElement().getAttribute(
				"data-param-showLogging"))) {
			logger = new GeoGebraLoggerM(touchGUI);
			logger.setLogDestination(LogDestination.CONSOLES);
			logger.setLogLevel("DEBUG");
		}

		this.initImageManager();
		this.setSaved();
	}

	@Override
	public void addMenuItem(MenuInterface parentMenu, String filename,
			String name, boolean asHtml, MenuInterface subMenu) {
	}

	public void addTitleChangedListener(TitleChangedListener t) {
		this.titleListeners.add(t);
	}

	@Override
	public void afterLoadFileAppOrNot() {
		App.debug("After file load ...");
		// TODO: check what else we need to reset
		this.kernel.initUndoInfo();
		this.getEuclidianView1().synCanvasSize();
		this.getEuclidianView1().getEuclidianController()
				.stopCollectingMinorRepaints();
		// notify all construction title listeners
		if (!this.getConstructionTitle().equals("")) {
			this.setConstructionTitle(this.getConstructionTitle());
		} else {
			this.setConstructionTitle(TouchEntryPoint.browseGUI
					.getChosenMaterial().getMaterialTitle());
		}

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				TouchEntryPoint.tabletGUI.onResize();
			}
		});
		this.touchGUI.resetMode();
	}

	public void approveFileName() {
		this.isDefaultFileName = false;
	}

	@Override
	public void appSplashCanNowHide() {
	}

	@Override
	public void copyGraphicsViewToClipboard() {
	}

	@Override
	public EuclidianView createEuclidianView() {
		if (this.euclidianView == null) {
			// initEuclidianViews();
			this.euclidianView = (EuclidianView) this.getActiveEuclidianView();
		}
		return this.euclidianView;
	}

	@Override
	public void createNewWindow() {
	}

	@Override
	public void evalJavaScript(App app, String script, String arg) {

	}

	@Override
	public void evalPythonScript(App app, String string, String arg) {

	}

	@Override
	public void exitAll() {
	}

	@Override
	public void fileNew() {
		this.kernel.clearConstruction(true);
		this.touchGUI.allowEditing(true);
		this.kernel.initUndoInfo();
		this.setDefaultConstructionTitle();
		this.setLabelingStyle(ConstructionDefaults.LABEL_VISIBLE_POINTS_ONLY);
		this.settings.beginBatch();
		this.settings.getEuclidian(1).reset();
		this.settings.getEuclidian(1).setShowAxes(true, true);
		this.settings.getEuclidian(1).setPointCapturing(
				EuclidianStyleConstants.POINT_CAPTURING_AUTOMATIC);
		this.settings.endBatch();
		this.kernel.notifyRepaint();
	}

	protected void fireTitleChangedEvent(String title) {
		for (final TitleChangedListener t : this.titleListeners) {
			t.onTitleChange(title);
		}
	}

	@Override
	public long freeMemory() {
		return 0;
	}

	@Override
	public boolean freeMemoryIsCritical() {
		return false;
	}

	@Override
	public EuclidianViewInterfaceCommon getActiveEuclidianView() {
		return this.touchGUI.getEuclidianViewPanel().getEuclidianView();
	}

	/**
	 * required for repaint
	 */
	@Override
	public AlgebraView getAlgebraView() {
		return this.touchGUI.getAlgebraViewPanel() == null ? null
				: this.touchGUI.getAlgebraViewPanel().getAlgebraView();
	}

	@Override
	public Canvas getCanvas() {
		return ((EuclidianViewM) this.getActiveEuclidianView()).getCanvas();
	}

	@Override
	public ConstructionProtocolNavigation getConstructionProtocolNavigation() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Get the current constructions title
	 * 
	 */
	public String getConstructionTitle() {
		return this.kernel.getConstruction().getTitle();
	}

	@Override
	public String getCountryFromGeoIP() throws Exception {
		return null;
	}

	@Override
	public DialogManager getDialogManager() {
		return null;
	}

	@Override
	public Factory getFactory() {
		return null;
	}

	public FileManagerM getFileManager() {
		return this.fm;
	}

	@Override
	protected FontManager getFontManager() {
		return this.fontManager;
	}

	@Override
	public GuiManager getGuiManager() {
		// TODO
		if (this.guiManager == null) {
			this.guiManager = new GuiManagerT();
		}
		return this.guiManager;
	}

	@Override
	public double getHeight() {
		return 0;
	}

	@Override
	public String getLanguageFromCookie() {
		return null;
	}

	@Override
	protected void getLayoutXML(StringBuilder sb, boolean asPreference) {
		sb.append("\t<perspectives>\n");

		final Perspective tmp = new Perspective("tmp");
		tmp.setShowAxes(this.getEuclidianView1().getShowAxis(1));
		tmp.setShowGrid(this.getEuclidianView1().getShowGrid());
		final DockPanelData[] dock = new DockPanelData[2];
		final int width = Window.getClientWidth();
		final int algebraWidth = TabletGUI.computeAlgebraWidth();
		final int height = Window.getClientHeight();
		tmp.setToolbarDefinition(ToolBar.getAllToolsNoMacros(false, true));
		tmp.setShowToolBar(true);
		dock[0] = new DockPanelData(App.VIEW_EUCLIDIAN, "", true, false, true,
				new GPoint(0, 0), AwtFactory.prototype.newDimension(width
						- algebraWidth, height), "", width - algebraWidth);
		dock[1] = new DockPanelData(App.VIEW_ALGEBRA, "",
				this.touchGUI.isAlgebraShowing(), false, true, new GPoint(width
						- algebraWidth, 0), AwtFactory.prototype.newDimension(
						algebraWidth, height), "", algebraWidth);
		tmp.setSplitPaneData(new DockSplitPaneData[0]);
		tmp.setDockPanelData(dock);
		// save the current perspective

		sb.append(tmp.getXml());

		// save all custom perspectives as well

		sb.append("\t</perspectives>\n");
	}

	/**
	 * 
	 * @return language of client
	 */
	public native String getLocale() /*-{
										var language = window.navigator.systemLanguage
										|| window.navigator.language;
										return language;
										}-*/;

	@Override
	public String getLocaleStr() {
		// never used? - getCurrentLocale always returns "default"
		final String localeName = LocaleInfo.getCurrentLocale().getLocaleName();
		App.debug("Current Locale: " + localeName);

		if (localeName.toLowerCase().equals(LocalizationW.DEFAULT_LOCALE)) {
			return LocalizationW.DEFAULT_LANGUAGE;
		}
		return localeName.substring(0, 2);
	}

	@Override
	public GFont getPlainFontCommon() {
		return new geogebra.html5.awt.GFontW("normal");
	}

	@Override
	public PythonBridge getPythonBridge() {
		return null;
	}

	@Override
	public SpreadsheetTableModel getSpreadsheetTableModel() {
		return null;
	}

	@Override
	public String getToolTooltipHTML(int mode) {
		throw new UnsupportedOperationException();
	}

	public GeoGebraTouchGUI getTouchGui() {
		return this.touchGUI;
	}

	@Override
	public ViewManager getViewManager() {
		return new ViewManagerM();
	}

	@Override
	public double getWidth() {
		return 0;
	}

	@Override
	protected int getWindowHeight() {
		return Window.getClientHeight();
	}

	@Override
	protected int getWindowWidth() {
		return Window.getClientWidth();
	}

	@Override
	public boolean hasEuclidianView2EitherShowingOrNot() {
		return false;
	}

	@Override
	protected void initGuiManager() {

	}

	@Override
	public boolean isApplet() {
		return false;
	}

	public boolean isDefaultFileName() {
		return this.isDefaultFileName;
	}

	@Override
	public boolean isHTML5Applet() {
		return true;
	}

	@Override
	public boolean isShowingEuclidianView2() {
		return false;
	}

	@Override
	public boolean isUsingFullGui() {
		return false;
	}

	@Override
	protected EuclidianController newEuclidianController(
			geogebra.common.kernel.Kernel kernel1) {
		return null;
	}

	@Override
	protected EuclidianView newEuclidianView(boolean[] showAxes1,
			boolean showGrid1) {
		return null;
	}

	/**
	 * @param handler
	 *            handler to be registered
	 */
	public void registerErrorHandler(ErrorHandler handler) {
		this.errorHandlers.push(handler);
	}

	public void removeTitleChangedListener(TitleChangedListener t) {
		this.titleListeners.remove(t);
	}

	@Override
	protected void resetCommandDictionary() {
	}

	/**
	 * Set the current constructions title and notify all TitleChangedListener
	 * 
	 * @param title
	 *            the new title of the current construction
	 */
	public void setConstructionTitle(String title) {
		this.kernel.getConstruction().setTitle(title);
		this.isDefaultFileName = false;
		this.fireTitleChangedEvent(title);
	}

	public void setDefaultConstructionTitle() {
		this.setConstructionTitle(this.fm.getDefaultConstructionTitle(this
				.getLocalization()));
		this.isDefaultFileName = true;
	}

	public void setFileManager(FileManagerM fm) {
		this.fm = fm;
	}

	@Override
	public void setLabels() {
		this.touchGUI.setLabels();
		TouchEntryPoint.worksheetGUI.setLabels();
		TouchEntryPoint.browseGUI.setLabels();
	}

	public void setLanguage() {
		final String locale = this.getLocale();
		final String language = locale.substring(0, 2);

		String country = "";
		if (locale.contains("-")) {
			country = locale.split("-")[1];
		}

		this.setLanguage(language, country);
	}

	@Override
	public void setShowConstructionProtocolNavigation(boolean show,
			boolean playButton, double playDelay, boolean showProtButton) {

	}

	@Override
	public void setWaitCursor() {

	}

	@Override
	public boolean showAlgebraInput() {
		return false;
	}

	@Override
	public void showCommandError(String command, String message) {

	}

	@Override
	public void showError(String s) {
		if (this.errorHandlers.peek() != null) {
			this.errorHandlers.peek().showError(s);
		}
	}

	@Override
	public void showError(String key, String error) {
		this.showErrorDialog(this.getLocalization().getError(key) + ": "
				+ error);
	}

	@Override
	public void showErrorDialog(String s) {
		if (this.errorHandlers.peek() != null) {
			this.errorHandlers.peek().showError(s);
		}
	}

	@Override
	public void showLoadingAnimation(boolean b) {
	}

	@Override
	public void showMessage(String error) {
	}

	@Override
	public void showRelation(GeoElement geoElement, GeoElement geoElement2) {

	}

	@Override
	public void showURLinBrowser(String string) {

	}

	@Override
	public boolean showView(int view) {
		return false;
	}

	/**
	 * Creates a new {@link Kernel}, a new instance of {@link MyXMLio} and
	 * initializes the components of the {@link GeoGebraTouchGUI}.
	 */
	public void start() {
		this.initKernel();

		this.touchGUI.initComponents(this.kernel);
		super.euclidianView = this.touchGUI.getEuclidianViewPanel()
				.getEuclidianView();

		hasFullPermissions = true;
		this.setUndoActive(true);

		super.initing = false;
		this.getScriptManager();

		if (Location.getParameter("lang") != null) {
			this.setLanguage(Location.getParameter("lang"));
		} else {
			this.setLanguage();
		}

		this.setDefaultConstructionTitle();
	}

	@Override
	public void storeUndoInfo() {
		if (this.isUndoActive()) {
			this.kernel.storeUndoInfo();
		}
		this.setUnsaved();
	}

	private void toggleAVvisibility(DockPanelData[] dockPanelData) {
		for (final DockPanelData dp : dockPanelData) {
			if (dp.getViewId() == App.VIEW_ALGEBRA) {
				this.touchGUI.setAlgebraVisible(dp.isVisible());
			}
		}
	}

	/**
	 * @param handler
	 *            handler to be unregistered
	 */
	public void unregisterErrorHandler(ErrorHandler handler) {
		this.errorHandlers.remove(handler);
	}

	@Override
	public void updateActions() {
		TouchEntryPoint.getLookAndFeel().updateUndoSaveButtons();
	}

	@Override
	public void updateApplicationLayout() {
	}

	@Override
	public void updateMenubar() {

	}

	// // alternative, falls probleme mit Android - nicht getestet
	// public native String getLocale() /*-{
	//
	// var lang;
	// if (navigator
	// && navigator.userAgent
	// && (lang = navigator.userAgent
	// .match(/android.*\W(\w\w)-(\w\w)\W/i))) {
	// lang = lang[1];
	// }
	//
	// if (!lang && navigator) {
	// if (navigator.language) {
	// lang = navigator.language;
	// } else if (navigator.browserLanguage) {
	// lang = navigator.browserLanguage;
	// } else if (navigator.systemLanguage) {
	// lang = navigator.systemLanguage;
	// } else if (navigator.userLanguage) {
	// lang = navigator.userLanguage;
	// }
	// lang = lang.substr(0, 2);
	// }
	//
	// alert("current language is", lang);
	// return lang;
	// }-*/;

	// TODO: use with SelectionManager
	// public void updateSelection(boolean updatePropertiesView){
	// if(this.getMode() == EuclidianConstants.MODE_MOVE){
	// this.touchGUI.updateStylingBar(this.getSelectionManager());
	// }
	// }

	@Override
	public void updateStyleBars() {

	}

	@Override
	public void updateUI() {

	}

	@Override
	public void uploadToGeoGebraTube() {
	}

}
