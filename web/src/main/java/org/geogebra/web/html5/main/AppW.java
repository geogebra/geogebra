package org.geogebra.web.html5.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.geogebra.common.GeoGebraConstants.Versions;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.DrawEquation;
import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.export.pstricks.GeoGebraExport;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.factories.CASFactory;
import org.geogebra.common.factories.Factory;
import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.geogebra3D.kernel3D.commands.CommandDispatcher3D;
import org.geogebra.common.gui.AccessibilityManagerInterface;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.gui.view.algebra.AlgebraView.SortMode;
import org.geogebra.common.io.MyXMLio;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.javax.swing.GImageIcon;
import org.geogebra.common.javax.swing.GOptionPane;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.GeoFactory;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.UndoManager;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.AppConfigDefault;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.FontManager;
import org.geogebra.common.main.GeoElementSelectionListener;
import org.geogebra.common.main.MaterialsManagerI;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.main.SpreadsheetTableModel;
import org.geogebra.common.main.SpreadsheetTableModelSimple;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.main.settings.AlgebraSettings;
import org.geogebra.common.main.settings.DefaultSettings;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.SettingsBuilder;
import org.geogebra.common.media.VideoManager;
import org.geogebra.common.move.events.BaseEventPool;
import org.geogebra.common.move.ggtapi.models.ClientInfo;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.Provider;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.common.move.operations.Network;
import org.geogebra.common.move.operations.NetworkOperation;
import org.geogebra.common.move.views.OfflineView;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.ScriptManager;
import org.geogebra.common.plugin.SensorLogger;
import org.geogebra.common.sound.SoundManager;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimerListener;
import org.geogebra.common.util.ImageManager;
import org.geogebra.common.util.MD5EncrypterGWTImpl;
import org.geogebra.common.util.NormalizerMinimal;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.common.util.lang.Language;
import org.geogebra.common.util.profiler.FpsProfiler;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.awt.GDimensionW;
import org.geogebra.web.html5.awt.GFontW;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.euclidian.EuclidianControllerW;
import org.geogebra.web.html5.euclidian.EuclidianPanelWAbstract;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.euclidian.MouseTouchGestureControllerW;
import org.geogebra.web.html5.euclidian.profiler.FpsProfilerW;
import org.geogebra.web.html5.export.GeoGebraToAsymptoteW;
import org.geogebra.web.html5.export.GeoGebraToPgfW;
import org.geogebra.web.html5.export.GeoGebraToPstricksW;
import org.geogebra.web.html5.factories.AwtFactoryW;
import org.geogebra.web.html5.factories.FactoryW;
import org.geogebra.web.html5.factories.FormatFactoryW;
import org.geogebra.web.html5.factories.UtilFactoryW;
import org.geogebra.web.html5.gui.AlgebraInput;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.LoadingApplication;
import org.geogebra.web.html5.gui.ToolBarInterface;
import org.geogebra.web.html5.gui.accessibility.AccessibilityManagerW;
import org.geogebra.web.html5.gui.accessibility.PerspectiveAccessibilityAdapter;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.gui.laf.GgbSettings;
import org.geogebra.web.html5.gui.laf.MebisSettings;
import org.geogebra.web.html5.gui.laf.VendorSettings;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.util.LightBox;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.gui.util.ViewsChangedListener;
import org.geogebra.web.html5.gui.zoompanel.ZoomPanel;
import org.geogebra.web.html5.io.ConstructionException;
import org.geogebra.web.html5.io.MyXMLioW;
import org.geogebra.web.html5.javax.swing.GOptionPaneW;
import org.geogebra.web.html5.kernel.GeoElementGraphicsAdapterW;
import org.geogebra.web.html5.kernel.UndoManagerW;
import org.geogebra.web.html5.kernel.commands.CommandDispatcherW;
import org.geogebra.web.html5.main.settings.DefaultSettingsW;
import org.geogebra.web.html5.main.settings.SettingsBuilderW;
import org.geogebra.web.html5.move.googledrive.GoogleDriveOperation;
import org.geogebra.web.html5.sound.GTimerW;
import org.geogebra.web.html5.sound.SoundManagerW;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.html5.util.ArticleElementInterface;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.ImageLoadCallback;
import org.geogebra.web.html5.util.ImageManagerW;
import org.geogebra.web.html5.util.ImageWrapper;
import org.geogebra.web.html5.util.NetworkW;
import org.geogebra.web.html5.util.UUIDW;
import org.geogebra.web.html5.util.ViewW;
import org.geogebra.web.html5.util.debug.LoggerW;
import org.geogebra.web.html5.video.VideoManagerW;
import org.geogebra.web.plugin.WebsocketLogger;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class AppW extends App implements SetLabels, HasLanguage {
	public static final String STORAGE_MACRO_KEY = "storedMacro";
	public static final String STORAGE_MACRO_ARCHIVE = "macroArchive";

	private static final int LOWER_HEIGHT = 350;
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
	protected GoogleDriveOperation googleDriveOperation;

	protected FontManagerW fontManager;
	private SpreadsheetTableModelSimple tableModel;
	private SoundManagerW soundManager;
	private VideoManagerW videoManager;
	private AsyncManager asyncManager;

	protected MaterialsManagerI fm;
	private Material activeMaterial;

	protected final ArticleElementInterface articleElement;

	protected EuclidianPanelWAbstract euclidianViewPanel;
	protected Canvas canvas;

	private final GLookAndFeelI laf;

	protected ArrayList<Widget> popups = new ArrayList<>();
	// protected GeoGebraFrame frame = null;

	private GlobalKeyDispatcherW globalKeyDispatcher;

	// when losing focus, remembering it so that ENTER can give focus back
	private static volatile Element lastActiveElement = null;
	// but not in case of anything important in any app has focus,
	// we shall set it to true in each of those cases, e.g. AV input bar too !!!
	private static boolean anyAppHasFocus = true;

	private ReaderTimer readerTimer;
	private boolean toolLoadedFromStorage;
	private Storage storage;
	WebsocketLogger webSocketLogger = null;
	private boolean keyboardNeeded;
	private ArrayList<ViewsChangedListener> viewsChangedListener = new ArrayList<>();
	private GDimension preferredSize;
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
	private ArrayList<MouseTouchGestureControllerW> euclidianHandlers = new ArrayList<>();
	private ViewW viewW;
	private ZoomPanel zoomPanel;
	private PopupRegistry popupRegistry = new PopupRegistry();
	private VendorSettings vendorSettings;
	private DefaultSettings defaultSettings;
	private FpsProfiler fpsProfiler;

	Timer timeruc = new Timer() {
		@Override
		public void run() {
			updateConsBoundingBox();
		}
	};

	Scheduler.ScheduledCommand sucCallback = new Scheduler.ScheduledCommand() {
		@Override
		public void execute() {
			// 0.5 seconds is good for the user and maybe for the computer
			// too
			timeruc.schedule(500);
		}
	};

	/**
	 * @param articleElement
	 *            {@link ArticleElement}
	 * @param dimension
	 *            int
	 * @param laf
	 *            (null for webSimple) {@link GLookAndFeelI}
	 */
	protected AppW(ArticleElementInterface articleElement, int dimension,
			GLookAndFeelI laf) {
		super(getVersion(articleElement, dimension, laf));
		setPrerelease(articleElement.getDataParamPrerelease());

		// laf = null in webSimple
		setUndoRedoEnabled(articleElement.getDataParamEnableUndoRedo()
				&& (laf == null || laf.undoRedoSupported()));

		this.loc = new LocalizationW(dimension);
		this.articleElement = articleElement;
		NativeFocusHandler.addNativeFocusHandler(articleElement.getElement(),
				this);
		this.laf = laf;

		getTimerSystem();
		this.showInputTop = InputPosition.algebraView;
		Window.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				fitSizeToScreen();
				windowResized();
				closePopupsInRegistry();
			}
		});
		if (!StringUtil
				.empty(getArticleElement().getParamScaleContainerClass())) {
			Browser.addMutationObserver(getParent(
					getArticleElement().getParamScaleContainerClass()),
					new AsyncOperation<String>() {

						@Override
						public void callback(String obj) {
							checkScaleContainer();
						}
					});
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
		if (getArticleElement().getDataParamFitToScreen()
				|| (zoomPanel != null && zoomPanel.isFullScreen())) {
			return;
		}
		Log.debug("RESIZE: Start");
		if (!StringUtil
				.empty(getArticleElement().getParamScaleContainerClass())) {
			Element parent = getParent(
					getArticleElement().getParamScaleContainerClass());
			if (parent != null) {
				scaleTo(parent.getOffsetWidth(),
						Math.max(
								getArticleElement().getParamAutoHeight()
								? parent.getOffsetWidth()
								: 0, parent.getOffsetHeight()));
				if (parent != this.getScalerParent()) {
					resizeContainer();
				}
			}
		} else if (!getArticleElement().getParamDisableAutoScale()) {
			int border = 0;
			// only apply right border if left border is nonzero: important for
			// iframes
			if (this.getAbsLeft() > 0) {
				border = Window.getClientWidth() > SCREEN_WIDTH_THRESHOLD
					? BIG_SCREEN_MARGIN : SMALL_SCREEN_MARGIN;
			}
			int width = Window.getClientWidth() - (int) getAbsLeft() - border;
			scaleTo(width, Window.getClientHeight());
			resizeContainer();
		} else {
			scaleWithRatio(getArticleElement().getDataParamScale());
		}
		recalculateEnvironments();
	}

	private void resizeContainer() {
		if (getScalerParent() != null) {
			Style style = getScalerParent().getStyle();
			double scale = articleElement.getScaleX();
			style.setWidth(getWidth() * scale, Unit.PX);
			style.setHeight(getHeight() * scale, Unit.PX);
		}
	}

	private Element getScalerParent() {
		return articleElement.getParentElement() == null ? null
				: articleElement.getParentElement().getParentElement();
	}

	private void scaleTo(int width, int height) {
		double xscale = width / getWidth();
		double yscale = height / getHeight();
		boolean upscale = getArticleElement().getParamAllowUpscale();
		double scale = LayoutUtilW.getDeviceScale(xscale, yscale, upscale);
		if (!upscale) {
			scale = Math.min(scale, getArticleElement().getDataParamScale());
		}
		scaleWithRatio(scale);
	}

	/**
	 * @param scale
	 *            scale ratio
	 */
	public void scaleWithRatio(double scale) {
		Browser.scale(articleElement.getParentElement(), scale, 0, 0);
		getArticleElement().resetScale(scale);
		deferredForceResize();
	}

	private Element getParent(String containerClass) {
		Element current = getArticleElement().getParentElement();
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
		Element header = Dom.querySelector("GeoGebraHeader");
		if (header != null) {
			header.removeFromParent();
			getArticleElement().attr("marginTop", "0");
			fitSizeToScreen();
		}
	}

	/**
	 * Called if header visibility is changed.
	 */
	public void onHeaderVisible() {
		// TODO listener (?)
	}

	private static Versions getVersion(ArticleElementInterface ae,
			int dimension,
			GLookAndFeelI laf2) {
		return laf2 == null ? Versions.WEB_FOR_BROWSER_SIMPLE
				: laf2.getVersion(dimension, ae.getDataParamAppName());
	}

	/**
	 * handler for window resize
	 */
	protected final void windowResized() {
		for (MouseTouchGestureControllerW mtg : this.euclidianHandlers) {
			mtg.calculateEnvironment();
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
		uniqueId = UUIDW.randomUUID().toString();
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
			drawEquation = new DrawEquationW();
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

	@Override
	public final VideoManager getVideoManager() {
		if (videoManager == null) {
			videoManager = new VideoManagerW();
		}
		return videoManager;
	}

	/**
	 * Use the async manager's schedule method when you try to access
	 * parts of the code that have been split by the GWT compiler
	 * @return the instance of the AsyncManager
	 */
	public final AsyncManager getAsyncManager() {
		if (asyncManager == null) {
			asyncManager = new AsyncManager(this);
			asyncManager.ensureModulesLoaded(articleElement.getDataParamPreloadModules());
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

	/**
	 * @return {@link Canvas}
	 */
	public Canvas getCanvas() {
		return canvas;
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

		if (FormatFactory.getPrototype() == null) {
			FormatFactory.setPrototypeIfNull(new FormatFactoryW());
		}

		if (AwtFactory.getPrototype() == null) {
			AwtFactory.setPrototypeIfNull(new AwtFactoryW());
		}

		if (StringUtil.getPrototype() == null) {
			StringUtil.setPrototypeIfNull(new StringUtil());
		}

		if (!CASFactory.isInitialized()) {
			CASFactory.setPrototype((CASFactory) GWT.create(CASFactory.class));
		}

		if (UtilFactory.getPrototype() == null) {
			UtilFactory.setPrototypeIfNull(new UtilFactoryW());
		}
	}

	@Override
	final public GlobalKeyDispatcherW getGlobalKeyDispatcher() {
		if (globalKeyDispatcher == null) {
			globalKeyDispatcher = newGlobalKeyDispatcher();
			if (articleElement != null && articleElement.getDataParamApp()) {
				globalKeyDispatcher.setFocused(true);
			}
		}
		return globalKeyDispatcher;
	}

	/**
	 * @return a new instance of {@link GlobalKeyDispatcherW}
	 */
	private GlobalKeyDispatcherW newGlobalKeyDispatcher() {
		return new GlobalKeyDispatcherW(this);
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
		return new ScriptManagerW(this);
	}

	// ================================================
	// native JS
	// ================================================

	@Override
	public void callAppletJavaScript(String fun, String... args) {
		if (args == null || args.length == 0) {
			JsEval.callNativeJavaScript(fun);
		} else if (args.length == 1) {
			Log.debug("calling function: " + fun + "(" + args[0] + ")");
			JsEval.callNativeJavaScript(fun, args[0]);
		} else {
			JsArrayString jsStrings = (JsArrayString) JavaScriptObject
					.createArray();
			for (Object obj : args) {
				jsStrings.push(obj.toString());
			}
			JsEval.callNativeJavaScriptMultiArg(fun, jsStrings);
		}
	}

	@Override
	public boolean loadXML(final String xml) throws Exception {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					getXMLio().processXMLString(xml, true, false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		getAsyncManager().scheduleCallback(r);

		return true;
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
		if (asyncCall && getGuiManager() != null) {
			getGuiManager().updateKeyboardLanguage();
		}
	}

	/**
	 * Notify components about initial localization load
	 */
	public void notifyLocalizationLoaded() {
		// TODO Auto-generated method stub
	}

	@Override
	public void setLanguage(final String browserLang) {
		final String lang = Language
				.getClosestGWTSupportedLanguage(browserLang).getLocaleGWT();
		getLocalization().cancelCallback();
		if (lang != null && lang.equals(loc.getLocaleStr())) {
			Log.debug("Language is already " + loc.getLocaleStr());
			setLabels();
			notifyLocalizationLoaded();
			return;
		}
		if (lang == null || "".equals(lang)) {
			Log.warn("language being set to empty string");
			setLanguage("en");
			return;
		}

		Log.debug("setting language to:" + lang + ", browser lang:"
				+ browserLang);
		getLocalization().loadScript(lang, this);
	}

	/**
	 * @param language
	 *            language ISO code
	 * @param country
	 *            country or country_variant
	 */
	public void setLanguage(String language, String country) {

		if (language == null || "".equals(language)) {
			Log.warn("error calling setLanguage(), setting to English (US): "
					+ language + "_" + country);
			setLanguage("en");
			return;
		}

		if (country == null || "".equals(country)) {
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
		Runnable r = new Runnable() {
			@Override
			public void run() {
				loadFileWithoutErrorHandling(archiveContent, asSlide);
			}
		};

		getAsyncManager().scheduleCallback(r);
	}

	/**
	 * Try loading a file only once (might fail with CommandNotLoadedError)
	 * @param archiveContent
	 *            zip archive content
	 * @param asSlide
	 *            whether to reload just a slide
	 */
	public void loadFileWithoutErrorHandling(GgbFile archiveContent, boolean asSlide) {
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
	 * @param isggs
	 *            whether the extension is GGS
	 */
	public void loadGgbFileAsBase64Again(String dataUrl, boolean isggs) {
		prepareReloadGgbFile();
		ViewW view = getViewW();
		if (!isggs && getEmbedManager() != null) {
			getEmbedManager().embed(dataUrl);
		} else {
			view.processBase64String(dataUrl);
		}
	}

	/**
	 * Loads a binary file (ggb, ggs)
	 *
	 * @param binary
	 *            binary file
	 */
	public void loadGgbFileAsBinaryAgain(JavaScriptObject binary) {
		prepareReloadGgbFile();
		ViewW view = getViewW();
		view.processBinaryString(binary);
	}

	private void prepareReloadGgbFile() {
		getImageManager().reset();
	}

	private void loadFile(GgbFile archiveContent, final boolean asSlide)
			throws Exception {
		if (archiveContent.containsKey(GgbFile.STRUCTURE_JSON)) {
			getArticleElement().attr("appName", "notes");
			getAppletFrame().initPageControlPanel(this);
			if (getPageController() != null) {
				getEuclidianView1().initBgCanvas();
				getPageController().loadSlides(archiveContent);
				return;
			}
		}

		beforeLoadFile(asSlide);

		GgbFile archive = archiveContent.duplicate("tmp");

		// Handling of construction and macro file
		final String construction = archive.remove(MyXMLio.XML_FILE);
		final String macros = archive.remove(MyXMLio.XML_FILE_MACRO);
		final String defaults2d = archive.remove(MyXMLio.XML_FILE_DEFAULTS_2D);
		final String defaults3d = is3D()
				? archive.remove(MyXMLio.XML_FILE_DEFAULTS_3D) : null;

		String libraryJS = archive.remove(MyXMLio.JAVASCRIPT_FILE);

		// Construction (required)
		if (construction == null && macros == null) {
			throw new ConstructionException(
					"File is corrupt: No GeoGebra data found");
		}

		if (construction != null) {
			// ggb file: remove all macros from kernel before processing
			kernel.removeAllMacros();
		}

		// Library JavaScript (optional)
		if (libraryJS == null) { // TODO: && !isGGTfile)
			kernel.resetLibraryJavaScript();
		} else {
			kernel.setLibraryJavaScript(libraryJS);
		}
		HashMap<String, String> toLoad = new HashMap<>();
		for (Entry<String, String> entry : archive.entrySet()) {

			String key = entry.getKey();

			if (getImageManager().getExternalImage(key, this, false) == null) {
				maybeProcessImage(key, entry.getValue(), toLoad);
			}
		}
		if (getEmbedManager() != null) {
			getEmbedManager().loadEmbeds(archive);
		}
		if (construction == null) {
			if (macros != null) {
				getXMLio().processXMLString(macros, true, true);
			}

			setCurrentFile(archiveContent);
			afterLoadFileAppOrNot(asSlide);
			if (!hasMacroToRestore()) {
				getGuiManager().refreshCustomToolsInToolBar();
			}
			getGuiManager().updateToolbar();
			return;
		}
		Runnable afterImages = new Runnable() {

			@Override
			public void run() {
				try {
					setHideConstructionProtocolNavigation();
					Log.debug("images loaded");
					// Macros (optional)
					if (macros != null) {
						// macros = DataUtil.utf8Decode(macros);
						// //DataUtil.utf8Decode(macros);
						getXMLio().processXMLString(macros, true, true);
					}
					int seed = getArticleElement().getParamRandomSeed();
					if (seed != -1) {
						setRandomSeed(seed);
					}
					getXMLio().processXMLString(construction, true, false,
							true);
					// defaults (optional)
					if (defaults2d != null) {
						getXMLio().processXMLString(defaults2d, false, true);
					}
					if (defaults3d != null) {
						getXMLio().processXMLString(defaults3d, false, true);
					}
					afterLoadFileAppOrNot(asSlide);

				} catch (Exception e) {
					Log.debug(e);
				}
			}

		};
		if (toLoad.isEmpty()) {
			afterImages.run();
			setCurrentFile(archiveContent);
			// getKernel().setNotifyViewsActive(true);
		} else {
			// on images do nothing here: wait for callback when images loaded.
			getImageManager().triggerImageLoading(this, afterImages, toLoad);
			setCurrentFile(archiveContent);
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
					macro.setViewId(VIEW_EUCLIDIAN3D);
				} else {
					macro.setViewId(VIEW_EUCLIDIAN);
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
				loadGgbFile(currentFile, false);
			} catch (Exception e) {
				clearConstruction();
			}
		} else {
			clearConstruction();
		}
	}

	private boolean maybeProcessImage(String filename0, String content,
			HashMap<String, String> toLoad) {
		String fn = filename0.toLowerCase();
		if (fn.equals(MyXMLio.XML_FILE_THUMBNAIL)) {
			return false; // Ignore thumbnail
		}

		FileExtensions ext = StringUtil.getFileExtension(fn);

		// Ignore non image files
		if (!ext.isImage()) {
			return false;
		}
		String filename = filename0;
		// bug in old versions (PNG saved with wrong extension)
		// change BMP, TIFF, TIF -> PNG
		if (!ext.isAllowedImage()) {
			filename = StringUtil.changeFileExtension(filename,
					FileExtensions.PNG);
		}

		// for file names e.g. /geogebra/main/nav_play.png in GeoButtons
		// Log.debug("filename2 = " + filename);
		// Log.debug("ext2 = " + ext);

		if (ext.equals(FileExtensions.SVG)) {
			// IE11/Edge needs SVG to be base64 encoded
			String fixedContent =
					Browser.encodeSVG(ImageManager.fixSVG(content));
			getImageManager().addExternalImage(filename, fixedContent);
			toLoad.put(filename, fixedContent);
		} else {
			getImageManager().addExternalImage(filename, content);
			toLoad.put(filename, content);
		}
		return true;
	}

	@Override
	public final ImageManagerW getImageManager() {
		return imageManager;
	}

	protected void initImageManager() {
		imageManager = new ImageManagerW();
	}

	@Override
	public final void setXML(String xml, boolean clearAll) {
		if (clearAll) {
			setCurrentFile(null);
		}

		try {
			// make sure objects are displayed in the correct View
			setActiveView(App.VIEW_EUCLIDIAN);
			getXMLio().processXMLString(xml, clearAll, false);
		} catch (MyError err) {
			err.printStackTrace();
			showError(err);
		} catch (Exception e) {
			e.printStackTrace();
			showError(Errors.LoadFileFailed);
		}
	}

	@Override
	public boolean clearConstruction() {
		// if (isSaved() || saveCurrentFile()) {
		kernel.clearConstruction(true);

		kernel.initUndoInfo();
		resetMaxLayerUsed();
		setCurrentFile(null);
		setMoveMode();

		return true;

		// }
		// return false;
	}

	@Override
	public final MyImage getExternalImageAdapter(String fileName, int width,
			int height) {
		ImageElement im = getImageManager().getExternalImage(fileName, this,
				true);
		if (im == null) {
			return null;
		}
		if (width != 0 && height != 0) {
			im.setWidth(width);
			im.setHeight(height);
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
		invokeLater(new Runnable() {
			@Override
			public void run() {
				geo1.runClickScripts(string);
			}
		});
	}

	@Override
	public void invokeLater(final Runnable runnable) {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				runnable.run();
			}
		});
	}

	@Override
	public final CASFactory getCASFactory() {
		return CASFactory.getPrototype();
	}

	@Override
	public final void fileNew() {
		// clear all
		// triggers the "do you want to save" dialog
		// so must be called first
		if (!clearConstruction()) {
			return;
		}
		clearMedia();
		resetUniqueId();
		setLocalID(-1);
		resetActiveMaterial();

		if (getGoogleDriveOperation() != null) {
			getGoogleDriveOperation().resetStorageInfo();
		}
		resetUI();
	}

	/**
	 * Remove all widgets for videos and embeds.
	 */
	@Override
	public void clearMedia() {
		if (getVideoManager() != null) {
			getVideoManager().removePlayers();
		}
		if (getEmbedManager() != null) {
			getEmbedManager().removeAll();
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
	 * @param macro
	 *            Macro need to be stored.
	 * @param writeBack
	 *            Is it a new one or a modification.
	 */
	public void storeMacro(Macro macro, boolean writeBack) {
		createStorage();
		if (storage == null) {
			return;
		}

		String b64 = getGgbApi().getMacrosBase64();

		storage.setItem(STORAGE_MACRO_ARCHIVE, b64);

		storage.setItem(STORAGE_MACRO_KEY, macro.getToolName());
	}

	protected void createStorage() {
		if (storage == null) {
			storage = Storage.getSessionStorageIfSupported();
		}
	}

	protected boolean hasMacroToRestore() {
		createStorage();
		if (storage != null) {
			return storage.getItem(STORAGE_MACRO_ARCHIVE) != null;
		}

		return false;
	}

	protected void restoreMacro() {
		createStorage();
		if (storage != null) {
			if (storage.getItem(STORAGE_MACRO_ARCHIVE) != null) {
				getKernel().removeAllMacros();
				String b64 = storage.getItem(STORAGE_MACRO_ARCHIVE);
				getGgbApi().setBase64(b64);
			}
		}
	}

	protected boolean openMacroFromStorage() {
		createStorage();

		if (storage != null) {
			if (storage.getItem(STORAGE_MACRO_KEY) != null) {
				String macroName = storage.getItem(STORAGE_MACRO_KEY);
				try {
					openMacro(macroName);
					Window.setTitle(macroName);
					setToolLoadedFromStorage(true);
					return true;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
		return googleDriveOperation;
	}

	/**
	 * @param fileToHandle
	 *            archive
	 * @return whether file is valid
	 */
	public boolean openFile(JavaScriptObject fileToHandle) {
		resetPerspectiveParam();
		return doOpenFile(fileToHandle, null);
	}

	/**
	 * Remove perspective parameter
	 */
	public void resetPerspectiveParam() {
		if (getArticleElement() != null) {
			getArticleElement().attr("perspective", "");
		}
	}

	/**
	 * Opens the ggb or ggt file
	 *
	 * @param fileToHandle
	 *            file
	 * @param callback
	 *            callback
	 * @return returns true, if fileToHandle is ggb or ggt file, otherwise
	 *         returns false. Note that If the function returns true, it's don't
	 *         mean, that the file opening was successful, and the opening
	 *         finished already.
	 */
	public native boolean doOpenFile(JavaScriptObject fileToHandle,
			JavaScriptObject callback) /*-{
		var ggbRegEx = /\.(ggb|ggt|ggs|csv|off|pdf)$/i;
		var fileName = fileToHandle.name.toLowerCase();
		if (!fileName.match(ggbRegEx)) {
			return false;
		}
		var appl = this;
		if (fileName.match(/\.(pdf)$/i)) {
			appl.@org.geogebra.web.html5.main.AppW::openPDF(Lcom/google/gwt/core/client/JavaScriptObject;)(fileToHandle);
			return true;
		}

		var reader = new FileReader();
		reader.onloadend = function(ev) {
			if (reader.readyState === reader.DONE) {
				var fileStr = reader.result;
				if (fileName.match(/\.(ggb|ggt|ggs)$/i)) {
					appl.@org.geogebra.web.html5.main.AppW::loadGgbFileAsBase64Again(Ljava/lang/String;Z)(fileStr, fileName.match(/\.(ggs)$/i));
				}
				if (fileName.match(/\.(csv)$/i)) {
					appl.@org.geogebra.web.html5.main.AppW::openCSV(Ljava/lang/String;)(atob(fileStr.substring(fileStr.indexOf(",")+1)));
				}
				if (fileName.match(/\.(off)$/i)) {
					appl.@org.geogebra.web.html5.main.AppW::openOFF(Ljava/lang/String;)(atob(fileStr.substring(fileStr.indexOf(",")+1)));
				}
				if (callback != null) {
					callback();
				}
			}
		};
		reader.readAsDataURL(fileToHandle);
		return true;
	}-*/;

	/**
	 * @param str
	 *            string to copy
	 */
	public void copyBase64ToClipboardChromeWebAppCase(String str) {
		// This should do nothing in webSimple!
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

		Network network = new NetworkW();

		networkOperation = new NetworkOperation(network);
		BaseEventPool offlineEventPool = new BaseEventPool(networkOperation,
				false);
		NetworkW.attach("offline", offlineEventPool);
		BaseEventPool onlineEventPool = new BaseEventPool(networkOperation,
				true);
		NetworkW.attach("online", onlineEventPool);
		OfflineView ov = new OfflineView();
		networkOperation.setView(ov);
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
		CommandDispatcherW cmd = new CommandDispatcherW(cmdKernel);
		if (!enableGraphing()) {
			cmd.setEnabled(false);
		}
		return cmd;
	}

	@Override
	public CommandDispatcher3D newCommand3DDispatcher(Kernel cmdKernel) {
		return null;
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
	 * On drag&drop or insert from URL this would be called too, but that would
	 * set security exceptions
	 *
	 * @param url
	 *            - the data url of the image
	 * @param clientx
	 *            - desired position on the canvas (x) - unused
	 * @param clienty
	 *            - desired position on the canvas (y) - unused
	 * @return image
	 */
	public GeoImage urlDropHappened(String url, int clientx, int clienty) {
		return urlDropHappened(url, null, null, null);
	}

	/**
	 * Loads an image and puts it on the canvas (this happens on webcam input)
	 * On drag&drop or insert from URL this would be called too, but that would
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

		MD5EncrypterGWTImpl md5e = new MD5EncrypterGWTImpl();
		String zipDirectory = md5e.encrypt(url);

		// with dummy extension, maybe gif or jpg in real
		String imgFileName = zipDirectory + ".png";

		String fn = imgFileName;
		int index = imgFileName.lastIndexOf('/');
		if (index != -1) {
			fn = fn.substring(index + 1, fn.length()); // filename without
		}
		// path
		fn = org.geogebra.common.util.Util.processFilename(fn);

		// filename will be of form
		// "a04c62e6a065b47476607ac815d022cc\liar.gif"
		imgFileName = zipDirectory + '/' + fn;

		GeoImage ret = createImageFromString(imgFileName, url, null,
				corner1 == null, corner1, corner2, corner4);
		if (insertImageCallback != null) {
			this.insertImageCallback.run();
		}

		return ret;
	}

	/**
	 * Loads an image and puts it on the canvas (this happens by drag & drop)
	 *
	 * @param imgFileName
	 *            - the file name of the image
	 * @param fileStr
	 *            - the image data url
	 */
	public void imageDropHappened(String imgFileName, String fileStr) {
		String fn = ImageManagerW.getMD5FileName(imgFileName, fileStr);

		createImageFromString(fn, fileStr, null, true, null, null, null);
	}

	/**
	 * @param imgFileName
	 *            filename
	 */
	@Override
	public GeoImage createImageFromString(final String imgFileName,
			String imageAsString, GeoImage imageOld,
			final boolean autoCorners, final String c1, final String c2,
			final String c4) {
		final Construction cons = getKernel().getConstruction();
		String fileStr = imageAsString;
		if (fileStr.startsWith(StringUtil.svgMarker)) {
			fileStr = Browser.decodeBase64(
					fileStr.substring(StringUtil.svgMarker.length()));
			fileStr = ImageManager.fixSVG(fileStr);
			fileStr = Browser.encodeSVG(fileStr);
		} else if (fileStr.startsWith("<svg") || fileStr.startsWith("<?xml")) {
			fileStr = Browser.encodeSVG(fileStr);
		}
		getImageManager().addExternalImage(imgFileName, fileStr);
		final GeoImage geoImage = imageOld != null ? imageOld
				: new GeoImage(cons);
		getImageManager().triggerSingleImageLoading(imgFileName, geoImage);

		final App app = this;

		final ImageWrapper img = new ImageWrapper(
				getImageManager().getExternalImage(imgFileName, this, true));
		img.attachNativeLoadHandler(getImageManager(), new ImageLoadCallback() {
			@Override
			public void onLoad() {
				geoImage.setImageFileName(imgFileName,
						img.getElement().getWidth(),
						img.getElement().getHeight());
				if (autoCorners) {
					getGuiManager().setImageCornersFromSelection(geoImage);
				} else {

					if (c1 != null) {

						GeoPointND corner1 = kernel.getAlgebraProcessor()
								.evaluateToPoint(c1, null, true);
						geoImage.setCorner(corner1, 0);

						GeoPoint corner2;
						if (c2 != null) {
							corner2 = (GeoPoint) kernel.getAlgebraProcessor()
									.evaluateToPoint(c2, null, true);
						} else {
							corner2 = new GeoPoint(cons, 0, 0, 1);
							geoImage.calculateCornerPoint(corner2,
									2);
						}
						geoImage.setCorner(corner2, 1);

						// make sure 2nd corner is on screen
						ImageManager.ensure2ndCornerOnScreen(
								corner1.getInhomX(), corner2, app);

						if (c4 != null) {
							GeoPointND corner4 = kernel.getAlgebraProcessor()
									.evaluateToPoint(c4, null, true);
							geoImage.setCorner(corner4, 2);
						}

					}

					geoImage.setLabel(null);
					GeoImage.updateInstances(app);

				}
				if (getImageManager().isPreventAuxImage()) {
					geoImage.setAuxiliaryObject(false);
				}
				setDefaultCursor();
				storeUndoInfo();
			}
		});

		return geoImage;
	}

	/**
	 * Opens the image file
	 *
	 * @param fileToHandle
	 *            javascript handle for the file
	 * @param callback
	 *            load callback
	 * @return returns true, if fileToHandle image file, otherwise return false.
	 *         Note that If the function returns true, it's don't mean, that the
	 *         file opening was successful, and the opening finished already.
	 */
	public native boolean openFileAsImage(JavaScriptObject fileToHandle,
			JavaScriptObject callback) /*-{
		var imageRegEx = /\.(png|jpg|jpeg|gif|bmp|svg)$/i;
		if (!fileToHandle.name.toLowerCase().match(imageRegEx))
			return false;

		var appl = this;
		var reader = new FileReader();
		reader.onloadend = function(ev) {
			if (reader.readyState === reader.DONE) {
				var fileStr = reader.result;
				var fileName = fileToHandle.name;
				appl.@org.geogebra.web.html5.main.AppW::imageDropHappened(Ljava/lang/String;Ljava/lang/String;)(fileName, fileStr);
				if (callback != null) {
					callback();
				}
			}
		};
		reader.readAsDataURL(fileToHandle);
		return true;
	}-*/;

	/**
	 * @return the id of the articleelement
	 */
	public String getArticleId() {
		return articleElement.getId();
	}

	/**
	 * @param articleid
	 *            the article id added by scriptManager
	 *
	 *            this method is called by scriptmanager after ggbOnInit
	 */
	public static native void appletOnLoad(String articleid) /*-{
		if (typeof $wnd.ggbAppletOnLoad === "function") {
			$wnd.ggbAppletOnLoad(articleid);
		}
	}-*/;

	/**
	 * Get a pane for showing messages
	 *
	 * @return option pane
	 */
	public GOptionPaneW getOptionPane() {
		return getGuiManager() != null ? getGuiManager().getOptionPane()
				: new GOptionPaneW(getPanel(), this);
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
		clientInfo.setLanguage(getLocalization().getLanguage());
		clientInfo.setWidth((int) getWidth());
		clientInfo.setHeight((int) getHeight());
		clientInfo.setType(getClientType());
		clientInfo.setId(getClientID());
		clientInfo.setAppName(getConfig().getAppCode());
		return clientInfo;
	}

	/**
	 * Initializes the user authentication
	 *
	 * @param op
	 *            login operation
	 * @param mayLogIn
	 *            whether login dialog may be opened
	 */
	public void initSignInEventFlow(LogInOperation op, boolean mayLogIn) {

		// Initialize the signIn operation
		loginOperation = op;
		if (getNetworkOperation().isOnline()) {
			if (this.getLAF() != null && this.getLAF().supportsGoogleDrive()) {
				initGoogleDriveEventFlow();
			}
			if (mayLogIn) {
				loginOperation.performTokenLogin();
			}
		} else {
			loginOperation.startOffline();
		}
	}

	/**
	 * Load Google Drive APIs
	 */
	protected void initGoogleDriveEventFlow() {
		// overriden in AppW
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

		initSettings();

		fontManager = new FontManagerW();
		setFontSize(16, false);
		initEuclidianViews();

		initImageManager();

		setFontSize(16, true);

		getScriptManager(); // gbOnInit() is only called after file loads
							// completely

		FileDropHandlerW.registerDropHandler(getFrameElement(), this);
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

		if (!getArticleElement().getDataParamEnable3D(true)) {
			return false;
		}

		return super.is3DViewEnabled();
	}

	private void setViewsEnabled() {
		if (!getConfig().isCASEnabled()) {
			getSettings().getCasSettings().setEnabled(false);
		} else if (getArticleElement().getDataParamEnableCAS(false)
				|| !getArticleElement().getDataParamEnableCAS(true)) {
			getSettings().getCasSettings().setEnabled(
					getArticleElement().getDataParamEnableCAS(false));
		}
		if (getSettings().getCasSettings().isEnabled()
				&& has(Feature.SYMBOLIC_AV)) {
			getKernel().setSymbolicMode(getConfig().getSymbolicMode());
		}

		if (is3DDisabledForApp()) {
			if (getSettings().getEuclidian(-1) != null) {
				getSettings().getEuclidian(-1).setEnabled(false);
			}
		} else if (getArticleElement().getDataParamEnable3D(false)
				|| !getArticleElement().getDataParamEnable3D(true)) {
			if (getSettings().supports3D()) {
				getSettings().getEuclidian(-1).setEnabled(
						getArticleElement().getDataParamEnable3D(false));
			}
		}

		if (getArticleElement().getDataParamEnableGraphing(false)
				|| !getArticleElement().getDataParamEnableGraphing(true)) {

			boolean enableGraphing = getArticleElement()
					.getDataParamEnableGraphing(false);
			getSettings().getEuclidian(1).setEnabled(enableGraphing);
			getSettings().getEuclidian(2).setEnabled(enableGraphing);
		}
	}

	/**
	 * @return preferred size
	 */
	public GDimension getPreferredSize() {
		if (preferredSize == null) {
			return new GDimensionW(800, 600);
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

	private void updateContentPane(boolean updateComponentTreeUI) {
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
		if (updateComponentTreeUI) {
			updateTreeUI();
		}

		// reset mode and focus
		set1rstMode();
	}

	/**
	 * Updates the GUI of the main component.
	 */
	public void updateContentPane() {
		updateContentPane(true);
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
			tableModel = new SpreadsheetTableModelSimple(this, SPREADSHEET_INI_ROWS,
					SPREADSHEET_INI_COLS);
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

	// methods used just from AppWapplet (and AppWsimple)
	/**
	 *
	 * @param w
	 *            last selected view
	 * @param el
	 *            target element
	 */
	public void focusLost(View w, Element el) {
		// other things are handled in subclasses of AppW
		// anyAppHasFocus = false;
		if (el != null) {
			setLastActive(el);
		}
	}

	/**
	 *
	 * @param w
	 *            selected view
	 * @param el
	 *            target element
	 */
	public void focusGained(View w, Element el) {
		// this is used through the super keyword
		// anyAppHasFocus = true;
		if (el != null) {
			setLastActive(el);
		}
	}

	/**
	 * Update toolbar from custom definition
	 */
	public void setCustomToolBar() {
		// only needed in AppWFull
	}

	/**
	 * @return whether EV1 is the only visible view
	 */

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

	// ========================================================
	// Getters/Setters
	// ========================================================

	@Override
	public boolean isHTML5Applet() {
		return true;
	}

	/**
	 * @return article element with parameters
	 */
	public ArticleElementInterface getArticleElement() {
		return articleElement;
	}

	@Override
	public boolean isApplet() {
		return !getArticleElement().getDataParamApp();
	}

	/**
	 * @return active material
	 */
	public Material getActiveMaterial() {
		return this.activeMaterial;
	}

	/**
	 * @param mat
	 *            active material
	 */
	public void setActiveMaterial(Material mat) {
		this.activeMaterial = mat;
	}

	private void resetActiveMaterial() {
		this.activeMaterial = null;
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
			return (view == App.VIEW_EUCLIDIAN);
		}
		return getGuiManager().showView(view);
	}

	// ========================================================
	// Languages
	// ========================================================

	/**
	 * Checks for GeoGebraLangUI in URL, then in cookie, then checks browser
	 * language
	 *
	 * @return user preferred language
	 */
	public String getLanguageFromCookie() {
		return UserPreferredLanguage.get(this);
	}

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
		setAltText();
		translateHeader();
	}

	private void setTitle() {
		String titleTransKey = getVendorSettings().getAppTitle(getConfig());
		String title = getLocalization().getMenu(titleTransKey);
		if (getArticleElement().getLoginAPIurl() != null) {
			Browser.changeMetaTitle(title);
		}
		getArticleElement().getElement().setAttribute("aria-label", title);
	}

	protected void translateHeader() {
		Element header = Dom.querySelector("GeoGebraHeader");
		if (header != null) {
			UserPreferredLanguage.translate(this, header);
		}
	}

	@Override
	public boolean letRedefine() {
		// AbstractApplication.debug("implementation needed"); // TODO
		// Auto-generated
		return true;
	}

	// ============================================
	// IMAGES
	// ============================================

	/**
	 * @return refresh applet image
	 */
	public ImageElement getRefreshViewImage() {
		ImageElement imgE = ImageManagerW
				.getInternalImage(GuiResourcesSimple.INSTANCE.viewRefresh());
		attachNativeLoadHandler(imgE);
		return imgE;
	}

	/**
	 * @return play image
	 */
	public ImageElement getPlayImage() {
		return ImageManagerW.getInternalImage(GuiResourcesSimple.INSTANCE.play_black());
	}

	/**
	 * @return pause image
	 */
	public ImageElement getPauseImage() {
		return ImageManagerW.getInternalImage(
				GuiResourcesSimple.INSTANCE.pause_black());
	}

	/**
	 * @return play image with hover effect
	 */
	public ImageElement getPlayImageHover() {
		return ImageManagerW.getInternalImage(
				GuiResourcesSimple.INSTANCE.play_purple());
	}

	/**
	 * @return pause image with hover effect
	 */
	public ImageElement getPauseImageHover() {
		return ImageManagerW.getInternalImage(
				GuiResourcesSimple.INSTANCE.pause_purple());
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
	 * Export given view to clipboard as png
	 *
	 * TODO actually downloads image
	 *
	 * @param ev
	 *            view
	 */
	public final void copyEVtoClipboard(EuclidianViewW ev) {
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
		Log.debug("opening: " + pageUrl);

		// assume showURLinBrowserWaiterFixedDelay is called before
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
		Scheduler.get().scheduleDeferred(sucCallback);
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
	public void registerPopup(Widget widget) {
		popups.add(widget);
	}

	public void centerAndResizeViews() {
		// to be overridden in AppWFull
	}

	@Override
	public void closePopups() {
		closePopupsNoTooltips();
		ToolTipManagerW.hideAllToolTips();

		if (!isUnbundled() && getGuiManager() != null
				&& getGuiManager().hasAlgebraView()) {
			getAlgebraView().resetItems(false);
		}

		if (getActiveEuclidianView() != null) {
			getActiveEuclidianView().getEuclidianController()
					.setObjectMenuActive(false);
		}
		//hideSymbolicEditors();
	}

	/**
	 * Close popups, keep tooltips
	 */
	public void closePopupsNoTooltips() {
		for (Widget widget : popups) {
			widget.setVisible(false);
		}
		popups.clear();
	}

	/**
	 * @param el
	 *            element that can be cliked without closingpopups
	 */
	public void addAsAutoHidePartnerForPopups(Element el) {
		for (Widget popup : popups) {
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
		return popups.size() > 0;
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
		return getArticleElement().getDataClientID();
	}

	/**
	 * @return whether toolbar should be shown
	 */
	public boolean isShowToolbar() {
		if (this.articleElement == null) {
			return false;
		}
		return this.articleElement.getDataParamShowToolBar(false)
				|| this.articleElement.getDataParamApp();
	}

	/**
	 * @param fallback
	 *            fallback when computation gives 0
	 * @return width of central pane
	 */
	public int getWidthForSplitPanel(int fallback) {
		int ret = getAppletWidth(); // border already excluded

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
		int windowHeight = getAppletHeight();
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
		getScriptManager().disableListeners();
		kernel.initUndoInfo();
		getScriptManager().enableListeners();
	}

	@Override
	public boolean supportsView(int viewID) {
		if (viewID == App.VIEW_CAS && !getLAF().isSmart()) {
			if (!Browser.supportsJsCas()) {
				return false;
			}
		}

		if (viewID == App.VIEW_CAS) {
			return (getSettings().getCasSettings().isEnabled())
					&& getArticleElement().getDataParamEnableCAS(true)
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

		if (isUnbundled()) {
			return;
		}
		set1rstMode();
	}

	@Override
	public void set1rstMode() {
		setMoveMode();
	}

	@Override
	public void uploadToGeoGebraTube() {
		// no upload without UI
	}

	@Override
	public void updateApplicationLayout() {
		Log.debug("updateApplicationLayout: Implementation needed...");
	}

	@Override
	public void setShowToolBar(boolean toolbar, boolean help) {
		if (toolbar) {
			// JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE
			// .propertiesKeysJS());
		}
		super.setShowToolBar(toolbar, help);
	}

	/**
	 * @return applet ID
	 */
	public final String getAppletId() {
		return articleElement.getDataParamId();
	}

	/**
	 * @return frame widget
	 */
	public abstract GeoGebraFrameW getAppletFrame();

	/**
	 * @return whether the focus was lost
	 */
	private static native Element nativeLoseFocus(Element element) /*-{
		var active = $doc.activeElement;
		var containsMask = $wnd.Node.DOCUMENT_POSITION_CONTAINS;
		if (active
				&& ((active === element) || (active
						.compareDocumentPosition(element) & containsMask))) {
			active.blur();
			return active;
		}
		return null;
	}-*/;

	@Override
	public void loseFocus() {
		// probably this is called on ESC, so the reverse
		// should happen on ENTER
		Element ret = nativeLoseFocus(articleElement.getElement());
		if (ret != null) {
			setLastActive(ret);
			setAnyAppFocused(false);
			getGlobalKeyDispatcher().setFocused(false);
		}
	}

	private static void setAnyAppFocused(boolean b) {
		anyAppHasFocus = b;
	}

	/**
	 * @return whether we can focus on ENTER
	 */
	private static native boolean nativeGiveFocusBack() /*-{
		var active = $doc.activeElement;
		if (active && (active !== $doc.body)) {

			//if SVG clicked, getClassName returns non-string

			if (typeof active.className == "string"
					&& active.className.match(/geogebraweb-dummy-invisible/)) {
				// actually, ESC focuses this, does not blur!
				return true;
			}
			// this shall execute the default ENTER action on
			// that element, to be independent (e.g. click on links!)
			// OR if it is part of GeoGebra, then we shall also not
			// support selecting GeoGebra again by ENTER
			//return false; // behold the other return false;

			// not doing more checks, don't do any action, which is safe
			return false;
		}
		// blurred, probably, so it's safe to focus on ENTER
		return true;
	}-*/;

	/**
	 * Let's say this is the pair of loseFocus, so that only loseFocus can lose
	 * focus from ALL applets officially (i.e. "ESC"), and from each part of
	 * each applet (e.g. input bar, Graphics view, etc), while only
	 * giveFocusBack can give focus back to an applet removed by the loseFocus
	 * method - to avoid hidden bugs.
	 *
	 * What if focus is received by some other method than ENTER (pair of ESC)?
	 * I think let's allow it, but if ENTER comes next, then we should adjust
	 * our knowledge about it (otherwise, it should have been watched in the
	 * entire codebase, which is probably worse, for there are possibilities of
	 * errors). This way just these two methods shall be checked.
	 */
	public static void giveFocusBack() {
		if (anyAppHasFocus) {
			// here we are sure that ENTER should not do anything
			return;
		}

		// update for the variable in this case, must be made anyway
		// just it is a question whether this shall also mean a focus?
		// BUT only when nativeGiveFocusBack is changed, and this
		// variable also filled perfectly
		// anyAppHasFocus = true;

		// here we could insert static aggregates of relevant
		// variables like getGlobalKeyDispatcher().InFocus
		// ... but what if e.g. the input bar has focus?

		// then we can easily check for $doc.activeElement,
		// whether it means blur=OK
		if (nativeGiveFocusBack()) {
			if (lastActiveElement != null) {
				anyAppHasFocus = true;
				lastActiveElement.focus();
			}
		}
	}

	@Override
	public boolean isScreenshotGenerator() {
		return this.articleElement.getDataParamScreenshotGenerator();
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

	/**
	 * Show message in a popup
	 *
	 * @param message
	 *            message
	 * @param title
	 *            popup title
	 */
	public void showMessage(final String message, final String title) {
		getOptionPane().showConfirmDialog(message, title,
				GOptionPane.DEFAULT_OPTION, GOptionPane.INFORMATION_MESSAGE,
				null);
	}

	/**
	 * @param content
	 *            content
	 * @param title
	 *            popup title
	 * @param buttonText
	 *            button text
	 * @param handler
	 *            button click handler
	 *
	 */
	public void showMessage(final HTML content, final String title,
			String buttonText, AsyncOperation<String[]> handler) {
		content.addStyleName("examContent");
		ScrollPanel scrollPanel = new ScrollPanel(content);
		scrollPanel.addStyleName("examScrollPanel");
		getOptionPane().showConfirmDialog(scrollPanel, title,
				GOptionPane.DEFAULT_OPTION, GOptionPane.INFORMATION_MESSAGE,
				buttonText, null, handler);
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
	public ErrorHandler getDefaultErrorHandler() {
		return new ErrorHandlerW(this);
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

		String ggbApplet = getAppletId();

		// TODO: maybe use sandbox?
		String script = script0;

		script = "document.ggbApplet= document." + ggbApplet
				+ "; ggbApplet = document." + ggbApplet + ";" + script;

		// script = "ggbApplet = document.ggbApplet;"+script;

		// add eg arg="A"; to start
		if (arg != null) {
			script = "arg=\"" + arg + "\";" + script;
		}
		JsEval.evalScriptNative(script, ggbApplet);
	}

	public void attachNativeLoadHandler(ImageElement img) {
		addNativeLoadHandler(img, getActiveEuclidianView());
	}

	private native void addNativeLoadHandler(ImageElement img,
			EuclidianView view) /*-{
		img
				.addEventListener(
						"load",
						function() {
							view.@org.geogebra.web.html5.euclidian.EuclidianViewW::updateBackground()();
						});
	}-*/;

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

	/**
	 * @return whether small keyboard is needed
	 */
	public boolean needsSmallKeyboard() {
		return (getHeight() > 0 && getHeight() < LOWER_HEIGHT)
				|| (getHeight() == 0 && getArticleElement()
						.getDataParamHeight() < LOWER_HEIGHT);
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
	 * hide the on-screen keyboard (if it is visible)
	 */
	public void hideKeyboard() {
		// Overwritten in subclass - nothing to do here
	}

	/**
	 * @return whether app is offline
	 */
	public boolean isOffline() {
		return !getNetworkOperation().isOnline();
	}

	public boolean isToolLoadedFromStorage() {
		return toolLoadedFromStorage;
	}

	public void setToolLoadedFromStorage(boolean toolLoadedFromStorage) {
		this.toolLoadedFromStorage = toolLoadedFromStorage;
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

	@Override
	public SensorLogger getSensorLogger() {
		if (webSocketLogger == null) {
			webSocketLogger = new WebsocketLogger(getKernel());
		}
		return webSocketLogger;
	}

	/**
	 * @param b
	 *            whether keyboard is needed
	 */
	public void setKeyboardNeeded(boolean b) {
		this.keyboardNeeded = b;
	}

	/**
	 * @return whether keyboard is needed for current perspective
	 */
	public boolean isKeyboardNeeded() {
		return keyboardNeeded;
	}

	/**
	 * @param perspID
	 *            perspective id
	 */
	public void showStartTooltip(int perspID) {
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
	public boolean enableFileFeatures() {
		return this.articleElement.getDataParamEnableFileFeatures();
	}

	/**
	 * Update prerelease / canary flags
	 *
	 * @param prereleaseStr
	 *            prerelease parameter
	 */
	public void setPrerelease(String prereleaseStr) {
		this.canary = false;
		this.prerelease = false;

		if ("canary".equals(prereleaseStr)) {
			canary = true;
			prerelease = true;
		} else if ("true".equals(prereleaseStr)) {
			this.prerelease = true;
		}
	}

	/**
	 * @param el
	 *            article element
	 * @return true if prerelease
	 *
	 *         Remove this function if GGB-2051 released. Used only in GGB-2051
	 */
	public static boolean isPrerelease(ArticleElement el) {
		String p = el.getDataParamPrerelease();
		return "true".equals(p) || "canary".equals(p);
	}

	@Override
	public void hideMenu() {
		// for applets with menubar
	}

	/**
	 * @param path
	 *            path for external saving
	 */
	public void setExternalPath(String path) {
		if (getKernel() != null && getKernel().getConstruction() != null
				&& getKernel().getConstruction().getTitle() == null
				|| "".equals(getKernel().getConstruction().getTitle())) {
			int lastSlash = Math.max(path.lastIndexOf('/'),
					path.lastIndexOf('\\'));
			String title = path.substring(lastSlash + 1).replace(".ggb", "");
			getKernel().getConstruction().setTitle(title);
		}
		getFileManager().setFileProvider(Provider.LOCAL);
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
	public void openPDF(JavaScriptObject pdfFile) {
		// only makes sense in GUI
	}

	/**
	 *
	 * @return Pixel ratio including external transforms
	 */
	public double getPixelRatio() {
		return Browser.getPixelRatio() * articleElement.readScaleX();
	}

	public void addWindowResizeListener(MouseTouchGestureControllerW mtg) {
		this.euclidianHandlers.add(mtg);
	}

	public boolean showToolBarHelp() {
		return getArticleElement().getDataParamShowToolBarHelp(true);
	}

	/**
	 * @return root panel of the applet
	 */
	public abstract Panel getPanel();

	private Timer altTextTimer = new Timer() {

		@Override
		public void run() {
			getEuclidianView1().setAltText();
			if (hasEuclidianView2(1)) {
				getEuclidianView2(1).setAltText();
			}
			if (isEuclidianView3Dinited()) {
				((EuclidianViewWInterface) getEuclidianView3D()).setAltText();
			}
		}
	};

	@Override
	public void setAltText() {
		altTextTimer.schedule(700);
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
		return (!isApplet()
				|| getArticleElement().getDataParamShowMenuBar(false)
				|| getArticleElement().getDataParamAllowStyleBar(false))
				&& enableGraphing();
	}

	@Override
	public boolean showResetIcon() {
		return super.showResetIcon() && !allowStylebar();
	}

	/**
	 * @return whether graphics view and commands are allowed
	 */
	public boolean enableGraphing() {
		return getArticleElement().getDataParamEnableGraphing(true);
	}

	/**
	 * @return whether a file was used for initialization
	 */
	public boolean isStartedWithFile() {
		return getArticleElement().getDataParamFileName().length() > 0
				|| getArticleElement().getDataParamBase64String().length() > 0
				|| getArticleElement().getDataParamTubeID().length() > 0
				|| this.getArticleElement().getDataParamJSON().length() > 0
				|| (getArticleElement().getDataParamApp()
						&& Location.getParameter("state") != null);
	}

	@Override
	public GTimer newTimer(GTimerListener listener, int delay) {
		return new GTimerW(listener, delay);
	}

	@Override
	public void readLater(GeoNumeric geo) {
		if (!kernel.getConstruction().isFileLoading()
				&& (!articleElement.preventFocus()
						|| getGlobalKeyDispatcher().isFocused())) {
			if (readerTimer == null) {
				readerTimer = new ReaderTimer();
			}
			readerTimer.setGeo(geo);
			readerTimer.schedule(700);
		}
	}

	/**
	 * @param id
	 *            material id
	 * @param sharingKey
	 *            material sharing key
	 * @param title
	 *            material title
	 */
	public void updateMaterialURL(int id, String sharingKey, String title) {
		setTubeId(id > 0 ? Integer.toString(id) : sharingKey);
		if (articleElement.getDataParamApp() && sharingKey != null) {

			Browser.changeUrl(getCurrentURL(sharingKey, false));
			if (!StringUtil.empty(title)) {
				Browser.changeMetaTitle(title);
			}
		}
	}

	/**
	 * @param sharingKey
	 *            material sharing key
	 * @param absolute
	 *            whether to include host name
	 * @return url
	 */
	public String getCurrentURL(String sharingKey, boolean absolute) {
		String shareLinkPrefix = articleElement.getParamShareLinkPrefix();
		String apiURL = this.getLoginOperation().getGeoGebraTubeAPI().getUrl();
		String host = apiURL.substring(0, apiURL.indexOf("/", 12));
		if (StringUtil.empty(shareLinkPrefix)) {
			shareLinkPrefix = "classic";
		}
		String path = "/" + shareLinkPrefix + "/" + sharingKey;
		return absolute ? host + path : path;
	}

	/**
	 * Update rounding from article parameter
	 */
	public void updateRounding() {
		setRounding(getArticleElement().getDataParamRounding());
	}

	/**
	 * Show perspective picker
	 */
	public void showPerspectivesPopup() {
		// overridden in AppWFull
	}

	/**
	 * Hide perspective picker
	 */
	public void closePerspectivesPopup() {
		// only for GUI
	}

	/**
	 * @param index
	 *            perspective ID
	 */
	public void setActivePerspective(int index) {
		// only for GUI
	}

	/**
	 * Focus this app
	 */
	public void addFocusToApp() {
		if (!GlobalKeyDispatcherW.getIsHandlingTab()) {
			getGlobalKeyDispatcher().setFocused(true);
			return;
		}

		// add focus to AV if visible
		AlgebraView av = getAlgebraView();
		boolean visible = av != null && av.isShowing();
		if (visible) {
			((Widget) av).getElement().focus();
			focusGained(av, ((Widget) av).getElement());
			return;
		}

		// focus -> EV
		EuclidianViewW ev = getEuclidianView1();
		visible = ev != null && ev.isShowing();
		if (visible) {
			ev.getCanvasElement().focus();
			ev.focusGained();
		}
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
		// GeoGebraPreferencesW.getPref().loadForApp(app, p);
	}

	@Override
	public void ensureEvSizeSet(EuclidianSettings evSet) {
		GDimension gd = evSet.getPreferredSize();
		if (gd.getWidth() == 0 || gd.getHeight() == 0) {
			// border already excluded
			int width = getAppletWidth();
			int height = getAppletHeight();
			if (width == 0 || height == 0) {
				// setting a standard size, like in
				// compabilityLayout
				// fixing a real bug of height 0
				width = 598; // 2: border
				height = 438; // 2: border
			}
			evSet.setPreferredSize(
					AwtFactory.getPrototype().newDimension(width, height));
		}
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
				callback.callback(new GeoGebraToPstricksW(AppW.this));

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
				callback.callback(new GeoGebraToAsymptoteW(AppW.this));

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
				callback.callback(new GeoGebraToPgfW(AppW.this));

			}
		});
	}

	public static native Element getHiddenTextArea() /*-{
		var hiddenTextArea = $doc.getElementById('hiddenCopyPasteTextArea');
		if (!hiddenTextArea) {
			hiddenTextArea = $doc.createElement("textarea");
			hiddenTextArea.id = 'hiddenCopyPasteTextArea';
			hiddenTextArea.style.position = 'absolute';
			hiddenTextArea.style.width = '10px';
			hiddenTextArea.style.height = '10px';
			hiddenTextArea.style.zIndex = '100';
			hiddenTextArea.style.left = '-1000px';
			hiddenTextArea.style.top = '0px';
			$doc.getElementsByTagName('body')[0].appendChild(hiddenTextArea);
		}
		//hiddenTextArea.value = '';
		return hiddenTextArea;
	}-*/;

	/**
	 * https://jsfiddle.net/alvaroAV/a2pt16yq/ works in IE11, Chrome
	 *
	 * this method doesn't always work in Edge, Firefox as needs to be run from eg
	 * button
	 * 
	 * IE11: asks user for permission
	 *
	 * @param value text to copy
	 */
	public static native void copyToSystemClipboardNative(String value) /*-{

		// async Clipboard API
		// doesn't work in Firefox either so might as well just use execCommand('copy')
		//if ($wnd.navigator.clipboard && $wnd.navigator.clipboard.writeText) {
		//	// https://github.com/gwtproject/gwt/issues/9490
		//	// .catch changed to ["catch"]
		//	$wnd.navigator.clipboard.writeText(value).then(function() {
		//		$wnd.console.log("Clipboard copy OK")
		//	})["catch"](function(e) {
		//		$wnd.console.log("Problem copying to clipboard")
		//	});
		//}

		// currently seems to work in Chrome, IE11, Edge+Chromium
		// doesn't seem to work in Edge, Firefox from GGB button
		// document.execCommand('cut'/'copy') was denied because it was not called 
		// from inside a short running user-generated event handler.
		var copyFrom = @org.geogebra.web.html5.main.AppW::getHiddenTextArea()();
		copyFrom.value = value;
		copyFrom.select();
		$doc.execCommand('copy');

	}-*/;

	@Override
	public void copyTextToSystemClipboard(String text) {
		Log.debug("copying to clipboard " + text);
		copyToSystemClipboardNative(text);
	}

	/**
	 * @param text
	 *            text to copy
	 * @param notify
	 *            callback after copy is done
	 */
	public void copyTextToSystemClipboard(String text, Runnable notify) {
		Log.debug("copying to clipboard " + text);
		copyToSystemClipboardNative(text);

		if (notify != null) {
			notify.run();
		}
	}

	private static void setLastActive(Element e) {
		lastActiveElement = e;
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
	 * Handle click ouside of any view
	 */
	public void onUnhandledClick() {
		// only with GUI
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
		return AppConfigDefault
				.isUnbundledOrNotes(articleElement.getDataParamAppName())
				&& !"notes".equals(articleElement.getDataParamAppName());

	}

	@Override
	public AppConfig getConfig() {
		return new AppConfigDefault();
	}

	@Override
	public boolean isUnbundledGraphing() {
		return "graphing".equals(articleElement.getDataParamAppName());
	}

	@Override
	public boolean isUnbundledGeometry() {
		return "geometry".equals(articleElement.getDataParamAppName());
	}

	/**
	 * @return whether we are running 3D grapher
	 */
	public boolean isUnbundled3D() {
		return "3d".equals(articleElement.getDataParamAppName());
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
		return getWidth() < getHeight();
	}

	/**
	 * Open share dialog (native or HTML popup)
	 */
	public void share() {
		// only with GUI
	}

	@Override
	public void exportStringToFile(String extension, String content) {

		String url;

		if ("html".equals(extension)) {
			url = StringUtil.htmlMarker + content;
		} else {
			url = Browser.addTxtMarker(content);
			// do we ever need this?
			// url = StringUtil.txtMarker + URL.encodePathSegment(content);
		}

		dispatchEvent(new org.geogebra.common.plugin.Event(
				EventType.OPEN_DIALOG, null, "export3D"));
		getFileManager().showExportAsPictureDialog(url, getExportTitle(),
				extension, "Export", this);
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

	/**
	 * When multiple slides are present give ID of the current one, otherwise
	 * give default slide ID
	 *
	 * @return the string ID of current slide
	 */
	public String getSlideID() {
		return getPageController() == null
				? GgbFile.SLIDE_PREFIX + GgbFile.getCounter()
				: getPageController().getSlideID();
	}

	@Override
	public void copyGraphicsViewToClipboard() {
		if (!isCopyImageToClipboardAvailable()) {
			Log.debug("window.copyGraphicsToClipboardExternal() not available");
			return;
		}

		EuclidianViewW ev = (EuclidianViewW) getActiveEuclidianView();
		nativeCopyToClipboardExternal(ev.getExportImageDataUrl(3, false, false));
	}

	private native String nativeCopyToClipboardExternal(String s) /*-{
		return $wnd.copyGraphicsToClipboardExternal(s);
	}-*/;

	/**
	 * @return whether native clipboard API is available
	 */
	public native boolean isCopyImageToClipboardAvailable() /*-{
		return !!$wnd.copyGraphicsToClipboardExternal;
	}-*/;

	@Override
	public void copyImageToClipboard(String dataURI) {
		if (!isCopyImageToClipboardAvailable()) {
			Log.debug("window.copyGraphicsToClipboardExternal() not available");
			return;
		}
		nativeCopyToClipboardExternal(dataURI);
	}

	/**
	 * @return zip file handler
	 */
	public ViewW getViewW() {
		if (viewW == null) {
			viewW = new ViewW(this);
		}
		return viewW;
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
		invokeLater(new Runnable() {

			@Override
			public void run() {
				EuclidianViewW.forceResize(getEuclidianView1());
			}
		});

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
		return "mebis".equalsIgnoreCase(articleElement.getParamVendor());
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
			accessibilityManager = new AccessibilityManagerW(this,
					createPerspectiveAccessibilityAdapter());
		}
		return accessibilityManager;
	}

	/**
	 * @return adapter for tabbing through views
	 */
	protected PerspectiveAccessibilityAdapter createPerspectiveAccessibilityAdapter() {
		return new SinglePanelAccessibilityAdapter(this);
	}

	public ZoomPanel getZoomPanel() {
		return zoomPanel;
	}

	@Override
	public SettingsBuilder newSettingsBuilder() {
		return new SettingsBuilderW(this);
	}

	/**
	 *
	 * @return then embedded calculator apis.
	 */
	public JavaScriptObject getEmbeddedCalculators() {
		// iplemented in AppWFull
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
	public EuclidianController getEuclidianController() {
		return super.getEuclidianController();
	}
}
