package org.geogebra.desktop.headless;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.SwingUtilities;

import org.geogebra.common.GeoGebraConstants.Versions;
import org.geogebra.common.awt.GBufferedImage;
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
import org.geogebra.common.geogebra3D.kernel3D.commands.CommandDispatcher3D;
import org.geogebra.common.gui.Layout;
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.io.MyXMLio;
import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.jre.factory.FormatFactoryJre;
import org.geogebra.common.jre.gui.MyImageJre;
import org.geogebra.common.jre.headless.AppDI;
import org.geogebra.common.jre.headless.DialogManagerNoGui;
import org.geogebra.common.jre.headless.EuclidianController3DNoGui;
import org.geogebra.common.jre.headless.EuclidianControllerNoGui;
import org.geogebra.common.jre.headless.EuclidianView3DNoGui;
import org.geogebra.common.jre.headless.EuclidianViewNoGui;
import org.geogebra.common.jre.headless.FontManagerNoGui;
import org.geogebra.common.jre.kernel.commands.CommandDispatcher3DJre;
import org.geogebra.common.jre.kernel.commands.CommandDispatcherJre;
import org.geogebra.common.jre.plugin.GgbAPIJre;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.GeoFactory;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.UndoManager;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppCompanion;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.FontManager;
import org.geogebra.common.main.GlobalKeyDispatcher;
import org.geogebra.common.main.GuiManagerInterface;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.SpreadsheetTableModel;
import org.geogebra.common.main.SpreadsheetTableModelSimple;
import org.geogebra.common.media.VideoManager;
import org.geogebra.common.plugin.GgbAPI;
import org.geogebra.common.plugin.ScriptManager;
import org.geogebra.common.plugin.SensorLogger;
import org.geogebra.common.sound.SoundManager;
import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimerListener;
import org.geogebra.common.util.ImageManager;
import org.geogebra.common.util.NormalizerMinimal;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.awt.GBufferedImageD;
import org.geogebra.desktop.awt.GDimensionD;
import org.geogebra.desktop.awt.GFontD;
import org.geogebra.desktop.awt.GGraphics2DD;
import org.geogebra.desktop.euclidian.DrawEquationD;
import org.geogebra.desktop.factories.AwtFactoryD;
import org.geogebra.desktop.factories.LaTeXFactoryD;
import org.geogebra.desktop.factories.LoggingCASFactoryD;
import org.geogebra.desktop.factories.UtilFactoryD;
import org.geogebra.desktop.geogebra3D.App3DCompanionD;
import org.geogebra.desktop.gui.menubar.GeoGebraMenuBar;
import org.geogebra.desktop.io.MyXMLioD;
import org.geogebra.desktop.kernel.UndoManagerD;
import org.geogebra.desktop.kernel.geos.GeoElementGraphicsAdapterD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.move.ggtapi.models.LoginOperationD;
import org.geogebra.desktop.plugin.GgbAPID;
import org.geogebra.desktop.plugin.ScriptManagerD;
import org.geogebra.desktop.plugin.UDPLoggerD;
import org.geogebra.desktop.sound.SoundManagerD;
import org.geogebra.desktop.util.GTimerD;
import org.geogebra.desktop.util.ImageManagerD;
import org.geogebra.desktop.util.LoggerD;
import org.geogebra.desktop.util.Normalizer;
import org.geogebra.desktop.util.StringUtilD;


/**
 * App for testing: does not use Swing
 * 
 * @author Zbynek
 *
 */
public class AppDNoGui extends App implements AppDI {
	private GgbAPI ggbapi;
	private LocalizationD loc;
	private SpreadsheetTableModelSimple tableModel;
	private DrawEquationD drawEquation;
	private boolean is3Dactive;
	private EuclidianView3DNoGui ev3d;
	private SoundManager soundManager;
	private DialogManager dialogManager;

	/**
	 * @param loc
	 *            localization
	 * @param silent
	 *            whether to mute logging
	 */
	public AppDNoGui(LocalizationD loc, boolean silent) {
		this(loc, silent, 3);
	}

	/**
	 * @param loc
	 *            localization
	 * @param silent
	 *            whether to mute logging
	 */
	public AppDNoGui(LocalizationD loc, boolean silent, int dimension) {

		super(Versions.DESKTOP);
		if (!silent) {
			Log.setLogger(new LoggerD());
		}

		prerelease = true;
		String prop = System.getProperty("ggb.prerelease");
		if ("false".equals(prop)) {
			prerelease = false;
		}
		Log.debug(prerelease ? "Start up prerelese." : "Start up stable.");

		// print GeoGebra & Java versions
		StringBuilder sb = new StringBuilder();
		GeoGebraMenuBar.appendVersion(sb, this);
		sb.append(" Java ");
		AppD.appendJavaVersion(sb);
		Log.debug(sb.toString());

		initFactories();
		this.kernel = dimension == 2 ? new Kernel(this, new GeoFactory())
				: new Kernel3D(this, new GeoFactory3D());
		initSettings();
		this.loc = loc;
		loc.setLocale(Locale.US);
		loc.setApp(this);
		Layout.initializeDefaultPerspectives(this, 0.2);
		initEuclidianViews();
		loginOperation = new LoginOperationD(this);
		kernel.attach(euclidianView);
	}

	@Override
	protected AppCompanion newAppCompanion() {
		return new App3DCompanionD(this);
	}

	/**
	 * init factories
	 */
	protected void initFactories() {

		if (AwtFactory.getPrototype() == null) {
			AwtFactory.setPrototypeIfNull(new AwtFactoryD());
		}

		if (FormatFactory.getPrototype() == null) {
			FormatFactory.setPrototypeIfNull(new FormatFactoryJre());
		}

		if (LaTeXFactory.getPrototype() == null) {
			LaTeXFactory.setPrototypeIfNull(new LaTeXFactoryD());
		}

		if (UtilFactory.getPrototype() == null) {
			UtilFactory.setPrototypeIfNull(new UtilFactoryD());
		}

		if (StringUtil.getPrototype() == null) {
			StringUtil.setPrototypeIfNull(new StringUtilD());
		}

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
	public void showError(String s) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void showErrorDialog(String s) {
		// TODO Auto-generated method stub

	}

	@Override
	public ScriptManager newScriptManager() {
		return new ScriptManagerD(this);
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
		return is3Dactive && ev3d != null ? ev3d : euclidianView;
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
		return new ImageManagerD();
	}

	@Override
	public GuiManagerInterface getGuiManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DialogManager getDialogManager() {
		return dialogManager;
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
		MyImageJre im = ImageManagerD.getExternalImage(filename);
		return im;
	}

	@Override
	public void setActiveView(int evID) {
		this.is3Dactive = evID == App.VIEW_EUCLIDIAN3D;
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
		if (drawEquation == null) {
			drawEquation = new DrawEquationD();
		}
		return drawEquation;
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
		return new GFontD(new Font("sans", Font.PLAIN, 12));
	}

	@Override
	protected EuclidianView newEuclidianView(boolean[] showAxes1,
			boolean showGrid1) {
		this.getSettings().getEuclidian(1)
				.setPreferredSize(new GDimensionD(800, 600));
		GGraphics2DD g2 = new GGraphics2DD(
				new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB)
						.createGraphics());
		return new EuclidianViewNoGui(getEuclidianController(), 1,
				this.getSettings().getEuclidian(1), g2);
	}

	@Override
	public EuclidianController newEuclidianController(Kernel kernel1) {
		return new EuclidianControllerNoGui(this, kernel1);
	}

	@Override
	public UndoManager getUndoManager(Construction cons) {
		return new UndoManagerD(cons, true);
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
	public void updateDynamicStyleBars() {
		// not implemented here
	}

	@Override
	public void set1rstMode() {
		// TODO Auto-generated method stub
	}

	@Override
	public SpreadsheetTableModel getSpreadsheetTableModel() {
		if (tableModel == null) {
			tableModel = new SpreadsheetTableModelSimple(this,
					SPREADSHEET_INI_ROWS,
					SPREADSHEET_INI_COLS);
		}
		return tableModel;
	}

	@Override
	public void setXML(String xml, boolean clearAll) {
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

		if (ggbapi == null) {
			ggbapi = new GgbAPIJre(this) {

				@Override
				public byte[] getGGBfile() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public void setErrorDialogsActive(boolean flag) {
					// TODO Auto-generated method stub
				}

				@Override
				public void refreshViews() {
					// TODO Auto-generated method stub
				}

				@Override
				public void openFile(String strURL) {
					try {
						String lowerCase = StringUtil.toLowerCaseUS(strURL);
						URL url = new URL(strURL);
						GFileHandler.loadXML(AppDNoGui.this, url.openStream(),
								lowerCase.endsWith(
										FileExtensions.GEOGEBRA_TOOL
												.toString()));
					} catch (Exception e) {
						throw new RuntimeException(e);
					}

				}

				@Override
				public boolean writePNGtoFile(String filename,
						double exportScale, boolean transparent, double DPI,
						boolean greyscale) {
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
				protected String base64encodePNG(boolean transparent,
						double DPI, double exportScale, EuclidianView ev) {
					ev.updateBackground();
					GBufferedImage img = ((EuclidianViewNoGui) ev)
							.getExportImage(exportScale, transparent, ExportType.PNG);
					return GgbAPID.base64encode(
							GBufferedImageD.getAwtBufferedImage(img), DPI);
				}

			};
		}

		return ggbapi;
	}

	@Override
	public SoundManager getSoundManager() {
		if (soundManager == null) {
			soundManager = new SoundManagerD(this);
		}
		return soundManager;
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
	public void callAppletJavaScript(String string, String... args) {
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
		// TODO Auto-generated method stub

	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isHTML5Applet() {
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
		kernel.clearConstruction(true);
		kernel.initUndoInfo();
		resetMaxLayerUsed();
		this.resetCurrentFile();
		setMoveMode();
		return true;
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
		return Normalizer.getInstance();
	}

	@Override
	public void runScripts(GeoElement geo1, String string) {
		// TODO Auto-generated method stub
	}

	@Override
	public CASFactory getCASFactory() {
		return new LoggingCASFactoryD();
	}

	@Override
	public Factory getFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MyXMLio createXMLio(Construction cons) {
		return new MyXMLioD(cons.getKernel(), cons);
	}

	@Override
	public Localization getLocalization() {
		return loc;
	}

	@Override
	public CommandDispatcher getCommandDispatcher(Kernel kernel) {
		return new CommandDispatcherJre(kernel);
	}

	@Override
	public CommandDispatcher3D getCommand3DDispatcher(Kernel kernel) {
		return new CommandDispatcher3DJre(kernel);
	}

	@Override
	public void showCustomizeToolbarGUI() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isSelectionRectangleAllowed() {
		return true;
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
	public void resetCurrentFile() {
		// TODO Auto-generated method stub
	}

	/**
	 * @param locale
	 *            locale
	 */
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
		getKernel().updateConstruction(false);
		setUnsaved();

		// setLabels(); // update display
		// setOrientation();
	}

	// public static char unicodeThousandsSeparator = ','; // \u066c for Arabic

	private SensorLogger udpLogger;

	/**
	 * @param locale
	 *            locale
	 */
	public void setLocale(Locale locale) {
		if (locale == loc.getLocale()) {
			return;
		}
		// Locale oldLocale = loc.getLocale();

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
		return ev3d = new EuclidianView3DNoGui(
				new EuclidianController3DNoGui(this, kernel),
				this.getSettings().getEuclidian(3));
	}

	@Override
	public void invokeLater(Runnable runnable) {
		SwingUtilities.invokeLater(runnable);
	}

	@Override
	public boolean is3D() {
		return true;
	}

	public void testFeatures() {
		boolean pre = prerelease;
		ArrayList<Feature> stable = new ArrayList<>();
		ArrayList<Feature> beta = new ArrayList<>();
		ArrayList<Feature> dead = new ArrayList<>();

		for(Feature f:Feature.values()){

			this.prerelease = false;
			if (has(f)) {
				stable.add(f);
			} else {
				this.prerelease = true;
				if (has(f)) {
					beta.add(f);
				} else {
					dead.add(f);
				}
			}
		}
		Log.debug(stable.size() + StringUtil.join("\n", stable));
		Log.debug(beta.size() + StringUtil.join("\n", beta));
		Log.debug(dead.size() + StringUtil.join("\n", dead));

		prerelease = pre;
	}

	@Override
	public VideoManager getVideoManager() {
		// not implemented here.
		return null;
	}

	public void initDialogManager(boolean clear, String... inputs) {
		dialogManager = clear ? null : new DialogManagerNoGui(this, inputs);
	}

	@Override
	public void addExternalImage(String name, MyImageJre img) {
		// TODO Auto-generated method stub
	}

	@Override
	public void storeFrameCenter() {
		// TODO Auto-generated method stub
	}

	@Override
	public MyImageJre getExportImage(double thumbnailPixelsX,
			double thumbnailPixelsY) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MyImageJre getExternalImage(String fileName) {
		// TODO Auto-generated method stub
		return null;
	}

}
