package org.geogebra.desktop.main;

import java.util.Locale;

import org.geogebra.common.GeoGebraConstants.Versions;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.DrawEquation;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.factories.CASFactory;
import org.geogebra.common.factories.Factory;
import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.factories.LaTeXFactory;
import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.geogebra3D.kernel3D.GeoFactory3D;
import org.geogebra.common.geogebra3D.kernel3D.Kernel3D;
//import org.geogebra.common.geogebra3D.kernel3D.Kernel3D;
import org.geogebra.common.gui.Layout;
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.io.MyXMLio;
import org.geogebra.common.jre.factory.FormatFactoryJre;
import org.geogebra.common.jre.plugin.GgbAPIJre;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.UndoManager;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import org.geogebra.common.main.AlgoCubicSwitchInterface;
import org.geogebra.common.main.AlgoCubicSwitchParams;
import org.geogebra.common.main.AlgoKimberlingWeightsInterface;
import org.geogebra.common.main.AlgoKimberlingWeightsParams;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppCompanion;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.main.FontManager;
import org.geogebra.common.main.GlobalKeyDispatcher;
import org.geogebra.common.main.GuiManagerInterface;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.SpreadsheetTableModel;
import org.geogebra.common.plugin.GgbAPI;
import org.geogebra.common.plugin.ScriptManager;
import org.geogebra.common.plugin.SensorLogger;
import org.geogebra.common.sound.SoundManager;
import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimer.GTimerListener;
import org.geogebra.common.util.ImageManager;
import org.geogebra.common.util.NormalizerMinimal;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.factories.AwtFactoryD;
import org.geogebra.desktop.factories.CASFactoryD;
import org.geogebra.desktop.factories.LaTeXFactoryD;
import org.geogebra.desktop.factories.UtilFactoryD;
import org.geogebra.desktop.geogebra3D.App3DCompanionD;
import org.geogebra.desktop.io.MyXMLioD;
import org.geogebra.desktop.kernel.UndoManagerD;
import org.geogebra.desktop.kernel.geos.GeoElementGraphicsAdapterD;
import org.geogebra.desktop.plugin.ScriptManagerD;
import org.geogebra.desktop.plugin.UDPLoggerD;
import org.geogebra.desktop.util.GTimerD;
import org.geogebra.desktop.util.LoggerD;
import org.geogebra.desktop.util.StringUtilD;

public class AppDNoGui extends App {
	private GgbAPI ggbapi;
	private LocalizationD loc;
	private SpreadsheetTableModelD tableModel;

	public AppDNoGui(LocalizationD loc, boolean silent) {

		super(Versions.DESKTOP);
		if (!silent) {
			Log.logger = new LoggerD();
		}

		prerelease = true;
		initFactories();
		this.kernel = new Kernel3D(this, new GeoFactory3D());
		settings = companion.newSettings();
		this.loc = loc;
		loc.setLocale(Locale.US);
		loc.setApp(this);
		Layout.initializeDefaultPerspectives(this, 0.2);
		myXMLio = new MyXMLioD(kernel, kernel.getConstruction());
		initEuclidianViews();

	}

	@Override
	protected AppCompanion newAppCompanion() {
		return new App3DCompanionD(this);
	}

	private void initFactories() {
		AwtFactory.prototype = new AwtFactoryD();
		FormatFactory.prototype = new FormatFactoryJre();
		LaTeXFactory.prototype = new LaTeXFactoryD();

		// moved to getCASFactory() so that applets load quicker
		// geogebra.common.factories.CASFactory.prototype = new CASFactoryD();

		// moved to getCASFactory() so that applets load quicker
		// geogebra.common.factories.SwingFactory.prototype = new
		// SwingFactoryD();

		UtilFactory.prototype = new UtilFactoryD();

		// moved to getFactory() so that applets load quicker
		// geogebra.common.factories.Factory.prototype = new FactoryD();

		StringUtil.prototype = new StringUtilD();

	}

	@Override
	public boolean isApplet() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void storeUndoInfo() {
		// TODO Auto-generated method stub

	}

	@Override
	public void storeUndoInfoAndStateForModeStarting() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isUsingFullGui() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean showView(int view) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void showError(String s) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void showErrorDialog(String s) {
		// TODO Auto-generated method stub

	}

	@Override
	public ScriptManager getScriptManager() {
		if (scriptManager == null) {
			scriptManager = new ScriptManagerD(this);
		}
		return scriptManager;
	}

	@Override
	public boolean freeMemoryIsCritical() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long freeMemory() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public AlgebraView getAlgebraView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EuclidianView getActiveEuclidianView() {
		return euclidianView;
	}

	@Override
	public boolean hasEuclidianView2EitherShowingOrNot(int idx) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isShowingEuclidianView2(int idx) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ImageManager getImageManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GuiManagerInterface getGuiManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DialogManager getDialogManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void initGuiManager() {
		// TODO Auto-generated method stub

	}

	@Override
	public void evalJavaScript(App app, String script, String arg)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public EuclidianView createEuclidianView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MyImage getExternalImageAdapter(String filename, int width,
			int height) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setActiveView(int evID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showError(String string, String str) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resetUniqueId() {
		// TODO Auto-generated method stub

	}

	@Override
	public DrawEquation getDrawEquation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public GFont getPlainFontCommon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected EuclidianView newEuclidianView(boolean[] showAxes1,
			boolean showGrid1) {
		return new EuclidianViewNoGui(euclidianController, 1, this
				.getSettings().getEuclidian(1));
	}

	@Override
	public EuclidianController newEuclidianController(Kernel kernel1) {
		return new EuclidianControllerNoGui(this, kernel1);
	}

	@Override
	public UndoManager getUndoManager(Construction cons) {
		return new UndoManagerD(cons);
	}

	@Override
	public GeoElementGraphicsAdapter newGeoElementGraphicsAdapter() {
		return new GeoElementGraphicsAdapterD(this);
	}

	@Override
	public void setWaitCursor() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateStyleBars() {
		// TODO Auto-generated method stub

	}

	@Override
	public void set1rstMode() {
		// TODO Auto-generated method stub

	}

	@Override
	public SpreadsheetTableModel getSpreadsheetTableModel() {
		if (tableModel == null) {
			tableModel = new SpreadsheetTableModelD(this, SPREADSHEET_INI_ROWS,
					SPREADSHEET_INI_COLS);
		}
		return tableModel;
	}

	@Override
	public void setXML(String string, boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public GgbAPI getGgbApi() {

		if (ggbapi == null) {
			ggbapi = new GgbAPIJre(this) {

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

				public boolean writePNGtoFile(String filename,
						double exportScale, boolean transparent, double DPI) {
					// TODO Auto-generated method stub
					return false;
				}

				public void clearImage(String label) {
					// TODO Auto-generated method stub

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
				protected String base64encodePNG(boolean transparent,
						double DPI, double exportScale, EuclidianView ev) {
					// TODO Auto-generated method stub
					return null;
				}

			};
		}

		return ggbapi;
	}

	@Override
	public SoundManager getSoundManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean showAlgebraInput() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public GlobalKeyDispatcher getGlobalKeyDispatcher() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void callAppletJavaScript(String string, Object[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateMenubar() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateUI() {
		// TODO Auto-generated method stub

	}

	@Override
	protected FontManager getFontManager() {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub

	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isHTML5Applet() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void showURLinBrowser(String string) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uploadToGeoGebraTube() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateApplicationLayout() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean clearConstruction() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createNewWindow() {
		// TODO Auto-generated method stub

	}

	@Override
	public void fileNew() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean loadXML(String xml) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void copyGraphicsViewToClipboard() {
		// TODO Auto-generated method stub

	}

	@Override
	public void copyBase64ToClipboard() {
		// TODO Auto-generated method stub

	}

	@Override
	public void copyFullHTML5ExportToClipboard() {
		// TODO Auto-generated method stub

	}

	@Override
	public void exitAll() {
		// TODO Auto-generated method stub

	}

	@Override
	public NormalizerMinimal getNormalizer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void runScripts(GeoElement geo1, String string) {
		// TODO Auto-generated method stub

	}

	@Override
	public CASFactory getCASFactory() {
		return new CASFactoryD();
	}

	@Override
	public Factory getFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MyXMLio getXMLio() {
		return myXMLio;
	}

	@Override
	public MyXMLio createXMLio(Construction cons) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Localization getLocalization() {
		return loc;
	}

	@Override
	public double getMillisecondTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public AlgoKimberlingWeightsInterface getAlgoKimberlingWeights() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double kimberlingWeight(AlgoKimberlingWeightsParams kw) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public AlgoCubicSwitchInterface getAlgoCubicSwitch() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String cubicSwitch(AlgoCubicSwitchParams kw) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CommandDispatcher getCommandDispatcher(Kernel k) {
		return new CommandDispatcher(k) {
		};
	}

	@Override
	public void showCustomizeToolbarGUI() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isSelectionRectangleAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getEnglishCommand(String command) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void closePopups() {
		// TODO Auto-generated method stub

	}

	@Override
	public GTimer newTimer(GTimerListener listener, int delay) {
		return new GTimerD(listener, delay);
	}

	@Override
	public void setCurrentFile(Object file) {
		// TODO Auto-generated method stub

	}

	public void setLanguage(Locale locale) {

		if ((locale == null)
				|| loc.getLocale().toString().equals(locale.toString())) {
			return;
		}

		if (!initing) {
			setMoveMode();
		}

		// load resource files
		setLocale(locale);

		// update right angle style in euclidian view (different for German)
		// if (euclidianView != null)
		// euclidianView.updateRightAngleStyle(locale);

		// make sure digits are updated in all numbers
		getKernel().updateConstruction();
		setUnsaved();

		// setLabels(); // update display
		// setOrientation();
	}

	// public static char unicodeThousandsSeparator = ','; // \u066c for Arabic

	StringBuilder testCharacters = new StringBuilder();
	private SensorLogger udpLogger;

	public void setLocale(Locale locale) {
		if (locale == loc.getLocale()) {
			return;
		}
		Locale oldLocale = loc.getLocale();

		// only allow special locales due to some weird server
		// problems with the naming of the property files
		loc.setLocale(locale);

		// update font for new language (needed for e.g. chinese)
		// try {
		// fontManager.setLanguage(loc.getLocale());
		// } catch (Exception e) {
		// e.printStackTrace();
		// showError(e.getMessage());
		//
		// // go back to previous locale
		// loc.setLocale(oldLocale);
		// }

		getLocalization().updateLanguageFlags(locale.getLanguage());

	}

	@Override
	public SensorLogger getSensorLogger() {
		if (udpLogger == null) {
			udpLogger = new UDPLoggerD(getKernel());
		}
		return udpLogger;
	}

	@Override
	public EuclidianView3DInterface getEuclidianView3D() {
		return new EuclidianView3DNoGui(
				new EuclidianController3DNoGui(this, kernel),
				this.getSettings().getEuclidian(3));
	}
}
