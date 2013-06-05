package geogebra.web.main;

import geogebra.common.GeoGebraConstants;
import geogebra.common.awt.GDimension;
import geogebra.common.awt.GFont;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.factories.Factory;
import geogebra.common.gui.menubar.MenuInterface;
import geogebra.common.gui.view.algebra.AlgebraView;
import geogebra.common.gui.view.consprotocol.ConstructionProtocolNavigation;
import geogebra.common.javax.swing.GOptionPane;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.App;
import geogebra.common.main.FontManager;
import geogebra.common.main.GeoElementSelectionListener;
import geogebra.common.main.MyError;
import geogebra.common.main.SpreadsheetTableModel;
import geogebra.common.main.settings.Settings;
import geogebra.common.plugin.jython.PythonBridge;
import geogebra.common.util.Language;
import geogebra.common.util.MD5EncrypterGWTImpl;
import geogebra.common.util.StringUtil;
import geogebra.common.util.debug.GeoGebraLogger.LogDestination;
import geogebra.common.util.debug.GeoGebraProfiler;
import geogebra.html5.awt.GDimensionW;
import geogebra.html5.css.GuiResources;
import geogebra.html5.io.MyXMLioW;
import geogebra.html5.js.JavaScriptInjector;
import geogebra.html5.main.AppWeb;
import geogebra.html5.main.FontManagerW;
import geogebra.html5.main.LocalizationW;
import geogebra.html5.main.ViewManager;
import geogebra.html5.sound.SoundManagerW;
import geogebra.html5.util.ArticleElement;
import geogebra.html5.util.MyDictionary;
import geogebra.html5.util.debug.GeoGebraLogger;
import geogebra.web.Web;
import geogebra.web.Web.GuiToLoad;
import geogebra.web.euclidian.EuclidianControllerW;
import geogebra.web.euclidian.EuclidianViewW;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.app.GGWCommandLine;
import geogebra.web.gui.app.GGWMenuBar;
import geogebra.web.gui.app.GGWToolBar;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.gui.applet.GeoGebraFrame;
import geogebra.web.gui.dialog.DialogManagerW;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.infobar.InfoBarW;
import geogebra.web.gui.inputbar.AlgebraInputW;
import geogebra.web.gui.layout.panels.Euclidian2DockPanelW;
import geogebra.web.gui.layout.panels.EuclidianDockPanelW;
import geogebra.web.gui.menubar.GeoGebraMenubarW;
import geogebra.web.gui.menubar.LanguageCommand;
import geogebra.web.gui.tooltip.ToolTipManagerW;
import geogebra.web.gui.view.consprotocol.ConstructionProtocolNavigationW;
import geogebra.web.gui.view.spreadsheet.SpreadsheetTableModelW;
import geogebra.web.helper.MyGoogleApis;
import geogebra.web.helper.MySkyDriveApis;
import geogebra.web.helper.ObjectPool;
import geogebra.web.javax.swing.GCheckBoxMenuItem;
import geogebra.web.javax.swing.GOptionPaneW;
import geogebra.web.javax.swing.GPopupMenuW;
import geogebra.web.kernel.KernelW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.MissingResourceException;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.TextAlign;
import com.google.gwt.canvas.dom.client.Context2d.TextBaseline;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class AppW extends AppWeb {

	public final static String syntaxStr = "_Syntax";
	public static String geoIPCountryName;
	public static String geoIPLanguage;
	
	private HashMap<String, String> englishCommands = null;

	private FontManagerW fontManager;
	private SpreadsheetTableModelW tableModel;
	private GuiManagerW guiManager;
	private SoundManagerW soundManager;
	private geogebra.web.gui.dialog.DialogManagerW dialogManager;
	private ToolTipManagerW toolTipManager;
	
	

	private ArticleElement articleElement;
	private String ORIGINAL_BODY_CLASSNAME = "";

	private GeoGebraFrame  frame = null;
	private GeoGebraAppFrame appFrame = null;
	private EuclidianDockPanelW euclidianViewPanel;
	private Canvas canvas;

	private boolean[] showAxes = { true, true };
	private boolean showGrid = false;

	/**
	 * Preferred application frame size. Used in case frame size needs updating.
	 */
	private GDimension preferredSize = new GDimensionW(0,0);

	boolean menuKeysLoaded = false;
	private ObjectPool objectPool;

	/******************************************************
	 * Constructs AppW for applets with undo enabled
	 * 
	 * @param ae
	 * @param gf
	 */
	public AppW(ArticleElement ae, GeoGebraFrame gf) {
		this(ae, gf, true);
	}

	/******************************************************
	 * Constructs AppW for applets
	 * 
	 * @param undoActive
	 *            if true you can undo by CTRL+Z and redo by CTRL+Y
	 */
	public AppW(ArticleElement ae, GeoGebraFrame gf, final boolean undoActive) {
		this.articleElement = ae;
		this.frame = gf;
		this.objectPool = new ObjectPool();
		setDataParamHeight(frame.getDataParamHeight());
		setDataParamWidth(frame.getDataParamWidth());
		this.useFullGui = ae.getDataParamGui();
		if (ae.getDataParamShowLogging()) {
			startLogger();
		} else {
			// make sure $wnd.console works in IE9
			GeoGebraLogger.initConsole();
		}
		infobar = new InfoBarW(this);

		info("GeoGebra " + GeoGebraConstants.VERSION_STRING + " "
		        + GeoGebraConstants.BUILD_DATE + " "
		        + Window.Navigator.getUserAgent());
		initCommonObjects();

		initing = true;

		this.euclidianViewPanel = new EuclidianDockPanelW(this, false);
		//(EuclidianDockPanelW)getGuiManager().getLayout().getDockManager().getPanel(App.VIEW_EUCLIDIAN);
		this.canvas = this.euclidianViewPanel.getCanvas();
		canvas.setWidth("1px");
		canvas.setHeight("1px");
		canvas.setCoordinateSpaceHeight(1);
		canvas.setCoordinateSpaceWidth(1);

		initCoreObjects(undoActive, this);

		removeDefaultContextMenu(this.getArticleElement());
	}

	/********************************************************
	 * Constructs AppW for full GUI based GeoGebraWeb
	 * 
	 * @param article
	 * @param geoGebraAppFrame
	 * @param undoActive
	 */
	public AppW(ArticleElement article, GeoGebraAppFrame geoGebraAppFrame,
	        boolean undoActive) {
		this.articleElement = article;
		this.appFrame = geoGebraAppFrame;
		this.objectPool = new ObjectPool();
		this.objectPool.setMyGoogleApis(new MyGoogleApis(this));
		this.objectPool.setMySkyDriveApis(new MySkyDriveApis(this));
		createAppSplash();
		App.useFullAppGui = true;
		appCanvasHeight = appFrame.getCanvasCountedHeight();
		appCanvasWidth = appFrame.getCanvasCountedWidth();

		setCurrentFileId();
		if (article.getDataParamShowLogging()) {
			startLogger();
		} else {
			// make sure $wnd.console works in IE9
			GeoGebraLogger.initConsole();
		}
		infobar = new InfoBarW(this);

		initCommonObjects();

		this.euclidianViewPanel = appFrame.getEuclidianView1Panel();
		this.canvas = euclidianViewPanel.getCanvas();

		initCoreObjects(undoActive, this);

		// initing = true;

		removeDefaultContextMenu();
	}

	/*************************************************
	 * Constructs AppW for full GUI based GeoGebraWeb with undo enabled
	 * 
	 * @param article
	 * @param geoGebraAppFrame
	 */
	public AppW(ArticleElement article, GeoGebraAppFrame geoGebraAppFrame) {
		this(article, geoGebraAppFrame, true);
	}

	// ========================================================
	// INITIALIZING
	// ========================================================

	/**
	 * Initializes the application, seeds factory prototypes, creates Kernel and
	 * MyXMLIO
	 * 
	 * @param undoActive
	 */
	public void init(final boolean undoAct) {
		initCommonObjects();
	}

	private void initCommonObjects() {
		initFactories();
		geogebra.common.factories.UtilFactory.prototype = new geogebra.web.factories.UtilFactoryW();
		geogebra.common.factories.Factory
		        .setPrototype(new geogebra.web.factories.FactoryW());
		// App.initializeSingularWS();

		// neded to not overwrite anything already exists
		ORIGINAL_BODY_CLASSNAME = RootPanel.getBodyElement().getClassName();
	}

	private void showSplashImageOnCanvas() {
		if (this.canvas != null) {
			String geogebra = "GeoGebra";

			canvas.setWidth("427px");
			canvas.setHeight("120px");
			canvas.setCoordinateSpaceWidth(427);
			canvas.setCoordinateSpaceHeight(120);
			Context2d ctx = canvas.getContext2d();
			ctx.clearRect(0, 0, canvas.getCoordinateSpaceWidth(),
			        canvas.getCoordinateSpaceHeight());
			ctx.setTextBaseline(TextBaseline.TOP);
			ctx.setTextAlign(TextAlign.START);
			ctx.setFont("50px Century Gothic, Helvetica, sans-serif");
			ctx.setFillStyle("#666666");
			ctx.fillText(geogebra, 33, 37);
			// TextMetrics txm = ctx.measureText(geogebra);
			// ctx.setFillStyle("#7e7eff");
			// ctx.setTextAlign(TextAlign.LEFT);
			// ctx.setFont("20px Century Gothic, Helvetica, sans-serif");
			// ctx.fillText("4",txm.getWidth(),37);
		}
	}

	/**
	 * Initializes Kernel, EuclidianView, EuclidianSettings, etc..
	 * 
	 * @param undoActive
	 * @param this_app
	 */
	void initCoreObjects(final boolean undoActive, final App this_app) {

		kernel = new KernelW(this_app);

		// init settings
		settings = new Settings();

		myXMLio = new MyXMLioW(kernel, kernel.getConstruction());

		fontManager = new FontManagerW();
		setFontSize(12);
		initEuclidianViews();

		initImageManager();

		setFontSize(12);
		// setLabelDragsEnabled(false);
		capturingThreshold = 20;

		// make sure undo allowed
		hasFullPermissions = true;

		getScriptManager();// .ggbOnInit();//this is not called here because we
		                   // have to delay it
		                   // until the canvas is first drawn

		setUndoActive(undoActive);
		registerFileDropHandlers((CanvasElement) canvas.getElement().cast());
		afterCoreObjectsInited();

	}

	private void afterCoreObjectsInited() {
		if (appFrame != null) {
			initGuiManager();
			getGuiManager().getLayout().setPerspectives(tmpPerspectives);

			getSettings().getEuclidian(1).setPreferredSize(
			        geogebra.common.factories.AwtFactory.prototype
			                .newDimension(appCanvasWidth, appCanvasHeight));
			getEuclidianView1().synCanvasSize();
			getEuclidianView1().doRepaint2();
			stopCollectingRepaints();
			appFrame.finishAsyncLoading(articleElement, appFrame, this);
		} else if (frame != null) {

			// Code to run before buildApplicationPanel
			initGuiManager();
			getGuiManager().getLayout().setPerspectives(tmpPerspectives);
			// Code to have run before buildApplicationPanel

			GeoGebraFrame.finishAsyncLoading(articleElement, frame, this);
			initing = false;
		}
	}

	private static void startLogger() {
		logger = new GeoGebraLogger();
		logger.setLogDestination(LogDestination.CONSOLES);
		logger.setLogLevel(Window.Location.getParameter("logLevel"));
	}

	// ========================================================
	// Getters/Setters
	// ========================================================

	@Override
	public boolean isHTML5Applet() {
		return true;
	}

	@Override
	public String getVersionString() {
		return super.getVersionString() + "-HTML5";
	}

	public ArticleElement getArticleElement() {
		return articleElement;
	}

	public GeoGebraFrame getGeoGebraFrame() {
		return frame;
	}

	@Override
	public boolean isApplet() {
		return !GuiToLoad.APP.equals(Web.currentGUI);
	}

	@Override
	public boolean isUsingFullGui() {
		// return useFullGui;
		// TODO
		return guiManager != null;
	}

	@Override
	public GuiManagerW getGuiManager() {
		if (guiManager == null) {
			// TODO: add getGuiManager(), see #1783
			if (getUseFullGui() || showToolBar) {
				guiManager = new GuiManagerW(this);
			}
		}

		return guiManager;
	}



	@Override
    public Canvas getCanvas() {
		return canvas;
	}

	@Override
	protected EuclidianView newEuclidianView(boolean[] showAxes,
	        boolean showGrid) {
		return euclidianView = new EuclidianViewW(euclidianViewPanel,
		        euclidianController, showAxes, showGrid, 1, getSettings()
		                .getEuclidian(1));
	}

	@Override
	protected EuclidianController newEuclidianController(Kernel kernel) {
		return new EuclidianControllerW(kernel);

	}

	@Override
	public SpreadsheetTableModel getSpreadsheetTableModel() {
		if (tableModel == null) {
			tableModel = new SpreadsheetTableModelW(this, SPREADSHEET_INI_ROWS,
			        SPREADSHEET_INI_COLS);
		}
		return tableModel;
	}

	@Override
	public geogebra.common.main.DialogManager getDialogManager() {
		if (dialogManager == null) {
			dialogManager = new DialogManagerW(this);
		}
		return dialogManager;
	}

	@Override
	public Factory getFactory() {
		return Factory.getPrototype();
	}

	

	public ToolTipManagerW getToolTipManager(){
		if(toolTipManager == null){
		toolTipManager = new ToolTipManagerW(this);
		}
		return toolTipManager;
	}

	// ========================================================
	// Undo/Redo
	// ========================================================

	@Override
	public void setUndoActive(boolean flag) {
		// don't allow undo when running with restricted permissions
		/*
		 * if (flag && !hasFullPermissions) { flag = false; }
		 */

		if (kernel.isUndoActive() == flag) {
			return;
		}

		kernel.setUndoActive(flag);
		if (flag) {
			kernel.initUndoInfo();
		}

		if (guiManager != null) {
			getGuiManager().updateActions();
		}

		// isSaved = true;
	}

	@Override
	public void storeUndoInfo() {
		if (isUndoActive()) {
			kernel.storeUndoInfo();
			// isSaved = false;
		}
	}

	public void restoreCurrentUndoInfo() {
		if (isUndoActive()) {
			kernel.restoreCurrentUndoInfo();
			// isSaved = false;
		}
	}

	// ===================================================
	// Views
	// ===================================================

	public EuclidianDockPanelW getEuclidianViewpanel() {
		return euclidianViewPanel;
	}

	@Override
	public boolean hasEuclidianView2EitherShowingOrNot() {
		return (guiManager != null) &&
			getGuiManager().hasEuclidianView2EitherShowingOrNot();
	}

	public boolean hasEuclidianView2() {
		return (guiManager != null) && getGuiManager().hasEuclidianView2();
	}

	@Override
	public EuclidianViewW getEuclidianView2() {
		return (EuclidianViewW)getGuiManager().getEuclidianView2();
	}

	@Override
	public EuclidianViewInterfaceCommon getActiveEuclidianView() {
		if (getGuiManager() == null) {
			return getEuclidianView1();
		}
		return (EuclidianViewW) getGuiManager().getActiveEuclidianView();
	}

	@Override
	public boolean isShowingEuclidianView2() {
		return (guiManager != null) && getGuiManager().hasEuclidianView2()
			&& getGuiManager().getEuclidianView2().isShowing();
	}

	@Override
	public EuclidianViewW createEuclidianView() {
		return (EuclidianViewW) this.euclidianView;
	}

	@Override
	public AlgebraView getAlgebraView() {
		return getGuiManager().getAlgebraView();
		// if (guiManager == null) {
		// return null;
		// }
		// return guiManager.getAlgebraView();
	}

	@Override
	public boolean showView(int view) {
		return getGuiManager().showView(view);
	}

	private void attachViews() {
		if (!getGuiManager().getAlgebraView().isAttached())
			getGuiManager().attachView(VIEW_ALGEBRA);

		if (needsSpreadsheetTableModel())
			getSpreadsheetTableModel();// its constructor calls attachView as a
		// side-effect
		// Attached only on first click
		// getGuiManager().attachView(VIEW_PROPERTIES);
	}

	// ========================================================
	// Languages
	// ========================================================

	private static ArrayList<String> supportedLanguages = null;

	/**
	 * @return ArrayList of languages suitable for GWT, eg "en", "de_AT"
	 */
	public static ArrayList<String> getSupportedLanguages() {

		if (supportedLanguages != null) {
			return supportedLanguages;
		}

		supportedLanguages = new ArrayList<String>();

		Language[] languages = Language.values();

		for (int i = 0; i < languages.length; i++) {

			Language language = languages[i];

			if (language.fullyTranslated || GeoGebraConstants.IS_PRE_RELEASE) {
				supportedLanguages.add(language.localeGWT);
			}
		}

		return supportedLanguages;

	}

	/**
	 * This method was supposed to change the initial language depending on the
	 * GeoIP of the user-agent.
	 */
	public void initializeLanguage() {

		// App.debug("GeoIP Country: " + AppW.geoIPCountryName);
		// App.debug("GeoIP Language: " + AppW.geoIPLanguage);
		//
		// App.debug("Test closeset language: " +
		// Language.getClosestGWTSupportedLanguage(AppW.geoIPLanguage));

		// initially change the language to a one that comes from GeoIP.
		setDefaultLanguage();
	}

	/**
		 * 
		 */
	public void setDefaultLanguage() {
		// App.debug("Browser Language: " + AppW.geoIPLanguage);

		String[] localeNames = LocaleInfo.getAvailableLocaleNames();
		for (int i = 0; i < localeNames.length; i++) {
			App.debug("Locale Name: " + localeNames[i]);
		}

		String lCookieName = LocaleInfo.getLocaleCookieName();
		String lCookieValue = null;
		if (lCookieName != null) {
			lCookieValue = Cookies.getCookie(lCookieName);
		}
		String currentLanguage = LocaleInfo.getCurrentLocale().getLocaleName();
		String closestlangcodetoGeoIP = Language
		        .getClosestGWTSupportedLanguage(AppW.geoIPLanguage);

		App.debug("Cookie Value: " + lCookieValue + ", currentLanguage: "
		        + currentLanguage + ", Language from GeoIP: "
		        + AppW.geoIPLanguage + ", closest Language from GeoIP: "
		        + closestlangcodetoGeoIP);

		if (Language.isEnabledInGWT(closestlangcodetoGeoIP)) {

			App.debug("Language is enabeled!!!");

			if (lCookieValue == null
			        && currentLanguage != closestlangcodetoGeoIP
			        && !LocalizationW.DEFAULT_LANGUAGE.equals(currentLanguage)) {

				App.debug("Changing Language depending on GeoIP!");

				// Window.Location.assign( // or replace()
				// Window.Location.createUrlBuilder()
				// .setParameter(LocaleInfo.getLocaleQueryParam(), "ar")
				// .buildString());

				UrlBuilder newUrl = Window.Location.createUrlBuilder();
				newUrl.setParameter(LanguageCommand.LOCALE_PARAMETER,
				        closestlangcodetoGeoIP);
				Window.Location.assign(newUrl.buildString());

				Cookies.removeCookie(lCookieName);
				Cookies.setCookie(lCookieName, closestlangcodetoGeoIP);

			}

		}

	}

	@Override
	public String getLocaleStr() {
		String localeName = LocaleInfo.getCurrentLocale().getLocaleName();
		App.trace("Current Locale: " + localeName);

		if (localeName.toLowerCase().equals(LocalizationW.DEFAULT_LOCALE)) {
			return LocalizationW.DEFAULT_LANGUAGE;
		}
		return localeName.substring(0, 2);
	}

	@Override
    public String getLanguageFromCookie() {
		return Cookies.getCookie("GGWlang");
	}

	

	public void setLabels() {
		if (initing) {
			return;
		}
		if (guiManager != null) {
			getGuiManager().setLabels();
		}
		// if (rbplain != null) {
		kernel.updateLocalAxesNames();
		// }
		updateCommandDictionary();
	}

	/**
	 * This method is used for debugging purposes:
	 */
	public static void displaySupportedLocales() {
		String[] localeNames = LocaleInfo.getAvailableLocaleNames();
		for (int i = 0; i < localeNames.length; i++) {
			App.debug("GWT Module Supported Locale no." + i + ", Locale Name: "
			        + localeNames[i]);
		}
	}

	/**
	 * This method is used for debugging purposes:
	 */
	public static void displayLocaleCookie() {
		App.debug("Locale Cookie Name: " + LocaleInfo.getLocaleCookieName()
		        + ", Cookie Value: "
		        + Cookies.getCookie(LocaleInfo.getLocaleCookieName()));
	}

	@Override
	public boolean letRedefine() {
		// AbstractApplication.debug("implementation needed"); // TODO
		// Auto-generated
		return true;
	}

	MyDictionary commandDictionary = null;

	private MyDictionary getCommandDict() {
		if (commandDictionary == null) {
			try {
				// commandDictionary =
				// Dictionary.getDictionary("__GGB__dictionary_"+language);
				commandDictionary = MyDictionary.getDictionary("command",
				        getLocalization().getLanguage());
			} catch (MissingResourceException e) {
				// commandDictionary =
				// Dictionary.getDictionary("__GGB__dictionary_en");
				commandDictionary = MyDictionary.getDictionary("command", "en");
				App.error("Missing Dictionary " + getLocalization().getLanguage());
			}
		}

		return commandDictionary;

	}

	@Override
	public String getCountryFromGeoIP() {
		// warn("unimplemented");

		App.debug("GeoIPCountry: " + AppW.geoIPCountryName);
		App.debug("GeoIPLanguage: " + AppW.geoIPLanguage);
		return AppW.geoIPCountryName;
	}

	// ========================================================
	// FILE HANDLING
	// ========================================================

	

	private String driveBase64Content = null;
	private String driveBase64description = null;
	private String driveBase64FileName = null;
	/**
	 * static because it gets from server side, either "" or the set filename
	 */
	public String currentFileId = null;

	
	public void refreshCurrentFileDescriptors(String fName, String desc,
	        String fileCont) {
		driveBase64Content = fileCont;
		driveBase64description = desc;
		driveBase64FileName = fName;
		((DialogManagerW) getDialogManager())
		        .refreshAndShowCurrentFileDescriptors(driveBase64FileName,
		                driveBase64description);

	}

	public String getFileName() {
		return driveBase64FileName;
	}

	public String getFileDescription() {
		return driveBase64description;
	}

	private native void setCurrentFileId() /*-{
		if ($wnd.GGW_appengine) {
			this.@geogebra.web.main.AppW::currentFileId = $wnd.GGW_appengine.FILE_IDS[0];
		}
	}-*/;

	

	
	
	@Override
    protected void resetStorageInfo(){
		driveBase64FileName = null;
		driveBase64description = null;
		driveBase64Content = null;
		currentFileId = "";
		((DialogManagerW) getDialogManager())
		        .refreshAndShowCurrentFileDescriptors(driveBase64FileName,
		                driveBase64description);
	}
	
	protected void clearInputBar(){
		if (isUsingFullGui() && showAlgebraInput()) {
			AlgebraInputW ai = (getGuiManager().getAlgebraInput());
			ai.clear();
		}
	}
	
	

	/**
	 * Opens the image file
	 * 
	 * @param fileToHandle
	 * @param callback
	 * @return returns true, if fileToHandle image file, otherwise return false.
	 *         Note that If the function returns true, it's don't mean, that the
	 *         file opening was successful, and the opening finished already.
	 */
	public native boolean openFileAsImage(JavaScriptObject fileToHandle,
	        JavaScriptObject callback) /*-{
		var imageRegEx = /\.(png|jpg|jpeg|gif|bmp)$/i;
		if (!fileToHandle.name.toLowerCase().match(imageRegEx))
			return false;

		var appl = this;
		var reader = new FileReader();
		reader.onloadend = function(ev) {
			if (reader.readyState === reader.DONE) {
				var reader2 = new FileReader();
				var base64result = reader.result;
				reader2.onloadend = function(eev) {
					if (reader2.readyState === reader2.DONE) {
						var fileStr = base64result;
						var fileStr2 = reader2.result;
						var fileName = fileToHandle.name;
						appl.@geogebra.web.main.AppW::imageDropHappened(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lgeogebra/common/kernel/geos/GeoPoint;)(fileName, fileStr, fileStr2, null);
						if (callback != null)
							callback();
					}
				}
				reader2.readAsBinaryString(fileToHandle);
			}
		};
		reader.readAsDataURL(fileToHandle);
		return true;
	}-*/;

	/**
	 * Register file drop handlers for the canvas of this application
	 */
	native void registerFileDropHandlers(CanvasElement ce) /*-{

		var appl = this;
		var canvas = ce;

		if (canvas) {
			canvas.addEventListener("dragover", function(e) {
				e.preventDefault();
				e.stopPropagation();
				canvas.style.borderColor = "#ff0000";
			}, false);
			canvas.addEventListener("dragenter", function(e) {
				e.preventDefault();
				e.stopPropagation();
			}, false);
			canvas
					.addEventListener(
							"drop",
							function(e) {
								e.preventDefault();
								e.stopPropagation();
								canvas.style.borderColor = "#000000";
								var dt = e.dataTransfer;
								if (dt.files.length) {
									var fileToHandle = dt.files[0];

									//at first this tries to open the fileToHandle as image,
									//if fileToHandle not an image, this will try to open as ggb or ggt.
									if (!appl.@geogebra.web.main.AppW::openFileAsImage(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(fileToHandle, null)) {
										appl.@geogebra.web.main.AppW::openFileAsGgb(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(fileToHandle, null);
									}

									//console.log(fileToHandle.name);
								} else {
									// This would raise security exceptions later - see ticket #2301
									//var gdat = dt.getData("URL");
									//if (gdat && gdat != " ") {
									//	var coordx = e.offsetX ? e.offsetX : e.layerX;
									//	var coordy = e.offsetY ? e.offsetY : e.layerY;
									//	appl.@geogebra.web.main.AppW::urlDropHappened(Ljava/lang/String;II)(gdat, coordx, coordy);
									//}
								}
							}, false);
		}
		$doc.body.addEventListener("dragover", function(e) {
			e.preventDefault();
			e.stopPropagation();
			if (canvas)
				canvas.style.borderColor = "#000000";
		}, false);
		$doc.body.addEventListener("drop", function(e) {
			e.preventDefault();
			e.stopPropagation();
		}, false);
	}-*/;

	/**
	 * Loads an image and puts it on the canvas (this happens on webcam input)
	 * On drag&drop or insert from URL this would be called too, but that would
	 * set security exceptions
	 * 
	 * @param url
	 *            - the data url of the image
	 * @param clientx
	 *            - desired position on the canvas (x) - unused
	 * @param clienty
	 *            - desired position on the canvas (y) - unused
	 */
	public void urlDropHappened(String url, int clientx, int clienty) {

		// Filename is temporarily set until a better solution is found
		// TODO: image file name should be reset after the file data is
		// available

		MD5EncrypterGWTImpl md5e = new MD5EncrypterGWTImpl();
		String zip_directory = md5e.encrypt(url);

		// with dummy extension, maybe gif or jpg in real
		String imgFileName = zip_directory + ".png";

		String fn = imgFileName;
		int index = imgFileName.lastIndexOf('/');
		if (index != -1) {
			fn = fn.substring(index + 1, fn.length()); // filename without
		}
		// path
		fn = geogebra.common.util.Util.processFilename(fn);

		// filename will be of form
		// "a04c62e6a065b47476607ac815d022cc\liar.gif"
		imgFileName = zip_directory + '/' + fn;

		doDropHappened(imgFileName, url, null);
	}

	/**
	 * Loads an image and puts it on the canvas (this happens by drag & drop)
	 * 
	 * @param imgFileName
	 *            - the file name of the image
	 * @param fileStr
	 *            - the image data url
	 * @param fileStr2
	 *            - the image binary string
	 * @param clientx
	 *            - desired position on the canvas (x)
	 * @param clienty
	 *            - desired position on the canvas (y)
	 */
	public void imageDropHappened(String imgFileName, String fileStr,
	        String fileStr2, GeoPoint loc) {

		MD5EncrypterGWTImpl md5e = new MD5EncrypterGWTImpl();
		String zip_directory = md5e.encrypt(fileStr2);

		String fn = imgFileName;
		int index = imgFileName.lastIndexOf('/');
		if (index != -1) {
			fn = fn.substring(index + 1, fn.length()); // filename without
		}
		// path
		fn = geogebra.common.util.Util.processFilename(fn);

		// filename will be of form
		// "a04c62e6a065b47476607ac815d022cc\liar.gif"
		imgFileName = zip_directory + '/' + fn;

		doDropHappened(imgFileName, fileStr, loc);
	}

	private void doDropHappened(String imgFileName, String fileStr, GeoPoint loc) {

		Construction cons = getKernel().getConstruction();
		EuclidianViewInterfaceCommon ev = getActiveEuclidianView();
		getImageManager().addExternalImage(imgFileName,
		        fileStr);
		GeoImage geoImage = new GeoImage(cons);
		getImageManager().triggerSingleImageLoading(
		        imgFileName, geoImage);
		geoImage.setImageFileName(imgFileName);

		if (loc == null) {
			double cx = ev.getXmin() + (ev.getXmax() - ev.getXmin()) / 4;
			double cy = ev.getYmin() + (ev.getYmax() - ev.getYmin()) / 4;
			GeoPoint gsp = new GeoPoint(cons, cx, cy, 1);
			gsp.setLabel(null);
			gsp.setLabelVisible(false);
			gsp.update();
			geoImage.setCorner(gsp, 0);

			cx = ev.getXmax() - (ev.getXmax() - ev.getXmin()) / 4;
			GeoPoint gsp2 = new GeoPoint(cons, cx, cy, 1);
			gsp2.setLabel(null);
			gsp2.setLabelVisible(false);
			gsp2.update();
			geoImage.setCorner(gsp2, 1);
		} else {
			geoImage.setCorner(loc, 0);
		}

		geoImage.setLabel(null);
		GeoImage.updateInstances();

		// these things are done in Desktop GuiManager.loadImage too
		GeoElement[] geos = { geoImage };
		getActiveEuclidianView().getEuclidianController().clearSelections();
		getActiveEuclidianView().getEuclidianController()
		        .memorizeJustCreatedGeos(geos);
		setDefaultCursor();
	}

	@Override
    public void appSplashCanNowHide() {
		if (splashDialog != null) {
			splashDialog.canNowHide();
			attachViews();
		}

		// Well, it may cause freeze if we attach this too early

		// allow eg ?command=A=(1,1);B=(2,2) in URL
		String cmd = com.google.gwt.user.client.Window.Location
		        .getParameter("command");

		if (cmd != null) {

			App.debug("exectuing commands: " + cmd);

			String[] cmds = cmd.split(";");
			for (int i = 0; i < cmds.length; i++) {
				getKernel().getAlgebraProcessor()
				        .processAlgebraCommandNoExceptionsOrErrors(cmds[i],
				                false);
			}
		}

	}

	

	// ================================================
	// ERROR HANDLING
	// ================================================

	@Override
	public void showError(MyError e) {
		final String command = e.getcommandName();

		// TODO
		App.debug("TODO later: make sure splash screen not showing");

		if (command == null) {
			showErrorDialog(e.getLocalizedMessage());
			return;
		}

		final PopupPanel dialog = new PopupPanel(false, true);

		Button ok = new Button(getPlain("OK"));
		ok.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialog.removeFromParent();
			}
		});
		Button showHelp = new Button(getPlain("ShowOnlineHelp"));
		showHelp.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				getGuiManager().openCommandHelp(command);
				dialog.removeFromParent();
			}
		});

		FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.addStyleName("DialogButtonPanel");
		buttonPanel.add(ok);
		buttonPanel.add(showHelp);

		VerticalPanel panel = new VerticalPanel();
		String[] lines = e.getLocalizedMessage().split("\n");
		for (String item : lines) {
			panel.add(new Label(item));
		}

		panel.add(buttonPanel);
		dialog.setWidget(panel);
		dialog.center();
		dialog.show();
		ok.getElement().focus();

	}

	@Override
	public void showError(String key, String error) {
		showErrorDialog(getLocalization().getError(key) + ":\n" + error);
	}

	@Override
    public void showMessage(final String message) {
		App.printStacktrace("showMessage: " + message);
		GOptionPaneW.INSTANCE.showConfirmDialog(null, message,
		        getPlain("ApplicationName") + " - " + getMenu("Info"),
		        GOptionPane.DEFAULT_OPTION, 0);
	}

	@Override
	public void showErrorDialog(final String msg) {
		final PopupPanel dialog = new PopupPanel(false, true);
		// dialog.setText(getPlain("ApplicationName") + " - " +
		// getMenu("Info"));

		GOptionPaneW.INSTANCE.showConfirmDialog(null, msg,
		        getPlain("ApplicationName") + " - "
		                + getLocalization().getError("Error"),
		        GOptionPane.DEFAULT_OPTION, 0);
	}

	@Override
	public void showError(String s) {
		showErrorDialog(s);
	}

	@Override
	public boolean freeMemoryIsCritical() {
		// can't access available memory etc from JavaScript
		return false;
	}

	@Override
	public long freeMemory() {
		return 0;
	}
	
	@Override
	public void evalJavaScript(App app, String script, String arg) {

		// TODO: maybe use sandbox?

		String ggbApplet = getDataParamId();
		script = "ggbApplet = document." + ggbApplet + ";" + script;

		// script = "ggbApplet = document.ggbApplet;"+script;

		// add eg arg="A"; to start
		if (arg != null) {
			script = "arg=\"" + arg + "\";" + script;
		}

		evalScriptNative(script);
	}

	
	public static int getAbsoluteLeft(Element element){
		return element.getAbsoluteLeft();
	}

	public static int getAbsoluteRight(Element element){
		return element.getAbsoluteRight();
	}

	public static int getAbsoluteTop(Element element){
		return element.getAbsoluteTop();
	}

	public static int getAbsoluteBottom(Element element){
		return element.getAbsoluteBottom();
	}
	
	public static native void removeDefaultContextMenu(Element element) /*-{
		
		function eventOnElement(e){
			
			x1 = @geogebra.web.main.AppW::getAbsoluteLeft(Lcom/google/gwt/dom/client/Element;)(element);
			x2 = @geogebra.web.main.AppW::getAbsoluteRight(Lcom/google/gwt/dom/client/Element;)(element);
			y1 = @geogebra.web.main.AppW::getAbsoluteTop(Lcom/google/gwt/dom/client/Element;)(element);
			y2 = @geogebra.web.main.AppW::getAbsoluteBottom(Lcom/google/gwt/dom/client/Element;)(element);
		
			if((e.pageX < x1) || (e.pageX > x2) ||
				(e.pageY < y1) || (e.pageY > y2)){
					return false;
				}
			return true;
		}
		
		if ($doc.addEventListener) {
			$doc.addEventListener('contextmenu', function(e) {
				if (eventOnElement(e)) e.preventDefault();
			}, false);
		} else {
			$doc.attachEvent('oncontextmenu', function() {
				if (eventOnElement(e)) window.event.returnValue = false;
			});
		}
	}-*/;

	public static native void removeDefaultContextMenu() /*-{
	
	if ($doc.addEventListener) {
		$doc.addEventListener('contextmenu', function(e) {
			e.preventDefault();
		}, false);
	} else {
		$doc.attachEvent('oncontextmenu', function() {
			window.event.returnValue = false;
		});
	}
}-*/;

	
	public native String getNativeEmailSet() /*-{
		if ($wnd.GGW_appengine) {
			return $wnd.GGW_appengine.USER_EMAIL;
		} else
			return "";
	}-*/;

	public void attachNativeLoadHandler(ImageElement img) {
		addNativeLoadHandler(img, getGuiManager().getActiveEuclidianView());
	}

	private native void addNativeLoadHandler(ImageElement img,
	        EuclidianView view) /*-{
		img.addEventListener("load", function() {
			view.@geogebra.web.euclidian.EuclidianViewW::updateBackground()();
		});
	}-*/;

	public static native void console(JavaScriptObject dataAsJSO) /*-{
		@geogebra.common.main.App::debug(Ljava/lang/String;)(dataAsJSO);
	}-*/;

	// ============================================
	// LAYOUT & GUI UPDATES
	// ============================================

	@Override
	public boolean showAlgebraInput() {
		App.debug("showAlgebraInput: implementation needed"); // TODO
		                                                      // Auto-generated
		return false;
	}

	@Override
	public double getWidth() {
		if (canvas == null)
			return 0;
		return canvas.getCanvasElement().getWidth();
	}

	@Override
	public double getHeight() {
		if (canvas == null)
			return 0;
		return canvas.getCanvasElement().getHeight();
	}

	@Override
	public void updateMenubar() {
		// getGuiManager().updateMenubar();
		App.debug("AppW.updateMenubar() - implementation needed - just finishing"); // TODO
		// Auto-generated
	}

	@Override
	public void updateStyleBars() {

		if (!isUsingFullGui() || isIniting()) {
			return;
		}

		if (getEuclidianView1().hasStyleBar()) {
			getEuclidianView1().getStyleBar().updateStyleBar();
		}

		if (hasEuclidianView2() && getEuclidianView2().hasStyleBar()) {
			getEuclidianView2().getStyleBar().updateStyleBar();
		}
	}

	
	
	public static Widget getRootComponent(AppW app) {
		return app.getGuiManager().getLayout().getRootComponent();
	}
	
	public void updateCenterPanel(boolean updateUI) {
		LayoutPanel centerPanel = null;
		
		if (isUsingFullGui()) {
			appFrame.setFrameLayout();
		} else {
			//TODO: handle applets?
			//centerPanel.add(this.getEuclidianViewpanel());
		}

		appFrame.onResize();
		
		if (updateUI) {
			//SwingUtilities.updateComponentTreeUI(centerPanel);
		}
	}

	private Widget oldSplitLayoutPanel = null;	// just a technical helper variable
	
	public void buildApplicationPanel() {

		if (showMenuBar) {
			attachMenubar();
		}

		if (showToolBar) {
			attachToolbar();
		}

		attachSplitLayoutPanel();

		if (showAlgebraInput) {
			attachAlgebraInput();
		}
	}

	public void refreshSplitLayoutPanel() {
		if (frame != null && frame.getWidgetCount() != 0 &&
			frame.getWidgetIndex(getSplitLayoutPanel()) == -1 &&
			frame.getWidgetIndex(oldSplitLayoutPanel) != -1) {
			int wi = frame.getWidgetIndex(oldSplitLayoutPanel);
			frame.insert(getSplitLayoutPanel(), wi);
			frame.remove(oldSplitLayoutPanel);
			oldSplitLayoutPanel = getSplitLayoutPanel();
			removeDefaultContextMenu(getSplitLayoutPanel().getElement());
		}
	}

	public void attachAlgebraInput() {
		GGWCommandLine inputbar = new GGWCommandLine();
		inputbar.attachApp(this);
		frame.add(inputbar);
	}

	public void attachMenubar() {
		GGWMenuBar menubar = new GGWMenuBar();
		menubar.init(this);
		frame.add(menubar);
		objectPool.setGgwMenubar(menubar);
	}

	private GGWToolBar ggwToolBar = null;

	public void attachToolbar() {
		ggwToolBar = new GGWToolBar();
		ggwToolBar.init(this);
		frame.add(ggwToolBar);
	}

	public GGWToolBar getAppletGGWToolbar() {
		return ggwToolBar;
	}

	public void attachSplitLayoutPanel() {
		frame.add(oldSplitLayoutPanel = getSplitLayoutPanel());
		removeDefaultContextMenu(getSplitLayoutPanel().getElement());
	}

	public Widget getSplitLayoutPanel() {
		if (getGuiManager() == null)
			return null;
		if (getGuiManager().getLayout() == null)
			return null;
		return getGuiManager().getLayout().getRootComponent();
	}

	@Override
    public void syncAppletPanelSize(int width, int height, int evno) {
		if (!isFullAppGui()) {
			if (evno == 1) {
				// this should follow the resizing of the EuclidianView
				int widthDiff = width - euclidianViewPanel.getOffsetWidth();
				euclidianViewPanel.setPixelSize(width, height);//provided there is no style bar
				if (getSplitLayoutPanel() != null)
					getSplitLayoutPanel().setPixelSize(
						getSplitLayoutPanel().getOffsetWidth() + widthDiff, height);
			} else if (evno == 2) {// or the EuclidianView 2
				Euclidian2DockPanelW ew = (Euclidian2DockPanelW)
					getGuiManager().getLayout().getDockManager().getPanel(App.VIEW_EUCLIDIAN2);
				int widthDiff = width - ew.getOffsetWidth();
				ew.setPixelSize(width, height);
				if (getSplitLayoutPanel() != null)
					getSplitLayoutPanel().setPixelSize(
						getSplitLayoutPanel().getOffsetWidth() + widthDiff, height);
			}
		}
	}

	/**
	 * @param ggwGraphicsViewWidth
	 * 
	 *            Resets the width of the Canvas converning the Width of its
	 *            wrapper (splitlayoutpanel center)
	 */
	public void ggwGraphicsViewDimChanged(int width, int height) {
		getSettings().getEuclidian(1).setPreferredSize(
		        geogebra.common.factories.AwtFactory.prototype.newDimension(
		                width, height));

		// simple setting temp.
		appCanvasHeight = height;
		appCanvasWidth = width;

		getEuclidianView1().synCanvasSize();
		getEuclidianView1().doRepaint2();
		stopCollectingRepaints();
		((EuclidianControllerW) getActiveEuclidianView()
		        .getEuclidianController()).updateOffsets();
	}

	/**
	 * Resets the width of the Canvas converning the Width of its
	 * wrapper (splitlayoutpanel center)
	 *
	 * @param width, height
	 */
	public void ggwGraphicsView2DimChanged(int width, int height) {
		getSettings().getEuclidian(2).setPreferredSize(
			geogebra.common.factories.AwtFactory.prototype.newDimension(
				width, height));

		// simple setting temp.
		//appCanvasHeight = height;
		//appCanvasWidth = width;

		getEuclidianView2().synCanvasSize();
		getEuclidianView2().doRepaint2();
		stopCollectingRepaints();
		((EuclidianControllerW) getEuclidianView2()
			.getEuclidianController()).updateOffsets();
	}

	public void updateToolBar() {
		if (!showToolBar || isIniting()) {
			return;
		}

		getGuiManager().updateToolbar();

		setMoveMode();
	}

	@Override
	public void updateApplicationLayout() {
		App.debug("updateApplicationLayout: Implementation needed...");
	}

	public void setShowInputHelpPanel(boolean b) {
		App.debug("setShowInputHelpPanel: Implementation needed...");
	}

	@Override
	public void setShowToolBar(boolean toolbar, boolean help) {
		if (toolbar) {
			JavaScriptInjector.inject(GuiResources.INSTANCE.propertiesKeysJS()
			        .getText());
		}
		super.setShowToolBar(toolbar, help);
	}

	public void setShowAxesSelected(GCheckBoxMenuItem mi) {
		// GeoGebraMenubarW.setMenuSelected(mi, getGuiManager()
		// .getActiveEuclidianView().getShowXaxis()
		// && (getGuiManager().getActiveEuclidianView().getShowYaxis()));
		mi.setSelected(getGuiManager().getActiveEuclidianView().getShowXaxis()
		        && (getGuiManager().getActiveEuclidianView().getShowYaxis()));
	}

	public void setShowGridSelected(GCheckBoxMenuItem mi) {
		// GeoGebraMenubarW.setMenuSelected(mi, getGuiManager()
		// .getActiveEuclidianView().getShowGrid());
		mi.setSelected(getGuiManager().getActiveEuclidianView().getShowGrid());
	}

	// ============================================
	// IMAGES
	// ============================================

	

	private String createImageSrc(String ext, String base64) {
		String dataUrl = "data:image/" + ext + ";base64," + base64;
		return dataUrl;
	}

	public ImageElement getRefreshViewImage() {
		// don't need to load gui jar as reset image is in main jar
		ImageElement imgE = getImageManager().getInternalImage(GuiResources.INSTANCE
		        .viewRefresh());
		attachNativeLoadHandler(imgE);
		return imgE;
	}

	public ImageElement getPlayImage() {
		// don't need to load gui jar as reset image is in main jar
		return getImageManager().getInternalImage(GuiResources.INSTANCE.navPlay());
	}

	public ImageElement getPauseImage() {
		// don't need to load gui jar as reset image is in main jar
		return getImageManager().getInternalImage(GuiResources.INSTANCE.navPause());
	}

	// ============================================
	// XML
	// ============================================

	@Override
	protected void getWindowLayoutXML(StringBuilder sb, boolean asPreference) {
		// save the dimensions of the current window
		sb.append("\t<window width=\"");

		if (getEuclidianView1().getWidth() > 1) {
			// double ratio = 800.0 * (getEuclidianView1().getHeight() + 170) /
			// 600.0;
			// sb.append((int)ratio);
			// so it seems GeoGebraTube doesn't add anything to the following:
			sb.append(getEuclidianView1().getWidth());
		} else {
			sb.append(800);
		}

		sb.append("\" height=\"");

		if (getEuclidianView1().getHeight() > 1) {
			// 170 is a GeoGebraTube hack
			sb.append(getEuclidianView1().getHeight() + 170);
		} else {
			sb.append(600);
		}

		sb.append("\" />\n");

		if (guiManager == null) {
			initGuiManager();
		}
		getGuiManager().getLayout().getXml(sb, asPreference);// TODO
		                                                     // implementation
		                                                     // needed

		// labeling style
		// default changed so we need to always save this now
		// if (labelingStyle != ConstructionDefaults.LABEL_VISIBLE_AUTOMATIC) {
		sb.append("\t<labelingStyle ");
		sb.append(" val=\"");
		sb.append(getLabelingStyle());
		sb.append("\"/>\n");
		// }
	}

	

	// ============================================
	// SCRIPTS
	// ============================================

	@Override
	public void evalPythonScript(App app, String string, String arg) {
		debug("Python scripting not supported");

	}

	@Override
	public PythonBridge getPythonBridge() {
		// not available in web
		return null;
	}

	// =======================================
	// KEYBOARD
	// =======================================


	// ============================================
	// FONTS
	// ============================================

	@Override
	public GFont getPlainFontCommon() {
		return new geogebra.html5.awt.GFontW("normal");
	}

	@Override
	public FontManager getFontManager() {
		return fontManager;
	}

	@Override
	public GFont getFontCanDisplay(String testString, boolean serif, int style,
	        int size) {
		return fontManager.getFontCanDisplay(testString, serif, style, size);
	}

	// ============================================
	// CURSORS
	// ============================================

	@Override
	public void setWaitCursor() {
		RootPanel.get().setStyleName(ORIGINAL_BODY_CLASSNAME);
		RootPanel.get().addStyleName("cursor_wait");
	}

	@Override
	public void setDefaultCursor() {
		RootPanel.get().setStyleName(ORIGINAL_BODY_CLASSNAME);
	}

	public void resetCursor() {
		RootPanel.get().setStyleName(ORIGINAL_BODY_CLASSNAME);
	}

	@Override
	public void updateUI() {
		App.debug("updateUI: implementation needed for GUI"); // TODO
		                                                      // Auto-generated

	}

	// ========================================
	// EXPORT & GEOTUBE
	// ========================================
	public void copyEVtoClipboard() {
		Window.open(getEuclidianView1().getExportImageDataUrl(3, false),
		        "_blank", null);
	}

	@Override
	public void copyGraphicsViewToClipboard() {
		App.debug("unimplemented");
	}

	@Override
	public void uploadToGeoGebraTube() {
		showURLinBrowserWaiterFixedDelay();
		GeoGebraTubeExportWeb ggbtube = new GeoGebraTubeExportWeb(this);
		getGgbApi().getBase64(true,
		        getUploadToGeoGebraTubeCallback(ggbtube));
	}

	public native JavaScriptObject getUploadToGeoGebraTubeCallback(
	        GeoGebraTubeExportWeb ggbtube) /*-{
		return function(base64string) {
			ggbtube.@geogebra.web.main.GeoGebraTubeExportWeb::uploadWorksheetSimple(Ljava/lang/String;)(base64string);
		}
	}-*/;

	// ========================================
	// MISC
	// ========================================

	/**
	 * Clear selection
	 * 
	 * @param repaint
	 *            whether all views need repainting afterwards
	 */
	/*
	 * @Override public void clearSelectedGeos(boolean repaint) { // if
	 * (getUseFullGui()) ? if (useFullAppGui) ((AlgebraViewW)
	 * getAlgebraView()).clearSelection(); super.clearSelectedGeos(repaint); }
	 */

	@Override
	public GeoElementSelectionListener getCurrentSelectionListener() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
    public void showLoadingAnimation(boolean go) {
		// showSplashImageOnCanvas();

	}

	@Override
	public void showURLinBrowser(final String pageUrl) {
		// Window.open(pageUrl, "_blank", "");
		debug("opening: " + pageUrl);

		// assume showURLinBrowserWaiterFixedDelay is called before
		showURLinBrowserPageUrl = pageUrl;
	}

	public String showURLinBrowserPageUrl = null;

	public native void showURLinBrowserWaiterFixedDelay() /*-{
		this.@geogebra.web.main.AppW::showURLinBrowserPageUrl = null;

		var that = this;
		var timer = {};
		function intervalTask() {
			if (that.@geogebra.web.main.AppW::showURLinBrowserPageUrl != null) {
				$wnd.open(
						that.@geogebra.web.main.AppW::showURLinBrowserPageUrl,
						"_blank");
				if (timer.tout) {
					$wnd.clearInterval(timer.tout);
				}
			}
		}

		timer.tout = $wnd.setInterval(intervalTask, 700);
	}-*/;

	@Override
	public void initGuiManager() {
		setWaitCursor();
		guiManager = newGuiManager();
		guiManager.setLayout(new geogebra.web.gui.layout.LayoutW());
		guiManager.initialize();
		setDefaultCursor();
	}

	/**
	 * @return a GuiManager for GeoGebraWeb
	 */
	protected GuiManagerW newGuiManager() {
		return new GuiManagerW(AppW.this);
	}

	private geogebra.web.gui.app.SplashDialog splashDialog = null;

	private void createAppSplash() {
		splashDialog = new geogebra.web.gui.app.SplashDialog();
	}

	public static native void console(String string) /*-{
		if ($wnd && $wnd.console) {
			@geogebra.common.main.App::debug(Ljava/lang/String;)(string);
		}
	}-*/;

	public GeoGebraAppFrame getAppFrame() {
		return appFrame;
	}

	@Override
	public void exitAll() {
		App.debug("unimplemented");
	}

	public void addMenuItem(GPopupMenuW wrappedPopup, String filename,
	        String name, boolean asHtml, MenuInterface subMenu) {
		addMenuItem(wrappedPopup.getPopupMenu(), filename, name, asHtml,
		        subMenu);
	}

	@Override
	public void addMenuItem(MenuInterface parentMenu, String filename,
	        String name, boolean asHtml, MenuInterface subMenu) {
		addMenuItem((MenuBar) parentMenu, filename, name, asHtml, subMenu);
	}

	public void addMenuItem(MenuBar parentMenu, String filename, String name,
	        boolean asHtml, MenuInterface subMenu) {
		String funcName = filename.substring(0, filename.lastIndexOf('.'));
		ImageResource imgRes = (ImageResource) (AppResources.INSTANCE
		        .getResource(funcName));
		String iconString = imgRes.getSafeUri().asString();

		parentMenu.addItem(GeoGebraMenubarW.getMenuBarHtml(iconString, name),
		        true, (MenuBar) subMenu);
	}

	/**
	 * This is used for LaTeXes in GeoGebraWeb (DrawText, DrawEquationWeb)
	 */
	@Override
	public void scheduleUpdateConstruction() {

		// set up a scheduler in case 0.5 seconds would not be enough for the
		// computer
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			public void execute() {

				// 0.5 seconds is good for the user and maybe for the computer
				// too
				Timer timeruc = new Timer() {
					@Override
					public void run() {
						boolean force = kernel.getForceUpdatingBoundingBox();
						kernel.setForceUpdatingBoundingBox(true);
						kernel.getConstruction().updateConstruction();
						kernel.notifyRepaint();
						kernel.setForceUpdatingBoundingBox(force);
					}
				};
				timeruc.schedule(500);
			}
		});
	}

	@Override
	public void createNewWindow() {
		// TODO implement it ?
	}
	
	public ObjectPool getObjectPool() {
		return this.objectPool;
	}

	@Override
    public ViewManager getViewManager() {
	    return getGuiManager();
    }
	
	public static native void debug(JavaScriptObject j) /*-{
		$wnd.console.log(j);
	}-*/;
	
	public boolean menubarRestricted() {
		return frame != null;
	}
	
	@Override
    public String getDataParamId(){
		return getArticleElement().getDataParamId();
	
	}

	@Override
    protected void resetCommandDictionary() {
	    this.commandDictionary = null;
	    
    }

	@Override
    public void afterLoadFileAppOrNot() {

		getGuiManager().getLayout().setPerspectives(getTmpPerspectives());

		getScriptManager().ggbOnInit();	// put this here from Application constructor because we have to delay scripts until the EuclidianView is shown

		kernel.initUndoInfo();

		getEuclidianView1().synCanvasSize();
		if (!useFullAppGui) {

			getEuclidianView1().doRepaint2();
			stopCollectingRepaints();

			frame.splash.canNowHide();
			getEuclidianView1().requestFocusInWindow();

			if (needsSpreadsheetTableModel())
				getSpreadsheetTableModel();

			refreshSplitLayoutPanel();
		} else {
			splashDialog.canNowHide();
			updateCenterPanel(true);
			getEuclidianView1().doRepaint2();
			stopCollectingRepaints();
			// Well, it may cause freeze if we attach this too early
			attachViews();
			((SplitLayoutPanel)getSplitLayoutPanel()).forceLayout();
			((SplitLayoutPanel)getSplitLayoutPanel()).onResize();
			this.getEuclidianViewpanel().onResize();
			getEuclidianView1().doRepaint2();
		}
		
		this.getEuclidianViewpanel().updateNavigationBar();
		GeoGebraProfiler.getInstance().profileEnd();
    }

	@Override
    public void tubeSearch(String phrase) {
	    // TODO Auto-generated method stub
	    
    }

	/**
	 * Returns the tool name and tool help text for the given tool as an HTML
	 * text that is useful for tooltips.
	 * 
	 * @param mode
	 *            : tool ID
	 */
	public String getToolTooltipHTML(int mode) {

		// TODO: fix this code copied from desktop
		//if getLocalization().getTooltipLocale() != null) {
		//	getLocalization().setTooltipFlag();
		// }

		StringBuilder sbTooltip = new StringBuilder();
		sbTooltip.append("<html><b>");
		sbTooltip.append(StringUtil.toHTMLString(getToolName(mode)));
		sbTooltip.append("</b><br>");
		sbTooltip.append(StringUtil.toHTMLString(getToolHelp(mode)));
		sbTooltip.append("</html>");

		getLocalization().clearTooltipFlag();

		return sbTooltip.toString();

	}

	public ConstructionProtocolNavigation getConstructionProtocolNavigation() {

		if (constProtocolNavigation == null) {
			constProtocolNavigation = new ConstructionProtocolNavigationW(this);
		}

		return constProtocolNavigation;
	
    }
	
	public void notifyFileLoaded(){
		if (this.getDialogManager() instanceof DialogManagerW && !((DialogManagerW) this.getDialogManager()).isGoogleDriveChooserNull()) {
			((DialogManagerW) this.getDialogManager()).getGoogleDriveFileChooser().hide();
		}
	}

	public GDimension getPreferredSize() {
		return preferredSize;
	}

	@Override
	public void setPreferredSize(geogebra.common.awt.GDimension size) {
		preferredSize = size;
	}
}
