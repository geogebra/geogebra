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
package org.geogebra.desktop.gui.app;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.jre.util.DownloadManager;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.AppId;
import org.geogebra.desktop.CommandLineArguments;
import org.geogebra.desktop.euclidianND.EuclidianViewInterfaceD;
import org.geogebra.desktop.export.GraphicExportDialog;
import org.geogebra.desktop.geogebra3D.euclidian3D.EuclidianView3DD;
import org.geogebra.desktop.gui.FileDropTargetListener;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.dialog.DialogManagerD;
import org.geogebra.desktop.gui.util.AnimatedGifEncoder;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.GeoGebraPreferencesD;
import org.geogebra.desktop.util.FrameCollector;
import org.geogebra.desktop.util.HttpRequestD;

import com.himamis.retex.editor.share.util.Unicode;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * GeoGebra's main window.
 */
public class GeoGebraFrame extends JFrame
		implements WindowFocusListener, Printable, ComponentListener {

	private static final long serialVersionUID = 1L;

	private static final int VERSION_CHECK_DAYS = 1;
	// This works only for subversion numbers <= 999 (change 1000 to 10000 for
	// 9999):
	private static final int VERSION_TO_LONG_MULTIPLIER = 1000;

	private static ArrayList<GeoGebraFrame> instances = new ArrayList<>();

	private static GeoGebraFrame activeInstance;
	private static Object lock = new Object();

	private static FileDropTargetListener dropTargetListener;

	private static List<NewInstanceListener> instanceListener = new ArrayList<>();

	protected AppD app;

	private Timer timer;
	private long born;

	public GeoGebraFrame() {
		instances.add(this);
		setActiveInstance(this);
		born = System.currentTimeMillis();
		this.addComponentListener(this);
	}

	// public static void printInstances() {
	// System.out.println("FRAMES: " + instances.size());
	// for (int i=0; i < instances.size(); i++) {
	// GeoGebraFrame frame = (GeoGebraFrame) instances.get(i);
	// System.out.println(" " + (i+1) + ", applet: " + frame.app.isApplet() +
	// ", "
	// + frame);
	// }
	// }

	/**
	 * Disposes this frame and removes it from the static instance list.
	 */
	@Override
	public void dispose() {
		instances.remove(this);
		if (this == activeInstance) {
			setActiveInstance(null);
		}
	}

	public AppD getApplication() {
		return app;
	}

	public void setApplication(AppD app) {
		this.app = app;
	}

	public int getInstanceNumber() {
		for (int i = 0; i < instances.size(); i++) {
			if (this == instances.get(i)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public void windowGainedFocus(WindowEvent arg0) {
		setActiveInstance(this);
		app.updateMenuWindow();
	}

	@Override
	public void windowLostFocus(WindowEvent arg0) {

		// fix for Mac OS bug: close open popups manually
		Window[] w = this.getOwnedWindows();
		for (Window win : w) {
			if (win.getClass().getName()
					.equals("javax.swing.Popup$HeavyWeightWindow")) {
				win.setVisible(false);
			}
		}
	}

	@Override
	public Locale getLocale() {
		Locale defLocale = GeoGebraPreferencesD.getPref().getDefaultLocale();

		if (defLocale == null) {
			return super.getLocale();
		}
		return defLocale;
	}

	@Override
	public void setVisible(boolean flag) {
		if (flag) {
			updateSize();

			// set location
			int instanceID = instances.size() - 1;
			if (instanceID > 0) {
				// move right and down of last instance
				GeoGebraFrame prevInstance = getInstance(instanceID - 1);
				Point loc = prevInstance.getLocation();

				// make sure we stay on screen
				Dimension d1 = getSize();
				Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
				loc.x = Math.min(loc.x + 20, dim.width - d1.width);
				loc.y = Math.min(loc.y + 20, dim.height - d1.height - 25);
				setLocation(loc);
			} else {
				// center
				setLocationRelativeTo(null);
			}

			super.setVisible(true);
			app.getActiveEuclidianView().requestFocusInWindow();
		} else {
			if (!isShowing()) {
				return;
			}

			instances.remove(this);
			GeoGebraPreferencesD.getPref().saveFileList();

			if (instances.size() == 0) {
				super.setVisible(false);
				dispose();

				if (!app.isApplet()) {
					AppD.exit(0);
				}
			} else {
				super.setVisible(false);
				updateAllTitles();
			}
		}
	}

	/**
	 * Provide temporary information about window size (for applet designers)
	 */
	@Override
	public void componentResized(ComponentEvent e) {
		if (System.currentTimeMillis() < born + 5000) {
			return;
		}

		this.setTitle((int) getSize().getWidth() + "" + Unicode.MULTIPLY + ""
				+ (int) getSize().getHeight());

		if (timer == null) {
			timer = new Timer(3000, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e1) {
					String title = getPreferredTitle();
					setTitle(title);

				}

			});
		}
		timer.setRepeats(false);
		timer.restart();

	}

	String getPreferredTitle() {
		return app.getCurrentFile() == null ? "GeoGebra Classic 5"
				: app.getCurrentFile().getName();
	}

	public void updateSize() {
		// get frame size from layout manager
		Dimension size = app.getPreferredSize();

		// check if frame fits on screen
		Rectangle screenSize = AppD.getScreenSize();

		if (size.width > screenSize.width || size.height > screenSize.height) {
			size.width = screenSize.width;
			size.height = screenSize.height;
			setLocation(0, 0);
		}

		setSize(size);
	}

	/**
	 * Main method to create initial GeoGebra window.
	 * 
	 * @param args
	 *            file name parameter
	 */
	public static synchronized void main(CommandLineArguments args) {

		init(args, new GeoGebraFrame());
	}

	public static synchronized void init(CommandLineArguments args,
			GeoGebraFrame wnd) {

		// Fixing #3772. TODO: This could be moved to somewhat later (to have
		// proper logging), but *before* any GUI operations.
		if (AppD.WINDOWS_VISTA_OR_LATER) {
			try {
				AppId.setCurrentProcessExplicitAppUserModelID("geogebra.AppId");
				System.out.println("AppID = "
						+ AppId.getCurrentProcessExplicitAppUserModelID());
			} catch (Throwable t) {
				System.err.println("problem setting AppId: " + t.getMessage());
			}
		}

		if (AppD.MAC_OS) {
			initMacSpecifics();
		}

		// set look and feel
		if (args.containsArg("laf")) {
			AppD.setLAF(args.getStringValue("laf").equals("system"));
		} else {
			// system LAF for Windows and Mac; cross-platform for LINUX, others
			AppD.setLAF(AppD.MAC_OS || AppD.WINDOWS);
		}

		if (args.containsArg("resetSettings")) {
			GeoGebraPreferencesD.getPref().clearPreferences(wnd.app);
		}
		// Set GeoGebraPreferences mode (system properties or property file)
		// before it is called for the first time
		String settingsFile = args.getStringValue("settingsfile");
		if (settingsFile.length() > 0) {
			GeoGebraPreferencesD.setPropertyFileName(settingsFile);
		}

		// load list of previously used files
		GeoGebraPreferencesD.getPref().loadFileList();

		// create first window and show it
		createNewWindow(args, wnd);

	}

	/**
	 * Returns the active GeoGebra window.
	 * 
	 * @return the active GeoGebra window.
	 */
	public static synchronized GeoGebraFrame getActiveInstance() {
		return activeInstance;
	}

	private static void setActiveInstance(GeoGebraFrame frame) {
		synchronized (lock) {
			activeInstance = frame;
		}
	}

	/**
	 * adds a NewInstanceListener, fired whenever a new Instance of
	 * GeoGebraFrame is created.
	 * 
	 * @param l
	 */
	public static void addNewInstanceListener(NewInstanceListener l) {
		instanceListener.add(l);
	}

	/**
	 * MacOS X specific initialization. Note: this method can only be run on a
	 * Mac!
	 */
	public static void initMacSpecifics() {
		try {
			// init mac application listener
			MacApplicationListener.initMacApplicationListener();

			// mac menu bar
			// System.setProperty("com.apple.macos.useScreenMenuBar", "true");
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		} catch (Exception e) {
			Log.debug(e + "");
		}
	}

	public static synchronized GeoGebraFrame createNewWindow(
			CommandLineArguments args) {
		return createNewWindow(args, new GeoGebraFrame());
	}

	/**
	 * Creates new GeoGebra window
	 * 
	 * @param args
	 *            Command line arguments
	 * @param wnd
	 * @return the new window
	 */
	// public abstract GeoGebra buildGeoGebra();

	/**
	 * return the application running geogebra
	 * 
	 * @param args
	 * @param frame
	 * @return the application running geogebra
	 */
	protected AppD createApplication(CommandLineArguments args, JFrame frame) {
		return new AppD(args, frame, true);
	}

	public synchronized GeoGebraFrame createNewWindow(CommandLineArguments args,
			Macro macro) {
		return createNewWindow(args, copy());
	}

	protected GeoGebraFrame copy() {
		return new GeoGebraFrame();
	}

	/**
	 * Creates new GeoGebra window
	 * 
	 * @param args
	 *            Command line arguments
	 * @param wnd
	 * @return the new window
	 */
	public static synchronized GeoGebraFrame createNewWindow(
			final CommandLineArguments args, GeoGebraFrame wnd) {
		// set Application's size, position and font size

		final AppD app = wnd.createApplication(args, wnd);
		// app.openPopUps(true);

		// app.getApplicationGUImanager().setMenubar(new
		// geogebra.gui.menubar.GeoGebraMenuBar(app));
		app.getGuiManager().initMenubar();

		// init GUI
		wnd.app = app;
		wnd.getContentPane().add(app.buildApplicationPanel());
		dropTargetListener = new FileDropTargetListener(app);
		wnd.setGlassPane(((GuiManagerD) app.getGuiManager()).getLayout()
				.getDockManager().getGlassPane());
		wnd.setDropTarget(new DropTarget(wnd, dropTargetListener));
		wnd.addWindowFocusListener(wnd);
		updateAllTitles();

		// handle application args visible
		if (args != null) {
			if (args.containsArg("showAlgebraWindow")) {
				boolean showAlgebraWindow = args
						.getBooleanValue("showAlgebraWindow", true);
				app.getGuiManager().setShowView(showAlgebraWindow,
						App.VIEW_ALGEBRA);
			}

			if (args.containsArg("showSpreadsheet")) {
				boolean showSpreadsheet = args
						.getBooleanValue("showSpreadsheet", true);
				app.getGuiManager().setShowView(showSpreadsheet,
						App.VIEW_SPREADSHEET);
			}

			if (args.containsArg("showCAS") && app.supportsView(App.VIEW_CAS)) {
				boolean showCAS = args.getBooleanValue("showCAS", true);
				app.getGuiManager().setShowView(showCAS, App.VIEW_CAS);
			}

		}

		app.updateMenubar();

		wnd.setVisible(true);

		// init some things in the background
		if (!app.isApplet()) {
			/*
			 * Thread runner = new Thread() { public void run() { // init
			 * properties dialog
			 * ((GuiManagerD)app.getGuiManager()).initPropertiesDialog();
			 * 
			 * // init file chooser
			 * ((GuiManagerD)app.getGuiManager()).initFileChooser();
			 * 
			 * // init CAS app.getKernel().getGeoGebraCAS();
			 * 
			 * // init JLaTeXMath Graphics2D g2d =
			 * app.getEuclidianView().g2Dtemp; Drawable.drawEquation(app,
			 * app.getEuclidianView().g2Dtemp, 0, 0, "x^{2}", g2d.getFont(),
			 * false, Color.BLACK, Color.WHITE); } };
			 */
			Thread runner = GeoGebraFrame.createAppThread(app);
			runner.start();
		}

		checkCommandLineExport(app);

		// open the sign-in and/or sidebar popup once the GUI has initialized
		if (args != null && args.getNoOfFiles() == args.getNoOfTools()) {
			app.setAllowPopups(true);
			app.showPopUps();
		}

		for (NewInstanceListener l : instanceListener) {
			l.newInstance(wnd);
		}

		return wnd;
	}

	private static AppThread createAppThread(AppD app) {
		return new AppThread(app);
	}

	private static class AppThread extends Thread {

		AppD app;

		public AppThread(AppD app) {
			this.app = app;
		}

		@Override
		public void run() {

			// init file chooser
			((DialogManagerD) this.app.getDialogManager())
					.initFileChooser();

			// init singularWS
			// No, we cannot do it here at the moment since it will break file
			// loading containing Singular,
			// so we do it AppD.java --- see [22746] which is reverted at the
			// moment.
			// app.initializeSingularWSD();

			// init JLaTeXMath
			// Graphics2D g2d =
			// this.app.getEuclidianView1().getTempGraphics2D();
			// app.getDrawEquation().drawEquation(this.app, null,
			// new GGraphics2DD(g2d), 0, 0, "x^{2}",
			// new GFontD(g2d.getFont()), false,
			// GColor.BLACK, GColor.WHITE, false, false, null);

			// check if newer version is available
			// must be done last as internet may not be available
			if (!app.isApplet()) {
				checkVersion();
			}
		}

		/**
		 * Downloads newest GeoGebra .jar files and puts them into the user's
		 * AppData directory. Also downloads license.txt.
		 * 
		 * @throws IOException
		 */
		@SuppressFBWarnings({ "RV_RETURN_VALUE_IGNORED_BAD_PRACTICE",
				"don't need to check return value of mkdirs() or delete()" })
		private static void downloadGeoGebraJars() throws IOException {
			ZipInputStream zis = null;
			try {
				// Creating working directory:
				String updateDir = System.getenv("APPDATA")
						+ GeoGebraConstants.GEOGEBRA_JARS_UPDATE_DIR;
				Log.debug("Creating " + updateDir);
				new File(updateDir).mkdirs();

				// Downloading newest .jar files in a .zip:
				String filename = updateDir + File.separator
						+ "geogebra-jars.zip";
				File dest = new File(filename);
				URL url = new URL(GeoGebraConstants.GEOGEBRA_ONLINE_JARS_ZIP);
				Log.debug("Downloading "
						+ GeoGebraConstants.GEOGEBRA_ONLINE_JARS_ZIP);
				DownloadManager.copyURLToFile(url, dest);

				// Unzipping:
				// Borrowed from
				// http://www.concretepage.com/java/read_zip_file_java.php:
				InputStream is = new FileInputStream(filename);
				zis = new ZipInputStream(is);
				ZipEntry ze;
				byte[] buff = new byte[1024];
				while ((ze = zis.getNextEntry()) != null) {
					// get file name
					String name = ze.getName();
					FileOutputStream fos = new FileOutputStream(
							updateDir + File.separator + name);
					Log.debug("Extracting " + name);
					try {

						int l = 0;
						// write buffer to file
						while ((l = zis.read(buff)) > 0) {
							fos.write(buff, 0, l);
						}
					} finally {
						fos.close();
					}
				}

				dest.delete();

			} catch (Exception e) {
				Log.error("Unsuccessful update");
			} finally {
				if (zis != null) {
					zis.close();
				}
			}
		}

		/**
		 * Checks if a newer version is available. It runs (at most) every day
		 * for major updates, but every run for minor updates.
		 */
		private void checkVersion() {
			Log.debug("Checking version");
			if (!app.getVersionCheckAllowed()) {
				Log.debug("Version check is not allowed");
				return;
			}

			String lastVersionCheck = GeoGebraPreferencesD.getPref()
					.loadPreference(GeoGebraPreferencesD.VERSION_LAST_CHECK,
							"");
			Long nowL = new Date().getTime();
			String nowLS = nowL.toString();

			boolean checkNeeded = false;

			if (lastVersionCheck == null || "".equals(lastVersionCheck)) {
				checkNeeded = true;
				Log.debug("major version check needed: no check was done yet");
			}

			else {
				Long lastVersionCheckL = Long.valueOf(lastVersionCheck);
				if (lastVersionCheckL
						+ 1000L * 60 * 60 * 24 * VERSION_CHECK_DAYS < nowL) {
					checkNeeded = true;
					Log.debug("major version check needed: lastVersionCheckL="
							+ lastVersionCheckL + " nowL=" + nowL);
				} else {
					Log.debug("no major version check needed: lastVersionCheck="
							+ lastVersionCheckL + " nowL=" + nowL);
				}
			}

			String myVersion = GeoGebraConstants.VERSION_STRING;
			HttpRequestD httpr = (HttpRequestD) UtilFactory.getPrototype()
					.newHttpRequest();
			String newestVersion = null;
			StringBuilder sb = new StringBuilder();
			Long newestVersionL;
			Long currentVersionL = versionToLong(myVersion);

			try {

				if (checkNeeded) {

					sb.append(GeoGebraConstants.VERSION_URL);
					sb.append("?ver=");
					sb.append(myVersion);
					sb.append("&os=");

					if (AppD.WINDOWS) {
						sb.append("win");
					} else if (AppD.MAC_OS) {
						sb.append("mac");
					} else {
						sb.append("linux");
					}

					sb.append("&java=");
					AppD.appendJavaVersion(sb);

					newestVersion = httpr
							.sendRequestGetResponseSync(sb.toString());

					if (newestVersion == null) {
						// probably not online
						Log.error("Problem fetching " + sb);
						return;
					}
					newestVersion = newestVersion.replaceAll("-", ".");
					newestVersionL = versionToLong(newestVersion);

					Log.debug("current=" + currentVersionL + " newest="
							+ newestVersionL);
					if (currentVersionL < newestVersionL) {
						Localization loc = app.getLocalization();
						String q = loc.getMenu("NewerVersionA").replaceAll("%0",
								newestVersion);
						String dl = loc.getMenu("GoToDownloadPage");
						Object[] options = { loc.getMenu("Cancel"), dl };
						Component comp = app.getMainComponent();
						int returnVal = JOptionPane.showOptionDialog(comp, q,
								dl, JOptionPane.DEFAULT_OPTION,
								JOptionPane.WARNING_MESSAGE, null, options,
								options[0]);
						// store date of current check only when notification
						// has been shown:
						GeoGebraPreferencesD.getPref().savePreference(
								GeoGebraPreferencesD.VERSION_LAST_CHECK, nowLS);
						if (returnVal == 1) {
							app.getGuiManager().showURLinBrowser(
									GeoGebraConstants.INSTALLERS_URL);
						}
					}
				} // checkneeded

				sb = new StringBuilder();

				sb.append(GeoGebraConstants.VERSION_URL_MINOR);
				sb.append("?ver=");
				sb.append(myVersion);
				sb.append("&os=");

				if (AppD.WINDOWS) {
					sb.append("win");
				} else if (AppD.MAC_OS) {
					sb.append("mac");
				} else {
					sb.append("linux");
				}

				sb.append("&java=");
				AppD.appendJavaVersion(sb);

				newestVersion = httpr.sendRequestGetResponseSync(sb.toString());

				if (newestVersion == null) {
					Log.error("problem fetching " + sb);
					return;
				}

				newestVersion = newestVersion.replaceAll("-", ".");
				newestVersionL = versionToLong(newestVersion);

				Log.debug("newest_minor=" + newestVersionL);
				// Windows only: automatic update

				if (AppD.WINDOWS && currentVersionL < newestVersionL) {
					downloadGeoGebraJars();
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
	}

	/**
	 * Converts a version string to a long value (e.g. 4.1.2.3 to 4001002003)
	 * 
	 * @param version
	 *            string
	 * @return long value
	 */

	static Long versionToLong(String version) {
		String[] subversions = version.split("\\.");
		Long n = 0L;
		int l = subversions.length;
		for (int i = 0; i < l; ++i) {
			String c = subversions[i];
			n = n * VERSION_TO_LONG_MULTIPLIER + Integer.parseInt(c);
		}
		return n;
	}

	public static int getInstanceCount() {
		return instances.size();
	}

	public static ArrayList<GeoGebraFrame> getInstances() {
		return instances;
	}

	static GeoGebraFrame getInstance(int i) {
		return instances.get(i);
	}

	public static void updateAllTitles() {
		for (int i = 0; i < instances.size(); i++) {
			AppD app = instances.get(i).app;
			app.updateTitle();
		}
	}

	/**
	 * Checks all opened GeoGebra instances if their current file is the given
	 * file.
	 * 
	 * @param file
	 * @return GeoGebra instance with file open or null
	 */
	public static GeoGebraFrame getInstanceWithFile(File file) {
		if (file == null) {
			return null;
		}

		try {
			String absPath = file.getCanonicalPath();
			for (int i = 0; i < instances.size(); i++) {
				GeoGebraFrame inst = instances.get(i);
				AppD app = inst.app;

				File currFile = app.getCurrentFile();
				if (currFile != null) {
					if (absPath.equals(currFile.getCanonicalPath())) {
						return inst;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean isIconified() {
		return getExtendedState() == Frame.ICONIFIED;
	}

	/**
	 * Returns the dropTarget listener for this frame.
	 * 
	 * @return the dropTarget listener for this frame.
	 */
	public FileDropTargetListener getDropTargetListener() {
		return dropTargetListener;
	}

	@Override
	public int print(Graphics g, PageFormat pf, int pageIndex)
			throws PrinterException {

		if (pageIndex > 0) {
			return NO_SUCH_PAGE;
		}

		Graphics2D g2d = (Graphics2D) g;
		g2d.translate(pf.getImageableX(), pf.getImageableY());

		double xScale = pf.getImageableWidth() / this.getWidth();
		double yScale = pf.getImageableHeight() / this.getHeight();
		double scale = Math.min(xScale, yScale);
		g2d.scale(scale, scale);

		this.printAll(g);

		return PAGE_EXISTS;

	}

	public static void checkCommandLineExport(final AppD app) {

		final CommandLineArguments args = app.getCommandLineArgs();

		if (args != null && args.containsArg("exportAnimation")
				&& args.containsArg("slider")) {

			String dpiStr = args.getStringValue("dpi");

			final int dpi = Integer
					.parseInt("".equals(dpiStr) ? "300" : dpiStr);

			final EuclidianView ev = app.getActiveEuclidianView();
			final String filename0 = args.getStringValue("exportAnimation");

			final String extension = StringUtil.getFileExtensionStr(filename0);

			final String filename = StringUtil.removeFileExtension(filename0);

			GeoElement export1 = app.getKernel()
					.lookupLabel(EuclidianView.EXPORT1);
			GeoElement export2 = app.getKernel()
					.lookupLabel(EuclidianView.EXPORT2);

			if ("gif".equals(extension) && export1 != null && export2 != null) {
				// maximize window
				// avoids clipping unless export size is especially large
				// needed for Animated GIF export from GeoGebraWeb
				// which runs this server-side
				Frame frame = app.getFrame();
				frame.setExtendedState(
						frame.getExtendedState() | Frame.MAXIMIZED_BOTH);
			}

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {

					String sliderName = args.getStringValue("slider");
					GeoElement slider = app.getKernel().lookupLabel(sliderName);

					if (slider == null || !slider.isGeoNumeric()
							|| !((GeoNumeric) slider).isSlider()) {
						Log.error(sliderName + " is not a slider");
						AppD.exit(0);
					}

					app.getKernel().getAnimatonManager().stopAnimation();
					GeoNumeric num = (GeoNumeric) slider;

					int type = num.getAnimationType();
					double min = num.getIntervalMin();
					double max = num.getIntervalMax();

					double val;

					double step;
					int n;

					switch (type) {
					case GeoElement.ANIMATION_DECREASING:
						step = -num.getAnimationStep();
						n = (int) ((max - min) / -step);
						if (DoubleUtil.isZero(((max - min) / -step) - n)) {
							n++;
						}
						if (n == 0) {
							n = 1;
						}
						val = max;
						break;
					case GeoElement.ANIMATION_OSCILLATING:
						step = num.getAnimationStep();
						n = (int) ((max - min) / step) * 2;
						if (DoubleUtil.isZero(((max - min) / step * 2) - n)) {
							n++;
						}
						if (n == 0) {
							n = 1;
						}
						val = min;
						break;
					default: // GeoElement.ANIMATION_INCREASING:
						// GeoElement.ANIMATION_INCREASING_ONCE:
						step = num.getAnimationStep();
						n = (int) ((max - min) / step);
						if (DoubleUtil.isZero(((max - min) / step) - n)) {
							n++;
						}
						if (n == 0) {
							n = 1;
						}
						val = min;
					}

					if ("gif".equals(extension)) {

						// "true" (default) or "false"
						String loop = args.getStringValue("loop");

						// time between frames in ms
						String delayStr = args.getStringValue("delay");

						final int delay = Integer.parseInt(
								"".equals(delayStr) ? "10" : delayStr);

						final AnimatedGifEncoder gifEncoder = new AnimatedGifEncoder();
						gifEncoder.setQuality(1);
						gifEncoder.start(new File(filename + ".gif"));

						gifEncoder.setDelay(delay); // miliseconds
						if (!"false".equals(loop)) {
							// repeat forever
							gifEncoder.setRepeat(0);
						}

						FrameCollector collector = new FrameCollector() {

							@Override
							public void addFrame(BufferedImage img) {
								gifEncoder.addFrame(img);

							}

							@Override
							public void finish() {
								gifEncoder.finish();

							}
						};

						app.exportAnimatedGIF(ev, collector, num, n, val, min,
								max, step);

						Log.debug("animated GIF exported successfully");

						AppD.exit(0);
					}

					double printingScale = ev.getPrintingScale();
					double exportScale = (printingScale * dpi) / 2.54
							/ ev.getXscale();
					boolean textAsShapes = true;
					boolean transparent = true;
					boolean useEMFplus = true;

					int pixelWidth = (int) Math
							.floor(ev.getExportWidth() * exportScale);
					int pixelHeight = (int) Math
							.floor(ev.getExportHeight() * exportScale);

					for (int i = 0; i < n; i++) {

						Log.debug("exporting frame " + i + "of " + n);

						// avoid values like 14.399999999999968
						val = DoubleUtil.checkDecimalFraction(val);

						num.setValue(val);
						num.updateRepaint();

						File file = new File(filename + i + "." + extension);

						GraphicExportDialog.export(extension,
								(EuclidianViewInterfaceD) ev, file, transparent,
								dpi, exportScale, textAsShapes, useEMFplus,
								pixelWidth, pixelHeight, app);

						val += step;

						if (val > max + Kernel.STANDARD_PRECISION
								|| val < min - Kernel.STANDARD_PRECISION) {
							val -= 2 * step;
							step *= -1;
						}

					}

					AppD.exit(0);
				}
			});

		}

		if (args != null && args.containsArg("export")) {
			final String filename = args.getStringValue("export");
			final String extension = StringUtil.getFileExtensionStr(filename);
			String dpiStr = args.getStringValue("dpi");

			final int dpi = Integer
					.parseInt("".equals(dpiStr) ? "300" : dpiStr);

			Log.debug("attempting to export: " + filename + " at " + dpiStr
					+ "dpi");

			// wait for EuclidianView etc to initialize before export
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {

					EuclidianViewInterfaceD ev = (EuclidianViewInterfaceD) app
							.getActiveEuclidianView();
					try {

						boolean export3D = false;

						// if 3D view exists, assume that we should export
						// that
						// (only PNG supported right now for 3D)
						if (app.isEuclidianView3Dinited()) {

							if ("png".equals(extension)) {
								Log.debug("exporting 3D View");
								ev = (EuclidianView3DD) app
										.getEuclidianView3D();

								export3D = true;
							}
						}

						double printingScale = ev.getPrintingScale();
						double exportScale = (printingScale * dpi) / 2.54
								/ ev.getXscale();
						final boolean transparent = true;
						final boolean textAsShapes = true;
						final boolean useEMFplus = true;
						int pixelWidth = (int) Math
								.floor(ev.getExportWidth() * exportScale);
						int pixelHeight = (int) Math
								.floor(ev.getExportHeight() * exportScale);

						int dpi2 = dpi;

						String maxSizeStr = args.getStringValue("maxSize");

						if (maxSizeStr != null && !"".equals(maxSizeStr)) {

							// ************************
							double maxSize = Integer.parseInt(maxSizeStr);
							Log.debug("desiredSize = " + maxSize);
							double size = Math.max(ev.getExportWidth(),
									ev.getExportHeight());
							Log.debug("size = " + size);

							exportScale = Math.min(
									maxSize / Math.floor(ev.getExportWidth()),
									maxSize / Math.floor(ev.getExportHeight()));
							Log.debug("exportScale = " + exportScale);
							pixelWidth = (int) Math
									.floor(ev.getExportWidth() * exportScale);
							Log.debug("pixelWidth = " + pixelWidth);
							pixelHeight = (int) Math
									.floor(ev.getExportHeight() * exportScale);
							Log.debug("pixelHeight = " + pixelHeight);

							dpi2 = (int) (exportScale * ev.getXscale() * 2.54
									/ printingScale);
							Log.debug("dpi2 = " + dpi2);
						}

						final File file = new File(filename);

						GraphicExportDialog.export(extension, ev, file,
								transparent, dpi2, exportScale, textAsShapes,
								useEMFplus, pixelWidth, pixelHeight, app);

						// HACK
						// do it again for 3D, first call initializes JOGL
						if (export3D) {
							GraphicExportDialog.export(extension, ev, file,
									transparent, dpi2, exportScale,
									textAsShapes, useEMFplus, pixelWidth,
									pixelHeight, app);
						}

						Log.debug("Graphics View exported successfully to "
								+ file.getAbsolutePath());

					} catch (Throwable t) {
						t.printStackTrace();
					}
					AppD.exit(0);
				}

			});

		}
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub

	}

	public static void doWithActiveInstance(NewInstanceListener listener) {
		if (activeInstance == null || activeInstance.getApplication() == null) {
			addNewInstanceListener(listener);
		} else {
			listener.newInstance(activeInstance);
		}
	}

}