package geogebra.touch;

import geogebra.common.awt.GColor;
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
import geogebra.common.io.MyXMLio;
import geogebra.common.io.layout.DockPanelData;
import geogebra.common.io.layout.DockSplitPaneData;
import geogebra.common.io.layout.Perspective;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.main.DialogManager;
import geogebra.common.main.FontManager;
import geogebra.common.main.SpreadsheetTableModel;
import geogebra.common.main.settings.Settings;
import geogebra.common.plugin.jython.PythonBridge;
import geogebra.common.util.debug.Log;
import geogebra.common.util.debug.Log.LogDestination;
import geogebra.html5.euclidian.EuclidianViewWeb;
import geogebra.html5.gui.view.algebra.AlgebraViewWeb;
import geogebra.html5.main.AppWeb;
import geogebra.html5.main.FontManagerW;
import geogebra.html5.main.ViewManager;
import geogebra.html5.util.debug.GeoGebraLogger;
import geogebra.html5.util.ggtapi.JSONparserGGT;
import geogebra.touch.gui.GeoGebraTouchGUI;
import geogebra.touch.gui.InfoBarT;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.euclidian.EuclidianViewT;
import geogebra.touch.utils.GeoGebraLoggerT;
import geogebra.touch.utils.TitleChangedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
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
	private FileManagerT fm;
	private GuiManager guiManager;

	// accepting range for hitting Geos (except for Points) is multiplied with
	// this factor (for Points see EuclidianView)
	private final int selectionFactor = 3;
	private boolean isDefaultFileName;
	private final Stack<ErrorHandler> errorHandlers;
	private final DefaultErrorHandler defaultErrorHandler;

	/**
	 * Initializes the factories, {@link FontManagerW} and {@link Settings}.
	 * 
	 * @param touchGUI
	 *            graphic user interface
	 * @see geogebra.common.factories.FormatFactory FormatFactory
	 * @see geogebra.common.factories.AwtFactory AwtFactory
	 */
	TouchApp(final GeoGebraTouchGUI touchGUI) {
		super();
		this.titleListeners = new ArrayList<TitleChangedListener>();

		super.initing = true;
		this.touchGUI = touchGUI;
		this.errorHandlers = new Stack<ErrorHandler>();
		this.defaultErrorHandler = new DefaultErrorHandler(
				this.getLocalization());
		this.errorHandlers.add(this.defaultErrorHandler);
		this.setLabelDragsEnabled(false);

		initFactories();

		infobar = new InfoBarT();

		this.fontManager = new FontManagerW();

		this.settings = new Settings();

		this.setFontSize(12);

		this.capturingThreshold *= this.selectionFactor;

		if ("true".equals(RootPanel.getBodyElement().getAttribute(
				"data-param-showLogging"))) {
			Log.logger = new GeoGebraLogger();
			Log.logger.setLogDestination(LogDestination.CONSOLES);
			Log.logger.setLogLevel("DEBUG");
		} else if ("onscreen".equals(RootPanel.getBodyElement().getAttribute(
				"data-param-showLogging"))) {
			Log.logger = new GeoGebraLoggerT(touchGUI);
			Log.logger.setLogDestination(LogDestination.CONSOLES);
			Log.logger.setLogLevel("DEBUG");
		}

		this.initImageManager();
		this.setSaved();
	}

	@Override
	public void addMenuItem(final MenuInterface parentMenu,
			final String filename, final String name, final boolean asHtml,
			final MenuInterface subMenu) {
	}

	public void addTitleChangedListener(final TitleChangedListener t) {
		this.titleListeners.add(t);
	}

	@Override
	public void afterLoadFileAppOrNot() {
		App.debug("After file load ...");
		// FIXME: check what else we need to reset
		this.kernel.initUndoInfo();
		this.getEuclidianView1().synCanvasSize();
		TouchEntryPoint.getLookAndFeel().resetNativeHandlers();
		this.getEuclidianView1().getEuclidianController()
				.stopCollectingMinorRepaints();
		// notify all construction title listeners
		if (!this.getConstructionTitle().equals("")) {
			this.setConstructionTitle(this.getConstructionTitle());
		} else {
			if (TouchEntryPoint.hasBrowseGUI()) {
				this.setConstructionTitle(TouchEntryPoint.getBrowseGUI()
						.getChosenMaterial().getMaterialTitle());
			}
		}

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				TouchEntryPoint.tabletGUI.updateViewSizes();
			}
		});
		this.touchGUI.resetMode();
	}

	void approveFileName() {
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
	public void evalJavaScript(final App app, final String script,
			final String arg) {

	}

	@Override
	public void evalPythonScript(final App app, final String string,
			final String arg) {

	}

	@Override
	public void exitAll() {
	}

	@Override
	public void fileNew() {
		// FIXME what has to be reset?
		// add missing settings!!!
		this.kernel.clearConstruction(true);
		this.touchGUI.allowEditing(true);
		this.kernel.initUndoInfo();
		setDefaultConstructionTitle();
		getEuclidianView1().reset();
		getEuclidianView1().setBackground(GColor.WHITE);
		getEuclidianView1().setShowAxes(true, true);
		this.touchGUI.resetMode();
		setSaved();
		this.touchGUI.updateViewSizes();

		// this.kernel.clearConstruction(true);
		// this.touchGUI.allowEditing(true);
		// this.kernel.initUndoInfo();
		// this.setDefaultConstructionTitle();
		// this.setLabelingStyle(ConstructionDefaults.LABEL_VISIBLE_POINTS_ONLY);
		// this.settings.beginBatch();
		// this.settings.getEuclidian(1).reset();
		//
		// this.settings.getEuclidian(1).setPointCapturing(
		// EuclidianStyleConstants.POINT_CAPTURING_AUTOMATIC);
		// this.settings.endBatch();
		// this.kernel.notifyRepaint();
	}

	protected void fireTitleChangedEvent(final String title) {
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
		return ((EuclidianViewT) this.getActiveEuclidianView()).getCanvas();
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

	public FileManagerT getFileManager() {
		return this.fm;
	}

	@Override
	protected FontManager getFontManager() {
		return this.fontManager;
	}

	@Override
	public GuiManager getGuiManager() {

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
	protected void getLayoutXML(final StringBuilder sb,
			final boolean asPreference) {
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
	private native String getLocale() /*-{
										var language = window.navigator.systemLanguage
										|| window.navigator.language;
										return language;
										}-*/;

	@Override
	public String getLocaleStr() {
		return this.getLocale();
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
	public String getToolTooltipHTML(final int mode) {
		throw new UnsupportedOperationException();
	}

	public GeoGebraTouchGUI getTouchGui() {
		return this.touchGUI;
	}

	@Override
	public ViewManager getViewManager() {
		return new ViewManagerT();
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
			final geogebra.common.kernel.Kernel kernel1) {
		return null;
	}

	@Override
	protected EuclidianView newEuclidianView(final boolean[] showAxes1,
			final boolean showGrid1) {
		return null;
	}

	/**
	 * @param handler
	 *            handler to be registered
	 */
	public void registerErrorHandler(final ErrorHandler handler) {
		this.errorHandlers.push(handler);
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
	public void setConstructionTitle(final String title) {
		this.kernel.getConstruction().setTitle(title);
		this.isDefaultFileName = false;
		this.fireTitleChangedEvent(title);
	}

	public void setDefaultConstructionTitle() {
		this.setConstructionTitle(this.fm.getDefaultConstructionTitle(this
				.getLocalization()));
		this.isDefaultFileName = true;
	}

	public void setFileManager(final FileManagerT fm) {
		this.fm = fm;
	}

	@Override
	public void setLabels() {
		this.touchGUI.setLabels();
		if (TouchEntryPoint.hasWorksheetGUI()) {
			TouchEntryPoint.getWorksheetGUI().setLabels();
		}
		if (TouchEntryPoint.hasBrowseGUI()) {
			TouchEntryPoint.getBrowseGUI().setLabels();
		}
		// TODO remove
		// this.showError("Labels set");
	}

	public void setLanguage() {
		final String locale = this.getLocaleStr();
		final String language = locale.substring(0, 2);

		String country = "";
		if (locale.contains("-")) {
			country = locale.split("-")[1];
		}

		this.setLanguage(language, country);
	}

	@Override
	public void setShowConstructionProtocolNavigation(final boolean show,
			final boolean playButton, final double playDelay,
			final boolean showProtButton) {

	}

	@Override
	public void setWaitCursor() {

	}

	@Override
	public boolean showAlgebraInput() {
		return false;
	}

	@Override
	public void showCommandError(final String command, final String message) {

	}

	@Override
	public void showError(final String s) {
		if (this.errorHandlers.peek() != null) {
			this.errorHandlers.peek().showError(s);
		}
	}

	@Override
	public void showError(final String key, final String error) {
		showErrorDialog(this.getLocalization().getError(key) + ": " + error);
	}

	@Override
	public void showErrorDialog(final String s) {
		if (this.errorHandlers.peek() != null) {
			this.errorHandlers.peek().showError(s);
		}
	}

	@Override
	public void showLoadingAnimation(final boolean b) {
	}

	@Override
	public void showMessage(final String error) {
	}

	@Override
	public void showRelation(final GeoElement geoElement,
			final GeoElement geoElement2) {

	}

	@Override
	public void showURLinBrowser(final String string) {

	}

	@Override
	public boolean showView(final int view) {
		return false;
	}

	/**
	 * Creates a new {@link Kernel}, a new instance of {@link MyXMLio} and
	 * initializes the components of the {@link GeoGebraTouchGUI}.
	 */
	void start() {
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
		this.initNetworkEventFlow();
	}

	@Override
	public void storeUndoInfo() {
		if (this.isUndoActive()) {
			this.kernel.storeUndoInfo();
		}
		this.setUnsaved();
	}

	/**
	 * @param handler
	 *            handler to be unregistered
	 */
	public void unregisterErrorHandler(final ErrorHandler handler) {
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

	@Override
	public void openMaterial(final String s) {
		TouchEntryPoint.showWorksheetGUI(JSONparserGGT.parseMaterial(s));
	}

	@Override
	public void ensureEditing() {
		TouchEntryPoint.showTabletGUI();
		TouchEntryPoint.allowEditing(true);
	}

	@Override
	public String getDataParamId() {
		return "ggbTouch";
	}

	@Override
	public void doRepaintViews() {
		if (this.euclidianView != null) {
			((EuclidianViewWeb) this.euclidianView).doRepaint2();
		}
		if (this.getAlgebraView() != null) {
			((AlgebraViewWeb) this.getAlgebraView()).doRepaint2();
		}
	}

}
