package org.geogebra.common.jre.headless;

import java.util.HashMap;
import java.util.Locale;

import org.geogebra.common.GeoGebraConstants.Platform;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.DrawEquation;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.factories.CASFactory;
import org.geogebra.common.factories.CASFactoryDummy;
import org.geogebra.common.factories.Factory;
import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.gui.Layout;
import org.geogebra.common.gui.layout.DockManager;
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.io.MyXMLio;
import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.jre.factory.FormatFactoryJre;
import org.geogebra.common.jre.io.MyXMLioCommon;
import org.geogebra.common.jre.kernel.commands.CommandDispatcherJre;
import org.geogebra.common.jre.main.LocalizationJre;
import org.geogebra.common.jre.util.UtilFactoryJre;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.geos.GeoAudio;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.main.FontManager;
import org.geogebra.common.main.GlobalKeyDispatcher;
import org.geogebra.common.main.GuiManagerInterface;
import org.geogebra.common.main.SpreadsheetTableModel;
import org.geogebra.common.main.SpreadsheetTableModelSimple;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.DefaultSettings;
import org.geogebra.common.main.settings.config.AppConfigCas;
import org.geogebra.common.main.settings.config.AppConfigDefault;
import org.geogebra.common.main.settings.config.AppConfigGeometry;
import org.geogebra.common.main.settings.config.AppConfigGraphing;
import org.geogebra.common.main.settings.config.AppConfigGraphing3D;
import org.geogebra.common.main.settings.config.AppConfigNotes;
import org.geogebra.common.main.settings.config.AppConfigScientific;
import org.geogebra.common.main.settings.config.AppConfigUnrestrictedGraphing;
import org.geogebra.common.main.undo.DefaultUndoManager;
import org.geogebra.common.main.undo.UndoManager;
import org.geogebra.common.plugin.GgbAPI;
import org.geogebra.common.plugin.ScriptManager;
import org.geogebra.common.sound.SoundManager;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimerListener;
import org.geogebra.common.util.ImageManager;
import org.geogebra.common.util.NormalizerMinimal;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

/**
 * Common App class used for testing.
 */
public class AppCommon extends App {

	private final LocalizationJre localization;
	private final Layout layout;
	private DialogManagerNoGui dialogManager;
	private DefaultSettings defaultSettings;
	private SpreadsheetTableModel tableModel;
	private CASFactory casFactory = new CASFactoryDummy();
	private boolean appletFlag = false;
	private ImageManager imageManager;
	private final HashMap<String, MyImageCommon> externalImages = new HashMap<>();

	public AppCommon(LocalizationJre loc, AwtFactory awtFactory) {
		this(loc, awtFactory, new AppConfigDefault());
	}

	/**
	 * Construct an AppCommon.
	 * 
	 * @param loc
	 *            localization
	 * @param awtFactory
	 *            AWT factory
	 * @param appConfig config
	 */
	public AppCommon(LocalizationJre loc, AwtFactory awtFactory, AppConfig appConfig) {
		super(Platform.ANDROID);
		this.appConfig = appConfig;
		AwtFactory.setPrototypeIfNull(awtFactory);
		initFactories();
		initKernel();
		localization = loc;
		initLocalization();
		getLocalization().initTranslateCommand();
		initSettings();
		initEuclidianViews();
		layout = new LayoutHeadless();
		layout.initializeDefaultPerspectives(this, 0.2);
		Log.setLogger(new Log() {

			@Override
			public void print(Level level, Object logEntry) {
				if (logEntry instanceof Throwable) {
					((Throwable) logEntry).printStackTrace(); // NOPMD
				} else {
					System.out.println(logEntry); // NOPMD
				}
			}
		});
	}

	@Override
	public DefaultSettings getDefaultSettings() {
		if (defaultSettings == null) {
			defaultSettings = new DefaultSettingsCommon();
		}
		return defaultSettings;
	}

	@Override
	protected void initLocalization() {
		localization.setApp(this);
		super.initLocalization();
	}

	private static void initFactories() {
		FormatFactory.setPrototypeIfNull(new FormatFactoryJre());
		StringUtil.setPrototypeIfNull(new StringUtil());
		UtilFactoryJre.setupRegexFactory();
	}

	@Override
	protected void showErrorDialog(String msg) {
		// not needed with no UI
	}

	@Override
	protected void initGuiManager() {
		// not needed with no UI
	}

	@Override
	protected EuclidianView newEuclidianView(boolean[] showAxes1, boolean showGrid1) {
		getSettings().getEuclidian(1).setPreferredSize(
				AwtFactory.getPrototype().newDimension(800, 600));
		return new EuclidianViewNoGui(getEuclidianController(), 1,
				getSettings().getEuclidian(1),
				createGraphics());
	}

	protected GGraphics2D createGraphics() {
		return AwtFactory.getPrototype().createBufferedImage(800, 600, false)
				.createGraphics();
	}

	@Override
	public FontManager getFontManager() {
		return new FontManagerNoGui();
    }

    @Override
    protected int getWindowWidth() {
		return 800;
    }

    @Override
    protected int getWindowHeight() {
		return 600;
    }

    @Override
    protected void getLayoutXML(StringBuilder sb, boolean asPreference) {
		// TODO
    }

    @Override
    public CommandDispatcher newCommandDispatcher(Kernel cmdKernel) {
		return new CommandDispatcherJre(cmdKernel);
    }

    @Override
    public void invokeLater(Runnable runnable) {
		runnable.run();
    }

    @Override
    public boolean isApplet() {
        return appletFlag;
    }

    @Override
    public void storeUndoInfo() {
		if (isUndoActive()) {
			kernel.storeUndoInfo();
			setUnsaved();
		}
    }

    @Override
    public void closePopups() {
		// not needed with no UI
    }

    @Override
    public GTimer newTimer(GTimerListener listener, int delay) {
		return new GTimer() {

			@Override
			public void start() {
				// stub

			}

			@Override
			public void startRepeat() {
				// stub

			}

			@Override
			public void stop() {
				// stub

			}

			@Override
			public boolean isRunning() {
				// stub
				return false;
			}

			@Override
			public void setDelay(int timerDelay) {
				// stub

			}
		};
    }

    @Override
    public boolean isUsingFullGui() {
        return false;
    }

    @Override
    public boolean showView(int view) {
		Perspective p = getTmpPerspective();
		if (p != null) {
			for (DockPanelData dp : p.getDockPanelData()) {
				if (dp.getViewId() == view) {
					return dp.isVisible();
				}
			}
		}
		return view == App.VIEW_EUCLIDIAN;
    }

    @Override
    public void showError(String localizedError) {
		// not needed with no UI
    }

    @Override
    public void showError(String string, String str) {
		// not needed with no UI
    }

    @Override
    public AlgebraView getAlgebraView() {
        return null;
    }

    @Override
    public EuclidianView getActiveEuclidianView() {
		return getEuclidianView1();
    }

    @Override
    public boolean hasEuclidianView2EitherShowingOrNot(int idx) {
        return false;
    }

    @Override
    public boolean isShowingEuclidianView2(int idx) {
        return false;
    }

    @Override
    public ImageManager getImageManager() {
        return imageManager;
    }

    @Override
    public GuiManagerInterface getGuiManager() {
        return null;
    }

    @Override
	public DialogManager getDialogManager() {
		return dialogManager;
	}

    @Override
    public void evalJavaScript(App app, String script, String arg) {
		// TODO delegate to scriptManager
    }

    @Override
    public double getWidth() {
        return 0;
    }

    @Override
    public double getHeight() {
        return 0;
    }

    @Override
    public GFont getPlainFontCommon() {
		return AwtFactory.getPrototype().newFont("serif", 0, 12);
    }

    @Override
    public GeoElementGraphicsAdapter newGeoElementGraphicsAdapter() {
		return new GeoElementGraphicsAdapter() {

			@Override
			public MyImage getFillImage() {
				return image;
			}

			@Override
			public void setImageFileName(String fileName) {
				this.imageFileName = fileName;
				if (fileName != null) {
					setImageOnly(getExternalImageAdapter(fileName, 0, 0));
				}
			}

			@Override
			public void convertToSaveableFormat() {
				// stub

			}
		};
    }

    @Override
    public void setWaitCursor() {
		// not needed with no UI
    }

    @Override
    public void updateStyleBars() {
		// not needed with no UI
    }

    @Override
    public void updateDynamicStyleBars() {
		// not needed with no UI
    }

    @Override
    public void set1rstMode() {
		// TODO
    }

    @Override
	public SpreadsheetTableModel getSpreadsheetTableModel() {
		if (tableModel == null) {
			tableModel = new SpreadsheetTableModelSimple(this,
					SPREADSHEET_INI_ROWS, SPREADSHEET_INI_COLS);
		}
		return tableModel;
	}

    @Override
    public GgbAPI getGgbApi() {
		return new GgbAPIHeadless(this);
    }

    @Override
    public SoundManager getSoundManager() {
		return new SoundManager() {

			@Override
			public void pauseResumeSound(boolean b) {
				// stub
			}

			@Override
			public void playSequenceNote(int double1, double double2, int i,
					int j) {
				// stub
			}

			@Override
			public void playSequenceFromString(String string, int double1) {
				// stub
			}

			@Override
			public void playFunction(GeoFunction geoFunction, double double1,
					double double2) {
				// stub
			}

			@Override
			public void playFile(GeoElement geoElement, String file) {
				// stub
			}

			@Override
			public void playFunction(GeoFunction geoFunction, double double1,
					double double2, int double3, int double4) {
				// stub
			}

			@Override
			public void loadGeoAudio(GeoAudio geo) {
				// stub
			}

			@Override
			public int getDuration(GeoAudio geoAudio) {
				// stub
				return 0;
			}

			@Override
			public int getCurrentTime(GeoAudio geoAudio) {
				// stub
				return 0;
			}

			@Override
			public void setCurrentTime(GeoAudio geoAudio, int pos) {
				// stub

			}

			@Override
			public void checkURL(String url, AsyncOperation<Boolean> callback) {
				// stub

			}

			@Override
			public void play(GeoAudio geo) {
				// stub

			}

			@Override
			public void pause(GeoAudio geo) {
				// stub

			}

			@Override
			public boolean isPlaying(GeoAudio geo) {
				return false;
			}
		};
    }

    @Override
    public boolean showAlgebraInput() {
        return false;
    }

    @Override
    public GlobalKeyDispatcher getGlobalKeyDispatcher() {
        return null;
    }

    @Override
    public void callAppletJavaScript(String string, String args) {
		// TODO delegate to ScriptManager
    }

    @Override
    public void updateMenubar() {
		// not needed with no UI
    }

    @Override
    public void updateUI() {
		// not needed with no UI
    }

    @Override
    public void showURLinBrowser(String string) {
		// not needed with no UI
    }

    @Override
    public void updateApplicationLayout() {
		// not needed with no UI
    }

    @Override
	public boolean clearConstruction() {
		kernel.clearConstruction(true);
		kernel.initUndoInfo();
		resetMaxLayerUsed();
		resetCurrentFile();
		setMoveMode();
		return true;
	}

    @Override
    public void fileNew() {
		clearConstruction();
    }

    @Override
    public void copyGraphicsViewToClipboard() {
		// not needed with no UI
    }

    @Override
    public void exitAll() {
		// not needed with no UI
    }

    @Override
    public void runScripts(GeoElement geo1, String string) {
		// TODO
    }

    @Override
    public boolean freeMemoryIsCritical() {
        return false;
    }

    @Override
    public long freeMemory() {
        return 0;
    }

    @Override
    public EuclidianView createEuclidianView() {
        return null;
    }

    @Override
    public void setActiveView(int evID) {
		// only needed with 3D: overridden
    }

    @Override
    public UndoManager getUndoManager(Construction cons) {
		return new DefaultUndoManager(cons);
    }

    @Override
    public boolean isHTML5Applet() {
        return false;
    }

    @Override
    public CASFactory getCASFactory() {
		return casFactory;
    }

    @Override
    public Factory getFactory() {
        return null;
    }

    @Override
    public NormalizerMinimal getNormalizer() {
		return new NormalizerMinimal();
    }

    @Override
    public void reset() {
		// TODO
    }

    @Override
    public EuclidianController newEuclidianController(Kernel kernel1) {
		return new EuclidianControllerNoGui(this, kernel1);
    }

    @Override
    public DrawEquation getDrawEquation() {
        return new DrawEquationStub();
    }

    @Override
    public void resetUniqueId() {
		// TODO
    }

    @Override
    public LocalizationJre getLocalization() {
        return localization;
    }

	@Override
	public MyXMLio createXMLio(Construction cons) {
		return new MyXMLioCommon(cons.getKernel(), cons);
	}

    @Override
    public void showCustomizeToolbarGUI() {
		// not needed with no UI
    }

    @Override
    public boolean isSelectionRectangleAllowed() {
		return true;
    }

    @Override
    public MyImage getExternalImageAdapter(String filename, int width, int height) {
		if (StringUtil.empty(filename)) {
			return null;
		}
        return externalImages.computeIfAbsent(filename, foo -> new MyImageCommon(width, height));
    }

	@Override
    public ScriptManager newScriptManager() {
		return new ScriptManager(this) {

			@Override
			public void ggbOnInit() {
				// no JS
			}
		};
    }

	/**
	 * @param clear
	 *            whether to disable dialog manager
	 * @param inputs
	 *            prepared inputs
	 */
	public void initDialogManager(boolean clear, String... inputs) {
		dialogManager = clear ? null : new DialogManagerNoGui(this, inputs);
	}

	/**
	 * For testing only
	 */
	public void setConfig(AppConfig config) {
		this.appConfig = config;
		this.kernel.setEquationBehaviour(config.getEquationBehaviour());
	}

	public void setCASFactory(CASFactory casFactory) {
		this.casFactory = casFactory;
	}

	/**
	 * Sets restricted/standalone Graphing config and reinitializes the app.
	 */
	public void setGraphingConfig() {
		setConfig(new AppConfigGraphing());
		reInit();
	}

	/**
	 * Sets unrestricted/Suite Graphing config and reinitializes the app.
	 */
	public void setUnrestrictedGraphingConfig() {
		setConfig(new AppConfigUnrestrictedGraphing());
		reInit();
	}

	/**
	 * Sets Scientific config and reinitializes the app.
	 */
	public void setScientificConfig() {
		setConfig(new AppConfigScientific());
		reInit();
	}

	/**
	 * Sets Geometry config and reinitializes the app.
	 */
	public void setGeometryConfig() {
		setConfig(new AppConfigGeometry());
		reInit();
	}

	/**
	 * Sets 3D config and reinitializes the app.
	 */
	public void set3dConfig() {
		setConfig(new AppConfigGraphing3D());
		reInit();
	}

	/**
	 * Sets Geometry config and reinitializes the app.
	 */
	public void setCasConfig() {
		setConfig(new AppConfigCas());
		reInit();
	}

	/**
	 * Sets Classic config and reinitializes the app.
	 */
	public void setDefaultConfig() {
		setConfig(new AppConfigDefault());
		reInit();
	}

	private void reInit() {
		resetAlgebraOutputFilter();
		kernel.setAlgebraProcessor(null);
		initSettingsUpdater().resetSettingsOnAppStart();
	}

	public void setScriptManager(ScriptManager scriptManager) {
		this.scriptManager = scriptManager;
	}

	public void setAppletFlag(boolean appletFlag) {
		this.appletFlag = appletFlag;
	}

	@Override
	public boolean isWhiteboardActive() {
		return getConfig() instanceof AppConfigNotes;
	}

	@Override
	public Layout getLayout() {
		return layout;
	}

	public void setPrerelease() {
		prerelease = true;
	}

	private static class LayoutHeadless extends Layout {

		@Override
		public Perspective createPerspective() {
			return null;
		}

		@Override
		public boolean applyPerspective(Perspective perspective) {
			return false;
		}

		@Override
		public boolean isOnlyVisible(int viewID) {
			return false;
		}

		@Override
		public DockManager getDockManager() {
			return null;
		}

		@Override
		public void settingsChanged(AbstractSettings settings) {
			// stub
		}
	}

	/**
	 * @param locale
	 *            locale
	 */
	public void setLocale(Locale locale) {
		if (locale == getLocalization().getLocale()) {
			return;
		}
		// Locale oldLocale = loc.getLocale();

		// only allow special locales due to some weird server
		// problems with the naming of the property files
		getLocalization().setLocale(locale);
		getLocalization().updateLanguageFlags(locale.getLanguage());
	}

	@Override
	public boolean isUnbundledGeometry() {
		return "geometry".equals(getConfig().getAppCode());
	}

	public void setImageManager(ImageManager imgManager) {
		imageManager = imgManager;
	}

	@Override
	public boolean isUnbundled() {
		return AppConfigDefault.isUnbundled(getConfig().getAppCode());
	}

	@Override
	protected void getViewsXML(StringBuilder sb, boolean asPreference) {
		getSettings().getAlgebra().getXML(sb, asPreference);
	}
}
