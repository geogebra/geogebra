/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop.main;

import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Locale;
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
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
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
import org.geogebra.common.export.pstricks.GeoGebraToAsymptote;
import org.geogebra.common.export.pstricks.GeoGebraToPgf;
import org.geogebra.common.export.pstricks.GeoGebraToPstricks;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.factories.CASFactory;
import org.geogebra.common.factories.Factory;
import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.factories.LaTeXFactory;
import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.geogebra3D.io.OFFHandler;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.io.XMLParseException;
import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.javax.swing.GImageIcon;
import org.geogebra.common.jre.factory.FormatFactoryJre;
import org.geogebra.common.jre.gui.MyImageJre;
import org.geogebra.common.jre.headless.AppDI;
import org.geogebra.common.jre.kernel.commands.CommandDispatcher3DJre;
import org.geogebra.common.jre.main.TemplateHelper;
import org.geogebra.common.jre.util.Base64;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.geos.AnimationExportSlider;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.main.ProverSettings;
import org.geogebra.common.main.SpreadsheetTableModel;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.DefaultSettings;
import org.geogebra.common.main.settings.SettingsBuilder;
import org.geogebra.common.main.settings.updater.SettingsUpdaterBuilder;
import org.geogebra.common.media.VideoManager;
import org.geogebra.common.plugin.ScriptManager;
import org.geogebra.common.util.AsyncOperation;
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
import org.geogebra.common.util.lang.Language;
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
import org.geogebra.desktop.euclidian.event.MouseEventUtil;
import org.geogebra.desktop.euclidianND.EuclidianViewInterfaceD;
import org.geogebra.desktop.export.GeoGebraTubeExportD;
import org.geogebra.desktop.export.PrintPreviewD;
import org.geogebra.desktop.export.pstricks.ExportGraphicsFactoryD;
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
import org.geogebra.desktop.gui.toolbar.ToolbarContainer;
import org.geogebra.desktop.gui.util.ImageSelection;
import org.geogebra.desktop.headless.GFileHandler;
import org.geogebra.desktop.io.MyXMLioD;
import org.geogebra.desktop.io.OFFReader;
import org.geogebra.desktop.javax.swing.GImageIconD;
import org.geogebra.desktop.kernel.geos.GeoElementGraphicsAdapterD;
import org.geogebra.desktop.main.settings.DefaultSettingsD;
import org.geogebra.desktop.main.settings.SettingsBuilderD;
import org.geogebra.desktop.main.settings.updater.FontSettingsUpdaterD;
import org.geogebra.desktop.main.undo.UndoManagerD;
import org.geogebra.desktop.move.ggtapi.models.LoginOperationD;
import org.geogebra.desktop.plugin.GgbAPID;
import org.geogebra.desktop.plugin.ScriptManagerD;
import org.geogebra.desktop.sound.SoundManagerD;
import org.geogebra.desktop.util.CopyPasteD;
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

/**
 * GeoGebra Application
 *
 * @author Markus Hohenwarter
 */
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
	 * Names for geogebra.jar.
	 */
	public final static String GEOGEBRA_JAR = "geogebra.jar";

	// ==============================================================
	// LOCALE fields
	// ==============================================================

	// ==============================================================
	// FILE fields
	// ==============================================================

	private static final LinkedList<File> fileList = new LinkedList<>();
	protected File currentPath;
	protected File currentImagePath;
	protected File currentFile = null;

	/**
	 * maximum number of files to (save &amp;) show in File &rarr; Recent
	 * submenu
	 */
	public static final int MAX_RECENT_FILES = 8;

	// ==============================================================
	// RESOURCE fields
	// ==============================================================

	private final LocalizationD loc;

	// ==============================================================
	// GUI fields
	// ==============================================================

	private JFrame frame;

	/** Main component */
	protected Component mainComp;

	/** Panels that form the main content panel */
	protected JPanel centerPanel;
	protected JPanel northPanel;
	protected JPanel southPanel;
	protected JPanel eastPanel;
	protected JPanel westPanel;

	/**
	 * Split pane panel that holds main content panel and a slide-out sidebar
	 * help panel for the input bar.
	 */
	private JSplitPane applicationSplitPane;

	private DockBarInterface dockBar;
	private boolean showDockBar = true;
	private boolean isDockBarEast = true;

	/**
	 * Preferred application frame size. Used in case frame size needs updating.
	 */
	private GDimension preferredSize;

	/** Horizontal page margin in cm */
	public static final double PAGE_MARGIN_X = (1.8 * 72) / 2.54;

	/** Vertical page margin in cm */
	public static final double PAGE_MARGIN_Y = (1.8 * 72) / 2.54;

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

	protected boolean isErrorDialogShowing = false;

	public boolean macsandbox = false;

	private CopyPasteD copyPaste;
	private int centerX;
	private int centerY;

	/*************************************************************
	 * Construct application within JFrame
	 * 
	 * @param args command line arguments
	 * @param frame frame
	 * @param undoActive whether undo is active
	 */
	public AppD(CommandLineArguments args, JFrame frame, boolean undoActive) {
		this(args, frame, null, undoActive, new LocalizationD(2));
	}

	/*************************************************************
	 * Construct application within Container (e.g. GeoGebraPanel)
	 * 
	 * @param args command line arguments
	 * @param comp parent panel
	 * @param undoActive whether undo is active
	 */
	public AppD(CommandLineArguments args, Container comp, boolean undoActive) {
		this(args, null, comp, undoActive, new LocalizationD(2));
	}

	/*************************************************************
	 * GeoGebra application general constructor
	 *
	 * @param args command line arguments
	 * @param comp parent panel
	 * @param frame frame
	 * @param undoActive whether undo is active
	 * @param loc localization
	 */
	public AppD(CommandLineArguments args, JFrame frame, Container comp,
			boolean undoActive,
			LocalizationD loc) {

		super(Platform.DESKTOP);

		this.loc = loc;
		loc.setApp(this);
		this.cmdArgs = args;

		setFileVersion(GeoGebraConstants.VERSION_STRING,
				getConfig().getAppCode());

		if (args != null) {
			handleHelpVersionArgs(args);
		}

		if (frame != null) {
			mainComp = frame;
		} else {
			mainComp = comp;
		}

		useFullGui = true;

		getCodeBase(); // initialize runningFromJar

		Log.debug("runningFromJar=" + runningFromJar);
		// don't want to redirect System.out and System.err when running from IDE
		if (runningFromJar) {
			setUpLogging();
		} else {
			Log.debug("Not setting up logging via LogManager");
		}
		System.setProperty("io.sf.carte.echosvg.warn_destination", "false");
		// needed for JavaScript getCommandName(), getValueString() to work
		// (security problem running non-locally)

		preferredSize = new GDimensionD(800, 600);

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
		GeoGebraPreferencesD.getPref().initDefaultXML(this);

		if (ggtloading) {
			GeoGebraPreferencesD.getPref().loadXMLPreferences(this);
		}

		// open file given by startup parameter
		handleOptionArgsEarly(args); // for --regressionFile=...

		boolean fileLoaded = handleFileArg(args);

		// initialize GUI
		if (isUsingFullGui()) {
			initGuiManager();
			// set frame
			if (frame != null) {
				setFrame(frame);
			}
		}

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

		if (isUsingFullGui()) {
			getGuiManager().getLayout()
					.setPerspectiveOrDefault(getTmpPerspective());
		}

		if (needsSpreadsheetTableModel) {
			// if tableModel==null, will create one
			getSpreadsheetTableModel();
		}

		if (isUsingFullGui() && ggtloading) {
			getGuiManager().setToolBarDefinition(ToolBar.getAllTools(this));
		}

		setUndoActive(undoActive);

		// applet/command line options
		handleOptionArgs(args);

		initing = false;

		// for key listening
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.addKeyEventDispatcher(this);

		getFactory();

		setSaved();

		// user authentication handling
		initSignInEventFlow();
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

	/**
	 * @param frame app frame
	 */
	public void setFrame(JFrame frame) {
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
					+ "  --language=LANGUAGE_CODE"
					// here "auto" is also accepted
					+ "\t\tset language using locale strings, e.g. en, de, de_AT, ...\n"
					+ "  --showAlgebraInput=BOOLEAN\tshow/hide algebra input field\n"
					+ "  --showAlgebraInputTop=BOOLEAN\tshow algebra input at top/bottom\n"
					+ "  --showAlgebraWindow=BOOLEAN\tshow/hide algebra window\n"
					+ "  --showSpreadsheet=BOOLEAN\tshow/hide spreadsheet\n"
					// here "disable" is also accepted
					+ "  --showCAS=BOOLEAN\tshow/hide CAS window\n"
					// here "disable" is also accepted
					+ "  --show3D=BOOLEAN\tshow/hide 3D window\n"
					+ "  --showSplash=BOOLEAN\tenable/disable the splash screen\n"
					+ "  --enableUndo=BOOLEAN\tenable/disable Undo\n"
					+ "  --fontSize=NUMBER\tset default font size\n"
					+ "  --showAxes=BOOLEAN\tshow/hide coordinate axes\n"
					+ "  --showGrid=BOOLEAN\tshow/hide grid\n"
					+ "  --settingsFile=PATH|FILENAME\tload/save settings from/in a local file\n"
					+ "  --resetSettings\treset current settings\n"
					+ "  --regressionFile=FILENAME"
							+ "\texport textual representations of dependent objects, then exit\n"
					+ "  --versionCheckAllow=SETTING"
							+ "\tallow version check (on/off or true/false for single launch)\n"
					+ "  --logLevel=LEVEL\tset logging level "
							+ "(EMERGENCY|ALERT|CRITICAL|ERROR|WARN|NOTICE|INFO|DEBUG|TRACE)\n"
					+ "  --logFile=FILENAME\tset log file\n"
					+ "  --silent\tCompletely mute logging\n"
					+ "  --prover=OPTIONS\tSet options for the prover subsystem "
							+ "(use --proverhelp for more information)\n"
			);

			AppD.exit(0);
		}
		if (args.containsArg("proverhelp")) {
			ProverSettings proverSettings = ProverSettings.get();
			// help message for the prover
			System.out.println(
					"  --prover=OPTIONS\tset options for the prover subsystem\n"
							+ "    where OPTIONS is a comma separated list, formed with the "
							+ "following available settings (defaults in brackets):\n"
							+ "      engine:ENGINE\tset engine "
							+ "(Auto|OpenGeoProver|Recio|Botana|PureSymbolic) ["
							+ proverSettings.proverEngine + "]\n"
							+ "      timeout:SECS\tset the maximum time attributed to the prover"
							+ " (in seconds) ["
							+ proverSettings.proverTimeout + "]\n"
							+ "      maxterms:NUMBER\tset the maximal number of terms ["
							+ proverSettings.getMaxTerms()
							+ "] (OpenGeoProver only)\n"
							+ "      method:METHOD\tset the method (Wu|Groebner|Area) ["
							+ proverSettings.proverMethod
							+ "] (OpenGeoProver/Recio only)\n"
							+ "      usefixcoords:NUMBER1NUMBER2\tuse fix coordinates for the first"
							+ " NUMBER1 for Prove and NUMBER2 for ProveDetails, maximum of 4 both ["
							+ proverSettings.useFixCoordinatesProve
							+ proverSettings.useFixCoordinatesProveDetails
							+ "] (Botana only)\n"
							+ "      captionalgebra:BOOLEAN\tshow algebraic debug information"
							+ " in object captions ["
							+ proverSettings.captionAlgebra
							+ "] (Botana only)\n"
							+ "  Example: --prover=engine:Botana,timeout:10,"
							+ "fpnevercoll:true,usefixcoords:43\n");
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
	 * @param component component
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
		if (args.containsArg("prover")) {
			String[] proverOptions = args.getStringValue("prover").split(",");
			for (String proverOption : proverOptions) {
				setProverOption(proverOption);
			}
		}
	}

	// **************************************************************************
	// STATUS
	// **************************************************************************

	@Override
	final public boolean isApplet() {
		return false;
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

	private static boolean virtualKeyboardActive = false;

	public static boolean isVirtualKeyboardActive() {
		return virtualKeyboardActive;
	}

	public static void setVirtualKeyboardActive(boolean active) {
		virtualKeyboardActive = active;
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

	/**
	 * @param file currently open file
	 */
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

	/**
	 * Add to first position in the recent file list
	 * @param file file
	 */
	public static void addToFileList(File file) {
		if ((file == null) || !file.exists()) {
			return;
		}

		// add or move fileName to front of list
		fileList.remove(file);
		fileList.addFirst(file);
	}

	/**
	 * @param i index
	 * @return recent file with given index
	 */
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
			((GuiManagerD) getGuiManager()).clearInputbar();
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
		getGuiManager().updateToolbarDefinition();
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
				CommandLineArguments windowArgs = args.getGlobalArguments().add(key, fileArgument);
				SwingUtilities.invokeLater(() -> GeoGebraFrame.createNewWindow(windowArgs));
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

		boolean success;

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
	 * @return success
	 * @throws IOException if URL can't be opened
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

		return end == page.length() || end == begin // attribute value not
		// terminated or empty
				? null : page.substring(begin, end);
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
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(url.openStream(),
						StandardCharsets.UTF_8))) {
			StringBuilder page = new StringBuilder();
			String line;
			while (null != (line = reader.readLine())) {
				page.append(line); // page does not contain any line breaks
				// '\n', '\r' or "\r\n"
			}
			return page.toString();
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
	 * @return whether  just the euclidian view is visible in the document just loaded.
	 */
	private boolean isJustEuclidianVisible() {
		Perspective docPerspective = getTmpPerspective();

		if (docPerspective == null) {
			return false;
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

	/**
	 * needed for padding in Windows XP or earlier without check, checkbox isn't
	 * shown in Vista, Win 7
	 */
	public void setEmptyIcon(JCheckBoxMenuItem cb) {
		if (!WINDOWS) {
			cb.setIcon(getEmptyIcon());
		}
	}

	private static int ptToPx(int points) {
		int px;
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

	/**
	 * @param res resource
	 * @param borderColor border color
	 * @return scaled icon
	 */
	public ImageIcon getScaledIcon(ImageResourceD res, Color borderColor) {
		ImageIcon icon = imageManager.getImageIcon(res, borderColor);
		return scaleIcon(icon, getScaledIconSize());
	}

	/**
	 * @param res resource
	 * @return scaled icon
	 */
	public ImageIcon getScaledIconCommon(ImageResourceD res) {
		ImageIcon icon = imageManager.getImageIcon(res, null);
		return scaleIcon(icon, getScaledIconSize());
	}

	/**
	 * @param res resource
	 * @param iconSize icon size
	 * @return scaled icon
	 */
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

	/**
	 * @param fileName filename
	 * @return scaled image
	 */
	public Image getScaledInternalImage(ImageResourceD fileName) {
		MyImageD img = imageManager.getInternalImage(fileName);
		int iconSize = getScaledIconSize();
		return img.getImage().getScaledInstance(iconSize, iconSize, 0);
	}

	/**
	 *
	 * @param modeText mode name
	 * @param borderColor border color
	 * @return tool icon
	 */
	public ScaledIcon getToolBarImage(String modeText, Color borderColor) {

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
		int maxSize = imageManager.getMaxIconSize();
		return imageManager.getResponsiveScaledIcon(icon, maxSize);
	}

	/**
	 * @param border border color
	 * @return tool icon
	 */
	public ImageIcon getToolIcon(Color border) {
		ImageResourceD res;
		if (imageManager.getMaxIconSize() <= 32 && imageManager.getPixelRatio() <= 1.0) {
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

	/***
	 * @return returns VIEW_REFRESH image
	 */
	public Image getRefreshViewImage() {
		// don't need to load gui jar as reset image is in main jar
		return getMenuInternalImage(GuiResourcesD.VIEW_REFRESH);
	}

	/***
	 * @return returns NAV_PLAY image
	 */
	public Image getPlayImage() {
		// don't need to load gui jar as reset image is in main jar
		return imageManager.getInternalImage(GuiResourcesD.NAV_PLAY).getImage();
	}

	/***
	 * @return returns NAV_PLAY_CIRCLE image
	 */
	public Image getPlayImageCircle() {
		// don't need to load gui jar as reset image is in main jar
		return imageManager.getInternalImage(GuiResourcesD.NAV_PLAY_CIRCLE)
				.getImage();
	}

	/***
	 * @return returns NAV_PLAY_HOVER image
	 */
	public Image getPlayImageCircleHover() {
		// don't need to load gui jar as reset image is in main jar
		return imageManager.getInternalImage(GuiResourcesD.NAV_PLAY_HOVER)
				.getImage();
	}

	/***
	 * @return returns NAV_PAUSE_CIRCLE image
	 */
	public Image getPauseImageCircle() {
		// don't need to load gui jar as reset image is in main jar
		return imageManager.getInternalImage(GuiResourcesD.NAV_PAUSE_CIRCLE)
				.getImage();
	}

	/***
	 * @return returns NAV_PAUSE_CIRCLE_HOVER image
	 */
	public Image getPauseImageCircleHover() {
		// don't need to load gui jar as reset image is in main jar
		return imageManager
				.getInternalImage(GuiResourcesD.NAV_PAUSE_CIRCLE_HOVER)
				.getImage();
	}

	/***
	 * @return returns NAV_PAUSE image
	 */
	public Image getPauseImage() {
		// don't need to load gui jar as reset image is in main jar
		return imageManager.getInternalImage(GuiResourcesD.NAV_PAUSE)
				.getImage();
	}

	/***
	 *
	 * @param filename filename
	 * @return returns image by path
	 */
	@Override
	public MyImageD getExternalImage(String filename) {
		return ImageManagerD.getExternalImage(filename);
	}

	/***
	 *
	 * @param filename filename
	 * @param width width
	 * @param height height
	 * @return returns image by path
	 */
	@Override
	public final MyImage getExternalImageAdapter(String filename, int width,
			int height) {
		return ImageManagerD.getExternalImage(filename);
	}

	/***
	 * adds external image
	 * @param filename filename
	 * @param image image
	 */
	@Override
	public void addExternalImage(String filename, MyImageJre image) {
		imageManager.addExternalImage(filename, image);
	}

	@Override
	public GImageIcon wrapGetModeIcon(int mode) {
		return new GImageIconD(getModeIcon(mode));
	}

	/***
	 * returns an ImageIcon based on a given integer mode
	 * @param mode mode
	 * @return imageIcon
	 */
	public ScaledIcon getModeIcon(int mode) {
		ScaledIcon icon;

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
					icon = new ScaledIcon(getToolIcon(border), imageManager.getPixelRatio());
				} else {
					// use image as icon
					int size = imageManager.getMaxScaledIconSize();
					icon = new ScaledIcon(new ImageIcon(ImageManagerD.addBorder(img.getImage()
							.getScaledInstance(size, -1, Image.SCALE_SMOOTH),
							border, null)), imageManager.getPixelRatio());
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
	 * @return file content
	 */
	public String loadTextFile(String s) {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try {
			InputStream is = AppD.class.getResourceAsStream(s);
			br = new BufferedReader(
					new InputStreamReader(is, StandardCharsets.UTF_8));
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

	/***
	 * copies the contents of a EuclidianView object to the clipboard
	 * @param copyView copyView
	 */
	public void copyGraphicsViewToClipboard(final EuclidianView copyView) {

		getSelectionManager().clearSelectedGeos(true, false);
		updateSelection(false);

		Thread runner = new Thread(() -> {
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
		});
		runner.start();

	}

	static void simpleExportToClipboard(EuclidianView ev) {
		double scale = getMaxScaleForClipBoard(ev);

		// copy drawing pad to the system clipboard
		Image img = GBufferedImageD.getAwtBufferedImage(ev.getExportImage(scale));
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
			base64Image = StringUtil.removePngMarker(base64Image);
		}
		handleImageExport(base64Image);
	}

	private static Rectangle screenSize = null;

	/***
	 * gets the screensize (taking into account toolbars etc)
	 * @return screensize
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
		Language lang = Language.fromLanguageTagOrLocaleString(languageISOCode);
		return Locale.forLanguageTag(lang.toLanguageTag());
	}

	@Override
	public void setTooltipLanguage(String ttLanguage) {
		setTooltipLanguage(StringUtil.empty(ttLanguage) ? null
				: Language.fromLanguageTagOrLocaleString(ttLanguage));
	}

	/**
	 * @param ttLanguage tooltip language
	 */
	public void setTooltipLanguage(Language ttLanguage) {
		boolean updateNeeded = loc.setTooltipLanguage(ttLanguage);

		updateNeeded = updateNeeded || (loc.getTooltipLanguage() != null);

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
		setLocale(getLocale(s));
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

	/***
	 * Sets the locale of the application.
	 * The locale determines the language and cultural settings that the application should use.
	 * @param locale locale
	 */
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
			fontManager.setLanguage(loc);
		} catch (Exception e) {
			showGenericError(e);

			// go back to previous locale
			loc.setLocale(oldLocale);
		}

		getLocalization().updateLanguageFlags(loc.getLocale().getLanguage());
		if (guiManager != null) {
			guiManager.updateFonts();
		}
	}

	/**
	 * @return current locale
	 */
	public Locale getLocale() {
		return loc.getLocale();
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
		updateComponentTreeUI();

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

			if (showDockBar) {
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

			getFontSettingsUpdater().resetFonts();
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

	/**
	 * updates the main center panel of the application's user interface with the appropriate
	 * components depending on whether the full GUI or just the Euclidian view is being used.
	 * @param updateUI updateUI
	 */
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

	/**
	 * validates the frame
	 */
	public void validateComponent() {
		if (frame != null) {
			frame.validate();
		}
	}

	/**
	 * This method updates the toolbar in the GUI.
	 * If the toolbar is not supposed to be shown or if the GUI is
	 * currently being initialized, the method returns without making any updates.
	 */
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

	/**
	 * updateMenuWindow
	 */
	public void updateMenuWindow() {
		if (!showMenuBar || !isUsingFullGui() || isIniting()) {
			return;
		}

		getGuiManager().updateMenuWindow();
		getGuiManager().updateMenuFile();
	}

	/**
	 * updates frame title
	 */
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

	/**
	 * returns the JFrame that contains the GeoGebra application
	 * Only one thread can access this method at a time, which is necessary to prevent concurrency
	 * issues when multiple threads try to access or modify the frame variable simultaneously.
	 * @return JFrame
	 */
	public synchronized JFrame getFrame() {
		if ((frame == null) && (getGuiManager() != null)) {
			frame = ((GuiManagerD) getGuiManager()).createFrame();
		}

		return frame;
	}

	public Component getMainComponent() {
		return mainComp;
	}

	public GDimension getPreferredSize() {
		return preferredSize;
	}

	@Override
	public void setPreferredSize(GDimension size) {
		preferredSize = size;
	}

	/**
	 * getContentPane
	 * @return content pane of the frame
	 */
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

	/**
	 * showToolBarHelp
	 * @return boolean showToolBarHelp
	 */
	public boolean showToolBarHelp() {
		return showToolBarHelp;
	}

	/**
	 * updateToolBarLayout
	 */
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

	/**
	 * showDockBar
	 * @return showDockBar
	 */
	public boolean isShowDockBar() {
		return showDockBar;
	}

	public boolean isDockBarEast() {
		return isDockBarEast;
	}

	/**
	 *  Indicates whether the dock bar should be oriented to the east or not
	 * @param isDockBarEast boolean
	 */
	public void setDockBarEast(boolean isDockBarEast) {
		this.isDockBarEast = isDockBarEast;
		if (getDockBar() != null) {
			dockBar.setEastOrientation(isDockBarEast);
		}
	}

	/**
	 * Set show dockBar with GUI update
	 * 
	 * @param showDockBar whether to show the dockbar
	 */
	public void setShowDockBar(boolean showDockBar) {
		setShowDockBar(showDockBar, true);
	}

	/**
	 * sets whether the dockbar should be shown
	 * @param showDockBar boolean
	 * @param update boolean
	 */
	public void setShowDockBar(boolean showDockBar, boolean update) {
		this.showDockBar = showDockBar;
		if (update) {
			updateContentPane();
		}
	}

	// ***************************************************************************
	// TOOL TIPS
	// **************************************************************************

	/**
	 * @return always true
	 * @deprecated you should probably call EuclidianView.getAllowToolTips() instead
	 */
	@Deprecated
	public boolean getAllowToolTips() {
		return true;
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

		if (loc.getTooltipLanguage() != null) {
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

	/**
	 * If cursor is null, create a cursor using a 16x16 image with all pixels set to transparent.
	 * @return returns a transparent cursor, which is a cursor that is not visible on the screen.
	 */
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

	/**
	 * exit
	 */
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
	 * @return success
	 */
	public boolean loadFile(File file, boolean isMacroFile) {

		if (!checkFileExistsAndShowFileNotFound(file)) {
			return false;
		}

		return loadExistingFile(file, isMacroFile);

	}

	private OFFHandler offHandler;

	/**
	 * This is a method that loads an OFF file (Object File Format) into the application.
	 * @param file file
	 * @return loading status
	 */
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

	/**
	 * Loads XML file
	 * @param file file
	 * @param isMacroFile boolean
	 * @return loading status
	 */
	public boolean loadExistingFile(File file, boolean isMacroFile) {

		setWaitCursor();
		if (!isMacroFile) {
			// hide navigation bar for construction steps if visible
			setHideConstructionProtocolNavigation();
		}

		return loadXML(file, isMacroFile);
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

			boolean success;

			// pretend we're initializing the application to prevent unnecessary
			// update
			if (!initing) {
				initing = true;
				success = doLoadXML(fis, isMacroFile);
				initing = false;
			} else {
				success = doLoadXML(fis, isMacroFile);
			}

			if (success && !isMacroFile) {
				setCurrentFile(file);
			}

			return success;
		} catch (Exception e) {
			setCurrentFile(null);
			Log.debug(e);
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
			boolean success = doLoadXML(url.openStream(),
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

	private boolean doLoadXML(InputStream inputStream, boolean isMacroFile)
			throws IOException, XMLParseException {
		storeFrameCenter();
		boolean ok = GFileHandler.loadXML(this, inputStream, isMacroFile);
		if (ok) {
			hideDockBarPopup();
		}
		return ok;
	}

	/**
	 * loads an XML file as a String
	 * @param xml construction XML
	 * @return whether loading was successful
	 */
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
		} catch (RuntimeException | XMLParseException err) {
			setCurrentFile(null);
			Log.debug(err);
			return false;
		}
	}

	private void storeFrameCenter() {
		centerX = getWindowCenterX();
		centerY = getWindowCenterY();
	}

	/**
	 * re-centers window
	 */
	public void centerFrame() {
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

	public byte[] getMacroFileAsByteArray() {
		return getMacroFileAsByteArray(kernel.getAllMacros());
	}

	/**
	 * @param untilMacro first macro not to be serialized (null to serialize all)
	 * @return first several macros serialized to byte array, until given macro
	 */
	public byte[] getMacrosBefore(Macro untilMacro) {
		ArrayList<Macro> previousMacros = new ArrayList<>();
		for (Macro macro: kernel.getAllMacros()) {
			if (macro == untilMacro) {
				break;
			}
			previousMacros.add(macro);
		}
		return getMacroFileAsByteArray(previousMacros);
	}

	private byte[] getMacroFileAsByteArray(ArrayList<Macro> macros) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			getXMLio().writeMacroStream(os, macros, kernel.getAllMacros());
			os.flush();
			return os.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * loadMacroFileFromByteArray
	 * @param byteArray byteArray
	 * @param removeOldMacros removeOldMacros
	 */
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

	/**
	 * getOFFHandler
	 * @return OFFHandler
	 */
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
	 * @return the CodeBase URL.
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
			if (path.endsWith(GEOGEBRA_JAR)) {
				runningFromJar = true;
				path = path.substring(0,
						path.length() - GEOGEBRA_JAR.length());
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

	/**
	 * startDispatchingEventsTo
	 * @param comp component to dispatch events to
	 */
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

	/**
	 * stopDispatchingEvents
	 */
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

	/**
	 * getGlassPane
	 * @return Component
	 */
	public Component getGlassPane() {
		if (mainComp == frame) {
			return frame.getGlassPane();
		}
		return null;
	}

	/**
	 * setGlassPane
	 * @param component component
	 */
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
					rootComp.setVisible(false);
					return true;
				}
			}

			// key event came from another window or applet: ignore it
			if (!inExternalWindow(this, eventPane)) {
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

	/**
	 * check for alt pressed (but not ctrl) (or ctrl but not alt on MacOS)
	 * @param e event
	 * @return isAltDown
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

	/**
	 * @param isMetaDown whether meta key is down
	 * @param isControlDown whether ctrl key is down
	 * @return whether to treat event as ctrl key down (depends on OS)
	 */
	public static boolean isControlDown(boolean isMetaDown,
			boolean isControlDown) {

		// multiple selection
		return (MAC_OS && isMetaDown) // Mac: meta down for
				// multiple selection, Ctrl for other OS
				|| (!MAC_OS && isControlDown);
	}

	public static boolean isMiddleClick(MouseEventND e) {
		return e.isMiddleClick();
	}

	/**
	 * isRightClickForceMetaDown
	 * @param e event
	 * @return boolean
	 */
	public static boolean isRightClickForceMetaDown(MouseEvent e) {
		return (MAC_OS && e.isControlDown()) // Mac: ctrl click = right click
						|| (e.isMetaDown()); // non-Mac: right click = meta click
	}

	/**
	 * removeTraversableKeys
	 * @param p panel
	 */
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
					Log.trace("" + msg);

					// make sure splash screen not showing (will be in front)
					GeoGebra.hideSplash();

					isErrorDialogShowing = true;
					final String msgDisplay = msg.substring(0,
							Math.min(msg.length(), 1000));
					// use SwingUtilities to make sure this gets executed in the
					// correct
					// (=GUI) thread.
					SwingUtilities.invokeLater(() -> {
						// TODO investigate why this freezes Firefox
						// sometimes
						JOptionPane.showConfirmDialog(mainComp, msgDisplay,
								GeoGebraConstants.APPLICATION_NAME + " - "
										+ getLocalization()
												.getError("Error"),
								JOptionPane.DEFAULT_OPTION,
								JOptionPane.WARNING_MESSAGE);
						isErrorDialogShowing = false;
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

	/**
	 * @param message message to show in confirm dialog
	 */
	public void showMessage(final String message) {
		// use SwingUtilities to make sure this gets executed in the correct
		// (=GUI) thread.
		SwingUtilities.invokeLater(() -> JOptionPane.showConfirmDialog(mainComp, message,
				GeoGebraConstants.APPLICATION_NAME + " - "
						+ getLocalization().getMenu("Info"),
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE));
	}

	// **************************************************************************
	// SCRIPTING: GgbAPI
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
					"Logging into explicitly defined file, not using LogManager");
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
		LoggerD.setTimeShown(false); // do not print the time twice

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

		System.setOut(new PrintStream(los, true, StandardCharsets.UTF_8));
		logger = Logger.getLogger("stderr");
		los = new LoggingOutputStream(logger, StdOutErrLevel.STDERR);
		System.setErr(new PrintStream(los, true, StandardCharsets.UTF_8));

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

	/**
	 * gets a String from the clipboard
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
			drawEquation = new DrawEquationD(getFrame());
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

	@Override
	public void callAppletJavaScript(String string, String args) {
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
	public void evalJavaScript(App app, String script, String arg) {
		((ScriptManagerD) getScriptManager()).evalJavaScript(app, script, arg);
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
	public boolean isMiddleClick(AbstractEvent e) {
		return isMiddleClick((MouseEventND) e);
	}

	/**
	 *  returns an AWT Font that can display a given string with the specified properties
	 * @param string the string to be displayed
	 * @param serif whether the font should be serif or not
	 * @param fontStyle whether the font should be plain, italic, or bold-italic
	 * @param size font size
	 * @return AWT Font
	 */
	public Font getFontCanDisplayAwt(String string, boolean serif, int fontStyle,
			int size) {
		return ((GFontD) getFontManager().getFontCanDisplay(string, serif, fontStyle,
				size)).getAwtFont();
	}

	public Font getFontCanDisplayAwt(String string) {
		return GFontD.getAwtFont(getFontCanDisplay(string));
	}

	/**
	 *  returns a font that can display the string given
	 * @param value string to be displayed
	 * @param fontStyle font style
	 * @return AWT Font
	 */
	public Font getFontCanDisplayAwt(String value, int fontStyle) {
		int fontSize = settings.getFontSettings().getAppFontSize();
		GFont font = getFontCreator().newSansSerifFont(value, fontStyle, fontSize);
		return GFontD.getAwtFont(font);
	}

	@Override
	public boolean isMacOS() {
		return MAC_OS;
	}

	@Override
	public boolean isWindows() {
		return WINDOWS;
	}

	/*
	 * current possible values http://mindprod.com/jgloss/properties.html AIX
	 * Digital Unix FreeBSD HP UX Irix Linux Mac OS Mac OS X MPE/iX Netware 4.11
	 * OS/2 Solaris Windows 2000 Windows 7 Windows 95 Windows 98 Windows NT
	 * Windows Vista Windows XP
	 */
	private static final String OS = StringUtil
			.toLowerCaseUS(System.getProperty("os.name"));
	private static final String VERSION = StringUtil
			.toLowerCaseUS(System.getProperty("os.version"));

	public static final boolean MAC_OS = OS.startsWith("mac");
	public static final boolean WINDOWS = OS.startsWith("windows");
	public static final boolean LINUX = OS.startsWith("linux");

	/**
	 * @return true if running on Mac OS Big Sur or later versions.
	 */
	public static boolean isMacOsBigSurOrLater() {
		if (!MAC_OS) {
			return false;
		}
		try {
			double version = Double.parseDouble(VERSION);
			return version > 10.15;
		} catch (NumberFormatException exception) {
			return false;
		}
	}

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

	/**
	 * uploadToGeoGebraTube
	 */
	public void uploadToGeoGebraTube() {
		GeoGebraTubeExportD ggbtube = new GeoGebraTubeExportD(this);
		ggbtube.uploadWorksheet();
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
	 * @return Left/Right as appropriate for eg Hebrew / Arabic
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
	 * @return Left/Right as appropriate for eg Hebrew / Arabic
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
		SwingUtilities.invokeLater(() -> geo1.runClickScripts(string));
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

	/**
	 * Sets the component orientation of the given Component
	 * based on the reading order of the current localization
	 * @param c Component
	 */
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
	 * @param panel panel
	 */
	public void setFlowLayoutOrientation(JPanel panel) {
		if (getLocalization().isRightToLeftReadingOrder()) {
			panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		} else {
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		}
	}

	// **************************************************************************
	// ConstructionProtocol
	// **************************************************************************

	/**
	 *  exports an animated GIF of an EuclidianView.
	 *  The GIF is generated by iterating through a range of values
	 *  and capturing an image of the view for each.
	 * @param ev EuclidianView
	 * @param gifEncoder gifEncoder
	 * @param num AnimationExportSlider
	 * @param n n
	 * @param initVal initVal
	 * @param min min
	 * @param max max
	 * @param stepSize stepSize
	 */
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

			BufferedImage img = GBufferedImageD.getAwtBufferedImage(
					ev.getExportImage(1));
			if (img == null) {
				Log.error("image null");
			} else {
				gifEncoder.addFrame(img);
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
		// Inizialize the login operation -- stub only, no sign in UI in desktop
		loginOperation = new LoginOperationD();
	}

	@Override
	public CommandDispatcher newCommandDispatcher(Kernel kernel) {
		return new CommandDispatcher3DJre(kernel);
	}

	/**
	 * only for 3D so thumbnail can be generated. Overridden in App3D
	 */
	public void uploadToGeoGebraTubeOnCallback() {

		uploadToGeoGebraTube();
	}

	@Override
	public void set1rstMode() {
		setMode(((GuiManagerD) this.getGuiManager()).getToolbarPanel()
				.getFirstToolbar().getFirstMode());
	}

	/**
	 * @param file file to insert
	 */
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
			getXMLio().processXMLString(ad.getAllMacrosXML(), false, true);

			// alternative solution
			// app.addMacroXML(ad.getKernel().getMacroXML(
			// ad.getKernel().getAllMacros()));
		} catch (Exception ex) {
			Log.debug("Could not load any macros at \"Insert File\"");
			ex.printStackTrace();
		}

		// afterwards, the file is loaded into "ad" in theory,
		// so we have to use the CopyPaste class to copy it

		getCopyPaste().insertFrom(ad, this);

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

	/**
	 * @param file template file
	 */
	final public void applyTemplate(File file) {

		// using code from newWindowAction, combined with
		// Michael's suggestion
		// true as undo info is necessary for copy-paste!
		AppD ad = newAppForTemplateOrInsertFile();

		// now, we have to load the file into AppD
		ad.loadFile(file, false);

		new TemplateHelper(this).applyTemplate(ad);

		// almost forgotten something important!
		// ad should be closed!
		ad.exit();
		// this is also needed to make it possible
		// to load the same style file once again
		ad.getFrame().dispose();

	}

	private boolean popupsDone = false;

	/**
	 * shows pop up
	 */
	public void showPopUps() {
		if (isAllowPopups()) {

			// Show login popup
			if (!popupsDone) {
				popupsDone = true;

				EventQueue.invokeLater(() -> {
					if (isShowDockBar()) {
						showPerspectivePopup();
					}
				});
			}
		}
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

	/**
	 * returns an ImageIcon for a given ImageResourceD object
	 * @param res ImageResourceD
	 * @return ImageIcon
	 */
	public ImageIcon getMenuIcon(ImageResourceD res) {
		if (isMacOS()) {
			// fixed-size, 16x16 icons for mac menu
			return getScaledIcon(res, 16);
		}

		return getScaledIcon(res, null);
	}

	/**
	 * get image based on name
	 * @param name name of image
	 * @return Image
	 */
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
	public boolean hasMultipleSelectModifier(AbstractEvent e) {
		if (!(e instanceof MouseEventD)) {
			return false;
		}
		return MouseEventUtil.hasMultipleSelectModifier((MouseEventD) e);
	}

	@Override
	public GTimer newTimer(GTimerListener listener, int delay) {
		return new GTimerD(listener, delay);
	}

	private final ScheduledExecutorService scheduler = Executors
			.newScheduledThreadPool(1);

	private ScheduledFuture<?> handler;

	private PrintPreviewD printPreview;

	private static volatile MessageDigest md5EncrypterD;

	@Override
	public void schedulePreview(final Runnable scheduledPreview) {

		cancelPreview();

		Runnable threadSafeCallback = () -> SwingUtilities.invokeLater(scheduledPreview);
		handler = scheduler.schedule(threadSafeCallback,
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

	/**
	 * initializes toolbar
	 * @param app AppD
	 * @param toolbarPosition toolbarPosition
	 * @param showToolBarHelp showToolBarHelp
	 * @param northPanel northPanel
	 * @param eastPanel eastPanel
	 * @param southPanel southPanel
	 * @param westPanel westPanel
	 */
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

	/**
	 * initializes input bar
	 * @param app AppD
	 * @param showInputTop whether the input top should be shown
	 * @param northPanel JPanel
	 * @param southPanel JPanel
	 */
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

	/**
	 * getMenuBarPanel
	 * @param appD AppD
	 * @param applicationPanel JPanel
	 * @return JPanel
	 */
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
	public CopyPasteD getCopyPaste() {

		if (copyPaste == null) {
			copyPaste = new CopyPasteD();
		}

		return copyPaste;
	}

	@Override
	public void invokeLater(Runnable runnable) {
		SwingUtilities.invokeLater(runnable);

	}

	@Override
	public void newGeoGebraToPstricks(AsyncOperation<GeoGebraExport> callback) {
		callback.callback(new GeoGebraToPstricks(this, new ExportGraphicsFactoryD()));
	}

	@Override
	public void newGeoGebraToAsymptote(
			AsyncOperation<GeoGebraExport> callback) {
		callback.callback(new GeoGebraToAsymptote(this, new ExportGraphicsFactoryD()));
	}

	@Override
	public void newGeoGebraToPgf(AsyncOperation<GeoGebraExport> callback) {
		callback.callback(new GeoGebraToPgf(this, new ExportGraphicsFactoryD()));
	}

	public void setPrintPreview(PrintPreviewD printPreviewD) {
		printPreview = printPreviewD;
	}

	public PrintPreviewD getPrintPreview() {
		return printPreview;
	}

	/**
	 * @param url online image URL
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
				+ "iVBORw0KGgoAAAANSUhEUgAAAWgAAAA+CAMAAAAxm3G5AAACPVBMVEUAAAD////Ly8v///+UlJT////"
					+ "////////z8/P///+wsLDi4uJubm78/Pz4+Pj///////////96enrs7Oy+vr7///////////////"
					+ "/X19eGhoaUlJT///////+hoaH///////////////////////////////////////9vb2///////"
					+ "///////////////////////////////////////////////////////////////////////////"
					+ "//////////////////////////////////////////////////////////////96enqHh4f////"
					+ "///////////////////////////////////////////////////////////////////////////"
					+ "/////////////////////////////////////Gxsb///////+0tLT///+ampofHx/X19fb29sBA"
					+ "QEHBwcODg7l5eWJiYn///////9DQ0O9vb2jo6P29vaqqqpwcHBJSUnLy8uAgIBFRUU0NDR/f3+e"
					+ "np4yMjIpKSmdnZ3u7u6qqqrKysoFBQV1dXUkJCT///9lZWWYmP8AAACoqKhMTID29vbPz89ubm4"
					+ "KCg8GBgYdHTDw8PDf398TEyDs7OwmJkA/Pz9ycsBfX6CRkZGPj/B8fNCfn5+BgYF4eHg5OWAwMF"
					+ "A4ODgqKioQEBCFheBycr9NTU0wMDBeXl4YGBj6+vpCQnBoaGhXV1d7e89VVZBpabB5IoDqAAAAk"
					+ "3RSTlMA/b/50Pzw98/7w8Px49ja7fPkyMAJ5+TpwNnP9gTI9ahHsfLFlPEi1/DSzUQGAr9sUBYN"
					+ "GBHct97KmmfU0LShe0A3HqWLcY9+eFhKKSDj2FU9u2M6Gw8L4MfCdSa5gKyXh2BNhFo0MS0rE+t"
					+ "d/qqd/p7+/fz9/PTn/PwQB/37yPPu6Ofm9/Dv7eXk49/d3djDoJ3CiGq2AAAXqElEQVR42u1cBX"
					+ "cbRxDW6nR3OsmyJNsCO6pBZtmO7ZiZGWJK4thO4nCapE2ZmZnkXCkpMzO3v623vEdu2r6m7Wunf"
					+ "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";
		}

		if (GeoGebraConstants.APPLET_PLAY_PNG.equals(url)) {
			return StringUtil.pngMarker
					+ "iVBORw0KGgoAAAANSUhEUgAAAFAAAABQCAMAAAC5zwKfAAAAhFBMVEUAAABmZmb///9qampsbGz"
					+ "+/v6qqqpvb29ubm6enp6rq6t3d3d1dXVxcXH6+vqCgoJ+fn719fXv7+/n5+eTk5N0dHSXl5eNjY"
					+ "2KioqIiIji4uLe3t6UlJSQkJDr6+vW1tbCwsK5ubmioqK7u7uzs7OmpqaFhYXk5OR7e3vNzc3Jy"
					+ "cmvr6833NujAAAAAXRSTlMAQObYZgAAAlhJREFUWMPVmNuW2jAMRS2Ogk0JCZBwvzPATNv//7/S"
					+ "drqyBoxl2U/dH7DXUeSLYvP/czrPAPAdgG+DTNsCRKAvMJKlQwLIA4BWb2st02tAQ13OFcloSnc"
					+ "UR2wnmGKZG5k3gKIBRN8H6YAQ0pGacchHKawEnxo4wadn5vdZSmbr37rpYOcRgjKA4NMzevQtBa"
					+ "F68TBlgogOQ5Wavxww/ta1I4e0iOQBx6poTmwpFtf5BuShnPTuNH2GvmbrPeF7f6jWu1jlLNhi/"
					+ "lb0PpVNbaH6iqW3gH7R+8fk+9BSBG3XEn/Cjv0RHB+RpIR3iubMiBS2QWGn3LSOo64DhEvuqN5X"
					+ "FhERSUrYMT3MHAVgSehRnsCCUCj5ieYMDpa8lRM+dmduERigakHoYbKvnV+4vAuXJJTsVR4An7C"
					+ "+C8dSQj9rfnE+LJ0o9PMT/jYPZonCDfuF1zJR+PZC2E8UNsNX1/MtRVhtxnh1Oiz0y6ZYDxgv1+"
					+ "FCnbC5gkMDslI4PQp7WSecrJ+ulxxhNV1YiOchYoXFumUmEoUc1+Vi3xdvvrqbNcWE0x/MJGGih"
					+ "ZPDeASKFQ6lkqtp7UAypfkEwYTV+9ZBN34hlHB/YdYO7lePEdvi776wluLAxQQjYvO7Fx8WKUP2"
					+ "HPSMPV1KBkWzFP8bmUmBe3gYoEyQ9Gcmr8GOEWXB5pFzXkbzTEkZ1J1HXbTc4fzGIOPVRv6A+VX"
					+ "DBOBEXyAjSMfYSOTEy30QgjNRrEBRsOaBOIKb0TAWwmFrtOwgFJvAyuLBCiKUA5PDrp4xs3PsmO"
					+ "1wrpXl8wsCuyePXN5O7AAAAABJRU5ErkJggg==";
		}

		return url;
	}

	@Override
	public void resetCurrentFile() {
		setCurrentFile(null);
	}

	@Override
	public void exportStringToFile(String ext, String content, boolean showDialog) {
		try {
			File exportFile = getGuiManager().showSaveDialog(FileExtensions.get(ext),
					null, ext + " " + loc.getMenu("Files"), true, false);
			if (exportFile == null) {
				return;
			}
			BufferedWriter objBufferedWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(exportFile),
							StandardCharsets.UTF_8));
			Log.debug("Export to " + exportFile.getName());
			objBufferedWriter.write(content);
			objBufferedWriter.close();
		} catch (IOException e) {
			Log.debug(e);
		}
	}
	
	@Override
	public void handleImageExport(String base64image) {
		if (base64image.startsWith("<svg") || base64image.startsWith("<?xml")
				|| base64image.startsWith("%PDF")) {
			getCopyPaste().copyTextToSystemClipboard(base64image);
			return;
		}

		byte[] pngData;
		try {
			pngData = Base64.decode(base64image.getBytes(StandardCharsets.UTF_8));
			ByteArrayInputStream bis = new ByteArrayInputStream(pngData);
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

	@Override
	public GeoImage createImageFromString(final String imgFileName,
			String imgBase64, GeoImage imageOld, boolean autoCorners, GeoPointND c1,
			GeoPointND c2) {
		GeoImage geoImage = imageOld != null ? imageOld
				: new GeoImage(getKernel().getConstruction());

		kernel.getApplication().getImageManager().addExternalImage(imgFileName,
				imgBase64);
		geoImage.setImageFileName(imgFileName);
		geoImage.setCorner(c1, 0);
		geoImage.setCorner(c2, 1);
		return geoImage;
	}

	@Override
	public String md5Encrypt(String s) {
		return md5EncryptStatic(s);
	}

	/**
	 * @param s string to hash
	 * @return md5 hash
	 */
	public static String md5EncryptStatic(String s) {
		if (getMd5Encrypter() == null) {
			return UUID.randomUUID().toString();
		}
		getMd5Encrypter().update(s.getBytes(StandardCharsets.UTF_8));
		byte[] md5hash = md5EncrypterD.digest();
		return StringUtil.convertToHex(md5hash);
	}

	/**
	 * @return md5 encrypter
	 */
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

	@Override
	public String getModeIconBase64(int m) {
		ScaledIcon icon = getModeIcon(m);
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
		return new SettingsUpdaterBuilder(this)
				.withFontSettingsUpdater(new FontSettingsUpdaterD(this));
	}

	@Override
	public DefaultSettings getDefaultSettings() {
		if (defaultSettings == null) {
			defaultSettings = new DefaultSettingsD();
		}
		return defaultSettings;
	}
}
