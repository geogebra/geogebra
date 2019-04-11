package org.geogebra.common.jre.headless;

import org.geogebra.common.GeoGebraConstants.Versions;
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
import org.geogebra.common.gui.font.FontCreator;
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.io.MyXMLio;
import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.jre.factory.FormatFactoryJre;
import org.geogebra.common.jre.io.MyXMLioCommon;
import org.geogebra.common.jre.kernel.commands.CommandDispatcherJre;
import org.geogebra.common.jre.main.LocalizationJre;
import org.geogebra.common.jre.plugin.GgbAPIJre;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.DefaultUndoManager;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.UndoManager;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import org.geogebra.common.main.App;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.main.FontManager;
import org.geogebra.common.main.GlobalKeyDispatcher;
import org.geogebra.common.main.GuiManagerInterface;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.SpreadsheetTableModel;
import org.geogebra.common.main.SpreadsheetTableModelSimple;
import org.geogebra.common.main.settings.DefaultSettings;
import org.geogebra.common.plugin.GgbAPI;
import org.geogebra.common.plugin.ScriptManager;
import org.geogebra.common.sound.SoundManager;
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

	private LocalizationJre localization;
	private DialogManagerNoGui dialogManager;
	private DefaultSettings defaultSettings;
	private FontCreator fontCreator;
	private SpreadsheetTableModel tableModel;

    /**
     * Construct an AppCommon.
     */
	public AppCommon(LocalizationJre loc, AwtFactory awtFactory) {
		super(Versions.ANDROID_NATIVE_GRAPHING);
		AwtFactory.setPrototypeIfNull(awtFactory);
        initFactories();
		initKernel();
		localization = loc;
        initLocalization();
		getLocalization().initTranslateCommand();
		initSettings();
		initEuclidianViews();
		Layout.initializeDefaultPerspectives(this, 0.2);
		Log.setLogger(new Log() {

			@Override
			protected void print(String logEntry, Level level) {
				System.out.println(logEntry);
			}

			@Override
			public void doPrintStacktrace(String message) {
				new Throwable(message).printStackTrace();

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
	public FontCreator getFontCreator() {
    	if (fontCreator == null) {
    		fontCreator = new FontCreatorNoGui(settings.getFontSettings());
		}
		return fontCreator;
	}

	@Override
    protected void initLocalization() {
        localization.setApp(this);
        super.initLocalization();
    }

    private void initFactories() {
        FormatFactory.setPrototypeIfNull(new FormatFactoryJre());
		StringUtil.setPrototypeIfNull(new StringUtil());
    }

    @Override
    protected void showErrorDialog(String msg) {

    }

    @Override
    protected void initGuiManager() {

    }

    @Override
    protected EuclidianView newEuclidianView(boolean[] showAxes1, boolean showGrid1) {
		this.getSettings().getEuclidian(1).setPreferredSize(
				AwtFactory.getPrototype().newDimension(800, 600));
		return new EuclidianViewNoGui(getEuclidianController(), 1,
				this.getSettings().getEuclidian(1),
				createGraphics());
    }

	protected final GGraphics2D createGraphics() {
		return AwtFactory.getPrototype().createBufferedImage(800, 600, false)
				.createGraphics();
	}

	@Override
    protected FontManager getFontManager() {
		return new FontManagerNoGui();
    }

    @Override
    protected int getWindowWidth() {
        return 0;
    }

    @Override
    protected int getWindowHeight() {
        return 0;
    }

    @Override
    protected void getLayoutXML(StringBuilder sb, boolean asPreference) {

    }

    @Override
    public CommandDispatcher getCommandDispatcher(Kernel kernel) {
		return new CommandDispatcherJre(kernel);
    }

    @Override
    public CommandDispatcher getCommand3DDispatcher(Kernel kernel) {
        return null;
    }

    @Override
    public void invokeLater(Runnable runnable) {

    }

    @Override
    public boolean isApplet() {
        return false;
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

    }

    @Override
    public GTimer newTimer(GTimerListener listener, int delay) {
        return null;
    }

    @Override
    public boolean isUsingFullGui() {
        return false;
    }

    @Override
    public boolean showView(int view) {
		Perspective p = this.getTmpPerspective(null);
		if (p != null) {
			for (DockPanelData dp : p.getDockPanelData()) {
				if (dp.getViewId() == view) {
					return dp.isVisible();
				}
			}
		}
		return false;
    }

    @Override
    public void showError(String localizedError) {

    }

    @Override
    public void showError(String string, String str) {

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
        return null;
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
    public void evalJavaScript(App app, String script, String arg) throws Exception {

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
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void setImageFileName(String fileName) {
				// TODO Auto-generated method stub

			}

			@Override
			public void convertToSaveableFormat() {
				// TODO Auto-generated method stub

			}
		};
    }

    @Override
    public void setWaitCursor() {

    }

    @Override
    public void updateStyleBars() {

    }

    @Override
    public void updateDynamicStyleBars() {

    }

    @Override
    public void set1rstMode() {

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
	public void setXML(String xml, boolean clearAll) {
		// TODO copied from AppDNoGui
		if (xml == null) {
			return;
		}
		if (clearAll) {
			resetCurrentFile();
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
			showError("LoadFileFailed");
		}
    }

    @Override
    public GgbAPI getGgbApi() {
		return new GgbAPIJre(this) {

			public byte[] getGGBfile() {
				// TODO Auto-generated method stub
				return null;
			}

			public void setErrorDialogsActive(boolean flag) {
				// TODO Auto-generated method stub

			}

			public void refreshViews() {
				// TODO Auto-generated method stub

			}

			public void openFile(String strURL) {
				// TODO Auto-generated method stub

			}

			public boolean writePNGtoFile(String filename, double exportScale,
					boolean transparent, double DPI, boolean greyscale) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			protected void exportPNGClipboard(boolean transparent, int DPI,
					double exportScale, EuclidianView ev) {
				// TODO Auto-generated method stub

			}

			@Override
			protected void exportPNGClipboardDPIisNaN(boolean transparent,
					double exportScale, EuclidianView ev) {
				// TODO Auto-generated method stub

			}

			@Override
			protected String base64encodePNG(boolean transparent, double DPI,
					double exportScale, EuclidianView ev) {
				// TODO Auto-generated method stub
				return null;
			}
		};
    }

    @Override
    public SoundManager getSoundManager() {
        return null;
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
    public void callAppletJavaScript(String string, String... args) {

    }

    @Override
    public void updateMenubar() {

    }

    @Override
    public void updateUI() {

    }

    @Override
    public void showURLinBrowser(String string) {

    }

    @Override
    public void uploadToGeoGebraTube() {

    }

    @Override
    public void updateApplicationLayout() {

    }

    @Override
	public boolean clearConstruction() {
		kernel.clearConstruction(true);
		kernel.initUndoInfo();
		resetMaxLayerUsed();
		this.resetCurrentFile();
		setMoveMode();
		return true;
	}

    @Override
    public void fileNew() {

    }

    @Override
    public boolean loadXML(String xml) throws Exception {
        return false;
    }

    @Override
    public void copyGraphicsViewToClipboard() {

    }

    @Override
    public void exitAll() {

    }

    @Override
    public void runScripts(GeoElement geo1, String string) {

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
		return new CASFactoryDummy();
    }

    @Override
    public Factory getFactory() {
        return null;
    }

    @Override
    public NormalizerMinimal getNormalizer() {
        return null;
    }

    @Override
    public void reset() {

    }

    @Override
    public EuclidianController newEuclidianController(Kernel kernel1) {
		return new EuclidianControllerNoGui(this, kernel1);
    }

    @Override
    public DrawEquation getDrawEquation() {
        return null;
    }

    @Override
    public void resetUniqueId() {

    }

    @Override
    public Localization getLocalization() {
        return localization;
    }

    @Override
    public MyXMLio createXMLio(Construction cons) {
		return new MyXMLioCommon(getKernel(), getKernel().getConstruction());
    }

    @Override
    public void showCustomizeToolbarGUI() {

    }

    @Override
    public boolean isSelectionRectangleAllowed() {
		return true;
    }

    @Override
    public MyImage getExternalImageAdapter(String filename, int width, int height) {
        return null;
    }

    @Override
    public ScriptManager newScriptManager() {
		return new ScriptManager(this) {

			@Override
			public void ggbOnInit() {
				// no JS
			}

			@Override
			public void callJavaScript(String jsFunction, String[] args) {
				// no JS
			}
		};
    }

	public void initDialogManager(boolean clear, String... inputs) {
		dialogManager = clear ? null : new DialogManagerNoGui(this, inputs);
	}
}
