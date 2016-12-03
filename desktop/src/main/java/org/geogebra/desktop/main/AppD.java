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
package org.geogebra.desktop.main;

import java.awt.AWTKeyStroke;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
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
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
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

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.GeoGebraConstants.Versions;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianCursor;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.factories.CASFactory;
import org.geogebra.common.factories.Factory;
import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.factories.LaTeXFactory;
import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.io.OFFHandler;
import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.io.layout.PerspectiveDecoder;
import org.geogebra.common.javax.swing.GImageIcon;
import org.geogebra.common.jre.factory.FormatFactoryJre;
import org.geogebra.common.jre.util.Base64;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.barycentric.AlgoCubicSwitch;
import org.geogebra.common.kernel.barycentric.AlgoKimberlingWeights;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.geos.AnimationExportSlider;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import org.geogebra.common.main.AlgoCubicSwitchInterface;
import org.geogebra.common.main.AlgoCubicSwitchParams;
import org.geogebra.common.main.AlgoKimberlingWeightsInterface;
import org.geogebra.common.main.AlgoKimberlingWeightsParams;
import org.geogebra.common.main.App;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.ProverSettings;
import org.geogebra.common.main.SingularWSSettings;
import org.geogebra.common.main.SpreadsheetTableModel;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.move.ggtapi.models.json.JSONTokener;
import org.geogebra.common.move.ggtapi.operations.OpenFromGGTOperation;
import org.geogebra.common.plugin.SensorLogger;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.Charsets;
import org.geogebra.common.util.CopyPaste;
import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimer.GTimerListener;
import org.geogebra.common.util.Language;
import org.geogebra.common.util.LowerCaseDictionary;
import org.geogebra.common.util.NormalizerMinimal;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.Util;
import org.geogebra.common.util.debug.Log;
import org.geogebra.common.util.debug.Log.LogDestination;
import org.geogebra.desktop.CommandLineArguments;
import org.geogebra.desktop.GeoGebra;
import org.geogebra.desktop.awt.GBufferedImageD;
import org.geogebra.desktop.awt.GDimensionD;
import org.geogebra.desktop.awt.GFontD;
import org.geogebra.desktop.euclidian.DrawEquationD;
import org.geogebra.desktop.euclidian.EuclidianControllerD;
import org.geogebra.desktop.euclidian.EuclidianViewD;
import org.geogebra.desktop.euclidian.event.MouseEventD;
import org.geogebra.desktop.euclidian.event.MouseEventND;
import org.geogebra.desktop.euclidianND.EuclidianViewInterfaceD;
import org.geogebra.desktop.export.GeoGebraTubeExportD;
import org.geogebra.desktop.factories.AwtFactoryD;
import org.geogebra.desktop.factories.CASFactoryD;
import org.geogebra.desktop.factories.FactoryD;
import org.geogebra.desktop.factories.LaTeXFactoryD;
import org.geogebra.desktop.factories.UtilFactoryD;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.MyImageD;
import org.geogebra.desktop.gui.app.GeoGebraFrame;
import org.geogebra.desktop.gui.dialog.AxesStyleListRenderer;
import org.geogebra.desktop.gui.dialog.DashListRenderer;
import org.geogebra.desktop.gui.dialog.PointStyleListRenderer;
import org.geogebra.desktop.gui.dialog.options.OptionsAdvancedD;
import org.geogebra.desktop.gui.inputbar.AlgebraInputD;
import org.geogebra.desktop.gui.layout.DockBar;
import org.geogebra.desktop.gui.layout.DockPanelD;
import org.geogebra.desktop.gui.layout.LayoutD;
import org.geogebra.desktop.gui.toolbar.ToolbarContainer;
import org.geogebra.desktop.gui.toolbar.ToolbarD;
import org.geogebra.desktop.gui.util.ImageSelection;
import org.geogebra.desktop.io.MyXMLioD;
import org.geogebra.desktop.io.OFFReader;
import org.geogebra.desktop.javax.swing.GImageIconD;
import org.geogebra.desktop.kernel.UndoManagerD;
import org.geogebra.desktop.kernel.geos.GeoElementGraphicsAdapterD;
import org.geogebra.desktop.move.ggtapi.models.AuthenticationModelD;
import org.geogebra.desktop.move.ggtapi.models.LoginOperationD;
import org.geogebra.desktop.plugin.GgbAPID;
import org.geogebra.desktop.plugin.ScriptManagerD;
import org.geogebra.desktop.plugin.UDPLoggerD;
import org.geogebra.desktop.sound.SoundManagerD;
import org.geogebra.desktop.util.FrameCollector;
import org.geogebra.desktop.util.GTimerD;
import org.geogebra.desktop.util.GuiResourcesD;
import org.geogebra.desktop.util.ImageManagerD;
import org.geogebra.desktop.util.ImageResourceD;
import org.geogebra.desktop.util.LoggerD;
import org.geogebra.desktop.util.Normalizer;
import org.geogebra.desktop.util.StringUtilD;
import org.geogebra.desktop.util.UtilD;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class AppD extends App implements KeyEventDispatcher {

	/**
	 * License file
	 */
	public static final String LICENSE_FILE = "/org/geogebra/desktop/_license.txt";

	/**
	 * Command line arguments
	 */
	protected CommandLineArguments args;

	// ==============================================================
	// JAR fields
	// ==============================================================

	/**
	 * Possible alternative names for geogebra.jar. Used for 3D webstart at the
	 * moment.
	 */
	public final static String[] GEOGEBRA_JAR_ALT = { "geogebra.jar",
			"geogebra-jogl2.jar" };

	// ==============================================================
	// LOCALE fields
	// ==============================================================

	// ==============================================================
	// FILE fields
	// ==============================================================

	private static LinkedList<File> fileList = new LinkedList<File>();
	protected File currentPath, currentImagePath, currentFile = null;

	/** maximum number of files to (save &) show in File -> Recent submenu */
	public static final int MAX_RECENT_FILES = 8;

	// ==============================================================
	// RESOURCE fields
	// ==============================================================

	private ResourceBundle rbcommandEnglish, rbmenuEnglish, rbsettings;

	private final LocalizationD loc;

	private static final String RB_SETTINGS = "/org/geogebra/desktop/export/settings";

	// ==============================================================
	// APPLET fields
	// ==============================================================

	private static volatile AppletImplementation appletImpl;
	private static Object lock = new Object();
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

	protected boolean showAlgebraView = true;

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


	/**
	 * made a little darker in ggb40 (problem showing on some projectors)
	 */
	public static final Color COLOR_SELECTION = new Color(210, 210, 225);

	// ==============================================================
	// MODEL & MANAGER fields
	// ==============================================================

	private final FontManagerD fontManager;

	/** GUI manager */
	protected GuiManagerInterfaceD guiManager;

	private GlobalKeyDispatcherD globalKeyDispatcher;

	protected ImageManagerD imageManager;

	private GgbAPID ggbapi = null;
	private SpreadsheetTableModelD tableModel;

	private AuthenticationModelD authenticationModel;

	// ==============================================================
	// MISC FLAGS
	// ==============================================================

	private boolean printScaleString = false;

	private boolean allowToolTips = true;

	protected boolean isErrorDialogShowing = false;

	public boolean macsandbox = false;

	/*************************************************************
	 * Construct application within JFrame
	 * 
	 * @param args
	 * @param frame
	 * @param undoActive
	 */
	public AppD(CommandLineArguments args, JFrame frame, boolean undoActive) {
		this(args, frame, null, null, undoActive, new LocalizationD(2));
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
		this(args, null, appletImpl, null, undoActive, new LocalizationD(2));
	}

	/*************************************************************
	 * Construct application within Container (e.g. GeoGebraPanel)
	 * 
	 * @param args
	 * @param comp
	 * @param undoActive
	 */
	public AppD(CommandLineArguments args, Container comp, boolean undoActive) {
		this(args, null, null, comp, undoActive, new LocalizationD(2));
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
	public AppD(CommandLineArguments args, JFrame frame,
			AppletImplementation appletImpl, Container comp,
			boolean undoActive, LocalizationD loc) {

		super(Versions.DESKTOP);

		this.loc = loc;
		loc.setApp(this);
		this.args = args;

		if (args != null && !args.containsArg("silent")) {
			Log.setLogger(new LoggerD());
			Log.setLogDestination(LogDestination.CONSOLE);
			if (args.containsArg("logLevel")) {
				Log.setLogLevel(args.getStringValue("logLevel"));
			}
			if (args.containsArg("logFile")) {
				Log.setLogDestination(LogDestination.FILE);
				Log.setLogFile(args.getStringValue("logFile"));
			}
			if (args.containsArg("logShowCaller")) {
				Log.setCallerShown(args.getBooleanValue("logShowCaller", true));
			}
			if (args.containsArg("logShowTime")) {
				Log.setTimeShown(args.getBooleanValue("logShowTime", true));
			}
			if (args.containsArg("logShowLevel")) {
				Log.setLevelShown(args.getBooleanValue("logShowLevel", true));
			}
		}
		this.prerelease = args != null
				&& (args.containsArg("prerelease") || args
						.containsArg("canary"));
		this.canary = args != null && args.containsArg("canary");

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

		Log.debug("isApplet=" + isApplet + " runningFromJar=" + runningFromJar
				+ " appletImpl=" + appletImpl);
		if (!isApplet && runningFromJar) {
			setUpLogging();
		} else {
			Log.debug("Not setting up logging via LogManager");
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
		settings = companion.newSettings();

		// init euclidian view
		initEuclidianViews();

		// load file on startup and set fonts
		// set flag to avoid multiple calls of setLabels() and
		// updateContentPane()
		initing = true;
		// setFontSize(12);

		// This is needed because otherwise Exception might come and
		// GeoGebra may exit. (dockPanel not entirely defined)
		// This is needed before handleFileArg because
		// we don't want to redefine the toolbar string from the file.
		boolean ggtloading = isLoadingTool(args);

		// init xml io for construction loading
		myXMLio = new MyXMLioD(kernel, kernel.getConstruction());

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

		// here we initialize SingularWS
		// for a better approach see [22746] --- but it would break file loading
		// at the moment
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
				imageManager.setMaxIconSizeAsPt(getFontSize());
			}

			if (MAC_OS) {
				String path = System.getProperty("user.home") + "/Documents";
				if (currentPath == null) {
					currentPath = new File(path);
				}
				if (currentImagePath == null) {
					currentImagePath = new File(path);
				}
			}
		}

		if (isUsingFullGui() && (tmpPerspectives != null)) {
			getGuiManager().getLayout().setPerspectives(
					tmpPerspectives,
					PerspectiveDecoder.decode(this.perspectiveParam,
							getKernel().getParser(),
							ToolBar.getAllToolsNoMacros(false, false, this)));
		}

		if (needsSpreadsheetTableModel) {
			// if tableModel==null, will create one
			getSpreadsheetTableModel();
		}

		if (isUsingFullGui() && ggtloading) {
			getGuiManager().setToolBarDefinition(
ToolbarD.getAllTools(this));
		}

		setUndoActive(undoActive);

		// applet/command line options
		handleOptionArgs(args);

		initing = false;

		// for key listening
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.addKeyEventDispatcher(this);

		if (!isApplet()) {
			getScriptManager().ggbOnInit();
			getFactory();
		}

		setSaved();

		if (getCASVersionString().equals("")) {
			setCASVersionString(loc.getMenu("CASInitializing"));

		}

		if (!isApplet()) {
			// user authentication handling
			initSignInEventFlow();
		}

	}

	// **************************************************************************
	// INIT
	// **************************************************************************

	public void setApplet(AppletImplementation appletImpl0) {
		isApplet = true;
		synchronized (lock) {
			AppD.appletImpl = appletImpl0;
		}
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
			loadFile(this, currentFile, false);
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
		frame.setIconImage(getInternalImage(GuiResourcesD.GEOGEBRA64));

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
			Log.debug(e + "");
		}
	}

	/**
	 * Toggles between the system LAF and the cross-platform LAF
	 */
	public static void toggleCrossPlatformLAF() {
		setLAF(!UIManager.getLookAndFeel().isNativeLookAndFeel());
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

	private static void handleHelpVersionArgs(CommandLineArguments args) {

		System.out.println("GeoGebra " + GeoGebraConstants.VERSION_STRING + " "
				+ GeoGebraConstants.BUILD_DATE + " Java " + getJavaVersion()
				+ "\n");

		if (args.containsArg("help")) {
			// help message
			System.out
					.println("Usage: java -jar geogebra.jar [OPTION] [FILE]\n"
							+ "Start GeoGebra with the specified OPTIONs and open the given FILE.\n"
							+ "  --help\t\tprint this message\n"
							+ "  --v\t\tprint version\n"
							+ "  --language=LANGUAGE_CODE\t\tset language using locale strings, e.g. en, de, de_AT, ...\n" // here
																															// "auto"
																															// is
																															// also
																															// accepted
							+ "  --showAlgebraInput=BOOLEAN\tshow/hide algebra input field\n"
							+ "  --showAlgebraInputTop=BOOLEAN\tshow algebra input at top/bottom\n"
							+ "  --showAlgebraWindow=BOOLEAN\tshow/hide algebra window\n"
							+ "  --showSpreadsheet=BOOLEAN\tshow/hide spreadsheet\n"
					+ "  --showCAS=BOOLEAN\tshow/hide CAS window\n" // here
																	// "disable"
																	// is also
																	// accepted
							+ "  --show3D=BOOLEAN\tshow/hide 3D window\n" // here
																			// "disable"
																			// is
																			// also
																			// accepted
							+ "  --showSplash=BOOLEAN\tenable/disable the splash screen\n"
							+ "  --enableUndo=BOOLEAN\tenable/disable Undo\n"
							+ "  --fontSize=NUMBER\tset default font size\n"
							+ "  --showAxes=BOOLEAN\tshow/hide coordinate axes\n"
							+ "  --showGrid=BOOLEAN\tshow/hide grid\n"
							+ "  --settingsFile=PATH|FILENAME\tload/save settings from/in a local file\n"
							+ "  --resetSettings\treset current settings\n"
							+ "  --regressionFile=FILENAME\texport textual representations of dependent objects, then exit\n"
							+ "  --versionCheckAllow=SETTING\tallow version check (on/off or true/false for single launch)\n"
							+ "  --logLevel=LEVEL\tset logging level (EMERGENCY|ALERT|CRITICAL|ERROR|WARN|NOTICE|INFO|DEBUG|TRACE)\n"
							+ "  --logFile=FILENAME\tset log file\n"
							+ "  --silent\tCompletely mute logging\n"
							+ "  --prover=OPTIONS\tSet options for the prover subsystem (use --proverhelp for more information)\n"
					/*
					 * +
					 * "  --singularWS=OPTIONS\tSet options for SingularWS (use --singularWShelp for more information)\n"
					 */
					);

			AppD.exit(0);
		}
		if (args.containsArg("proverhelp")) {
			ProverSettings proverSettings = ProverSettings.get();
			// help message for the prover
			System.out
					.println("  --prover=OPTIONS\tset options for the prover subsystem\n"
							+ "    where OPTIONS is a comma separated list, formed with the following available settings (defaults in brackets):\n"
							+ "      engine:ENGINE\tset engine (Auto|OpenGeoProver|Recio|Botana|PureSymbolic) ["
									+ proverSettings.proverEngine
							+ "]\n"
							+ "      timeout:SECS\tset the maximum time attributed to the prover (in seconds) ["
									+ proverSettings.proverTimeout
							+ "]\n"
							+ "      maxterms:NUMBER\tset the maximal number of terms ["
									+ proverSettings.maxTerms
							+ "] (OpenGeoProver only)\n"
							+ "      method:METHOD\tset the method (Wu|Groebner|Area) ["
									+ proverSettings.proverMethod
							+ "] (OpenGeoProver/Recio only)\n"
							/*
							 * +
							 * "      fpnevercoll:BOOLEAN\tassume three free points are never collinear for Prove ["
							 * + ProverSettings.freePointsNeverCollinear +
							 * "] (Botana only, forced to 'yes' when SingularWS is unavailable)\n"
							 */
							+ "      usefixcoords:NUMBER1NUMBER2\tuse fix coordinates for the first NUMBER1 for Prove and NUMBER2 for ProveDetails, maximum of 4 both ["
									+ proverSettings.useFixCoordinatesProve
									+ proverSettings.useFixCoordinatesProveDetails
							+ "] (Botana only)\n"
							/*
							 * +
							 * "      transcext:BOOLEAN\tuse polynomial ring with coeffs from a transcendental extension for Prove ["
							 * + ProverSettings.transcext +
							 * "] (Botana only, needs SingularWS)\n"
							 */
							+ "      captionalgebra:BOOLEAN\tshow algebraic debug information in object captions ["
									+ proverSettings.captionAlgebra
							+ "] (Botana only)\n"
							+ "  Example: --prover=engine:Botana,timeout:10,fpnevercoll:true,usefixcoords:43\n");
			AppD.exit(0);
		}
		if (args.containsArg("singularWShelp")) {
			// help message for singularWS
			System.out
					.println(" --singularWS=OPTIONS\tset options for SingularWS\n"
							+ "   where OPTIONS is a comma separated list, formed with the following available settings (defaults in brackets):\n"
							+ "      enable:BOOLEAN\tuse Singular WebService when possible ["
									+ SingularWSSettings.useSingularWebService()
							+ "]\n"
							+ "      remoteURL:URL\tset the remote server URL ["
									+ SingularWSSettings
											.getSingularWebServiceRemoteURL()
							+ "]\n"
							+ "      timeout:SECS\tset the timeout ["
									+ SingularWSSettings.getTimeout()
							+ "]\n"
							+ "      caching:BOOLEAN\tset server side caching ["
							+ SingularWSSettings.getCachingText()
							+ "]\n"
							+ "  Example: singularWS=timeout:3\n");
			AppD.exit(0);
		}
		if (args.containsArg("v")) {
			AppD.exit(0);
		}
	}

	@Override
	public EuclidianController newEuclidianController(Kernel kernel1) {
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
		imageManager = new ImageManagerD(component);
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

		newLayout(this);
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
			if (showAlgebraInputTop) {
				setInputPosition(InputPosition.top, false);
			}
		}

		String fontSize = args.getStringValue("fontSize");
		if (fontSize.length() > 0) {
			setFontSize(Util.getValidFontSize(Integer.parseInt(fontSize)), true);
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

		if (args.containsArg("giacJSONtests")) {

			// set CAS timeout to 13 seconds
			kernel.getApplication().getSettings().getCasSettings()
					.setTimeoutMilliseconds(13000);

			String filename = args.getStringValue("giacJSONtests");

			int count = 0;

			ArrayList<String> errors = new ArrayList<String>();

			// Open the file
			FileInputStream fstream;
			try {
				fstream = new FileInputStream(filename);
				BufferedReader br = new BufferedReader(new InputStreamReader(
						fstream, Charsets.UTF_8));

				String strLine;

				// Read File Line By Line
				while ((strLine = br.readLine()) != null
						&& (strLine.indexOf("JSONSTART") == -1)) {
					// Print the content on the console
					//System.out.println("IGNORE " + strLine);
				}
				

				while ((strLine = br.readLine()) != null
						&& (strLine.indexOf("JSONEND") == -1)) {
					// Print the content on the console

					strLine = strLine.trim();

					if (strLine.endsWith(",")) {
						strLine = strLine.substring(0, strLine.length() - 1);
					}
					// System.out.println(strLine);

					if (strLine.startsWith("{")) {
						
						count++;

						JSONTokener tokener = new JSONTokener(strLine);
						JSONObject response = new JSONObject(tokener);
						String command = (String) response.get("cmd");
						String result = (String) response.get("result");
						String category = (String) response.get("cat");
						// String notes = (String) response.get("notes");
						// JSONObject responseArray =
						// response.getJSONObject("cmd");

						// System.out.println("response = " + response);
						// System.out.println("result = " + result);

						//command = "Solve[13^(x+1)-2*13^x=(1/5)*5^x,x]";
						//result = "{-ln(55)/ln(13/5)}|OR|{x=(-ln(11)-ln(5))/(ln(13)-ln(5))}";

						String casResult = getGgbApi().evalGeoGebraCAS(command);

						String casResultOriginal = casResult;

						// remove spaces
						casResult = casResult.replace(" ", "");
						result = result.replace(" ", "");

						// sort out arbitrary constants
						result = result.replaceAll("n_[0-9]*", "n_0");
						result = result.replaceAll("c_[0-9]*", "c_0");

						casResult = casResult.replaceAll(
								"arbconst\\([+0-9]*\\)",
								"c_0");

						casResult = casResult.replaceAll(
								"arbint\\(([+0-9]*)\\)",
								"n_0");

						String[] results = {result};
						
						if (result.indexOf("|OR|") > -1) {
							results = result.split("\\|OR\\|");
						}

						boolean OK = false;

						// check if one of the answers matches
						for (int i = 0; i < results.length; i++) {
							if (casResult.equals(results[i])) {
								OK = true;
								break;
							}
						}

						if (OK || "GEOGEBRAERROR".equals(result)
								|| "RANDOM".equals(result)) {
							Log.debug("OK " + count);
						} else {

							String error = "\n\nnot OK " + count + "\ncmd = "
									+ command + "\ndesired result= "
									+ StringUtil.toJavaString(result)
									+ "\nactual result = "
									+ StringUtil
											.toJavaString(casResultOriginal);
							
							// to auto-fill answers for new test-cases
							// error = "{ cat:\"Integral\", cmd:\"";
							// error += StringUtil.toJavaString(command);
							// error += "\", result:\"";
							// error +=
							// StringUtil.toJavaString(casResultOriginal);
							// error += "\", notes:\"from Giac's tests\" },\n";

							Log.error(error);
							errors.add(error);
							
							

						}

					}
					
					

					


				}

				br.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Log.error("CAS TESTS ENDED. Total tests run = " + count
					+ ". Failed = " + errors.size());

			Iterator<String> it = errors.iterator();
			while (it.hasNext()) {
				System.out.println(it.next());
			}

			AppD.exit(0);

		}

		boolean macSandbox = args.getBooleanValue("macSandbox", false);
		if (macSandbox) {
			this.macsandbox = true;
		}

		setVersionCheckAllowed(args.getStringValue("versionCheckAllow"));

	}

	@SuppressFBWarnings({ "DM_EXIT", "" })
	public static void exit(int i) {
		System.exit(i);
		;

	}

	private static boolean versionCheckAllowed = true;

	private void setVersionCheckAllowed(String versionCheckAllow) {

		if (isApplet()) {
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
			Log.warn("Option versionCheckAllow not recognized : "
					.concat(versionCheckAllow));
		}

		versionCheckAllowed = GeoGebraPreferencesD.getPref()
				.loadVersionCheckAllow("true");

	}

	private static void setProverOption(String option) {
		String[] str = option.split(":", 2);
		ProverSettings proverSettings = ProverSettings.get();
		if ("engine".equalsIgnoreCase(str[0])) {
			if ("OpenGeoProver".equalsIgnoreCase(str[1])
					|| "Recio".equalsIgnoreCase(str[1])
					|| "Botana".equalsIgnoreCase(str[1])
					|| "alternativeBotana".equalsIgnoreCase(str[1])
					|| "PureSymbolic".equalsIgnoreCase(str[1])
					|| "Auto".equalsIgnoreCase(str[1])) {
				proverSettings.proverEngine = str[1].toLowerCase();
				return;
			}
			Log.warn("Option not recognized: ".concat(option));
			return;
		}
		if ("timeout".equalsIgnoreCase(str[0])) {
			proverSettings.proverTimeout = Integer.parseInt(str[1]);
			return;
		}
		if ("maxTerms".equalsIgnoreCase(str[0])) {
			proverSettings.maxTerms = Integer.parseInt(str[1]);
			return;
		}
		if ("method".equalsIgnoreCase(str[0])) {
			if ("Groebner".equalsIgnoreCase(str[1])
					|| "Wu".equalsIgnoreCase(str[1])
					|| "Area".equalsIgnoreCase(str[1])) {
				proverSettings.proverMethod = str[1].toLowerCase();
				return;
			}
			Log.warn("Method parameter not recognized: ".concat(option));
			return;
		}
		if ("fpnevercoll".equalsIgnoreCase(str[0])) {
			proverSettings.freePointsNeverCollinear = Boolean
					.parseBoolean(str[1]);
			return;
		}
		if ("usefixcoords".equalsIgnoreCase(str[0])) {
			int fixcoordsP = Integer.parseInt(str[1].substring(0, 1));
			int fixcoordsPD = Integer.parseInt(str[1].substring(1, 2));

			if (fixcoordsP < 0 || fixcoordsP > 4)
				Log.error("Improper value for usefixcoords for Prove, using default instead");
			else
				proverSettings.useFixCoordinatesProve = fixcoordsP;

			if (fixcoordsPD < 0 || fixcoordsPD > 4)
				Log.error("Improper value for usefixcoords for ProveDetails, using default instead");
			else
				proverSettings.useFixCoordinatesProveDetails = fixcoordsPD;

			return;
		}
		if ("transcext".equalsIgnoreCase(str[0])) {
			proverSettings.transcext = Boolean.parseBoolean(str[1]);
			return;
		}
		if ("captionalgebra".equalsIgnoreCase(str[0])) {
			proverSettings.captionAlgebra = Boolean.parseBoolean(str[1]);
			return;
		}
		Log.warn("Prover option not recognized: ".concat(option));
	}

	private static void setSingularWSOption(String option) {
		String[] str = option.split(":", 2);
		if ("enable".equalsIgnoreCase(str[0])) {
			SingularWSSettings.setUseSingularWebService(
					Boolean.valueOf(str[1]).booleanValue());
			return;
		}
		if ("remoteURL".equalsIgnoreCase(str[0])) {
			SingularWSSettings
					.setSingularWebServiceRemoteURL(str[1].toLowerCase());
			return;
		}
		if ("timeout".equalsIgnoreCase(str[0])) {
			SingularWSSettings.setTimeout(Integer.parseInt(str[1]));
			return;
		}
		if ("caching".equalsIgnoreCase(str[0])) {
			SingularWSSettings.setCachingFromText(str[1]);
			return;
		}
		Log.warn("Prover option not recognized: ".concat(option));
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

		if (isApplet()) {
			return false;
		}

		return versionCheckAllowed;

	}

	protected void handleOptionArgsEarly(CommandLineArguments args) {
		if (args == null) {
			return;
		}

		if (args.containsArg("showCAS")) {
			String showCASs = args.getStringValue("showCAS");
			if (showCASs.equalsIgnoreCase("disable")) {
				disableCASView();
				getSettings().getCasSettings().setEnabled(false);
			}
		}

		if (args.containsArg("show3D")) {
			String show3Ds = args.getStringValue("show3D");
			if (show3Ds.equalsIgnoreCase("disable")) {
				disable3DView();
			}
		}

		String language = args.getStringValue("language");
		if (language.length() > 0) {
			if ("Auto".equalsIgnoreCase(language)) {
				Locale systemLocale = Locale.getDefault();
				setLocale(systemLocale);
			} else {
				setLocale(getLocale(language));
			}
		}
		boolean eg = args.getBooleanValue("enableGraphing", true);
		kernel.getAlgebraProcessor().setCommandsEnabled(eg);
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

	@Override
	public void setCurrentFile(Object file) {
		if (currentFile == file) {
			return;
		}

		currentFile = (File) file;
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

	@Override
	public void createNewWindow() {
		GeoGebraFrame.createNewWindow(args.getGlobalArguments());
	}

	@Override
	public void fileNew() {

		// clear all
		// triggers the "do you want to save" dialog
		// so must be called first
		if (!clearConstruction()) {
			return;
		}

		// clear input bar
		if (isUsingFullGui() && showAlgebraInput()) {
			getGuiManager().clearInputbar();
		}

		// reset spreadsheet columns, reset trace columns
		if (isUsingFullGui()) {
			getGuiManager().resetSpreadsheet();
			getGuiManager().resetCasView();
		}

		resetEVs();

		resetAllToolbars();

		// reload the saved/(default) preferences
		GeoGebraPreferencesD.getPref().loadXMLPreferences(this);
		resetUniqueId();
	}

	private void resetAllToolbars() {

		GuiManagerD gm = (GuiManagerD) getGuiManager();

		DockPanelD[] panels = gm.getLayout().getDockManager().getPanels();
		for (DockPanelD panel : panels) {
			if (panel.canCustomizeToolbar()) {
				panel.setToolbarString(panel.getDefaultToolbarString());
			}
		}

		gm.setToolBarDefinition(gm.getDefaultToolbarString());
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

		BufferedWriter regressionFileWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(regressionFile),
						"UTF-8"));

		kernel.updateConstruction();
		regressionFileWriter.append(getXMLio().getConstructionRegressionOut());
		regressionFileWriter.close();
		AppD.exit(0);
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
		return lowerCase.endsWith(FileExtensions.GEOGEBRA_TOOL.toString());
	}

	/**
	 * Opens a file specified as last command line argument
	 * 
	 * @return true if a file was loaded successfully
	 */
	private boolean handleFileArg(final CommandLineArguments args) {
		if ((args == null) || (args.getNoOfFiles() == 0)) {
			return false;
		}

		boolean successRet = true;

		for (int i = 0; i < args.getNoOfFiles(); i++) {

			final String fileArgument = args.getStringValue("file" + i);
			final String key = "file0";

			if (i > 0) { // load in new Window
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {

						GeoGebraFrame.createNewWindow(args
								.getGlobalArguments().add(key, fileArgument));
					}
				});
			} else {

				try {
					boolean success;
					String lowerCase = StringUtil.toLowerCase(fileArgument);
					FileExtensions ext = StringUtil
							.getFileExtension(lowerCase);

					boolean isMacroFile = ext
							.equals(FileExtensions.GEOGEBRA_TOOL);

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
								if (showConsProtNavigation()
										|| !isJustEuclidianVisible()) {
									useFullGui = true;
								}
							}
						}
					} else if (lowerCase.startsWith("base64://")) {

						// substring to strip off base64://
						byte[] zipFile = Base64
								.decode(fileArgument.substring(9));
						success = loadXML(zipFile);

						if (success && !isMacroFile) {
							if (!isUsingFullGui()) {
								if (showConsProtNavigation()
										|| !isJustEuclidianVisible()) {
									useFullGui = true;
								}
							}
						}
					} else if (ext.equals(FileExtensions.HTM)
							|| ext.equals(FileExtensions.HTML)) {
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
			JOptionPane.showConfirmDialog(getMainComponent(), getLocalization()
					.getError("FileNotFound") + ":\n" + file.getAbsolutePath(),
					getLocalization().getError("Error"),
					JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
			return false;
		}

		boolean success = false;

		setWaitCursor();
		// hide navigation bar for construction steps if visible
		setHideConstructionProtocolNavigation();

		try {
			success = loadFromHtml(file.toURI().toURL()); // file.toURL() does
			// not escape
			// illegal
			// characters
		} catch (Exception e) {
			setDefaultCursor();
			showError(getLocalization().getError("LoadFileFailed") + ":\n"
					+ file);
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
			reader = new BufferedReader(
					new InputStreamReader(url.openStream(), Charsets.UTF_8));
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
			setActiveView(this, view);
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
	public EuclidianViewD getEuclidianView2(int idx) {
		return (EuclidianViewD) getGuiManager().getEuclidianView2(idx);
	}

	@Override
	public boolean hasEuclidianView2(int idx) {
		return (guiManager != null) && getGuiManager().hasEuclidianView2(idx);
	}

	@Override
	public boolean hasEuclidianView2EitherShowingOrNot(int idx) {
		return (guiManager != null)
				&& getGuiManager().hasEuclidianView2EitherShowingOrNot(1);
	}

	@Override
	public boolean isShowingEuclidianView2(int idx) {
		return (guiManager != null) && getGuiManager().hasEuclidianView2(idx)
				&& getGuiManager().getEuclidianView2(idx).isShowing();
	}

	@Override
	public EuclidianView getActiveEuclidianView() {
		if (getGuiManager() == null) {
			return getEuclidianView1();
		}
		return getGuiManager().getActiveEuclidianView();
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
	public ImageManagerD getImageManager() {
		return imageManager;
	}



	@Override
	public void setGUIFontSize(int size) {

		// TRAC-4770
		if (size != -1) {
			// set tool icon size between 32 and 64
			imageManager.setMaxIconSize(Math.max(32, size * 2));
		}

		super.setGUIFontSize(size);
	}

	@Override
	public void setFontSize(int points, boolean update) {

		if (guiFontSize == -1) {
			// set tool icon size between 32 and 64
			imageManager.setMaxIconSizeAsPt(points);
		}

		super.setFontSize(points, update);
	}

	

	public ImageIcon getImageIcon(ImageResourceD res) {
		return imageManager.getImageIcon(res, null);
	}

	public ImageIcon getScaledIcon(ImageResourceD res) {
		return getScaledIcon(res, null);
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

	protected String getMenuIconPath() {
		// int fontSize = getGUIFontSize();
		String path = "/gui/images/64px/";
		// if (fontSize < 30) {
		// path += "20px/";
		// } else if (fontSize >= 30 && fontSize < 79.5) {
		// path += "40px/";
		// } else if (fontSize >= 79.5) {
		// path += "106px/";
		// }
		//
		return path;
	}

	public static int ptToPx(int points) {
		int px = 0;
		switch (points) {
		case 12:
		case 14:
		case 16:
			px = 16;
			break;
		case 18:
		case 20:
		case 24:
		case 28:
			px = 24;
			break;
		case 32:
		case 48:
			px = 48;
			break;

		}
		return px;
	}

	public int getScaledIconSize() {
		return ptToPx(getFontSize());
	}

	public ImageIcon getScaledIcon(ImageResourceD res, Color borderColor) {
		ImageIcon icon = imageManager.getImageIcon(
				res,
				borderColor);
		return scaleIcon(icon, getScaledIconSize());
	}

	public ImageIcon getScaledIconCommon(ImageResourceD res) {
		ImageIcon icon = imageManager
				.getImageIcon(res, null);
		return scaleIcon(icon, getScaledIconSize());
	}

	public ImageIcon getScaledIcon(ImageResourceD res, int iconSize) {
		ImageIcon icon = imageManager.getImageIcon(
				res, null);
		return scaleIcon(icon, iconSize);
	}

	private ImageIcon scaleIcon(ImageIcon icon, int iconSize) {
		if (icon == null || iconSize == 0) {
			return null;
		}
		Image img = icon.getImage().getScaledInstance(iconSize, iconSize,
				Image.SCALE_SMOOTH);
		return new ImageIcon(img);

	}

	public Image getScaledInternalImage(ImageResourceD fileName) {
		MyImageD img = imageManager
				.getInternalImage(fileName);
		int iconSize = getScaledIconSize();
		return img.getImage().getScaledInstance(iconSize, iconSize, 0);
	}
	
	/**
	 * Attempt to return a flag to represent the current language
	 * 
	 * Not always possible to return a sensible value as there is not a 1-1
	 * correspondence between countries & languages
	 * 
	 * @return
	 * 
	 */
	public String getFlagName() {

		String country = Language.getCountry(this, getLocale().getLanguage(),
				getLocale().getCountry());

		// http://stackoverflow.com/questions/10175658/is-there-a-simple-way-to-get-the-language-code-from-a-country-code-in-php
		// http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2

		country = StringUtil.toLowerCase(country);

		String flag = country + ".png";

		return flag;
	}



	public ImageIcon getScaledFlagIcon(String filename) {
		ImageIcon icon = imageManager.getFlagIcon(filename);
		if (isMacOS()) {
			return icon;
		}
		return scaleIcon(icon, getScaledIconSize());
	}


	public ImageIcon getToolBarImage(String modeText, Color borderColor) {

		ImageIcon icon = imageManager.getImageIcon(
						imageManager.getToolImageResource(modeText, false),
				borderColor);

		/*
		 * mathieu 2010-04-10 see ImageManager3D.getImageResourceGeoGebra() if
		 * (icon == null) { // load3DJar(); // try to find this image in 3D
		 * extension path = "org/geogebra/desktop/geogebra3D/images/" + filename; icon =
		 * imageManager.getImageIcon(path, borderColor); }
		 */

		if (icon == null) {
			icon = getToolIcon(borderColor);
		}

		// scale icon if necessary
		icon = ImageManagerD.getScaledIcon(icon,
				Math.min(icon.getIconWidth(), imageManager.getMaxIconSize()),
				Math.min(icon.getIconHeight(), imageManager.getMaxIconSize()));

		return icon;
	}

	public ImageIcon getToolIcon(Color border) {
		ImageResourceD res;
		if (imageManager.getMaxIconSize() <= 32) {
			res = GuiResourcesD.TOOL_MODE32;
		} else {
			res = GuiResourcesD.TOOL_MODE64;
		}

		return imageManager.getImageIcon(res, border);
	}

	public ImageIcon getEmptyIcon() {
		return imageManager.getImageIcon(GuiResourcesD.EMPTY);
	}

	public Image getInternalImage(ImageResourceD filename) {
		return imageManager.getInternalImage(filename)
				.getImage();
	}

	public Image getRefreshViewImage() {
		// don't need to load gui jar as reset image is in main jar
		return getMenuInternalImage(GuiResourcesD.VIEW_REFRESH);
	}

	public Image getPlayImage() {
		// don't need to load gui jar as reset image is in main jar
		return imageManager.getInternalImage(GuiResourcesD.NAV_PLAY).getImage();
	}

	public Image getPlayImageCircle() {
		// don't need to load gui jar as reset image is in main jar
		return imageManager.getInternalImage(
				GuiResourcesD.NAV_PLAY_CIRCLE)
				.getImage();
	}

	public Image getPlayImageCircleHover() {
		// don't need to load gui jar as reset image is in main jar
		return imageManager.getInternalImage(
				GuiResourcesD.NAV_PLAY_HOVER)
				.getImage();
	}

	public Image getPauseImageCircle() {
		// don't need to load gui jar as reset image is in main jar
		return imageManager.getInternalImage(
				GuiResourcesD.NAV_PAUSE_CIRCLE)
				.getImage();
	}

	public Image getPauseImageCircleHover() {
		// don't need to load gui jar as reset image is in main jar
		return imageManager.getInternalImage(
				GuiResourcesD.NAV_PAUSE_CIRCLE_HOVER)
				.getImage();
	}

	public Image getPauseImage() {
		// don't need to load gui jar as reset image is in main jar
		return imageManager.getInternalImage(GuiResourcesD.NAV_PAUSE)
				.getImage();
	}

	public MyImageD getExternalImage(String filename) {
		return ImageManagerD.getExternalImage(filename);
	}

	@Override
	public final MyImage getExternalImageAdapter(String filename, int width,
			int height) {
		MyImageD im = ImageManagerD.getExternalImage(filename);
		return im;
	}

	public void addExternalImage(String filename, MyImageD image) {
		imageManager.addExternalImage(filename, image);
	}

	@Override
	public GImageIcon wrapGetModeIcon(int mode) {
		return new GImageIconD(getModeIcon(mode));
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
				MyImageD img = getExternalImage(iconName);
				if (img == null || img.isSVG()) {
					// default icon
					icon = getToolIcon(border);
				} else {
					// use image as icon
					int size = imageManager.getMaxIconSize();
					icon = new ImageIcon(ImageManagerD.addBorder(img.getImage()
							.getScaledInstance(size, -1, Image.SCALE_SMOOTH),
							border));
				}
			} catch (Exception e) {
				Log.debug("macro does not exist: ID = " + macroID);
				return null;
			}
		} else {
			// standard case
			String modeText = EuclidianConstants.getModeTextSimple(mode);
			// bugfix for Turkish locale added Locale.US
			icon = getToolBarImage(modeText, border);
			if (icon == null) {
				Log.debug("icon missing for mode " + modeText + " (" + mode
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
	public String createImage(MyImageD image, String imageFileName) {
		String fileName = imageFileName;
		MyImageD img = image;
		try {
			String zip_directory = img.getMD5();

			String fn = fileName;
			int index = fileName.lastIndexOf(File.separator);
			if (index != -1) {
				fn = fn.substring(index + 1, fn.length()); // filename without
			}
			// path
			fn = Util.processFilename(fn);

			// filename will be of form
			// "a04c62e6a065b47476607ac815d022cc/filename.ext"
			fileName = zip_directory + "/" + fn;

			/*
			 * 
			 * // write and reload image to make sure we can save it // without
			 * problems ByteArrayOutputStream os = new ByteArrayOutputStream();
			 * getXMLio().writeImageToStream(os, fileName, img); os.flush();
			 * ByteArrayInputStream is = new
			 * ByteArrayInputStream(os.toByteArray());
			 * 
			 * // reload the image img = ImageIO.read(is); is.close();
			 * os.close();
			 * 
			 * 
			 * 
			 * setDefaultCursor(); if (img == null) {
			 * showError("LoadFileFailed"); return null; }
			 */
			// make sure this filename is not taken yet
			MyImageD oldImg = ImageManagerD.getExternalImage(fileName);
			if (oldImg != null) {
				// image with this name exists already
				if ((oldImg.getWidth() == img.getWidth())
						&& (oldImg.getHeight() == img.getHeight())) {
					// same size and filename => we consider the images as equal
					return fileName;
				}
				// same name but different size: change filename
				// this bit of code should now be
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
				} while (ImageManagerD.getExternalImage(fileName) != null);
			}

			imageManager.addExternalImage(fileName, img);

			return fileName;
		} catch (Exception e) {
			setDefaultCursor();
			e.printStackTrace();
			showError("LoadFileFailed");
			return null;
		} catch (java.lang.OutOfMemoryError t) {
			Log.debug("Out of memory");
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
					Charsets.UTF_8));
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

	@Override
	public void copyGraphicsViewToClipboard() {

		copyGraphicsViewToClipboard(getGuiManager().getActiveEuclidianView());
	}


	@Override
	public void copyBase64ToClipboard() {

		// don't include preview bitmap
		Toolkit.getDefaultToolkit()
				.getSystemClipboard()
				.setContents(new StringSelection(getGgbApi().getBase64(false)),
						null);
	}

	@Override
	public void copyFullHTML5ExportToClipboard() {

		Toolkit.getDefaultToolkit().getSystemClipboard()
				.setContents(new StringSelection(getFullHTML5ExportString()),
						null);
	}

	public void copyGraphicsViewToClipboard(final EuclidianView euclidianView) {

		getSelectionManager().clearSelectedGeos(true, false);
		updateSelection(false);

		Thread runner = new Thread() {
			@Override
			public void run() {
				setWaitCursor();

				simpleExportToClipboard(euclidianView);

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

	static void simpleExportToClipboard(EuclidianView ev) {
		double scale = getMaxScaleForClipBoard(ev);

		// copy drawing pad to the system clipboard
		Image img = GBufferedImageD.getAwtBufferedImage(((EuclidianViewD) ev)
				.getExportImage(scale));
		ImageSelection imgSel = new ImageSelection(
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

	/**
	 * @param maxX
	 *            maximum width
	 * @param maxY
	 *            maximum height
	 * @return preview image
	 * @throws OutOfMemoryError
	 *             error
	 */
	public BufferedImage getExportImage(double maxX, double maxY)
			throws OutOfMemoryError {

		return GBufferedImageD
				.getAwtBufferedImage(getActiveEuclidianViewExportImage(maxX,
						maxY));
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
	public void setTooltipLanguage(String s) {

		boolean updateNeeded = loc.setTooltipLanguage(s);

		updateNeeded = updateNeeded || (loc.getTooltipLocale() != null);

		if (updateNeeded) {
			setLabels(); // update eg Tooltips for Toolbar
		}

	}

	@Override
	public int getTooltipTimeout() {
		int dmd = ToolTipManager.sharedInstance().getDismissDelay();
		if ((dmd <= 0) || (dmd == Integer.MAX_VALUE)) {
			return -1;
		}
		dmd /= 1000;
		for (int i = 0; i < (OptionsAdvancedD.tooltipTimeoutsLength()
				- 1); i++) {
			if (Integer.parseInt(OptionsAdvancedD.tooltipTimeouts(i)) >= dmd) {
				return Integer.parseInt(OptionsAdvancedD.tooltipTimeouts(i));
			}
		}
		return Integer
				.parseInt(
						OptionsAdvancedD.tooltipTimeouts(
								OptionsAdvancedD.tooltipTimeoutsLength() - 2));
	}

	@Override
	public void setLanguage(String s) {
		String[] parts = s.split("_");
		String language = parts[0];
		String country = parts.length > 1 ? parts[1] : null;
		Locale loc = null;
		if (language != null) {
			if (country != null) {
				loc = new Locale(language, country);
			} else {
				loc = new Locale(language);
			}
		}
		setLocale(loc);
	}
	/**
	 * set language via iso language string
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
		getKernel().updateConstructionLanguage();
		setUnsaved();

		setLabels(); // update display
		setOrientation();
	}

	StringBuilder testCharacters = new StringBuilder();

	public void setLocale(Locale locale) {
		if (locale == loc.getLocale()) {
			return;
		}
		Locale oldLocale = loc.getLocale();

		// only allow special locales due to some weird server
		// problems with the naming of the property files
		loc.setLocale(locale);

		// update font for new language (needed for e.g. chinese)
		try {
			fontManager.setLanguage(loc.getLocale());
		} catch (Exception e) {
			e.printStackTrace();
			showError(e.getMessage());

			// go back to previous locale
			loc.setLocale(oldLocale);
		}

		getLocalization().updateLanguageFlags(locale.getLanguage());

	}

	/**
	 * @return current locale
	 */
	public Locale getLocale() {
		return loc.getLocale();
	}

	@Override
	final public String getEnglishCommand(String key) {

		if (rbcommandEnglish == null) {
			rbcommandEnglish = MyResourceBundle.createBundle(
					LocalizationD.RB_COMMAND, Locale.ENGLISH);
		}

		try {
			return rbcommandEnglish.getString(key);
		} catch (Exception e) {
			return key;
		}
	}

	final public String getEnglishMenu(String key) {

		if (rbmenuEnglish == null) {
			rbmenuEnglish = MyResourceBundle.createBundle(
					LocalizationD.RB_MENU, Locale.ENGLISH);
		}
		try {
			return rbmenuEnglish.getString(key);
		} catch (Exception e) {
			return key;
		}
	}

	/**
	 * Shows localized help message
	 * 
	 * @param key
	 *            key (for plain) to be localized
	 */
	public void showHelp(String key) {
		final String text = loc.getMenu(key);

		JOptionPane.showConfirmDialog(mainComp, text,
				GeoGebraConstants.APPLICATION_NAME + " - " + getMenu("Help"),
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
		} else if ((frame != null) && (frame == mainComp)) {
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

		if (hasEuclidianView2(1)) {
			getEuclidianView2(1).updateSize();
		}

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
			kernel.setViewsLabels();
		}

		if (loc.propertiesFilesPresent()) {
			kernel.updateLocalAxesNames();
		}

		updateCommandDictionary();
	}

	private void setOrientation() {
		if (initing) {
			return;
		}
		if (guiManager != null) {
			kernel.setViewsOrientation();
		}
	}

	protected void updateComponentTreeUI() {
		if (isApplet()) {
			SwingUtilities.updateComponentTreeUI(appletImpl.getJApplet());
		} else if ((frame != null) && (frame == mainComp)) {
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
				dockBar = newDockBar(this);
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
			mainPanel.add(eastPanel, getLocalization().borderEast());
			mainPanel.add(westPanel, getLocalization().borderWest());

			applicationPanel.add(mainPanel, BorderLayout.CENTER);

			if (showDockBar && !isApplet()) {
				if (dockBar.isEastOrientation())
					applicationPanel.add((Component) dockBar, getLocalization()
							.borderEast());
				else {
					applicationPanel.add((Component) dockBar, getLocalization()
							.borderWest());
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
				return getMenuBarPanel(this, applicationPanel);
			}

			resetFonts();
			// Standard case: return application panel
			return applicationPanel;
		}

		// Minimal applet case: return only the center panel with the EV
		applicationPanel.add(
				((EuclidianViewInterfaceD) euclidianView).getJPanel(),
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

	/**
	 * Updates the configuration of the panels surrounding the main panel
	 * (toolbar, input bar etc.). This method should be called when the
	 * visibility or arrangement of these components is changed.
	 */
	@Override
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
			initInputBar(this, getInputPosition() == InputPosition.top,
					northPanel,
					southPanel);
		}

		if (showToolBar) {
			initToolbar(this, getToolbarPosition(), showToolBarHelp,
					northPanel, eastPanel, southPanel, westPanel);
		}

		if (frame != null && frame.getContentPane() != null) {
			frame.getContentPane().validate();
		}

	}

	@Override
	public void updateCenterPanel(boolean updateUI) {
		if (centerPanel == null) {
			return;
		}

		centerPanel.removeAll();

		if (isUsingFullGui()) {
			centerPanel.add(getRootComponent(this), BorderLayout.CENTER);
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

	protected GuiManagerInterfaceD newGuiManager() {
		return newGuiManager(this);
	}

	/**
	 * @return this application's GUI manager.
	 */
	@Override
	final public synchronized GuiManagerInterfaceD getGuiManager() {
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

	public Dimension getPreferredSize() {
		return preferredSize;
	}

	@Override
	public void setPreferredSize(GDimension size) {
		preferredSize = GDimensionD.getAWTDimension(size);
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

	@Override
	public void setToolbarPosition(int position, boolean update) {
		toolbarPosition = position;
		if (update) {
			updateApplicationLayout();
			updateMenubar();
		}
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

	@Override
	public boolean showMenuBar() {
		return showMenuBar;
	}

	@Override
	protected void hideDockBarPopup() {
		if (getDockBar() != null)
			getDockBar().hidePopup();
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
		if (getDockBar() != null) {
			dockBar.setEastOrientation(isDockBarEast);
		}
	}

	/**
	 * Set show dockBar with GUI update
	 * 
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
	@Override
	public String getToolTooltipHTML(int mode) {

		if (loc.getTooltipLocale() != null) {
			loc.setTooltipFlag();
		}

		String toolTipHtml = super.getToolTooltipHTML(mode);

		loc.clearTooltipFlag();

		return toolTipHtml;

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
	final public GFont getPlainFontCommon() {
		return new GFontD(fontManager.getPlainFont());
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


	@Override
	public void setWaitCursor() {
		Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
		mainComp.setCursor(waitCursor);

		if (euclidianView != null) {
			((EuclidianViewInterfaceD) getActiveEuclidianView())
					.setCursor(waitCursor);
		}

		if (guiManager != null) {
			guiManager.allowGUIToRefresh();
		}
	}

	@Override
	public void setDefaultCursor() {
		mainComp.setCursor(Cursor.getDefaultCursor());

		if (guiManager != null) {
			for (int i = 0; i < guiManager.getEuclidianViewCount(); i++) {
				if (guiManager.hasEuclidianView2EitherShowingOrNot(i)) {
					((EuclidianViewInterfaceCommon) guiManager
							.getEuclidianView2(i))
									.setCursor(EuclidianCursor.DEFAULT);
				}
			}
		} else if (euclidianView != null) {
			getEuclidianView1().setCursor(Cursor.getDefaultCursor());
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
	@Override
	public boolean clearConstruction() {
		if (isSaved() || saveCurrentFile()) {

			kernel.clearConstruction(true);

			kernel.initUndoInfo();
			resetMaxLayerUsed();
			setCurrentFile(null);
			setMoveMode();

			return true;
		}

		return false;
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
				exitFrame();
			}
		}
	}

	protected void exitFrame() {
		frame.setVisible(false);
		if (getGuiManager() != null) {
			getGuiManager().exitAllCurrent();
		}
	}

	@Override
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

		if (!checkFileExistsAndShowFileNotFound(file)) {
			return false;
		}

		return loadExistingFile(file, isMacroFile);

	}

	private OFFHandler offHandler;
	public boolean loadOffFile(File file) {
		if (!checkFileExistsAndShowFileNotFound(file)) {
			return false;
		}
		kernel.notifyOpeningFile(file.getName());

		boolean status = true;
		try {
			OFFReader reader = new OFFReader();
			if (!initing) {
				initing = true;
				reader.parse(file, getOFFHandler());
				initing = false;
			} else {
				reader.parse(file, getOFFHandler());
			}
		} catch (Exception ex) {
			status = false;
			ex.printStackTrace();
		}

		kernel.notifyFileOpenComplete(true);

		return status;
	}

	protected final boolean checkFileExistsAndShowFileNotFound(File file) {
		// show file not found message
		if (!file.exists()) {
			/*
			 * First parameter can not be the main component of the application,
			 * otherwise that component would be validated too early if a
			 * missing file was loaded through the command line, which causes
			 * some nasty rendering problems.
			 */
			JOptionPane.showConfirmDialog(
					null,
					getLocalization().getError("FileNotFound") + ":\n"
							+ file.getAbsolutePath(), getLocalization()
							.getError("Error"), JOptionPane.DEFAULT_OPTION,
					JOptionPane.WARNING_MESSAGE);
			return false;
		}

		return true;
	}

	public boolean loadExistingFile(File file, boolean isMacroFile) {

		kernel.notifyOpeningFile(file.getName());

		setWaitCursor();

		if (!isMacroFile) {
			// hide navigation bar for construction steps if visible
			setHideConstructionProtocolNavigation();
		}

		boolean success = loadXML(file, isMacroFile);

		try {
			createRegressionFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		kernel.notifyFileOpenComplete(success);
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
			showError(getLocalization().getError("LoadFileFailed") + ":\n"
					+ file);
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
			showError("LoadFileFailed", e.getMessage());
			setCurrentFile(null);
			return false;
		}
	}

	/*
	 * loads an XML file as a String
	 */
	@Override
	public boolean loadXML(String xml) {
		try {

			// make sure objects are displayed in the correct View
			setActiveView(App.VIEW_EUCLIDIAN);

			getXMLio().processXMLString(xml, true, false);

			kernel.initUndoInfo();
			setSaved();
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

			// store current location of the window
			storeFrameCenter();

			// make sure objects are displayed in the correct View
			setActiveView(App.VIEW_EUCLIDIAN);

			// reset unique id (for old files, in case they don't have one)
			resetUniqueId();

			BufferedInputStream bis = new BufferedInputStream(is);

			bis = new BufferedInputStream(is);

			if (bis.markSupported()) {
				bis.mark(Integer.MAX_VALUE);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(bis, Charsets.UTF_8));
				String str = reader.readLine();

				// check if .ggb file is actually a base64 file from 4.2 Chrome
				// App
				if (str != null && str.startsWith("UEs")) {

					StringBuilder sb = new StringBuilder(str);
					sb.append("\n");

					while ((str = reader.readLine()) != null) {
						sb.append(str + "\n");
					}

					reader.close();
					is.close();
					bis.close();

					byte[] zipFile = Base64.decode(sb.toString());

					return loadXML(zipFile);
				}

				bis.reset();
			}

			getXMLio().readZipFromInputStream(bis, isMacroFile);

			is.close();
			bis.close();

			if (!isMacroFile) {
				kernel.initUndoInfo();
				setSaved();
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

	private int centerX, centerY;

	private void storeFrameCenter() {
		centerX = getWindowCenterX();
		centerY = getWindowCenterY();
	}

	public void centerFrame() {
		// re-center window
		if (frame != null) {
			int x0 = centerX - frame.getWidth() / 2;
			if (x0 < 0) {
				x0 = 0;
			}
			int y0 = centerY - frame.getHeight() / 2;
			if (y0 < 0) {
				y0 = 0;
			}
			frame.setLocation(x0, y0);
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
			((MyXMLioD) myXMLio).writeGeoGebraFile(file);
			setSaved();
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
			((MyXMLioD) myXMLio).writeMacroFile(file, macros);
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
		if (xml == null) {
			return;
		}
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
			getXMLio().writeMacroStream(os, kernel.getAllMacros());
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
				getXMLio().readZipFromInputStream(is, true);
				is.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	final public MyXMLioD getXMLio() {
		return (MyXMLioD) myXMLio;
	}

	public OFFHandler getOFFHandler() {
		if (offHandler == null) {
			offHandler = new OFFHandler(kernel, kernel.getConstruction());
		}
		return offHandler;
	}

	@Override
	public MyXMLioD createXMLio(Construction cons) {
		return new MyXMLioD(cons.getKernel(), cons);
	}

	@Override
	public void storeUndoInfo() {
		if (isUndoActive()) {
			kernel.storeUndoInfo();
			setUnsaved();
		}
	}

	public void restoreCurrentUndoInfo() {
		if (isUndoActive()) {
			kernel.restoreCurrentUndoInfo();
			setUnsaved();
		}
	}

	@Override
	protected int getWindowWidth() {
		if ((frame != null) && (frame.getWidth() > 0)) {
			return frame.getWidth();
		} else {
			return 800;
		}
	}

	@Override
	protected int getWindowHeight() {
		if ((frame != null) && (frame.getHeight() > 0)) {
			return frame.getHeight();
		} else {
			return 600;
		}
	}

	private int getWindowCenterX() {
		if (frame != null) {
			return frame.getX() + frame.getWidth() / 2;
		} else {
			return 400;
		}
	}

	private int getWindowCenterY() {
		if (frame != null) {
			return frame.getY() + frame.getHeight() / 2;
		} else {
			return 300;
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
	protected void getLayoutXML(StringBuilder sb, boolean asPreference) {
		if (guiManager == null) {
			initGuiManager();
		}
		getGuiManager().getLayout().getXml(sb, asPreference);
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
			for (int i = 0; i < GEOGEBRA_JAR_ALT.length; ++i) {
				if (path.endsWith(GEOGEBRA_JAR_ALT[i])) {
					runningFromJar = true;
					path = path.substring(0, path.length()
							- GEOGEBRA_JAR_ALT[i].length());
				}
			}

			// set codebase
			codebase = new URL(path);
		} catch (Exception e) {
			Log.info("GeoGebra is running with restricted permissions.");

			// make sure temporary files not used
			// eg ggbApi.getPNGBase64()
			ImageIO.setUseCache(false);

			if (appletImpl != null) {
				// applet codebase
				codebase = appletImpl.getJApplet().getCodeBase();
			}
		}

	}

	// **************************************************************************
	// EVENT DISPATCHING
	// **************************************************************************

	private GlassPaneListener glassPaneListener;

	private ErrorHandler defaultErrorHandler;

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
	@Override
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
		if (eventPane != mainPane) {

			// ESC from dialog: close it
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				Component rootComp = SwingUtilities.getRoot(e.getComponent());
				if (rootComp instanceof JDialog) {
					((JDialog) rootComp).setVisible(false);
					return true;
				}
			}

			// key event came from another window or applet: ignore it
			if (isApplet() || !inExternalWindow(this, eventPane)) {
				return false;
			}

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

		return isControlDown(e.isMetaDown(), e.isControlDown());

	}

	public static final boolean isControlDown(boolean isMetaDown,
			boolean isControlDown) {

		/*
		 * debug("isMetaDown = "+e.isMetaDown()); debug("isControlDown =
		 * "+e.isControlDown()); debug("isShiftDown = "+e.isShiftDown());
		 * debug("isAltDown = "+e.isAltDown()); debug("isAltGrDown =
		 * "+e.isAltGraphDown()); debug("fakeRightClick = "+fakeRightClick);
		 */

		if (fakeRightClick) {
			return false;
		}

		boolean ret = (MAC_OS && isMetaDown) // Mac: meta down for
				// multiple
				// selection
				|| (!MAC_OS && isControlDown); // non-Mac: Ctrl down for
		// multiple selection

		// debug("isPopupTrigger = "+e.isPopupTrigger());
		// debug("ret = " + ret);
		return ret;
		// return e.isControlDown();
	}

	private static boolean fakeRightClick = false;

	public static boolean isMiddleClick(MouseEventND e) {
		return e.isMiddleClick();
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

	@Override
	public void showError(String key) {
		showErrorDialog(key);
	}

	@Override
	public void showError(String key, String error) {
		showErrorDialog(getLocalization().getError(key) + ":\n" + error);
	}

	/**
	 * Show error dialog with given message
	 * 
	 * @param msg
	 *            (localized) message
	 */
	@Override
	public void showErrorDialog(final String msg) {
		if (!isErrorDialogsActive()) {
			return;
		}
		if (this.getErrorHandler() != null) {
			this.getErrorHandler().showError(msg);
			return;
		}

	}

	@Override
	public ErrorHandler getDefaultErrorHandler() {
		if (defaultErrorHandler == null) {
			defaultErrorHandler = new ErrorHandler() {

				public void showError(final String msg) {
					// don't remove, useful
					if (msg == null) {
						return;
					}
					Log.printStacktrace("" + msg);

					// make sure splash screen not showing (will be in front)
					GeoGebra.hideSplash();

					isErrorDialogShowing = true;

					// use SwingUtilities to make sure this gets executed in the
					// correct
					// (=GUI) thread.
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							// TODO investigate why this freezes Firefox
							// sometimes
							JOptionPane.showConfirmDialog(mainComp, msg,
									GeoGebraConstants.APPLICATION_NAME + " - "
											+ getLocalization()
													.getError("Error"),
									JOptionPane.DEFAULT_OPTION,
									JOptionPane.WARNING_MESSAGE);
							isErrorDialogShowing = false;
						}
					});

				}

				public void resetError() {
					showError(null);
				}

				public boolean onUndefinedVariables(String string,
						AsyncOperation<String[]> callback) {
					return getGuiManager().checkAutoCreateSliders(string,
							callback);
				}

				
				public void showCommandError(String command, String message) {

						// make sure splash screen not showing (will be in front)
					GeoGebra.hideSplash();

					Object[] options = { getLocalization().getPlain("OK"),
							getLocalization().getPlain("ShowOnlineHelp") };
						int n = JOptionPane.showOptionDialog(mainComp, message,
								GeoGebraConstants.APPLICATION_NAME + " - "
										+ getLocalization().getError("Error"),
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

				public String getCurrentCommand() {
					return null;
				}
					



			};
		}
		return defaultErrorHandler;
	}

	/**
	 * @return whether there is an open error dialog
	 */
	public boolean isErrorDialogShowing() {
		return isErrorDialogShowing;
	}

	public void showMessage(final String message) {
		// use SwingUtilities to make sure this gets executed in the correct
		// (=GUI) thread.
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JOptionPane.showConfirmDialog(mainComp, message,
						GeoGebraConstants.APPLICATION_NAME + " - "
								+ getMenu("Info"), JOptionPane.DEFAULT_OPTION,
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
	}

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
	public ScriptManagerD getScriptManager() {
		if (scriptManager == null) {
			scriptManager = new ScriptManagerD(this);
		}
		return (ScriptManagerD) scriptManager;
	}

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
		Log.debug("Setting up logging");
		if (Log.getLogDestination() == LogDestination.FILE) {
			// File logging already set up, don't override:
			Log.debug("Logging into explicitly defined file into GeoGebraLogger, not using LogManager");
			return;
		}

		// initialize logging to go to rolling log file
		logManager = LogManager.getLogManager();
		logManager.reset();

		logFile = new StringBuilder(30);

		logFile.append(UtilD.getTempDir());
		logFile.append("GeoGebraLog_");
		// randomize filename
		for (int i = 0; i < 10; i++) {
			logFile.append((char) ('a' + Math.round(Math.random() * 25)));
		}
		logFile.append(".txt");

		Log.debug("Logging is redirected to " + logFile.toString());
		Log.setTimeShown(false); // do not print the time twice

		// log file max size 10K, 1 file, append-on-open
		Handler fileHandler;
		try {
			fileHandler = new FileHandler(logFile.toString(),
					Log.LOGFILE_MAXLENGTH, 1, false);
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

		try {
			System.setOut(new PrintStream(los, true, Charsets.UTF_8));
			logger = Logger.getLogger("stderr");
			los = new LoggingOutputStream(logger, StdOutErrLevel.STDERR);
			System.setErr(new PrintStream(los, true, Charsets.UTF_8));
		} catch (UnsupportedEncodingException e) {
			// do nothing
		}

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

		logFile.append(UtilD.getTempDir());
		logFile.append("GeoGebraLog_");
		// randomize filename
		for (int i = 0; i < 10; i++) {
			logFile.append((char) ('a' + Math.round(Math.random() * 25)));
		}
		logFile.append(".txt");

		Log.setLogDestination(LogDestination.FILE);
		Log.setLogFile(logFile.toString());
		Log.debug(logFile.toString());
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
				Log.debug(contents.getTransferDataFlavors()[0]);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}

	// **************************************************************************
	// SOUNDS
	// **************************************************************************

	private SoundManagerD soundManager = null;

	@Override
	public SoundManagerD getSoundManager() {
		if (soundManager == null) {
			soundManager = new SoundManagerD(this);
		}
		return soundManager;
	}

	/*
	 * public void checkCommands(HashMap<String, CommandProcessor> map) {
	 * initTranslatedCommands();
	 * 
	 * if (rbcommand == null) { return; // eg applet with no properties jar }
	 * 
	 * Enumeration<String> e = rbcommand.getKeys(); while (e.hasMoreElements())
	 * { String s = e.nextElement(); if (!s.contains(syntaxStr) && (map.get(s)
	 * == null)) { boolean write = true; try { rbcommand.getString(s +
	 * syntaxStr); } catch (Exception ex) { write = false; } if (write) {
	 * debug("checkCommands: " + s); } } } }
	 */

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

	@Override
	public void resetUniqueId() {
		uniqueId = "" + UUID.randomUUID();
		setTubeId(0);
	}

	// //////////////////////////////////
	// FILE VERSION HANDLING
	// //////////////////////////////////

	private DialogManager dialogManager;

	private OpenFromGGTOperation openFromGGTOperation;

	@Override
	public void callAppletJavaScript(String string, Object[] args) {
		getApplet().callJavaScript(string, args);
	}

	@Override
	public boolean showView(int view) {
		if (getGuiManager() == null) {
			return view == App.VIEW_EUCLIDIAN;
		}
		return getGuiManager().showView(view);
	}

	@Override
	public void evalJavaScript(App app, String script, String arg)
			throws Exception {
		getScriptManager().evalJavaScript(app, script, arg);
	}

	@Override
	public int getMD5folderLength(String fullPath) {
		return fullPath.indexOf(File.separator);
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
		return appCP != null ? appCP.getWidth() : 0;
	}

	@Override
	public double getHeight() {
		if (isApplet()) {
			AppletImplementation applet = getApplet();
			return applet.height;
		}
		JPanel appCP = getCenterPanel();
		return appCP != null ? appCP.getHeight() : 0;
	}

	@Override
	public GFont getFontCommon(boolean b, int i, int size) {
		return new GFontD(getFont(b, i, size));
	}

	public GFont getBoldFontCommon() {
		return new GFontD(getBoldFont());
	}

	@Override
	public void repaintSpreadsheet() {
		if (getGuiManager() != null && getGuiManager().hasSpreadsheetView()) {
			getGuiManager().getSpreadsheetView().repaintView();
		}

	}

	@Deprecated
	@Override
	public UndoManagerD getUndoManager(Construction cons) {
		return new UndoManagerD(cons);
	}

	@Override
	public GeoElementGraphicsAdapter newGeoElementGraphicsAdapter() {
		return new GeoElementGraphicsAdapterD(this);
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
	public boolean isRightClick(AbstractEvent e) {
		return isRightClick(MouseEventD.getEvent(e));
	}

	@Override
	public boolean isControlDown(AbstractEvent e) {
		return e != null && isControlDown(e.isMetaDown(), e.isControlDown());
	}

	@Override
	public boolean isMiddleClick(AbstractEvent e) {
		return isMiddleClick((MouseEventND) e);
	}

	public Font getFontCanDisplayAwt(String string, boolean b, int plain, int i) {
		return getFontManager().getFontCanDisplayAwt(string, b, plain, i);
	}

	public Font getFontCanDisplayAwt(String string) {
		return GFontD.getAwtFont(getFontCanDisplay(string));
	}

	public Font getFontCanDisplayAwt(String value, int plain) {
		return GFontD.getAwtFont(getFontCanDisplay(value, plain));
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

	/**
	 * Whether we are using Java 7 (can't use clipboard on OSX)
	 * 
	 * @return whether we are using Java 7
	 */
	public boolean isJava7() {
		return System.getProperty("java.version").startsWith("1.7.");
	}

	/*
	 * current possible values http://mindprod.com/jgloss/properties.html AIX
	 * Digital Unix FreeBSD HP UX Irix Linux Mac OS Mac OS X MPE/iX Netware 4.11
	 * OS/2 Solaris Windows 2000 Windows 7 Windows 95 Windows 98 Windows NT
	 * Windows Vista Windows XP
	 */
	private static String OS = System.getProperty("os.name").toLowerCase(
			Locale.US);

	public static final boolean MAC_OS = OS.startsWith("mac");
	public static final boolean WINDOWS = OS.startsWith("windows");
	public static final boolean LINUX = OS.startsWith("linux");

	// make sure still works in the future on eg Windows 10/11
	// note Java 7u40 returns "Windows 8" for Windows 8.1 and Windows 10
	private static final boolean WINDOWS_XP_OR_EARLIER = OS
			.startsWith("windows 2000") || OS.startsWith("windows 95")
			|| OS.startsWith("windows 98") || OS.startsWith("windows nt") || OS.startsWith("windows xp");
	
	public static final boolean WINDOWS_VISTA_OR_LATER = WINDOWS
			&& !WINDOWS_XP_OR_EARLIER;

	public static final boolean WINDOWS_VISTA_OR_EARLIER = WINDOWS_XP_OR_EARLIER
			|| OS.startsWith("windows vista");

	@Override
	public boolean isHTML5Applet() {
		return false;
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
	public void showURLinBrowser(String strURL) {
		getGuiManager().showURLinBrowser(strURL);

	}

	@Override
	public void uploadToGeoGebraTube() {
		GeoGebraTubeExportD ggbtube = new GeoGebraTubeExportD(this);
		ggbtube.uploadWorksheet(null);
	}

	@Override
	public LowerCaseDictionary newLowerCaseDictionary() {
		return new LowerCaseDictionary(Normalizer.getInstance());
	}

	public CommandLineArguments getCommandLineArgs() {
		return args;
	}

	/**
	 * 
	 * return Left/Right as appropriate for eg Hebrew / Arabic
	 * 
	 * return int rather than FlowLayout.LEFT so we're not dependent on awt
	 */
	public int flowLeft() {
		if (!getLocalization().isRightToLeftReadingOrder()) {
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
		if (getLocalization().isRightToLeftReadingOrder()) {
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
		getEuclidianView2(1).dispatchEvent(event);

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
			@Override
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
		return getLocalization().isRightToLeftReadingOrder() ? ComponentOrientation.RIGHT_TO_LEFT
				: ComponentOrientation.LEFT_TO_RIGHT;
	}

	public void setComponentOrientation(Component c) {
		boolean rtl = getLocalization().isRightToLeftReadingOrder();
		ComponentOrientation orientation = rtl ? ComponentOrientation.RIGHT_TO_LEFT
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
			((JTextField) c).setHorizontalAlignment(rtl ? SwingConstants.RIGHT
					: SwingConstants.LEFT);
		} else if (c instanceof JComboBox) {
			JComboBox cb = (JComboBox) c;
			ListCellRenderer renderer = cb.getRenderer();
			if (!(renderer instanceof DashListRenderer
					|| renderer instanceof AxesStyleListRenderer || renderer instanceof PointStyleListRenderer)) {
				// if we didn't load GUI yet, assume there is no tool creation
				// dialog
				if (getGuiManager() == null
						|| !getGuiManager().belongsToToolCreator(renderer)) {
					renderer = new DefaultListCellRenderer();
					cb.setRenderer(renderer);
				}
				((JLabel) renderer)
						.setHorizontalAlignment(rtl ? SwingConstants.RIGHT
								: SwingConstants.LEFT);
			}
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
		if (getLocalization().isRightToLeftReadingOrder())
			panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		else
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));
	}

	// **************************************************************************
	// SINGULAR
	// **************************************************************************

	private class initializeSingularWS_thread implements Runnable {
		public initializeSingularWS_thread() {
		}

		@Override
		public void run() {
			// Display info about this particular thread
			Log.debug(Thread.currentThread() + " running");
			initializeSingularWS();
		}
	}

	public void initializeSingularWSD() {
		Thread t = new Thread(new initializeSingularWS_thread(), "compute");
		long startTime = System.currentTimeMillis();
		t.start();
		int i = 0;
		while (t.isAlive()) {
			Log.debug("Waiting for the initialization: " + i++);
			try {
				t.join(250);
			} catch (InterruptedException e) {
				return;
			}
			if (((System.currentTimeMillis() - startTime) > SingularWSSettings
					.getTimeout() * 1000L)
					&& t.isAlive()) {
				Log.debug("SingularWS startup timeout");
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

	public void exportAnimatedGIF(EuclidianView ev, FrameCollector gifEncoder,
			AnimationExportSlider num,
			int n, double val, double min, double max, double step) {
		for (int i = 0; i < n; i++) {

			// avoid values like 14.399999999999968
			val = Kernel.checkDecimalFraction(val);

			num.setValue(val);
			num.updateRepaint();

			Image img = GBufferedImageD
					.getAwtBufferedImage(((EuclidianViewD) ev)
							.getExportImage(1));
			if (img == null) {
				Log.error("image null");
			} else {
				gifEncoder.addFrame((BufferedImage) img);
			}

			val += step;

			if (val > max + 0.00000001 || val < min - 0.00000001) {
				val -= 2 * step;
				step *= -1;
			}

		}

		gifEncoder.finish();

	}

	@Override
	public LocalizationD getLocalization() {
		return loc;
	}

	@Override
	public double getMillisecondTime() {
		return System.nanoTime() / 1000000d;
	}

	// ** DON'T PUT IN COMMON OTHERWISE WEB PROJECT DOESN'T GET SPLIT UP **
	@Override
	public AlgoKimberlingWeightsInterface getAlgoKimberlingWeights() {
		if (kimberlingw != null) {
			return kimberlingw;
		}
		return (kimberlingw = new AlgoKimberlingWeights());
	}

	// ** DON'T PUT IN COMMON OTHERWISE WEB PROJECT DOESN'T GET SPLIT UP **
	@Override
	public double kimberlingWeight(AlgoKimberlingWeightsParams kw) {
		return getAlgoKimberlingWeights().weight(kw);
	}

	// ** DON'T PUT IN COMMON OTHERWISE WEB PROJECT DOESN'T GET SPLIT UP **
	@Override
	public AlgoCubicSwitchInterface getAlgoCubicSwitch() {
		if (cubicw != null) {
			return cubicw;
		}
		return (cubicw = new AlgoCubicSwitch());
	}

	// ** DON'T PUT IN COMMON OTHERWISE WEB PROJECT DOESN'T GET SPLIT UP **
	@Override
	public String cubicSwitch(AlgoCubicSwitchParams kw) {
		return getAlgoCubicSwitch().getEquation(kw);
	}

	/**
	 * Initializes the sign in Operation and tries to login in the user with the
	 * stored token
	 */
	protected void initSignInEventFlow() {

		// Inizialize the login operation
		loginOperation = new LoginOperationD();

		// Try to login the stored user
		loginOperation.performTokenLogin();
	}

	public void initOpenFromGGTEventFlow() {
		if (openFromGGTOperation == null) {
			openFromGGTOperation = new OpenFromGGTOperation(this);
		}
	}

	public OpenFromGGTOperation getOpenFromGGTOperation() {
		return openFromGGTOperation;
	}

	@Override
	public CommandDispatcher getCommandDispatcher(Kernel kernel2) {
		return new CommandDispatcher(kernel2) {
		};
	}

	/**
	 * only for 3D so thumbnail can be generated. Overridden in App3D
	 */
	public void uploadToGeoGebraTubeOnCallback() {

		uploadToGeoGebraTube();
	}

	private SensorLogger udpLogger;

	private String perspectiveParam = "";

	@Override
	public SensorLogger getSensorLogger() {
		if (udpLogger == null) {
			udpLogger = new UDPLoggerD(getKernel());
		}
		return udpLogger;
	}

	public void setPerspectiveParam(String perspective) {
		this.perspectiveParam = perspective;

	}

	@Override
	public void set1rstMode() {
		setMode(((GuiManagerD) this.getGuiManager()).getToolbarPanel()
				.getFirstToolbar().getFirstMode());
	}

	final public void insertFile(File file) {

		// using code from newWindowAction, combined with
		// Michael's suggestion
		AppD ad = newAppForTemplateOrInsertFile();
		
		// now, we have to load the file into AppD
		ad.getGuiManager().loadFile(file, false);

		// now we have to copy the macros from ad to app
		// in order to make some advanced constructions work
		// as it was hard to copy macro classes, let's use
		// strings, but how to load them into the application?
		try {
			getXMLio().processXMLString(ad.getMacroXML(), false, true);

			// alternative solution
			// app.addMacroXML(ad.getKernel().getMacroXML(
			// ad.getKernel().getAllMacros()));
		} catch (Exception ex) {
			Log.debug("Could not load any macros at \"Insert File\"");
			ex.printStackTrace();
		}

		// afterwards, the file is loaded into "ad" in theory,
		// so we have to use the CopyPaste class to copy it

		getCopyPaste().copyToXML(ad, new ArrayList<GeoElement>(ad
				.getKernel().getConstruction()
				.getGeoSetWithCasCellsConstructionOrder()), true);

		// and paste
		getCopyPaste().pasteFromXML(this, true);

		// forgotten something important!
		// ad should be closed!
		ad.exit();
		// this is also needed to make it possible
		// to load the same file once again
		ad.getFrame().dispose();

	}
	
	protected AppD newAppForTemplateOrInsertFile(){
		return new AppD(new CommandLineArguments(null), new JPanel(), true);
	}

	final public void applyTemplate(File file) {

		// using code from newWindowAction, combined with
		// Michael's suggestion
		// true as undo info is necessary for copy-paste!
		AppD ad = newAppForTemplateOrInsertFile();

		// now, we have to load the file into AppD
		ad.getGuiManager().loadFile(file, false);
		
		setLabelingStyle(ad.getLabelingStyle());
		getKernel().setConstructionDefaults(ad.getKernel());
		getKernel().setVisualStyles(ad.getKernel());
		getKernel().updateConstruction();

		// almost forgotten something important!
		// ad should be closed!
		ad.exit();
		// this is also needed to make it possible
		// to load the same style file once again
		ad.getFrame().dispose();

	}

	private boolean popupsDone = false;

	

	@Override
	public void showPopUps() {
		LoginOperationD signIn = (LoginOperationD) getLoginOperation();
		if (!signIn.isTubeCheckDone()) {
			return;
		}

		if (isAllowPopups()) {

			// Show login popup
			if (!popupsDone) {
				popupsDone = true;

				EventQueue.invokeLater(new Runnable() {
					@Override
					@SuppressWarnings("synthetic-access")
					public void run() {
						boolean showDockPopup = true;

						LoginOperationD signIn = (LoginOperationD) getLoginOperation();
						if (signIn.isTubeAvailable() && !signIn.isLoggedIn()) {
							showDockPopup = showTubeLogin();
						}

						if (showDockPopup && isShowDockBar()) {
							showPerspectivePopup();
						}
					}
				});
			}
		}
	}

	protected boolean showTubeLogin() {
		// for debugging only
		// force sign-in popup if not logged in
		// GeoGebraPreferencesD.getPref().savePreference(
		// GeoGebraPreferencesD.USER_LOGIN_SKIP,
		// "false");

		boolean showDockPopup = true;

		String skipLogin = GeoGebraPreferencesD.getPref().loadPreference(
				GeoGebraPreferencesD.USER_LOGIN_SKIP, "false");

		if (!"true".equals(skipLogin)) {
			showDockPopup = false;
			GeoGebraPreferencesD.getPref().savePreference(
					GeoGebraPreferencesD.USER_LOGIN_SKIP, "true");

			getGuiManager().login();
		}

		return showDockPopup;
	}

	protected void showPerspectivePopup() {
		getDockBar().showPopup();
	}

	/**
	 * resume 3D openGL renderer
	 */
	public void resume3DRenderer() {
		// used in 3D
	}

	@Override
	public void showCustomizeToolbarGUI() {
		getDialogManager().showToolbarConfigDialog();
	}

	@Override
	public boolean isSelectionRectangleAllowed() {
		return true;
	}

	public ImageIcon getMenuIcon(ImageResourceD res) {
		if (isMacOS()) {
			// fixed-size, 16x16 icons for mac menu
			return getScaledIcon(res, 16);
		}

		return getScaledIcon(res, null);
	}


	public Image getMenuInternalImage(ImageResourceD name) {
		if (isMacOS()) {
			// no scaling for mac menu
			return getInternalImage(name);
		}

		return getScaledInternalImage(name);
	}


	public void needThumbnailFor3D() {
		// nothing to do here
	}

	public boolean useHugeGuiForInput3D() {
		return false;
	}

	/**
	 * huge size for undo/redo/etc. buttons when huge GUI is needed for some 3D
	 * inputs
	 * 
	 */
	public static final int HUGE_UNDO_BUTTON_SIZE = 36;

	@Override
	public void closePopups() {
		// TODO Auto-generated method stub

	}

	@Override
	public GTimer newTimer(GTimerListener listener, int delay) {
		return new GTimerD(listener, delay);
	}

	private final ScheduledExecutorService scheduler = Executors
			.newScheduledThreadPool(1);

	private ScheduledFuture<?> handler;

	public void schedulePreview(Runnable scheduledPreview) {
		
		cancelPreview();

		handler = scheduler.schedule(scheduledPreview,
				SCHEDULE_PREVIEW_DELAY_IN_MILLISECONDS,
				TimeUnit.MILLISECONDS);
	}

	public void cancelPreview() {
		if (handler != null) {
			handler.cancel(false);
		}
	}

	private static GuiManagerD getGuiManager(AppD app) {
		return (GuiManagerD) app.getGuiManager();
	}

	public static void initToolbar(AppD app, int toolbarPosition,
			boolean showToolBarHelp, JPanel northPanel, JPanel eastPanel,
			JPanel southPanel, JPanel westPanel) {

		GuiManagerD guiManager = getGuiManager(app);
		LocalizationD loc = app.getLocalization();
		// initialize toolbar panel even if it's not used (hack)
		guiManager.getToolbarPanelContainer();

		ToolbarContainer toolBarContainer = (ToolbarContainer) guiManager
				.getToolbarPanelContainer();
		JComponent helpPanel = toolBarContainer.getToolbarHelpPanel();
		toolBarContainer.setOrientation(toolbarPosition);
		ToolbarContainer.setShowHelp(showToolBarHelp);

		switch (toolbarPosition) {
		case SwingConstants.NORTH:
			northPanel.add(toolBarContainer, BorderLayout.NORTH);
			break;
		case SwingConstants.SOUTH:
			southPanel.add(toolBarContainer, BorderLayout.NORTH);
			break;
		case SwingConstants.EAST:
			eastPanel.add(toolBarContainer, loc.borderEast());
			if (showToolBarHelp && helpPanel != null) {
				northPanel.add(helpPanel, BorderLayout.NORTH);
			}
			break;
		case SwingConstants.WEST:
			westPanel.add(toolBarContainer, loc.borderWest());
			if (showToolBarHelp && helpPanel != null) {
				northPanel.add(helpPanel, BorderLayout.NORTH);
			}
			break;
		}

		northPanel.revalidate();
		southPanel.revalidate();
		westPanel.revalidate();
		eastPanel.revalidate();
		toolBarContainer.buildGui();
		if (helpPanel != null) {
			helpPanel.revalidate();
		}
	}

	public static void initInputBar(AppD app, boolean showInputTop,
			JPanel northPanel, JPanel southPanel) {
		GuiManagerD gui = (GuiManagerD) app.getGuiManager();
		if (showInputTop) {
			northPanel.add(gui.getAlgebraInput(), BorderLayout.SOUTH);
		} else {
			southPanel.add(gui.getAlgebraInput(), BorderLayout.SOUTH);
		}
		((AlgebraInputD) gui.getAlgebraInput()).updateOrientation(showInputTop);
	}

	public static JPanel getMenuBarPanel(AppD appD, JPanel applicationPanel) {
		JPanel menuBarPanel = new JPanel(new BorderLayout());
		menuBarPanel.add(appD.getGuiManager().getMenuBar(), BorderLayout.NORTH);
		menuBarPanel.add(applicationPanel, BorderLayout.CENTER);
		return menuBarPanel;
	}

	public static GuiManagerD newGuiManager(AppD appD) {
		return new GuiManagerD(appD);
	}

	public static void loadFile(AppD app, File currentFile, boolean b) {
		app.getGuiManager().loadFile(currentFile, false);
	}

	public static void setActiveView(AppD app, int view) {
		getGuiManager(app).getLayout().getDockManager().setFocusedPanel(view);
	}

	public static boolean inExternalWindow(AppD app, Component eventPane) {
		return getGuiManager(app).getLayout().inExternalWindow(eventPane);
	}

	public static Component getRootComponent(AppD app) {
		return getGuiManager(app).getLayout().getRootComponent();
	}

	public static void newLayout(AppD app) {
		app.guiManager.setLayout(new LayoutD(app));
	}

	public static DockBarInterface newDockBar(AppD app) {
		return new DockBar(app);
	}

	public boolean isDesktop() {
		return true;
	}

	public CopyPaste getCopyPaste() {

		if (copyPaste == null) {
			copyPaste = new CopyPaste();
		}

		return copyPaste;
	}


}
