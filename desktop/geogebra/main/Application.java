/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/**
 * GeoGebra Application
 *
 * @author Markus Hohenwarter
 */
package geogebra.main;

import geogebra.CommandLineArguments;
import geogebra.GeoGebra;
import geogebra.common.GeoGebraConstants;
import geogebra.common.euclidian.AbstractEuclidianView;
import geogebra.common.euclidian.DrawTextField;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.gui.dialog.DialogManager;
import geogebra.common.gui.inputfield.AutoCompleteTextField;
import geogebra.common.gui.view.spreadsheet.AbstractSpreadsheetTableModel;
import geogebra.common.io.MyXMLHandler;
import geogebra.common.io.layout.DockPanelData;
import geogebra.common.io.layout.Perspective;
import geogebra.common.kernel.AbstractAnimationManager;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.ConstructionDefaults;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Macro;
import geogebra.common.kernel.Macro;
import geogebra.common.kernel.Relation;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.AbstractApplication;
import geogebra.common.main.MyError;
import geogebra.common.main.settings.ConstructionProtocolSettings;
import geogebra.common.main.settings.Settings;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.common.util.StringUtil;
import geogebra.common.util.Unicode;
import geogebra.euclidian.DrawEquation;
import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianStatic;
import geogebra.euclidian.EuclidianView;
import geogebra.euclidianND.EuclidianViewND;
import geogebra.export.GeoGebraTubeExportDesktop;
import geogebra.export.GraphicExportDialog;
import geogebra.export.WorksheetExportDialog;
import geogebra.gui.GuiManager;
import geogebra.gui.app.GeoGebraFrame;
import geogebra.gui.inputbar.AlgebraInput;
import geogebra.gui.layout.DockBar;
import geogebra.gui.util.ImageSelection;
import geogebra.gui.view.algebra.AlgebraView;
import geogebra.gui.view.spreadsheet.SpreadsheetTableModel;
import geogebra.gui.view.spreadsheet.SpreadsheetTraceManager;
import geogebra.io.MyXMLio;
import geogebra.kernel.AnimationManager;
import geogebra.kernel.UndoManager;
import geogebra.kernel.commands.CmdBarCode;
import geogebra.kernel.geos.GeoElementGraphicsAdapterDesktop;
import geogebra.plugin.CallJavaScript;
import geogebra.plugin.GgbAPI;
import geogebra.plugin.PluginManager;
import geogebra.plugin.ScriptManager;
import geogebra.plugin.jython.PythonBridge;
import geogebra.sound.SoundManager;
import geogebra.util.DebugPrinterDesktop;
import geogebra.util.DownloadManager;
import geogebra.util.ImageManager;
import geogebra.util.Util;

import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.imageio.ImageIO;
import javax.naming.OperationNotSupportedException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.WindowConstants;

public class Application extends AbstractApplication implements
		KeyEventDispatcher {

	// license file
	public static final String LICENSE_FILE = "/geogebra/gui/_license.txt";

	// jar file names
	public final static String CAS_JAR_NAME = "geogebra_cas.jar";
	public final static String JAVASCRIPT_JAR_NAME = "geogebra_javascript.jar";
	public static final String[] JAR_FILES = { "geogebra.jar",
			"geogebra_main.jar", "geogebra_gui.jar", CAS_JAR_NAME,
			"geogebra_algos.jar", "geogebra_export.jar", JAVASCRIPT_JAR_NAME, // don't
																				// put
																				// at
																				// end
																				// (sometimes
																				// omitted,
																				// see
																				// WorksheetExportDialog)
			"jlatexmath.jar", // LaTeX
			"jlm_greek.jar", // Greek Unicode codeblock (for LaTeX texts)
			"jlm_cyrillic.jar", // Cyrillic Unicode codeblock (for LaTeX texts)
			"geogebra_properties.jar" };

	// supported GUI languages (from properties files)
	public static ArrayList<Locale> supportedLocales = new ArrayList<Locale>();
	static {
		if (GeoGebraConstants.IS_PRE_RELEASE) {
			supportedLocales.add(new Locale("sq")); // Albanian
		}

		if (GeoGebraConstants.IS_PRE_RELEASE) {
			supportedLocales.add(new Locale("hy")); // Armenian
		}

		supportedLocales.add(new Locale("ar")); // Arabic
		supportedLocales.add(new Locale("eu")); // Basque
		supportedLocales.add(new Locale("bs")); // Bosnian
		if (GeoGebraConstants.IS_PRE_RELEASE) {
			supportedLocales.add(new Locale("bg")); // Bulgarian
		}
		supportedLocales.add(new Locale("ca")); // Catalan
		if (GeoGebraConstants.IS_PRE_RELEASE) {
			supportedLocales.add(new Locale("zh", "CN")); // Chinese
		}
		// (Simplified)
		supportedLocales.add(new Locale("zh", "TW")); // Chinese (Traditional)
		supportedLocales.add(new Locale("hr")); // Croatian
		supportedLocales.add(new Locale("cs")); // Czech
		supportedLocales.add(new Locale("da")); // Danish
		supportedLocales.add(new Locale("nl")); // Dutch
		supportedLocales.add(new Locale("en")); // English
		supportedLocales.add(new Locale("en", "GB")); // English (UK)
		supportedLocales.add(new Locale("en", "AU")); // English (Australia)
		supportedLocales.add(new Locale("et")); // Estonian
		supportedLocales.add(new Locale("fi")); // Finnish
		supportedLocales.add(new Locale("fr")); // French
		supportedLocales.add(new Locale("gl")); // Galician
		supportedLocales.add(new Locale("ka")); // Georgian
		supportedLocales.add(new Locale("de")); // German
		supportedLocales.add(new Locale("de", "AT")); // German (Austria)
		supportedLocales.add(new Locale("el")); // Greek
		// supportedLocales.add(new Locale("gu")); // Gujarati
		supportedLocales.add(new Locale("iw")); // Hebrew
		if (GeoGebraConstants.IS_PRE_RELEASE) {
			supportedLocales.add(new Locale("hi")); // Hindi
		}
		supportedLocales.add(new Locale("hu")); // Hungarian
		supportedLocales.add(new Locale("is")); // Icelandic
		if (GeoGebraConstants.IS_PRE_RELEASE) {
			supportedLocales.add(new Locale("in")); // Indonesian
		}
		supportedLocales.add(new Locale("it")); // Italian
		supportedLocales.add(new Locale("ja")); // Japanese

		supportedLocales.add(new Locale("kk")); // Kazakh
		supportedLocales.add(new Locale("ko")); // Korean
		supportedLocales.add(new Locale("lt")); // Lithuanian
		if (GeoGebraConstants.IS_PRE_RELEASE) {
			supportedLocales.add(new Locale("ml")); // Malayalam
		}
		supportedLocales.add(new Locale("mk")); // Macedonian
		if (GeoGebraConstants.IS_PRE_RELEASE) {
			supportedLocales.add(new Locale("mr")); // Marathi
		}
		supportedLocales.add(new Locale("ms")); // Malay
		if (GeoGebraConstants.IS_PRE_RELEASE) {
			supportedLocales.add(new Locale("ne")); // Nepalese
		}
		supportedLocales.add(new Locale("no", "NO")); // Norwegian (Bokmal)
		supportedLocales.add(new Locale("no", "NO", "NY")); // Norwegian(Nynorsk)
		// supportedLocales.add(new Locale("oc")); // Occitan
		supportedLocales.add(new Locale("fa")); // Persian
		supportedLocales.add(new Locale("pl")); // Polish
		supportedLocales.add(new Locale("pt")); // Portugese (Brazil)
		supportedLocales.add(new Locale("pt", "PT")); // Portuguese (Portugal)
		// supportedLocales.add(new Locale("pa")); // Punjabi
		if (GeoGebraConstants.IS_PRE_RELEASE) {
			supportedLocales.add(new Locale("ro")); // Romanian
		}
		supportedLocales.add(new Locale("ru")); // Russian
		supportedLocales.add(new Locale("sr")); // Serbian
		if (GeoGebraConstants.IS_PRE_RELEASE) {
			supportedLocales.add(new Locale("si")); // Sinhala (Sri Lanka)
		}

		supportedLocales.add(new Locale("sk")); // Slovakian
		supportedLocales.add(new Locale("sl")); // Slovenian
		supportedLocales.add(new Locale("es")); // Spanish
		supportedLocales.add(new Locale("sv")); // Swedish
		// supportedLocales.add(new Locale("ty")); // Tahitian
		supportedLocales.add(new Locale("ta")); // Tamil

		if (GeoGebraConstants.IS_PRE_RELEASE) {
			supportedLocales.add(new Locale("tl")); // Filipino
		}
		// supportedLocales.add(new Locale("te")); // Telugu
		if (GeoGebraConstants.IS_PRE_RELEASE) {
			supportedLocales.add(new Locale("th")); // Thai
		}

		supportedLocales.add(new Locale("tr")); // Turkish
		if (GeoGebraConstants.IS_PRE_RELEASE) {
			supportedLocales.add(new Locale("uk")); // Ukrainian
		}
		// supportedLocales.add(new Locale("ur")); // Urdu
		supportedLocales.add(new Locale("vi")); // Vietnamese
		if (GeoGebraConstants.IS_PRE_RELEASE) {
			supportedLocales.add(new Locale("cy")); // Welsh
		}
		if (GeoGebraConstants.IS_PRE_RELEASE) {
			supportedLocales.add(new Locale("ia")); // Interlingua
		}

		if (GeoGebraConstants.IS_PRE_RELEASE) {
			supportedLocales.add(new Locale("ji")); // Yiddish
		}
	}



	// made a little darker in ggb40
	// (problem showing on some projectors)
	public static final Color COLOR_SELECTION = new Color(210, 210, 225);

	// Font settings
	public static final int MIN_FONT_SIZE = 10;

	

	

	// maximum number of files to (save &) show in File -> Recent submenu
	public static final int MAX_RECENT_FILES = 8;

	// file extension string
	public static final String FILE_EXT_GEOGEBRA = "ggb";
	public static final String FILE_EXT_GEOGEBRA_TOOL = "ggt";
	public static final String FILE_EXT_PNG = "png";
	public static final String FILE_EXT_EPS = "eps";
	public static final String FILE_EXT_PDF = "pdf";
	public static final String FILE_EXT_EMF = "emf";
	public static final String FILE_EXT_SVG = "svg";
	public static final String FILE_EXT_HTML = "html";
	public static final String FILE_EXT_HTM = "htm";
	public static final String FILE_EXT_TEX = "tex";

	protected File currentPath, currentImagePath, currentFile = null;

	// page margin in cm
	public static final double PAGE_MARGIN_X = (1.8 * 72) / 2.54;
	public static final double PAGE_MARGIN_Y = (1.8 * 72) / 2.54;

	private static final String RB_MENU = "/geogebra/properties/menu";
	private static final String RB_COMMAND = "/geogebra/properties/command";
	private static final String RB_ERROR = "/geogebra/properties/error";
	private static final String RB_PLAIN = "/geogebra/properties/plain";
	private static final String RB_SYMBOL = "/geogebra/properties/symbols";
	public static final String RB_JAVA_UI = "/geogebra/properties/javaui";
	private static final String RB_COLORS = "/geogebra/properties/colors";

	private static final String RB_SETTINGS = "/geogebra/export/settings";

	// private static Color COLOR_STATUS_BACKGROUND = new Color(240, 240, 240);

	/**
	 * The preferred size of this application. Used in case the frame size
	 * should be updated.
	 */
	private Dimension preferredSize = new Dimension();

	public static final int DEFAULT_ICON_SIZE = 32;

	private JFrame frame;
	private static AppletImplementation appletImpl;
	private final FontManager fontManager;

	protected GuiManager guiManager;
	

	private Component mainComp;
	private boolean isApplet = false;

	

	private GlobalKeyDispatcher globalKeyDispatcher;

	// For language specific settings 
	private Locale currentLocale;

	private Locale tooltipLocale = null;
	private ResourceBundle rbmenu, rbmenuTT, rbcommand, rbcommandTT,
			rbcommandEnglish, rbcommandOld, rbcommandScripting, rberror,
			rbcolors, rbplain, rbplainTT, rbmenuEnglish, rbsymbol, rbsettings;
	protected ImageManager imageManager;
	private int maxIconSize = DEFAULT_ICON_SIZE;

	// Hashtable for translation of commands from
	// local language to internal name
	// key = local name, value = internal name


	protected boolean showAlgebraView = true;

	/*
	 * stored here rather than in algebra view so that it can be set without
	 * creating an AV (compatibility with 3.2)
	 */
	public boolean showAuxiliaryObjects = false;
	private boolean showAlgebraInput = true;
	private boolean showInputTop = false;
	private boolean showInputHelpToggle = true;
	protected boolean showToolBar = true;
	private boolean showToolBarTop = true;
	
	protected boolean showConsProtNavigation = false;
	
	
	private boolean printScaleString = false;
	
	private boolean allowToolTips = true;

	private boolean rightClickEnabled = true;
	private boolean chooserPopupsEnabled = true;
	private boolean isErrorDialogsActive = true;
	private boolean isErrorDialogShowing = false;
	private static LinkedList<File> fileList = new LinkedList<File>();
	// private int guiFontSize;
	// private int axesFontSize;
	// private int euclidianFontSize;

	protected JPanel centerPanel, topPanel, bottomPanel;

	private ArrayList<Perspective> tmpPerspectives = new ArrayList<Perspective>();

	private GgbAPI ggbapi = null;
	private PluginManager pluginmanager = null;
	private PythonBridge pythonBridge = null;

	// GUI elements to support a sidebar help panel for the input bar.
	// The help panel slides open on a button press from the input bar.
	private JSplitPane applicationSplitPane;

	private DockBar dockBar;

	public void openDockBar() {
		if (dockBar != null) {
			dockBar.openDockBar();
		}
	}

	private SpreadsheetTableModel tableModel;
	
	
	
	
	public Application(CommandLineArguments args, JFrame frame,
			boolean undoActive) {
		this(args, frame, null, null, undoActive);
	}

	public Application(CommandLineArguments args,
			AppletImplementation appletImpl, boolean undoActive) {
		this(args, null, appletImpl, null, undoActive);
	}

	public Application(CommandLineArguments args, Container comp,
			boolean undoActive) {
		this(args, null, null, comp, undoActive);
	}

	protected Application(CommandLineArguments args, JFrame frame,
			AppletImplementation appletImpl, Container comp, boolean undoActive) {
		if(!args.containsArg("silent"))
			AbstractApplication.dbg = new DebugPrinterDesktop();
		

		setFileVersion(GeoGebraConstants.VERSION_STRING);
		
		OS = System.getProperty("os.name").toLowerCase(
				Locale.US);
		
		if (args != null) {
			handleHelpVersionArgs(args);
		}

		isApplet = appletImpl != null;

		JApplet applet = null;
		if (frame != null) {
			mainComp = frame;
		} else if (isApplet) {
			applet = appletImpl.getJApplet();
			mainComp = applet;
			setApplet(appletImpl);
		} else {
			mainComp = comp;
		}

		useFullGui = !isApplet || appletImpl.needsGui();

		// don't want to redirect System.out and System.err when running as
		// Applet
		// or eg from Eclipse
		getCodeBase(); // initialize runningFromJar

		if (!isApplet && runningFromJar) {
			setUpLogging();
		}

		// needed for JavaScript getCommandName(), getValueString() to work
		// (security problem running non-locally)
		if (isApplet) {
			preferredSize = appletImpl.getJApplet().getSize();
			// needs command.properties in main.jar
			// causes problems when not in English
			// initCommandBundle();
		} else {
			preferredSize = new Dimension(800, 600);
		}

		fontManager = new FontManager();
		initImageManager(mainComp);

		// set locale
		setLocale(mainComp.getLocale());

		// init kernel
		initFactories();
		initKernel();
		kernel.setPrintDecimals(Kernel.STANDARD_PRINT_DECIMALS);

		// init settings
		settings = new Settings();

		// init euclidian view
		initEuclidianViews();
		
		// create Python Bridge
		if (!isApplet) {
			pythonBridge = new PythonBridge(this);
		}
		
		// load file on startup and set fonts
		// set flag to avoid multiple calls of setLabels() and
		// updateContentPane()
		initing = true;
		setFontSize(12);

		// This is needed because otherwise Exception might come and
		// GeoGebra may exit. (dockPanel not entirely defined)
		// This is needed before handleFileArg because
		// we don't want to redefine the toolbar string from the file.
		boolean ggtloading = isLoadingTool(args);
		if (ggtloading) {
			if (!isApplet) {
				GeoGebraPreferences.getPref().loadXMLPreferences(this);
			}
		}

		// init default preferences if necessary
		if (!isApplet) {
			GeoGebraPreferences.getPref().initDefaultXML(this);
		}

		// init xml io for construction loading
		myXMLio = new MyXMLio((Kernel) kernel, kernel.getConstruction());

		// open file given by startup parameter
		handleOptionArgsEarly(args); // for --regressionFile=...
		boolean fileLoaded = handleFileArg(args);

		// initialize GUI
		if (isUsingFullGui()) {
			initGuiManager();

			// set frame
			if (!isApplet && (frame != null)) {
				setFrame(frame);
			}
		}

		if (!isApplet) {
			// load XML preferences
			currentPath = GeoGebraPreferences.getPref().getDefaultFilePath();
			currentImagePath = GeoGebraPreferences.getPref()
					.getDefaultImagePath();

			if (!fileLoaded && !ggtloading) {
				GeoGebraPreferences.getPref().loadXMLPreferences(this);
			}
		}

		if (isUsingFullGui() && (tmpPerspectives != null) && !ggtloading) {
			getGuiManager().getLayout().setPerspectives(tmpPerspectives);
		}

		setUndoActive(undoActive);

		// applet/command line options
		handleOptionArgs(args);

		initing = false;

		// for key listening
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.addKeyEventDispatcher(this);

		// init plugin manager for applications
		if (!isApplet) {
			pluginmanager = getPluginManager();
		}

		if (!isApplet()) {
			getScriptManager().ggbOnInit();
		}

		isSaved = true;

		if (getCASVersionString().equals("")) {
			setCASVersionString(getPlain("CASInitializing"));
			
		}
	}

	private void initFactories() {
		geogebra.common.factories.AwtFactory.prototype = new geogebra.factories.AwtFactory();
		geogebra.common.factories.FormatFactory.prototype = new geogebra.factories.FormatFactory();
		geogebra.common.factories.LaTeXFactory.prototype = new geogebra.factories.LaTeXFactory();
		geogebra.common.factories.CASFactory.prototype = new geogebra.factories.CASFactory();
		geogebra.common.factories.SwingFactory.prototype = new geogebra.factories.SwingFactory();
		geogebra.common.factories.UtilFactory.prototype = new geogebra.factories.UtilFactory();
		geogebra.common.util.StringUtil.prototype = new geogebra.util.StringUtil();
		// TODO: probably there is better way
		geogebra.common.awt.Color.black = geogebra.awt.Color.black;
		geogebra.common.awt.Color.white = geogebra.awt.Color.white;
		geogebra.common.awt.Color.blue = geogebra.awt.Color.blue;
		geogebra.common.awt.Color.gray = geogebra.awt.Color.gray;
		geogebra.common.awt.Color.lightGray = geogebra.awt.Color.lightGray;
		geogebra.common.awt.Color.darkGray = geogebra.awt.Color.darkGray;
		
		geogebra.common.euclidian.HatchingHandler.prototype = new geogebra.euclidian.HatchingHandler();
		geogebra.common.euclidian.EuclidianStatic.prototype = new geogebra.euclidian.EuclidianStatic();
		
		geogebra.common.euclidian.clipping.DoubleArrayFactory.prototype = new geogebra.euclidian.clipping.DoubleArrayFactory();
		
	}

	private void handleHelpVersionArgs(CommandLineArguments args) {
		if (args.containsArg("help")) {
			// help message
			System.out
					.println("Usage: java -jar geogebra.jar [OPTION] [FILE]\n"
							+ "Start GeoGebra with the specified OPTIONs and open the given FILE.\n"
							+ "  --help\t\tprint this message\n"
							+ "  --v\t\tprint version\n"
							+ "  --language=LANGUAGE_CODE\t\tset language using locale strings, e.g. en, de, de_AT, ...\n"
							+ "  --showAlgebraInput=BOOLEAN\tshow/hide algebra input field\n"
							+ "  --showAlgebraInputTop=BOOLEAN\tshow algebra input at top/bottom\n"
							+ "  --showAlgebraWindow=BOOLEAN\tshow/hide algebra window\n"
							+ "  --showSpreadsheet=BOOLEAN\tshow/hide spreadsheet\n"
							+ (GeoGebraConstants.CAS_VIEW_ENABLED ? "  --showCAS=BOOLEAN\tshow/hide CAS window\n"
									: "")
							+ "  --showSplash=BOOLEAN\tenable/disable the splash screen\n"
							+ "  --enableUndo=BOOLEAN\tenable/disable Undo\n"
							+ "  --fontSize=NUMBER\tset default font size\n"
							+ "  --showAxes=BOOLEAN\tshow/hide coordinate axes\n"
							+ "  --showGrid=BOOLEAN\tshow/hide grid\n"
							+ "  --settingsFile=PATH|FILENAME\tload/save settings from/in a local file\n"
							+ "  --resetSettings\treset current settings\n"
							+ "  --antiAliasing=BOOLEAN\tturn anti-aliasing on/off\n"
							+ "  --regressionFile=FILENAME\texport textual representations of dependent objects, then exit\n");
			System.exit(0);
		}
		// help debug applets
		System.out.println("GeoGebra " + GeoGebraConstants.VERSION_STRING + " "
				+ GeoGebraConstants.BUILD_DATE + " Java "
				+ System.getProperty("java.version"));
		if (args.containsArg("v")) {
			System.exit(0);
		}

	}

	
	@Override
	protected EuclidianController newEuclidianController(Kernel kernel) {
		return new EuclidianController(kernel);
	}

	@Override
	protected AbstractEuclidianView newEuclidianView(boolean[] showAxes,
			boolean showGrid) {
		return new EuclidianView(euclidianController, showAxes, showGrid,
				getSettings().getEuclidian(1));
	}

	/**
	 * init the ImageManager (and ImageManager3D for 3D)
	 * 
	 * @param component
	 */
	protected void initImageManager(Component component) {
		imageManager = new ImageManager(component);
	}

	/**
	 * @return True if the whole GUI is available, false if just the euclidian
	 *         view is displayed.
	 */
	@Override
	final public synchronized boolean isUsingFullGui() {
		return useFullGui;
	}

	/**
	 * Initialize the gui manager.
	 */
	final protected void initGuiManager() {
		setWaitCursor();
		guiManager = newGuiManager();
		guiManager.setLayout(new geogebra.gui.layout.Layout());
		guiManager.initialize();
		setDefaultCursor();
	}

	protected GuiManager newGuiManager() {
		return new GuiManager(Application.this);
	}

	/**
	 * @return this application's GUI manager.
	 */
	@Override
	final public synchronized GuiManager getGuiManager() {
		return guiManager;
	}



	final public JApplet getJApplet() {
		if (appletImpl == null) {
			return null;
		} else {
			return appletImpl.getJApplet();
		}
	}

	final public Font getBoldFont() {
		return fontManager.getBoldFont();
	}

	final public Font getItalicFont() {
		return fontManager.getItalicFont();
	}

	final public Font getPlainFont() {
		return fontManager.getPlainFont();
	}
	
	@Override
	final public geogebra.common.awt.Font getPlainFontCommon() {
		return new geogebra.awt.Font(fontManager.getPlainFont());
	}

	final public Font getSerifFont() {
		return fontManager.getSerifFont();
	}

	final public Font getSmallFont() {
		return fontManager.getSmallFont();
	}

	final public Font getFont(boolean serif, int style, int size) {
		return fontManager.getFont(serif, style, size);
	}

	/**
	 * @return the font manager to access fonts for different tasks
	 */
	final public FontManager getFontManager() {
		return fontManager;
	}



	/**
	 * Sets state of application to "saved", so that no warning appears on
	 * close.
	 * 
	 * @author Zbynek Konecny
	 * @version 2010-05-26
	 */
	public void setSaved() {
		isSaved = true;
	}

	/**
	 * Sets application state to "unsaved" so that user is reminded on close.
	 */
	@Override
	public void setUnsaved() {
		isSaved = false;
	}

	public void fileNew() {
		kernel.resetLibraryJavaScript();
		
		// This needs to happen *before* clearConstruction is called
		// as clearConstruction calls notifyClearView which triggers the
		// updating of the Python Script
		kernel.resetLibraryPythonScript();

		// clear all
		clearConstruction();

		// clear input bar
		if (isUsingFullGui() && showAlgebraInput()) {
			AlgebraInput ai = (AlgebraInput) (getGuiManager().getAlgebraInput());
			ai.clear();
		}

		// reset spreadsheet columns, reset trace columns
		if (isUsingFullGui()) {
			getGuiManager().resetSpreadsheet();
		}

		resetMaxLayerUsed();
		getEuclidianView1().resetXYMinMaxObjects();
		if (hasEuclidianView2EitherShowingOrNot()) {
			getEuclidianView2().resetXYMinMaxObjects();
		}

		if (scriptManager != null) {
			scriptManager.resetListeners();
		}

		resetUniqueId();
	}

	

	
	

	public boolean getAllowToolTips() {
		return allowToolTips;
	}

	/**
	 * Sets allowToolTips flag and toggles tooltips for the application.
	 */
	public void setAllowToolTips(boolean allowToolTips) {
		this.allowToolTips = allowToolTips;
		ToolTipManager.sharedInstance().setEnabled(allowToolTips);
	}

	/**
	 * Updates the GUI of the main component.
	 */
	public void updateContentPane() {
		updateContentPane(true);
	}

	/**
	 * Updates the GUI of the framd and its size.
	 */
	public void updateContentPaneAndSize() {
		if (initing) {
			return;
		}

		updateContentPane(false);
		if ((frame != null) && frame.isShowing()) {
			getGuiManager().updateFrameSize();
		}
		updateComponentTreeUI();
	}

	private void updateContentPane(boolean updateComponentTreeUI) {
		if (initing) {
			return;
		}

		Container cp;
		if (isApplet) {
			cp = appletImpl.getJApplet().getContentPane();
		} else if (frame != null) {
			cp = frame.getContentPane();
		} else {
			cp = (Container) mainComp;
		}

		addMacroCommands();
		cp.removeAll();
		cp.add(buildApplicationPanel());
		fontManager.setFontSize(getGUIFontSize());

		// update sizes
		euclidianView.updateSize();

		// update layout
		if (updateComponentTreeUI) {
			updateComponentTreeUI();
		}

		// reset mode and focus
		setMoveMode();
		if (mainComp.isShowing()) {
			euclidianView.requestFocusInWindow();
		}

		System.gc();
	}

	protected void updateComponentTreeUI() {
		if (isApplet()) {
			SwingUtilities.updateComponentTreeUI(appletImpl.getJApplet());
		} else if (frame != null) {
			SwingUtilities.updateComponentTreeUI(frame);
		} else if (mainComp != null) {
			SwingUtilities.updateComponentTreeUI(mainComp);
		}
	}

	/**
	 * Builds a panel with all components that should be shown on screen (like
	 * toolbar, input field, algebra view).
	 */
	public JPanel buildApplicationPanel() {
		JPanel panel = new JPanel(new BorderLayout());

		// remove existing elements
		if (centerPanel != null) {
			centerPanel.removeAll();
		} else {
			centerPanel = new JPanel(new BorderLayout());
		}
		centerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0,
				SystemColor.controlShadow));
		updateCenterPanel(true);

		// full GUI => use layout manager, add other GUI elements as requested
		if (isUsingFullGui()) {
			topPanel = new JPanel(new BorderLayout());
			bottomPanel = new JPanel(new BorderLayout());

			updateTopBottomPanels();

			// ==================
			// G.Sturr 2010-11-14
			// Create a help panel for the input bar and a JSplitPane to contain
			// it.
			// The splitPane defaults with the application on the left and null
			// on the right.
			// Our help panel will be added/removed as needed by the input bar.
			if (applicationSplitPane == null) {
				applicationSplitPane = new JSplitPane(
						JSplitPane.HORIZONTAL_SPLIT, centerPanel, null);
				applicationSplitPane.setBorder(BorderFactory
						.createEmptyBorder());
				// help panel is on the right, so set all resize weight to the
				// left pane
				applicationSplitPane.setResizeWeight(1.0);
				applicationSplitPane.setDividerSize(0);
			}

			if (dockBar == null) {
				dockBar = new DockBar(this);
			}

			JPanel subPanel = new JPanel(new BorderLayout());

			subPanel.add(topPanel, BorderLayout.NORTH);
			subPanel.add(applicationSplitPane, BorderLayout.CENTER);
			subPanel.add(bottomPanel, BorderLayout.SOUTH);

			panel.add(subPanel, BorderLayout.CENTER);
			panel.add(dockBar, BorderLayout.WEST);

			// init labels
			setLabels();

			// Menubar; if the main component is a JPanel, we need to add the
			// menubar manually to the north
			if (showMenuBar() && (mainComp instanceof JPanel)) {
				JPanel menuBarPanel = new JPanel(new BorderLayout());
				menuBarPanel.add(getGuiManager().getMenuBar(),
						BorderLayout.NORTH);
				menuBarPanel.add(panel, BorderLayout.CENTER);
				return menuBarPanel;
			} else {
				// standard case: return
				return panel;
			}
		}

		// minimal applet => just display EV
		
		panel.add(((EuclidianView) euclidianView).getJPanel(), BorderLayout.CENTER);
		centerPanel.add(panel, BorderLayout.CENTER);
		return panel;
		
	}

	/**
	 * Open/close the sidebar help panel for the input bar
	 */
	public void setShowInputHelpPanel(boolean isVisible) {
		if (isVisible) {
			applicationSplitPane.setRightComponent(getGuiManager()
					.getInputHelpPanel());
			if (applicationSplitPane.getLastDividerLocation() <= 0) {
				applicationSplitPane
						.setLastDividerLocation(applicationSplitPane.getWidth()
								- getGuiManager().getInputHelpPanel()
										.getMinimumSize().width);
			}
			applicationSplitPane.setDividerLocation(applicationSplitPane
					.getLastDividerLocation());
			applicationSplitPane.setDividerSize(8);

		} else {
			applicationSplitPane.setLastDividerLocation(applicationSplitPane
					.getDividerLocation());
			applicationSplitPane.setRightComponent(null);
			applicationSplitPane.setDividerSize(0);
		}
	}

	public void updateDockBar() {
		if (dockBar != null) {
			dockBar.updateViewButtons();
		}
	}

	/**
	 * Updates the component layout of the top and bottom panels. These panels
	 * hold the toolbar and algebra input bar, so this method is called when the
	 * visibility or arrangement of these components is changed.
	 */
	public void updateTopBottomPanels() {
		if ((topPanel == null) || (bottomPanel == null)) {
			return;
		}

		topPanel.removeAll();
		bottomPanel.removeAll();

		if (showAlgebraInput) {
			if (showInputTop) {
				topPanel.add(getGuiManager().getAlgebraInput(),
						BorderLayout.SOUTH);
			} else {
				bottomPanel.add(getGuiManager().getAlgebraInput(),
						BorderLayout.SOUTH);
			}
		}

		// initialize toolbar panel even if it's not used (hack)
		getGuiManager().getToolbarPanelContainer();

		if (showToolBar) {
			if (showToolBarTop) {
				topPanel.add(getGuiManager().getToolbarPanelContainer(),
						BorderLayout.NORTH);
			} else {
				bottomPanel.add(getGuiManager().getToolbarPanelContainer(),
						BorderLayout.NORTH);
			}
		}
		topPanel.revalidate();
	}



	private String regressionFileName = null;

	public void createRegressionFile() throws IOException {
		if (regressionFileName == null) {
			return;
		}
		File regressionFile = new File(regressionFileName);
		FileWriter regressionFileWriter = new FileWriter(regressionFile);
		kernel.updateConstruction();
		regressionFileWriter.append(myXMLio.getConstructionRegressionOut());
		regressionFileWriter.close();
		System.exit(0);
	}



	/**
	 * Adds a macro from XML
	 * 
	 * @param xml
	 *            macro code (including &lt;macro> wrapper)
	 * @return True if successful
	 */
	public boolean addMacroXML(String xml) {
		boolean ok = true;
		try {
			myXMLio.processXMLString("<geogebra format=\""
					+ GeoGebraConstants.XML_FILE_FORMAT + "\">" + xml
					+ "</geogebra>", false, true);
		} catch (MyError err) {
			err.printStackTrace();
			showError(err);
			ok = false;
		} catch (Exception e) {
			e.printStackTrace();
			ok = false;
			showError("LoadFileFailed");
		}
		return ok;
	}

	public void updateCenterPanel(boolean updateUI) {
		if (centerPanel == null) {
			return;
		}

		centerPanel.removeAll();

		if (isUsingFullGui()) {
			centerPanel.add(getGuiManager().getLayout().getRootComponent(),
					BorderLayout.CENTER);
		} else {
			centerPanel.add(getEuclidianView1().getJPanel(), BorderLayout.CENTER);
		}

		if (updateUI) {
			updateComponentTreeUI();
		}
	}

	public JPanel getCenterPanel() {
		return centerPanel;
	}

	public void validateComponent() {
		if (isApplet) {
			appletImpl.getJApplet().validate();
		} else {
			frame.validate();
		}
	}

	/**
	 * Handles command line options
	 */
	private void handleOptionArgs(CommandLineArguments args) {
		// args.containsArg("help");
		if (args == null) {
			return;
		}

		String language = args.getStringValue("language");
		if (language.length() > 0) {
			setLocale(getLocale(language));
		}

		if (args.containsArg("showAlgebraInput")) {
			boolean showAlgebraInput = args.getBooleanValue("showAlgebraInput",
					true);
			if (!showAlgebraInput) {
				setShowAlgebraInput(false, false);
			}
		}

		if (args.containsArg("showAlgebraInputTop")) {
			boolean showAlgebraInputTop = args.getBooleanValue(
					"showAlgebraInputTop", true);
			setShowInputTop(showAlgebraInputTop, false);
		}

		String fontSize = args.getStringValue("fontSize");
		if (fontSize.length() > 0) {
			setFontSize(Integer.parseInt(fontSize));
		}

		boolean enableUndo = args.getBooleanValue("enableUndo", true);
		if (!enableUndo) {
			setUndoActive(false);
		}

		if (args.containsArg("showAxes")) {
			boolean showAxes = args.getBooleanValue("showAxes", true);
			this.showAxes[0] = showAxes;
			this.showAxes[1] = showAxes;
			this.getSettings().getEuclidian(1).setShowAxes(showAxes, showAxes);
			this.getSettings().getEuclidian(2).setShowAxes(showAxes, showAxes);
		}

		if (args.containsArg("showGrid")) {
			boolean showGrid = args.getBooleanValue("showGrid", false);
			this.showGrid = showGrid;
			this.getSettings().getEuclidian(1).showGrid(showGrid);
			this.getSettings().getEuclidian(2).showGrid(showGrid);
		}

		if (args.containsArg("primary")) {
			boolean primary = args.getBooleanValue("primary", false);
			if (primary) {

				getGuiManager().getLayout().applyPerspective("BasicGeometry");
				GlobalKeyDispatcher.changeFontsAndGeoElements(this, 20, false);
				setLabelingStyle(ConstructionDefaults.LABEL_VISIBLE_ALWAYS_OFF);
				getEuclidianView1().setCapturingThreshold(10);
				kernel.setPrintDecimals(0); // rounding to 0dp
				GeoAngle defaultAngle = (GeoAngle) getKernel()
						.getConstruction().getConstructionDefaults()
						.getDefaultGeo(ConstructionDefaults.DEFAULT_ANGLE);
				defaultAngle.setAllowReflexAngle(false);
			}
		}

		boolean antiAliasing = args.getBooleanValue("antiAliasing", true);
		if (!antiAliasing) {
			this.antialiasing = false;
			this.getEuclidianView1().setAntialiasing(antiAliasing);
			this.getEuclidianView2().setAntialiasing(antiAliasing);
		}
	}

	private void handleOptionArgsEarly(CommandLineArguments args) {
		if (args == null) {
			return;
		}
		if (args.containsArg("regressionFile")) {
			this.regressionFileName = args.getStringValue("regressionFile");
		}
	}

	/**
	 * This function helps determine if a ggt file was loaded because if a ggt
	 * file was loaded we will need to load something instead of the ggb
	 * 
	 * @return true if file is loading and is a ggt file
	 */
	private boolean isLoadingTool(CommandLineArguments args) {
		if ((args == null) || (args.getNoOfFiles() == 0)) {
			return false;
		}
		String fileArgument = args.getStringValue("file0");
		String lowerCase = toLowerCase(fileArgument);
		return lowerCase.endsWith(FILE_EXT_GEOGEBRA_TOOL);
	}

	/**
	 * Opens a file specified as last command line argument
	 * 
	 * @return true if a file was loaded successfully
	 */
	private boolean handleFileArg(CommandLineArguments args) {
		if ((args == null) || (args.getNoOfFiles() == 0)) {
			return false;
		}

		boolean successRet = true;

		for (int i = 0; i < args.getNoOfFiles(); i++) {

			final String fileArgument = args.getStringValue("file" + i);

			if (i > 0) { // load in new Window
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						String[] argsNew = { fileArgument };
						GeoGebraFrame.createNewWindow(new CommandLineArguments(
								argsNew));
					}
				});
			} else {

				try {
					boolean success;
					String lowerCase = toLowerCase(fileArgument);
					boolean isMacroFile = lowerCase
							.endsWith(FILE_EXT_GEOGEBRA_TOOL);

					if (lowerCase.startsWith("http:")
							|| lowerCase.startsWith("file:")) {
						// replace all whitespace characters by %20 in URL
						// string
						String fileArgument2 = fileArgument.replaceAll("\\s",
								"%20");
						URL url = new URL(fileArgument2);
						success = loadXML(url, isMacroFile);

						// check if full GUI is necessary
						if (success && !isMacroFile) {
							if (!isUsingFullGui()) {
								if (showConsProtNavigation
										|| !isJustEuclidianVisible()) {
									useFullGui = true;
								}
							}
						}
					} else if (lowerCase.startsWith("base64://")) {

						// substring to strip off base64://
						byte[] zipFile = geogebra.common.util.Base64
								.decode(fileArgument.substring(9));
						success = loadXML(zipFile);

						if (success && !isMacroFile) {
							if (!isUsingFullGui()) {
								if (!isJustEuclidianVisible()) {
									useFullGui = true;
								}
							}
						}
					} else {
						File f = new File(fileArgument);
						f = f.getCanonicalFile();
						success = loadFile(f, isMacroFile);

					}

					successRet = successRet && success;
				} catch (Exception e) {
					e.printStackTrace();
					successRet = false;
				}
			}
		}

		return successRet;
	}

	
	public void setApplet(AppletImplementation appletImpl) {
		isApplet = true;
		Application.appletImpl = appletImpl;
		mainComp = appletImpl.getJApplet();
	}

	public AppletImplementation getApplet() {
		return appletImpl;
	}

	

	public void reset() {
		if (appletImpl != null) {
			appletImpl.reset();
		} else if (currentFile != null) {
			getGuiManager().loadFile(currentFile, false);
		} else {
			clearConstruction();
		}
	}

	public void setFrame(JFrame frame) {
		isApplet = false;
		mainComp = frame;

		this.frame = frame;
		updateTitle();

		// Windows 7 uses this for the Toolbar icon too
		// (needs to be larger)
		frame.setIconImage(getInternalImage("geogebra64.png"));

		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		WindowListener[] wl = frame.getWindowListeners();
		if ((wl == null) || (wl.length == 0)) {
			// window closing listener
			WindowAdapter windowListener = new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent event) {
					exit();
				}
			};
			frame.addWindowListener(windowListener);
		}
	}

	@Override
	final public boolean isApplet() {
		return isApplet;
	}

	public boolean isStandaloneApplication() {
		return !isApplet && (mainComp instanceof JFrame);
	}

	public synchronized JFrame getFrame() {
		if ((frame == null) && (getGuiManager() != null)) {
			frame = getGuiManager().createFrame();
		}

		return frame;
	}

	public Component getMainComponent() {
		return mainComp;
	}

	public Dimension getPreferredSize() {
		return preferredSize;
	}

	@Override
	public void setPreferredSize(geogebra.common.awt.Dimension size) {
		preferredSize = geogebra.awt.Dimension.getAWTDimension(size);
	}

	/**
	 * Save all perspectives included in a document into an array with temporary
	 * perspectives.
	 * 
	 * @param perspectives
	 */
	@Override
	public void setTmpPerspectives(ArrayList<Perspective> perspectives) {
		tmpPerspectives = perspectives;
	}

	public ArrayList<Perspective> getTmpPerspectives() {
		return tmpPerspectives;
	}

	/**
	 * Check if just the euclidian view is visible in the document just loaded.
	 * 
	 * @return
	 * @throws OperationNotSupportedException
	 */
	private boolean isJustEuclidianVisible()
			throws OperationNotSupportedException {
		if (tmpPerspectives == null) {
			throw new OperationNotSupportedException();
		}

		Perspective docPerspective = null;

		for (Perspective perspective : tmpPerspectives) {
			if (perspective.getId().equals("tmp")) {
				docPerspective = perspective;
			}
		}

		if (docPerspective == null) {
			throw new OperationNotSupportedException();
		}

		boolean justEuclidianVisible = false;

		for (DockPanelData panel : docPerspective.getDockPanelData()) {
			if ((panel.getViewId() == AbstractApplication.VIEW_EUCLIDIAN)
					&& panel.isVisible()) {
				justEuclidianVisible = true;
			} else if (panel.isVisible()) {
				justEuclidianVisible = false;
				break;
			}
		}

		return justEuclidianVisible;
	}

	@Override
	public EuclidianView getEuclidianView1() {
		return (EuclidianView)euclidianView;
	}

	@Override
	public AlgebraView getAlgebraView() {
		if (guiManager == null) {
			return null;
		}
		return guiManager.getAlgebraView();
	}

	@Override
	public EuclidianView getEuclidianView2() {
		return getGuiManager().getEuclidianView2();
	}

	@Override
	public boolean hasEuclidianView2() {
		return (guiManager != null) && getGuiManager().hasEuclidianView2();
	}

	public boolean hasEuclidianView2EitherShowingOrNot() {
		return (guiManager != null)
				&& getGuiManager().hasEuclidianView2EitherShowingOrNot();
	}

	@Override
	public boolean isShowingEuclidianView2() {
		return (guiManager != null) && getGuiManager().hasEuclidianView2()
				&& getGuiManager().getEuclidianView2().isShowing();
	}

	

	@Override
	public EuclidianViewND getActiveEuclidianView() {
		if (getGuiManager() == null) {
			return getEuclidianView1();
		}
		return getGuiManager().getActiveEuclidianView();
	}

	//TODO: maybe we want to implement this for EV2 as well
	public BufferedImage getExportImage(double maxX, double maxY)
			throws OutOfMemoryError {

		double scale = Math.min(maxX / getEuclidianView1().getSelectedWidth(),
				maxY / getEuclidianView1().getSelectedHeight());

		return getEuclidianView1().getExportImage(scale);
	}

	public void setShowAxesSelected(JCheckBoxMenuItem cb) {
		cb.setSelected(((AbstractEuclidianView) getGuiManager()
				.getActiveEuclidianView()).getShowXaxis()
				&& ((AbstractEuclidianView) getGuiManager().getActiveEuclidianView())
						.getShowYaxis());
	}

	public void setShowGridSelected(JCheckBoxMenuItem cb) {
		cb.setSelected(((AbstractEuclidianView) getGuiManager()
				.getActiveEuclidianView()).getShowGrid());
	}



	
	

	/**
	 * Sets the maximum pixel size (width and height) of all icons in the user
	 * interface. Larger icons are scaled down.
	 * 
	 * @param pixel
	 *            max icon size between 16 and 32 pixels
	 */
	public void setMaxIconSize(int pixel) {
		maxIconSize = Math.min(32, Math.max(16, pixel));
	}

	public int getMaxIconSize() {
		return maxIconSize;
	}

	public ImageIcon getImageIcon(String filename) {
		return getImageIcon(filename, null);
	}

	public ImageIcon getImageIcon(String filename, Color borderColor) {
		return imageManager
				.getImageIcon("/gui/images/" + filename, borderColor);
	}

	public ImageIcon getToolBarImage(String filename, Color borderColor) {
		String path = "/gui/toolbar/images/" + filename;
		ImageIcon icon = imageManager.getImageIcon(path, borderColor);

		/*
		 * mathieu 2010-04-10 see ImageManager3D.getImageResourceGeoGebra() if
		 * (icon == null) { // load3DJar(); // try to find this image in 3D
		 * extension path = "/geogebra/geogebra3D/images/" + filename; icon =
		 * imageManager.getImageIcon(path, borderColor); }
		 */

		if (icon == null) {
			icon = getToolIcon(borderColor);
		}

		// scale icon if necessary
		icon = ImageManager.getScaledIcon(icon,
				Math.min(icon.getIconWidth(), maxIconSize),
				Math.min(icon.getIconHeight(), maxIconSize));

		return icon;
	}

	public ImageIcon getToolIcon(Color border) {
		return imageManager.getImageIcon(
				"/gui/toolbar/images/mode_tool_32.png", border);
	}

	public ImageIcon getEmptyIcon() {
		return imageManager.getImageIcon("/gui/images/empty.gif");
	}

	public Image getInternalImage(String filename) {
		return imageManager.getInternalImage("/gui/images/" + filename);
	}

	public Image getRefreshViewImage() {
		// don't need to load gui jar as reset image is in main jar
		return imageManager.getInternalImage("/main/view-refresh.png");
	}

	public Image getPlayImage() {
		// don't need to load gui jar as reset image is in main jar
		return imageManager.getInternalImage("/main/nav_play.png");
	}

	public Image getPauseImage() {
		// don't need to load gui jar as reset image is in main jar
		return imageManager.getInternalImage("/main/nav_pause.png");
	}

	public BufferedImage getExternalImage(String filename) {
		return ImageManager.getExternalImage(filename);
	}

	@Override
	public geogebra.common.awt.BufferedImage getExternalImageAdapter(String filename) {
		return new geogebra.awt.BufferedImage(
				ImageManager.getExternalImage(filename));
	}

	public void addExternalImage(String filename, BufferedImage image) {
		imageManager.addExternalImage(filename, image);
	}

	// public void startEditing(GeoElement geo) {
	// if (showAlgebraView)
	// getApplicationGUImanager().startEditingAlgebraView(geo);
	// }

	public final void zoom(double px, double py, double zoomFactor) {
		((AbstractEuclidianView) getGuiManager().getActiveEuclidianView()).zoom(px, py,
				zoomFactor, 15, true);
	}

	/**
	 * Sets the ratio between the scales of y-axis and x-axis, i.e. ratio =
	 * yscale / xscale;
	 */
	public final void zoomAxesRatio(double axesratio) {
		((AbstractEuclidianView) getGuiManager().getActiveEuclidianView())
				.zoomAxesRatio(axesratio, true);
	}

	public final void setStandardView() {
		((EuclidianView) getGuiManager().getActiveEuclidianView())
				.setStandardView(true);
	}

	public final void setViewShowAllObjects() {
		((EuclidianView) getGuiManager().getActiveEuclidianView())
				.setViewShowAllObjects(true);
	}

	/***************************************************************************
	 * LOCALE part
	 **************************************************************************/

	/**
	 * Creates a Locale object according to the given language code. The
	 * languageCode string should consist of two letters for the language, two
	 * letters for the country and two letters for the variant. E.g. "en" ...
	 * language: English , no country specified, "deAT" or "de_AT" ... language:
	 * German , country: Austria, "noNONY" or "no_NO_NY" ... language: Norwegian
	 * , country: Norway, variant: Nynorsk
	 */
	public static Locale getLocale(String languageCode) {
		// remove "_" from string
		languageCode = languageCode.replaceAll("_", "");

		Locale loc;
		if (languageCode.length() == 6) {
			// language, country, variant
			loc = new Locale(languageCode.substring(0, 2),
					languageCode.substring(2, 4), languageCode.substring(4, 6));
		} else if (languageCode.length() == 4) {
			// language, country
			loc = new Locale(languageCode.substring(0, 2),
					languageCode.substring(2, 4));
		} else {
			// language only
			loc = new Locale(languageCode.substring(0, 2));
		}
		return loc;
	}

	/*
	 * used to force properties to be read from secondary (tooltip) language if
	 * one has been selected
	 */
	@Override
	public void setTooltipFlag() {
		if (tooltipLocale != null) {
			tooltipFlag = true;
		}
	}

	/*
	 * sets secondary language
	 */
	@Override
	public void setTooltipLanguage(String s) {

		Locale locale = null;

		for (int i = 0; i < supportedLocales.size(); i++) {
			if (supportedLocales.get(i).toString().equals(s)) {
				locale = supportedLocales.get(i);
				break;
			}
		}

		boolean updateNeeded = (rbplainTT != null) || (rbmenuTT != null);

		rbplainTT = null;
		rbmenuTT = null;

		if (locale == null) {
			tooltipLocale = null;
		} else if (currentLocale.toString().equals(locale.toString())) {
			tooltipLocale = null;
		} else {
			tooltipLocale = locale;
		}

		updateNeeded = updateNeeded || (tooltipLocale != null);

		if (updateNeeded) {
			setLabels(); // update eg Tooltips for Toolbar
		}

	}

	public Locale getTooltipLanguage() {
		return tooltipLocale;
	}
	
	public String getTooltipLanguageString() {
		if(tooltipLocale==null)
			return null;
		return tooltipLocale.toString();
	}

	@Override
	public int getTooltipTimeout() {
		int dmd = ToolTipManager.sharedInstance().getDismissDelay();
		if ((dmd <= 0) || (dmd == Integer.MAX_VALUE)) {
			return -1;
		}
		dmd /= 1000;
		for (int i = 0; i < (MyXMLHandler.tooltipTimeouts.length - 1); i++) {
			if (Integer.parseInt(MyXMLHandler.tooltipTimeouts[i]) >= dmd) {
				return Integer.parseInt(MyXMLHandler.tooltipTimeouts[i]);
			}
		}
		return Integer
				.parseInt(MyXMLHandler.tooltipTimeouts[MyXMLHandler.tooltipTimeouts.length - 2]);
	}

	/**
	 * set language via iso language string
	 */
	public void setLanguage(Locale locale) {

		if ((locale == null)
				|| currentLocale.toString().equals(locale.toString())) {
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

		setLabels(); // update display

		System.gc();
	}

	

	/*
	 * removed Michael Borcherds 2008-03-31 private boolean reverseLanguage =
	 * false; //FKH 20040822 final public boolean isReverseLanguage() { //FKH
	 * 20041010 // for Chinese return reverseLanguage; }
	 */

	

	


	
	// public static char unicodeThousandsSeparator = ','; // \u066c for Arabic

	
	
	StringBuilder testCharacters = new StringBuilder();

	public void setLocale(Locale locale) {
		if (locale == currentLocale) {
			return;
		}
		Locale oldLocale = currentLocale;

		// only allow special locales due to some weird server
		// problems with the naming of the property files
		currentLocale = getClosestSupportedLocale(locale);
		updateResourceBundles();

		// update font for new language (needed for e.g. chinese)
		try {
			fontManager.setLanguage(currentLocale);
		} catch (Exception e) {
			e.printStackTrace();
			showError(e.getMessage());

			// go back to previous locale
			currentLocale = oldLocale;
			updateResourceBundles();
		}

		updateReverseLanguage(locale.getLanguage());

	}

	/**
	 * Returns a locale object that has the same country and/or language as
	 * locale. If the language of locale is not supported an English locale is
	 * returned.
	 */
	private static Locale getClosestSupportedLocale(Locale locale) {
		int size = supportedLocales.size();

		// try to find country and variant
		String country = locale.getCountry();
		String variant = locale.getVariant();

		if (country.length() > 0) {
			for (int i = 0; i < size; i++) {
				Locale loc = supportedLocales.get(i);
				if (country.equals(loc.getCountry())
						&& variant.equals(loc.getVariant())) {
					// found supported country locale
					return loc;
				}
			}
		}

		// try to find language
		String language = locale.getLanguage();
		for (int i = 0; i < size; i++) {
			Locale loc = supportedLocales.get(i);
			if (language.equals(loc.getLanguage())) {
				// found supported country locale
				return loc;
			}
		}

		// we didn't find a matching country or language,
		// so we take English
		return Locale.ENGLISH;
	}

	private void updateResourceBundles() {
		if (rbmenu != null) {
			rbmenu = MyResourceBundle.createBundle(RB_MENU, currentLocale);
		}
		if (rberror != null) {
			rberror = MyResourceBundle.createBundle(RB_ERROR, currentLocale);
		}
		if (rbplain != null) {
			rbplain = MyResourceBundle.createBundle(RB_PLAIN, currentLocale);
		}
		if (rbcommand != null) {
			rbcommand = MyResourceBundle
					.createBundle(RB_COMMAND, currentLocale);
		}
		if (rbcolors != null) {
			rbcolors = MyResourceBundle.createBundle(RB_COLORS, currentLocale);
		}
		if (rbsymbol != null) {
			rbsymbol = MyResourceBundle.createBundle(RB_SYMBOL, currentLocale);
		}
	}

	/*
	 * private void updateSecondaryResourceBundles() { //if (rbmenuSecondary !=
	 * null) // rbmenuSecondary = MyResourceBundle.createBundle(RB_MENU,
	 * currentLocale); //if (rberrorSecondary != null) // rberrorSecondary =
	 * MyResourceBundle.createBundle(RB_ERROR, currentLocale); //if
	 * (rbplainSecondary != null) // rbplainSecondary =
	 * MyResourceBundle.createBundle(RB_PLAIN, currentLocale); if
	 * (rbcommandSecondary != null) rbcommandSecondary =
	 * MyResourceBundle.createBundle(RB_COMMAND, secondaryLocale); //if
	 * (rbcolorsSecondary != null) // rbcolorsSecondary =
	 * MyResourceBundle.createBundle(RB_COLORS, currentLocale); } //
	 */

	public final static String syntaxCAS = ".SyntaxCAS";
	public final static String syntax3D = ".Syntax3D";
	public final static String syntaxStr = ".Syntax";

	
	
	

	


	public Locale getLocale() {
		return currentLocale;
	}


	/*
	 * properties methods
	 */

	@Override
	final public String getColor(String key) {

		if (key == null) {
			return "";
		}

		if ((key.length() == 5)
				&& toLowerCase(key).startsWith("gray")) {
			switch (key.charAt(4)) {
			case '0':
				return getColor("white");
			case '1':
				return getPlain("AGray", Unicode.fraction1_8);
			case '2':
				return getPlain("AGray", Unicode.fraction1_4); // silver
			case '3':
				return getPlain("AGray", Unicode.fraction3_8);
			case '4':
				return getPlain("AGray", Unicode.fraction1_2);
			case '5':
				return getPlain("AGray", Unicode.fraction5_8);
			case '6':
				return getPlain("AGray", Unicode.fraction3_4);
			case '7':
				return getPlain("AGray", Unicode.fraction7_8);
			default:
				return getColor("black");
			}
		}

		if (rbcolors == null) {
			initColorsResourceBundle();
		}

		try {
			return rbcolors.getString(toLowerCase(key));
		} catch (Exception e) {
			return key;
		}
	}

	@Override
	final public String reverseGetColor(String str) {
		str = StringUtil.removeSpaces(toLowerCase(str));
		if (rbcolors == null) {
			initColorsResourceBundle();
		}

		try {

			Enumeration<String> enumer = rbcolors.getKeys();
			while (enumer.hasMoreElements()) {
				String key = enumer.nextElement();
				if (str.equals(StringUtil.removeSpaces(toLowerCase(rbcolors.getString(key))
						))) {
					return key;
				}
			}

			return str;
		} catch (Exception e) {
			return str;
		}
	}

	@Override
	final public String getPlain(String key) {

		if (tooltipFlag) {
			return getPlainTooltip(key);
		}

		if (rbplain == null) {
			initPlainResourceBundle();
		}

		try {
			return rbplain.getString(key);
		} catch (Exception e) {
			return key;
		}
	}

	final public String getPlainTooltip(String key) {

		if (tooltipLocale == null) {
			return getPlain(key);
		}

		if (rbplainTT == null) {
			initPlainTTResourceBundle();
		}

		try {
			return rbplainTT.getString(key);
		} catch (Exception e) {
			return key;
		}
	}

	final public String getSymbol(int key) {
		if (rbsymbol == null) {
			initSymbolResourceBundle();
		}

		String ret = null;

		try {
			ret = rbsymbol.getString("S." + key);
		} catch (Exception e) {
		}

		if ("".equals(ret)) {
			return null;
		} else {
			return ret;
		}
	}

	final public String getSymbolTooltip(int key) {
		if (rbsymbol == null) {
			initSymbolResourceBundle();
		}

		String ret = null;

		try {
			ret = rbsymbol.getString("T." + key);
		} catch (Exception e) {
		}

		if ("".equals(ret)) {
			return null;
		} else {
			return ret;
		}
	}

	// final public String reverseGetPlain(String str) {
	// if (rbplain == null) {
	// initPlainResourceBundle();
	// }
	//
	// str = str.toLowerCase();
	//
	// try {
	// Enumeration enumer = rbplain.getKeys();
	//
	// while (enumer.hasMoreElements()) {
	// String key = (String)enumer.nextElement();
	// if (rbplain.getString(key).toLowerCase().equals(str))
	// return key;
	// }
	//
	// return str;
	// } catch (Exception e) {
	// return str;
	// }
	// }

	private void initPlainResourceBundle() {
		rbplain = MyResourceBundle.createBundle(RB_PLAIN, currentLocale);
		if (rbplain != null) {
			kernel.updateLocalAxesNames();
		}
	}

	private void initPlainTTResourceBundle() {
		rbplainTT = MyResourceBundle.createBundle(RB_PLAIN, tooltipLocale);
	}

	private void initSymbolResourceBundle() {
		rbsymbol = MyResourceBundle.createBundle(RB_SYMBOL, currentLocale);
	}

	private void initColorsResourceBundle() {
		rbcolors = MyResourceBundle.createBundle(RB_COLORS, currentLocale);
	}

	private boolean showConstProtNavigationNeedsUpdate = false;

	@Override
	final public String getMenu(String key) {

		if (tooltipFlag) {
			return getMenuTooltip(key);
		}

		if (rbmenu == null) {
			rbmenu = MyResourceBundle.createBundle(RB_MENU, currentLocale);
		}

		try {
			return rbmenu.getString(key);
		} catch (Exception e) {
			return key;
		}
	}

	final public String getMenuTooltip(String key) {

		if (tooltipLocale == null) {
			return getMenu(key);
		}

		if (rbmenuTT == null) {
			rbmenuTT = MyResourceBundle.createBundle(RB_MENU, tooltipLocale);
		}

		try {
			return rbmenuTT.getString(key);
		} catch (Exception e) {
			return key;
		}
	}

	@Override
	final public String getError(String key) {
		if (rberror == null) {
			rberror = MyResourceBundle.createBundle(RB_ERROR, currentLocale);
		}

		try {
			return rberror.getString(key);
		} catch (Exception e) {
			return key;
		}
	}


	@Override
	public void initCommand() {
		if (rbcommand == null) {
			rbcommand = MyResourceBundle
					.createBundle(RB_COMMAND, currentLocale);
		}
		
	}

	@Override
	final public String getInternalCommand(String cmd) {
		initTranslatedCommands();
		Enumeration<String> enume;
		String s;
		enume = rbcommand.getKeys();
		while (enume.hasMoreElements()) {
			s = enume.nextElement();
			// check isn't .Syntax, .SyntaxCAS, .Syntax3D
			if (s.indexOf(syntaxStr) == -1) {
				// make sure that when si[] is typed in script, it's changed to
				// Si[] etc
				if (getCommand(s).toLowerCase().equals(cmd.toLowerCase())) {
					return s;
				}
			}
		}
		return null;
	}

	final public String getReverseCommand(String key) {
		initTranslatedCommands();

		key = toLowerCase(key);
		try {

			Enumeration<String> enume = rbcommand.getKeys();

			while (enume.hasMoreElements()) {
				String s = enume.nextElement();

				// check internal commands
				if (toLowerCase(s).equals(key)) {
					return s;
				}

				// check localized commands
				if (toLowerCase(rbcommand.getString(s)).equals(key)) {
					return s;
				}
			}

			return null;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	final public String getCommand(String key) {

		if (tooltipFlag) {
			return getCommandTooltip(key);
		}

		initTranslatedCommands();

		try {
			return rbcommand.getString(key);
		} catch (Exception e) {
			return key;
		}
	}

	final public String getCommandTooltip(String key) {

		if (tooltipLocale == null) {
			return getCommand(key);
		}
		if (rbcommandTT == null) {
			rbcommandTT = MyResourceBundle.createBundle(RB_COMMAND,
					tooltipLocale);
		}

		try {
			return rbcommandTT.getString(key);
		} catch (Exception e) {
			return key;
		}
	}

	final public String getEnglishCommand(String key) {

		if (rbcommandEnglish == null) {
			rbcommandEnglish = MyResourceBundle.createBundle(RB_COMMAND,
					Locale.ENGLISH);
		}

		try {
			return rbcommandEnglish.getString(key);
		} catch (Exception e) {
			return key;
		}
	}

	final public String getEnglishMenu(String key) {

		if (rbmenuEnglish == null) {
			rbmenuEnglish = MyResourceBundle.createBundle(RB_MENU,
					Locale.ENGLISH);
		}
		try {
			return rbmenuEnglish.getString(key);
		} catch (Exception e) {
			return key;
		}
	}

	@Override
	public String getCommandSyntax(String key) {

		String command = getCommand(key);
		String syntax = getCommand(key + syntaxStr);

		syntax = syntax.replace("[", command + '[');

		return syntax;
	}

	public String getCommandSyntaxCAS(String key) {

		String command = getCommand(key);
		String syntax = getCommand(key + syntaxCAS);

		syntax = syntax.replace("[", command + '[');

		return syntax;
	}

	final public String getSetting(String key) {
		if (rbsettings == null) {
			rbsettings = MyResourceBundle.loadSingleBundleFile(RB_SETTINGS);
		}

		try {
			return rbsettings.getString(key);
		} catch (Exception e) {
			return null;
		}
	}

	public boolean propertiesFilesPresent() {
		return rbplain != null;
	}

	
	@Override
	public void showRelation(GeoElement a, GeoElement b) {
		JOptionPane.showConfirmDialog(mainComp,
				new Relation(kernel).relation(a, b),
				getPlain("ApplicationName") + " - " + getCommand("Relation"),
				JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);

	}

	public void showHelp(String key) {
		final String text = getPlain(key); // Michael Borcherds changed to use
		// getPlain() and removed try/catch

		JOptionPane.showConfirmDialog(mainComp, text,
				getPlain("ApplicationName") + " - " + getMenu("Help"),
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
	}

	@Override
	public void showError(String key) {
		showErrorDialog(getError(key));
	}

	@Override
	public void showError(String key, String error) {
		showErrorDialog(getError(key) + ":\n" + error);
	}

	@Override
	public void showError(MyError e) {
		String command = e.getcommandName();

		// make sure splash screen not showing (will be in front)
		if (GeoGebra.splashFrame != null) {
			GeoGebra.splashFrame.setVisible(false);
		}

		if (command == null) {
			showErrorDialog(e.getLocalizedMessage());
			return;
		}

		Object[] options = { getPlain("OK"), getPlain("ShowOnlineHelp") };
		int n = JOptionPane.showOptionDialog(mainComp, e.getLocalizedMessage(),
				getPlain("ApplicationName") + " - " + getError("Error"),
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, // do
																				// not
																				// use
																				// a
																				// custom
																				// Icon
				options, // the titles of buttons
				options[0]); // default button title

		if (n == 1) {
			getGuiManager().openCommandHelp(command);
		}

	}

	public void showErrorDialog(final String msg) {
		if (!isErrorDialogsActive) {
			return;
		}

		// make sure splash screen not showing (will be in front)
		if (GeoGebra.splashFrame != null) {
			GeoGebra.splashFrame.setVisible(false);
		}

		Application.printStacktrace("showErrorDialog: " + msg);
		isErrorDialogShowing = true;

		// use SwingUtilities to make sure this gets executed in the correct
		// (=GUI) thread.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// TODO investigate why this freezes Firefox sometimes
				JOptionPane
						.showConfirmDialog(mainComp, msg,
								getPlain("ApplicationName") + " - "
										+ getError("Error"),
								JOptionPane.DEFAULT_OPTION,
								JOptionPane.WARNING_MESSAGE);
				isErrorDialogShowing = false;
			}
		});
	}

	public boolean isErrorDialogShowing() {
		return isErrorDialogShowing;
	}

	public void showMessage(final String message) {
		Application.printStacktrace("showMessage: " + message);

		// use SwingUtilities to make sure this gets executed in the correct
		// (=GUI) thread.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JOptionPane.showConfirmDialog(mainComp, message,
						getPlain("ApplicationName") + " - " + getMenu("Info"),
						JOptionPane.DEFAULT_OPTION,
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
	}

	/**
	 * Downloads a bitmap from the URL and stores it in this application's
	 * imageManager. Michael Borcherds
	 * 
	 * public String getImageFromURL(String url) { try{
	 * 
	 * BufferedImage img=javax.imageio.ImageIO.read(new URL(url)); return
	 * createImage(img, "bitmap.png"); } catch (Exception e) {return null;} }
	 */

	@Override
	public void setWaitCursor() {
		Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
		mainComp.setCursor(waitCursor);

		if (euclidianView != null) {
			((EuclidianView)euclidianView).setCursor(waitCursor);
		}

		if (guiManager != null) {
			guiManager.allowGUIToRefresh();
		}
	}

	@Override
	public void setDefaultCursor() {
		mainComp.setCursor(Cursor.getDefaultCursor());
		if (euclidianView != null) {
			((EuclidianView)euclidianView).setCursor(Cursor.getDefaultCursor());
		}
		if ((guiManager != null) && guiManager.hasEuclidianView2()) {
			guiManager.getEuclidianView2().setCursor(Cursor.getDefaultCursor());
		}

	}

	/*
	 * private methods for display
	 */

	public File getCurrentFile() {
		return currentFile;
	}

	public File getCurrentPath() {
		return currentPath;
	}

	public void setCurrentPath(File file) {
		currentPath = file;
	}

	public void setCurrentFile(File file) {
		if (currentFile == file) {
			return;
		}

		currentFile = file;
		if (currentFile != null) {
			currentPath = currentFile.getParentFile();
			addToFileList(currentFile);
		}

		if (!isIniting() && isUsingFullGui()) {
			updateTitle();
			getGuiManager().updateMenuWindow();
		}
	}

	public static void addToFileList(File file) {
		if ((file == null) || !file.exists()) {
			return;
		}

		// add or move fileName to front of list
		fileList.remove(file);
		fileList.addFirst(file);
	}

	public static File getFromFileList(int i) {
		if (fileList.size() > i) {
			return fileList.get(i);
		} else {
			return null;
		}
	}

	public static int getFileListSize() {
		return fileList.size();
	}

	public void updateTitle() {
		if (frame == null) {
			return;
		}

		getGuiManager().updateFrameTitle();
	}

	
	

	

	public void updateUI() {
		if (!initing) {
			if (appletImpl != null) {
				SwingUtilities.updateComponentTreeUI(appletImpl.getJApplet());
			}
			if (frame != null) {
				SwingUtilities.updateComponentTreeUI(frame);
			}
		}
		
	}

	public void resetFonts() {
		getFontManager().setFontSize(getGUIFontSize());
		updateFonts();
	}

	

	

	
	private void setLabels() {
		if (initing) {
			return;
		}

		if (guiManager != null) {
			getGuiManager().setLabels();
		}

		if (rbplain != null) {
			kernel.updateLocalAxesNames();
		}

		updateCommandDictionary();
	}

	/**
	 * Returns name of given tool.
	 * 
	 * @param mode
	 *            number
	 */
	public String getToolName(int mode) {
		return getToolNameOrHelp(mode, true);
	}

	/**
	 * Returns the tool help text for the given tool.
	 * 
	 * @param mode
	 *            number
	 */
	public String getToolHelp(int mode) {
		return getToolNameOrHelp(mode, false);
	}

	/**
	 * Returns the tool name and tool help text for the given tool as an HTML
	 * text that is useful for tooltips.
	 * 
	 * @param mode
	 *            : tool ID
	 */
	public String getToolTooltipHTML(int mode) {

		if (tooltipLocale != null) {
			tooltipFlag = true;
		}

		StringBuilder sbTooltip = new StringBuilder();
		sbTooltip.append("<html><b>");
		sbTooltip.append(StringUtil.toHTMLString(getToolName(mode)));
		sbTooltip.append("</b><br>");
		sbTooltip.append(StringUtil.toHTMLString(getToolHelp(mode)));
		sbTooltip.append("</html>");

		tooltipFlag = false;

		return sbTooltip.toString();

	}

	private String getToolNameOrHelp(int mode, boolean toolName) {
		// macro
		String ret;

		if (mode >= EuclidianConstants.MACRO_MODE_ID_OFFSET) {
			// MACRO
			int macroID = mode - EuclidianConstants.MACRO_MODE_ID_OFFSET;
			try {
				Macro macro = (Macro) kernel.getMacro(macroID);
				if (toolName) {
					// TOOL NAME
					ret = macro.getToolName();
					if ("".equals(ret)) {
						ret = macro.getCommandName();
					}
				} else {
					// TOOL HELP
					ret = macro.getToolHelp();
					if ("".equals(ret)) {
						ret = macro.getNeededTypesString();
					}
				}
			} catch (Exception e) {
				AbstractApplication
						.debug("Application.getModeText(): macro does not exist: ID = "
								+ macroID);
				// e.printStackTrace();
				return "";
			}
		} else {
			// STANDARD TOOL
			String modeText = getKernel().getModeText(mode);
			if (toolName) {
				// tool name
				ret = getMenu(modeText);
			} else {
				// tool help
				ret = getMenu(modeText + ".Help");
			}
		}

		return ret;
	}

	public ImageIcon getModeIcon(int mode) {
		ImageIcon icon;

		Color border = Color.lightGray;

		// macro
		if (mode >= EuclidianConstants.MACRO_MODE_ID_OFFSET) {
			int macroID = mode - EuclidianConstants.MACRO_MODE_ID_OFFSET;
			try {
				Macro macro = (Macro) kernel.getMacro(macroID);
				String iconName = macro.getIconFileName();
				BufferedImage img = getExternalImage(iconName);
				if (img == null) {
					// default icon
					icon = getToolBarImage("mode_tool_32.png", border);
				} else {
					// use image as icon
					icon = new ImageIcon(ImageManager.addBorder(img, border));
				}
			} catch (Exception e) {
				AbstractApplication.debug("macro does not exist: ID = "
						+ macroID);
				return null;
			}
		} else {
			// standard case
			String modeText = getKernel().getModeText(mode);
			// bugfix for Turkish locale added Locale.US
			String iconName = "mode_" + toLowerCase(modeText)
					+ "_32.gif";
			icon = getToolBarImage(iconName, border);
			if (icon == null) {
				AbstractApplication.debug("icon missing for mode " + modeText
						+ " (" + mode + ")");
			}
		}
		return icon;
	}

	public boolean onlyGraphicsViewShowing() {
		if (!isUsingFullGui()) {
			return true;
		}

		return getGuiManager().getLayout().isOnlyVisible(
				AbstractApplication.VIEW_EUCLIDIAN);
	}

	@Override
	public boolean showAlgebraInput() {
		return showAlgebraInput;
	}

	public void setShowAlgebraInput(boolean flag, boolean update) {
		showAlgebraInput = flag;

		if (update) {
			updateTopBottomPanels();
			updateMenubar();
		}
	}

	public boolean showInputTop() {
		return showInputTop;
	}

	public void setShowInputTop(boolean flag, boolean update) {
		if (flag == showInputTop) {
			return;
		}

		showInputTop = flag;

		if (update && !isIniting()) {
			updateTopBottomPanels();
		}
	}

	public boolean showInputHelpToggle() {
		return showInputHelpToggle;
	}

	public boolean showToolBarTop() {
		return showToolBarTop;
	}

	public void setShowToolBarTop(boolean flag) {
		if (flag == showToolBarTop) {
			return;
		}

		showToolBarTop = flag;

		if (!isIniting()) {
			updateTopBottomPanels();
		}
	}

	public boolean getShowCPNavNeedsUpdate() {
		return showConstProtNavigationNeedsUpdate;
	}

	/**
	 * Displays the construction protocol navigation
	 */
	@Override
	public void setShowConstructionProtocolNavigation(boolean flag) {
		if ((flag == showConsProtNavigation)
				&& (showConstProtNavigationNeedsUpdate == false)) {
			return;
		}
		showConsProtNavigation = flag;

		if (getGuiManager() != null) {
			getGuiManager().setShowConstructionProtocolNavigation(flag);
			updateMenubar();
			showConstProtNavigationNeedsUpdate = false;
		} else {
			showConstProtNavigationNeedsUpdate = true;
		}
	}

	public boolean showConsProtNavigation() {
		return showConsProtNavigation;
	}

	public boolean showAuxiliaryObjects() {
		return showAuxiliaryObjects;
	}

	@Override
	public void setShowAuxiliaryObjects(boolean flag) {
		showAuxiliaryObjects = flag;

		if (getGuiManager() != null) {
			getGuiManager().setShowAuxiliaryObjects(flag);
			updateMenubar();
		}
	}

	public void setShowMenuBar(boolean flag) {
		showMenuBar = flag;
	}

	public void setShowToolBar(boolean toolbar) {
		showToolBar = toolbar;

		if (!isIniting()) {
			updateTopBottomPanels();
			updateMenubar();
		}
	}

	public void setShowToolBarNoUpdate(boolean toolbar) {
		showToolBar = toolbar;
	}

	public void setShowToolBar(boolean toolbar, boolean help) {
		showToolBar = toolbar;

		if (showToolBar) {
			getGuiManager().setShowToolBarHelp(help);
		}
	}

	public boolean showToolBar() {
		return showToolBar;
	}

	public boolean showMenuBar() {
		return showMenuBar;
	}

	/**
	 * Enables or disables right clicking in this application. This is useful
	 * for applets.
	 */
	public void setRightClickEnabled(boolean flag) {
		rightClickEnabled = flag;
	}

	/**
	 * Enables or disables popups when multiple objects selected This is useful
	 * for applets.
	 */
	public void setChooserPopupsEnabled(boolean flag) {
		chooserPopupsEnabled = flag;
	}

	final public boolean isRightClickEnabled() {
		return rightClickEnabled;
	}

	final public boolean areChooserPopupsEnabled() {
		return chooserPopupsEnabled;
	}

	
	public boolean letShowPopupMenu() {
		return rightClickEnabled;
	}

	public boolean letShowPropertiesDialog() {
		return rightClickEnabled;
	}

	public void updateToolBar() {
		if (!showToolBar || isIniting()) {
			return;
		}

		getGuiManager().updateToolbar();

		if (!initing) {
			if (appletImpl != null) {
				SwingUtilities.updateComponentTreeUI(appletImpl.getJApplet());
			}
			if (frame != null) {
				SwingUtilities.updateComponentTreeUI(frame);
			}
		}

		setMoveMode();
	}

	public void updateMenubar() {
		if (!showMenuBar || !isUsingFullGui() || isIniting()) {
			return;
		}

		getGuiManager().updateMenubar();
		getGuiManager().updateActions();
		updateDockBar();
		System.gc();
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

	public void updateMenuWindow() {
		if (!showMenuBar || !isUsingFullGui() || isIniting()) {
			return;
		}

		getGuiManager().updateMenuWindow();
		getGuiManager().updateMenuFile();
		System.gc();
	}

	

	/**
	 * // think about this Downloads the latest jar files from the GeoGebra
	 * server.
	 * 
	 * private void updateGeoGebra() { try { File dest = new File(codebase +
	 * Application.JAR_FILE); URL jarURL = new URL(Application.UPDATE_URL +
	 * Application.JAR_FILE);
	 * 
	 * if (dest.exists()) { // check if jarURL is newer then dest try {
	 * URLConnection connection = jarURL.openConnection(); if
	 * (connection.getLastModified() <= dest.lastModified()) { showMessage("No
	 * update available"); return; } } catch (Exception e) { // we don't know if
	 * the file behind jarURL is newer than dest // so don't do anything
	 * showMessage("No update available: " + (e.getMessage())); return; } } //
	 * copy JAR_FILE if (!CopyURLToFile.copyURLToFile(this, jarURL, dest))
	 * return; // copy properties file dest = new File(codebase +
	 * Application.PROPERTIES_FILE); jarURL = new URL(Application.UPDATE_URL +
	 * Application.PROPERTIES_FILE); if (!CopyURLToFile.copyURLToFile(this,
	 * jarURL, dest)) return; // copy jscl file dest = new File(codebase +
	 * Application.JSCL_FILE); jarURL = new URL(Application.UPDATE_URL +
	 * Application.JSCL_FILE); if (!CopyURLToFile.copyURLToFile(this, jarURL,
	 * dest)) return;
	 * 
	 * 
	 * showMessage("Update finished. Please restart GeoGebra."); } catch
	 * (Exception e) { showError("Update failed: "+ e.getMessage()); } }
	 */

	/**
	 * Clears the current construction. Used for File-New.
	 */
	public void clearConstruction() {
		if (isSaved() || saveCurrentFile()) {
			kernel.clearConstruction();

			kernel.initUndoInfo();
			setCurrentFile(null);
			setMoveMode();
		}
	}

	public void exit() {
		// glassPane is active: don't exit now!
		if (glassPaneListener != null) {
			return;
		}

		// stop sound thread if currently playing
		if (getSoundManager() != null) {
			getSoundManager().stopCurrentSound();
		}

		if (isSaved() || (appletImpl != null) || saveCurrentFile()) {
			if (appletImpl != null) {
				setApplet(appletImpl);
				appletImpl.showApplet();
			} else {
				frame.setVisible(false);
			}
		}
	}

	public synchronized void exitAll() {
		// glassPane is active: don't exit now!
		if (glassPaneListener != null) {
			return;
		}

		getGuiManager().exitAll();
	}

	// returns true for YES or NO and false for CANCEL
	public boolean saveCurrentFile() {
		return getGuiManager().saveCurrentFile();
	}

	/*
	 * public void updateStatusLabelAxesRatio() { if (statusLabelAxesRatio !=
	 * null) statusLabelAxesRatio.setText(
	 * euclidianView.getXYscaleRatioString()); }
	 */

	
	@Override
	public AbstractEuclidianView createEuclidianView() {
		return (AbstractEuclidianView)this.euclidianView;
	}

	/***************************************************************************
	 * SAVE / LOAD methodes
	 **************************************************************************/

	/**
	 * Load file
	 */
	public boolean loadFile(File file, boolean isMacroFile) {
		// show file not found message
		if (!file.exists()) {
			/*
			 * First parameter can not be the main component of the application,
			 * otherwise that component would be validated too early if a
			 * missing file was loaded through the command line, which causes
			 * some nasty rendering problems.
			 */
			JOptionPane.showConfirmDialog(null, getError("FileNotFound")
					+ ":\n" + file.getAbsolutePath(), getError("Error"),
					JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
			return false;
		}

		setWaitCursor();

		if (!isMacroFile) {
			// hide navigation bar for construction steps if visible
			setShowConstructionProtocolNavigation(false);
		}

		boolean success = loadXML(file, isMacroFile);

		try {
			createRegressionFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return success;
	}

	/**
	 * Loads construction file
	 * 
	 * @return true if successful
	 */
	final public boolean loadXML(File file, boolean isMacroFile) {
		try {
			FileInputStream fis = null;
			fis = new FileInputStream(file);

			boolean success = false;

			// pretend we're initializing the application to prevent unnecessary
			// update
			if (!initing) {
				initing = true;
				success = loadXML(fis, isMacroFile);
				initing = false;
			} else {
				success = loadXML(fis, isMacroFile);
			}

			if (success && !isMacroFile) {
				setCurrentFile(file);
			}
			return success;
		} catch (Exception e) {
			setCurrentFile(null);
			e.printStackTrace();
			showError(getError("LoadFileFailed") + ":\n" + file);
			return false;
		} finally {
			initing = false;
		}
	}

	/**
	 * Loads construction file from URL
	 * 
	 * @return true if successful
	 */
	final public boolean loadXML(URL url, boolean isMacroFile) {
		try {
			boolean success = loadXML(url.openStream(), isMacroFile);

			// don't clear JavaScript here -- we may have just read one from the file.
			// MyXMLio.readZip() handles script resetting

			// set current file
			if (!isMacroFile && url.toExternalForm().startsWith("file")) {
				String path = url.getPath();
				path = path.replaceAll("%20", " ");
				File f = new File(path);
				if (f.exists()) {
					setCurrentFile(f);
				}
			}

			return success;
		} catch (Exception e) {
			setCurrentFile(null);
			return false;
		}
	}

	public boolean loadXML(byte[] zipFile) {
		try {

			// make sure objects are displayed in the correct View
			setActiveView(AbstractApplication.VIEW_EUCLIDIAN);

			((MyXMLio)myXMLio).readZipFromString(zipFile);

			kernel.initUndoInfo();
			isSaved = true;
			setCurrentFile(null);
			// command list may have changed due to macros
			updateCommandDictionary();

			return true;
		} catch (Exception err) {
			setCurrentFile(null);
			err.printStackTrace();
			return false;
		}
	}

	/*
	 * loads an XML file as a String
	 */
	public boolean loadXML(String xml) {
		try {

			// make sure objects are displayed in the correct View
			setActiveView(AbstractApplication.VIEW_EUCLIDIAN);

			myXMLio.processXMLString(xml, true, false);

			kernel.initUndoInfo();
			isSaved = true;
			setCurrentFile(null);
			// command list may have changed due to macros
			updateCommandDictionary();

			return true;
		} catch (Exception err) {
			setCurrentFile(null);
			err.printStackTrace();
			return false;
		}
	}

	private boolean loadXML(InputStream is, boolean isMacroFile)
			throws Exception {
		try {
			if (!isMacroFile) {
				setMoveMode();
			}

			// make sure objects are displayed in the correct View
			setActiveView(AbstractApplication.VIEW_EUCLIDIAN);

			// reset unique id (for old files, in case they don't have one)
			resetUniqueId();

			BufferedInputStream bis = new BufferedInputStream(is);
			((MyXMLio)myXMLio).readZipFromInputStream(bis, isMacroFile);
			is.close();
			bis.close();

			if (!isMacroFile) {
				kernel.initUndoInfo();
				isSaved = true;
				setCurrentFile(null);
			}

			// command list may have changed due to macros
			updateCommandDictionary();

			return true;
		} catch (MyError err) {
			setCurrentFile(null);
			showError(err);
			return false;
		}
	}

	@Override
	public void setActiveView(int view) {
		if (getGuiManager() != null) {
			getGuiManager().getLayout().getDockManager().setFocusedPanel(view);
		}
	}

	/**
	 * Saves all objects.
	 * 
	 * @return true if successful
	 */
	public boolean saveGeoGebraFile(File file) {
		try {
			setWaitCursor();
			((MyXMLio)myXMLio).writeGeoGebraFile(file);
			isSaved = true;
			setDefaultCursor();
			return true;
		} catch (Exception e) {
			setDefaultCursor();
			showError("SaveFileFailed");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Saves given macros to file.
	 * 
	 * @return true if successful
	 */
	final public boolean saveMacroFile(File file, ArrayList<Macro> macros) {
		try {
			setWaitCursor();
			((MyXMLio)myXMLio).writeMacroFile(file, macros);
			setDefaultCursor();
			return true;
		} catch (Exception e) {
			setDefaultCursor();
			showError("SaveFileFailed");
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void setXML(String xml, boolean clearAll) {
		if (clearAll) {
			setCurrentFile(null);
		}

		try {

			// make sure objects are displayed in the correct View
			setActiveView(AbstractApplication.VIEW_EUCLIDIAN);

			myXMLio.processXMLString(xml, clearAll, false);
		} catch (MyError err) {
			err.printStackTrace();
			showError(err);
		} catch (Exception e) {
			e.printStackTrace();
			showError("LoadFileFailed");
		}
	}

	// endFKH

	public String getPreferencesXML() {
		return myXMLio.getPreferencesXML();
	}

	public byte[] getMacroFileAsByteArray() {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			((MyXMLio)myXMLio).writeMacroStream(os, kernel.getAllMacros());
			os.flush();
			return os.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void loadMacroFileFromByteArray(byte[] byteArray,
			boolean removeOldMacros) {
		try {
			if (removeOldMacros) {
				kernel.removeAllMacros();
			}

			if (byteArray != null) {
				ByteArrayInputStream is = new ByteArrayInputStream(byteArray);
				((MyXMLio)myXMLio).readZipFromInputStream(is, true);
				is.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	final public MyXMLio getXMLio() {
		return (MyXMLio)myXMLio;
	}

	public boolean isSaved() {
		return isSaved;
	}

	@Override
	public void storeUndoInfo() {
		if (isUndoActive()) {
			kernel.storeUndoInfo();
			isSaved = false;
		}
	}

	public void restoreCurrentUndoInfo() {
		if (isUndoActive()) {
			kernel.restoreCurrentUndoInfo();
			isSaved = false;
		}
	}

	/*
	 * final public void clearAll() { // load preferences
	 * GeoGebraPreferences.loadXMLPreferences(this); updateContentPane(); //
	 * clear construction kernel.clearConstruction(); kernel.initUndoInfo();
	 * 
	 * isSaved = true; System.gc(); }
	 */

	
	protected void getWindowLayoutXML(StringBuilder sb, boolean asPreference) {
		// save the dimensions of the current window
				sb.append("\t<window width=\"");

				if ((frame != null) && (frame.getWidth() > 0)) {
					sb.append(frame.getWidth());
				} else {
					sb.append(800);
				}

				sb.append("\" height=\"");

				if ((frame != null) && (frame.getHeight() > 0)) {
					sb.append(frame.getHeight());
				} else {
					sb.append(600);
				}

				sb.append("\" />\n");

				if (guiManager == null) {
					initGuiManager();
				}
				getGuiManager().getLayout().getXml(sb, asPreference);

				// labeling style
				// default changed so we need to always save this now
				// if (labelingStyle != ConstructionDefaults.LABEL_VISIBLE_AUTOMATIC) {
				sb.append("\t<labelingStyle ");
				sb.append(" val=\"");
				sb.append(getLabelingStyle());
				sb.append("\"/>\n");
				// }
	}




	
	/**
	 * Returns the CodeBase URL.
	 */
	public static URL getCodeBase() {
		if (codebase == null) {
			initCodeBase();
		}
		return codebase;
	}

	private static URL codebase;
	private static boolean runningFromJar = false;

	private static void initCodeBase() {
		try {
			// application codebase
			String path = GeoGebra.class.getProtectionDomain().getCodeSource()
					.getLocation().toExternalForm();
			// remove "geogebra.jar" from end of codebase string
			if (path.endsWith(JAR_FILES[0])) {
				runningFromJar = true;
				path = path.substring(0, path.length() - JAR_FILES[0].length());

			}
			// set codebase
			codebase = new URL(path);
			hasFullPermissions = true;
		} catch (Exception e) {
			System.out
					.println("GeoGebra is running with restricted permissions.");
			hasFullPermissions = false;

			// make sure temporary files not used
			// eg ggbApi.getPNGBase64()
			ImageIO.setUseCache(false);

			if (appletImpl != null) {
				// applet codebase
				codebase = appletImpl.getJApplet().getCodeBase();
			}
		}

	}

	final public static boolean isWebstart() {
		if (codebase == null) {
			initCodeBase();
		}
		return codebase.toString().startsWith(
				GeoGebraConstants.GEOGEBRA_ONLINE_WEBSTART_BASE + "jnlp/")
				|| codebase
						.toString()
						.startsWith(
								GeoGebraConstants.GEOGEBRA_ONLINE_WEBSTART_BASE_ALTERNATIVE
										+ "jnlp/");
	}

	final public static boolean isWebstartDebug() {
		if (codebase == null) {
			initCodeBase();
		}
		return codebase.toString().startsWith(
				GeoGebraConstants.GEOGEBRA_ONLINE_WEBSTART_BASE + "debug")
				|| codebase
						.toString()
						.startsWith(
								GeoGebraConstants.GEOGEBRA_ONLINE_WEBSTART_BASE_ALTERNATIVE
										+ "debug");
	}

	final public static boolean hasFullPermissions() {
		return hasFullPermissions;
	}



	

	
	
	
	
	/* Event dispatching */
	private GlassPaneListener glassPaneListener;

	public void startDispatchingEventsTo(JComponent comp) {
		if (guiManager != null) {
			getDialogManager().closeAll();
		}

		if (glassPaneListener == null) {
			Component glassPane = getGlassPane();
			glassPaneListener = new GlassPaneListener(glassPane,
					getContentPane(), comp);

			// mouse
			glassPane.addMouseListener(glassPaneListener);
			glassPane.addMouseMotionListener(glassPaneListener);

			// keys
			KeyboardFocusManager.getCurrentKeyboardFocusManager()
					.addKeyEventDispatcher(glassPaneListener);

			glassPane.setVisible(true);
		}
	}

	public void stopDispatchingEvents() {
		if (glassPaneListener != null) {
			Component glassPane = getGlassPane();
			glassPane.removeMouseListener(glassPaneListener);
			glassPane.removeMouseMotionListener(glassPaneListener);

			KeyboardFocusManager.getCurrentKeyboardFocusManager()
					.removeKeyEventDispatcher(glassPaneListener);

			glassPane.setVisible(false);
			glassPaneListener = null;
		}
	}

	public Component getGlassPane() {
		if (mainComp == frame) {
			return frame.getGlassPane();
		} else if ((appletImpl != null)
				&& (mainComp == appletImpl.getJApplet())) {
			return appletImpl.getJApplet().getGlassPane();
		} else {
			return null;
		}
	}

	public void setGlassPane(Component component) {
		if ((appletImpl != null) && (mainComp == appletImpl.getJApplet())) {
			appletImpl.getJApplet().setGlassPane(component);
		} else if (mainComp == frame) {
			frame.setGlassPane(component);
		}
	}

	public Container getContentPane() {
		if (mainComp == frame) {
			return frame.getContentPane();
		} else if ((appletImpl != null)
				&& (mainComp == appletImpl.getJApplet())) {
			return appletImpl.getJApplet().getContentPane();
		} else {
			return null;
		}
	}

	/*
	 * KeyEventDispatcher implementation to handle key events globally for the
	 * application
	 */
	public boolean dispatchKeyEvent(KeyEvent e) {
		// make sure the event is not consumed
		if (e.isConsumed()) {
			return true;
		}
		
		
		controlDown = isControlDown(e);
		shiftDown = e.isShiftDown();
		

		// check if key event came from this main component
		// (needed to take care of multiple application windows or applets)
		Component eventPane = SwingUtilities.getRootPane(e.getComponent());
		Component mainPane = SwingUtilities.getRootPane(mainComp);
		if ((eventPane != mainPane)
				&& !getGuiManager().getLayout().inExternalWindow(eventPane)) {
			// ESC from dialog: close it
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				Component rootComp = SwingUtilities.getRoot(e.getComponent());
				if (rootComp instanceof JDialog) {
					((JDialog) rootComp).setVisible(false);
					return true;
				}
			}

			// key event came from another window or applet: ignore it
			return false;
		}else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
			handleShiftEvent(shiftDown);
		}

		// if the glass pane is visible, don't do anything
		// (there might be an animation running)
		Component glassPane = getGlassPane();
		if ((glassPane != null) && glassPane.isVisible()) {
			return false;
		}

		// handle global keys like ESC and function keys
		return getGlobalKeyDispatcher().dispatchKeyEvent(e);
	}
	
	/**
	 * handle shift key pressed or released
	 */
	protected void handleShiftEvent(boolean isShiftDown){
		
	}

	@Override
	final public GlobalKeyDispatcher getGlobalKeyDispatcher() {
		if (globalKeyDispatcher == null) {
			globalKeyDispatcher = newGlobalKeyDispatcher();
		}
		return globalKeyDispatcher;
	}

	protected GlobalKeyDispatcher newGlobalKeyDispatcher() {
		return new GlobalKeyDispatcher(this);
	}

	public boolean isPrintScaleString() {
		return printScaleString;
	}

	public void setPrintScaleString(boolean printScaleString) {
		this.printScaleString = printScaleString;
	}

	public File getCurrentImagePath() {
		return currentImagePath;
	}

	public void setCurrentImagePath(File currentImagePath) {
		this.currentImagePath = currentImagePath;
	}

	/**
	 * Loads text file and returns content as String.
	 */
	public String loadTextFile(String s) {
		StringBuilder sb = new StringBuilder();
		try {
			InputStream is = Application.class.getResourceAsStream(s);
			BufferedReader br = new BufferedReader(new InputStreamReader(is,
					"UTF8"));
			String thisLine;
			while ((thisLine = br.readLine()) != null) {
				sb.append(thisLine);
				sb.append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public final boolean isErrorDialogsActive() {
		return isErrorDialogsActive;
	}

	public final void setErrorDialogsActive(boolean isErrorDialogsActive) {
		this.isErrorDialogsActive = isErrorDialogsActive;
	}

	/**
	 * PluginManager gets API with this H-P Ulven 2008-04-16
	 */
	@Override
	public GgbAPI getGgbApi() {
		if (ggbapi == null) {
			ggbapi = new GgbAPI(this);
		}

		return ggbapi;
	}
	
	public PythonBridge getPythonBridge() {
		if (!pythonBridge.isReady()) {
			pythonBridge.init();
		}
		return pythonBridge;
	}
	
	public String getCurrentPythonScript() {
		String script = getPythonBridge().getCurrentPythonScript();
		if (script == null) {
			return kernel.getLibraryPythonScript();
		}
		return script;
	}
	
	public boolean isPythonWindowVisible() {
		if (!pythonBridge.isReady()) {
			return false;
		}
		return getPythonBridge().isWindowVisible();
	}

	public ScriptManager getScriptManager() {
		if (scriptManager == null) {
			scriptManager = new ScriptManager(this);
		}
		return (ScriptManager) scriptManager;
	}

	/*
	 * GgbAPI needs this H-P Ulven 2008-05-25
	 */
	public PluginManager getPluginManager() {
		if (pluginmanager == null) {
			pluginmanager = new PluginManager(this);
		}
		return pluginmanager;
	}// getPluginManager()

	// Michael Borcherds 2008-06-22
	public static void printStacktrace(String message) {
		try {

			throw new Exception(message);

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	

	/*
	 * current possible values http://mindprod.com/jgloss/properties.html AIX
	 * Digital Unix FreeBSD HP UX Irix Linux Mac OS Mac OS X MPE/iX Netware 4.11
	 * OS/2 Solaris Windows 2000 Windows 7 Windows 95 Windows 98 Windows NT
	 * Windows Vista Windows XP
	 */

	/*
	 * needed for padding in Windows XP or earlier without check, checkbox isn't
	 * shown in Vista, Win 7
	 */
	public void setEmptyIcon(JCheckBoxMenuItem cb) {
		if (!WINDOWS_VISTA_OR_LATER) {
			cb.setIcon(getEmptyIcon());
		}
	}

	/*
	 * check for alt pressed (but not ctrl) (or ctrl but not alt on MacOS)
	 */
	public static boolean isAltDown(InputEvent e) {
		// we don't want to act when AltGr is down
		// as it is used eg for entering {[}] is some locales
		// NB e.isAltGraphDown() doesn't work
		if (e.isAltDown() && e.isControlDown()) {
			return false;
		}

		return MAC_OS ? e.isControlDown() : e.isAltDown();
	}

	// global controlDown, shiftDown flags
	// Application.dispatchKeyEvent sets these on every keyEvent.

	private static boolean controlDown = false;
	private static boolean shiftDown = false;

	public static boolean getControlDown() {
		return controlDown;
	}

	public static boolean getShiftDown() {
		return shiftDown;
	}

	public static boolean isControlDown(InputEvent e) {

		/*
		 * debug("isMetaDown = "+e.isMetaDown()); debug("isControlDown =
		 * "+e.isControlDown()); debug("isShiftDown = "+e.isShiftDown());
		 * debug("isAltDown = "+e.isAltDown()); debug("isAltGrDown =
		 * "+e.isAltGraphDown()); debug("fakeRightClick = "+fakeRightClick);
		 */

		if (fakeRightClick) {
			return false;
		}

		boolean ret = (MAC_OS && e.isMetaDown()) // Mac: meta down for
				// multiple
				// selection
				|| (!MAC_OS && e.isControlDown()); // non-Mac: Ctrl down for
		// multiple selection

		// debug("isPopupTrigger = "+e.isPopupTrigger());
		// debug("ret = " + ret);
		return ret;
		// return e.isControlDown();
	}

	private static boolean fakeRightClick = false;

	public static boolean isMiddleClick(MouseEvent e) {
		return (e.getButton() == 2) && (e.getClickCount() == 1);
	}

	public static boolean isRightClick(MouseEvent e) {

		// right-click returns isMetaDown on MAC_OS
		// so we want to return true for isMetaDown
		// if it occurred first at the same time as
		// a popup trigger
		if (MAC_OS && !e.isMetaDown()) {
			fakeRightClick = false;
		}

		if (MAC_OS && e.isPopupTrigger() && e.isMetaDown()) {
			fakeRightClick = true;
		}

		/*
		 * debug("isMetaDown = "+e.isMetaDown()); debug("isControlDown =
		 * "+e.isControlDown()); debug("isShiftDown = "+e.isShiftDown());
		 * debug("isAltDown = "+e.isAltDown()); debug("isAltGrDown =
		 * "+e.isAltGraphDown()); debug("isPopupTrigger = "+e.isPopupTrigger());
		 * debug("fakeRightClick = "+fakeRightClick);
		 */

		if (fakeRightClick) {
			return true;
		}

		boolean ret =
		// e.isPopupTrigger() ||
		(MAC_OS && e.isControlDown()) // Mac: ctrl click = right click
				|| (!MAC_OS && e.isMetaDown()); // non-Mac: right click = meta
		// click

		// debug("ret = " + ret);
		return ret;
		// return e.isMetaDown();
	}

	
	/**
	 * stores an image in the application's imageManager.
	 * 
	 * @return fileName of image stored in imageManager
	 */
	public String createImage(BufferedImage img, String fileName) {
		try {
			// Michael Borcherds 2007-12-10 START moved MD5 code from GeoImage
			// to here
			String zip_directory = "";
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				if (img == null) {
					AbstractApplication.debug("image==null");
				}
				ImageIO.write(img, "png", baos);
				byte[] fileData = baos.toByteArray();

				MessageDigest md;
				md = MessageDigest.getInstance("MD5");
				byte[] md5hash = new byte[32];
				md.update(fileData, 0, fileData.length);
				md5hash = md.digest();
				zip_directory = StringUtil.convertToHex(md5hash);
			} catch (Exception e) {
				AbstractApplication.debug("MD5 Error");
				zip_directory = "images";
				// e.printStackTrace();
			}

			String fn = fileName;
			int index = fileName.lastIndexOf(File.separator);
			if (index != -1) {
				fn = fn.substring(index + 1, fn.length()); // filename without
			}
			// path
			fn = Util.processFilename(fn);

			// filename will be of form
			// "a04c62e6a065b47476607ac815d022cc\liar.gif"
			fileName = zip_directory + File.separator + fn;

			// Michael Borcherds 2007-12-10 END

			// write and reload image to make sure we can save it
			// without problems
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			((MyXMLio)myXMLio).writeImageToStream(os, fileName, img);
			os.flush();
			ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());

			// reload the image
			img = ImageIO.read(is);
			is.close();
			os.close();

			setDefaultCursor();
			if (img == null) {
				showError("LoadFileFailed");
				return null;
			}

			// make sure this filename is not taken yet
			BufferedImage oldImg = ImageManager.getExternalImage(fileName);
			if (oldImg != null) {
				// image with this name exists already
				if ((oldImg.getWidth() == img.getWidth())
						&& (oldImg.getHeight() == img.getHeight())) {
					// same size and filename => we consider the images as equal
					return fileName;
				} else {
					// same name but different size: change filename
					// Michael Borcherds: this bit of code should now be
					// redundant as it
					// is near impossible for the filename to be the same unless
					// the files are the same
					int n = 0;
					do {
						n++;
						int pos = fileName.lastIndexOf('.');
						String firstPart = pos > 0 ? fileName.substring(0, pos)
								: "";
						String extension = pos < fileName.length() ? fileName
								.substring(pos) : "";
						fileName = firstPart + n + extension;
					} while (ImageManager.getExternalImage(fileName) != null);
				}
			}

			imageManager.addExternalImage(fileName, img);

			return fileName;
		} catch (Exception e) {
			setDefaultCursor();
			e.printStackTrace();
			showError("LoadFileFailed");
			return null;
		} catch (java.lang.OutOfMemoryError t) {
			AbstractApplication.debug("Out of memory");
			System.gc();
			setDefaultCursor();
			// t.printStackTrace();
			// TODO change to OutOfMemoryError
			showError("LoadFileFailed");
			return null;
		}
	}

	

	public static String getExtension(File file) {
		String fileName = file.getName();
		int dotPos = fileName.lastIndexOf('.');

		if ((dotPos <= 0) || (dotPos == (fileName.length() - 1))) {
			return "";
		} else {
			return fileName.substring(dotPos + 1).toLowerCase(Locale.US); // Michael
		}
	}

	public static File addExtension(File file, String fileExtension) {
		if (file == null) {
			return null;
		}
		if (getExtension(file).equals(fileExtension)) {
			return file;
		} else {
			return new File(file.getParentFile(), // path
					file.getName() + '.' + fileExtension); // filename
		}
	}

	public static File removeExtension(File file) {
		if (file == null) {
			return null;
		}
		String fileName = file.getName();
		int dotPos = fileName.indexOf('.');

		if (dotPos <= 0) {
			return file;
		} else {
			return new File(file.getParentFile(), // path
					fileName.substring(0, dotPos));
		}
	}

	public static String removeExtension(String fileName) {
		if (fileName == null) {
			return null;
		}
		int dotPos = fileName.indexOf('.');

		if (dotPos <= 0) {
			return fileName;
		} else {
			return fileName.substring(0, dotPos);
		}
	}

	final static int MEMORY_CRITICAL = 100 * 1024;
	static Runtime runtime = Runtime.getRuntime();

	@Override
	public boolean freeMemoryIsCritical() {

		if (runtime.freeMemory() > MEMORY_CRITICAL) {
			return false;
		}

		System.gc();

		return runtime.freeMemory() < MEMORY_CRITICAL;
	}

	@Override
	public long freeMemory() {
		return runtime.freeMemory();
	}

	public long getHeapSize() {
		return runtime.maxMemory();
	}

	public void traceMethodsOn(boolean on) {
		runtime.traceMethodCalls(on);
	}

	public void copyGraphicsViewToClipboard() {

		copyGraphicsViewToClipboard((EuclidianView) getGuiManager()
				.getActiveEuclidianView());
	}

	public void copyGraphicsViewToClipboard(final EuclidianView ev) {

		clearSelectedGeos();

		final Application app = this;

		Thread runner = new Thread() {
			@Override
			public void run() {
				setWaitCursor();

				simpleExportToClipboard(ev);
				
				/*
				 * doesn't work in Win7, XP pasting into eg Paint
				 * pasting into eg Office 2010 is OK
				 * 
				 * 
				if (!WINDOWS_VISTA_OR_LATER) {

					// use other method for WinXP or earlier
					// GraphicExportDialog.exportPNG() doesn't work well on XP
					// eg paste into Paint

					simpleExportToClipboard(ev);

				} else {

					GraphicExportDialog export = new GraphicExportDialog(app);
					export.setDPI("300");

					if (!export.exportPNG(true, false)) {
						// if there's an error (eg memory) just do a simple
						// export
						simpleExportToClipboard(ev);

					}
				}*/

				setDefaultCursor();
			}
		};
		runner.start();

	}

	private void simpleExportToClipboard(EuclidianView ev) {
		double scale = 2d;
		double size = ev.getExportWidth() * ev.getExportHeight();

		// Windows XP clipboard has trouble with images larger than this
		// at double scale (with scale = 2d)
		if (size > 500000) {
			scale = 2.0 * Math.sqrt(500000 / size);
		}

		// copy drawing pad to the system clipboard
		Image img = ev.getExportImage(scale);
		ImageSelection imgSel = new ImageSelection(img);
		Toolkit.getDefaultToolkit().getSystemClipboard()
				.setContents(imgSel, null);
	}

	private static Rectangle screenSize = null;

	/*
	 * gets the screensize (taking into account toolbars etc)
	 */
	public static Rectangle getScreenSize() {
		if (screenSize == null) {
			GraphicsEnvironment env = GraphicsEnvironment
					.getLocalGraphicsEnvironment();
			screenSize = env.getMaximumWindowBounds();
		}

		return screenSize;

	}

	Cursor transparentCursor = null;

	public Cursor getTransparentCursor() {

		if (transparentCursor == null) {
			int[] pixels = new int[16 * 16];
			Image image = Toolkit.getDefaultToolkit().createImage(
					new MemoryImageSource(16, 16, pixels, 0, 16));

			transparentCursor = Toolkit.getDefaultToolkit().createCustomCursor(
					image, new Point(0, 0), "invisibleCursor");
		}
		return transparentCursor;
	}

	Cursor eraserCursor = null;

	public Cursor getEraserCursor() {

		if (eraserCursor == null) {

			Dimension dim = Toolkit.getDefaultToolkit().getBestCursorSize(48,
					48);

			AbstractApplication.debug("getBestCursorSize = " + dim.width + " "
					+ dim.width);

			int size = Math.max(dim.width, dim.height);

			size = Math.max(48, size); // basically we want a size of 48

			Image image = new BufferedImage(size, size,
					BufferedImage.TYPE_INT_ARGB);

			Graphics2D g = (Graphics2D) image.getGraphics();
			EuclidianView.setAntialiasing(g);
			g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
					RenderingHints.VALUE_STROKE_PURE);

			g.setColor(Color.DARK_GRAY);
			g.setStroke(geogebra.awt.BasicStroke.getAwtStroke(EuclidianStatic.getStroke(2,
					EuclidianStyleConstants.LINE_TYPE_FULL)));

			g.drawOval((10 * size) / 48, (10 * size) / 48, (30 * size) / 48,
					(30 * size) / 48);

			eraserCursor = Toolkit.getDefaultToolkit().createCustomCursor(
					image, new Point(size / 2, size / 2), "eraserCursor");
		}
		return eraserCursor;
	}

	private static boolean virtualKeyboardActive = false;

	public static boolean isVirtualKeyboardActive() {
		return virtualKeyboardActive;
	}

	public static void setVirtualKeyboardActive(boolean active) {
		virtualKeyboardActive = active;
		// Application.debug("VK active:"+virtualKeyboardActive);
	}

	
	
	

	// determines which CAS is being used

	/*
	 * public void setDefaultCAS(int CAS) { boolean success = false; if (CAS ==
	 * CAS_MAXIMA) { Application.debug("Attempting to set CAS=Maxima"); success
	 * = setMaximaCAS(); } else if (CAS == CAS_MPREDUCE) {
	 * Application.debug("Attempting to set CAS=MPReduce");
	 * kernel.setDefaultCAS(CAS_MPREDUCE); success = true; } else if (CAS ==
	 * CAS_MATHPIPER) { Application.debug("Attempting to set CAS=MathPiper");
	 * kernel.setDefaultCAS(CAS_MATHPIPER); success = true; }
	 * 
	 * // fallback / default option if (!success) {
	 * Application.debug("Attempting to set CAS=MathPiper");
	 * kernel.setDefaultCAS(CAS_MATHPIPER); }
	 * 
	 * }
	 */

	// public MaximaConfiguration maximaConfiguration = null;

	/*
	 * eg --maximaPath=
	 * 
	 * private void setMaximaPath(String optionValue) { maximaConfiguration =
	 * new MaximaConfiguration();
	 * maximaConfiguration.setMaximaExecutablePath(optionValue);
	 * kernel.setDefaultCAS(CAS_MAXIMA); }
	 */

	/*
	 * eg --CAS=maxima
	 * 
	 * private boolean setMaximaCAS(){
	 * 
	 * maximaConfiguration = JacomaxAutoConfigurator.guessMaximaConfiguration();
	 * 
	 * if (maximaConfiguration != null) { kernel.setDefaultCAS(CAS_MAXIMA);
	 * return true; }
	 * 
	 * return false; }
	 */

	/*
	 * stops eg TAB automatically transferring focus between panes
	 */
	public void removeTraversableKeys(JPanel p) {
		Set<AWTKeyStroke> set = p
				.getFocusTraversalKeys(KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS);
		set.clear();
		p.setFocusTraversalKeys(KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS,
				set);
		p.setFocusTraversalKeys(KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS,
				set);
		p.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
				set);
		p.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
				set);

	}

	LogManager logManager;
	// String logFile = DownloadManager.getTempDir()+"GeoGebraLog.txt";
	// public String logFile = "c:\\GeoGebraLog.txt";
	public StringBuilder logFile = null;

	/*
	 * code from
	 * http://blogs.sun.com/nickstephen/entry/java_redirecting_system_out_and
	 */
	private void setUpLogging() {

		// initialize logging to go to rolling log file
		logManager = LogManager.getLogManager();
		logManager.reset();

		logFile = new StringBuilder(30);

		logFile.append(DownloadManager.getTempDir());
		logFile.append("GeoGebraLog_");
		// randomize filename
		for (int i = 0; i < 10; i++) {
			logFile.append((char) ('a' + Math.round(Math.random() * 25)));
		}
		logFile.append(".txt");

		AbstractApplication.debug(logFile.toString());

		// log file max size 10K, 1 file, append-on-open
		Handler fileHandler;
		try {
			fileHandler = new FileHandler(logFile.toString(), 10000, 1, false);
		} catch (Exception e) {
			logFile = null;
			return;

		}
		fileHandler.setFormatter(new SimpleFormatter());
		Logger.getLogger("").addHandler(fileHandler);

		// preserve old stdout/stderr streams in case they might be useful
		// PrintStream stdout = System.out;
		// PrintStream stderr = System.err;

		// now rebind stdout/stderr to logger
		Logger logger;
		LoggingOutputStream los;

		logger = Logger.getLogger("stdout");
		los = new LoggingOutputStream(logger, StdOutErrLevel.STDOUT);
		System.setOut(new PrintStream(los, true));

		logger = Logger.getLogger("stderr");
		los = new LoggingOutputStream(logger, StdOutErrLevel.STDERR);
		System.setErr(new PrintStream(los, true));
		// show stdout going to logger
		// System.out.println("Hello world!");

		// now log a message using a normal logger
		// logger = Logger.getLogger("test");
		// logger.info("This is a test log message");

		// now show stderr stack trace going to logger
		// try {
		// throw new RuntimeException("Test");
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		// and output on the original stdout
		// stdout.println("Hello on old stdout");
	}

	/*
	 * return folder that the jars are running from eg needed to find local
	 * Maxima install
	 */
	public static String getCodeBaseFolder() {
		String codeBaseFolder = getCodeBase().toString();

		if (!codeBaseFolder.startsWith("file:/")) {
			return null;
		}

		// change %20 to <space>
		if (WINDOWS) {
			codeBaseFolder = codeBaseFolder.replaceAll("%20", " ");
		}

		// strip "file:/", leave leading / for Mac & Linux
		return codeBaseFolder.substring(WINDOWS ? 6 : 5);
	}

	public void exportToLMS() {
		clearSelectedGeos();
		WorksheetExportDialog d = new WorksheetExportDialog(this);

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		JPanel appCP = getCenterPanel();
		int width, height;
		if (appCP != null) {
			width = appCP.getWidth();
			height = appCP.getHeight();
		} else {
			width = WorksheetExportDialog.DEFAULT_APPLET_WIDTH;
			height = WorksheetExportDialog.DEFAULT_APPLET_HEIGHT;
		}

		clipboard.setContents(
				new StringSelection(d.getAppletTag(this, null, width, height,
						false, true, false, false)), null);
		d.setVisible(false);
		d.dispose();

		showMessage(getMenu("ClipboardMessage"));

	}

	/*
	 * gets a String from the clipboard
	 * 
	 * @return null if not possible
	 */
	public String getStringFromClipboard() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable contents = clipboard.getContents(null);
		String str = null;
		try {
			str = (String) contents.getTransferData(DataFlavor.stringFlavor);
		} catch (UnsupportedFlavorException e) {
		} catch (IOException e) {
		}
		return str;
	}

	@Override
	public ImageManager getImageManager() {
		return imageManager;
	}

	private SoundManager soundManager = null;

	@Override
	public SoundManager getSoundManager() {
		if (soundManager == null) {
			soundManager = new SoundManager(this);
		}
		return soundManager;
	}



	
	


	
	
	public void checkCommands(HashMap<String, CommandProcessor> map) {
		initTranslatedCommands();

		if (rbcommand == null) {
			return; // eg applet with no properties jar
		}

		Enumeration<String> e = rbcommand.getKeys();
		while (e.hasMoreElements()) {
			String s = e.nextElement();
			if (!s.contains(syntaxStr) && (map.get(s) == null)) {
				boolean write = true;
				try {
					rbcommand.getString(s + syntaxStr);
				} catch (Exception ex) {
					write = false;
				}
				if (write) {
					System.out.println(s);
				}
			}
		}
	}

	

	

	@Override
	public void setScrollToShow(boolean b) {
		if (guiManager != null) {
			guiManager.setScrollToShow(b);
		}
	}

	
	
	DrawEquation drawEquation;

	@Override
	public DrawEquation getDrawEquation() {
		if (drawEquation == null) {
			drawEquation = new DrawEquation();
		}
		return drawEquation;
	}

	/** flag to test whether to draw Equations full resolution */
	public boolean exporting = false;


	// random id to identify ggb files
	// eg so that GeoGebraTube can notice it's a version of the same file
	private String uniqueId = "" + UUID.randomUUID();

	public String getUniqueId() {
		return uniqueId;
	}

	@Override
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public void resetUniqueId() {
		uniqueId = "" + UUID.randomUUID();
	}

	// //////////////////////////////////
	// FILE VERSION HANDLING
	// //////////////////////////////////


	protected SpreadsheetTraceManager traceManager;

	private DialogManager dialogManager;
	
	public void setShowInputHelpToggle(boolean flag) {
		if (showInputHelpToggle == flag) {
			return;
		}

		showInputHelpToggle = flag;
		getGuiManager().updateAlgebraInput();
		updateMenubar();
	}

	@Override
	public void callAppletJavaScript(String string, Object[] args) {
		getApplet().callJavaScript(string, args);
	}


	@Override
	public void evalPythonScript(AbstractApplication app, String script,
			String arg) {
		if (arg != null) script = "arg="+arg+";"+script;
		Application.debug(script);
		getPythonBridge().eval(script);
		
	}

	@Override
	public boolean showView(int view) {
		return getGuiManager().showView(view);
	}

	@Override
	public void traceToSpreadsheet(GeoElement ge) {
		getGuiManager().traceToSpreadsheet(ge);
	}

	@Override
	public void resetTraceColumn(GeoElement ge) {
		getGuiManager().resetTraceColumn(ge);
	}

	@Override
	public String getTraceXML(GeoElement ge) {
		return getTraceManager().getTraceXML(ge);
	}

	@Override
	public String toLowerCase(String str) {
		return str.toLowerCase(Locale.US);
	}

	public String toUpperCase(String s) {
		return s.toUpperCase(Locale.US);
	}

	@Override
	public String getLanguage() {
		return getLocale().getLanguage();
	}

	@Override
	public void evalScript(AbstractApplication app, String script, String arg) {
		CallJavaScript.evalScript(app, script, arg);

	}

	@Override
	public int getMD5folderLength(String fullPath) {
		return fullPath.indexOf(File.separator);
	}

	@Override
	public void setShowConstructionProtocolNavigation(boolean show,
			boolean playButton, double playDelay, boolean showProtButton) {
		// TODO the settings should *always* be stored in the
		// ConstructionProtoclSettings object
		if (getGuiManager() != null) {
			setShowConstructionProtocolNavigation(show);

			if (show) {
				getGuiManager().setShowConstructionProtocolNavigation(show,
						playButton, playDelay, showProtButton);
			}
		} else {
			ConstructionProtocolSettings cpSettings = getSettings()
					.getConstructionProtocol();
			cpSettings.setShowPlayButton(playButton);
			cpSettings.setPlayDelay(playDelay);
			cpSettings.setShowConstructionProtocol(showProtButton);
			setShowConstructionProtocolNavigation(show);
		}

	}

	// TODO: should be moved to ApplicationSettings
	@Override
	public void setTooltipTimeout(int ttt) {
		if (ttt > 0) {
			ToolTipManager.sharedInstance().setDismissDelay(ttt * 1000);
			// make it fit into tooltipTimeouts array:
			ToolTipManager.sharedInstance().setDismissDelay(
					getTooltipTimeout() * 1000);
		} else {
			ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		}
	}

	@Override
	public double getWidth() {
		if (isApplet()) {
			AppletImplementation applet = getApplet();
			return applet.width;
		} 
			JPanel appCP = getCenterPanel();
			return appCP.getWidth();
	}
	
	@Override
	public double getHeight() {
		if (isApplet()) {
			AppletImplementation applet = getApplet();
			return applet.height;
		} 
			JPanel appCP = getCenterPanel();
			return appCP.getHeight();
	}

	@Override
	public geogebra.common.awt.Font getFontCommon(boolean b, int i, int size) {
		return new geogebra.awt.Font(getFont(b,i,size));
	}

	public geogebra.common.awt.Font getBoldFontCommon() {
		return new geogebra.awt.Font(getBoldFont());
	}
	
	@Override
	public SpreadsheetTraceManager getTraceManager() {
		if (traceManager == null)
			traceManager = new SpreadsheetTraceManager(this);
		return traceManager;
	}

	@Override
	public void repaintSpreadsheet() {
		if (isUsingFullGui() && getGuiManager().hasSpreadsheetView()) {
			getGuiManager().getSpreadsheetView().repaint();
		}
		
	}

	

	@Deprecated
	@Override
	public UndoManager getUndoManager(Construction cons) {
		return new UndoManager(cons);
	}

	@Override
	public GeoElementGraphicsAdapter newGeoElementGraphicsAdapter() {
		return new GeoElementGraphicsAdapterDesktop(this);
	}

	@Override
	public AbstractAnimationManager newAnimationManager(Kernel kernel2) {
		return new AnimationManager(kernel2);
	}

	@Override
	public AlgoElement newAlgoShortestDistance(Construction cons, String label,
			GeoList list, GeoPointND start, GeoPointND end, GeoBoolean weighted) {
		return new geogebra.kernel.discrete.AlgoShortestDistance(cons, label, list, start, end, weighted);	
	}

	
	@Override
	public AbstractSpreadsheetTableModel getSpreadsheetTableModel() {
		if(tableModel == null){
			tableModel = new SpreadsheetTableModel(this,SPREADSHEET_INI_ROWS,SPREADSHEET_INI_COLS);
		}
		return tableModel;
	}
	
	@Override
	public CommandProcessor newCmdBarCode(){
		return new CmdBarCode(kernel);
	}

	@Override
	public void initScriptingBundle() {
		rbcommandScripting = MyResourceBundle.createBundle(RB_COMMAND,
				new Locale(getScriptingLanguage()));
		debug(rbcommandScripting.getLocale());
		
	}

	@Override
	public String getScriptingCommand(String internal) {
		return rbcommandScripting.getString(internal);
	}

	@Override
	protected boolean isCommandChanged() {
		// TODO Auto-generated method stub
		return rbcommandOld != rbcommand;
	}

	@Override
	protected void setCommandChanged(boolean b) {
		rbcommandOld = rbcommand;
		
	}

	@Override
	protected boolean isCommandNull() {
		return rbcommand == null;
	}
	@Override
	public boolean isRightClick(AbstractEvent e) {
		return isRightClick(geogebra.euclidian.event.MouseEvent.getEvent(e));
	}

	@Override
	public boolean isControlDown(AbstractEvent e) {
		return isControlDown(geogebra.euclidian.event.MouseEvent.getEvent(e));
	}
	
	@Override
	public boolean isMiddleClick(AbstractEvent e) {
		return isMiddleClick(geogebra.euclidian.event.MouseEvent.getEvent(e));
	}

	public Font getFontCanDisplayAwt(String string, boolean b, int plain, int i) {
		return geogebra.awt.Font.getAwtFont(getFontCanDisplay(string,b,plain,i));
	}

	public Font getFontCanDisplayAwt(String string) {
		return geogebra.awt.Font.getAwtFont(getFontCanDisplay(string));
	}

	public Font getFontCanDisplayAwt(String value, int plain) {
		return geogebra.awt.Font.getAwtFont(getFontCanDisplay(value,plain));
	}
	

	@Override
	public boolean isMacOS() {
		return MAC_OS;
	}

	@Override
	public boolean isWindows() {
		return WINDOWS;
	}

	@Override
	public boolean isWindowsVistaOrLater() {
		return WINDOWS_VISTA_OR_LATER;
	}

	// don't pull these up to common, use the non static methods isWindows(), isMacOS(), isWindowsVistaOrLater() instead
	private static String OS = System.getProperty("os.name").toLowerCase(Locale.US);
	public static boolean MAC_OS = OS.startsWith("mac"); 
	public static boolean WINDOWS = OS.startsWith("windows");
	public static boolean LINUX = OS.startsWith("linux");
	// make sure still works in the future on eg Windows 9 
	public static boolean WINDOWS_VISTA_OR_LATER = WINDOWS 
			&& !OS.startsWith("windows 2000") && !OS.startsWith("windows 95") 
			&& !OS.startsWith("windows 98") && !OS.startsWith("windows nt") 
			&& !OS.startsWith("windows xp");

	@Override
	public boolean isHTML5Applet() {
		return false;
	}

	@Override
	public StringType getFormulaRenderingType() {
		return StringType.LATEX;
	}

	@Override
	public DialogManager getDialogManager() {
		
		if (dialogManager == null) {
			if (getGuiManager() == null) {
				dialogManager = new DialogManagerMinimal(this);
			} else {
				dialogManager = getGuiManager().getDialogManager();
			}
		}
		
		return dialogManager;
	}

	@Override
	public String getLocaleStr() {
		return getLocale().toString();
	}

	@Override
	public void showURLinBrowser(String strURL) {
		getGuiManager().showURLinBrowser(strURL);
		
	}

	@Override
	public void uploadToGeoGebraTube() {
 		GeoGebraTubeExportDesktop ggbtube = new GeoGebraTubeExportDesktop(this);
 		ggbtube.uploadWorksheet();
	}

}
