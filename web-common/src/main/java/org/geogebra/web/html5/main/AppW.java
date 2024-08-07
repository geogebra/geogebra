package org.geogebra.web.html5.main;

import static org.geogebra.common.GeoGebraConstants.CAS_APPCODE;
import static org.geogebra.common.GeoGebraConstants.G3D_APPCODE;
import static org.geogebra.common.GeoGebraConstants.GEOMETRY_APPCODE;
import static org.geogebra.common.GeoGebraConstants.GRAPHING_APPCODE;
import static org.geogebra.common.GeoGebraConstants.SUITE_APPCODE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.GeoGebraConstants.Platform;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.DrawEquation;
import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.exam.ExamController;
import org.geogebra.common.export.pstricks.GeoGebraExport;
import org.geogebra.common.export.pstricks.GeoGebraToAsymptote;
import org.geogebra.common.export.pstricks.GeoGebraToPgf;
import org.geogebra.common.export.pstricks.GeoGebraToPstricks;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.factories.CASFactory;
import org.geogebra.common.factories.Factory;
import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.gui.AccessibilityManagerInterface;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.inputfield.HasLastItem;
import org.geogebra.common.gui.view.algebra.AlgebraView.SortMode;
import org.geogebra.common.io.MyXMLio;
import org.geogebra.common.io.XMLParseException;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.javax.swing.GImageIcon;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.GeoFactory;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.selector.CommandFilterFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.main.FontManager;
import org.geogebra.common.main.GeoElementSelectionListener;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.main.MaterialsManagerI;
import org.geogebra.common.main.SpreadsheetTableModel;
import org.geogebra.common.main.SpreadsheetTableModelSimple;
import org.geogebra.common.main.UndoRedoMode;
import org.geogebra.common.main.settings.AlgebraSettings;
import org.geogebra.common.main.settings.DefaultSettings;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.SettingsBuilder;
import org.geogebra.common.main.settings.config.AppConfigDefault;
import org.geogebra.common.main.undo.UndoManager;
import org.geogebra.common.move.ggtapi.models.ClientInfo;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Pagination;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.move.operations.NetworkOperation;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventDispatcher;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.ScriptManager;
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.common.sound.SoundManager;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimerListener;
import org.geogebra.common.util.MD5EncrypterGWTImpl;
import org.geogebra.common.util.NormalizerMinimal;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.common.util.lang.Language;
import org.geogebra.common.util.profiler.FpsProfiler;
import org.geogebra.ggbjdk.java.awt.geom.Dimension;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.regexp.client.NativeRegExpFactory;
import org.geogebra.regexp.shared.RegExpFactory;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.GeoGebraGlobal;
import org.geogebra.web.html5.awt.GFontW;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.euclidian.EuclidianControllerW;
import org.geogebra.web.html5.euclidian.EuclidianPanelWAbstract;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.euclidian.profiler.FpsProfilerW;
import org.geogebra.web.html5.export.ExportGraphicsFactoryW;
import org.geogebra.web.html5.factories.AwtFactoryW;
import org.geogebra.web.html5.factories.FactoryW;
import org.geogebra.web.html5.factories.FormatFactoryW;
import org.geogebra.web.html5.factories.UtilFactoryW;
import org.geogebra.web.html5.gui.AlgebraInput;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.HasHide;
import org.geogebra.web.html5.gui.LoadingApplication;
import org.geogebra.web.html5.gui.ToolBarInterface;
import org.geogebra.web.html5.gui.accessibility.AccessibilityManagerW;
import org.geogebra.web.html5.gui.accessibility.AccessibilityView;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.gui.laf.GgbSettings;
import org.geogebra.web.html5.gui.laf.MebisSettings;
import org.geogebra.web.html5.gui.laf.SignInControllerI;
import org.geogebra.web.html5.gui.laf.VendorSettings;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.util.BrowserStorage;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.util.LightBox;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.gui.util.ViewsChangedListener;
import org.geogebra.web.html5.gui.zoompanel.FullScreenState;
import org.geogebra.web.html5.gui.zoompanel.ZoomPanel;
import org.geogebra.web.html5.io.MyXMLioW;
import org.geogebra.web.html5.kernel.GeoElementGraphicsAdapterW;
import org.geogebra.web.html5.kernel.UndoManagerW;
import org.geogebra.web.html5.kernel.commands.CommandDispatcherW;
import org.geogebra.web.html5.main.settings.DefaultSettingsW;
import org.geogebra.web.html5.main.settings.SettingsBuilderW;
import org.geogebra.web.html5.move.googledrive.GoogleDriveOperation;
import org.geogebra.web.html5.safeimage.ImageLoader;
import org.geogebra.web.html5.sound.GTimerW;
import org.geogebra.web.html5.sound.SoundManagerW;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.html5.util.ArchiveEntry;
import org.geogebra.web.html5.util.ArchiveLoader;
import org.geogebra.web.html5.util.Base64;
import org.geogebra.web.html5.util.CopyPasteW;
import org.geogebra.web.html5.util.GeoGebraElement;
import org.geogebra.web.html5.util.GlobalHandlerRegistry;
import org.geogebra.web.html5.util.ImageManagerW;
import org.geogebra.web.html5.util.UUIDW;
import org.geogebra.web.html5.util.debug.LoggerW;
import org.geogebra.web.html5.util.keyboard.KeyboardManagerInterface;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.client.Style;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.timer.client.Timer;
import org.gwtproject.user.client.ui.RequiresResize;
import org.gwtproject.user.client.ui.RootPanel;
import org.gwtproject.user.client.ui.Widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;

import elemental2.core.ArrayBuffer;
import elemental2.core.Uint8Array;
import elemental2.dom.DomGlobal;
import elemental2.dom.EventTarget;
import elemental2.dom.File;
import elemental2.dom.FileReader;
import elemental2.dom.HTMLImageElement;
import elemental2.dom.URL;
import jsinterop.base.JsPropertyMap;

public abstract class AppW extends App implements SetLabels, HasLanguage {
	public static final String STORAGE_ALL_MACROS_KEY = "storedMacros";
	public static final String STORAGE_MACRO_KEY_PREFIX = "storedMacro_";
	public static final String EDIT_MACRO_URL_PARAM_NAME = "editMacroName";
	public static final String EDITED_MACRO_NAME_KEY = "editedMacroName";
	public static final String EDITED_MACRO_XML_KEY = "editedMacroXML";
	/*
	 * Note: the following numbers need to be in sync with deploygbb to scale
	 * screenshots
	 */
	private static final int SCREEN_WIDTH_THRESHOLD = 480;
	private static final int BIG_SCREEN_MARGIN = 30;
	private static final int SMALL_SCREEN_MARGIN = 10;

	private DrawEquationW drawEquation;

	private NormalizerMinimal normalizerMinimal;
	protected GgbAPIW ggbapi;
	private final LocalizationW loc;
	private ImageManagerW imageManager;
	private GgbFile currentFile = null;
	// random id to identify ggb files
	// eg so that GeoGebraTube can notice it's a version of the same file
	private int localID = -1;
	private long syncStamp;

	protected FontManagerW fontManager;
	private SpreadsheetTableModelSimple tableModel;
	private SoundManagerW soundManager;
	private AsyncManager asyncManager;

	private CopyPasteW copyPaste;

	protected final GeoGebraElement geoGebraElement;
	protected final AppletParameters appletParameters;

	protected EuclidianPanelWAbstract euclidianViewPanel;

	private final GLookAndFeelI laf;

	protected ArrayList<HasHide> popups = new ArrayList<>();
	// protected GeoGebraFrame frame = null;

	private GlobalKeyDispatcherW globalKeyDispatcher;
	private boolean openedForMacroEditing = false;
	private BrowserStorage storage;
	private final ArrayList<ViewsChangedListener> viewsChangedListener = new ArrayList<>();
	private GDimension preferredSize;
	private int appletWidth = 0;
	private int appletHeight = 0;
	private NetworkOperation networkOperation;
	private PageListControllerInterface pageController;

	/*
	 * True if showing the "alpha" in Input Boxes is allowed. (we can hide the
	 * symbol buttons with data-param-allowSymbolTable parameter)
	 */
	private boolean allowSymbolTables = true;
	private boolean stylebarAllowed = true;
	private boolean undoRedoPanelAllowed = true;
	private TimerSystemW timers;
	HashMap<String, String> revTranslateCommandTable = new HashMap<>();
	private Runnable closeBroserCallback;
	private Runnable insertImageCallback;
	private final ArrayList<RequiresResize> euclidianHandlers = new ArrayList<>();
	private ArchiveLoader archiveLoader;
	private ZoomPanel zoomPanel;
	private final PopupRegistry popupRegistry = new PopupRegistry();
	private VendorSettings vendorSettings;
	private DefaultSettings defaultSettings;
	private FpsProfiler fpsProfiler;
	private AccessibilityView accessibilityView;
	Timer timeruc = new Timer() {
		@Override
		public void run() {
			updateConsBoundingBox();
		}
	};
	private final GlobalHandlerRegistry dropHandlers = new GlobalHandlerRegistry();
	private Widget lastFocusableWidget;
	private FullScreenState fullscreenState;
	private ToolTipManagerW toolTipManager;
	private final ExamController examController = GlobalScope.examController;

	/**
	 * @param geoGebraElement
	 *            the element which contains the application,
	 *            used to be an article element, now a div
	 * @param dimension
	 *            int
	 * @param laf
	 *            (null for webSimple) {@link GLookAndFeelI}
	 */
	protected AppW(GeoGebraElement geoGebraElement, AppletParameters appletParameters,
			int dimension, GLookAndFeelI laf) {
		super(getPlatform(appletParameters, dimension, laf));
		this.geoGebraElement = geoGebraElement;
		this.appletParameters = appletParameters;

		setPrerelease(appletParameters.getDataParamPrerelease());

		// laf = null in webSimple
		boolean hasUndo = appletParameters.getDataParamEnableUndoRedo()
				&& (laf == null || laf.undoRedoSupported());
		setUndoRedoMode(hasUndo ? UndoRedoMode.GUI : UndoRedoMode.DISABLED);

		this.loc = new LocalizationW(getConfig(), dimension);
		this.laf = laf;

		getTimerSystem();
		this.showInputTop = InputPosition.algebraView;
		dropHandlers.addEventListener(DomGlobal.window, "resize", event -> {
			fitSizeToScreen();
			windowResized();
		});
		if (!StringUtil
				.empty(getAppletParameters().getParamScaleContainerClass())) {
			getGlobalHandlers().add(
					Browser.addMutationObserver(getParent(
							getAppletParameters().getParamScaleContainerClass()),
							this::checkScaleContainer));
		}
	}

	/**
	 * Resize to fill browser
	 */
	public final void fitSizeToScreen() {
		getAppletFrame().fitSizeToScreen();
	}

	/**
	 * Scale to container if needed.
	 */
	public void checkScaleContainer() {
		if (getAppletParameters().getDataParamFitToScreen()
				|| (zoomPanel != null && zoomPanel.isFullScreen())) {
			return;
		}
		Log.debug("RESIZE: Start");
		if (!StringUtil
				.empty(getAppletParameters().getParamScaleContainerClass())) {
			Element parent = getParent(
					getAppletParameters().getParamScaleContainerClass());
			if (parent != null) {
				scaleTo(parent.getOffsetWidth(),
						Math.max(
								getAppletParameters().getParamAutoHeight()
								? parent.getOffsetWidth()
								: 0, parent.getOffsetHeight()));
				if (parent != this.getScalerParent()) {
					resizeContainer();
				}
			}
		} else if (!getAppletParameters().getParamDisableAutoScale()) {
			int border = 0;
			// only apply right border if left border is nonzero: important for
			// iframes
			if (this.getAbsLeft() > 0) {
				border = NavigatorUtil.getWindowWidth() > SCREEN_WIDTH_THRESHOLD
					? BIG_SCREEN_MARGIN : SMALL_SCREEN_MARGIN;
			}
			int width = NavigatorUtil.getWindowWidth() - (int) getAbsLeft() - border;
			scaleTo(width, NavigatorUtil.getWindowHeight());
			resizeContainer();
		} else {
			scaleWithRatio(getAppletParameters().getDataParamScale());
		}
		recalculateEnvironments();
	}

	private void resizeContainer() {
		if (getScalerParent() != null) {
			Style style = getScalerParent().getStyle();
			double scale = geoGebraElement.getScaleX();
			// check for zero size needed if applet is not visible in DOM
			if (getWidth() > 0 && getHeight() > 0) {
				style.setWidth(getWidth() * scale, Unit.PX);
				style.setHeight(getHeight() * scale, Unit.PX);
			}
		}
	}

	private Element getScalerParent() {
		return geoGebraElement.getParentElement() == null ? null
				: geoGebraElement.getParentElement().getParentElement();
	}

	private void scaleTo(int width, int height) {
		double xscale = width / getWidth();
		double yscale = height / getHeight();
		boolean upscale = getAppletParameters().getParamAllowUpscale();
		double scale = LayoutUtilW.getDeviceScale(xscale, yscale, upscale);
		if (!upscale) {
			scale = Math.min(scale, getAppletParameters().getDataParamScale());
		}
		scaleWithRatio(scale);
	}

	/**
	 * @param scale
	 *            scale ratio
	 */
	public void scaleWithRatio(double scale) {
		Browser.scale(geoGebraElement.getParentElement(), scale, 0, 0);
		geoGebraElement.resetScale();
		deferredForceResize();
	}

	private Element getParent(String containerClass) {
		Element current = geoGebraElement.getParentElement();
		while (current != null) {
			if (current.hasClassName(containerClass)) {
				return current;
			}
			current = current.getParentElement();
		}
		return null;
	}

	/**
	 * Remove the external GeoGebraHeader element
	 */
	public void removeHeader() {
		Element header = Dom.querySelector(".GeoGebraHeader");
		if (header != null) {
			header.removeFromParent();
			getAppletParameters().setAttribute("marginTop", "0");
			fitSizeToScreen();
		}
	}

	/**
	 * Called if header visibility is changed.
	 */
	public void onHeaderVisible() {
		// TODO listener (?)
	}

	private static Platform getPlatform(AppletParameters ae,
										int dimension,
										GLookAndFeelI laf2) {
		return laf2 == null ? Platform.WEB
				: laf2.getPlatform(dimension, ae.getDataParamAppName());
	}

	/**
	 * handler for window resize
	 */
	protected final void windowResized() {
		for (RequiresResize mtg : this.euclidianHandlers) {
			mtg.onResize();
		}
		if (this.getGuiManager() != null) {
			getGuiManager().setPixelRatio(getPixelRatio());
			if (isUnbundled()) {
				getGuiManager().resetMenuIfScreenChanged();
			}
		}
		if (this.zoomPanel != null) {
			zoomPanel.updateFullscreen();
		}
	}

	@Override
	public final void resetUniqueId() {
		uniqueId = UUIDW.generateUUIDString();
	}

	/**
	 * @return id of local saved file
	 */
	public int getLocalID() {
		return this.localID;
	}

	/**
	 * sets ID of local saved file
	 *
	 * @param id
	 *            int
	 */
	public void setLocalID(int id) {
		this.localID = id;
	}

	@Override
	public final DrawEquation getDrawEquation() {
		if (drawEquation == null) {
			drawEquation = new DrawEquationW(this::getPixelRatio);
		}

		return drawEquation;
	}

	@Override
	public final SoundManager getSoundManager() {
		if (soundManager == null) {
			soundManager = new SoundManagerW(this);
		}
		return soundManager;
	}

	/**
	 * Use the async manager's schedule method when you try to access
	 * parts of the code that have been split by the GWT compiler
	 * @return the instance of the AsyncManager
	 */
	public final AsyncManager getAsyncManager() {
		if (asyncManager == null) {
			asyncManager = new AsyncManager(this);
			asyncManager.ensureModulesLoaded(appletParameters.getDataParamPreloadModules());
		}

		return asyncManager;
	}

	public void commandsLoaded() {
		getAsyncManager().onResourceLoaded();
	}

	@Override
	public GgbAPIW getGgbApi() {
		if (ggbapi == null) {
			ggbapi = new GgbAPIW(this);
		}
		return ggbapi;
	}

	@Override
	public final NormalizerMinimal getNormalizer() {
		if (normalizerMinimal == null) {
			normalizerMinimal = new NormalizerMinimal();
		}

		return normalizerMinimal;
	}

	/**
	 * inits factories
	 */
	protected void initFactories() {
		FormatFactory.setPrototypeIfNull(new FormatFactoryW());
		AwtFactory.setPrototypeIfNull(new AwtFactoryW());
		StringUtil.setPrototypeIfNull(new StringUtil());
		UtilFactory.setPrototypeIfNull(new UtilFactoryW());
		RegExpFactory.setPrototypeIfNull(new NativeRegExpFactory());
	}

	@Override
	final public GlobalKeyDispatcherW getGlobalKeyDispatcher() {
		if (globalKeyDispatcher == null) {
			globalKeyDispatcher = new GlobalKeyDispatcherW(this);
		}
		return globalKeyDispatcher;
	}

	@Override
	public EuclidianViewW getEuclidianView1() {
		return (EuclidianViewW) euclidianView;
	}

	/**
	 * @return timer system for view repaints
	 */
	public TimerSystemW getTimerSystem() {
		if (timers == null) {
			timers = new TimerSystemW(this);
		}
		return timers;
	}

	@Override
	public ScriptManager newScriptManager() {
		return new ScriptManagerW(this, new DefaultExportedApi());
	}

	@Override
	protected EventDispatcher newEventDispatcher() {
		EventDispatcher dispatcher = new EventDispatcher(this);
		if (getAppletParameters().getDisableJavaScript()) {
			dispatcher.disableScriptType(ScriptType.JAVASCRIPT);
		}
		return dispatcher;
	}

	// ================================================
	// native JS
	// ================================================

	@Override
	public void callAppletJavaScript(String fun, String arg) {
		Log.debug("calling function: " + fun + "(" + arg + ")");
		JsEval.callNativeGlobalFunction(fun, arg);
	}

	@Override
	public MyXMLioW createXMLio(Construction cons) {
		return new MyXMLioW(cons.getKernel(), cons);
	}

	@Override
	public void doSetLanguage(String lang, boolean asyncCall) {
		getLocalization().setLanguage(lang);
		// make sure digits are updated in all numbers
		getKernel().updateConstructionLanguage();

		// update display & Input Bar Dictionary etc
		setLabels();

		notifyLocalizationLoaded();
		// importatnt for accessibility
		getFrameElement().setLang(lang == null ? "" : lang.replace("_", "-"));
	}

	/**
	 * Notify components about initial localization load
	 */
	public void notifyLocalizationLoaded() {
		// TODO Auto-generated method stub
	}

	@Override
	public void setLanguage(final String browserLang) {
		Language language1 = Language
				.fromLanguageTagOrLocaleString(browserLang);
		final String languageTag = language1.toLanguageTag();
		getLocalization().cancelCallback();
		if (languageTag.equals(loc.getLanguageTag())) {
			Log.debug("Language is already " + loc.getLanguageTag());
			setLabels();
			notifyLocalizationLoaded();
			return;
		}
		if (languageTag.isEmpty()) {
			Log.warn("language being set to empty string");
			setLanguage("en");
			return;
		}

		Log.debug("setting language to:" + languageTag + ", browser languageTag:"
				+ browserLang);
		getLocalization().loadScript(language1, this);
	}

	/**
	 * @param language
	 *            language ISO code
	 * @param country
	 *            country or country_variant
	 */
	public void setLanguage(String language, String country) {
		if (StringUtil.empty(language)) {
			Log.warn("error calling setLanguage(), setting to English (US): "
					+ language + "_" + country);
			setLanguage("en");
			return;
		}
		if (StringUtil.empty(country)) {
			setLanguage(language);
			return;
		}
		this.setLanguage(language + "_" + country);
	}

	@Override
	public LocalizationW getLocalization() {
		return loc;
	}

	/**
	 * Translates localized command name into internal TODO check whether this
	 * differs from translateCommand somehow and either document it or remove
	 * this method
	 *
	 * @param cmd
	 *            localized command name
	 * @return internal command name
	 */
	@Override
	final public String getInternalCommand(String cmd) {
		initTranslatedCommands();
		String s;
		String cmdLower = StringUtil.toLowerCaseUS(cmd);
		Commands[] values = Commands.values();
		if (revTranslateCommandTable.isEmpty()) { // we should clear this cache
													// on language change!
			for (Commands c : values) { // and fill it now if needed
				s = Commands.englishToInternal(c).name();

				// make sure that when si[] is typed in script, it's changed to
				// Si[] etc
				String lowerCaseCmd = StringUtil
						.toLowerCaseUS(getLocalization().getCommand(s));
				revTranslateCommandTable.put(lowerCaseCmd, s);
			}
			// add renamed commands
			Commands.addRenamed(revTranslateCommandTable, getLocalization());
		}
		return revTranslateCommandTable.get(cmdLower);
		// return null;
	}

	@Override
	protected void fillCommandDict() {
		super.fillCommandDict();
		revTranslateCommandTable.clear();
	}

	/**
	 * Try loading a file until it succeeds (all modules are loaded)
	 * @param archiveContent
	 *            zip archive content
	 * @param asSlide
	 *            whether to reload just a slide
	 */
	public void loadGgbFile(final GgbFile archiveContent, final boolean asSlide) {
		AlgebraSettings algebraSettings = getSettings().getAlgebra();
		algebraSettings.setModeChanged(false);
		clearMedia();

		try {
			loadFile(archiveContent, asSlide);
		} catch (Exception e) {
			// nothing
		}

		if (!algebraSettings.isModeChanged()) {
			algebraSettings.setTreeMode(SortMode.TYPE);
		}
	}

	/**
	 * @param dataUrl
	 *            the data url to load the ggb file
	 */
	public void loadGgbFileAsBase64(String dataUrl) {
		prepareReloadGgbFile();
		ArchiveLoader view = getArchiveLoader();
		view.processBase64String(dataUrl);
	}

	/**
	 * Loads a binary file (ggb, ggs). In Notes the .ggb files are embedded to current slide.
	 *
	 * @param binary
	 *            binary file
	 * @param fileName
	 *            file name, to decide whether to embed
	 */
	public void loadOrEmbedGgbFile(ArrayBuffer binary, String fileName) {
		EmbedManager embedManager = getEmbedManager();
		if (!fileName.endsWith("ggs") && embedManager != null) {
			Material mat = new Material(Material.MaterialType.ggb);
			mat.setBase64(Base64.bytesToBase64(new Uint8Array(binary)));
			embedManager.embed(mat);
		} else {
			loadGgbFileAsBinary(binary);
		}
	}

	/**
	 * Loads a binary file (ggb, ggs).
	 * @param binary
	 *            binary file
	 */
	public void loadGgbFileAsBinary(ArrayBuffer binary) {
		prepareReloadGgbFile();
		ArchiveLoader view = getArchiveLoader();
		view.processBinaryData(binary);
	}

	private void prepareReloadGgbFile() {
		getImageManager().reset();
	}

	private void loadFile(GgbFile archiveContent, final boolean asSlide)
			throws XMLParseException {
		if (archiveContent.containsKey(GgbFile.STRUCTURE_JSON)) {
			getAppletParameters().setAttribute("appName", "notes");
			getAppletFrame().initPageControlPanel(this);
			euclidianController.clearMeasurementTools();
			if (getPageController() != null) {
				getPageController().loadSlides(archiveContent);
				return;
			}
		}

		beforeLoadFile(asSlide);

		GgbFile archive = archiveContent.duplicate("tmp");
		final GgbArchive def = new GgbArchive(archive, is3D());
		// Handling of construction and macro file

		ArchiveEntry libraryJS = archive.remove(MyXMLio.JAVASCRIPT_FILE);

		// Construction (required)
		if (def.isInvalid()) {
			throw new XMLParseException(
					"File is corrupt: No GeoGebra data found");
		}

		// Library JavaScript (optional)
		if (libraryJS == null) { // TODO: && !isGGTfile)
			kernel.resetLibraryJavaScript();
		} else {
			kernel.setLibraryJavaScript(libraryJS.string);
		}

		// just to make SpotBugs happy
		EmbedManager embedManager = getEmbedManager();
		if (embedManager != null) {
			embedManager.loadEmbeds(archive);
		}

		if (!def.hasConstruction()) {
			if (def.hasMacros()) {
				getXMLio().processXMLString(def.getMacros(), true, true);
			}

			setCurrentFile(archiveContent);
			afterLoadFileAppOrNot(asSlide);
			if (!hasMacroToRestore()) {
				getGuiManager().refreshCustomToolsInToolBar();
			}
			getGuiManager().updateToolbar();
			return;
		}
		ImageLoader imageLoader = new ImageLoader(this, archive, archiveContent,
				() -> getAsyncManager().scheduleCallback(
						() -> runAfterLoadImages(def, asSlide)));
		imageLoader.load();
	}

	private void runAfterLoadImages(GgbArchive def, boolean asSlide) {
		if (def.hasConstruction()) {
			// ggb file: remove all macros from kernel before processing
			kernel.removeAllMacros();
		}
		try {
			setHideConstructionProtocolNavigation();
			Log.debug("images loaded");
			// Macros (optional)
			if (def.hasMacros()) {
				getXMLio().processXMLString(def.getMacros(), true, true);
			}
			int seed = getAppletParameters().getParamRandomSeed();
			if (seed != -1) {
				setRandomSeed(seed);
			}
			getXMLio().processXMLString(def.getConstruction(), true, false,
					getAppletParameters().getParamRandomize());
			// defaults (optional)
			if (def.hasDefaults2d()) {
				getXMLio().processXMLString(def.getDefaults2d(), false, true);
			}
			if (def.hasDefaults3d()) {
				getXMLio().processXMLString(def.getDefaults3d(), false, true);
			}
			if (!appletParameters.getDataParamTransparentGraphics()) {
				// fix for half-pixel not having any color; set AFTER splash screen
				// fallback compatible with applet-unfocused CSS class
				getFrameElement().getStyle().setBackgroundColor(
						appletParameters.getDataParamBorder("#D3D3D3"));
			}
			afterLoadFileAppOrNot(asSlide);
		} catch (XMLParseException | RuntimeException e) {
			Log.debug(e);
		}
	}

	/**
	 * Sets viewId for each macro
	 *
	 * @param toolbar3D
	 *            toolbar string for 3D View that includes its macros as well.
	 */
	public void setMacroViewIds(String toolbar3D) {
		int macroCount = kernel.getMacroNumber();
		for (int i = 0; i < macroCount; i++) {
			Macro macro = kernel.getMacro(i);
			if (macro.getViewId() == null) {
				int macroMode = EuclidianConstants.MACRO_MODE_ID_OFFSET + i;
				if (toolbar3D != null
						&& toolbar3D.contains(String.valueOf(macroMode))) {
					macro.setViewId(App.VIEW_EUCLIDIAN3D);
				} else {
					macro.setViewId(App.VIEW_EUCLIDIAN);
				}
			}
		}
	}

	/**
	 * Prepare for loading file
	 */
	private void beforeLoadFile(boolean asSlide) {
		// make sure the image manager will not wait for images from the *old*
		// file
		if (this.getImageManager() != null && !asSlide) {
			this.getImageManager().reset();
		}
		getEuclidianView1().setReIniting(true);
		if (hasEuclidianView2EitherShowingOrNot(1)) {
			getEuclidianView2(1).setReIniting(true);
		}
	}

	/**
	 * @param file
	 *            currently open file
	 */
	public void setCurrentFile(GgbFile file) {
		if (currentFile == file) {
			return;
		}

		currentFile = file;
	}

	@Override
	public void resetCurrentFile() {
		setCurrentFile(null);
	}

	/**
	 * @return controller for page control panel
	 */
	public PageListControllerInterface getPageController() {
		return pageController;
	}

	/**
	 * @param pageController
	 *            {@link PageListControllerInterface}
	 */
	public void setPageController(PageListControllerInterface pageController) {
		this.pageController = pageController;
	}

	/**
	 * @return current .zip file as hashmap
	 */
	public GgbFile getCurrentFile() {
		return currentFile;
	}

	@Override
	public void reset() {
		if (currentFile != null) {
			try {
				getAppletFrame().resetAppletOnLoad();
				loadGgbFile(currentFile, false);
			} catch (Exception e) {
				clearConstruction();
			}
		} else {
			clearConstruction();
		}
	}

	@Override
	public final ImageManagerW getImageManager() {
		return imageManager;
	}

	protected void initImageManager() {
		imageManager = new ImageManagerW();
	}

	@Override
	public boolean clearConstruction() {
		getSelectionManager().clearSelectedGeos(false);
		kernel.clearConstruction(true);

		kernel.initUndoInfo();
		resetMaxLayerUsed();
		setCurrentFile(null);

		return true;
	}

	@Override
	public final MyImage getExternalImageAdapter(String fileName, int width,
			int height) {
		HTMLImageElement im = getImageManager().getExternalImage(fileName, true);
		return getImageAdapter(im, fileName, width, height);
	}

	@Override
	public final MyImage getInternalImageAdapter(String fileName, int width,
			int height) {
		HTMLImageElement im = getImageManager().getInternalImage(fileName);
		return getImageAdapter(im, fileName, width, height);
	}

	private MyImage getImageAdapter(HTMLImageElement im, String fileName, int width, int height) {
		if (im == null) {
			return null;
		}
		if (width != 0 && height != 0) {
			im.width = width;
			im.height = height;
		}
		return new MyImageW(im, fileName.toLowerCase().endsWith(".svg"));
	}

	@Override
	public final UndoManager getUndoManager(Construction cons) {
		return new UndoManagerW(cons);
	}

	@Override
	public final GeoElementGraphicsAdapter newGeoElementGraphicsAdapter() {
		return new GeoElementGraphicsAdapterW(this);
	}

	@Override
	public final void runScripts(final GeoElement geo1, final String string) {
		invokeLater(() -> geo1.runClickScripts(string));
	}

	@Override
	public void invokeLater(final Runnable runnable) {
		Scheduler.get().scheduleDeferred(runnable::run);
	}

	@Override
	public final CASFactory getCASFactory() {
		return CASFactory.getPrototype();
	}

	@Override
	public final void fileNew() {
		resetPages();
		clearConstruction();
		clearMedia();
		resetUniqueId();
		setLocalID(-1);
		setActiveMaterial(null);

		if (getGoogleDriveOperation() != null) {
			getGoogleDriveOperation().resetStorageInfo();
		}
		resetFileHandle();
		resetUI();
		resetUrl();
		if (examController.isExamActive()) {
			examController.createNewTempMaterial();
		}
		setSaved();
	}

	protected void resetFileHandle() {
		// only with full UI
	}

	private void resetPages() {
		if (pageController != null) {
			pageController.resetPageControl();
		}
	}

	/**
	 * Reset the UI.
	 */
	protected void resetUI() {
		kernel.getInputPreviewHelper().clear();
		clearInputBar();
		if (!isUnbundled() && isPortrait()) {
			adjustViews(false, false);
		}
	}

	/**
	 * reset everything for new file
	 */
	public void resetOnFileNew() {
		// ignore active: don't save means we want new construction
		setWaitCursor();
		fileNew();
		setDefaultCursor();
		showPerspectivesPopupIfNeeded();
	}

	/**
	 * shows the template chooser if user is logged in and has templates,
	 * otherwise resets the UI for file new
	 */
	public void tryLoadTemplatesOnFileNew() {
		if (isWhiteboardActive() && getLoginOperation() != null) {
			getLoginOperation().getResourcesAPI().getTemplateMaterials(
					new MaterialCallbackI() {
						@Override
						public void onLoaded(List<Material> result, Pagination meta) {
							if (result.isEmpty()) {
								resetOnFileNew();
							} else {
								getGuiManager().getTemplateController()
										.fillTemplates(AppW.this, result);
								getDialogManager().showTemplateChooser();
							}
						}

						@Override
						public void onError(Throwable exception) {
							Log.error("Error on templates load");
							resetOnFileNew();
						}
					});
		} else {
			resetOnFileNew();
		}
	}

	/**
	 * Stores all the macros from the app in the storage
	 */
	public void storeAllMacros() {
		createStorage();
		String allMacrosB64 = getGgbApi().getAllMacrosBase64();
		storage.setItem(STORAGE_ALL_MACROS_KEY, allMacrosB64);
	}

	/**
	 * Stores the given macro in the storage
	 * @param macro is the macro that needs to be stored
	 */
	public void storeMacro(Macro macro) {
		if (macro == null) {
			return;
		}
		createStorage();
		String macroB64 = getGgbApi().getMacroBase64(macro);
		storage.setItem(createStorageMacroKey(macro.getEditName()), macroB64);
	}

	/**
	 * Returns the macro with the given name from the storage
	 * @param macroName is the name of the macro that needs to be returned
	 * @return the macro with the given name
	 */
	public String getMacroFromStorage(String macroName) {
		return storage.getItem(createStorageMacroKey(macroName));
	}

	/**
	 * Removes the macro with the given name from the storage
	 * @param macroName is the name of the macro that needs to be removed from the storage
	 */
	public void removeMacroFromStorage(String macroName) {
		storage.removeItem(createStorageMacroKey(macroName));
	}

	/**
	 * Removes all macros from the storage
	 */
	public void removeAllMacrosFromStorage() {
		createStorage();
		for (int i = 0; i < storage.getLength(); i++) {
			if (storage.key(i).startsWith(STORAGE_MACRO_KEY_PREFIX)) {
				storage.removeItem(storage.key(i));
			}
		}
	}

	/**
	 * Removes the given macro from the app and the storage
	 * @param macro is the macro that needs to be removed
	 */
	@Override
	public void removeMacro(Macro macro) {
		super.removeMacro(macro);
		removeMacroFromStorage(macro.getEditName());
	}

	/**
	 * Removes the macro with the given name from the app and the storage
	 * @param macroName is the name of the macro that needs to be removed
	 */
	@Override
	public void removeMacro(String macroName) {
		super.removeMacro(macroName);
		removeMacroFromStorage(macroName);
	}

	/**
	 * Removes all the macros from the app and the storage
	 */
	@Override
	public void removeAllMacros() {
		super.removeAllMacros();
		removeAllMacrosFromStorage();
	}

	/**
	 * Initializes the storage
	 */
	protected void createStorage() {
		if (storage == null) {
			storage = BrowserStorage.LOCAL;
		}
	}

	/**
	 * Checks if there is any macro in the storage
	 * @return whether there is any macro in the storage
	 */
	protected boolean hasMacroToRestore() {
		createStorage();
		for (int i = 0; i < storage.getLength(); i++) {
			if (storage.key(i).startsWith(STORAGE_MACRO_KEY_PREFIX)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Creates a storage key with the given macro name
	 * @param macroName is the name of the macro that the key will belong to
	 * @return the storage key belonging to the macro with the given name
	 */
	public String createStorageMacroKey(String macroName) {
		return STORAGE_MACRO_KEY_PREFIX + macroName;
	}

	/**
	 * Checks if the macro with the given name is in the storage
	 * @param macroName is the name of the macro that needs to be checked
	 * @return whether the storage contains the macro with the given name
	 */
	public boolean storageContainsMacro(String macroName) {
		return getMacroFromStorage(macroName) != null;
	}

	/**
	 * Restores the macro with the given name from the storage
	 * @param macroName is the name of the macro that needs to be restored
	 */
	public void restoreMacro(String macroName) {
		if (storageContainsMacro(macroName)) {
			getGgbApi().setBase64(getMacroFromStorage(macroName));
		}
	}

	/**
	 * Opens the macro that needs to be edited from storage
	 * @param editMacroName is the name of the macro that needs to be edited
	 * @return whether the opening was successful
	 */
	public boolean openEditMacroFromStorage(String editMacroName) {
		createStorage();
		if (storageContainsMacro(editMacroName)) {
			try {
				Macro editMacro = getKernel().getMacro(editMacroName);
				if (editMacro != null) {
					openEditMacro(editMacro);
					DomGlobal.document.title = editMacroName;
					return true;
				}
			} catch (Exception e) {
				Log.debug(e);
			}
		}
		return false;
	}

	/**
	 * sets the timestamp of last synchronization with ggbTube
	 *
	 * @param syncStamp
	 *            long
	 */
	public void setSyncStamp(long syncStamp) {
		this.syncStamp = syncStamp;
	}

	/**
	 * @return timestamp of last synchronization with ggbTube
	 */
	public long getSyncStamp() {
		return this.syncStamp;
	}

	/**
	 * @return GoogleDriveOperation
	 */
	public GoogleDriveOperation getGoogleDriveOperation() {
		return null;
	}

	/**
	 * @param fileToHandle
	 *            archive
	 * @return whether file is valid
	 */
	public boolean openFile(File fileToHandle) {
		resetPerspectiveParam();
		resetUrl();
		return doOpenFile(fileToHandle);
	}

	/**
	 * Remove perspective parameter
	 */
	public void resetPerspectiveParam() {
		if (getAppletParameters() != null) {
			getAppletParameters().setAttribute("perspective", "");
		}
	}

	/**
	 * Opens the ggb or ggt file
	 *
	 * @param fileToHandle
	 *            file
	 * @return returns true, if fileToHandle is ggb or ggt file, otherwise
	 *         returns false. Note that If the function returns true, it's don't
	 *         mean, that the file opening was successful, and the opening
	 *         finished already.
	 */
	public boolean doOpenFile(File fileToHandle) {
		String ggbRegEx = ".*\\.(ggb|ggt|ggs|csv|off|pdf|h5p)$";
		String fileName = fileToHandle.name.toLowerCase();
		if (!fileName.matches(ggbRegEx)) {
			return false;
		}
		if (fileName.endsWith(".pdf")) {
			openPDF(fileToHandle);
			return true;
		}
		if (fileName.endsWith(".h5p")) {
			openH5P(fileToHandle);
			return true;
		}

		FileReader reader = new FileReader();
		if (fileName.matches(".*\\.(ggb|ggt|ggs)$")) {
			reader.addEventListener("load",
					(event) -> loadOrEmbedGgbFile(reader.result.asArrayBuffer(), fileName));
			reader.readAsArrayBuffer(fileToHandle);
		} else {
			reader.addEventListener("load", (event) -> {
				if (reader.readyState == FileReader.DONE) {
					String fileStr = reader.result.asString();
					if (fileName.endsWith(".csv")) {
						openCSV(DomGlobal.atob(fileStr.substring(fileStr.indexOf(",") + 1)));
					}
					if (fileName.endsWith(".off")) {
						openOFF(DomGlobal.atob(fileStr.substring(fileStr.indexOf(",") + 1)));
					}
				}
			});
			reader.readAsDataURL(fileToHandle);
		}

		return true;
	}

	/**
	 * @param id
	 *            material ID
	 * @param onError
	 *            callback for errors
	 */
	public void openMaterial(String id, AsyncOperation<String> onError) {
		// only with GUI
	}

	/**
	 * @return OfflineOperation event flow
	 */
	public NetworkOperation getNetworkOperation() {
		return networkOperation;
	}

	/**
	 * Initialize online/offline state listener
	 */
	protected void initNetworkEventFlow() {
		networkOperation = new NetworkOperation(Browser.isOnline());
		EventTarget[] targets = {DomGlobal.window, DomGlobal.document};
		for (EventTarget target: targets) {
			getGlobalHandlers().addEventListener(target, "offline",
					e -> networkOperation.setOnline(false));
			getGlobalHandlers().addEventListener(target, "online",
					e -> networkOperation.setOnline(true));
		}
	}

	/**
	 * @param allowST
	 *            whether to allow symbol popup in input fields
	 */
	public void setAllowSymbolTables(boolean allowST) {
		allowSymbolTables = allowST;
	}

	/**
	 * @return true, if alpha buttons may be visible in input boxes.
	 */
	public boolean isAllowedSymbolTables() {
		return allowSymbolTables;
	}

	/**
	 * @param flag
	 *            whether stylebar can be shown also when menubar is hidden
	 */
	public void setAllowStyleBar(boolean flag) {
		stylebarAllowed = flag;
	}

	/**
	 * @return whether it's allowed to show stylebar even if menubar closed
	 */
	public boolean isStyleBarAllowed() {
		return stylebarAllowed;
	}

	/**
	 * Set wether the undo redo panel is allowed in the app.
	 *
	 * @param flag true if the panel is allowed, false otherwise
	 */
	public void setUndoRedoPanelAllowed(boolean flag) {
		undoRedoPanelAllowed = flag;
	}

	/**
	 * Returns if the undo redo panel is allowed in the app.
	 *
	 * @return true if the panel is allowed, false otherwise
	 */
	public boolean isUndoRedoPanelAllowed() {
		return undoRedoPanelAllowed;
	}

	@Override
	public CommandDispatcherW newCommandDispatcher(Kernel cmdKernel) {
		return new CommandDispatcherW(cmdKernel);
	}

	/**
	 * @param viewId
	 *            view ID
	 * @return the plotpanel euclidianview
	 */
	public EuclidianViewW getPlotPanelEuclidianView(int viewId) {
		if (getGuiManager() == null) {
			return null;
		}
		return (EuclidianViewW) getGuiManager().getPlotPanelView(viewId);
	}

	/**
	 * Loads an image and puts it on the canvas (this happens on webcam input)
	 * On drag &amp; drop or insert from URL this would be called too, but that would
	 * set security exceptions
	 *
	 * @param url
	 *            - the data url of the image
	 * @param corner1
	 *            corner 1 expression
	 * @param corner2
	 *            corner 2 expression
	 * @param corner4
	 *            corner 4 expression
	 * @return image
	 */
	public GeoImage urlDropHappened(String url, String corner1, String corner2,
			String corner4) {

		// Filename is temporarily set until a better solution is found
		// TODO: image file name should be reset after the file data is
		// available

		String zipDirectory = MD5EncrypterGWTImpl.encrypt(url);

		// with dummy extension, maybe gif or jpg in real
		String imgFileName = zipDirectory + ".png";

		String fn = imgFileName;
		int index = imgFileName.lastIndexOf('/');
		if (index != -1) {
			fn = fn.substring(index + 1); // filename without
		}
		// path
		fn = org.geogebra.common.util.Util.processFilename(fn);

		// filename will be of form
		// "a04c62e6a065b47476607ac815d022cc\liar.gif"
		imgFileName = zipDirectory + '/' + fn;

		SafeGeoImageFactory factory =
				new SafeGeoImageFactory(this).withAutoCorners(corner1 == null)
						.withCorners(corner1, corner2, corner4);
		GeoImage geoImage = factory.create(imgFileName, url);
		if (insertImageCallback != null) {
			this.insertImageCallback.run();
		}
		return geoImage;
	}

	/**
	 * Loads an image and puts it on the canvas (this happens by drag &amp; drop)
	 *
	 * @param fileName
	 *            - the file name of the image
	 * @param content
	 *            - the image data url
	 */
	public void imageDropHappened(String fileName, String content) {
		SafeGeoImageFactory factory = new SafeGeoImageFactory(this);
		String path = ImageManagerW.getMD5FileName(fileName, content);
		factory.create(path, content);
	}

	/**
	 * @param imgFileName
	 *            filename
	 */
	@Override
	public GeoImage createImageFromString(final String imgFileName,
			String imageAsString, GeoImage imageOld,
			final boolean autoCorners, final GeoPointND c1, final GeoPointND c2) {
		SafeGeoImageFactory factory =
				new SafeGeoImageFactory(this, imageOld).withAutoCorners(c1 == null)
						.withCorners(c1, c2);
		return factory.create(imgFileName, imageAsString);
	}

	/**
	 * Opens the image file
	 *
	 * @param fileToHandle
	 *            javascript handle for the file
	 * @return returns true, if fileToHandle image file, otherwise return false.
	 *         Note: If the function returns true, it doesn't mean the
	 *         file opening was successful, and the opening finished already.
	 */
	public boolean openFileAsImage(File fileToHandle) {
		String imageRegEx = ".*(png|jpg|jpeg|gif|bmp|svg)$";
		if (!fileToHandle.name.toLowerCase().matches(imageRegEx)) {
			return false;
		}
		if (getGuiManager() == null || !getGuiManager().toolbarHasImageMode()) {
			return true;
		}
		FileReader reader = new FileReader();
		reader.addEventListener("load", (event) -> {
			if (reader.readyState == FileReader.DONE) {
				String fileStr = reader.result.asString();
				String fileName = fileToHandle.name;
				imageDropHappened(fileName, fileStr);
			}
		});
		reader.readAsDataURL(fileToHandle);
		return true;
	}

	/**
	 * @return the id of the GeoGebra element
	 */
	public String getArticleId() {
		return geoGebraElement.getId();
	}

	/**
	 * @param articleid
	 *            the article id added by scriptManager
	 *
	 *            this method is called by scriptmanager after ggbOnInit
	 */
	public static void appletOnLoad(String articleid) {
		if (GeoGebraGlobal.getGgbAppletOnLoad() != null) {
			GeoGebraGlobal.getGgbAppletOnLoad().accept(articleid);
		}
	}

	@Override
	public void setActiveView(int evID) {
		if (getGuiManager() != null) {
			getGuiManager().setActiveView(evID);
		}
	}

	/**
	 * @return client info for API calls
	 */
	public final ClientInfo getClientInfo() {
		ClientInfo clientInfo = new ClientInfo();
		clientInfo.setModel(getLoginOperation().getModel());
		clientInfo.setLanguage(getLocalization().getLanguageTag());
		clientInfo.setWidth((int) getWidth());
		clientInfo.setHeight((int) getHeight());
		clientInfo.setType(getClientType());
		clientInfo.setId(getClientID());
		clientInfo.setAppName(getConfig().getAppCode());
		clientInfo.setAssign(getShareController().isAssign());
		return clientInfo;
	}

	/**
	 * Load Google Drive APIs
	 */
	protected void initGoogleDriveEventFlow() {
		// overriden in AppWFull
	}

	/**
	 * @param l
	 *            listener that checks for opened/closed views
	 */
	public void addViewsChangedListener(ViewsChangedListener l) {
		viewsChangedListener.add(l);
	}

	/**
	 * Notify listeners about open/closed view
	 */
	public void fireViewsChangedEvent() {
		for (ViewsChangedListener l : viewsChangedListener) {
			l.onViewsChanged();
		}
	}

	@Override
	public FontManager getFontManager() {
		return fontManager;
	}

	/**
	 * Initializes the application, seeds factory prototypes, creates Kernel and
	 * MyXMLIO
	 *
	 */
	protected void initCommonObjects() {
		initFactories();

		// Online - Offline event handling begins here
		initNetworkEventFlow();
	}

	/**
	 *
	 * @param thisApp
	 *            application
	 * @return a kernel
	 */
	protected Kernel newKernel(App thisApp) {
		return new Kernel(thisApp, new GeoFactory());
	}

	/**
	 * Initializes Kernel, EuclidianView, EuclidianSettings, etc..
	 *
	 */
	protected void initCoreObjects() {
		kernel = newKernel(this);
		kernel.setAngleUnit(kernel.getApplication().getConfig().getDefaultAngleUnit());

		initSettings();

		fontManager = new FontManagerW();
		setFontSize(16, false);
		initEuclidianViews();

		initImageManager();

		setFontSize(16, true);

		getScriptManager(); // gbOnInit() is only called after file loads
							// completely
		FileDropHandlerW.registerDropHandler(getFrameElement(), this, dropHandlers);
		setViewsEnabled();

		getAppletFrame().setApplication(this);
	}

	/**
	 *
	 * @return true if no 3D is available at all (graphing, geometry, whiteboard)
	 */
	protected boolean is3DDisabledForApp() {
		return isUnbundledGraphing() || isUnbundledGeometry();
	}

	@Override
	public boolean is3DViewEnabled() {
		return getAppletParameters().getDataParamEnable3D(true) && super.is3DViewEnabled();
	}

	private void setViewsEnabled() {
		if (!getConfig().isCASEnabled()) {
			getSettings().getCasSettings().setEnabled(false);
		} else if (getAppletParameters().getDataParamEnableCAS(false)
				|| !getAppletParameters().getDataParamEnableCAS(true)) {
			getSettings().getCasSettings().setEnabled(
					getAppletParameters().getDataParamEnableCAS(false));
		}
		if (getSettings().getCasSettings().isEnabled()) {
			getKernel().setSymbolicMode(getConfig().getSymbolicMode());
		}

		if (getSettings().getEuclidian(-1) != null) {
			if (getAppletParameters().getDataParamEnable3D(false)
					|| !getAppletParameters().getDataParamEnable3D(true)) {
				getSettings().getEuclidian(-1)
						.setEnabled(getAppletParameters().getDataParamEnable3D(false));
			}
		}

		String disableCAS = NavigatorUtil.getUrlParameter("disableCAS");
		if ("".equals(disableCAS) || "true".equals(disableCAS)) {
			kernel.getAlgebraProcessor()
					.addCommandFilter(CommandFilterFactory.createNoCasCommandFilter());
			enableCAS(false);
		}
		String disable3D = NavigatorUtil.getUrlParameter("disable3D");
		if ("".equals(disable3D) || "true".equals(disable3D)) {
			getSettings().getEuclidian(-1).setEnabled(false);
		}
	}

	@Override
	public GDimension getPreferredSize() {
		if (preferredSize == null) {
			return new Dimension(800, 600);
		}
		return preferredSize;
	}

	@Override
	public void setPreferredSize(GDimension size) {
		preferredSize = size;
	}

	/**
	 * @return element of the AppFrame / GeoGebraFrame
	 */
	public abstract Element getFrameElement();

	@Override
	public void setWaitCursor() {
		if (getDialogManager() instanceof LoadingApplication) {
			((LoadingApplication) getDialogManager()).showLoadingAnimation();
		}
		RootPanel.get().addStyleName("cursor_wait");
	}

	@Override
	public void setDefaultCursor() {
		if (getDialogManager() instanceof LoadingApplication) {
			((LoadingApplication) getDialogManager()).hideLoadingAnimation();
		}
		resetCursor();
	}

	/**
	 * Set default cursor for whole body
	 */
	public void resetCursor() {
		RootPanel.get().removeStyleName("cursor_wait");
	}

	/**
	 * Updates the GUI of the main component.
	 */
	public void updateContentPane() {
		if (initing) {
			return;
		}

		addMacroCommands();

		// used in AppWapplet
		buildApplicationPanel();

		// update sizes
		euclidianView.updateSize();
		// needed: GGB-624
		if (hasEuclidianView2(1)) {
			getEuclidianView2(1).updateSize();
		}

		// update layout
		updateTreeUI();
	}

	/**
	 * @return current look and feel
	 */
	public GLookAndFeelI getLAF() {
		return laf;
	}

	@Override
	public SpreadsheetTableModel getSpreadsheetTableModel() {
		if (tableModel == null) {
			tableModel = new SpreadsheetTableModelSimple(this, App.SPREADSHEET_INI_ROWS,
					App.SPREADSHEET_INI_COLS);
		}
		return tableModel;
	}

	/**
	 * Update layout of central pane
	 */
	protected void updateTreeUI() {
		// force layout for multiple panels
	}

	/**
	 * Build UI
	 */
	public void buildApplicationPanel() {
		// overridden in AppWApplet
	}

	/**
	 * Hide splash screen
	 */
	public void appSplashCanNowHide() {
		// only with GUI
	}

	/**
	 * Called from GuiManager, implementation depends on subclass
	 *
	 * @return toolbar object
	 */
	public ToolBarInterface getToolbar() {
		return null;
	}

	/**
	 * Update toolbar from custom definition
	 */
	public void setCustomToolBar() {
		// only needed in AppWFull
	}

	@Override
	public boolean isUsingFullGui() {
		return useFullGui;
	}

	@Override
	public GuiManagerInterfaceW getGuiManager() {
		return null;
	}

	// ========================================================
	// Getters/Setters
	// ========================================================

	@Override
	public boolean isHTML5Applet() {
		return true;
	}

	/**
	 * @return the containing element of this app
	 */
	public GeoGebraElement getGeoGebraElement() {
		return geoGebraElement;
	}

	/**
	 * @return article element with parameters
	 */
	public AppletParameters getAppletParameters() {
		return appletParameters;
	}

	@Override
	public boolean isApplet() {
		return !getAppletParameters().getDataParamApp();
	}

	@Override
	protected EuclidianView newEuclidianView(boolean[] showEvAxes,
			boolean showEvGrid) {

		return euclidianView = newEuclidianView(euclidianViewPanel,
				getEuclidianController(), showEvAxes, showEvGrid, 1,
				getSettings().getEuclidian(1));
	}

	/**
	 *
	 * @param evPanel
	 *            view panel
	 * @param ec
	 *            controller
	 * @param showEvAxes
	 *            show axes?
	 * @param showEvGrid
	 *            show grid ?
	 * @param id
	 *            euclidian view id
	 * @param evSettings
	 *            view settings
	 * @return new euclidian view
	 */
	public EuclidianViewW newEuclidianView(EuclidianPanelWAbstract evPanel,
			EuclidianController ec, boolean[] showEvAxes, boolean showEvGrid,
			int id, EuclidianSettings evSettings) {
		return new EuclidianViewW(evPanel, ec, id, evSettings);
	}

	@Override
	public EuclidianController newEuclidianController(Kernel kernel1) {
		return new EuclidianControllerW(kernel1);
	}

	@Override
	public DialogManager getDialogManager() {
		return null;
	}

	@Override
	public Factory getFactory() {
		return new FactoryW(this);
	}

	// ===================================================
	// Views
	// ===================================================

	/**
	 * @return euclidian panel
	 */
	public EuclidianPanelWAbstract getEuclidianViewpanel() {
		return euclidianViewPanel;
	}

	@Override
	public boolean hasEuclidianView2EitherShowingOrNot(int idx) {
		return (getGuiManager() != null)
				&& getGuiManager().hasEuclidianView2EitherShowingOrNot(idx);
	}

	@Override
	public EuclidianViewW getEuclidianView2(int idx) {

		if (getGuiManager() == null) {
			return null;
		}

		return (EuclidianViewW) getGuiManager().getEuclidianView2(idx);
	}

	@Override
	public EuclidianView getActiveEuclidianView() {
		if (getGuiManager() == null) {
			return getEuclidianView1();
		}
		return getGuiManager().getActiveEuclidianView();
	}

	@Override
	public boolean isShowingEuclidianView2(int idx) {
		return (getGuiManager() != null)
				&& getGuiManager().hasEuclidianView2(idx)
				&& getGuiManager().getEuclidianView2(idx).isShowing();
	}

	@Override
	public EuclidianViewW createEuclidianView() {
		return (EuclidianViewW) this.euclidianView;
	}

	@Override
	public boolean showView(int view) {
		if (getGuiManager() == null) {
			return view == App.VIEW_EUCLIDIAN;
		}
		return getGuiManager().showView(view);
	}

	// ========================================================
	// Languages
	// ========================================================

	@Override
	public void setLabels() {
		if (initing) {
			return;
		}
		loc.notifySetLabels();
		kernel.updateLocalAxesNames();
		kernel.setViewsLabels();
		updateCommandDictionary();
		setTitle();
		translateHeader();
	}

	protected void setTitle() {
		String titleTransKey = getVendorSettings().getAppTitle(getConfig());
		String title = getLocalization().getMenu(titleTransKey);
		if (getAppletParameters().getLoginAPIurl() != null
				&& getAppletParameters().getDataParamApp()) {
			Browser.changeMetaTitle(title);
		}
		geoGebraElement.getElement().setAttribute("aria-label", title);
	}

	protected void translateHeader() {
		UserPreferredLanguage.translate(this, ".GeoGebraHeader");
	}

	@Override
	public boolean letRedefine() {
		// TODO
		// Auto-generated
		return true;
	}

	// ============================================
	// IMAGES
	// ============================================

	/**
	 * @return refresh applet image
	 */
	public HTMLImageElement getRefreshViewImage() {
		HTMLImageElement imgE = ImageManagerW
				.getInternalImage(GuiResourcesSimple.INSTANCE.viewRefresh());
		imgE.addEventListener("load", (event) -> getActiveEuclidianView().updateBackground());
		return imgE;
	}

	/**
	 * @return play image
	 */
	public HTMLImageElement getPlayImage() {
		return ImageManagerW.getInternalImage(GuiResourcesSimple.INSTANCE.play_circle());
	}

	/**
	 * @return pause image
	 */
	public HTMLImageElement getPauseImage() {
		return ImageManagerW.getInternalImage(
				GuiResourcesSimple.INSTANCE.pause_circle());
	}

	/**
	 * @return play image with hover effect
	 */
	public HTMLImageElement getPlayImageHover() {
		String hoverColor = GeoGebraColorConstants.GEOGEBRA_ACCENT.toString();
		return ImageManagerW.getInternalImage(
				GuiResourcesSimple.INSTANCE.play_circle().withFill(hoverColor));
	}

	/**
	 * @return pause image with hover effect
	 */
	public HTMLImageElement getPauseImageHover() {
		String hoverColor = GeoGebraColorConstants.GEOGEBRA_ACCENT.toString();
		return ImageManagerW.getInternalImage(
				GuiResourcesSimple.INSTANCE.pause_circle().withFill(hoverColor));
	}

	// ============================================
	// XML
	// ============================================

	@Override
	protected int getWindowWidth() {
		if (getWidth() > 0) {
			return (int) getWidth();
		}
		return 800;
	}

	@Override
	protected int getWindowHeight() {
		if (getHeight() > 0) {
			return (int) getHeight();
		}
		return 600;
	}

	@Override
	protected void getLayoutXML(StringBuilder sb, boolean asPreference) {
		if (getGuiManager() == null) {
			initGuiManager();
		}
		if (getGuiManager() != null) {
			getGuiManager().getLayout().getXml(sb, asPreference);
		}
	}

	// ============================================
	// FONTS
	// ============================================

	@Override
	public GFont getPlainFontCommon() {
		return new GFontW("normal");
	}

	// ============================================
	// CURSORS
	// ============================================

	@Override
	public void updateUI() {
		if (getGuiManager() != null) {
			getGuiManager().setPixelRatio(getPixelRatio());
		}
	}

	// ========================================
	// EXPORT & GEOTUBE
	// ========================================

	/**
	 * Export given view to png
	 *
	 * @param ev
	 *            view
	 */
	public final void exportView(EuclidianViewW ev) {
		String image = ev.getExportImageDataUrl(3, true, false);
		String title = ev.getApplication().getKernel().getConstruction()
				.getTitle();
		title = "".equals(title) ? "GeoGebraImage" : title;
		getFileManager().exportImage(image, title, "png");
	}

	// ========================================
	// MISC
	// ========================================

	@Override
	public GeoElementSelectionListener getCurrentSelectionListener() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void showURLinBrowser(final String pageUrl) {
		getFileManager().open(pageUrl);
	}

	@Override
	public void initGuiManager() {
		// this should not be called from AppWsimple!
		// this method should be overridden in
		// AppWapplet and AppWapplication!
	}

	@Override
	public void exitAll() {
		Log.debug("unimplemented");
	}

	/**
	 * This is used for LaTeXes in GeoGebraWeb (DrawText, DrawEquationWeb)
	 */
	@Override
	public void scheduleUpdateConstruction() {

		// set up a scheduler in case 0.5 seconds would not be enough for the
		// computer
		timeruc.schedule(500);
	}

	protected void updateConsBoundingBox() {
		boolean force = kernel.getForceUpdatingBoundingBox();
		kernel.setForceUpdatingBoundingBox(true);
		kernel.getConstruction().updateConstructionLaTeX();
		kernel.notifyRepaint();
		kernel.setForceUpdatingBoundingBox(force);
	}

	/**
	 * File loading callback
	 *
	 * @param asSlide
	 *            whether jus a slide is loaded
	 */
	public abstract void afterLoadFileAppOrNot(boolean asSlide);

	/**
	 * Recalculate offsets/transforms for graphics events
	 */
	public void recalculateEnvironments() {
		if (getGuiManager() != null) {
			getGuiManager().recalculateEnvironments();
		}
		if (getEuclidianView1() != null) {
			getEuclidianView1().getEuclidianController().calculateEnvironment();
		}
	}

	@Override
	public void updateViewSizes() {
		// TODO Auto-generated method stub
	}

	/**
	 * @param widget
	 *            popup
	 */
	public void registerPopup(HasHide widget) {
		popups.add(widget);
	}

	public void centerAndResizeViews() {
		// to be overridden in AppWFull
	}

	@Override
	public void closePopups() {
		closePopupsNoTooltips();
		getToolTipManager().hideTooltip();

		if (!isUnbundled() && getGuiManager() != null
				&& getGuiManager().hasAlgebraView()) {
			getAlgebraView().resetItems(false);
		}

		if (getActiveEuclidianView() != null) {
			getActiveEuclidianView().getEuclidianController()
					.setObjectMenuActive(false);
		}
	}

	/**
	 * Close popups, keep tooltips
	 */
	public void closePopupsNoTooltips() {
		for (HasHide widget : popups) {
			widget.hide();
		}
		popups.clear();
	}

	/**
	 * @param el
	 *            element that can be cliked without closingpopups
	 */
	public void addAsAutoHidePartnerForPopups(Element el) {
		for (HasHide popup : popups) {
			if (popup instanceof GPopupPanel
					&& ((GPopupPanel) popup).isModal()) {
				((GPopupPanel) popup).addAutoHidePartner(el);
			}
		}
	}

	/**
	 * @return whether there are some open popups
	 */
	public boolean hasPopup() {
		return !popups.isEmpty();
	}

	/**
	 * @param widget
	 *            popup
	 */
	public void unregisterPopup(Widget widget) {
		popups.remove(widget);
	}

	/**
	 * @return client type for API calls
	 */
	public String getClientType() {
		if (getLAF() == null) {
			return "web";
		}
		return getLAF().getType();
	}

	private String getClientID() {
		return getAppletParameters().getDataClientID();
	}

	/**
	 * @return whether toolbar should be shown
	 */
	public boolean isShowToolbar() {
		if (this.appletParameters == null) {
			return false;
		}
		return this.appletParameters.getDataParamShowToolBar(false)
				|| this.appletParameters.getDataParamApp();
	}

	public final int getAppletWidth() {
		return appletWidth;
	}

	public void setAppletWidth(int width) {
		this.appletWidth = width;
	}

	public int getAppletHeight() {
		return appletHeight;
	}

	public void setAppletHeight(int height) {
		this.appletHeight = height;
	}

	/**
	 * @return app width excluding border
	 */
	public int getInnerAppletWidth() {
		int border = getAppletParameters().getBorderThickness();
		return getAppletWidth() - border <= 0 && (getPreferredSize() != null)
				? getPreferredSize().getWidth() : getAppletWidth() - border;
	}

	/**
	 * @return app height excluding border
	 */
	public int getInnerAppletHeight() {
		int border = getAppletParameters().getBorderThickness();
		return getAppletHeight() - border <= 0 && (getPreferredSize() != null)
				? getPreferredSize().getHeight() : getAppletHeight() - border;
	}

	/**
	 * @param fallback
	 *            fallback when computation gives 0
	 * @return width of central pane
	 */
	public int getWidthForSplitPanel(int fallback) {
		int ret = getAppletWidth() - getAppletParameters().getBorderThickness();

		// if it is not 0, there will be some scaling later
		if (ret <= 0) {
			ret = fallback;

			// empirical hack to make room for the toolbar always
			if (showToolBar() && ret < 598) {
				ret = 598; // 2: border
				// maybe this has to be put outside the "if"?
			}
		}
		return ret;
	}

	/**
	 * @param fallback
	 *            fallback if DOM not initialized
	 * @return height excluding toolbar/inputbar
	 */
	public int getHeightForSplitPanel(int fallback) {
		// border excluded
		int windowHeight = getAppletHeight() - getAppletParameters().getBorderThickness();
		// but we want to know the available height for the rootPane
		// so we either use the above as a heuristic,
		// or we should substract the height(s) of
		// toolbar, menubar, and input bar;
		// heuristics come from GeoGebraAppFrame
		if (showAlgebraInput()
				&& getInputPosition() != InputPosition.algebraView) {
			windowHeight -= GLookAndFeelI.COMMAND_LINE_HEIGHT;
		}
		if (showToolBar() && !isUnbundledOrWhiteboard()) {
			windowHeight -= GLookAndFeelI.TOOLBAR_HEIGHT;
		}
		// menubar height is always 0
		if (windowHeight <= 0) {
			windowHeight = fallback;
		}
		return windowHeight;
	}

	/**
	 * Initialize undo info without notifying scripts
	 */
	protected void initUndoInfoSilent() {
		getEventDispatcher().disableListeners();
		kernel.initUndoInfo();
		getEventDispatcher().enableListeners();
	}

	@Override
	public boolean supportsView(int viewID) {
		if (viewID == App.VIEW_CAS) {
			return (getSettings().getCasSettings().isEnabled())
					&& getAppletParameters().getDataParamEnableCAS(true)
					&& getCASFactory().isEnabled();
		}

		if ((getLAF() != null
				&& getLAF().examSupported())
				|| (getLAF() != null && getLAF().isTablet() && !isUnbundled()
						&& !isWhiteboardActive())) {
			if (viewID == App.VIEW_EUCLIDIAN) {
				return getSettings().getEuclidian(1).isEnabled();
			} else if (viewID == App.VIEW_EUCLIDIAN2) {
				return getSettings().getEuclidian(2).isEnabled();
			}
		}

		return viewID != App.VIEW_EUCLIDIAN3D;
	}

	/**
	 * Update toolbar content
	 */
	public void updateToolBar() {
		if (!showToolBar || isIniting()) {
			return;
		}

		if (getGuiManager() != null) {
			getGuiManager().updateToolbar();
		}
	}

	@Override
	public void set1rstMode() {
		setMoveMode();
	}

	@Override
	public void updateApplicationLayout() {
		Log.debug("updateApplicationLayout: Implementation needed...");
	}

	/**
	 * @return applet ID
	 */
	public final String getAppletId() {
		return appletParameters.getDataParamId();
	}

	/**
	 * @return frame widget
	 */
	public abstract GeoGebraFrameW getAppletFrame();

	@Override
	public boolean isScreenshotGenerator() {
		return this.appletParameters.getDataParamScreenshotGenerator();
	}

	/**
	 * Overwritten for applets, full app and for touch
	 *
	 * @return {@link MaterialsManagerI}
	 */
	public MaterialsManagerI getFileManager() {
		return null;
	}

	// ========================================================
	// Undo/Redo
	// ========================================================

	@Override
	public final void storeUndoInfo() {
		if (isUndoActive()) {
			kernel.storeUndoInfo();
			setUnsaved();
		}
	}

	// ========================================================
	// FILE HANDLING
	// ========================================================

	/**
	 * Clear classic input bar
	 */
	protected final void clearInputBar() {
		if (isUsingFullGui() && showAlgebraInput() && getGuiManager() != null) {
			AlgebraInput ai = getGuiManager().getAlgebraInput();
			if (ai != null) {
				ai.setText("");
			}
		}
	}

	// ================================================
	// ERROR HANDLING
	// ================================================

	@Override
	@Deprecated
	// use showError(Errors, String)
	public void showError(String key, String error) {

		String translatedError = getLocalization().getError(key);

		showErrorDialog(translatedError + ":\n" + error);
	}

	@Override
	public void showErrorDialog(final String msg) {
		if (!isErrorDialogsActive()) {
			return;
		}
		if (this.getErrorHandler() != null) {
			this.getErrorHandler().showError(msg);
		}
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
	public void evalJavaScript(App app, String script0, String arg) {
		String script = script0;
		Object exportedApi = ((ScriptManagerW) getScriptManager()).getApi();

		// add eg arg="A"; to start
		if (arg != null) {
			script = "arg=\"" + arg + "\";" + script;
		}
		if (appletParameters.getParamSandbox()) {
			((ScriptManagerW) getScriptManager()).getSandbox()
					.run(script);
		} else {
			ScriptManagerW.export("ggbApplet", exportedApi);
			JsEval.evalScriptNative(script, getGgbApi());
			if (getAppletId().startsWith(ScriptManagerW.ASSESSMENT_APP_PREFIX)) {
				ScriptManagerW.export("ggbApplet", null);
			}
		}
	}

	// ============================================
	// LAYOUT & GUI UPDATES
	// ============================================

	@Override
	public final boolean showAlgebraInput() {
		return showAlgebraInput;
	}

	@Override
	public double getWidth() {
		if (getFrameElement() == null) {
			return 0;
		}
		return getFrameElement().getOffsetWidth();
	}

	@Override
	public double getHeight() {
		if (getFrameElement() == null) {
			return 0;
		}
		return getFrameElement().getOffsetHeight();
	}

	@Override
	public void updateMenubar() {
		// TODO autogenerated
		Log.debug(
				"AppW.updateMenubar() - implementation needed - just finishing");
		// Don't remove this debug message, it is required in
		// test/scripts/benchmark/art-plotter/runtests-sql
	}

	@Override
	public void updateStyleBars() {
		if (!isUsingFullGui() || isIniting()) {
			return;
		}

		if (getEuclidianView1().hasStyleBar()) {
			getEuclidianView1().getStyleBar().updateStyleBar();
		}

		if (hasEuclidianView2(1) && getEuclidianView2(1).hasStyleBar()) {
			getEuclidianView2(1).getStyleBar().updateStyleBar();
		}

		if (isUnbundledOrWhiteboard()) {
			updateDynamicStyleBars();
		}
	}

	@Override
	public void updateDynamicStyleBars() {
		if (getEuclidianView1().hasDynamicStyleBar()) {
			getEuclidianView1().getDynamicStyleBar().updateStyleBar();
		}

		if (hasEuclidianView2(1) && getEuclidianView2(1).hasDynamicStyleBar()) {
			getEuclidianView2(1).getDynamicStyleBar().updateStyleBar();
		}
	}

	/**
	 * Update central panel
	 */
	public void updateCenterPanel() {
		// only needed with GUI
	}

	/**
	 * Resets the width of the Canvas converning the Width of its wrapper
	 * (splitlayoutpanel center)
	 *
	 * @param width
	 *            width in px
	 *
	 * @param height
	 *            height in px
	 */
	public void ggwGraphicsViewDimChanged(int width, int height) {
		// Log.debug("dim changed" + getSettings().getEuclidian(1));
		if (width > 0 && height > 0) {
			getSettings().getEuclidian(1).setPreferredSize(
					AwtFactory.getPrototype().newDimension(width, height));
		}

		// Log.debug("syn size");
		getEuclidianView1().setCoordinateSpaceSize(width, height);
		getEuclidianView1().doRepaint2();
	}

	/**
	 * Resets the width of the Canvas converning the Width of its wrapper
	 * (splitlayoutpanel center)
	 *
	 * @param width
	 *            new view width
	 * @param height
	 *            new view height
	 */
	public void ggwGraphicsView2DimChanged(int width, int height) {
		if (width > 0 && height > 0) {
			getSettings().getEuclidian(2).setPreferredSize(
					AwtFactory.getPrototype().newDimension(width, height));
		}
		// simple setting temp.
		// appCanvasHeight = height;
		// appCanvasWidth = width;

		getEuclidianView2(1).setCoordinateSpaceSize(width, height);
		getEuclidianView2(1).doRepaint2();
	}

	/**
	 * Resets the width of the Canvas converning the Width of its wrapper
	 * (splitlayoutpanel center)
	 *
	 * @param width
	 *            in pixels
	 * @param height
	 *            in pixels
	 */
	public void ggwGraphicsView3DDimChanged(int width, int height) {
		// only used for 3D
	}

	@Override
	public void ensureTimerRunning() {
		this.getTimerSystem().ensureRunning();
	}

	@Override
	public void showCustomizeToolbarGUI() {
		// only with GUI
	}

	/**
	 * shows the on-screen keyboard (or e.g. a show-keyboard-button)
	 *
	 * @param textField
	 *            keyboard listener
	 * @param forceShow
	 *            whether it must appear now
	 * @return whether keybaord is shown
	 */
	public boolean showKeyboard(MathKeyboardListener textField,
			boolean forceShow) {
		return false; // Overwritten in subclass
	}

	/**
	 * update the on-screen keyboard
	 *
	 * @param field
	 *            after the update the input of the keyboard is written into
	 *            this field
	 */
	public void updateKeyBoardField(MathKeyboardListener field) {
		// Overwritten in subclass - nothing to do here
	}

	/**
	 * @return whether app is offline
	 */
	public boolean isOffline() {
		return !getNetworkOperation().isOnline();
	}

	public boolean isOpenedForMacroEditing() {
		return openedForMacroEditing;
	}

	public void setOpenedForMacroEditing(boolean openedForMacroEditing) {
		this.openedForMacroEditing = openedForMacroEditing;
	}

	/**
	 * Updates the URL of the window
	 * @param url the new URL
	 */
	public void updateURL(URL url) {
		if (url != null) {
			DomGlobal.window.history
					.replaceState(null, DomGlobal.document.title, url.toString());
		}
	}

	/**
	 * @param runnable
	 *            callback for closing a header panel
	 */
	public void setCloseBrowserCallback(Runnable runnable) {
		this.closeBroserCallback = runnable;
	}

	/**
	 * Run callback for closing a header panel
	 */
	public void onBrowserClose() {
		if (this.closeBroserCallback != null) {
			this.closeBroserCallback.run();
			this.closeBroserCallback = null;
		}
	}

	public void addInsertImageCallback(Runnable runnable) {
		this.insertImageCallback = runnable;
	}

	/**
	 * @return whether menu is open
	 */
	public boolean isMenuShowing() {
		return false;
	}

	/**
	 * @param heightDiff
	 *            height difference
	 */
	public void addToHeight(int heightDiff) {
		// for applets with keyboard only
	}

	/**
	 * @param perspective
	 *            perspective
	 */
	public void showStartTooltip(Perspective perspective) {
		// probably needed in full version only
	}

	/**
	 * @return left within the page
	 */
	public double getAbsLeft() {
		return this.getFrameElement().getAbsoluteLeft();
	}

	/**
	 * @return top within the page
	 */
	public double getAbsTop() {
		return this.getFrameElement().getAbsoluteTop();
	}

	/**
	 * @return whether file operations (open / save) are allowed
	 */
	public boolean enableOnlineFileFeatures() {
		return this.appletParameters.getDataParamEnableFileFeatures()
				&& getLAF() != null && getLAF().hasLoginButton();
	}

	public boolean enableFileFeatures() {
		return this.appletParameters.getDataParamEnableFileFeatures();
	}

	/**
	 * Update prerelease flag
	 *
	 * @param prerelease
	 *            prerelease parameter
	 */
	public void setPrerelease(boolean prerelease) {
		this.prerelease = prerelease;
	}

	@Override
	public void hideMenu() {
		// for applets with menubar
	}

	/**
	 * @param runnable
	 *            callback for after file is saved
	 */
	public void checkSaved(AsyncOperation<Boolean> runnable) {
		// TODO Auto-generated method stub

	}

	/**
	 *
	 * @param base64
	 *            CSV content
	 */
	public void openCSV(String base64) {
		// TODO Auto-generated method stub

	}

	protected void updateNavigationBars() {
		if (showConstProtNavigationNeedsUpdate == null) {
			return;
		}

		for (int key : showConstProtNavigationNeedsUpdate.keySet()) {
			getGuiManager().getLayout().getDockManager().getPanel(key)
					.updateNavigationBar();
		}
	}

	@Override
	public boolean useShaders() {
		return true;
	}

	/**
	 * @param response
	 *            text of OFF file
	 **/
	public void openOFF(String response) {
		// only makes sense in 3D
	}

	/**
	 * @param pdfFile
	 *            PDF file
	 */
	public void openPDF(File pdfFile) {
		// only makes sense in GUI
	}

	public void openH5P(File pdfFile) {
		// only makes sense in GUI
	}

	/**
	 *
	 * @return Pixel ratio including external transforms
	 */
	public double getPixelRatio() {
		return Browser.getPixelRatio() * geoGebraElement.readScaleX();
	}

	public void addWindowResizeListener(RequiresResize mtg) {
		this.euclidianHandlers.add(mtg);
	}

	public boolean showToolBarHelp() {
		return getAppletParameters().getDataParamShowToolBarHelp(true);
	}

	@Override
	public void setAltText(GeoText altText) {
		getAccessibilityManager().appendAltText(altText);
	}

	@Override
	public GImageIcon wrapGetModeIcon(int mode) {
		return null;
	}

	@Override
	public boolean showAutoCreatedSlidersInEV() {
		return false;
	}

	/**
	 * @return whether stylebar may be shown
	 */
	public boolean allowStylebar() {
		return !isApplet()
				|| getAppletParameters().getDataParamShowMenuBar(false)
				|| getAppletParameters().getDataParamAllowStyleBar();
	}

	@Override
	public boolean showResetIcon() {
		return super.showResetIcon() && !allowStylebar();
	}

	/**
	 * @return whether a file was used for initialization
	 */
	public boolean isStartedWithFile() {
		return !getAppletParameters().getDataParamFileName().isEmpty()
				|| !getAppletParameters().getDataParamBase64String().isEmpty()
				|| !getAppletParameters().getDataParamTubeID().isEmpty()
				|| !getAppletParameters().getDataParamJSON().isEmpty()
				|| (getAppletParameters().getDataParamApp()
						&& NavigatorUtil.getUrlParameter("state") != null);
	}

	@Override
	public GTimer newTimer(GTimerListener listener, int delay) {
		return new GTimerW(listener, delay);
	}

	/**
	 * @param sharingKey
	 *            material sharing key
	 * @param title
	 *            material title
	 */
	public void updateMaterialURL(String sharingKey, String title) {
		setTubeId(sharingKey);
		if (appletParameters.getDataParamApp() && sharingKey != null) {
			Browser.changeUrl(getCurrentURL(sharingKey, false));
			if (!StringUtil.empty(title)) {
				Browser.changeMetaTitle(title);
			}
		}
	}

	public void updateMaterialURL(Material material) {
		updateMaterialURL(material.getSharingKeySafe(), material.getTitle());
	}

	/**
	 * @param sharingKey
	 *            material sharing key
	 * @param absolute
	 *            whether to include host name
	 * @return url
	 */
	public String getCurrentURL(String sharingKey, boolean absolute) {
		String shareLinkPrefix = appletParameters.getParamShareLinkPrefix();
		String host = getHost();
		if (StringUtil.empty(shareLinkPrefix)) {
			shareLinkPrefix = "classic";
		}
		String path = "/" + shareLinkPrefix + "/" + sharingKey;
		return absolute ? host + path : path;
	}

	/**
	 * @return host
	 */
	public String getHost() {
		String apiURL = this.getLoginOperation().getGeoGebraTubeAPI().getUrl();
		return apiURL.substring(0, apiURL.indexOf("/", 12));
	}

	/**
	 * Update rounding from article parameter
	 */
	public void updateRounding() {
		setRounding(getAppletParameters().getDataParamRounding());
	}

	/**
	 * Show perspective picker
	 */
	public void showPerspectivesPopupIfNeeded() {
		// overridden in AppWFull
	}

	/**
	 * @param perspective
	 *            perspective
	 */
	public void setActivePerspective(Perspective perspective) {
		// only for GUI
	}

	/**
	 * Reset settings for enabled views
	 */
	public void resetViewsEnabled() {
		// reset cas and 3d settings for restart of exam
		// needed for GGB-1015
		this.getSettings().getCasSettings().resetEnabled();
		this.getSettings().getEuclidian(-1).resetEnabled();
		getSettings().getEuclidian(1).resetEnabled();
		getSettings().getEuclidian(2).resetEnabled();
		setViewsEnabled();
	}

	/**
	 * @return whether perspectives popup is visible
	 */
	public boolean isPerspectivesPopupVisible() {
		return false;
	}

	/**
	 * @param p
	 *            perspective to be forced after settings
	 */
	public void loadPreferences(Perspective p) {
		// only in full app
	}

	/**
	 *
	 * @return 9999 (or 200 in web)
	 */
	@Override
	public int getMaxSpreadsheetRowsVisible() {
		return Kernel.MAX_SPREADSHEET_ROWS_WEB;
	}

	/**
	 *
	 * @return 9999 (or 200 in web)
	 */
	@Override
	public int getMaxSpreadsheetColumnsVisible() {
		return Kernel.MAX_SPREADSHEET_COLUMNS_WEB;
	}

	@Override
	public void newGeoGebraToPstricks(
			final AsyncOperation<GeoGebraExport> callback) {
		GWT.runAsync(GeoGebraExport.class, new RunAsyncCallback() {

			@Override
			public void onFailure(Throwable reason) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess() {
				LoggerW.loaded("export");
				callback.callback(new GeoGebraToPstricks(AppW.this, new ExportGraphicsFactoryW()));

			}
		});
	}

	@Override
	public void newGeoGebraToAsymptote(
			final AsyncOperation<GeoGebraExport> callback) {
		GWT.runAsync(GeoGebraExport.class, new RunAsyncCallback() {

			@Override
			public void onFailure(Throwable reason) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess() {
				LoggerW.loaded("export");
				callback.callback(new GeoGebraToAsymptote(AppW.this, new ExportGraphicsFactoryW()));

			}
		});
	}

	@Override
	public void newGeoGebraToPgf(
			final AsyncOperation<GeoGebraExport> callback) {
		GWT.runAsync(GeoGebraExport.class, new RunAsyncCallback() {

			@Override
			public void onFailure(Throwable reason) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess() {
				LoggerW.loaded("export");
				callback.callback(new GeoGebraToPgf(AppW.this, new ExportGraphicsFactoryW()));

			}
		});
	}

	/**
	 * Toggle menu visibility
	 */
	public void toggleMenu() {
		// only with GUI
	}

	@Override
	public boolean nativeCAS() {
		return Browser.externalCAS();
	}

	/**
	 * Update central pane and set view sizes
	 */
	public final void updateCenterPanelAndViews() {
		updateCenterPanel();
		updateViewSizes();
	}

	/**
	 * Recompute height of split panel
	 */
	public void updateSplitPanelHeight() {
		// implemented in subclass
	}

	@Override
	public boolean isUnbundled() {
		return AppConfigDefault.isUnbundled(getConfig().getAppCode());
	}

	@Override
	public boolean isUnbundledGraphing() {
		return GRAPHING_APPCODE.equals(getSubAppCode());
	}

	@Override
	public boolean isUnbundledGeometry() {
		return GEOMETRY_APPCODE.equals(getSubAppCode());
	}

	/**
	 * @return whether we are running 3D grapher
	 */
	public boolean isUnbundled3D() {
		return G3D_APPCODE.equals(getSubAppCode());
	}

	@Override
	public boolean isSuite() {
		return SUITE_APPCODE.equals(getConfig().getAppCode());
	}

	/**
	 * @return whether we are running cas
	 */
	public boolean isUnbundledCas() {
		return CAS_APPCODE.equals(getSubAppCode());
	}

	/**
	 * @return the sub app code, if it exists, or the app code
	 */
	private String getSubAppCode() {
		return getConfig().getSubAppCode() != null
				? getConfig().getSubAppCode()
				: getConfig().getAppCode();
	}

	/**
	 * Zoom to standard view
	 */
	public void ensureStandardView() {
		// only with GUI
	}

	/**
	 *
	 * @return true if app is on portrait mode;
	 */
	@Override
	public boolean isPortrait() {
		if (getAppletFrame() != null) {
			return getAppletFrame().computeWidth() < getAppletFrame().computeHeight();
		}
		return getWidth() < getHeight();
	}

	/**
	 * Open share dialog (native or HTML popup)
	 */
	public void share() {
		// only with GUI
	}

	@Override
	public void exportStringToFile(String extension, String content, boolean showDialog) {
		String url;

		if ("html".equals(extension)) {
			url = StringUtil.htmlMarker + content;
		} else {
			url = Browser.addTxtMarker(content);
			// do we ever need this?
			// url = StringUtil.txtMarker + URL.encodePathSegment(content);
		}

		dispatchEvent(new Event(EventType.OPEN_DIALOG, null, "export3D"));
		if (showDialog) {
			getFileManager().showExportAsPictureDialog(url, getExportTitle(),
					extension, "Export", this);
		} else {
			getFileManager().exportImage(url,  getExportTitle() + "." + extension,
					extension);
			dispatchEvent(new Event(EventType.EXPORT, null,
					"[\"" + extension + "\"]"));
		}
	}

	/**
	 * @return file extension of current construction
	 */
	public String getFileExtension() {
		return getPageController() == null ? ".ggb" : ".ggs";
	}

	@Override
	public void handleImageExport(String base64image0) {

		String base64image = base64image0;
		if (base64image.startsWith("<svg")) {
			// can't use data:image/svg+xml;utf8 in IE11 / Edge
			base64image = Browser.encodeSVG(base64image);
		} else if (base64image.startsWith("JVBER")) {
			base64image = StringUtil.pdfMarker + base64image;
		} else if (base64image.startsWith("iVBOR")) {
			// PNG
			base64image = StringUtil.pngMarker + base64image;
		}

		if (getDialogManager() != null) {
			getDialogManager().showExportImageDialog(base64image);

		} else {
			LightBox.showImage(base64image,
					base64image.startsWith(StringUtil.pdfMarker));
		}

	}

	@Override
	public String getSlideID() {
		return getPageController() == null
				? GgbFile.SLIDE_PREFIX + GgbFile.getCounter()
				: getPageController().getSlideID();
	}

	@Override
	public void copyGraphicsViewToClipboard() {
		EuclidianViewWInterface ev = (EuclidianViewWInterface) getActiveEuclidianView();
		copyImageToClipboard(ev.getExportImageDataUrl(3, false, false));
	}

	@Override
	public void copyImageToClipboard(String dataURI) {
		if (!ClipboardUtil.isCopyImageToClipboardAvailable()) {
			Log.debug("window.copyGraphicsToClipboard() not available");
			return;
		}
		try {
			ClipboardUtil.copyGraphicsToClipboard(dataURI);
		} catch (Exception e) {
			Log.warn("Clipboard API is new and maybe half-implemented in your browser.");
		}
	}

	/**
	 * @return zip archive handler
	 */
	public ArchiveLoader getArchiveLoader() {
		if (archiveLoader == null) {
			archiveLoader = new ArchiveLoader(this);
		}
		return archiveLoader;
	}

	/**
	 * @param html
	 *            whether to include HTML wrapper
	 */
	public void exportCollada(boolean html) {
		// empty for webSimple
	}

	/**
	 * @param zoomPanel
	 *            zoom panel
	 */
	public void setZoomPanel(ZoomPanel zoomPanel) {
		this.zoomPanel = zoomPanel;
	}

	/**
	 * @param run
	 *            localization callback
	 */
	public void afterLocalizationLoaded(Runnable run) {
		// overridden in Full
	}

	public EmbedManager newEmbedManager() {
		return null;
	}

	/**
	 * Force resize after 0 timeout. In Chrome this is needed after switching
	 * between tabs, in iOS safari for resizing.
	 */
	public void deferredForceResize() {
		invokeLater(() -> EuclidianViewW.forceResize(getEuclidianView1()));

	}

	/**
	 * @param popup
	 *            popup that should close on resize
	 */
	public void registerAutoclosePopup(GPopupPanel popup) {
		popupRegistry.add(popup);
	}

	public void closePopupsInRegistry() {
		popupRegistry.closeAll();
	}

	/**
	 * @return vendor dependent settings
	 */
	public VendorSettings getVendorSettings() {
		if (vendorSettings == null) {
			if (isMebis()) {
				vendorSettings = new MebisSettings();
			} else {
				vendorSettings = new GgbSettings();
			}
		}
		return vendorSettings;
	}

	@Override
	public boolean isMebis() {
		return "mebis".equalsIgnoreCase(appletParameters.getParamVendor());
	}

	@Override
	public DefaultSettings getDefaultSettings() {
		if (defaultSettings == null) {
			defaultSettings = new DefaultSettingsW();
		}
		return defaultSettings;
	}

	@Override
	public final AccessibilityManagerInterface getAccessibilityManager() {
		if (accessibilityManager == null) {
			accessibilityManager = new AccessibilityManagerW(this);
		}
		return accessibilityManager;
	}

	public ZoomPanel getZoomPanel() {
		return zoomPanel;
	}

	@Override
	public SettingsBuilder newSettingsBuilder() {
		return new SettingsBuilderW(this);
	}

	@Override
	public CopyPasteW getCopyPaste() {
		if (copyPaste == null) {
			copyPaste = new CopyPasteW();
		}

		return copyPaste;
	}

	/**
	 *
	 * @return then embedded calculator apis.
	 */
	public JsPropertyMap<Object> getEmbeddedCalculators(boolean includeGraspableMath) {
		// implemented in AppWFull
		return null;
	}

	@Override
	public GColor getPrimaryColor() {
		return getVendorSettings().getPrimaryColor();
	}

	@Override
	public FpsProfiler getFpsProfiler() {
		if (fpsProfiler == null) {
			fpsProfiler = new FpsProfilerW();
		}
		return fpsProfiler;
	}

	@Override
	public void testDraw() {
		getEuclidianController().getMouseTouchGestureController().getDrawingEmulator().draw();
	}

	@Override
	protected EuclidianControllerW getEuclidianController() {
		return (EuclidianControllerW) super.getEuclidianController();
	}

	@Override
	public void startDrawRecording() {
		getEuclidianController().getMouseTouchGestureController().startDrawRecording();
	}

	@Override
	public void endDrawRecordingAndLogResults() {
		getEuclidianController().getMouseTouchGestureController().endDrawRecordingAndLogResult();
	}

	/**
	 * @return manager for showing/hiding keyboard
	 */
	public @CheckForNull KeyboardManagerInterface getKeyboardManager() {
		return null;
	}

	/**
	 * Create provider of texts for ANS button.
	 *
	 * @return provider of last AV item
	 */
	public HasLastItem getLastItemProvider() {
		return null;
	}

	/**
	 * @return accessibility view
	 */
	public AccessibilityView getAccessibilityView() {
		if (this.accessibilityView == null) {
			accessibilityView = new AccessibilityView(this,
					new BaseWidgetFactory());
		}
		return accessibilityView;
	}

	/**
	 * Connect voiceover with the right panel
	 */
	public void updateVoiceover() {
		if (Browser.needsAccessibilityView()) {
			invokeLater(() -> getAccessibilityView().rebuild());
		}
	}

	public SignInControllerI getSignInController() {
		return getLAF().getSignInController(this);
	}

	/**
	 * reset url after e.g. new file
	 */
	public void resetUrl() {
		if (appletParameters.getDataParamApp()) {
			Browser.resetUrl();
			Browser.changeUrl("/" + appletParameters.getParamShareLinkPrefix());
		}
	}

	/**
	 * send event for open/close keyboard
	 * @param openKeyboard true if open keyboard event should be sent
	 */
	public void sendKeyboardEvent(boolean openKeyboard) {
		if (getConfig().sendKeyboardEvents()) {
			if (openKeyboard && !getAppletFrame().isKeyboardShowing()) {
				dispatchEvent(new Event(EventType.OPEN_KEYBOARD));
			}
			if (!openKeyboard) {
				dispatchEvent(new Event(EventType.CLOSE_KEYBOARD));
			}
		}
	}

	/**
	 * @return whether a file with multiple slides is open
	 */
	public boolean isMultipleSlidesOpen() {
		return getPageController() != null && getPageController().getSlideCount() > 1;
	}

	public GlobalHandlerRegistry getGlobalHandlers() {
		return dropHandlers;
	}

	/**
	 * If the current app supports subapps, witch suite to the given subapp,
	 * clearing all construction, and resetting almost all the settings
	 * @param appCode "graphing", "3d", "cas", "geometry" or "probability"
	 */
	public void switchToSubapp(String appCode) {
		// only with UI
	}

	public void setLastFocusableWidget(Widget lastFocusableWidget) {
		this.lastFocusableWidget = lastFocusableWidget;
	}

	/**
	 * Move focus to the last element
	 */
	public void moveFocusToLastWidget() {
		if (lastFocusableWidget != null) {
			lastFocusableWidget.getElement().setInnerText("");
			lastFocusableWidget.getElement().focus();
		}
	}

	@Override
	public StringTemplate getScreenReaderTemplate() {
		return getAppletParameters().getParamScreenReaderMode(NavigatorUtil.isMobile()
				|| NavigatorUtil.isMacOS())
				? StringTemplate.screenReaderAscii
				: StringTemplate.screenReaderUnicode;
	}

	@Override
	public void resetCommandDict() {
		super.resetCommandDict();
		setLabels(); // rebuilds input help panel
	}

	/**
	 * @return fullscreen state data
	 */
	public FullScreenState getFullscreenState() {
		if (fullscreenState == null) {
			fullscreenState = new FullScreenState();
		}
		return fullscreenState;
	}

	/**
	 * @return manager for snackbars
	 */
	public ToolTipManagerW getToolTipManager() {
		if (toolTipManager == null) {
			toolTipManager = new ToolTipManagerW();
		}
		return toolTipManager;
	}

	public boolean isLockedExam() {
		return !StringUtil.empty(getAppletParameters().getParamExamMode());
	}

	/**
	 * @param category - category name
	 * @return check if category should be enabled based on customToolbox parameter
	 */
	public boolean isToolboxCategoryEnabled(String category) {
		List<String> tools = getAppletParameters().getDataParamCustomToolbox();
		return tools.contains(category) || tools.isEmpty();
	}
}
