/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.main;

import geogebra.AppletImplementationInterface;
import geogebra.CommandLineArguments;
import geogebra.GeoGebraAppletPreloader;
import geogebra.common.GeoGebraConstants;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.MyBoolean;
import geogebra.common.main.App;
import geogebra.common.util.StringUtil;
import geogebra.euclidian.EuclidianViewD;
import geogebra.gui.GuiManagerD;
import geogebra.plugin.GgbAPID;
import geogebra.util.Util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import netscape.javascript.JSObject;

/**
 * GeoGebra applet implementation operating on a given JApplet object.
 */
public class AppletImplementation implements AppletImplementationInterface {

	public JApplet applet;

	protected AppD app;
	protected Kernel kernel;
	private JButton btOpen;
	private DoubleClickListener dcListener;
	private EuclidianViewD ev;
	public boolean undoActive;
	public boolean showToolBar, showToolBarHelp, showAlgebraInput,
			allowStyleBar;
	public boolean enableRightClick = true;
	public boolean useBrowserForJavaScript = true;
	public boolean enableChooserPopups = true;
	public boolean errorDialogsActive = true;
	public boolean enableLabelDrags = true;
	boolean enableShiftDragZoom = true;
	boolean allowRescaling = true;
	public boolean showMenuBar = false;
	// public boolean showSpreadsheet = false;
	// public boolean showAlgebraView = false;
	boolean showResetIcon = false;
	Color bgColor, borderColor;
	private String fileStr, customToolBar;
	private int maxIconSize;
	private JFrame wnd;
	private JSObject browserWindow;
	public int width, height;
	// public static URL codeBase=null;
	// public static URL documentBase=null;

	// private JavaScriptMethodHandler javaScriptMethodHandler;
	// private boolean javascriptLoadFile=false, javascriptReset=false;
	// private String javascriptLoadFileName="";
	private GgbAPID ggbApi = null; // Ulven 29.05.08

	public String ggbOnInitParam = null;

	/** Creates a new instance of GeoGebraApplet */
	public AppletImplementation(final JApplet applet) {
		this.applet = applet;

		// Allow rescaling eg ctrl+ ctrl- in Firefox
		applet.addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {

				Component c = e.getComponent();
				App.debug("Applet resized to: " + c.getWidth()
						+ ", " + c.getHeight());

				if (allowRescaling && (app != null) && !app.runningInFrame
						&& app.onlyGraphicsViewShowing()) {
					// use just horizontal scale factors
					// under normal circumstances, these should be the same
					double zoomFactor = (double) c.getWidth() / (double) width;// (double)c.getHeight()
																				// /
																				// (double)height
																				// ;
					app.getEuclidianView1().zoomAroundCenter(zoomFactor);

				}

				// these always need updating eg draw reset icon, play/pause
				// icon
				width = c.getWidth();
				height = c.getHeight();
			}

		});

		init();
	}

	public void dispose() {
		app = null;
		kernel = null;
		browserWindow = null;
		ev = null;

		if (wnd != null) {
			// see GeoGebraFrame.dispose()
			wnd.dispose();
			wnd = null;
		}
	}

	/**
	 * Initializes the CAS, GUI components, and downloads jar files in a
	 * separate thread.
	 */
	public void initInBackground() {
		App.debug("initInBackground");

		// start animation if wanted by ggb file
		if (kernel.wantAnimationStarted()) {
			kernel.getAnimatonManager().startAnimation();
		}

		// call JavaScript function ggbOnInit()
		initJavaScript();
		Object[] noArgs = {};
		Object[] arg = { ggbOnInitParam };

		App.debug("calling ggbOnInit("
				+ (((ggbOnInitParam == null) ? "" : ggbOnInitParam)) + ")");
		app.getScriptManager().callJavaScript("ggbOnInit",
				(ggbOnInitParam == null) ? noArgs : arg);

		// give applet time to repaint
		Thread initingThread = new Thread() {
			@Override
			public void run() {
				// wait a bit for applet to draw first time
				// then start background initing of GUI elements
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				// load all jar files in background
				GeoGebraAppletPreloader.loadAllJarFiles(!app
						.useBrowserForJavaScript());

			}
		};
		initingThread.start();
	}

	private void init() {
		
		// see #2604
		try {
			geogebra.cas.mpreduce.CASmpreduceD.mpreduce_static = null;
		} catch (java.lang.NoClassDefFoundError e) {
			App.warn("CAS jar missing");
		} 

		// codeBase=this.getCodeBase();
		// documentBase=this.getDocumentBase();

		// Application.debug("codeBase="+codeBase);
		// Application.debug("documentBase="+documentBase);

		// get parameters
		// filename of construction
		fileStr = applet.getParameter("filename");
		if ((fileStr != null)
				&& !(fileStr.startsWith("http") || fileStr.startsWith("file"))) {
			// add document base to file name
			URL base = applet.getDocumentBase();
			String documentBase = base.toString();
			if (fileStr.charAt(0) == '/') {
				fileStr = base.getProtocol() + "://" + base.getHost() + fileStr;
			} else {
				String path = documentBase.substring(0,
						documentBase.lastIndexOf('/') + 1);
				fileStr = path + fileStr;
			}
		} else {
			// check if ggb file is encoded as base 64
			String fileBase64 = applet.getParameter("ggbBase64");
			if (fileBase64 != null) {
				fileStr = "base64://" + fileBase64;
			}
		}
		App.debug("loading " + fileStr);

		// showToolBar = "true" or parameter is not available
		showToolBar = "true".equals(applet.getParameter("showToolBar"));

		// showToolBar = "true" or parameter is not available
		showToolBarHelp = showToolBar
				&& "true".equals(applet.getParameter("showToolBarHelp"));

		// customToolBar = "0 1 2 | 3 4 5 || 7 8 12" to set the visible toolbar
		// modes
		customToolBar = applet.getParameter("customToolBar");

		// showMenuBar = "true" or parameter is not available
		showMenuBar = "true".equals(applet.getParameter("showMenuBar"));

		// showSpreadsheet = "true" or parameter is not available
		// showSpreadsheet =
		// "true".equals(applet.getParameter("showSpreadsheet"));

		// showAlgebraView = "true" or parameter is not available
		// showAlgebraView =
		// "true".equals(applet.getParameter("showAlgebraView"));

		// showResetIcon = "true" or parameter is not available
		showResetIcon = "true".equals(applet.getParameter("showResetIcon"));

		// showAlgebraInput = "true" or parameter is not available
		showAlgebraInput = "true".equals(applet
				.getParameter("showAlgebraInput"));

		// default is true
		useBrowserForJavaScript = !"false".equals(applet
				.getParameter("useBrowserForJS"));

		// show style bar of views, default is false
		allowStyleBar = "true".equals(applet.getParameter("allowStyleBar"));

		// rightClickActive, default is "true"
		enableRightClick = !"false".equals(applet
				.getParameter("enableRightClick"));

		// enableChooserPopups, default is "true"
		enableChooserPopups = !"false".equals(applet
				.getParameter("enableChooserPopups"));

		// errorDialogsActive, default is "true"
		errorDialogsActive = !"false".equals(applet
				.getParameter("errorDialogsActive"));

		// enableLabelDrags, default is "true"
		enableLabelDrags = !"false".equals(applet
				.getParameter("enableLabelDrags"));

		// paramter for JavaScript ggbOnInit() call
		ggbOnInitParam = applet.getParameter("ggbOnInitParam");

		// enableShiftDragZoom, default is "true"
		enableShiftDragZoom = !"false".equals(applet
				.getParameter("enableShiftDragZoom"));

		// allowRescaling, default is "false"
		allowRescaling = "true".equals(applet.getParameter("allowRescaling"));

		undoActive = (showToolBar || showMenuBar);

		// set language manually by iso language string
		String language = applet.getParameter("language");
		String country = applet.getParameter("country");
		Locale loc = null;
		if (language != null) {
			if (country != null) {
				loc = new Locale(language, country);
			} else {
				loc = new Locale(language);
			}
			applet.setLocale(loc);
		}

		// bgcolor = "#CCFFFF" specifies the background color to be used for
		// the button panel
		try {
			bgColor = Color.decode(applet.getParameter("bgcolor"));
		} catch (Exception e) {
			bgColor = Color.white;
		}

		// borderColor = "#CCFFFF" specifies the border color to be used for
		// the applet panel
		try {
			borderColor = Color.decode(applet.getParameter("borderColor"));
		} catch (Exception e) {
			borderColor = Color.gray;
		}

		// maximum icon size to be used in the toolbar
		try {
			maxIconSize = Integer.parseInt(applet.getParameter("maxIconSize"));
		} catch (Exception e) {
			maxIconSize = AppD.DEFAULT_ICON_SIZE;
		}

		// build application and open file
		/*
		 * if (fileStr == null) { app = new CustomApplication(null, this,
		 * undoActive); } else { String[] args = { fileStr }; app = new
		 * CustomApplication(args, this, undoActive); }
		 */

		AppD.setLAF(AppD.MAC_OS || AppD.WINDOWS);

		if (fileStr == null) {
			app = buildApplication(null, undoActive);
		} else {
			String[] args = { fileStr };
			app = buildApplication(new CommandLineArguments(args), undoActive);
		}

		// needed to make sure unicodeZero works
		if (loc != null) {
			app.setLanguage(loc);
		}

		kernel = app.getKernel();

		/* Ulven 29.05.08 */
		ggbApi = app.getGgbApi();
	}

	protected AppD buildApplication(CommandLineArguments args,
			boolean undoActive) {
		return new AppD(args, this, undoActive);
	}

	public AppD getApplication() {
		return app;
	}

	/**
	 * @return If the applet parameters indicate that the GUI is necessary.
	 */
	public boolean needsGui() {
		return showAlgebraInput || showToolBar || showMenuBar
				|| enableRightClick;
	}

	public void initGUI() {
		JPanel myContenPane;


		// show interactive drawing pad
		// TODO use Appication methods (F.S.)
		// create applet panel
		myContenPane = createGeoGebraAppletPanel();

		// border around applet panel
		myContenPane.setBorder(BorderFactory.createLineBorder(borderColor));


		// replace applet's content pane
		Container cp = applet.getContentPane();

		App.debug("Initial size = " + cp.getWidth() + ", "
				+ cp.getHeight());
		// Application.debug("EuclidianView size = "+app.getEuclidianView().getPreferredSize().getWidth()+", "+app.getEuclidianView().getPreferredSize().getHeight());

		width = cp.getWidth();
		height = cp.getHeight();

		if (originalWidth < 0) {
			setInitialScaling();
		}

		cp.setBackground(bgColor);
		cp.removeAll();
		cp.add(myContenPane);

		// set move mode
		app.setMoveMode();
	}

	private int originalWidth = -1, originalHeight = -1;

	/*
	 * rescales if the width is not what's expected eg if browser is zoomed
	 */
	private void setInitialScaling() {
		if (allowRescaling) {
			if (!app.runningInFrame && app.onlyGraphicsViewShowing()) {
				originalWidth = (int) ev.getPreferredSize().getWidth();
				originalHeight = (int) ev.getPreferredSize().getHeight();
				double zoomFactorX = (double) width / (double) originalWidth;
				double zoomFactorY = (double) height / (double) originalHeight;
				double zoomFactor = Math.min(zoomFactorX, zoomFactorY);
				ev.zoomAroundCenter(zoomFactor);
			} else {
				originalWidth = width;
				originalHeight = height;
			}
		}

	}

	protected JPanel createGeoGebraAppletPanel() {
		JPanel appletPanel = new JPanel(new BorderLayout());
		appletPanel.setBackground(bgColor);

		app.setUndoActive(undoActive);
		app.setShowMenuBar(showMenuBar);
		// app.setShowSpreadsheetView(showSpreadsheet);
		// app.setShowAlgebraView(showAlgebraView);
		app.setShowAlgebraInput(showAlgebraInput, true);
		app.setUseBrowserForJavaScript(useBrowserForJavaScript);
		app.setShowToolBar(showToolBar, showToolBarHelp);
		app.setRightClickEnabled(enableRightClick);
		app.setErrorDialogsActive(errorDialogsActive);
		app.setLabelDragsEnabled(enableLabelDrags);
		app.setShiftDragZoomEnabled(enableShiftDragZoom);
		if ((customToolBar != null) && (customToolBar.length() > 0)
				&& showToolBar) {
			app.getGuiManager().setToolBarDefinition(customToolBar);
		}
		app.setShowResetIcon(showResetIcon);
		app.setMaxIconSize(maxIconSize);

		app.getSettings().getLayout().setAllowStyleBar(allowStyleBar);

		appletPanel.add(app.buildApplicationPanel(), BorderLayout.CENTER);
		ev = app.getEuclidianView1();
		ev.updateBackground();

		return appletPanel;
	}

	private class DoubleClickListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				showFrame();
			}
		}
	}

	private class ButtonClickListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			showFrame();
		}
	}

	private void showFrame() {
		Thread worker = new Thread() {
			@Override
			public void run() {

				app.runningInFrame = true;

				applet.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

				doShowFrame();

				applet.setCursor(Cursor.getDefaultCursor());
			}
		};
		worker.start();
	}

	private synchronized void doShowFrame() {

		// clear applet
		Container cp = applet.getContentPane();
		cp.removeAll();
		if (ev != null) {
			ev.removeMouseListener(dcListener);
		}

		JPanel p = new JPanel(new BorderLayout());
		p.setBackground(Color.white);
		JLabel label = new JLabel("GeoGebra "
				+ app.getPlain("WindowOpened") + "...");
		label.setFont(app.getPlainFont());
		p.add(label, BorderLayout.CENTER);
		cp.add(p);

		// initialize the GeoGebra frame's UIG
		initGeoGebraFrame();
		applet.validate();


		// show frame
		wnd.setVisible(true);
	}

	private synchronized void initGeoGebraFrame() {
		// build application panel
		if (wnd == null) {
			wnd = app.getFrame();
		}

		app.setFrame(wnd);
		app.setShowMenuBar(true);
		app.setShowAlgebraInput(true, false);
		app.setUndoActive(true);
		app.setShowToolBar(true, true);
		app.setRightClickEnabled(true);

		if ((customToolBar != null) && (customToolBar.length() > 0)) {
			app.getGuiManager().setToolBarDefinition(customToolBar);
		}

		// just update layout if the layout was already visible
		// (which isn't the case in button-only mode), see ticket #217
		if (app.isUsingFullGui()) {
			((GuiManagerD) app.getGuiManager()).updateLayout();
		}

		app.updateContentPane();
		app.resetFonts();
	}

	public void showApplet() {
		Thread worker = new Thread() {
			@Override
			public void run() {
				applet.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

				wnd.setVisible(false); // hide frame

				reinitGUI();


				applet.setCursor(Cursor.getDefaultCursor());
			}
		};
		worker.start();
	}

	private void reinitGUI() {

		app.runningInFrame = false;

		Container cp = applet.getContentPane();
		cp.removeAll();

		app.setApplet(this);

		if (app.isUsingFullGui()) {
			((GuiManagerD) app.getGuiManager()).updateLayout();
		}

		initGUI();

		app.resetFonts();
		app.refreshViews();

		applet.validate();
	}

	/* JAVA SCRIPT INTERFACE */
	/*
	 * Rewritten by Ulven 29.05.08: Moved method contents to GgbAPI and put in
	 * redirections to GgbApi. (Oneliners left as they are, nothing to gain...)
	 */

	/**
	 * Returns current construction as a ggb file in form of a byte array.
	 * 
	 * @return null if something went wrong
	 */
	public synchronized byte[] getGGBfile() {
		return ggbApi.getGGBfile(); // Ulven 29.05.08
	}

	/**
	 * Returns current construction in XML format. May be used for saving.
	 */
	public synchronized String getXML() {
		return ggbApi.getXML();
	}

	/**
	 * Returns current construction in XML format. May be used for saving.
	 */
	public synchronized String getBase64() {
		return getBase64(false);
	}

	/**
	 * Returns current construction in XML format. May be used for saving.
	 */
	public synchronized String getBase64(boolean includeThumbnail) {
		return ggbApi.getBase64(includeThumbnail);
	}

	/**
	 * Returns the GeoGebra XML string for the given GeoElement object, i.e.
	 * only the <element> tag is returned.
	 */
	public String getXML(String objName) {
		return ggbApi.getXML(objName);
	}

	/**
	 * For a dependent GeoElement objName the XML string of the parent algorithm
	 * and all its output objects is returned. For a free GeoElement objName ""
	 * is returned.
	 */
	public String getAlgorithmXML(String objName) {
		return ggbApi.getAlgorithmXML(objName);
	}

	/**
	 * Opens construction given in XML format. May be used for loading
	 * constructions.
	 */
	public synchronized void setXML(String xml) {
		app.setXML(xml, true);
		reinitGUI();
	}

	/**
	 * Opens construction given in Base64 format. May be used for loading
	 * constructions.
	 */
	public synchronized void setBase64(final String base64) {
		// base64 might contain an image, calls ImageIO etc
		AccessController.doPrivileged(new PrivilegedAction<Object>() {
			public Object run() {
				// perform the security-sensitive operation here
				ggbApi.setBase64(base64);
				return null;
			}
		});

	}

	/**
	 * Evaluates the given XML string and changes the current construction.
	 * Note: the construction is NOT cleared before evaluating the XML string.
	 */
	public synchronized void evalXML(String xmlString) {
		StringBuilder sb = new StringBuilder();

		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		sb.append("<geogebra format=\"" + GeoGebraConstants.XML_FILE_FORMAT
				+ "\">\n");
		ev.getXML(sb, false);
		sb.append("<construction>\n");
		sb.append(xmlString);
		sb.append("</construction>\n");
		sb.append("</geogebra>\n");
		app.setXML(sb.toString(), false);
	}

	public synchronized boolean evalCommand(final String cmdString) {
		return evalCommand(cmdString, true);
	}

	/**
	 * Evaluates the given string as if it was entered into GeoGebra's input
	 * text field.
	 */
	public synchronized boolean evalCommand(final String cmdString, final boolean waitForResult) {
		// waitForCAS();

		// avoid security problems calling from JavaScript
		MyBoolean ret = AccessController
				.doPrivileged(new PrivilegedAction<MyBoolean>() {
					public MyBoolean run() {
						// perform the security-sensitive operation here

						// make sure translated command names are loaded
						app.initTranslatedCommands();

						return new MyBoolean(kernel, app.getGgbApi()
								.evalCommand(cmdString, waitForResult));

					}
				});

		// return success
		return ret.getBoolean();
	}

	/**
	 * Evaluates the given string as if it was entered into GeoGebra CAS's input
	 * text field.
	 * 
	 * @return evaluation result in GeoGebraCAS syntax
	 */
	public synchronized String evalGeoGebraCAS(String cmdString) {
		return evalGeoGebraCAS(cmdString, false);
	}

	/**
	 * Evaluates the given string as if it was entered into the GeoGebraCAS
	 * input text field.
	 * 
	 * @param debugOutput
	 *            states whether debugging information should be printed to the
	 *            console
	 * @return evaluation result in GeoGebraCAS syntax
	 */
	public synchronized String evalGeoGebraCAS(final String cmdString,
			final boolean debugOutput) {
		// avoid security problems calling from JavaScript
		return AccessController.doPrivileged(new PrivilegedAction<String>() {
			public String run() {
				return ggbApi.evalGeoGebraCAS(cmdString, debugOutput);
			}
		});
	}// evalGeoGebraCAS(String)

	/**
	 * Evaluates the given string using the MathPiper CAS.
	 * 
	 * @deprecated since GeoGebra 4.0, use evalGeoGebraCAS() instead
	 * 
	 *             public synchronized String evalMathPiper(String cmdString) {
	 *             //waitForCAS();
	 * 
	 *             final String str = cmdString;
	 * 
	 *             // avoid security problems calling from JavaScript return
	 *             (String)AccessController.doPrivileged(new PrivilegedAction()
	 *             { public Object run() { // perform the security-sensitive
	 *             operation here return kernel.evaluateMathPiper(str);
	 * 
	 *             } }); }
	 */

	/**
	 * Evaluates the given string using the Yacas CAS.
	 * 
	 * @deprecated since GeoGebra 4.0, use evalGeoGebraCAS() instead
	 * 
	 *             public synchronized String evalYacas(String cmdString) {
	 *             return evalMathPiper(cmdString); }
	 */

	/**
	 * prints a string to the Java Console
	 */
	public synchronized void debug(String string) {
		App.debug(string);
	}

	// /**
	// * Waits until the GeoGebraCAS has been loaded in the background.
	// * Note: the GeoGebraCAS is automatically inited in
	// Application.initInBackground();
	// */
	// private synchronized void waitForCAS() {
	// if (kernel.isGeoGebraCASready()) return;
	//
	// // TODO: remove
	// System.out.println("waiting for CAS to be inited ...");
	//
	// while (!kernel.isGeoGebraCASready()) {
	// try { Thread.sleep(50); } catch (Exception e) {}
	// }
	//
	// // TODO: remove
	// System.out.println("   CAS loaded!");
	// }

	/**
	 * Turns on the fly creation of points in graphics view on (true) or off
	 * (false). Note: this is useful if you don't want tools to have the side
	 * effect of creating points. For example, when this flag is set to false,
	 * the tool "line through two points" will not create points on the fly when
	 * you click on the background of the graphics view.
	 */
	public synchronized void setOnTheFlyPointCreationActive(boolean flag) {
		app.setOnTheFlyPointCreationActive(flag);
	}

	public synchronized void setUndoPoint() {
		kernel.getConstruction().storeUndoInfo();
	}

	/**
	 * Turns showing of error dialogs on (true) or (off). Note: this is
	 * especially useful together with evalCommand().
	 */
	public synchronized void setErrorDialogsActive(boolean flag) {
		app.setErrorDialogsActive(flag);
	}

	/**
	 * Resets the initial construction (given in filename parameter) of this
	 * applet.
	 */
	public synchronized void reset() {

		if (fileStr == null) {
			return;
		}

		if (fileStr.startsWith("base64://")) {
			byte[] zipFile;
			try {
				zipFile = geogebra.common.util.Base64.decode(fileStr
						.substring(9));
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			app.loadXML(zipFile);
		} else {
			// avoid security problems calling from JavaScript
			AccessController.doPrivileged(new PrivilegedAction<Object>() {
				public Object run() {
					// perform the security-sensitive operation here
					app.setWaitCursor();
					try {
						URL ggbURL = new URL(fileStr);
						app.loadXML(ggbURL, StringUtil.toLowerCase(fileStr)
								.endsWith(AppD.FILE_EXT_GEOGEBRA_TOOL));
						reinitGUI();
						applet.validate();
					} catch (Exception e) {
						e.printStackTrace();
					}
					app.setDefaultCursor();

					return null;

				}
			});
		}

		if (allowRescaling) {
			ev.setTemporarySize(originalHeight, originalWidth);
			double zoomFactorX = (double) width / (double) originalWidth;
			double zoomFactorY = (double) height / (double) originalHeight;
			double zoomFactor = Math.min(zoomFactorX, zoomFactorY);
			ev.zoomAroundCenter(zoomFactor);
			ev.setTemporarySize(-1, -1);
		}

	}

	/**
	 * Refreshs all views. Note: clears traces in geometry window.
	 */
	public synchronized void refreshViews() {
		app.refreshViews();
	}

	/*
	 * returns IP address
	 */
	public synchronized String getIPAddress() {
		return Util.getIPAddress();
	}

	/*
	 * returns hostname
	 */
	public synchronized String getHostname() {
		return Util.getHostname();
	}

	/**
	 * Loads a construction from a file (given URL).
	 */
	public synchronized void openFile(final String strURL) {
		// avoid security problems calling from JavaScript
		AccessController.doPrivileged(new PrivilegedAction<Object>() {
			public Object run() {
				// perform the security-sensitive operation here
				// load file
				app.setWaitCursor();
				try {
					String myStrURL = strURL;
					String lowerCase = StringUtil.toLowerCase(strURL);
					if (!(lowerCase.startsWith("http") || lowerCase
							.startsWith("file"))) {
						myStrURL = applet.getCodeBase() + myStrURL;
					}
					URL ggbURL = new URL(myStrURL);
					app.loadXML(ggbURL, lowerCase
							.endsWith(AppD.FILE_EXT_GEOGEBRA_TOOL));
					reinitGUI();
				} catch (Exception e) {
					e.printStackTrace();
				}
				app.setDefaultCursor();

				return null;
			}
		});
	}

	/**
	 * @return The border color of the applet.
	 */
	public Color getBorderColor() {
		return borderColor;
	}

	/*
	 * public synchronized void setLanguage(String isoLanguageString) {
	 * app.setLanguage(new Locale(isoLanguageString)); }
	 * 
	 * public synchronized void setLanguage(String isoLanguageString, String
	 * isoCountryString) { app.setLanguage(new Locale(isoLanguageString,
	 * isoCountryString)); }
	 */

	/**
	 * Shows or hides the object with the given name in the geometry window.
	 */
	public synchronized void setVisible(String objName, boolean visible) {
		ggbApi.setVisible(objName, visible);
	}

	public synchronized boolean getVisible(String objName) {
		return ggbApi.getVisible(objName);
	}

	/**
	 * Sets the layer of the object with the given name in the geometry window.
	 * Michael Borcherds 2008-02-27
	 */
	public synchronized void setLayer(String objName, int layer) {
		ggbApi.setLayer(objName, layer);

	}

	/**
	 * Returns the layer of the object with the given name in the geometry
	 * window. returns layer, or -1 if object doesn't exist Michael Borcherds
	 * 2008-02-27
	 */
	public synchronized int getLayer(String objName) {
		return ggbApi.getLayer(objName);
	}

	/**
	 * Shows or hides a complete layer Michael Borcherds 2008-02-27
	 */
	public synchronized void setLayerVisible(int layer, boolean visible) {
		ggbApi.setLayerVisible(layer, visible);
	}

	/**
	 * Sets the fixed state of the object with the given name.
	 */
	public synchronized void setFixed(String objName, boolean flag) {
		ggbApi.setFixed(objName, flag);
	}

	/**
	 * Turns the trace of the object with the given name on or off.
	 */
	public synchronized void setTrace(String objName, boolean flag) {
		ggbApi.setTrace(objName, flag);
	}

	/**
	 * Shows or hides the label of the object with the given name in the
	 * geometry window.
	 */
	public synchronized void setLabelVisible(String objName, boolean visible) {
		ggbApi.setLabelVisible(objName, visible);
	}

	/**
	 * Sets the label style of the object with the given name in the geometry
	 * window. Possible label styles are NAME = 0, NAME_VALUE = 1 and VALUE = 2.
	 */
	public synchronized void setLabelStyle(String objName, int style) {
		ggbApi.setLabelStyle(objName, style);
	}

	/**
	 * Shows or hides the label of the object with the given name in the
	 * geometry window.
	 */
	public synchronized void setLabelMode(String objName, boolean visible) {
		ggbApi.setLabelMode(objName, visible);
	}

	/**
	 * Sets the color of the object with the given name.
	 */
	public synchronized void setColor(String objName, int red, int green,
			int blue) {
		ggbApi.setColor(objName, red, green, blue);
	}

	public synchronized void setLineStyle(String objName, int style) {
		ggbApi.setLineStyle(objName, style);
	}

	public synchronized void setLineThickness(String objName, int thickness) {
		ggbApi.setLineThickness(objName, thickness);
	}

	public synchronized void setPointStyle(String objName, int style) {
		ggbApi.setPointStyle(objName, style);
	}

	public synchronized void setPointSize(String objName, int style) {
		ggbApi.setPointSize(objName, style);
	}

	public synchronized void setFilling(String objName, double filling) {
		ggbApi.setFilling(objName, filling);
	}

	/*
	 * used by the automatic file tester (from JavaScript)
	 */
	public synchronized String getGraphicsViewCheckSum(final String algorithm,
			final String format) {
		// avoid security problems calling from JavaScript
		return (String) AccessController
				.doPrivileged(new PrivilegedAction<Object>() {
					public Object run() {
						// perform the security-sensitive operation here
						return ggbApi
								.getGraphicsViewCheckSum(algorithm, format);
					}
				});

	}

	/**
	 * Returns the color of the object as an hex string. Note that the
	 * hex-string starts with # and uses upper case letters, e.g. "#FF0000" for
	 * red.
	 */
	public synchronized String getColor(String objName) {
		return ggbApi.getColor(objName);
	}

	public synchronized double getFilling(String objName) {
		return ggbApi.getFilling(objName);
	}

	public synchronized int getLineStyle(String objName) {
		return ggbApi.getLineStyle(objName);
	}

	public synchronized int getLineThickness(String objName) {
		return ggbApi.getLineThickness(objName);
	}

	public synchronized int getPointStyle(String objName) {
		return ggbApi.getPointStyle(objName);
	}

	public synchronized int getPointSize(String objName) {
		return ggbApi.getPointSize(objName);
	}

	/**
	 * Deletes the object with the given name.
	 */
	public synchronized void deleteObject(String objName) {
		ggbApi.deleteObject(objName);
	}

	public synchronized void setAnimating(String objName, boolean animate) {
		ggbApi.setAnimating(objName, animate);
	}

	public synchronized void setAnimationSpeed(String objName, double speed) {
		ggbApi.setAnimationSpeed(objName, speed);
	}

	public synchronized void startAnimation() {
		kernel.getAnimatonManager().startAnimation();
	}

	public synchronized void stopAnimation() {
		kernel.getAnimatonManager().stopAnimation();
	}

	public void hideCursorWhenDragging(boolean hideCursorWhenDragging) {
		((AppD) kernel.getApplication())
				.setUseTransparentCursorWhenDragging(hideCursorWhenDragging);
	}

	public synchronized boolean isAnimationRunning() {
		return kernel.getAnimatonManager().isRunning();
	}

	/**
	 * Renames an object from oldName to newName.
	 * 
	 * @return whether renaming worked
	 */
	public synchronized boolean renameObject(String oldName, String newName) {
		return ggbApi.renameObject(oldName, newName);
	}

	/**
	 * Returns true if the object with the given name exists.
	 */
	public synchronized boolean exists(String objName) {
		return ggbApi.exists(objName);
	}

	/**
	 * Returns true if the object with the given name has a vaild value at the
	 * moment.
	 */
	public synchronized boolean isDefined(String objName) {
		return ggbApi.isDefined(objName);
	}

	/**
	 * Returns true if the object with the given name is independent.
	 */
	public synchronized boolean isIndependent(String objName) {
		return ggbApi.isIndependent(objName);
	}

	/**
	 * Returns the value of the object with the given name as a string.
	 */
	public synchronized String getValueString(String objName) {
		return ggbApi.getValueString(objName);
	}

	/**
	 * Returns the definition of the object with the given name as a string.
	 */
	public synchronized String getDefinitionString(String objName) {
		return ggbApi.getDefinitionString(objName);
	}

	/**
	 * Returns the command of the object with the given name as a string.
	 */
	public synchronized String getCommandString(String objName) {
		return ggbApi.getCommandString(objName);
	}

	/**
	 * Returns the x-coord of the object with the given name. Note: returns 0 if
	 * the object is not a point or a vector.
	 */
	public synchronized double getXcoord(String objName) {
		return ggbApi.getXcoord(objName);
	}

	/**
	 * Returns the y-coord of the object with the given name. Note: returns 0 if
	 * the object is not a point or a vector.
	 */
	public synchronized double getYcoord(String objName) {
		return ggbApi.getYcoord(objName);
	}

	/**
	 * Sets the coordinates of the object with the given name. Note: if the
	 * specified object is not a point or a vector, nothing happens.
	 */
	public synchronized void setCoords(String objName, double x, double y) {
		ggbApi.setCoords(objName, x, y);
	}

	/**
	 * Returns the double value of the object with the given name. Note: returns
	 * 0 if the object does not have a value.
	 */
	public synchronized double getValue(String objName) {
		return ggbApi.getValue(objName);
	}

	/**
	 * Sets the double value of the object with the given name. Note: if the
	 * specified object is not a number, nothing happens.
	 */
	public synchronized void setValue(String objName, double x) {
		ggbApi.setValue(objName, x);
	}

	/**
	 * Turns the repainting of all views on or off.
	 */
	public synchronized void setRepaintingActive(boolean flag) {
		// Application.debug("set repainting: " + flag);
		ggbApi.setRepaintingActive(flag);
	}

	/*
	 * Methods to change the geometry window's properties
	 */

	/**
	 * Sets the Cartesian coordinate system in the graphics window.
	 */
	public synchronized void setCoordSystem(double xmin, double xmax,
			double ymin, double ymax) {
		app.getEuclidianView1().setRealWorldCoordSystem(xmin, xmax, ymin, ymax);
	}

	/**
	 * Shows or hides the x- and y-axis of the coordinate system in the graphics
	 * window.
	 */
	public synchronized void setAxesVisible(boolean xVisible, boolean yVisible) {
		ggbApi.setAxesVisible(xVisible, yVisible);
	}

	/**
	 * Shows or hides the coordinate grid in the graphics window.
	 */
	public synchronized void setGridVisible(boolean flag) {
		app.getSettings().getEuclidian(1).showGrid(flag);
		app.getSettings().getEuclidian(2).showGrid(flag);
	}

	/**
	 * Returns an array with all object names.
	 */
	public synchronized String[] getAllObjectNames() {
		return ggbApi.getObjNames();
	}

	/**
	 * Returns the number of objects in the construction.
	 */
	public synchronized int getObjectNumber() {
		return ggbApi.getObjNames().length;
	}

	/**
	 * Returns the name of the n-th object of this construction.
	 */
	public synchronized String getObjectName(int i) {
		return ggbApi.getObjectName(i);
	}

	/**
	 * Returns the type of the object with the given name as a string (e.g.
	 * point, line, circle, ...)
	 */
	public synchronized String getObjectType(String objName) {
		return ggbApi.getObjectType(objName);
	}

	/**
	 * returns a String (base-64 encoded PNG file of the Graphics View)
	 */
	public synchronized String getPNGBase64(final double exportScale,
			final boolean transparent, final double DPI) {
		// avoid security problems calling from JavaScript
		return AccessController.doPrivileged(new PrivilegedAction<String>() {
			public String run() {
				// perform the security-sensitive operation here
				return ggbApi.getPNGBase64(exportScale, transparent, DPI);
			}
		});
	}

	/**
	 * returns a String (base-64 encoded PNG file of the Graphics View)
	 */
	public synchronized boolean writePNGtoFile(final String filename,
			final double exportScale, final boolean transparent,
			final double DPI) {
		// avoid security problems calling from JavaScript
		Boolean b = AccessController
				.doPrivileged(new PrivilegedAction<Boolean>() {
					public Boolean run() {
						// perform the security-sensitive operation here
						return ggbApi.writePNGtoFile(filename, exportScale,
								transparent, DPI);
					}
				});

		return b.booleanValue();
	}

	/**
	 * Sets the mode of the geometry window (EuclidianView).
	 */
	public synchronized void setMode(int mode) {
		app.setMode(mode);
	}

	public synchronized void initJavaScript() {

		if (!app.useBrowserForJavaScript()) {
			return;
		}

		if (browserWindow == null) {
			try {
				browserWindow = JSObject.getWindow(applet);

				if (browserWindow == null) {
					App
							.debug("Warning: could not initialize JSObject.getWindow() for GeoGebraApplet");
				}

			} catch (Exception e) {
				App
						.debug("Exception: could not initialize JSObject.getWindow() for GeoGebraApplet");
			}
		}
	}

	public void callJavaScript(String jsFunction, Object[] args) {
		// Application.debug("callJavaScript: " + jsFunction);

		initJavaScript();

		try {
			if (browserWindow != null) {
				App.debug("callJavaScript: " + jsFunction);
				browserWindow.call(jsFunction, args);
			} else {
				App
						.debug("Warning: could not initialize JSObject.getWindow() for GeoGebraApplet when calling "
								+ jsFunction);
			}
		} catch (Exception e) {
			System.err.println("Warning: Error calling JavaScript function '"
					+ jsFunction + "' (" + e.getLocalizedMessage() + ")");
			// e.printStackTrace();
		}
	}

	public JApplet getJApplet() {
		return applet;
	}

	public synchronized void registerAddListener(String JSFunctionName) {
		app.getScriptManager().registerAddListener(JSFunctionName);
	}

	public synchronized void unregisterAddListener(String JSFunctionName) {
		app.getScriptManager().unregisterAddListener(JSFunctionName);
	}

	public synchronized void registerRemoveListener(String JSFunctionName) {
		app.getScriptManager().registerRemoveListener(JSFunctionName);
	}

	public synchronized void unregisterRemoveListener(String JSFunctionName) {
		app.getScriptManager().unregisterRemoveListener(JSFunctionName);
	}

	public synchronized void registerClearListener(String JSFunctionName) {
		app.getScriptManager().registerClearListener(JSFunctionName);
	}

	public synchronized void unregisterClearListener(String JSFunctionName) {
		app.getScriptManager().unregisterClearListener(JSFunctionName);
	}

	public synchronized void registerRenameListener(String JSFunctionName) {
		app.getScriptManager().registerRenameListener(JSFunctionName);
	}

	public synchronized void unregisterRenameListener(String JSFunctionName) {
		app.getScriptManager().unregisterRenameListener(JSFunctionName);
	}

	public synchronized void registerUpdateListener(String JSFunctionName) {
		app.getScriptManager().registerUpdateListener(JSFunctionName);
	}

	public synchronized void unregisterUpdateListener(String JSFunctionName) {
		app.getScriptManager().unregisterUpdateListener(JSFunctionName);
	}

	public synchronized void registerObjectUpdateListener(String objName,
			String JSFunctionName) {
		app.getScriptManager().registerObjectUpdateListener(objName,
				JSFunctionName);
	}

	public synchronized void unregisterObjectUpdateListener(String objName) {
		app.getScriptManager().unregisterObjectUpdateListener(objName);
	}

	public synchronized void registerClickListener(String JSFunctionName) {
		app.getScriptManager().registerClickListener(JSFunctionName);
	}

	public synchronized void unregisterClickListener(String JSFunctionName) {
		app.getScriptManager().unregisterClickListener(JSFunctionName);
	}

	public synchronized void registerObjectClickListener(String objName,
			String JSFunctionName) {
		app.getScriptManager().registerObjectClickListener(objName,
				JSFunctionName);
	}

	public synchronized void unregisterObjectClickListener(String objName) {
		app.getScriptManager().unregisterObjectClickListener(objName);
	}

	public boolean isMoveable(String objName) {
		return ggbApi.isMoveable(objName);
	}

	public void drawToImage(String label, double[] x, double[] y) {
		ggbApi.drawToImage(label, x, y);

	}

	public void clearImage(String label) {
		ggbApi.clearImage(label);
	}

	public void uploadToGeoGebraTube() {
		app.uploadToGeoGebraTube();
		
	}

	public void setPenColor(int red, int green, int blue) {
		ggbApi.setPenColor(red, green, blue);
		
	}

	public void setPenSize(int size) {
		ggbApi.setPenSize(size);
	}

	public int getPenSize() {
		return ggbApi.getPenSize();
	}

	public String getPenColor() {
		return ggbApi.getPenColor();
	}

	public void setListValue(String objName, int index, double x) {
		ggbApi.setListValue(objName, index, x); 		
	}

	public double getListValue(String objName, int index) {
		return ggbApi.getListValue(objName, index); 
	}

}
