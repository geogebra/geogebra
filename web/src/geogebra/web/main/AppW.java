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
import geogebra.common.javax.swing.GOptionPane;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;
import geogebra.common.main.DialogManager;
import geogebra.common.main.FontManager;
import geogebra.common.main.GeoElementSelectionListener;
import geogebra.common.main.SpreadsheetTableModel;
import geogebra.common.main.settings.Settings;
import geogebra.common.util.AsyncOperation;
import geogebra.common.util.Language;
import geogebra.common.util.StringUtil;
import geogebra.common.util.debug.Log;
import geogebra.html5.awt.GDimensionW;
import geogebra.html5.css.GuiResources;
import geogebra.html5.gui.laf.GLookAndFeel;
import geogebra.html5.gui.laf.SmartLookAndFeel;
import geogebra.html5.gui.tooltip.ToolTipManagerW;
import geogebra.html5.io.MyXMLioW;
import geogebra.html5.js.JavaScriptInjector;
import geogebra.html5.main.AppWeb;
import geogebra.html5.main.FontManagerW;
import geogebra.html5.main.HasAppletProperties;
import geogebra.html5.main.LocalizationW;
import geogebra.html5.main.ViewManager;
import geogebra.html5.move.ggtapi.operations.LoginOperationW;
import geogebra.html5.move.ggtapi.operations.OpenFromGGTOperationW;
import geogebra.html5.sound.SoundManagerW;
import geogebra.html5.util.ArticleElement;
import geogebra.html5.util.MyDictionary;
import geogebra.web.WebStatic;
import geogebra.web.euclidian.EuclidianControllerW;
import geogebra.web.euclidian.EuclidianPanelWAbstract;
import geogebra.web.euclidian.EuclidianViewW;
import geogebra.web.gui.GuiManagerInterfaceW;
import geogebra.web.gui.dialog.DialogManagerW;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.inputbar.AlgebraInputW;
import geogebra.web.gui.menubar.GeoGebraMenubarW;
import geogebra.web.gui.menubar.LanguageCommand;
import geogebra.web.gui.view.probcalculator.ProbabilityCalculatorViewW;
import geogebra.web.gui.view.spreadsheet.SpreadsheetTableModelW;
import geogebra.web.helper.ObjectPool;
import geogebra.web.javax.swing.GCheckBoxMenuItem;
import geogebra.web.javax.swing.GOptionPaneW;
import geogebra.web.javax.swing.GPopupMenuW;
import geogebra.web.kernel.KernelW;
import geogebra.web.move.googledrive.operations.GoogleDriveOperationW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.MissingResourceException;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.TextAlign;
import com.google.gwt.canvas.dom.client.Context2d.TextBaseline;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class AppW extends AppWeb {

	public final static String syntaxStr = "_Syntax";
	public static String geoIPCountryName;
	public static String geoIPLanguage;
	
	private HashMap<String, String> englishCommands = null;

	private FontManagerW fontManager;
	private SpreadsheetTableModelW tableModel;
	private SoundManagerW soundManager;
	protected DialogManager dialogManager = null;
	private ToolTipManagerW toolTipManager;
	
	

	protected final ArticleElement articleElement;
	private String ORIGINAL_BODY_CLASSNAME = "";

	protected EuclidianPanelWAbstract euclidianViewPanel;
	protected Canvas canvas;

	private boolean[] showAxes = { true, true };
	private boolean showGrid = false;
	private final GLookAndFeel laf;

	/**
	 * Preferred application frame size. Used in case frame size needs updating.
	 */
	private GDimension preferredSize = new GDimensionW(0,0);

	boolean menuKeysLoaded = false;
	protected ObjectPool objectPool;
	private GoogleDriveOperationW googleDriveOperation;
	private OpenFromGGTOperationW openFromGGTOperation;
	
	/**
	 * Constructors will be called from subclasses
	 * AppWapplication, AppWapplet, and AppWsimple
	 */
	protected AppW(ArticleElement ae) {
		super();		
		this.articleElement = ae;
		if("smart".equals(ae.getDataParamLAF())){
			this.laf = new SmartLookAndFeel();
		}else if("modern".equals(ae.getDataParamLAF())){
			this.laf = new SmartLookAndFeel();
		}else{
			this.laf = new GLookAndFeel();
		}
	}

	// ========================================================
	// INITIALIZING
	// ========================================================

	/**
	 * Initializes the application, seeds factory prototypes, creates Kernel and
	 * MyXMLIO
	 * 
	 */
	protected void initCommonObjects() {
		initFactories();
		geogebra.common.factories.UtilFactory.prototype = new geogebra.web.factories.UtilFactoryW();
		geogebra.common.factories.Factory
		        .setPrototype(new geogebra.web.factories.FactoryW());
		// App.initializeSingularWS();

		// neded to not overwrite anything already exists
		ORIGINAL_BODY_CLASSNAME = RootPanel.getBodyElement().getClassName();
		
		//Online - Offline event handling begins here
		initNetworkEventFlow();
	}
	
	/** 
	 * Initializes the user authentication 
	 */
	protected void initSignInEventFlow() {
		
		// Initialize the signIn operation
		loginOperation = new LoginOperationW();
		if (getNetworkOperation().getOnline()) {
			initGoogleDriveEventFlow();
			loginOperation.performTokenLogin();
		}
	}
	
	/**
	 * initializes the google drive event flow
	 */
	protected void initGoogleDriveEventFlow() {
		
		googleDriveOperation = new GoogleDriveOperationW(this);
		
		if (getNetworkOperation().getOnline()) {
			googleDriveOperation.initGoogleDriveApi();
		}
		
	}
	
	/**
	 * initializes open from GGT event flow
	 */
	public void initOpenFromGGTEventFlow() {
		if (openFromGGTOperation == null) {
			openFromGGTOperation = new OpenFromGGTOperationW(this);
		}
	}
	
	public OpenFromGGTOperationW getOpenFromGGTOperation() {
		return openFromGGTOperation;
	}
	
	/**
	 * @return GoogleDriveOperation
	 */
	public GoogleDriveOperationW getGoogleDriveOperation() {
		return googleDriveOperation;
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
	 * 
	 * @param this_app application
	 * @return a kernel
	 */
	protected Kernel newKernel(App this_app){
		return new KernelW(this_app);
	}

	/**
	 * Initializes Kernel, EuclidianView, EuclidianSettings, etc..
	 * 
	 * @param undoActive
	 * @param this_app
	 */
	void initCoreObjects(final boolean undoActive, final App this_app) {

		kernel = newKernel(this_app);

		// init settings
		settings = new Settings();

		myXMLio = new MyXMLioW(kernel, kernel.getConstruction());

		fontManager = new FontManagerW();
		setFontSize(16);
		initEuclidianViews();

		initImageManager();

		setFontSize(16);
		// setLabelDragsEnabled(false);

		// make sure undo allowed
		hasFullPermissions = true;

		getScriptManager();// .ggbOnInit();//this is not called here because we
		                   // have to delay it
		                   // until the canvas is first drawn

		setUndoActive(undoActive);
		registerFileDropHandlers(getFrameElement());
		afterCoreObjectsInited();
		resetFonts();
	}

	
	protected void afterCoreObjectsInited() { } // TODO: abstract?

	

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

	@Override
	public boolean isApplet() {
		return !articleElement.getDataParamApp();
	}

	public boolean onlyGraphicsViewShowing() {
		if (!isUsingFullGui() || getGuiManager() == null) {
			return true;
		}

		return getGuiManager().getLayout().isOnlyVisible(App.VIEW_EUCLIDIAN);
	}

	@Override
	public boolean isUsingFullGui() {
		return useFullGui;
	}

	@Override
	public GuiManagerInterfaceW getGuiManager() {
		return null;
	}

	@Override
    public Canvas getCanvas() {
		return canvas;
	}

	public Element getFrameElement(){
		App.debug("getFrameElement() returns null, should be overridden by subclasses");
		return null;
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
	public DialogManager getDialogManager() {
		return dialogManager;
	}

	@Override
	public Factory getFactory() {
		return Factory.getPrototype();
	}

	

	//public ToolTipManagerW getToolTipManager(){
	//	if(toolTipManager == null){
	//	toolTipManager = new ToolTipManagerW(this);
	//	}
	//	return toolTipManager;
	//}

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

		if (getGuiManager() != null) {
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

	public EuclidianPanelWAbstract getEuclidianViewpanel() {
		return euclidianViewPanel;
	}

	@Override
	public boolean hasEuclidianView2EitherShowingOrNot() {
		return (getGuiManager() != null) &&
			getGuiManager().hasEuclidianView2EitherShowingOrNot();
	}

	@Override
    public boolean hasEuclidianView2() {
		return (getGuiManager() != null) && getGuiManager().hasEuclidianView2();
	}

	@Override
	public EuclidianViewW getEuclidianView2() {

		if (getGuiManager() == null)
			return null;

		return (EuclidianViewW)getGuiManager().getEuclidianView2();
	}
	


	@Override
	public EuclidianViewInterfaceCommon getActiveEuclidianView() {
		if (getGuiManager() == null) {
			return getEuclidianView1();
		}
		return getGuiManager().getActiveEuclidianView();
	}

	@Override
	public boolean isShowingEuclidianView2() {
		return (getGuiManager() != null) && getGuiManager().hasEuclidianView2()
			&& getGuiManager().getEuclidianView2().isShowing();
	}

	@Override
	public EuclidianViewW createEuclidianView() {
		return (EuclidianViewW) this.euclidianView;
	}

	@Override
	public AlgebraView getAlgebraView() {
		if (getGuiManager() == null) {
			return null;
		}
		return getGuiManager().getAlgebraView();
	}

	@Override
	public boolean showView(int view) {
		if (getGuiManager() == null) {
			return (view == App.VIEW_EUCLIDIAN);
		}
		return getGuiManager().showView(view);
	}

	protected void attachViews() {

		if (getGuiManager() == null)
			return;

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
		Log.trace("Current Locale: " + localeName);

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
		if (getGuiManager() != null) {
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
				Log.error("Missing Dictionary " + getLocalization().getLanguage());
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

	

	private String driveBase64description = null;
	private String driveBase64FileName = null;
	
	private String currentFileId = null;

	
	public String getCurrentFileId() {
		return currentFileId;
	}

	public void setCurrentFileId(String currentFileId) {
		this.currentFileId = currentFileId;
	}

	public void refreshCurrentFileDescriptors(String fName, String desc) {
		if (desc.equals("null") || desc.equals("undefined")) {
			driveBase64description = "";
		} else {
			driveBase64description = desc;
		}
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

	protected native void setCurrentFileId() /*-{
		if ($wnd.GGW_appengine) {
			this.@geogebra.web.main.AppW::currentFileId = $wnd.GGW_appengine.FILE_IDS[0];
		}
	}-*/;

	

	
	
	@Override
    protected void resetStorageInfo(){
		driveBase64FileName = null;
		driveBase64description = null;
		currentFileId = null;
		((DialogManagerW) getDialogManager())
		        .refreshAndShowCurrentFileDescriptors(driveBase64FileName,
		                driveBase64description);
	}
	
	@Override
    protected void clearInputBar(){
		if (isUsingFullGui() && showAlgebraInput() && getGuiManager() != null) {
			AlgebraInputW ai = (getGuiManager().getAlgebraInput());
			ai.clear();
		}
	}
	
	

	

	/**
	 * Register file drop handlers for the canvas of this application
	 */
	native void registerFileDropHandlers(Element ce) /*-{

		var appl = this;
		var frameElement = ce;

		if (frameElement) {
			frameElement.addEventListener("dragover", function(e) {
				e.preventDefault();
				e.stopPropagation();
				frameElement.style.borderColor = "#ff0000";
			}, false);
			frameElement.addEventListener("dragenter", function(e) {
				e.preventDefault();
				e.stopPropagation();
			}, false);
			frameElement
					.addEventListener(
							"drop",
							function(e) {
								e.preventDefault();
								e.stopPropagation();
								frameElement.style.borderColor = "#000000";
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
			if (frameElement)
				frameElement.style.borderColor = "#000000";
		}, false);
		$doc.body.addEventListener("drop", function(e) {
			e.preventDefault();
			e.stopPropagation();
		}, false);
	}-*/;

	

	


	// ================================================
	// ERROR HANDLING
	// ================================================


	@Override
	public void showCommandError(final String command, final String message) {
		// TODO
		App.debug("TODO later: make sure splash screen not showing");

		String title = getLocalization().getPlain("ApplicationName") + " - "
		        + getLocalization().getError("Error");

		String[] optionNames = { getLocalization().getPlain("OK"),
		        getLocalization().getPlain("ShowOnlineHelp") };

		GOptionPaneW.INSTANCE.showOptionDialog(this, message, title,
		        GOptionPane.CUSTOM_OPTION, GOptionPane.ERROR_MESSAGE, null,
		        optionNames, new AsyncOperation() {
			        @Override
			        public void callback(Object obj) {
			        	String[] dialogResult = (String[])obj;
				        if ("1".equals(dialogResult[0])) {
					        if (getGuiManager() != null) {
						        getGuiManager().openCommandHelp(command);
					        }
				        }
			        }
		        });

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
		        GOptionPane.OK_CANCEL_OPTION, GOptionPane.INFORMATION_MESSAGE,
		        null);
	}

	@Override
	public void showErrorDialog(final String msg) {
		App.printStacktrace("");

		String title = getLocalization().getPlain("ApplicationName") + " - "
		        + getLocalization().getError("Error");

		GOptionPaneW.INSTANCE.showConfirmDialog(this, msg, title,
		        GOptionPane.DEFAULT_OPTION, GOptionPane.ERROR_MESSAGE, null);
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
			element.addEventListener("MSHoldVisual", function(e) { e.preventDefault(); }, false);
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
		$doc.addEventListener("MSHoldVisual", function(e) { e.preventDefault(); }, false);
		
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
		addNativeLoadHandler(img, (EuclidianView)getActiveEuclidianView());
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
		if (getFrameElement() == null)
			return 0;
		return getFrameElement().getOffsetWidth();
	}

	@Override
	public double getHeight() {
		if (getFrameElement() == null)
			return 0;
		return getFrameElement().getOffsetHeight();
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

		// This is just used from tooltipManager yet
		if (app.getGuiManager() == null)
			return null;

		return app.getGuiManager().getLayout().getRootComponent();
	}

	@Override
    public void updateCenterPanel(boolean updateUI) { }

	public Widget getSplitLayoutPanel() {
		if (getGuiManager() == null)
			return null;
		if (getGuiManager().getLayout() == null)
			return null;
		return getGuiManager().getLayout().getRootComponent();
	}

	/**
	 * @param ggwGraphicsViewWidth
	 * 
	 *            Resets the width of the Canvas converning the Width of its
	 *            wrapper (splitlayoutpanel center)
	 */
	public void ggwGraphicsViewDimChanged(int width, int height) {
		App.debug("dim changed"+getSettings().getEuclidian(1));
		getSettings().getEuclidian(1).setPreferredSize(
		        geogebra.common.factories.AwtFactory.prototype.newDimension(
		                width, height));

		// simple setting temp.
		appCanvasHeight = height;
		appCanvasWidth = width;
		App.debug("syn size");
		getEuclidianView1().synCanvasSize();
		getEuclidianView1().doRepaint2();
		stopCollectingRepaints();
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
	}

	public void updateToolBar() {
		if (!showToolBar || isIniting()) {
			return;
		}

		if (getGuiManager() != null) {
			getGuiManager().updateToolbar();
		}

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
			JavaScriptInjector.inject(GuiResources.INSTANCE.propertiesKeysJS());
		}
		super.setShowToolBar(toolbar, help);
	}

	public void setShowAxesSelected(GCheckBoxMenuItem mi) {
		// GeoGebraMenubarW.setMenuSelected(mi, getGuiManager()
		// .getActiveEuclidianView().getShowXaxis()
		// && (getGuiManager().getActiveEuclidianView().getShowYaxis()));
		mi.setSelected(getActiveEuclidianView().getShowXaxis()
		        && (getActiveEuclidianView().getShowYaxis()));
	}

	public void setShowGridSelected(GCheckBoxMenuItem mi) {
		// GeoGebraMenubarW.setMenuSelected(mi, getGuiManager()
		// .getActiveEuclidianView().getShowGrid());
		mi.setSelected(getActiveEuclidianView().getShowGrid());
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
    protected int getWindowWidth(){
		if (getWidth() > 0) {
			return (int) getWidth();
		} else {
			return 800;
		}
	}
	
	@Override
	protected int getWindowHeight() {
		if (getHeight() > 0) {
			return (int) getHeight();
		} else {
			return 600;
		}
	}
	
	@Override
	protected void getLayoutXML(StringBuilder sb, boolean asPreference) {
		
		if (getGuiManager() == null) {
			initGuiManager();
		}
		getGuiManager().getLayout().getXml(sb, asPreference);
	}

	

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
		if (getDialogManager() instanceof DialogManagerW) {
			((DialogManagerW) getDialogManager()).showLoadingAnimation();
		}
		RootPanel.get().setStyleName(ORIGINAL_BODY_CLASSNAME);
		RootPanel.get().addStyleName("cursor_wait");
	}

	@Override
	public void setDefaultCursor() {
		if (getDialogManager() instanceof DialogManagerW) {
			((DialogManagerW) getDialogManager()).hideLoadingAnimation();
		}
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
		Window.open(getEuclidianView1().getExportImageDataUrl(3, true),
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
		// this should not be called from AppWsimple!
		// this method should be overridden in
		// AppWapplet and AppWapplication!
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

		if (subMenu instanceof MenuBar) ((MenuBar)subMenu).addStyleName("GeoGebraMenuBar");
		
		parentMenu.addItem(GeoGebraMenubarW.getMenuBarHtml(iconString, name, true),
		        true, (MenuBar) subMenu);
	}

	/**
	 * This is used for LaTeXes in GeoGebraWeb (DrawText, DrawEquationWeb)
	 */
	@Override
	public void scheduleUpdateConstruction() {

		// set up a scheduler in case 0.5 seconds would not be enough for the computer
		Scheduler.get().scheduleDeferred(sucCallback);
	}

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

	Scheduler.ScheduledCommand sucCallback = new Scheduler.ScheduledCommand() {
		public void execute() {
			// 0.5 seconds is good for the user and maybe for the computer
			// too
			timeruc.schedule(500);
		}
	};

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
		return true;
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
    public abstract void afterLoadFileAppOrNot();

	/**
	 * Returns the tool name and tool help text for the given tool as an HTML
	 * text that is useful for tooltips.
	 * 
	 * @param mode
	 *            : tool ID
	 */
	@Override
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

	public GDimension getPreferredSize() {
		return preferredSize;
	}

	@Override
	public void setPreferredSize(geogebra.common.awt.GDimension size) {
		preferredSize = size;
	}

	public void buildApplicationPanel() { }

	/**
	 * Updates the GUI of the main component.
	 */
	public void updateContentPane() {
		updateContentPane(true);
	}

	private void updateContentPane(boolean updateComponentTreeUI) {
		if (initing) {
			return;
		}

		addMacroCommands();

		// used in AppWapplet
		buildApplicationPanel();

		fontManager.setFontSize(getGUIFontSize());

		// update sizes
		euclidianView.updateSize();

		// update layout
		if (updateComponentTreeUI) {
			((SplitLayoutPanel)getSplitLayoutPanel()).forceLayout();
			//updateComponentTreeUI();
		}

		// reset mode and focus
		setMoveMode();

		if (euclidianView.isShowing()) {
			requestFocusInWindow();
		}
	}
	
	protected void requestFocusInWindow(){
		if(WebStatic.panelForApplets == null && !articleElement.preventFocus()){
			euclidianView.requestFocusInWindow();
		}
	}

	@Override
    public void appSplashCanNowHide() {
		// not sure we need this in web applets
		// (not application mode)

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

	/**
	 * Called from GuiManager, implementation depends on subclass
	 * @return toolbar object
	 */
	public Object getToolbar() {
		return null;
	}

	// methods used just from AppWapplet
	public void focusLost() { }
	public void focusGained() { }
	public void setCustomToolBar() { }

	// methods used just from AppWapplication
	public int getOWidth() {
		return 0;
	}

	public int getOHeight() {
		return 0;
	}

	public Object getGlassPane() {
		return null;
	}

	public void doOnResize() { }
	public void loadURL_GGB(String ggb) { }
	
	@Override
	public String getAppletId() {
		return articleElement.getDataParamId();		
	}

	@Override
    public HasAppletProperties getAppletFrame() {
	    //Should be implemented in subclasses
	    return null;
    }
	
	private static native void nativeLoseFocus(Element element) /*-{
		var active = $doc.activeElement
		if (active && 
				((active === element) || 
					(active.compareDocumentPosition(element) & $wnd.Node.DOCUMENT_POSITION_CONTAINS))) {
							active.blur();
							@geogebra.html5.main.GlobalKeyDispatcherW::InFocus = false;
		}
	}-*/;
	
	@Override
	public void loseFocus() {
		nativeLoseFocus(articleElement);
	}
	@Override
	public boolean isScreenshotGenerator(){
		return this.articleElement.getDataParamScreenshotGenerator();
	}

	public GLookAndFeel getLAF() {
	    return laf;
    }

	@Override
	public void recalculateEnvironments() {
	    if (getEuclidianView1() != null) {
	    	getEuclidianView1().getEuclidianController().calculateEnvironment();
	    }
	    if (getGuiManager() != null && getGuiManager().hasEuclidianView2()) {
	    	((EuclidianView) getGuiManager().getEuclidianView2()).getEuclidianController().calculateEnvironment();	    	
	    }
	    if (getGuiManager() != null && getGuiManager().hasProbabilityCalculator()) {
	    	((ProbabilityCalculatorViewW)getGuiManager().getProbabilityCalculator()).plotPanel.getEuclidianController().calculateEnvironment();
	    }
    }

	
	
	/**
	 * create menu item for 3D
	 * @return null if no 3D
	 */
	public GCheckBoxMenuItem createMenuItemFor3D() {
	    return null;
    }
	
	
	
}
