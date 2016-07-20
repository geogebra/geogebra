/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.jre.util.Base64;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.AppletImplementationInterface;
import org.geogebra.desktop.CommandLineArguments;
import org.geogebra.desktop.euclidian.EuclidianViewD;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.plugin.GgbAPID;

import geogebra.GeoGebraAppletPreloader;
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

	private String perspective;

	/** Creates a new instance of GeoGebraApplet */
	public AppletImplementation(final JApplet applet) {
		this.applet = applet;

		// Allow rescaling eg ctrl+ ctrl- in Firefox
		applet.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {

				Component c = e.getComponent();
				Log.debug("Applet resized to: " + c.getWidth() + ", "
						+ c.getHeight());

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
		Log.debug("initInBackground");

		// start animation if wanted by ggb file
		if (kernel.wantAnimationStarted()) {
			kernel.getAnimatonManager().startAnimation();
		}

		// call JavaScript function ggbOnInit()
		initJavaScript();
		Object[] noArgs = {};
		Object[] arg = { ggbOnInitParam };

		Log.debug("calling ggbOnInit("
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
		Log.debug("loading " + fileStr);

		// showToolBar = "true" or parameter is not available
		showToolBar = "true".equals(applet.getParameter("showToolBar"));

		// showToolBar = "true" or parameter is not available
		showToolBarHelp = showToolBar
				&& "true".equals(applet.getParameter("showToolBarHelp"));

		// customToolBar = "0 1 2 | 3 4 5 || 7 8 12" to set the visible toolbar
		// modes
		customToolBar = applet.getParameter("customToolBar");

		perspective = applet.getParameter("perspective");

		if (perspective == null) {
			perspective = "";
		}

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

		this.ggbApi = app.getGgbApi();
	}

	protected AppD buildApplication(CommandLineArguments args,
			boolean undoActive) {
		return new AppD(args, this, undoActive);
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

		Log.debug("Initial size = " + cp.getWidth() + ", " + cp.getHeight());
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
		app.setPerspectiveParam(perspective);
		app.setErrorDialogsActive(errorDialogsActive);
		app.setLabelDragsEnabled(enableLabelDrags);
		app.setShiftDragZoomEnabled(enableShiftDragZoom);
		if ((customToolBar != null) && (customToolBar.length() > 0)
				&& showToolBar) {
			app.getGuiManager().setToolBarDefinition(customToolBar);
		}
		app.setShowResetIcon(showResetIcon);
		app.getImageManager().setMaxIconSize(maxIconSize);

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
		JLabel label = new JLabel("GeoGebra " + app.getPlain("WindowOpened")
				+ "...");
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
				getGgbApi().setBase64(base64);
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
	private synchronized String evalGeoGebraCAS(final String cmdString,
			final boolean debugOutput) {
		// avoid security problems calling from JavaScript
		return AccessController.doPrivileged(new PrivilegedAction<String>() {
			public String run() {
				return getGgbApi().evalGeoGebraCAS(cmdString, debugOutput);
			}
		});
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
				zipFile = Base64.decode(fileStr.substring(9));
			} catch (Exception e) {
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
						app.loadXML(ggbURL,
								StringUtil.getFileExtension(fileStr)
										.equals(FileExtensions.GEOGEBRA_TOOL));
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
					app.loadXML(ggbURL,
							StringUtil.getFileExtension(lowerCase)
									.equals(FileExtensions.GEOGEBRA_TOOL));
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
	 * Methods to change the geometry window's properties
	 */





	/**
	 * returns a String (base-64 encoded PNG file of the Graphics View)
	 */
	public synchronized String getPNGBase64(final double exportScale,
			final boolean transparent, final double DPI,
			final boolean copyToClipboard) {
		// avoid security problems calling from JavaScript
		return AccessController.doPrivileged(new PrivilegedAction<String>() {
			public String run() {
				// perform the security-sensitive operation here
				return getGgbApi().getPNGBase64(exportScale, transparent, DPI,
						copyToClipboard);
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
						return getGgbApi().writePNGtoFile(filename, exportScale,
								transparent, DPI);
					}
				});

		return b.booleanValue();
	}



	public synchronized void initJavaScript() {

		/*
		 * if (!app.useBrowserForJavaScript()) { return; }
		 * 
		 * if (browserWindow == null) { try { browserWindow =
		 * JSObject.getWindow(applet);
		 * 
		 * if (browserWindow == null) { Log.debug(
		 * "Warning: could not initialize JSObject.getWindow() for GeoGebraApplet"
		 * ); }
		 * 
		 * } catch (Exception e) { Log.debug(
		 * "Exception: could not initialize JSObject.getWindow() for GeoGebraApplet"
		 * ); } }
		 */
	}

	public Object callJavaScript(String jsFunction, Object[] args) {
		// Application.debug("callJavaScript: " + jsFunction);

		initJavaScript();

		try {
			if (browserWindow != null) {
				Log.debug("callJavaScript: " + jsFunction);
				return browserWindow.call(jsFunction, args);
			}

			Log.debug("Warning: could not initialize JSObject.getWindow() for GeoGebraApplet when calling "
					+ jsFunction);
		} catch (Exception e) {
			Log.error("Warning: Error calling JavaScript function '"
					+ jsFunction + "' (" + e.getLocalizedMessage() + ")");
			// e.printStackTrace();
		}

		return null;
	}

	public JApplet getJApplet() {
		return applet;
	}

	public GgbAPID getGgbApi() {
		return ggbApi;
	}

}
