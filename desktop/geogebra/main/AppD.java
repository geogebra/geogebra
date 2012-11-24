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
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.factories.AwtFactory;
import geogebra.common.factories.CASFactory;
import geogebra.common.factories.Factory;
import geogebra.common.factories.SwingFactory;
import geogebra.common.gui.GuiManager;
import geogebra.common.gui.menubar.MenuInterface;
import geogebra.common.gui.view.algebra.AlgebraView;
import geogebra.common.io.MyXMLHandler;
import geogebra.common.io.layout.DockPanelData;
import geogebra.common.io.layout.Perspective;
import geogebra.common.kernel.AnimationManager;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.ConstructionDefaults;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Macro;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import geogebra.common.main.App;
import geogebra.common.main.DialogManager;
import geogebra.common.main.MyError;
import geogebra.common.main.ProverSettings;
import geogebra.common.main.SingularWSSettings;
import geogebra.common.main.SpreadsheetTableModel;
import geogebra.common.main.settings.ConstructionProtocolSettings;
import geogebra.common.main.settings.Settings;
import geogebra.common.util.Base64;
import geogebra.common.util.GeoGebraLogger.LogDestination;
import geogebra.common.util.Language;
import geogebra.common.util.LowerCaseDictionary;
import geogebra.common.util.NormalizerMinimal;
import geogebra.common.util.StringUtil;
import geogebra.common.util.Unicode;
import geogebra.euclidian.DrawEquationD;
import geogebra.euclidian.EuclidianControllerD;
import geogebra.euclidian.EuclidianViewD;
import geogebra.euclidianND.EuclidianViewND;
import geogebra.export.GeoGebraTubeExportDesktop;
import geogebra.export.WorksheetExportDialog;
import geogebra.factories.AwtFactoryD;
import geogebra.factories.CASFactoryD;
import geogebra.factories.FactoryD;
import geogebra.factories.FormatFactoryD;
import geogebra.factories.LaTeXFactoryD;
import geogebra.factories.SwingFactoryD;
import geogebra.factories.UtilFactoryD;
import geogebra.gui.GuiManagerD;
import geogebra.gui.infobar.InfoBarD;
import geogebra.gui.layout.DockPanel;
import geogebra.gui.view.consprotocol.ConstructionProtocolNavigation;
import geogebra.io.MyXMLio;
import geogebra.kernel.AnimationManagerD;
import geogebra.kernel.UndoManagerD;
import geogebra.kernel.commands.CmdBarCode;
import geogebra.kernel.geos.GeoElementGraphicsAdapterDesktop;
import geogebra.plugin.CallJavaScript;
import geogebra.plugin.GgbAPID;
import geogebra.plugin.PluginManager;
import geogebra.plugin.ScriptManagerD;
import geogebra.plugin.jython.AppletPythonBridge;
import geogebra.plugin.jython.PythonBridgeD;
import geogebra.sound.SoundManager;
import geogebra.util.DownloadManager;
import geogebra.util.GeoGebraLogger;
import geogebra.util.ImageManager;
import geogebra.util.Normalizer;
import geogebra.util.Util;

import java.awt.AWTKeyStroke;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import java.awt.event.ComponentEvent;
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
import java.net.URLConnection;
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
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

public class AppD extends App implements KeyEventDispatcher {

	/**
	 * License file
	 */
	public static final String LICENSE_FILE = "/geogebra/gui/_license.txt";

	/**
	 * Command line arguments
	 */
	private CommandLineArguments args;

	// ==============================================================
	// JAR fields
	// ==============================================================

	public final static String CAS_JAR_NAME = "geogebra_cas.jar";
	public final static String JAVASCRIPT_JAR_NAME = "geogebra_javascript.jar";

	/**
	 * Possible alternative names for geogebra.jar. Used for 3D webstart at the
	 * moment.
	 */
	public final static String[] GEOGEBRA_JAR_ALT = { "geogebra-jogl1.jar",
			"geogebra-jogl2.jar" };

	/**
	 * Jar file names
	 */
	public static final String[] JAR_FILES = { "geogebra.jar",
			"geogebra_main.jar", "geogebra_gui.jar", CAS_JAR_NAME,
			"geogebra_algos.jar", "geogebra_export.jar", JAVASCRIPT_JAR_NAME, // don't
																				// put
																				// at
																				// end
																				// (sometimes
																				// omitted,see
																				// WorksheetExportDialog)
			"jlatexmath.jar", // LaTeX
			"jlm_greek.jar", // Greek Unicode codeblock (for LaTeX texts)
			"jlm_cyrillic.jar", // Cyrillic Unicode codeblock (for LaTeX texts)
			"geogebra_usb.jar", "jython.jar", "geogebra_properties.jar" };

	// ==============================================================
	// LOCALE fields
	// ==============================================================

	private Locale currentLocale;
	private Locale tooltipLocale = null;

	// supported GUI languages (from properties files)
	private static ArrayList<Locale> supportedLocales = null;

	public static ArrayList<Locale> getSupportedLocales() {
		return getSupportedLocales(GeoGebraConstants.IS_PRE_RELEASE);
	}

	public static ArrayList<Locale> getSupportedLocales(boolean prerelease) {

		if (supportedLocales != null) {
			return supportedLocales;
		}

		supportedLocales = new ArrayList<Locale>();

		Language[] languages = Language.values();

		for (int i = 0; i < languages.length; i++) {

			Language language = languages[i];

			if (language.fullyTranslated || prerelease) {

				if (language.locale.length() == 2) {
					// eg "en"
					supportedLocales.add(new Locale(language.locale));
				} else if (language.locale.length() == 4) {
					// eg "enGB" -> "en", "GB"
					supportedLocales.add(new Locale(language.locale.substring(
							0, 2), language.locale.substring(2, 4)));
				} else if (language.locale.length() == 6) {
					// eg "noNONY" -> "no", "NO", "NY"
					supportedLocales.add(new Locale(language.locale.substring(
							0, 2), language.locale.substring(2, 4),
							language.locale.substring(4, 6)));
				}
			}

		}

		return supportedLocales;

	}

	// ==============================================================
	// FILE fields
	// ==============================================================

	private static LinkedList<File> fileList = new LinkedList<File>();
	protected File currentPath, currentImagePath, currentFile = null;

	/** maximum number of files to (save &) show in File -> Recent submenu */
	public static final int MAX_RECENT_FILES = 8;

	// file extension string
	/** extension of GeoGebra files */
	public static final String FILE_EXT_GEOGEBRA = "ggb";
	/** extension of GeoGebra tool files */
	public static final String FILE_EXT_GEOGEBRA_TOOL = "ggt";
	/** extension of PNG files */
	public static final String FILE_EXT_PNG = "png";
	/** extension of encapsualted postscript files */
	public static final String FILE_EXT_EPS = "eps";
	/** extension of PDF files */
	public static final String FILE_EXT_PDF = "pdf";
	/** extension of EMF files */
	public static final String FILE_EXT_EMF = "emf";
	/** extension of SVG files */
	public static final String FILE_EXT_SVG = "svg";
	/** extension of HTML files */
	public static final String FILE_EXT_HTML = "html";
	/** extension of HTM files */
	public static final String FILE_EXT_HTM = "htm";
	/** extension of TeX files */
	public static final String FILE_EXT_TEX = "tex";

	// ==============================================================
	// RESOURCE fields
	// ==============================================================

	private ResourceBundle rbmenu, rbmenuTT, rbcommand, rbcommandTT,
			rbcommandEnglish, rbcommandOld, rbcommandScripting, rberror,
			rbcolors, rbplain, rbplainTT, rbmenuEnglish, rbsymbol, rbsettings;

	private static final String RB_MENU = "/geogebra/properties/menu";
	private static final String RB_COMMAND = "/geogebra/properties/command";
	private static final String RB_ERROR = "/geogebra/properties/error";
	private static final String RB_PLAIN = "/geogebra/properties/plain";
	private static final String RB_SYMBOL = "/geogebra/properties/symbols";
	/** path to javaui properties (without extension) */
	public static final String RB_JAVA_UI = "/geogebra/properties/javaui";
	private static final String RB_COLORS = "/geogebra/properties/colors";
	private static final String RB_SETTINGS = "/geogebra/export/settings";

	// ==============================================================
	// APPLET fields
	// ==============================================================

	private static AppletImplementation appletImpl;
	private boolean isApplet = false;

	// ==============================================================
	// GUI fields
	// ==============================================================

	private JFrame frame;

	/** Main component */
	protected Component mainComp;

	/** Panels that form the main content panel */
	protected JPanel centerPanel, northPanel, southPanel, eastPanel, westPanel;

	/**
	 * Split pane panel that holds main content panel and a slide-out sidebar
	 * help panel for the input bar.
	 */
	private JSplitPane applicationSplitPane;

	private DockBarInterface dockBar;
	private boolean showDockBar = true;
	private boolean isDockBarEast = true;
	
	private int toolbarPosition = SwingConstants.NORTH;

	protected boolean showAlgebraView = true;

	protected boolean showConsProtNavigation = false;

	/**
	 * Preferred application frame size. Used in case frame size needs updating.
	 */
	private Dimension preferredSize = new Dimension();

	/** Horizontal page margin in cm */
	public static final double PAGE_MARGIN_X = (1.8 * 72) / 2.54;

	/** Vertical page margin in cm */
	public static final double PAGE_MARGIN_Y = (1.8 * 72) / 2.54;

	/** Default icon size */
	public static final int DEFAULT_ICON_SIZE = 32;
	private int maxIconSize = DEFAULT_ICON_SIZE;

	/**
	 * made a little darker in ggb40 (problem showing on some projectors)
	 */
	public static final Color COLOR_SELECTION = new Color(210, 210, 225);

	// ==============================================================
	// MODEL & MANAGER fields
	// ==============================================================

	private final FontManagerD fontManager;

	/** GUI manager */
	protected GuiManager guiManager;

	private GlobalKeyDispatcherD globalKeyDispatcher;

	protected ImageManager imageManager;

	private GgbAPID ggbapi = null;
	private PluginManager pluginmanager = null;

	private PythonBridgeD pythonBridge = null;

	private SpreadsheetTableModelD tableModel;

	// ==============================================================
	// MISC FLAGS
	// ==============================================================

	private boolean printScaleString = false;

	private boolean allowToolTips = true;

	private boolean isErrorDialogsActive = true;

	protected boolean isErrorDialogShowing = false;

	/*************************************************************
	 * Construct application within JFrame
	 * 
	 * @param args
	 * @param frame
	 * @param undoActive
	 */
	public AppD(CommandLineArguments args, JFrame frame, boolean undoActive) {
		this(args, frame, null, null, undoActive);
	}

	/*************************************************************
	 * Construct application within Applet
	 * 
	 * @param args
	 * @param appletImpl
	 * @param undoActive
	 */
	public AppD(CommandLineArguments args, AppletImplementation appletImpl,
			boolean undoActive) {
		this(args, null, appletImpl, null, undoActive);
	}

	/*************************************************************
	 * Construct application within Container (e.g. GeoGebraPanel)
	 * 
	 * @param args
	 * @param comp
	 * @param undoActive
	 */
	public AppD(CommandLineArguments args, Container comp, boolean undoActive) {
		this(args, null, null, comp, undoActive);
	}

	/*************************************************************
	 * GeoGebra application general constructor
	 * 
	 * @param args
	 * @param frame
	 * @param appletImpl
	 * @param comp
	 * @param undoActive
	 */
	protected AppD(CommandLineArguments args, JFrame frame,
			AppletImplementation appletImpl, Container comp, boolean undoActive) {

		this.args = args;

		if (args != null && !args.containsArg("silent")) {
			App.logger = new GeoGebraLogger();
			logger.setLogDestination(LogDestination.CONSOLE);
			if (args.containsArg("logLevel")) {
				logger.setLogLevel(args.getStringValue("logLevel"));
			}
			if (args.containsArg("logFile")) {
				logger.setLogDestination(LogDestination.FILE);
				logger.setLogFile(args.getStringValue("logFile"));
			}
		}

		infobar = new InfoBarD(this);

		setFileVersion(GeoGebraConstants.VERSION_STRING);

		OS = System.getProperty("os.name").toLowerCase(Locale.US);

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

		App.debug("isApplet=" + isApplet + " runningFromJar=" + runningFromJar
				+ " appletImpl=" + appletImpl);
		if (!isApplet && runningFromJar) {
			setUpLogging();
		} else {
			App.debug("Not setting up logging via LogManager");
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

		fontManager = new FontManagerD();
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
			pythonBridge = new PythonBridgeD(this);
		} else
			pythonBridge = new AppletPythonBridge(this);

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

		// init xml io for construction loading
		myXMLio = new MyXMLio(kernel, kernel.getConstruction());

		// init default preferences if necessary
		if (!isApplet) {
			GeoGebraPreferencesD.getPref().initDefaultXML(this);
		}

		if (ggtloading) {
			if (!isApplet) {
				GeoGebraPreferencesD.getPref().loadXMLPreferences(this);
			}
		}

		// open file given by startup parameter
		handleOptionArgsEarly(args); // for --regressionFile=...
		// init singularWS
		initializeSingularWSD();
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
			currentPath = GeoGebraPreferencesD.getPref().getDefaultFilePath();
			currentImagePath = GeoGebraPreferencesD.getPref()
					.getDefaultImagePath();

			if (!fileLoaded && !ggtloading) {
				GeoGebraPreferencesD.getPref().loadXMLPreferences(this);
			}
		}

		if (isUsingFullGui() && (tmpPerspectives != null)) {
			getGuiManager().getLayout().setPerspectives(tmpPerspectives);
		}

		if (isUsingFullGui() && ggtloading) {
			getGuiManager().setToolBarDefinition(
					geogebra.gui.toolbar.Toolbar.getAllTools(this));
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

	// **************************************************************************
	// INIT
	// **************************************************************************

	public void setApplet(AppletImplementation appletImpl) {
		isApplet = true;
		AppD.appletImpl = appletImpl;
		mainComp = appletImpl.getJApplet();
	}

	public AppletImplementation getApplet() {
		return appletImpl;
	}

	@Override
	public void reset() {
		if (appletImpl != null) {
			appletImpl.reset();
		} else if (currentFile != null) {
			AppD2.loadFile(this, currentFile, false);
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

	/**
	 * Sets the look and feel.
	 * 
	 * @param isSystemLAF
	 *            true => set system LAF, false => set cross-platform LAF
	 */
	public static void setLAF(boolean isSystemLAF) {
		try {
			if (isSystemLAF) {
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			} else {
				UIManager.setLookAndFeel(UIManager
						.getCrossPlatformLookAndFeelClassName());
			}
		} catch (Exception e) {
			App.debug(e + "");
		}
	}

	/**
	 * Toggles between the system LAF and the cross-platform LAF
	 */
	public static void toggleCrossPlatformLAF() {
		setLAF(!UIManager.getLookAndFeel().isNativeLookAndFeel());
	}

	private static void initFactories() {
		geogebra.common.factories.AwtFactory.prototype = new AwtFactoryD();
		geogebra.common.factories.FormatFactory.prototype = new FormatFactoryD();
		geogebra.common.factories.LaTeXFactory.prototype = new LaTeXFactoryD();

		// moved to getCASFactory() so that applets load quicker
		// geogebra.common.factories.CASFactory.prototype = new CASFactoryD();

		// moved to getCASFactory() so that applets load quicker
		// geogebra.common.factories.SwingFactory.prototype = new
		// SwingFactoryD();

		geogebra.common.factories.UtilFactory.prototype = new UtilFactoryD();

		// moved to getFactory() so that applets load quicker
		// geogebra.common.factories.Factory.prototype = new FactoryD();

		geogebra.common.util.StringUtil.prototype = new geogebra.util.StringUtil();

		geogebra.common.euclidian.HatchingHandler.prototype = new geogebra.euclidian.HatchingHandlerD();
		geogebra.common.euclidian.EuclidianStatic.prototype = new geogebra.euclidian.EuclidianStaticD();

		geogebra.common.euclidian.clipping.DoubleArrayFactory.prototype = new geogebra.euclidian.clipping.DoubleArrayFactory();

	}

	private static void handleHelpVersionArgs(CommandLineArguments args) {
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
							+ "  --regressionFile=FILENAME\texport textual representations of dependent objects, then exit\n"
							+ "  --versionCheckAllow=SETTING\tallow version check (on/off or true/false for single launch)\n"
							+ "  --logLevel=LEVEL\tset logging level (EMERGENCY|ALERT|CRITICAL|ERROR|WARN|NOTICE|INFO|DEBUG|TRACE)\n"
							+ "  --logFile=FILENAME\tset log file"
							+ "  --silent\tCompletely mute logging\n"
							+ "  --prover=OPTIONS\tset options for the prover subsystem (use --proverhelp for more information)\n"
							+ "  --singularWS=OPTIOMS\tset options for SingularWS (use --singularWShelp for more information)\n");

			System.exit(0);
		}
		if (args.containsArg("proverhelp")) {
			// help message for the prover
			System.out
					.println("  --prover=OPTIONS\tset options for the prover subsystem\n"
							+ "    where OPTIONS is a comma separated list, formed with the following available settings (defaults in brackets):\n"
							+ "      engine:ENGINE\tset engine (Auto|OpenGeoProver|Recio|Botana|PureSymbolic) ["
							+ ProverSettings.proverEngine
							+ "]\n"
							+ "      timeout:SECS\tset the maximum time attributed to the prover (in seconds) ["
							+ ProverSettings.proverTimeout
							+ "]\n"
							+ "      maxterms:NUMBER\tset the maximal number of terms ["
							+ ProverSettings.maxTerms
							+ "] (OpenGeoProver only)\n"
							+ "      method:METHOD\tset the method (Wu|Groebner|Area) ["
							+ ProverSettings.proverMethod
							+ "] (OpenGeoProver only)\n"
							+ "      fpnevercoll:BOOLEAN\tassume three free points are never collinear ["
							+ ProverSettings.freePointsNeverCollinear
							+ "] (Botana only)\n"
							+ "      usefixcoords:BOOLEAN\tuse fix coordinates for the first points ["
							+ ProverSettings.useFixCoordinates
							+ "] (Botana only)\n"
							+ "  Example: --prover=engine:Botana,timeout:10,fpnevercoll:false\n");
			System.exit(0);
		}
		if (args.containsArg("singularWShelp")) {
			// help message for singularWS
			System.out
					.println(" --singularWS=OPTIONS\tset options for SingularWS\n"
							+ "   where OPTIONS is a comma separated list, formed with the following available settings (defaults in brackets):\n"
							+ "      enable:BOOLEAN\tuse Singular WebService when possible ["
							+ SingularWSSettings.useSingularWebService
							+ "]\n"
							+ "      remoteURL:URL\tset the remote server URL ["
							+ SingularWSSettings.singularWebServiceRemoteURL
							+ "]\n"
							+ "      timeout:SECS\tset the timeout ["
							+ SingularWSSettings.singularWebServiceTimeout
							+ "]\n" + "  Example: singularWS=timeout:3\n");
			System.exit(0);
		}
		// help debug applets
		info("GeoGebra " + GeoGebraConstants.VERSION_STRING + " "
				+ GeoGebraConstants.BUILD_DATE + " Java " + getJavaVersion());
		if (args.containsArg("v")) {
			System.exit(0);
		}
	}

	@Override
	protected EuclidianControllerD newEuclidianController(Kernel kernel1) {
		return new EuclidianControllerD(kernel1);
	}

	@Override
	protected EuclidianView newEuclidianView(boolean[] showAxesFlags,
			boolean showGridFlags) {
		return new EuclidianViewD(euclidianController, showAxesFlags,
				showGridFlags, getSettings().getEuclidian(1));
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
	@Override
	final protected void initGuiManager() {
		setWaitCursor();
		guiManager = newGuiManager();

		AppD2.newLayout(this);
		// guiManager.setLayout(new geogebra.gui.layout.LayoutD());

		guiManager.initialize();
		setDefaultCursor();

		// make sure mouse events are NOT consumed when a popup menu is closed,
		// see #2574
		UIManager.put("PopupMenu.consumeEventOnClose", Boolean.FALSE);
	}

	// **************************************************************************
	// COMMAND LINE ARGUMENTS
	// **************************************************************************

	/**
	 * Handles command line options
	 */
	private void handleOptionArgs(CommandLineArguments args) {
		// args.containsArg("help");
		if (args == null) {
			return;
		}

		if (args.containsArg("showAlgebraInput")) {
			boolean showInputBar = args.getBooleanValue("showAlgebraInput",
					true);
			if (!showInputBar) {
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
			boolean showAxesParam = args.getBooleanValue("showAxes", true);
			this.showAxes[0] = showAxesParam;
			this.showAxes[1] = showAxesParam;
			this.getSettings().getEuclidian(1)
					.setShowAxes(showAxesParam, showAxesParam);
			this.getSettings().getEuclidian(2)
					.setShowAxes(showAxesParam, showAxesParam);
		}

		if (args.containsArg("showGrid")) {
			boolean showGridParam = args.getBooleanValue("showGrid", false);
			this.showGrid = showGridParam;
			this.getSettings().getEuclidian(1).showGrid(showGridParam);
			this.getSettings().getEuclidian(2).showGrid(showGridParam);
		}

		if (args.containsArg("primary")) {
			boolean primary = args.getBooleanValue("primary", false);
			if (primary) {

				getGuiManager().getLayout().applyPerspective("BasicGeometry");
				GlobalKeyDispatcherD.changeFontsAndGeoElements(this, 20, false);
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

		setVersionCheckAllowed(args.getStringValue("versionCheckAllow"));

	}

	private static boolean versionCheckAllowed = true;

	private void setVersionCheckAllowed(String versionCheckAllow) {

		if (isApplet() || isWebstart()) {
			versionCheckAllowed = false;
			return;
		}

		if (versionCheckAllow != null) {
			if (versionCheckAllow.equals("off")) {
				GeoGebraPreferencesD.getPref().saveVersionCheckAllow("false");
				versionCheckAllowed = false;
				return;
			}
			if (versionCheckAllow.equals("on")) {
				GeoGebraPreferencesD.getPref().saveVersionCheckAllow("true");
				versionCheckAllowed = true;
				return;
			}
			if (versionCheckAllow.equals("false")) {
				versionCheckAllowed = false;
				return;
			}
			if (versionCheckAllow.equals("true")) {
				versionCheckAllowed = true;
				return;
			}
			App.warn("Option versionCheckAllow not recognized : "
					.concat(versionCheckAllow));
		}

		versionCheckAllowed = GeoGebraPreferencesD.getPref()
				.loadVersionCheckAllow("true");

	}

	private static void setProverOption(String option) {
		String[] str = option.split(":", 2);
		if ("engine".equalsIgnoreCase(str[0])) {
			if ("OpenGeoProver".equalsIgnoreCase(str[1])
					|| "Recio".equalsIgnoreCase(str[1])
					|| "Botana".equalsIgnoreCase(str[1])
					|| "alternativeBotana".equalsIgnoreCase(str[1])
					|| "PureSymbolic".equalsIgnoreCase(str[1])
					|| "Auto".equalsIgnoreCase(str[1])) {
				ProverSettings.proverEngine = str[1].toLowerCase();
				return;
			}
			App.warn("Option not recognized: ".concat(option));
			return;
		}
		if ("timeout".equalsIgnoreCase(str[0])) {
			ProverSettings.proverTimeout = Integer.parseInt(str[1]);
			return;
		}
		if ("maxTerms".equalsIgnoreCase(str[0])) {
			ProverSettings.maxTerms = Integer.parseInt(str[1]);
			return;
		}
		if ("method".equalsIgnoreCase(str[0])) {
			if ("Groebner".equalsIgnoreCase(str[1])
					|| "Wu".equalsIgnoreCase(str[1])
					|| "Area".equalsIgnoreCase(str[1])) {
				ProverSettings.proverMethod = str[1].toLowerCase();
				return;
			}
			App.warn("Method parameter not recognized: ".concat(option));
			return;
		}
		if ("fpnevercoll".equalsIgnoreCase(str[0])) {
			ProverSettings.freePointsNeverCollinear = Boolean.valueOf(str[1])
					.booleanValue();
			return;
		}
		if ("usefixcoords".equalsIgnoreCase(str[0])) {
			ProverSettings.useFixCoordinates = Boolean.valueOf(str[1])
					.booleanValue();
			return;
		}
		App.warn("Prover option not recognized: ".concat(option));
	}

	private static void setSingularWSOption(String option) {
		String[] str = option.split(":", 2);
		if ("enable".equalsIgnoreCase(str[0])) {
			SingularWSSettings.useSingularWebService = Boolean.valueOf(str[1])
					.booleanValue();
			return;
		}
		if ("remoteURL".equalsIgnoreCase(str[0])) {
			SingularWSSettings.singularWebServiceRemoteURL = str[1]
					.toLowerCase();
			return;
		}
		if ("timeout".equalsIgnoreCase(str[0])) {
			SingularWSSettings.singularWebServiceTimeout = Integer
					.parseInt(str[1]);
			return;
		}
		App.warn("Prover option not recognized: ".concat(option));
	}

	/**
	 * Reports if GeoGebra version check is allowed. The version_check_allowed
	 * preference is read to decide this, which can be set by the command line
	 * option --versionCheckAllow (off/on). For changing the behavior for a
	 * single run, the same command line option must be used with false/true
	 * parameters.
	 * 
	 * @return if the check is allowed
	 * @author Zoltan Kovacs <zoltan@geogebra.org>
	 */
	public boolean getVersionCheckAllowed() {

		if (isApplet() || isWebstart()) {
			return false;
		}

		return versionCheckAllowed;

	}

	private void handleOptionArgsEarly(CommandLineArguments args) {
		if (args == null) {
			return;
		}
		String language = args.getStringValue("language");
		if (language.length() > 0) {
			setLocale(getLocale(language));
		}
		if (args.containsArg("regressionFile")) {
			this.regressionFileName = args.getStringValue("regressionFile");
		}
		if (args.containsArg("prover")) {
			String[] proverOptions = args.getStringValue("prover").split(",");
			for (int i = 0; i < proverOptions.length; i++) {
				setProverOption(proverOptions[i]);
			}
		}
		if (args.containsArg("singularWS")) {
			String[] singularWSOptions = args.getStringValue("singularWS")
					.split(",");
			for (int i = 0; i < singularWSOptions.length; i++) {
				setSingularWSOption(singularWSOptions[i]);
			}
		}
	}

	// **************************************************************************
	// STATUS
	// **************************************************************************

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

	@Override
	final public boolean isApplet() {
		return isApplet;
	}

	public boolean isStandaloneApplication() {
		return !isApplet && (mainComp instanceof JFrame);
	}

	public boolean onlyGraphicsViewShowing() {
		if (!isUsingFullGui()) {
			return true;
		}

		return getGuiManager().getLayout().isOnlyVisible(App.VIEW_EUCLIDIAN);
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

	private static boolean virtualKeyboardActive = false;

	public static boolean isVirtualKeyboardActive() {
		return virtualKeyboardActive;
	}

	public static void setVirtualKeyboardActive(boolean active) {
		virtualKeyboardActive = active;
		// Application.debug("VK active:"+virtualKeyboardActive);
	}

	// **************************************************************************
	// File Handling
	// **************************************************************************

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
		}
		return null;
	}

	public static int getFileListSize() {
		return fileList.size();
	}

	public static String getExtension(File file) {
		String fileName = file.getName();
		return getExtension(fileName);
	}

	public static String getExtension(String fileName) {
		int dotPos = fileName.lastIndexOf('.');

		if ((dotPos <= 0) || (dotPos == (fileName.length() - 1))) {
			return "";
		}
		return fileName.substring(dotPos + 1).toLowerCase(Locale.US); // Michael
	}

	public static File addExtension(File file, String fileExtension) {
		if (file == null) {
			return null;
		}
		if (getExtension(file).equals(fileExtension)) {
			return file;
		}
		return new File(file.getParentFile(), // path
				file.getName() + '.' + fileExtension); // filename
	}

	public static File removeExtension(File file) {
		if (file == null) {
			return null;
		}
		String fileName = file.getName();
		int dotPos = fileName.indexOf('.');

		if (dotPos <= 0) {
			return file;
		}
		return new File(file.getParentFile(), // path
				fileName.substring(0, dotPos));
	}

	public static String removeExtension(String fileName) {
		if (fileName == null) {
			return null;
		}
		int dotPos = fileName.indexOf('.');

		if (dotPos <= 0) {
			return fileName;
		}
		return fileName.substring(0, dotPos);
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
			getGuiManager().clearInputbar();
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

		resetUniqueId();
	}

	private String regressionFileName = null;

	/**
	 * Creates the regression file for the current GGB file with the textual
	 * content of the algebra window, then exits.
	 * 
	 * @throws IOException
	 *             if the file is not writable
	 */
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

	/**
	 * This function helps determine if a ggt file was loaded because if a ggt
	 * file was loaded we will need to load something instead of the ggb
	 * 
	 * @return true if file is loading and is a ggt file
	 */
	private static boolean isLoadingTool(CommandLineArguments args) {
		if ((args == null) || (args.getNoOfFiles() == 0)) {
			return false;
		}
		String fileArgument = args.getStringValue("file0");
		String lowerCase = StringUtil.toLowerCase(fileArgument);
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
						geogebra.gui.app.GeoGebraFrame
								.createNewWindow(new CommandLineArguments(
										argsNew));
					}
				});
			} else {

				try {
					boolean success;
					String lowerCase = StringUtil.toLowerCase(fileArgument);
					String ext = getExtension(lowerCase);

					boolean isMacroFile = ext.equals(FILE_EXT_GEOGEBRA_TOOL);

					if (lowerCase.startsWith("http:")
							|| lowerCase.startsWith("https:")
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
								if (showConsProtNavigation
										|| !isJustEuclidianVisible()) {
									useFullGui = true;
								}
							}
						}
					} else if (ext.equals(FILE_EXT_HTM)
							|| ext.equals(FILE_EXT_HTML)) {
						loadBase64File(new File(fileArgument));
						success = true;
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

	/**
	 * loads an html file with <param name="ggbBase64" value="UEsDBBQACAAI...
	 * 
	 * @param file
	 *            html file
	 * @return success
	 */
	public boolean loadBase64File(final File file) {
		if (!file.exists()) {
			// show file not found message
			JOptionPane.showConfirmDialog(getMainComponent(),
					getError("FileNotFound") + ":\n" + file.getAbsolutePath(),
					getError("Error"), JOptionPane.DEFAULT_OPTION,
					JOptionPane.WARNING_MESSAGE);
			return false;
		}

		boolean success = false;

		setWaitCursor();
		// hide navigation bar for construction steps if visible
		setShowConstructionProtocolNavigation(false);

		try {
			success = loadFromHtml(file.toURI().toURL()); // file.toURL() does
			// not escape
			// illegal
			// characters
		} catch (Exception e) {
			setDefaultCursor();
			showError(getError("LoadFileFailed") + ":\n" + file);
			e.printStackTrace();
			return false;

		}
		// updateGUIafterLoadFile(success, false);
		setDefaultCursor();
		return success;

	}

	/**
	 * Tries to load a construction from the following sources in order:
	 * <ol>
	 * <li>
	 * From embedded base64 string
	 * <ol type="a">
	 * <li><code>&lt;article ... data-param-ggbbase64="..." /&gt;</code></li>
	 * <li><code>&lt;param name="ggbBase64" value="..." /&gt;</code></li>
	 * </ol>
	 * </li>
	 * <li>
	 * From relative referenced *.ggb file
	 * <ol type="a">
	 * <li><code>&lt;article ... data-param-filename="..." /&gt;</code></li>
	 * <li><code>&lt;param name="filename" value="..." /&gt;</code></li>
	 * </ol>
	 * </li>
	 * </ol>
	 * 
	 */
	public boolean loadFromHtml(URL htmlurl) throws IOException {
		URL url = htmlurl;
		String page = fetchPage(url);
		page = page.replaceAll("\\s+", " "); // Normalize white spaces
		page = page.replace('"', '\''); // Replace double quotes (") with single
		// quotes (')
		String lowerCasedPage = page.toLowerCase(Locale.US); // We must preserve
		// casing for
		// base64
		// strings and
		// case sensitve
		// file systems

		String val = getAttributeValue(page, lowerCasedPage,
				"data-param-ggbbase64='");
		val = val == null ? getAttributeValue(page, lowerCasedPage,
				"name='ggbbase64' value='") : val;

		if (val != null) { // 'val' is the base64 string
			byte[] zipFile = Base64.decode(val);

			return loadXML(zipFile);
		}

		val = getAttributeValue(page, lowerCasedPage, "data-param-filename='");
		val = val == null ? getAttributeValue(page, lowerCasedPage,
				"name='filename' value='") : val;

		if (val != null) { // 'val' is the relative path to *.ggb file
			String path = url.getPath(); // http://www.geogebra.org/mobile/test.html?test=true
			// -> path would be
			// '/mobile/test.html'
			int index = path.lastIndexOf('/');
			path = index == -1 ? path : path.substring(0, index + 1); // Remove
			// the
			// 'test.html'
			// part
			path += val; // Add filename
			URL fileUrl = new URL(url.getProtocol(), url.getHost(), path);

			return loadXML(fileUrl, false);
		}

		final String iframeURL1 = "<iframe src='http://www.geogebratube.org/material/iframe/id/";
		final String iframeURL2 = "<iframe src='http://ggbtu.be/e";

		// try loading from an embedded iframe
		int index = lowerCasedPage.indexOf(iframeURL1);
		if (index > -1) {
			index += iframeURL1.length();
		} else {
			index = lowerCasedPage.indexOf(iframeURL2);

			if (index > -1) {
				index += iframeURL2.length();
			}

		}

		if (index > -1) {
			StringBuilder sb = new StringBuilder(
					"http://www.geogebratube.org/material/download/format/file/id/");
			while (index < lowerCasedPage.length()
					&& Character.isDigit(lowerCasedPage.charAt(index))) {
				sb.append(lowerCasedPage.charAt(index));
				index++;
			}

			url = new URL(sb.toString());
			return loadXML(url, false);
		}

		return false;
	}

	private static String getAttributeValue(String page, String lowerCasedPage,
			String attrName) {
		int index;
		if (-1 != (index = lowerCasedPage.indexOf(attrName))) { // value='test.ggb'
			index += attrName.length();
			return getAttributeValue(page, index, '\''); // Search for next
			// single quote (')
		}
		attrName = attrName.replaceAll("'", "");
		if (-1 != (index = lowerCasedPage.indexOf(attrName))) { // value=filename_
			// or
			// value=filename>
			// ( ) or (>)
			index += attrName.length();
			return getAttributeValue(page, index, ' ', '>'); // Search for next
			// white space (
			// ) or angle
			// bracket (>)
		}
		return null;
	}

	private static String getAttributeValue(String page, int begin,
			char... attributeEndMarkers) {
		int end = begin;
		while (end < page.length()
				&& !isMarker(attributeEndMarkers, page.charAt(end))) {
			end++;
		}

		return end == page.length() || end == begin ? // attribute value not
		// terminated or empty
		null
				: page.substring(begin, end);
	}

	private static boolean isMarker(char[] markers, char character) {
		for (char m : markers) {
			if (m == character) {
				return true;
			}
		}
		return false;
	}

	private static String fetchPage(URL url) throws IOException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuilder page = new StringBuilder();
			String line;
			while (null != (line = reader.readLine())) {
				page.append(line); // page does not contain any line breaks
				// '\n', '\r' or "\r\n"
			}
			return page.toString();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	// **************************************************************************
	// VIEWS
	// **************************************************************************

	@Override
	public void setActiveView(int view) {
		if (getGuiManager() != null) {
			AppD2.setActiveView(this, view);
		}
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
			if ((panel.getViewId() == App.VIEW_EUCLIDIAN) && panel.isVisible()) {
				justEuclidianVisible = true;
			} else if (panel.isVisible()) {
				justEuclidianVisible = false;
				break;
			}
		}

		return justEuclidianVisible;
	}

	@Override
	public EuclidianView createEuclidianView() {
		return this.euclidianView;
	}

	@Override
	public EuclidianViewD getEuclidianView1() {
		return (EuclidianViewD) euclidianView;
	}

	@Override
	public AlgebraView getAlgebraView() {
		if (guiManager == null) {
			return null;
		}
		return (AlgebraView) guiManager.getAlgebraView();
	}

	@Override
	public EuclidianViewD getEuclidianView2() {
		return (EuclidianViewD) getGuiManager().getEuclidianView2();
	}

	@Override
	public boolean hasEuclidianView2() {
		return (guiManager != null) && getGuiManager().hasEuclidianView2();
	}

	@Override
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
		return (EuclidianViewND) getGuiManager().getActiveEuclidianView();
	}

	public void setShowAxesSelected(JCheckBoxMenuItem cb) {
		cb.setSelected(getGuiManager().getActiveEuclidianView().getShowXaxis()
				&& (getGuiManager().getActiveEuclidianView().getShowYaxis()));
	}

	public void setShowGridSelected(JCheckBoxMenuItem cb) {
		cb.setSelected(getGuiManager().getActiveEuclidianView().getShowGrid());
	}

	// **************************************************************************
	// ICONS & IMAGES
	// **************************************************************************

	@Override
	public ImageManager getImageManager() {
		return imageManager;
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

	/*
	 * needed for padding in Windows XP or earlier without check, checkbox isn't
	 * shown in Vista, Win 7
	 */
	public void setEmptyIcon(JCheckBoxMenuItem cb) {
		if (!WINDOWS_VISTA_OR_LATER) {
			cb.setIcon(getEmptyIcon());
		}
	}

	public ImageIcon getImageIcon(String filename, Color borderColor) {
		return imageManager
				.getImageIcon("/gui/images/" + filename, borderColor);
	}

	/**
	 * Attempt to return a flag to represent the current language
	 * 
	 * Not always possible to return a sensible value as there is not a 1-1
	 * correspondance between countries & languages
	 * 
	 * @param useGeoIP
	 *            whether to look up the country using a GeoIP service
	 * @return
	 * 
	 */
	public String getFlagName(boolean useGeoIP) {

		String country = Language.getCountry(this, getLocale().getLanguage(),
				getLocale().getCountry(), useGeoIP);

		// http://stackoverflow.com/questions/10175658/is-there-a-simple-way-to-get-the-language-code-from-a-country-code-in-php
		// http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2

		country = StringUtil.toLowerCase(country);

		String flag = country + ".png";

		return flag;
	}

	public ImageIcon getFlagIcon(String filename) {
		return imageManager.getImageIcon("/gui/menubar/images/" + filename,
				null);
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
	public geogebra.common.awt.GBufferedImage getExternalImageAdapter(
			String filename) {
		BufferedImage im = ImageManager.getExternalImage(filename);
		if (im == null)
			return null;
		return new geogebra.awt.GBufferedImageD(im);
	}

	@Override
	public geogebra.common.awt.GImage getInternalImageAdapter(String filename) {
		Image im = imageManager.getInternalImage(filename);
		if (im == null)
			return null;
		return new geogebra.awt.GGenericImageD(im);
	}

	public void addExternalImage(String filename, BufferedImage image) {
		imageManager.addExternalImage(filename, image);
	}

	public ImageIcon getModeIcon(int mode) {
		ImageIcon icon;

		Color border = Color.lightGray;

		// macro
		if (mode >= EuclidianConstants.MACRO_MODE_ID_OFFSET) {
			int macroID = mode - EuclidianConstants.MACRO_MODE_ID_OFFSET;
			try {
				Macro macro = kernel.getMacro(macroID);
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
				App.debug("macro does not exist: ID = " + macroID);
				return null;
			}
		} else {
			// standard case
			String modeText = getKernel().getModeText(mode);
			// bugfix for Turkish locale added Locale.US
			String iconName = "mode_" + StringUtil.toLowerCase(modeText)
					+ "_32.gif";
			icon = getToolBarImage(iconName, border);
			if (icon == null) {
				App.debug("icon missing for mode " + modeText + " (" + mode
						+ ")");
			}
		}
		return icon;
	}

	/**
	 * stores an image in the application's imageManager.
	 * 
	 * @return fileName of image stored in imageManager
	 */
	public String createImage(BufferedImage image, String imageFileName) {
		String fileName = imageFileName;
		BufferedImage img = image;
		try {
			// Michael Borcherds 2007-12-10 START moved MD5 code from GeoImage
			// to here
			String zip_directory = "";
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				if (img == null) {
					App.debug("image==null");
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
				App.debug("MD5 Error");
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
			((MyXMLio) myXMLio).writeImageToStream(os, fileName, img);
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
				}
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

			imageManager.addExternalImage(fileName, img);

			return fileName;
		} catch (Exception e) {
			setDefaultCursor();
			e.printStackTrace();
			showError("LoadFileFailed");
			return null;
		} catch (java.lang.OutOfMemoryError t) {
			App.debug("Out of memory");
			System.gc();
			setDefaultCursor();
			// t.printStackTrace();
			// TODO change to OutOfMemoryError
			showError("LoadFileFailed");
			return null;
		}
	}

	// **************************************************************************
	// PRINTING & EXPORT
	// **************************************************************************

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
			InputStream is = AppD.class.getResourceAsStream(s);
			BufferedReader br = new BufferedReader(new InputStreamReader(is,
					"UTF8"));
			String thisLine;
			while ((thisLine = br.readLine()) != null) {
				sb.append(thisLine);
				sb.append('\n');
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public void copyGraphicsViewToClipboard() {

		copyGraphicsViewToClipboard((EuclidianViewD) getGuiManager()
				.getActiveEuclidianView());
	}

	public void copyGraphicsViewToClipboard(final EuclidianViewD ev) {

		clearSelectedGeos(true, false);
		updateSelection(false);

		Thread runner = new Thread() {
			@Override
			public void run() {
				setWaitCursor();

				simpleExportToClipboard(ev);

				/*
				 * doesn't work in Win7, XP pasting into eg Paint pasting into
				 * eg Office 2010 is OK
				 * 
				 * 
				 * if (!WINDOWS_VISTA_OR_LATER) {
				 * 
				 * // use other method for WinXP or earlier //
				 * GraphicExportDialog.exportPNG() doesn't work well on XP // eg
				 * paste into Paint
				 * 
				 * simpleExportToClipboard(ev);
				 * 
				 * } else {
				 * 
				 * GraphicExportDialog export = new GraphicExportDialog(app);
				 * export.setDPI("300");
				 * 
				 * if (!export.exportPNG(true, false)) { // if there's an error
				 * (eg memory) just do a simple // export
				 * simpleExportToClipboard(ev);
				 * 
				 * } }
				 */

				setDefaultCursor();
			}
		};
		runner.start();

	}

	static void simpleExportToClipboard(EuclidianViewD ev) {
		double scale = 2d;
		double size = ev.getExportWidth() * ev.getExportHeight();

		// Windows XP clipboard has trouble with images larger than this
		// at double scale (with scale = 2d)
		if (size > 500000) {
			scale = 2.0 * Math.sqrt(500000 / size);
		}

		// copy drawing pad to the system clipboard
		Image img = ev.getExportImage(scale);
		geogebra.gui.util.ImageSelection imgSel = new geogebra.gui.util.ImageSelection(
				img);
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

	// TODO: maybe we want to implement this for EV2 as well
	public BufferedImage getExportImage(double maxX, double maxY)
			throws OutOfMemoryError {

		double scale = Math.min(maxX / getEuclidianView1().getSelectedWidth(),
				maxY / getEuclidianView1().getSelectedHeight());

		return getEuclidianView1().getExportImage(scale);
	}

	// **************************************************************************
	// LOCALES
	// **************************************************************************

	/**
	 * Creates a Locale object according to the given language code. The
	 * languageCode string should consist of two letters for the language, two
	 * letters for the country and two letters for the variant. E.g. "en" ...
	 * language: English , no country specified, "deAT" or "de_AT" ... language:
	 * German , country: Austria, "noNONY" or "no_NO_NY" ... language: Norwegian
	 * , country: Norway, variant: Nynorsk
	 * 
	 * @param languageISOCode
	 *            locale iso code (may contain _ or not)
	 * @return locale
	 */
	public static Locale getLocale(String languageISOCode) {
		// remove "_" from string
		String languageCode = languageISOCode.replaceAll("_", "");

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

	@Override
	public void setTooltipFlag() {
		if (tooltipLocale != null) {
			tooltipFlag = true;
		}
	}

	@Override
	public void setTooltipLanguage(String s) {

		Locale locale = null;

		for (int i = 0; i < getSupportedLocales().size(); i++) {
			if (getSupportedLocales().get(i).toString().equals(s)) {
				locale = getSupportedLocales().get(i);
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

	/**
	 * @return locale of tooltips
	 */
	public Locale getTooltipLanguage() {
		return tooltipLocale;
	}

	@Override
	public String getTooltipLanguageString() {
		if (tooltipLocale == null)
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

		updateLanguageFlags(locale.getLanguage());

	}

	/**
	 * Returns a locale object that has the same country and/or language as
	 * locale. If the language of locale is not supported an English locale is
	 * returned.
	 */
	private static Locale getClosestSupportedLocale(Locale locale) {
		int size = getSupportedLocales().size();

		// try to find country and variant
		String country = locale.getCountry();
		String variant = locale.getVariant();

		if (country.length() > 0) {
			for (int i = 0; i < size; i++) {
				Locale loc = getSupportedLocales().get(i);
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
			Locale loc = getSupportedLocales().get(i);
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

	/**
	 * @return current locale
	 */
	public Locale getLocale() {
		return currentLocale;
	}

	// **************************************************************************
	// PROPERTIES
	// **************************************************************************

	@Override
	final public String getColor(String key) {

		if (key == null) {
			return "";
		}

		if ((key.length() == 5)
				&& StringUtil.toLowerCase(key).startsWith("gray")) {
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
			return rbcolors.getString(StringUtil.toLowerCase(key));
		} catch (Exception e) {
			return key;
		}
	}

	@Override
	final public String reverseGetColor(String locColor) {
		String str = StringUtil.removeSpaces(StringUtil.toLowerCase(locColor));
		if (rbcolors == null) {
			initColorsResourceBundle();
		}

		try {

			Enumeration<String> enumer = rbcolors.getKeys();
			while (enumer.hasMoreElements()) {
				String key = enumer.nextElement();
				if (str.equals(StringUtil.removeSpaces(StringUtil
						.toLowerCase(rbcolors.getString(key))))) {
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

	@Override
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

	@Override
	final public String getSymbol(int key) {
		if (rbsymbol == null) {
			initSymbolResourceBundle();
		}

		String ret = null;

		try {
			ret = rbsymbol.getString("S." + key);
		} catch (Exception e) {
			// do nothing
		}

		if ("".equals(ret)) {
			return null;
		}
		return ret;
	}

	@Override
	final public String getSymbolTooltip(int key) {
		if (rbsymbol == null) {
			initSymbolResourceBundle();
		}

		String ret = null;

		try {
			ret = rbsymbol.getString("T." + key);
		} catch (Exception e) {
			// do nothing
		}

		if ("".equals(ret)) {
			return null;
		}
		return ret;
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

	@Override
	final public String getReverseCommand(String command) {
		initTranslatedCommands();

		String key = StringUtil.toLowerCase(command);
		try {

			Enumeration<String> enume = rbcommand.getKeys();

			// check localized commands first
			// eg in French we have Intersect -> Intersection, Intersection ->
			// Inter
			while (enume.hasMoreElements()) {
				String s = enume.nextElement();

				if (StringUtil.toLowerCase(rbcommand.getString(s)).equals(key)) {
					return s;
				}

			}

			enume = rbcommand.getKeys();

			// if that fails check internal commands
			for (Commands c : Commands.values()) {
				if (StringUtil.toLowerCase(c.name()).equals(key)) {
					return Commands.englishToInternal(c).name();
				}
			}

			return null;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	final public String getCommand(String key) {

		initTranslatedCommands();

		try {
			return rbcommand.getString(key);
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
	protected String getSyntaxString() {
		return syntaxStr;
	}

	/**
	 * @param key
	 *            command name
	 * @return CAS syntax
	 */
	public String getCommandSyntaxCAS(String key) {

		String command = getCommand(key);
		String syntax = getCommand(key + syntaxCAS);

		syntax = syntax.replace("[", command + '[');

		return syntax;
	}

	/**
	 * Gets particular setting for HTML export
	 * 
	 * @param key
	 *            key
	 * @return setting for given key
	 */
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

	/**
	 * @return whether properties bundles were initiated (at least plain)
	 */
	public boolean propertiesFilesPresent() {
		return rbplain != null;
	}

	/**
	 * Shows localized help message
	 * 
	 * @param key
	 *            key (for plain) to be localized
	 */
	public void showHelp(String key) {
		final String text = getPlain(key); // Michael Borcherds changed to use
		// getPlain() and removed try/catch

		JOptionPane.showConfirmDialog(mainComp, text,
				getPlain("ApplicationName") + " - " + getMenu("Help"),
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
	}

	// **************************************************************************
	// GUI Updates
	// **************************************************************************

	/**
	 * Updates the GUI of the main component.
	 */
	public void updateContentPane() {
		updateContentPane(true);
	}

	/**
	 * Updates the GUI of the frame and its size.
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

	}

	@Override
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
	 * 
	 * @return application panel
	 */
	public JPanel buildApplicationPanel() {

		JPanel applicationPanel = new JPanel(new BorderLayout());

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

			// create panels if empty

			if (northPanel == null) {
				northPanel = new JPanel(new BorderLayout(0, 0));
			}
			if (southPanel == null) {
				southPanel = new JPanel(new BorderLayout(0, 0));
			}
			if (eastPanel == null) {
				eastPanel = new JPanel(new BorderLayout(0, 0));
			}
			if (westPanel == null) {
				westPanel = new JPanel(new BorderLayout(0, 0));
			}

			if (dockBar == null) {
				dockBar = AppD2.newDockBar(this);
				dockBar.setEastOrientation(isDockBarEast);
			}

			// clear the panels
			northPanel.removeAll();
			southPanel.removeAll();
			eastPanel.removeAll();
			westPanel.removeAll();
			northPanel.removeAll();

			// create a JSplitPane with the center panel as the left component.
			// The right component is null initially, but can be used for
			// sliding a
			// help panel in/out of the center application panel.
			if (applicationSplitPane == null) {
				applicationSplitPane = new JSplitPane(
						JSplitPane.HORIZONTAL_SPLIT, centerPanel, null);
				applicationSplitPane.setBorder(BorderFactory
						.createEmptyBorder());
				// set all resize weight to the left pane
				applicationSplitPane.setResizeWeight(1.0);
				applicationSplitPane.setDividerSize(0);
			}

			// add north/south panels to center panel
			JPanel northSouthCenter = new JPanel(new BorderLayout());
			northSouthCenter.add(applicationSplitPane, BorderLayout.CENTER);
			northSouthCenter.add(northPanel, BorderLayout.NORTH);
			northSouthCenter.add(southPanel, BorderLayout.SOUTH);

			// add east/west panels to the northSouthCenter panel
			// (this puts them outside, not sandwiched between north/south)
			JPanel mainPanel = new JPanel(new BorderLayout());
			mainPanel.add(northSouthCenter, BorderLayout.CENTER);
			mainPanel.add(eastPanel, borderEast());
			mainPanel.add(westPanel, borderWest());

			applicationPanel.add(mainPanel, BorderLayout.CENTER);

			if (showDockBar && !isApplet()) {
				if (dockBar.isEastOrientation())
					applicationPanel.add((Component) dockBar, borderEast());
				else {
					applicationPanel.add((Component) dockBar, borderWest());
				}
			}

			// configure the panel components (adds toolbar, input bar, dockbar)
			updateApplicationLayout();

			// init labels
			setLabels();

			// Special case: return application panel with menubar
			// If the main component is a JPanel, we need to add the
			// menubar manually to the north
			if (showMenuBar() && (mainComp instanceof JPanel)) {
				return AppD2.getMenuBarPanel(this, applicationPanel);
			}

			// Standard case: return application panel
			return applicationPanel;
		}

		// Minimal applet case: return only the center panel with the EV
		applicationPanel.add(((EuclidianViewND) euclidianView).getJPanel(),
				BorderLayout.CENTER);
		centerPanel.add(applicationPanel, BorderLayout.CENTER);
		return applicationPanel;

	}

	/**
	 * Open/close the sidebar help panel for the input bar
	 */
	public void setShowInputHelpPanel(boolean isVisible) {
		if (isVisible) {
			applicationSplitPane
					.setRightComponent((Component) (getGuiManager())
							.getInputHelpPanel());
			if (applicationSplitPane.getLastDividerLocation() <= 0) {
				applicationSplitPane
						.setLastDividerLocation(applicationSplitPane.getWidth()
								- (getGuiManager())
										.getInputHelpPanelMinimumWidth());
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
			dockBar.update();
		}
	}

	/**
	 * Updates the configuration of the panels surrounding the main panel
	 * (toolbar, input bar etc.). This method should be called when the
	 * visibility or arrangement of these components is changed.
	 */
	public void updateApplicationLayout() {
		if ((northPanel == null) || (southPanel == null) || (eastPanel == null)
				|| (westPanel == null)) {
			return;
		}

		northPanel.removeAll();
		southPanel.removeAll();
		eastPanel.removeAll();
		westPanel.removeAll();

		// handle input bar
		if (showAlgebraInput) {
			AppD2.initInputBar(this, showInputTop, northPanel, southPanel);
		}

		if (showToolBar) {

			AppD2.initToolbar(this, toolbarPosition, showToolBarHelp,
					northPanel, eastPanel, southPanel, westPanel);

		}

	}

	public void updateCenterPanel(boolean updateUI) {
		if (centerPanel == null) {
			return;
		}

		centerPanel.removeAll();

		if (isUsingFullGui()) {
			centerPanel.add(AppD2.getRootComponent(this), BorderLayout.CENTER);
		} else {
			centerPanel.add(getEuclidianView1().getJPanel(),
					BorderLayout.CENTER);
		}

		if (updateUI) {
			SwingUtilities.updateComponentTreeUI(centerPanel);
		}
	}

	public void validateComponent() {
		if (isApplet) {
			appletImpl.getJApplet().validate();
		} else {
			frame.validate();
		}
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

	@Override
	public void updateMenubar() {
		if (!showMenuBar || !isUsingFullGui() || isIniting()) {
			return;
		}

		getGuiManager().updateMenubar();
		getGuiManager().updateActions();
		updateDockBar();
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
	}

	public void updateTitle() {
		if (frame == null) {
			return;
		}

		getGuiManager().updateFrameTitle();
	}

	// **************************************************************************
	// GUI Getters/Setters
	// **************************************************************************

	protected GuiManager newGuiManager() {
		return AppD2.newGuiManager(this);
	}

	/**
	 * @return this application's GUI manager.
	 */
	@Override
	final public synchronized GuiManager getGuiManager() {
		return guiManager;
	}

	final public static JApplet getJApplet() {
		if (appletImpl == null) {
			return null;
		}
		return appletImpl.getJApplet();
	}

	public synchronized JFrame getFrame() {
		if ((frame == null) && (getGuiManager() != null)) {
			frame = (JFrame) getGuiManager().createFrame();
		}

		return frame;
	}

	public Component getMainComponent() {
		return mainComp;
	}

	/**
	 * Returns the mainComp field which wrapped in geogebra.common.awt.Component
	 * 
	 * @return wrapped mainComp
	 */
	public geogebra.common.awt.Component wrapGetMainComponent() {
		return AwtFactory.prototype.newComponent(mainComp);
	}

	public Dimension getPreferredSize() {
		return preferredSize;
	}

	@Override
	public void setPreferredSize(geogebra.common.awt.GDimension size) {
		preferredSize = geogebra.awt.GDimensionD.getAWTDimension(size);
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

	public JPanel getCenterPanel() {
		return centerPanel;
	}

	@Override
	public boolean showAlgebraInput() {
		return showAlgebraInput;
	}

	public void setToolbarPosition(int position, boolean update) {
		toolbarPosition = position;
		if (update) {
			updateApplicationLayout();
			updateMenubar();
		}
	}

	public int getToolbarPosition() {
		return toolbarPosition;
	}

	public boolean showToolBarTop() {
		return showToolBarTop;
	}

	public boolean showToolBarHelp() {
		return showToolBarHelp;
	}

	public void setShowToolBarTop(boolean showToolBarTop) {
		if (this.showToolBarTop == showToolBarTop) {
			return;
		}

		this.showToolBarTop = showToolBarTop;
		if (!isIniting()) {
			updateApplicationLayout();
		}
	}

	public void updateToolBarLayout() {
		if (!isIniting()) {
			updateApplicationLayout();
			updateMenubar();
		}
	}

	public boolean showMenuBar() {
		return showMenuBar;
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
				&& (!showConstProtNavigationNeedsUpdate)) {
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

	public void toggleShowConstructionProtocolNavigation() {

		setShowConstructionProtocolNavigation(!showConsProtNavigation());
		setUnsaved();
		updateCenterPanel(true);
		updateMenubar();

		if (getGuiManager() != null)
			((GuiManagerD) getGuiManager())
					.updateCheckBoxesForShowConstructinProtocolNavigation();

	}

	public boolean showConsProtNavigation() {
		return showConsProtNavigation;
	}

	@Override
	public void setShowAuxiliaryObjects(boolean flag) {
		showAuxiliaryObjects = flag;

		if (getGuiManager() != null) {
			getGuiManager().setShowAuxiliaryObjects(flag);
			updateMenubar();
		}
	}

	private boolean dontShowDockBarPopup = false;

	private void hideDockBarPopup() {
		if (getDockBar() != null)
			getDockBar().hidePopup();
		else
			// prevent next automatic popup
			dontShowDockBarPopup = true;

	}

	public void showDockBarPopup() {
		if (dontShowDockBarPopup)
			dontShowDockBarPopup = false;
		else
			getDockBar().showPopup();
	}

	public DockBarInterface getDockBar() {
		return dockBar;
	}

	public boolean isShowDockBar() {
		return showDockBar;
	}

	public boolean isDockBarEast() {
		return isDockBarEast;
	}
	
	public void setDockBarEast(boolean isDockBarEast) {
		this.isDockBarEast = isDockBarEast;
		if(getDockBar() != null){
			dockBar.setEastOrientation(isDockBarEast);
		}
	}
	
	/**
	 * Set show dockBar with GUI update 
	 * @param showDockBar
	 */
	public void setShowDockBar(boolean showDockBar) {
		setShowDockBar(showDockBar, true);
	}
	
	public void setShowDockBar(boolean showDockBar, boolean update) {
		this.showDockBar = showDockBar;
		if (update) {
			updateContentPane();
		}
	}

	// ***************************************************************************
	// TOOL TIPS
	// **************************************************************************

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

	// ***************************************************************************
	// FONTS
	// **************************************************************************

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
	final public geogebra.common.awt.GFont getPlainFontCommon() {
		return new geogebra.awt.GFontD(fontManager.getPlainFont());
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
	@Override
	final public FontManagerD getFontManager() {
		return fontManager;
	}

	// ***************************************************************************
	// CURSORS
	// **************************************************************************

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
			getActiveEuclidianView().setCursor(waitCursor);
		}

		if (guiManager != null) {
			guiManager.allowGUIToRefresh();
		}
	}

	@Override
	public void setDefaultCursor() {
		mainComp.setCursor(Cursor.getDefaultCursor());
		if (euclidianView != null) {
			getEuclidianView1().setCursor(Cursor.getDefaultCursor());
		}
		if ((guiManager != null) && guiManager.hasEuclidianView2()) {
			((EuclidianViewND) guiManager.getEuclidianView2())
					.setDefaultCursor();
		}

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

			int size = 32;

			/*
			 * we need two buffered images as the cursor only supports on/off
			 * for alpha
			 * 
			 * so we need to draw to an image without alpha support then draw
			 * that to one with alpha support then make "white" transparent
			 */
			BufferedImage image = new BufferedImage(size, size,
					BufferedImage.TYPE_INT_RGB);
			BufferedImage image2 = new BufferedImage(size, size,
					BufferedImage.TYPE_INT_ARGB);

			Graphics2D g = image.createGraphics();
			Graphics2D g2 = image2.createGraphics();

			g.setColor(Color.white);
			g.fillRect(0, 0, size, size);

			// turn on anti-aliasing.
			g.setStroke(new BasicStroke(4.0f)); // 4-pixel lines
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			g.setColor(new Color(0.5f, 0f, 0f));
			g.drawOval(3, 3, size - 7, size - 7);

			g2.drawImage(image, 0, 0, Color.white, null);

			for (int y = 0; y < size; y++) {
				for (int x = 0; x < size; x++) {

					int rgb = image.getRGB(x, y);

					int blue = rgb & 0xff;
					int green = (rgb & 0xff00) >> 8;
					int red = (rgb & 0xff0000) >> 16;
					// int alpha = (rgb & 0xff000000) >> 24;

					if (red == 255 && green == 255 && blue == 255) {
						// make white transparent
						image2.setRGB(x, y, 0);
					}

				}
			}

			eraserCursor = Toolkit.getDefaultToolkit().createCustomCursor(
					image2, new Point(size / 2, size / 2), "eraserCursor");
		}
		return eraserCursor;
	}

	// **************************************************************************
	// EXIT
	// **************************************************************************

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

	
	
	// **************************************************************************
	// SAVE / LOAD
	// **************************************************************************

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

			// don't clear JavaScript here -- we may have just read one from the
			// file.
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
			setActiveView(App.VIEW_EUCLIDIAN);

			((MyXMLio) myXMLio).readZipFromString(zipFile);

			kernel.initUndoInfo();
			isSaved = true;
			setCurrentFile(null);
			// command list may have changed due to macros
			updateCommandDictionary();

			hideDockBarPopup();

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
			setActiveView(App.VIEW_EUCLIDIAN);

			myXMLio.processXMLString(xml, true, false);

			kernel.initUndoInfo();
			isSaved = true;
			setCurrentFile(null);
			// command list may have changed due to macros
			updateCommandDictionary();

			hideDockBarPopup();

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
			setActiveView(App.VIEW_EUCLIDIAN);

			// reset unique id (for old files, in case they don't have one)
			resetUniqueId();

			BufferedInputStream bis = new BufferedInputStream(is);
			((MyXMLio) myXMLio).readZipFromInputStream(bis, isMacroFile);
			is.close();
			bis.close();

			if (!isMacroFile) {
				kernel.initUndoInfo();
				isSaved = true;
				setCurrentFile(null);
			}

			// command list may have changed due to macros
			updateCommandDictionary();

			hideDockBarPopup();

			return true;
		} catch (MyError err) {
			setCurrentFile(null);
			showError(err);
			return false;
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
			((MyXMLio) myXMLio).writeGeoGebraFile(file);
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
			((MyXMLio) myXMLio).writeMacroFile(file, macros);
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
			setActiveView(App.VIEW_EUCLIDIAN);

			myXMLio.processXMLString(xml, clearAll, false);
		} catch (MyError err) {
			err.printStackTrace();
			showError(err);
		} catch (Exception e) {
			e.printStackTrace();
			showError("LoadFileFailed");
		}
	}



	public byte[] getMacroFileAsByteArray() {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			((MyXMLio) myXMLio).writeMacroStream(os, kernel.getAllMacros());
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
				((MyXMLio) myXMLio).readZipFromInputStream(is, true);
				is.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	final public MyXMLio getXMLio() {
		return (MyXMLio) myXMLio;
	}

	@Override
	public MyXMLio createXMLio(Construction cons) {
		return new MyXMLio(cons.getKernel(), cons);
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

	@Override
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
	final private static String packgz = ".pack.gz";

	private static void initCodeBase() {
		try {
			// application codebase
			String path = GeoGebra.class.getProtectionDomain().getCodeSource()
					.getLocation().toExternalForm();

			// remove .pack.gz from end
			// not sure why we've started getting this (maybe when using the
			// mirror servers?)
			// eg Codebase:
			// http://jars.geogebra.org/webstart/4.2/jnlp/geogebra.jar.pack.gz
			if (path.endsWith(packgz)) {
				path = path.substring(0, path.length() - packgz.length());
			}

			// remove "geogebra.jar" from end of codebase string
			if (path.endsWith(JAR_FILES[0])) {
				runningFromJar = true;
				path = path.substring(0, path.length() - JAR_FILES[0].length());
			}

			// Forcing 3D webstart alternative JAR name to detect:
			for (int i = 0; i < GEOGEBRA_JAR_ALT.length; ++i) {
				if (path.endsWith(GEOGEBRA_JAR_ALT[i])) {
					runningFromJar = true;
					path = path.substring(0, path.length()
							- GEOGEBRA_JAR_ALT[i].length());
				}
			}

			// set codebase
			codebase = new URL(path);
			hasFullPermissions = true;
		} catch (Exception e) {
			App.info("GeoGebra is running with restricted permissions.");
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

	// **************************************************************************
	// EVENT DISPATCHING
	// **************************************************************************

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

	// **************************************************************************
	// KEY EVENTS
	// **************************************************************************

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
		// no layout in applets
				&& (isApplet() || !AppD2.inExternalWindow(this, eventPane))) {
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
		} else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
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
	 * 
	 * @param isShiftDown
	 *            whether shift is pressed
	 */
	protected void handleShiftEvent(boolean isShiftDown) {
		// we may overwrite in subclasses
	}

	@Override
	final public GlobalKeyDispatcherD getGlobalKeyDispatcher() {
		if (globalKeyDispatcher == null) {
			globalKeyDispatcher = newGlobalKeyDispatcher();
		}
		return globalKeyDispatcher;
	}

	protected GlobalKeyDispatcherD newGlobalKeyDispatcher() {
		return new GlobalKeyDispatcherD(this);
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
		 * debug("MAC_OS = "+MAC_OS); debug("isMetaDown = "+e.isMetaDown());
		 * debug("isControlDown ="+e.isControlDown());
		 * debug("isShiftDown = "+e.isShiftDown());
		 * debug("isAltDown = "+e.isAltDown());
		 * debug("isAltGrDown ="+e.isAltGraphDown());
		 * debug("isPopupTrigger = "+e.isPopupTrigger());
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

	public static boolean isRightClickForceMetaDown(MouseEvent e) {

		boolean ret =
		// e.isPopupTrigger() ||
		(MAC_OS && e.isControlDown()) // Mac: ctrl click = right click
				|| (e.isMetaDown()); // non-Mac: right click = meta
		// click

		// debug("ret = " + ret);
		return ret;
		// return e.isMetaDown();

	}

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

	// **************************************************************************
	// ERROR HANDLING
	// **************************************************************************

	public final boolean isErrorDialogsActive() {
		return isErrorDialogsActive;
	}

	public final void setErrorDialogsActive(boolean isErrorDialogsActive) {
		this.isErrorDialogsActive = isErrorDialogsActive;
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

	/**
	 * Show error dialog with given message
	 * 
	 * @param msg
	 *            (localized) message
	 */
	public void showErrorDialog(final String msg) {
		if (!isErrorDialogsActive) {
			return;
		}

		// make sure splash screen not showing (will be in front)
		if (GeoGebra.splashFrame != null) {
			GeoGebra.splashFrame.setVisible(false);
		}

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

	/**
	 * @return whether there is an open error dialog
	 */
	public boolean isErrorDialogShowing() {
		return isErrorDialogShowing;
	}

	public void showMessage(final String message) {
		AppD.printStacktrace("showMessage: " + message);

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

	// Michael Borcherds 2008-06-22
	public static void printStacktrace(String message) {
		try {
			throw new Exception(message);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// **************************************************************************
	// SCRIPTING: GgbAPI/PYTHON
	// **************************************************************************

	/**
	 * PluginManager gets API with this H-P Ulven 2008-04-16
	 */
	@Override
	public GgbAPID getGgbApi() {
		if (ggbapi == null) {
			ggbapi = new GgbAPID(this);
		}

		return ggbapi;
	}

	@Override
	public PythonBridgeD getPythonBridge() {
		if (!pythonBridge.isReady()) {
			pythonBridge.init();
		}
		return pythonBridge;
	}

	public String getCurrentPythonScript() {
		String script = null;
		if (pythonBridge.isReady()) {
			script = pythonBridge.getCurrentPythonScript();
		}
		if (script == null) {
			return kernel.getLibraryPythonScript();
		}
		return script;
	}

	/*
	 * public String getCurrentLogoScript() { String script =
	 * pythonBridge.getCurrentLogoScript(); if (script == null) { return
	 * kernel.getLibraryLogoScript(); } }
	 */

	public boolean isPythonWindowVisible() {
		if (!pythonBridge.isReady()) {
			return false;
		}
		return getPythonBridge().isWindowVisible();
	}

	@Override
	public ScriptManagerD getScriptManager() {
		if (scriptManager == null) {
			scriptManager = new ScriptManagerD(this);
		}
		return (ScriptManagerD) scriptManager;
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

	/*
	 * current possible values http://mindprod.com/jgloss/properties.html AIX
	 * Digital Unix FreeBSD HP UX Irix Linux Mac OS Mac OS X MPE/iX Netware 4.11
	 * OS/2 Solaris Windows 2000 Windows 7 Windows 95 Windows 98 Windows NT
	 * Windows Vista Windows XP
	 */

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

	// **************************************************************************
	// LOGGING
	// **************************************************************************

	LogManager logManager;
	// String logFile = DownloadManager.getTempDir()+"GeoGebraLog.txt";
	// public String logFile = "c:\\GeoGebraLog.txt";
	public StringBuilder logFile = null;

	/*
	 * code from
	 * http://blogs.sun.com/nickstephen/entry/java_redirecting_system_out_and
	 */
	private void setUpLogging() {
		App.debug("Setting up logging");
		if (logger.getLogDestination() == LogDestination.FILE) {
			// File logging already set up, don't override:
			App.debug("Logging into explicitly defined file into GeoGebraLogger, not using LogManager");
			return;
		}

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

		App.debug("Logging is redirected to " + logFile.toString());
		App.logger.setTimeShown(false); // do not print the time twice

		// log file max size 10K, 1 file, append-on-open
		Handler fileHandler;
		try {
			fileHandler = new FileHandler(logFile.toString(),
					logger.LOGFILE_MAXLENGTH, 1, false);
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

	private void setUpFileLogging() {

		// initialize logging to go to rolling log file
		StringBuilder logFile = new StringBuilder(30);

		logFile.append(DownloadManager.getTempDir());
		logFile.append("GeoGebraLog_");
		// randomize filename
		for (int i = 0; i < 10; i++) {
			logFile.append((char) ('a' + Math.round(Math.random() * 25)));
		}
		logFile.append(".txt");

		logger.setLogDestination(LogDestination.FILE);
		logger.setLogFile(logFile.toString());
		App.debug(logFile.toString());
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

	public void exportToLMS(boolean ggbWeb) {
		clearSelectedGeos(true, false);
		updateSelection(false);
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
				new StringSelection(d.getAppletTag(this, width, height, false,
						true, ggbWeb, false)), null);
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
			if (contents.getTransferDataFlavors() != null
					&& contents.getTransferDataFlavors().length > 0)
				debug(contents.getTransferDataFlavors()[0]);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}

	// **************************************************************************
	// SOUNDS
	// **************************************************************************

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
					debug("checkCommands: " + s);
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

	DrawEquationD drawEquation;

	@Override
	public DrawEquationD getDrawEquation() {
		if (drawEquation == null) {
			drawEquation = new DrawEquationD();
		}
		return drawEquation;
	}

	
	
	
	// random id to identify ggb files
	// eg so that GeoGebraTube can notice it's a version of the same file
	private String uniqueId = "" + UUID.randomUUID();

	@Override
	public String getUniqueId() {
		return uniqueId;
	}

	@Override
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	@Override
	public void resetUniqueId() {
		uniqueId = "" + UUID.randomUUID();
	}

	// //////////////////////////////////
	// FILE VERSION HANDLING
	// //////////////////////////////////

	private DialogManager dialogManager;

	@Override
	public void callAppletJavaScript(String string, Object[] args) {
		getApplet().callJavaScript(string, args);
	}

	@Override
	public void evalPythonScript(App app, String pythonScript, String arg) {
		String script = arg != null ? "arg=" + arg + ";" + pythonScript
				: pythonScript;
		App.debug(script);
		getPythonBridge().eval(script);

	}

	@Override
	public boolean showView(int view) {
		return getGuiManager().showView(view);
	}

	@Override
	public String getLanguage() {
		return getLocale().getLanguage();
	}

	@Override
	public void evalJavaScript(App app, String script, String arg) {
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
	public geogebra.common.awt.GFont getFontCommon(boolean b, int i, int size) {
		return new geogebra.awt.GFontD(getFont(b, i, size));
	}

	public geogebra.common.awt.GFont getBoldFontCommon() {
		return new geogebra.awt.GFontD(getBoldFont());
	}

	@Override
	public void repaintSpreadsheet() {
		if (getGuiManager() != null && getGuiManager().hasSpreadsheetView()) {
			getGuiManager().getSpreadsheetView().repaint();
		}

	}

	@Deprecated
	@Override
	public UndoManagerD getUndoManager(Construction cons) {
		return new UndoManagerD(cons);
	}

	@Override
	public GeoElementGraphicsAdapter newGeoElementGraphicsAdapter() {
		return new GeoElementGraphicsAdapterDesktop(this);
	}

	@Override
	public AnimationManager newAnimationManager(Kernel kernel2) {
		return new AnimationManagerD(kernel2);
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
	public CommandProcessor newCmdBarCode() {
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
		String ret = internal;
		try {
			ret = rbcommandScripting.getString(internal);
		} catch (Exception e) {
			App.error(internal + " missing from command.properties");
		}
		return ret;
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
		return geogebra.awt.GFontD.getAwtFont(getFontCanDisplay(string, b,
				plain, i));
	}

	public Font getFontCanDisplayAwt(String string) {
		return geogebra.awt.GFontD.getAwtFont(getFontCanDisplay(string));
	}

	public Font getFontCanDisplayAwt(String value, int plain) {
		return geogebra.awt.GFontD.getAwtFont(getFontCanDisplay(value, plain));
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

	// don't pull these up to common, use the non static methods isWindows(),
	// isMacOS(), isWindowsVistaOrLater() instead
	private static String OS = System.getProperty("os.name").toLowerCase(
			Locale.US);
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

	@SuppressWarnings("deprecation")
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
		ggbtube.uploadWorksheet(null);
	}

	@Override
	protected LowerCaseDictionary newLowerCaseDictionary() {
		return new LowerCaseDictionary(Normalizer.getInstance());
	}

	public CommandLineArguments getCommandLineArgs() {
		return args;
	}

	public void resetPen() {

		getEuclidianView1().getEuclidianController().resetPen();

		if (hasEuclidianView2()) {
			getEuclidianView2().getEuclidianController().resetPen();
		}

	}

	@Override
	public String getCountryFromGeoIP() throws Exception {
		URL u = new URL("http://www.geogebra.org/geoip/");
		URLConnection uc = u.openConnection();
		uc.setReadTimeout(3000);
		BufferedReader in;
		in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
		return in.readLine(); // the last line will never get a "\n" on its end
	}

	/**
	 * 
	 * return East/West as appropriate for eg Hebrew / Arabic
	 * 
	 * return String rather than app.borderEast() so we're not dependent on awt
	 */
	public String borderEast() {
		// return app.borderEast();
		if (isRightToLeftReadingOrder()) {
			return "West";
		} else {
			return "East";
		}
	}

	/**
	 * 
	 * return East/West as appropriate for eg Hebrew / Arabic
	 * 
	 * return String rather than app.borderWest() so we're not dependent on awt
	 */
	public String borderWest() {
		// return app.borderWest();
		if (!isRightToLeftReadingOrder()) {
			return "West";
		} else {
			return "East";
		}
	}

	/**
	 * 
	 * return Left/Right as appropriate for eg Hebrew / Arabic
	 * 
	 * return int rather than FlowLayout.LEFT so we're not dependent on awt
	 */
	public int flowLeft() {
		if (!isRightToLeftReadingOrder()) {
			return 0; // left
		} else {
			return 2; // right
		}
	}

	/**
	 * 
	 * return Left/Right as appropriate for eg Hebrew / Arabic
	 * 
	 * return int rather than FlowLayout.RIGHT so we're not dependent on awt
	 */
	public int flowRight() {
		if (isRightToLeftReadingOrder()) {
			return 0; // left
		} else {
			return 2; // right
		}
	}

	/**
	 * @param c
	 *            component calling for repaint
	 */
	public void repaintEuclidianViews(Component c) {

		ComponentEvent event = new ComponentEvent(c,
				ComponentEvent.COMPONENT_RESIZED);
		getEuclidianView1().dispatchEvent(event);
		getEuclidianView2().dispatchEvent(event);

	}

	@Override
	public void addMenuItem(MenuInterface parentMenu, String filename,
			String name, boolean asHtml, MenuInterface subMenu) {

		((JMenuItem) subMenu).setIcon(getImageIcon(filename));
		((JMenuItem) subMenu).setText(name);
		((JMenu) parentMenu).add((JMenuItem) subMenu);

	}

	/**
	 * 
	 * @return eg Java 1.7.0_03-64bit
	 */
	public static String getJavaVersion() {
		return appendJavaVersion(new StringBuilder(19)).toString();
	}

	/**
	 * 
	 * @param sb
	 *            StringBuilder
	 * @return StringBuilder with eg "Java 1.7.0_03-64bit" added
	 */
	public static StringBuilder appendJavaVersion(StringBuilder sb) {
		sb.append(System.getProperty("java.version"));

		String arch = System.getProperty("os.arch");
		if (arch == null || "".equals(arch)) {
			return sb;
		}

		sb.append('-');

		if ("x86".equals(arch) || "ppc".equals(arch) || "i386".equals(arch)) {
			sb.append("32bit");
		} else if ("amd64".equals(arch) || "x86_64".equals(arch)) {
			sb.append("64bit");
		} else {
			sb.append(arch);
		}

		return sb;
	}

	@Override
	public NormalizerMinimal getNormalizer() {
		return Normalizer.getInstance();
	}

	@Override
	public void runScripts(final GeoElement geo1, final String string) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				geo1.runClickScripts(string);
			}
		});
	}

	@Override
	public CASFactory getCASFactory() {
		CASFactory ret = CASFactory.getPrototype();

		if (ret == null) {
			ret = new CASFactoryD();
			CASFactory.setPrototype(ret);
		}

		return ret;

	}

	@Override
	public SwingFactory getSwingFactory() {
		SwingFactory ret = SwingFactory.getPrototype();

		if (ret == null) {
			ret = new SwingFactoryD();
			SwingFactory.setPrototype(ret);
		}

		return ret;

	}

	@Override
	public Factory getFactory() {
		Factory ret = Factory.getPrototype();

		if (ret == null) {
			ret = new FactoryD();
			Factory.setPrototype(ret);
		}

		return ret;

	}

	// **************************************************************************
	// COMPONENT ORIENTATION
	// **************************************************************************

	public ComponentOrientation getComponentOrientation() {
		return isRightToLeftReadingOrder() ? ComponentOrientation.RIGHT_TO_LEFT
				: ComponentOrientation.LEFT_TO_RIGHT;
	}

	// renderer for JComboBox (align left/right)
	private ListCellRenderer renderer = new DefaultListCellRenderer();

	public void setComponentOrientation(Component c) {

		ComponentOrientation orientation = isRightToLeftReadingOrder() ? ComponentOrientation.RIGHT_TO_LEFT
				: ComponentOrientation.LEFT_TO_RIGHT;
		c.setComponentOrientation(orientation);
		// c.applyComponentOrientation(orientation);

		if (c instanceof JMenu) {
			JMenu menu = (JMenu) c;
			int ncomponents = menu.getMenuComponentCount();
			for (int i = 0; i < ncomponents; ++i) {
				setComponentOrientation(menu.getMenuComponent(i));
			}
		} else if (c instanceof JTextField) {
			((JTextField) c)
					.setHorizontalAlignment(isRightToLeftReadingOrder() ? JTextField.RIGHT
							: JTextField.LEFT);
		} else if (c instanceof JComboBox) {
			JComboBox cb = (JComboBox) c;
			((JLabel) renderer)
					.setHorizontalAlignment(isRightToLeftReadingOrder() ? SwingConstants.RIGHT
							: SwingConstants.LEFT);
			cb.setRenderer(renderer);
		} else if (c instanceof Container) {
			Container container = (Container) c;
			int ncomponents = container.getComponentCount();
			for (int i = 0; i < ncomponents; ++i) {
				setComponentOrientation(container.getComponent(i));
			}
		}

	}

	/**
	 * set a flow layout for the panel with correct orientation
	 * 
	 * @param panel
	 */
	public void setFlowLayoutOrientation(JPanel panel) {
		if (isRightToLeftReadingOrder())
			panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		else
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));
	}

	/**
	 * @return new EuclidianDockPanelForPlane
	 */
	public DockPanel createEuclidianDockPanelForPlane(int id, String plane) {
		return null;
	}

	// **************************************************************************
	// SINGULAR
	// **************************************************************************

	private class initializeSingularWS_thread implements Runnable {
		public initializeSingularWS_thread() {
		}

		public void run() {
			// Display info about this particular thread
			App.debug(Thread.currentThread() + " running");
			initializeSingularWS();
		}
	}

	public void initializeSingularWSD() {
		Thread t = new Thread(new initializeSingularWS_thread(), "compute");
		long startTime = System.currentTimeMillis();
		t.start();
		int i = 0;
		while (t.isAlive()) {
			App.debug("Waiting for the initialization: " + i++);
			try {
				t.join(250);
			} catch (InterruptedException e) {
				return;
			}
			if (((System.currentTimeMillis() - startTime) > SingularWSSettings.singularWebServiceTimeout * 1000L)
					&& t.isAlive()) {
				App.debug("SingularWS startup timeout");
				t.interrupt();
				// t.join(); //
				// http://docs.oracle.com/javase/tutorial/essential/concurrency/simple.html
				return;
			}
		}
	}

	// **************************************************************************
	// ConstructionProtocol
	// **************************************************************************

	private ConstructionProtocolNavigation constProtocolNavigation;

	public ConstructionProtocolNavigation getConstructionProtocolNavigation() {
		if (constProtocolNavigation == null) {
			constProtocolNavigation = new ConstructionProtocolNavigation(this);
		}

		return constProtocolNavigation;
	}
}
