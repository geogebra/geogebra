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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
import org.geogebra.common.GeoGebraConstants.Platform;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianCursor;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.export.pstricks.GeoGebraExport;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.factories.CASFactory;
import org.geogebra.common.factories.Factory;
import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.factories.LaTeXFactory;
import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.geogebra3D.io.OFFHandler;
import org.geogebra.common.geogebra3D.kernel3D.commands.CommandDispatcher3D;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.io.layout.PerspectiveDecoder;
import org.geogebra.common.javax.swing.GImageIcon;
import org.geogebra.common.jre.factory.FormatFactoryJre;
import org.geogebra.common.jre.gui.MyImageJre;
import org.geogebra.common.jre.headless.AppDI;
import org.geogebra.common.jre.kernel.commands.CommandDispatcher3DJre;
import org.geogebra.common.jre.kernel.commands.CommandDispatcherJre;
import org.geogebra.common.jre.util.Base64;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.geos.AnimationExportSlider;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.main.App;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.main.HTML5Export;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.main.ProverSettings;
import org.geogebra.common.main.SingularWSSettings;
import org.geogebra.common.main.SpreadsheetTableModel;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.DefaultSettings;
import org.geogebra.common.main.settings.SettingsBuilder;
import org.geogebra.common.main.settings.updater.SettingsUpdaterBuilder;
import org.geogebra.common.media.VideoManager;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.move.ggtapi.models.json.JSONTokener;
import org.geogebra.common.plugin.ScriptManager;
import org.geogebra.common.plugin.SensorLogger;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.Charsets;
import org.geogebra.common.util.CopyPaste;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimerListener;
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
import org.geogebra.desktop.export.PrintPreviewD;
import org.geogebra.desktop.export.pstricks.GeoGebraToAsymptoteD;
import org.geogebra.desktop.export.pstricks.GeoGebraToPgfD;
import org.geogebra.desktop.export.pstricks.GeoGebraToPstricksD;
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
import org.geogebra.desktop.gui.dialog.DecorationListRenderer;
import org.geogebra.desktop.gui.dialog.PointStyleListRenderer;
import org.geogebra.desktop.gui.dialog.options.OptionsAdvancedD;
import org.geogebra.desktop.gui.inputbar.AlgebraInputD;
import org.geogebra.desktop.gui.layout.DockBar;
import org.geogebra.desktop.gui.layout.DockPanelD;
import org.geogebra.desktop.gui.layout.LayoutD;
import org.geogebra.desktop.gui.menubar.OptionsMenuController;
import org.geogebra.desktop.gui.toolbar.ToolbarContainer;
import org.geogebra.desktop.gui.toolbar.ToolbarD;
import org.geogebra.desktop.gui.util.ImageSelection;
import org.geogebra.desktop.headless.GFileHandler;
import org.geogebra.desktop.io.MyXMLioD;
import org.geogebra.desktop.io.OFFReader;
import org.geogebra.desktop.javax.swing.GImageIconD;
import org.geogebra.desktop.kernel.UndoManagerD;
import org.geogebra.desktop.kernel.geos.GeoElementGraphicsAdapterD;
import org.geogebra.desktop.main.settings.DefaultSettingsD;
import org.geogebra.desktop.main.settings.SettingsBuilderD;
import org.geogebra.desktop.main.settings.updater.SettingsUpdaterBuilderD;
import org.geogebra.desktop.move.OpenFromGGTOperation;
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

@SuppressWarnings("javadoc")
public class AppD extends App implements KeyEventDispatcher, AppDI {

	/**
	 * License file
	 */
	public static final String LICENSE_FILE = "/org/geogebra/desktop/_license.txt";

	/**
	 * Command line arguments
	 */
	protected CommandLineArguments cmdArgs;

	private DefaultSettings defaultSettings;

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

	private static LinkedList<File> fileList = new LinkedList<>();
	protected File currentPath, currentImagePath, currentFile = null;

	/**
	 * maximum number of files to (save &amp;) show in File &rarr; Recent
	 * submenu
	 */
	public static final int MAX_RECENT_FILES = 8;

	// ==============================================================
	// RESOURCE fields
	// ==============================================================

	private ResourceBundle rbmenuEnglish;

	private final LocalizationD loc;

	// ==============================================================
	// APPLET fields
	// ==============================================================

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

	// ==============================================================
	// MISC FLAGS
	// ==============================================================

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
		this(args, frame, null, undoActive, new LocalizationD(2));
	}

	/*************************************************************
	 * Construct application within Applet
	 * 
	 * @param args
	 * @param undoActive
	 */
	public AppD(CommandLineArguments args, boolean undoActive) {
		this(args, null, null, undoActive, new LocalizationD(2));
	}

	/*************************************************************
	 * Construct application within Container (e.g. GeoGebraPanel)
	 * 
	 * @param args
	 * @param comp
	 * @param undoActive
	 */
	public AppD(CommandLineArguments args, Container comp, boolean undoActive) {
		this(args, null, comp, undoActive, new LocalizationD(2));
	}

	/*************************************************************
	 * GeoGebra application general constructor
	 * 
	 * @param args
	 * @param frame
	 * @param comp
	 * @param undoActive
	 */
	public AppD(CommandLineArguments args, JFrame frame, Container comp,
			boolean undoActive,
			LocalizationD loc) {

		super(Platform.DESKTOP);

		this.loc = loc;
		loc.setApp(this);
		this.cmdArgs = args;
		this.prerelease = args != null && (args.containsArg("prerelease")
				|| args.containsArg("canary"));
		this.canary = args != null && args.containsArg("canary");
		if (args != null && !args.containsArg("silent")) {
			LoggerD logger = new LoggerD();
			logger.setReading(true);
			Log.setLogger(logger);
			Log.setLogDestination(LogDestination.CONSOLE);
			if (args.containsArg("logLevel")) {
				Log.setLogLevel(args.getStringValue("logLevel"));
			}
			if (args.containsArg("logFile")) {
				Log.setLogDestination(LogDestination.FILE);
				logger.setLogFileImpl(args.getStringValue("logFile"));
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

		if (canary) {
			Log.error("*****************************");
			Log.error("*** Running with --canary ***");
			Log.error("*****************************");
		} else if (prerelease) {
			Log.error("*********************************");
			Log.error("*** Running with --prerelease ***");
			Log.error("*********************************");
		}

		setFileVersion(GeoGebraConstants.VERSION_STRING,
				getConfig().getAppCode());

		if (args != null) {
			handleHelpVersionArgs(args);
		}

		isApplet = false;

		if (frame != null) {
			mainComp = frame;
		} else {
			mainComp = comp;
		}

		useFullGui = !isApplet;

		// don't want to redirect System.out and System.err when running as
		// Applet
		// or eg from Eclipse
		getCodeBase(); // initialize runningFromJar

		Log.debug("isApplet=" + isApplet + " runningFromJar=" + runningFromJar);
		if (!isApplet && runningFromJar) {
			setUpLogging();
		} else {
			Log.debug("Not setting up logging via LogManager");
		}

		// needed for JavaScript getCommandName(), getValueString() to work
		// (security problem running non-locally)

		preferredSize = new Dimension(800, 600);

		fontManager = new FontManagerD();
		initImageManager(mainComp);

		// set locale
		setLocale(mainComp.getLocale());

		// init kernel
		initFactories();
		initKernel();
		kernel.setPrintDecimals(getConfig().getDefaultPrintDecimals());

		// init settings
		initSettings();

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

		if (isUsingFullGui() && (getTmpPerspectives() != null)) {
			getGuiManager().getLayout()
					.setPerspectives(getTmpPerspectives(),
							PerspectiveDecoder.decode(
							this.perspectiveParam, getKernel().getParser(),
							ToolBar.getAllToolsNoMacros(false, false, this)));
		}

		if (needsSpreadsheetTableModel) {
			// if tableModel==null, will create one
			getSpreadsheetTableModel();
		}

		if (isUsingFullGui() && ggtloading) {
			getGuiManager().setToolBarDefinition(ToolbarD.getAllTools(this));
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
		if (kernel.wantAnimationStarted()) {
			kernel.getAnimatonManager().startAnimation();
			kernel.setWantAnimationStarted(false);
		}

	}

	// **************************************************************************
	// INIT
	// **************************************************************************

	@Override
	public void reset() {
		if (currentFile != null) {
			loadFile(this, currentFile);
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
	 *            true &rarr; set system LAF, false &rarr; set cross-platform
	 *            LAF
	 */
	public static void setLAF(boolean isSystemLAF) {
		try {
			if (isSystemLAF) {
				UIManager.setLookAndFeel(
						UIManager.getSystemLookAndFeelClassName());
			} else {
				UIManager.setLookAndFeel(
						UIManager.getCrossPlatformLookAndFeelClassName());
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
			System.out.println("Usage: java -jar geogebra.jar [OPTION] [FILE]\n"
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
			System.out.println(
					"  --prover=OPTIONS\tset options for the prover subsystem\n"
							+ "    where OPTIONS is a comma separated list, formed with the following available settings (defaults in brackets):\n"
							+ "      engine:ENGINE\tset engine (Auto|OpenGeoProver|Recio|Botana|PureSymbolic) ["
							+ proverSettings.proverEngine + "]\n"
							+ "      timeout:SECS\tset the maximum time attributed to the prover (in seconds) ["
							+ proverSettings.proverTimeout + "]\n"
							+ "      maxterms:NUMBER\tset the maximal number of terms ["
							+ proverSettings.getMaxTerms()
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
			System.out.println(
					" --singularWS=OPTIONS\tset options for SingularWS\n"
							+ "   where OPTIONS is a comma separated list, formed with the following available settings (defaults in brackets):\n"
							+ "      enable:BOOLEAN\tuse Singular WebService when possible ["
							+ SingularWSSettings.useSingularWebService() + "]\n"
							+ "      remoteURL:URL\tset the remote server URL ["
							+ SingularWSSettings
									.getSingularWebServiceRemoteURL()
							+ "]\n" + "      timeout:SECS\tset the timeout ["
							+ SingularWSSettings.getTimeout() + "]\n"
							+ "      caching:BOOLEAN\tset server side caching ["
							+ SingularWSSettings.getCachingText() + "]\n"
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
		return new EuclidianViewD(getEuclidianController(), showAxesFlags,
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
			boolean showAlgebraInputTop = args
					.getBooleanValue("showAlgebraInputTop", true);
			if (showAlgebraInputTop) {
				setInputPosition(InputPosition.top, false);
			}
		}

		String fontSize = args.getStringValue("fontSize");
		if (fontSize.length() > 0) {
			setFontSize(Util.getValidFontSize(Integer.parseInt(fontSize)),
					true);
		}

		boolean enableUndo = args.getBooleanValue("enableUndo", true);
		if (!enableUndo) {
			setUndoActive(false);
		}

		if (args.containsArg("showAxes")) {
			boolean showAxesParam = args.getBooleanValue("showAxes", true);
			this.showAxes[0] = showAxesParam;
			this.showAxes[1] = showAxesParam;
			this.getSettings().getEuclidian(1).setShowAxes(showAxesParam,
					showAxesParam);
			this.getSettings().getEuclidian(2).setShowAxes(showAxesParam,
					showAxesParam);
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

			if (filename == null || "".equals(filename)) {
				filename = "../common/src/main/resources/giac/__giac.js";
			}

			int count = 0;

			ArrayList<String> errors = new ArrayList<>();

			// Open the file
			FileInputStream fstream;
			try {
				fstream = new FileInputStream(filename);
				BufferedReader br = new BufferedReader(
						new InputStreamReader(fstream, Charsets.getUtf8()));

				String strLine;

				// Read File Line By Line
				while ((strLine = br.readLine()) != null
						&& (strLine.indexOf("JSONSTART") == -1)) {
					// Print the content on the console
					// System.out.println("IGNORE " + strLine);
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
						response.get("cat");

						// System.out.println("response = " + response);
						// System.out.println("result = " + result);

						// command = "Solve[13^(x+1)-2*13^x=(1/5)*5^x,x]";
						// result =
						// "{-ln(55)/ln(13/5)}|OR|{x=(-ln(11)-ln(5))/(ln(13)-ln(5))}";

						String casResult = getGgbApi().evalGeoGebraCAS(command);

						String casResultOriginal = casResult;

						// remove spaces
						casResult = casResult.replace(" ", "");
						result = result.replace(" ", "");

						// sort out arbitrary constants
						result = result.replaceAll("n_[0-9]*", "n_0");
						result = result.replaceAll("c_[0-9]*", "c_0");

						casResult = casResult
								.replaceAll("arbconst\\([+0-9]*\\)", "c_0");

						casResult = casResult
								.replaceAll("arbint\\(([+0-9]*)\\)", "n_0");

						String[] results = { result };

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
									+ "\nactual result = " + StringUtil
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
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
	}

	private static boolean versionCheckAllowed = true;

	private void setVersionCheckAllowed(String versionCheckAllow) {

		if (isApplet()) {
			versionCheckAllowed = false;
			return;
		}

		if (versionCheckAllow != null) {
			if ("off".equals(versionCheckAllow)) {
				GeoGebraPreferencesD.getPref().saveVersionCheckAllow("false");
				versionCheckAllowed = false;
				return;
			}
			if ("on".equals(versionCheckAllow)) {
				GeoGebraPreferencesD.getPref().saveVersionCheckAllow("true");
				versionCheckAllowed = true;
				return;
			}
			if ("false".equals(versionCheckAllow)) {
				versionCheckAllowed = false;
				return;
			}
			if ("true".equals(versionCheckAllow)) {
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
			proverSettings.setMaxTerms(Integer.parseInt(str[1]));
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

			if (fixcoordsP < 0 || fixcoordsP > 4) {
				Log.error(
						"Improper value for usefixcoords for Prove, using default instead");
			} else {
				proverSettings.useFixCoordinatesProve = fixcoordsP;
			}

			if (fixcoordsPD < 0 || fixcoordsPD > 4) {
				Log.error(
						"Improper value for usefixcoords for ProveDetails, using default instead");
			} else {
				proverSettings.useFixCoordinatesProveDetails = fixcoordsPD;
			}

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
	 * @author Zoltan Kovacs
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

	public void createNewWindow() {
		GeoGebraFrame.createNewWindow(cmdArgs.getGlobalArguments());
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

		kernel.updateConstruction(false);
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
		String lowerCase = StringUtil.toLowerCaseUS(fileArgument);
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

						GeoGebraFrame.createNewWindow(args.getGlobalArguments()
								.add(key, fileArgument));
					}
				});
			} else {

				try {
					boolean success;
					String lowerCase = StringUtil.toLowerCaseUS(fileArgument);
					FileExtensions ext = StringUtil.getFileExtension(lowerCase);

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
						if (success && !isMacroFile && !isUsingFullGui()) {
							if (showConsProtNavigation()
									|| !isJustEuclidianVisible()) {
								useFullGui = true;
							}
						}
					} else if (lowerCase.startsWith("base64://")) {

						// substring to strip off base64://
						byte[] zipFile = Base64
								.decode(fileArgument.substring(9));
						success = loadXML(zipFile);

						if (success && !isMacroFile && !isUsingFullGui()) {
							if (showConsProtNavigation()
									|| !isJustEuclidianVisible()) {
								useFullGui = true;
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
	 * loads an html file with &lt;param name="ggbBase64"
	 * value="UEsDBBQACAAI..."&gt;
	 * 
	 * @param file
	 *            html file
	 * @return success
	 */
	public boolean loadBase64File(final File file) {
		if (!file.exists()) {
			// show file not found message
			JOptionPane.showConfirmDialog(getMainComponent(),
					getLocalization().getError("FileNotFound") + ":\n"
							+ file.getAbsolutePath(),
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
			showError(Errors.LoadFileFailed, file.getName());
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
	 * <li>From embedded base64 string
	 * <ol type="a">
	 * <li><code>&lt;article ... data-param-ggbbase64="..." /&gt;</code></li>
	 * <li><code>&lt;param name="ggbBase64" value="..." /&gt;</code></li>
	 * </ol>
	 * </li>
	 * <li>From relative referenced *.ggb file
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
		String lowerCasedPage = StringUtil.toLowerCaseUS(page); // We must
																// preserve
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
			String attrName0) {
		String attrName = attrName0;
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
				null : page.substring(begin, end);
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
					new InputStreamReader(url.openStream(),
							Charsets.getUtf8()));
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
		Perspective docPerspective = getTmpPerspective(null);

		if (docPerspective == null) {
			throw new OperationNotSupportedException();
		}

		boolean justEuclidianVisible = false;

		for (DockPanelData panel : docPerspective.getDockPanelData()) {
			if ((panel.getViewId() == App.VIEW_EUCLIDIAN)
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

	/**
	 * Sets the tool icon size between 32 and 64.
	 * @param iconSize icon size
	 */
	public void setMaxIconSize(int iconSize) {
		imageManager.setMaxIconSizeAsPt(iconSize);
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
		default:
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
		ImageIcon icon = imageManager.getImageIcon(res, borderColor);
		return scaleIcon(icon, getScaledIconSize());
	}

	public ImageIcon getScaledIconCommon(ImageResourceD res) {
		ImageIcon icon = imageManager.getImageIcon(res, null);
		return scaleIcon(icon, getScaledIconSize());
	}

	public ImageIcon getScaledIcon(ImageResourceD res, int iconSize) {
		ImageIcon icon = imageManager.getImageIcon(res, null);
		return scaleIcon(icon, iconSize);
	}

	private static ImageIcon scaleIcon(ImageIcon icon, int iconSize) {
		if (icon == null || iconSize == 0) {
			return null;
		}
		Image img = icon.getImage().getScaledInstance(iconSize, iconSize,
				Image.SCALE_SMOOTH);
		return new ImageIcon(img);

	}

	public Image getScaledInternalImage(ImageResourceD fileName) {
		MyImageD img = imageManager.getInternalImage(fileName);
		int iconSize = getScaledIconSize();
		return img.getImage().getScaledInstance(iconSize, iconSize, 0);
	}

	public ImageIcon getToolBarImage(String modeText, Color borderColor) {

		ImageIcon icon = imageManager.getImageIcon(
				imageManager.getToolImageResource(modeText), borderColor,
				Color.WHITE);

		/*
		 * mathieu 2010-04-10 see ImageManager3D.getImageResourceGeoGebra() if
		 * (icon == null) { // load3DJar(); // try to find this image in 3D
		 * extension path = "org/geogebra/desktop/geogebra3D/images/" +
		 * filename; icon = imageManager.getImageIcon(path, borderColor); }
		 */

		if (icon == null) {
			icon = getToolIcon(borderColor);

			Log.debug("icon missing for mode " + modeText);
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
		return imageManager.getInternalImage(filename).getImage();
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
		return imageManager.getInternalImage(GuiResourcesD.NAV_PLAY_CIRCLE)
				.getImage();
	}

	public Image getPlayImageCircleHover() {
		// don't need to load gui jar as reset image is in main jar
		return imageManager.getInternalImage(GuiResourcesD.NAV_PLAY_HOVER)
				.getImage();
	}

	public Image getPauseImageCircle() {
		// don't need to load gui jar as reset image is in main jar
		return imageManager.getInternalImage(GuiResourcesD.NAV_PAUSE_CIRCLE)
				.getImage();
	}

	public Image getPauseImageCircleHover() {
		// don't need to load gui jar as reset image is in main jar
		return imageManager
				.getInternalImage(GuiResourcesD.NAV_PAUSE_CIRCLE_HOVER)
				.getImage();
	}

	public Image getPauseImage() {
		// don't need to load gui jar as reset image is in main jar
		return imageManager.getInternalImage(GuiResourcesD.NAV_PAUSE)
				.getImage();
	}

	@Override
	public MyImageD getExternalImage(String filename) {
		return ImageManagerD.getExternalImage(filename);
	}

	@Override
	public final MyImage getExternalImageAdapter(String filename, int width,
			int height) {
		MyImageD im = ImageManagerD.getExternalImage(filename);
		return im;
	}

	@Override
	public void addExternalImage(String filename, MyImageJre image) {
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
							border, null));
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
		}
		return icon;
	}

	/**
	 * stores an image in the application's imageManager.
	 * 
	 * @return fileName of image stored in imageManager
	 */
	public String createImage(MyImageD image, String imageFileName) {
		return imageManager.createImage(image, imageFileName, this);
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
		BufferedReader br = null;
		try {
			InputStream is = AppD.class.getResourceAsStream(s);
			br = new BufferedReader(
					new InputStreamReader(is, Charsets.getUtf8()));
			String thisLine;
			while ((thisLine = br.readLine()) != null) {
				sb.append(thisLine);
				sb.append('\n');
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

	@Override
	public void copyGraphicsViewToClipboard() {

		copyGraphicsViewToClipboard(getActiveEuclidianView());
	}

	@Override
	public void copyTextToSystemClipboard(String text) {
		Toolkit.getDefaultToolkit().getSystemClipboard()
				.setContents(new StringSelection(text), null);
	}

	@Override
	public void copyBase64ToClipboard() {

		// don't include preview bitmap
		copyTextToSystemClipboard(getGgbApi().getBase64(false));
	}

	@Override
	public void copyFullHTML5ExportToClipboard() {

		copyTextToSystemClipboard(HTML5Export.getFullString(this));
	}

	public void copyGraphicsViewToClipboard(final EuclidianView copyView) {

		getSelectionManager().clearSelectedGeos(true, false);
		updateSelection(false);

		Thread runner = new Thread() {
			@Override
			public void run() {
				setWaitCursor();

				simpleExportToClipboard(copyView);

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
		Image img = GBufferedImageD.getAwtBufferedImage(
				((EuclidianViewD) ev).getExportImage(scale));
		copyImageToClipboard(img);
	}

	/**
	 * Copy image to system clipboard
	 * 
	 * @param dataURI data URI of image to copy
	 */
	@Override
	public void copyImageToClipboard(String dataURI) {

		String base64Image = dataURI;

		if (base64Image.startsWith(StringUtil.pngMarker)) {
			base64Image = base64Image.substring(StringUtil.pngMarker.length(),
					base64Image.length());
		}
		handleImageExport(base64Image);
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
	@Override
	public MyImageJre getExportImage(double maxX, double maxY)
			throws OutOfMemoryError {

		return new MyImageD(GBufferedImageD.getAwtBufferedImage(
				getActiveEuclidianViewExportImage(maxX, maxY)));
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
		return Integer.parseInt(OptionsAdvancedD
				.tooltipTimeouts(OptionsAdvancedD.tooltipTimeoutsLength() - 2));
	}

	@Override
	public void setLanguage(String s) {
		String[] parts = s.split("_");
		String language = parts[0];
		String country = parts.length > 1 ? parts[1] : null;
		Locale locale = null;
		if (language != null) {
			if (country != null) {
				locale = new Locale(language, country);
			} else {
				locale = new Locale(language);
			}
		}
		setLocale(locale);
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
			showGenericError(e);

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

	final public String getEnglishMenu(String key) {

		if (rbmenuEnglish == null) {
			rbmenuEnglish = MyResourceBundle.createBundle(LocalizationD.RB_MENU,
					Locale.ENGLISH);
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
				GeoGebraConstants.APPLICATION_NAME + " - "
						+ loc.getMenu("Help"),
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
		if ((frame != null) && (frame == mainComp)) {
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
		if ((frame != null) && (frame == mainComp)) {
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
				applicationSplitPane
						.setBorder(BorderFactory.createEmptyBorder());
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
				if (dockBar.isEastOrientation()) {
					applicationPanel.add((Component) dockBar,
							getLocalization().borderEast());
				} else {
					applicationPanel.add((Component) dockBar,
							getLocalization().borderWest());
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

			getSettingsUpdater().getFontSettingsUpdater().resetFonts();
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
					.setRightComponent((getGuiManager()).getInputHelpPanel());
			if (applicationSplitPane.getLastDividerLocation() <= 0) {
				applicationSplitPane
						.setLastDividerLocation(applicationSplitPane.getWidth()
								- (((GuiManagerD) getGuiManager()))
										.getInputHelpPanelMinimumWidth());
			}
			applicationSplitPane.setDividerLocation(
					applicationSplitPane.getLastDividerLocation());
			applicationSplitPane.setDividerSize(8);

		} else {
			applicationSplitPane.setLastDividerLocation(
					applicationSplitPane.getDividerLocation());
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
					northPanel, southPanel);
		}

		if (showToolBar) {
			initToolbar(this, getToolbarPosition(), showToolBarHelp, northPanel,
					eastPanel, southPanel, westPanel);
		}

		if (frame != null && frame.getContentPane() != null) {
			frame.getContentPane().validate();
		}

	}

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
		if (frame != null) {
			frame.validate();
		}
	}

	public void updateToolBar() {
		if (!showToolBar || isIniting()) {
			return;
		}

		getGuiManager().updateToolbar();

		if (!initing) {
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

	@Override
	public void updateDynamicStyleBars() {
		// not implemented here
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

	@Override
	public void setShowToolBar(boolean toolbar, boolean help) {
		super.setShowToolBar(toolbar, help);
		if (toolbar && getGuiManager() != null) {
			getGuiManager().setShowToolBarHelp(help);
		}
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
		return null;
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
		}
		return null;

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

	public boolean showToolBarHelp() {
		return showToolBarHelp;
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
	public void hideDockBarPopup() {
		if (getDockBar() != null) {
			getDockBar().hidePopup();
		}
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
		return ((GFontD) fontManager.getBoldFont()).getAwtFont();
	}

	final public Font getItalicFont() {
		return ((GFontD) fontManager.getItalicFont()).getAwtFont();
	}

	final public Font getPlainFont() {
		return ((GFontD) fontManager.getPlainFont()).getAwtFont();
	}

	@Override
	final public GFont getPlainFontCommon() {
		return fontManager.getPlainFont();
	}

	final public Font getSerifFont() {
		return ((GFontD) fontManager.getSerifFont()).getAwtFont();
	}

	final public Font getSmallFont() {
		return ((GFontD) fontManager.getSmallFont()).getAwtFont();
	}

	final public GFont getFont(boolean serif, int style, int size) {
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
					guiManager.getEuclidianView2(i)
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
			Image image = Toolkit.getDefaultToolkit()
					.createImage(new MemoryImageSource(16, 16, pixels, 0, 16));

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

		if (isSaved() || saveCurrentFile()) {
			exitFrame();
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

		GuiManagerD.exitAll();
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
			showError(Errors.LoadFileFailed, file.getName());
		}

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
			JOptionPane.showConfirmDialog(null,
					getLocalization().getError("FileNotFound") + ":\n"
							+ file.getAbsolutePath(),
					getLocalization().getError("Error"),
					JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
			return false;
		}

		return true;
	}

	public boolean loadExistingFile(File file, boolean isMacroFile) {

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
		return success;
	}

	/**
	 * Loads construction file
	 * 
	 * @return true if successful
	 */
	final public boolean loadXML(File file, boolean isMacroFile) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);

			boolean success = false;

			// pretend we're initializing the application to prevent unnecessary
			// update
			if (!initing) {
				initing = true;
				success = GFileHandler.loadXML(this, fis, isMacroFile);
				initing = false;
			} else {
				success = GFileHandler.loadXML(this, fis, isMacroFile);
			}

			if (success && !isMacroFile) {
				setCurrentFile(file);
			}

			return success;
		} catch (Exception e) {
			setCurrentFile(null);
			e.printStackTrace();
			showError(Errors.LoadFileFailed, file.getName());
			return false;
		} finally {
			initing = false;
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Loads construction file from URL
	 * 
	 * @return true if successful
	 */
	final public boolean loadXML(URL url, boolean isMacroFile) {

		try {
			boolean success = GFileHandler.loadXML(this, url.openStream(),
					isMacroFile);

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
			showError(Errors.LoadFileFailed, e.getMessage());
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

	private int centerX, centerY;

	@Override
	public void storeFrameCenter() {
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
			getXMLio().writeGeoGebraFile(file);
			setSaved();
			setDefaultCursor();
			return true;
		} catch (Exception e) {
			setDefaultCursor();
			showError(Errors.SaveFileFailed);
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
			getXMLio().writeMacroFile(file, macros);
			setDefaultCursor();
			return true;
		} catch (Exception e) {
			setDefaultCursor();
			showError(Errors.SaveFileFailed);
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

			getXMLio().processXMLString(xml, clearAll, false);
		} catch (MyError err) {
			err.printStackTrace();
			showError(err);
		} catch (Exception e) {
			e.printStackTrace();
			showError(Errors.LoadFileFailed);
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
		return (MyXMLioD) super.getXMLio();
	}

	public OFFHandler getOFFHandler() {
		if (offHandler == null) {
			offHandler = new OFFHandler(kernel.getConstruction());
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
		}
		return 800;
	}

	@Override
	protected int getWindowHeight() {
		if ((frame != null) && (frame.getHeight() > 0)) {
			return frame.getHeight();
		}
		return 600;
	}

	private int getWindowCenterX() {
		if (frame != null) {
			return frame.getX() + frame.getWidth() / 2;
		}
		return 400;
	}

	private int getWindowCenterY() {
		if (frame != null) {
			return frame.getY() + frame.getHeight() / 2;
		}
		return 300;
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
					path = path.substring(0,
							path.length() - GEOGEBRA_JAR_ALT[i].length());
				}
			}

			// set codebase
			codebase = new URL(path);
		} catch (Exception e) {
			Log.info("GeoGebra is running with restricted permissions.");

			// make sure temporary files not used
			// eg ggbApi.getPNGBase64()
			ImageIO.setUseCache(false);
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
		}
		return null;
	}

	public void setGlassPane(Component component) {
		if (mainComp == frame) {
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

	private boolean controlDown = false;
	private boolean shiftDown = false;

	public boolean getControlDown() {
		return controlDown;
	}

	public boolean getShiftDown() {
		return shiftDown;
	}

	public static boolean isControlDown(InputEvent e) {

		return isControlDown(e.isMetaDown(), e.isControlDown());

	}

	public static final boolean isControlDown(boolean isMetaDown,
			boolean isControlDown) {

		/*
		 * debug("isMetaDown = "+e.isMetaDown()); debug("isControlDown =
		 * "+e.isControlDown()); debug("isShiftDown = "+e.isShiftDown()); debug(
		 * "isAltDown = "+e.isAltDown()); debug("isAltGrDown =
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
		 * debug("isControlDown ="+e.isControlDown()); debug("isShiftDown = "
		 * +e.isShiftDown()); debug("isAltDown = "+e.isAltDown()); debug(
		 * "isAltGrDown ="+e.isAltGraphDown()); debug("isPopupTrigger = "
		 * +e.isPopupTrigger()); debug("fakeRightClick = "+fakeRightClick);
		 */

		if (fakeRightClick) {
			return true;
		}

		boolean ret =
				// e.isPopupTrigger() ||
				(MAC_OS && e.isControlDown()) // Mac: ctrl click = right click
						|| (!MAC_OS && e.isMetaDown()); // non-Mac: right click
														// = meta
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
		Set<AWTKeyStroke> set = p.getFocusTraversalKeys(
				KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS);
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

				@Override
				public void showError(final String msg) {
					// don't remove, useful
					if (msg == null) {
						return;
					}
					Log.printStacktrace("" + msg);

					// make sure splash screen not showing (will be in front)
					GeoGebra.hideSplash();

					isErrorDialogShowing = true;
					final String msgDisplay = msg.substring(0,
							Math.min(msg.length(), 1000));
					// use SwingUtilities to make sure this gets executed in the
					// correct
					// (=GUI) thread.
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							// TODO investigate why this freezes Firefox
							// sometimes
							JOptionPane.showConfirmDialog(mainComp, msgDisplay,
									GeoGebraConstants.APPLICATION_NAME + " - "
											+ getLocalization()
													.getError("Error"),
									JOptionPane.DEFAULT_OPTION,
									JOptionPane.WARNING_MESSAGE);
							isErrorDialogShowing = false;
						}
					});

				}

				@Override
				public void resetError() {
					showError(null);
				}

				@Override
				public boolean onUndefinedVariables(String string,
						AsyncOperation<String[]> callback) {
					return getGuiManager().checkAutoCreateSliders(string,
							callback);
				}

				@Override
				public void showCommandError(String command, String message) {

					// make sure splash screen not showing (will be in front)
					GeoGebra.hideSplash();

					Object[] options = { getLocalization().getMenu("OK"),
							getLocalization().getMenu("ShowOnlineHelp") };
					int n = JOptionPane.showOptionDialog(mainComp, message,
							GeoGebraConstants.APPLICATION_NAME + " - "
									+ getLocalization().getError("Error"),
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, // do
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

				@Override
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
								+ getLocalization().getMenu("Info"),
						JOptionPane.DEFAULT_OPTION,
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
	public ScriptManager newScriptManager() {
		return new ScriptManagerD(this);
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
			Log.debug(
					"Logging into explicitly defined file into GeoGebraLogger, not using LogManager");
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
					&& contents.getTransferDataFlavors().length > 0) {
				Log.debug(contents.getTransferDataFlavors()[0]);
			}
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

	@Override
	public final VideoManager getVideoManager() {
		// not implemented here.
		return null;
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
	 * syntaxStr); } catch (Exception ex) { write = false; } if (write) { debug(
	 * "checkCommands: " + s); } } } }
	 */

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
		setTubeId(null);
	}

	// //////////////////////////////////
	// FILE VERSION HANDLING
	// //////////////////////////////////

	private DialogManager dialogManager;

	private OpenFromGGTOperation openFromGGTOperation;

	@Override
	public void callAppletJavaScript(String string, String... args) {
		// not needed in desktop
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
		((ScriptManagerD) getScriptManager()).evalJavaScript(app, script, arg);
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
			ToolTipManager.sharedInstance()
					.setDismissDelay(getTooltipTimeout() * 1000);
		} else {
			ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		}
	}

	@Override
	public double getWidth() {
		JPanel appCP = getCenterPanel();
		return appCP != null ? appCP.getWidth() : 0;
	}

	@Override
	public double getHeight() {
		JPanel appCP = getCenterPanel();
		return appCP != null ? appCP.getHeight() : 0;
	}

	@Override
	public GFont getFontCommon(boolean b, int i, int size) {
		return getFont(b, i, size);
	}

	public GFont getBoldFontCommon() {
		return fontManager.getBoldFont();
	}

	@Override
	public void repaintSpreadsheet() {
		if (getGuiManager() != null && getGuiManager().hasSpreadsheetView()) {
			getGuiManager().getSpreadsheetView().repaintView();
		}

	}

	@Override
	public UndoManagerD getUndoManager(Construction cons) {
		return new UndoManagerD(cons, false);
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

	public Font getFontCanDisplayAwt(String string, boolean b, int plain,
			int i) {
		return ((GFontD) getFontManager().getFontCanDisplay(string, b, plain,
				i)).getAwtFont();
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

	/**
	 * Whether we are using Java 7 (can't use clipboard on OSX)
	 * 
	 * @return whether we are using Java 7
	 */
	public static boolean isJava7() {
		return System.getProperty("java.version").startsWith("1.7.");
	}

	/*
	 * current possible values http://mindprod.com/jgloss/properties.html AIX
	 * Digital Unix FreeBSD HP UX Irix Linux Mac OS Mac OS X MPE/iX Netware 4.11
	 * OS/2 Solaris Windows 2000 Windows 7 Windows 95 Windows 98 Windows NT
	 * Windows Vista Windows XP
	 */
	private static final String OS = StringUtil
			.toLowerCaseUS(System.getProperty("os.name"));

	public static final boolean MAC_OS = OS.startsWith("mac");
	public static final boolean WINDOWS = OS.startsWith("windows");
	public static final boolean LINUX = OS.startsWith("linux");

	// make sure still works in the future on eg Windows 10/11
	// note Java 7u40 returns "Windows 8" for Windows 8.1 and Windows 10
	private static final boolean WINDOWS_XP_OR_EARLIER = OS
			.startsWith("windows 2000") || OS.startsWith("windows 95")
			|| OS.startsWith("windows 98") || OS.startsWith("windows nt")
			|| OS.startsWith("windows xp");

	public static final boolean WINDOWS_VISTA_OR_LATER = WINDOWS
			&& !WINDOWS_XP_OR_EARLIER;

	public static final boolean WINDOWS_VISTA_OR_EARLIER = WINDOWS_XP_OR_EARLIER
			|| OS.startsWith("windows vista");

	@Override
	public boolean isHTML5Applet() {
		return false;
	}

	@Override
	public DialogManagerMinimal getDialogManager() {

		if (dialogManager == null) {
			if (getGuiManager() == null) {
				dialogManager = new DialogManagerMinimal(this);
			} else {
				dialogManager = getGuiManager().getDialogManager();
			}
		}

		return (DialogManagerMinimal) dialogManager;
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
		return cmdArgs;
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
		}
		return 2; // right
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
		}
		return 2; // right
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

		if (!CASFactory.isInitialized()) {
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
		return getLocalization().isRightToLeftReadingOrder()
				? ComponentOrientation.RIGHT_TO_LEFT
				: ComponentOrientation.LEFT_TO_RIGHT;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setComponentOrientation(Component c) {
		boolean rtl = getLocalization().isRightToLeftReadingOrder();
		ComponentOrientation orientation = rtl
				? ComponentOrientation.RIGHT_TO_LEFT
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
			((JTextField) c).setHorizontalAlignment(
					rtl ? SwingConstants.RIGHT : SwingConstants.LEFT);
		} else if (c instanceof JComboBox) {
			JComboBox cb = (JComboBox) c;
			ListCellRenderer renderer = cb.getRenderer();
			if (!(renderer instanceof DashListRenderer
					|| renderer instanceof DecorationListRenderer
					|| renderer instanceof AxesStyleListRenderer
					|| renderer instanceof PointStyleListRenderer)) {
				// if we didn't load GUI yet, assume there is no tool creation
				// dialog
				if (getGuiManager() == null
						|| !getGuiManager().belongsToToolCreator(renderer)) {
					renderer = new DefaultListCellRenderer();
					cb.setRenderer(renderer);
				}
				((JLabel) renderer).setHorizontalAlignment(
						rtl ? SwingConstants.RIGHT : SwingConstants.LEFT);
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
		if (getLocalization().isRightToLeftReadingOrder()) {
			panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		} else {
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		}
	}

	// **************************************************************************
	// SINGULAR
	// **************************************************************************

	private class initializeSingularWS_thread implements Runnable {
		protected initializeSingularWS_thread() {
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
					.getTimeout() * 1000L) && t.isAlive()) {
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
			AnimationExportSlider num, int n, double initVal, double min,
			double max, double stepSize) {
		double val = initVal;
		double step = stepSize;
		for (int i = 0; i < n; i++) {

			// avoid values like 14.399999999999968
			val = DoubleUtil.checkDecimalFraction(val);

			num.setValue(val);
			num.updateRepaint();

			Image img = GBufferedImageD.getAwtBufferedImage(
					((EuclidianViewD) ev).getExportImage(1));
			if (img == null) {
				Log.error("image null");
			} else {
				gifEncoder.addFrame((BufferedImage) img);
			}

			val += step;

			if (val > max + Kernel.STANDARD_PRECISION
					|| val < min - Kernel.STANDARD_PRECISION) {
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

	/**
	 * Initializes the sign in Operation and tries to login in the user with the
	 * stored token
	 */
	protected void initSignInEventFlow() {

		// Inizialize the login operation
		loginOperation = new LoginOperationD(this);

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
	public CommandDispatcher newCommandDispatcher(Kernel kernel) {
		return new CommandDispatcherJre(kernel);
	}

	@Override
	public CommandDispatcher3D newCommand3DDispatcher(Kernel kernel) {
		return new CommandDispatcher3DJre(kernel);
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

		getCopyPaste()
				.copyToXML(ad,
						new ArrayList<>(ad.getKernel()
				.getConstruction().getGeoSetWithCasCellsConstructionOrder()),
				true);

		// and paste
		getCopyPaste().pasteFromXML(this, true);

		// forgotten something important!
		// ad should be closed!
		ad.exit();
		// this is also needed to make it possible
		// to load the same file once again
		ad.getFrame().dispose();

	}

	protected AppD newAppForTemplateOrInsertFile() {
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
		getKernel().updateConstruction(true);

		// almost forgotten something important!
		// ad should be closed!
		ad.exit();
		// this is also needed to make it possible
		// to load the same style file once again
		ad.getFrame().dispose();

	}

	private boolean popupsDone = false;

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
					public void run() {
						boolean showDockPopup = true;

						LoginOperationD signInOp = (LoginOperationD) getLoginOperation();
						if (signInOp.isTubeAvailable()
								&& !signInOp.isLoggedIn()) {
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

		String skipLogin = GeoGebraPreferencesD.getPref()
				.loadPreference(GeoGebraPreferencesD.USER_LOGIN_SKIP, "false");

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

	private PrintPreviewD printPreview;

	private OptionsMenuController optionsMenu;

	private static volatile MessageDigest md5EncrypterD;

	@Override
	public void schedulePreview(Runnable scheduledPreview) {

		cancelPreview();

		handler = scheduler.schedule(scheduledPreview,
				SCHEDULE_PREVIEW_DELAY_IN_MILLISECONDS, TimeUnit.MILLISECONDS);
	}

	@Override
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
		default:
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

	public static void loadFile(AppD app, File currentFile) {
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

	@Override
	public boolean isDesktop() {
		return true;
	}

	@Override
	public CopyPaste getCopyPaste() {

		if (copyPaste == null) {
			copyPaste = new CopyPaste();
		}

		return copyPaste;
	}

	@Override
	public void invokeLater(Runnable runnable) {
		SwingUtilities.invokeLater(runnable);

	}

	@Override
	public void newGeoGebraToPstricks(AsyncOperation<GeoGebraExport> callback) {
		callback.callback(new GeoGebraToPstricksD(this));
	}

	@Override
	public void newGeoGebraToAsymptote(
			AsyncOperation<GeoGebraExport> callback) {
		callback.callback(new GeoGebraToAsymptoteD(this));
	}

	@Override
	public void newGeoGebraToPgf(AsyncOperation<GeoGebraExport> callback) {
		callback.callback(new GeoGebraToPgfD(this));
	}

	public void setPrintPreview(PrintPreviewD printPreviewD) {
		printPreview = printPreviewD;
	}

	public PrintPreviewD getPrintPreview() {
		return printPreview;
	}

	/**
	 * 
	 * @param url
	 * @return url converted to a data URI if possible. If not, returns the URL
	 *         unaltered
	 */
	@Override
	protected String convertImageToDataURIIfPossible(String url) {

		// hard-code in desktop
		// * don't need to worry about download size
		// * works offline
		if (GeoGebraConstants.GEOGEBRA_LOADING_PNG.equals(url)) {
			return StringUtil.pngMarker
					+ "iVBORw0KGgoAAAANSUhEUgAAAWgAAAA+CAMAAAAxm3G5AAACPVBMVEUAAAD////Ly8v///+UlJT////////////z8/P///+wsLDi4uJubm78/Pz4+Pj///////////96enrs7Oy+vr7////////////////X19eGhoaUlJT///////+hoaH///////////////////////////////////////9vb2////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////96enqHh4f////////////////////////////////////////////////////////////////////////////////////////////////////////////////////Gxsb///////+0tLT///+ampofHx/X19fb29sBAQEHBwcODg7l5eWJiYn///////9DQ0O9vb2jo6P29vaqqqpwcHBJSUnLy8uAgIBFRUU0NDR/f3+enp4yMjIpKSmdnZ3u7u6qqqrKysoFBQV1dXUkJCT///9lZWWYmP8AAACoqKhMTID29vbPz89ubm4KCg8GBgYdHTDw8PDf398TEyDs7OwmJkA/Pz9ycsBfX6CRkZGPj/B8fNCfn5+BgYF4eHg5OWAwMFA4ODgqKioQEBCFheBycr9NTU0wMDBeXl4YGBj6+vpCQnBoaGhXV1d7e89VVZBpabB5IoDqAAAAk3RSTlMA/b/50Pzw98/7w8Px49ja7fPkyMAJ5+TpwNnP9gTI9ahHsfLFlPEi1/DSzUQGAr9sUBYNGBHct97KmmfU0LShe0A3HqWLcY9+eFhKKSDj2FU9u2M6Gw8L4MfCdSa5gKyXh2BNhFo0MS0rE+td/qqd/p7+/fz9/PTn/PwQB/37yPPu6Ofm9/Dv7eXk49/d3djDoJ3CiGq2AAAXqElEQVR42u1cBXcbRxDW6nR3OsmyJNsCO6pBZtmO7ZiZGWJK4thO4nCapE2ZmZnkXCkpMzO3v623vEdu2r6m7WunfW0iLcx8Ozs7MzsrjzM1VB89lu7rjEciyURNxUDTSpvnb6aWocGOcCSqSpKaKk+2N040t/7p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";
		}

		if (GeoGebraConstants.APPLET_PLAY_PNG.equals(url)) {
			return StringUtil.pngMarker
					+ "iVBORw0KGgoAAAANSUhEUgAAAFAAAABQCAMAAAC5zwKfAAAAhFBMVEUAAABmZmb///9qampsbGz+/v6qqqpvb29ubm6enp6rq6t3d3d1dXVxcXH6+vqCgoJ+fn719fXv7+/n5+eTk5N0dHSXl5eNjY2KioqIiIji4uLe3t6UlJSQkJDr6+vW1tbCwsK5ubmioqK7u7uzs7OmpqaFhYXk5OR7e3vNzc3Jycmvr6833NujAAAAAXRSTlMAQObYZgAAAlhJREFUWMPVmNuW2jAMRS2Ogk0JCZBwvzPATNv//7/SdrqyBoxl2U/dH7DXUeSLYvP/czrPAPAdgG+DTNsCRKAvMJKlQwLIA4BWb2st02tAQ13OFcloSncUR2wnmGKZG5k3gKIBRN8H6YAQ0pGacchHKawEnxo4wadn5vdZSmbr37rpYOcRgjKA4NMzevQtBaF68TBlgogOQ5Wavxww/ta1I4e0iOQBx6poTmwpFtf5BuShnPTuNH2GvmbrPeF7f6jWu1jlLNhi/lb0PpVNbaH6iqW3gH7R+8fk+9BSBG3XEn/Cjv0RHB+RpIR3iubMiBS2QWGn3LSOo64DhEvuqN5XFhERSUrYMT3MHAVgSehRnsCCUCj5ieYMDpa8lRM+dmduERigakHoYbKvnV+4vAuXJJTsVR4An7C+C8dSQj9rfnE+LJ0o9PMT/jYPZonCDfuF1zJR+PZC2E8UNsNX1/MtRVhtxnh1Oiz0y6ZYDxgv1+FCnbC5gkMDslI4PQp7WSecrJ+ulxxhNV1YiOchYoXFumUmEoUc1+Vi3xdvvrqbNcWE0x/MJGGihZPDeASKFQ6lkqtp7UAypfkEwYTV+9ZBN34hlHB/YdYO7lePEdvi776wluLAxQQjYvO7Fx8WKUP2HPSMPV1KBkWzFP8bmUmBe3gYoEyQ9Gcmr8GOEWXB5pFzXkbzTEkZ1J1HXbTc4fzGIOPVRv6A+VXDBOBEXyAjSMfYSOTEy30QgjNRrEBRsOaBOIKb0TAWwmFrtOwgFJvAyuLBCiKUA5PDrp4xs3PsmO1wrpXl8wsCuyePXN5O7AAAAABJRU5ErkJggg==";
		}

		return url;
	}

	@Override
	public void resetCurrentFile() {
		setCurrentFile(null);
	}

	@Override
	public void exportStringToFile(String ext, String content) {
		try {
			StringBuilder fileName = new StringBuilder();
			fileName.append("test.");
			fileName.append(ext);
			BufferedWriter objBufferedWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(fileName.toString()), "UTF-8"));
			Log.debug("Export to " + fileName);
			objBufferedWriter.write(content);
			objBufferedWriter.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void handleImageExport(String base64image) {
		if (base64image.startsWith("<svg") || base64image.startsWith("<?xml")
				|| base64image.startsWith("%PDF")) {
			copyTextToSystemClipboard(base64image);
			return;
		}

		byte[] png;
		try {
			png = Base64.decode(base64image.getBytes(Charsets.getUtf8()));
			ByteArrayInputStream bis = new ByteArrayInputStream(png);
			BufferedImage image = ImageIO.read(bis);
			copyImageToClipboard(image);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

	private static void copyImageToClipboard(Image img) {
		ImageSelection imgSel = new ImageSelection(img);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgSel,
				null);
	}

	public void showReinstallMessage() {
		Object[] options = { loc.getMenu("Cancel"), loc.getMenu("Download") };
		int n = JOptionPane.showOptionDialog(mainComp, loc.getMenu("FullReinstallNeeded"),
				GeoGebraConstants.APPLICATION_NAME + " - "
						+ getLocalization().getError("Error"),
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null,
				options, // the titles of buttons
				options[1]); // default button title

		if (n == 1) {
			showURLinBrowser(GeoGebraConstants.INSTALLERS_URL);
		}
	}

	@Override
	public GeoImage createImageFromString(final String imgFileName,
			String imgBase64, GeoImage imageOld, boolean autoCorners, String c1,
			String c2, String c4) {
		GeoImage geoImage = imageOld != null ? imageOld
				: new GeoImage(getKernel().getConstruction());

		kernel.getApplication().getImageManager().addExternalImage(imgFileName,
				imgBase64);
		geoImage.setImageFileName(imgFileName);
		return geoImage;
	}

	@Override
	public String md5Encrypt(String s) {
		return md5EncryptStatic(s);
	}

	public static String md5EncryptStatic(String s) {
		if (getMd5Encrypter() == null) {
			return UUID.randomUUID().toString();
		}
		getMd5Encrypter().update(s.getBytes(Charsets.getUtf8()));
		byte[] md5hash = md5EncrypterD.digest();
		return StringUtil.convertToHex(md5hash);
	}

	public static synchronized MessageDigest getMd5Encrypter() {
		if (md5EncrypterD == null) {
			try {
				md5EncrypterD = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}

		return md5EncrypterD;
	}

	public void isShowingLogInDialog() {
		// for 3D
	}

	@Override
	public String getModeIconBase64(int m) {
		ImageIcon icon = getModeIcon(m);
		Image img1 = icon.getImage();

		BufferedImage img2 = ImageManagerD.toBufferedImage(img1);
		return StringUtil.pngMarker + GgbAPID.base64encode(img2, 72);
	}

	/**
	 * Append XML describing the keyboard to given string builder
	 *
	 * @param sb
	 *            string builder
	 */
	@Override
	public void getKeyboardXML(StringBuilder sb) {
		sb.append("<keyboard width=\"");
		sb.append(((KeyboardSettings) getSettings().getKeyboard())
				.getKeyboardWidth());
		sb.append("\" height=\"");
		sb.append(((KeyboardSettings) getSettings().getKeyboard())
				.getKeyboardHeight());
		sb.append("\" opacity=\"");
		sb.append(((KeyboardSettings) getSettings().getKeyboard())
				.getKeyboardOpacity());
		sb.append("\" language=\"");
		sb.append(((KeyboardSettings) getSettings().getKeyboard())
				.getKeyboardLocale());
		sb.append("\" show=\"");
		sb.append(((KeyboardSettings) getSettings().getKeyboard())
				.isShowKeyboardOnStart());
		sb.append("\"/>");
	}

	@Override
	public AbstractSettings getKeyboardSettings(
			AbstractSettings keyboardSettings) {
		if (keyboardSettings == null) {
			return new KeyboardSettings();
		}
		return new KeyboardSettings(keyboardSettings.getListeners());
	}

	@Override
	public void updateKeyboardSettings(LinkedHashMap<String, String> attrs) {
		try {
			int width = Integer.parseInt(attrs.get("width"));
			KeyboardSettings kbs = (KeyboardSettings) getSettings()
					.getKeyboard();
			kbs.setKeyboardWidth(width);
			int height = Integer.parseInt(attrs.get("height"));
			kbs.setKeyboardHeight(height);
			double opacity = Double.parseDouble(attrs.get("opacity"));
			kbs.setKeyboardOpacity(opacity);
			boolean showOnStart = Boolean.parseBoolean(attrs.get("show"));
			kbs.setShowKeyboardOnStart(showOnStart);
			kbs.setKeyboardLocale(attrs.get("language"));
		} catch (RuntimeException e) {
			e.printStackTrace();
			Log.error("error in element <keyboard>");
		}
	}

	@Override
	public SettingsBuilder newSettingsBuilder() {
		return new SettingsBuilderD(this);
	}

	@Override
	protected SettingsUpdaterBuilder newSettingsUpdaterBuilder() {
		return new SettingsUpdaterBuilderD(this);
	}

	@Override
	public DefaultSettings getDefaultSettings() {
		if (defaultSettings == null) {
			defaultSettings = new DefaultSettingsD();
		}
		return defaultSettings;
	}
}
