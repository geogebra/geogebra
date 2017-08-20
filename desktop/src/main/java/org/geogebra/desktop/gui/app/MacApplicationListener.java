package org.geogebra.desktop.gui.app;

import java.io.File;

import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.main.AppD;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class MacApplicationListener
		implements com.apple.eawt.ApplicationListener {

	public static void initMacApplicationListener() {
		com.apple.eawt.Application app = new com.apple.eawt.Application();
		app.addApplicationListener(new MacApplicationListener());
	}

	/**
	 * Gets active instance of GeoGebra window. This method waits until an
	 * active instance was created by GeoGebra.main()
	 * 
	 * @return
	 */
	@SuppressFBWarnings({ "SWL_SLEEP_WITH_LOCK_HELD", "" })
	private synchronized static GeoGebraFrame getGGBInstance() {
		GeoGebraFrame wnd = null;
		while (wnd == null) {
			try {
				Thread.sleep(100);
				wnd = GeoGebraFrame.getActiveInstance();
			} catch (Exception e) {
				Log.debug("MacApplicationListener.getGGBInstance(): "
						+ e.getMessage());
				wnd = null;
			}
		}
		return wnd;
	}

	@Override
	public synchronized void handleQuit(com.apple.eawt.ApplicationEvent ev) {
		// quit all frames
		AppD app = getGGBInstance().getApplication();
		app.exitAll();
	}

	@Override
	public synchronized void handleAbout(
			com.apple.eawt.ApplicationEvent event) {
		event.setHandled(true);
		AppD app = getGGBInstance().getApplication();
		((GuiManagerD) app.getGuiManager()).showAboutDialog();
	}

	@Override
	public synchronized void handleOpenFile(
			com.apple.eawt.ApplicationEvent ev) {
		Log.debug("handleOpenFile event, filename: " + ev.getFilename());

		// open file
		String fileName = ev.getFilename();

		if (fileName != null) {
			File openFile = new File(fileName);
			if (openFile.exists()) {
				// get application instance
				GeoGebraFrame ggb = getGGBInstance();
				AppD app = ggb.getApplication();

				// open file
				File[] files = { openFile };
				// #1541
				boolean openInThisWindow = app.isSaved();
				((GuiManagerD) app.getGuiManager()).doOpenFiles(files,
						openInThisWindow);

				// make sure window is visible
				if (openInThisWindow) {
					ggb.setVisible(true);
				}
			}
		}
	}

	@Override
	public synchronized void handlePrintFile(
			com.apple.eawt.ApplicationEvent event) {
		Log.debug("handlePrintFile event, filename: " + event.getFilename());

		handleOpenFile(event);
		((GuiManagerD) getGGBInstance().getApplication().getGuiManager())
				.showPrintPreview();
	}

	@Override
	public synchronized void handleOpenApplication(
			com.apple.eawt.ApplicationEvent ev) {
		Log.debug("handleOpenApplication event, filename: " + ev.getFilename());

		// open file
		String fileName = ev.getFilename();
		if (fileName != null) {
			handleOpenFile(ev);
		} else {
			GeoGebraFrame
					.doWithActiveInstance(new NewInstanceListener() {
						@Override
						public void newInstance(GeoGebraFrame wnd) {
							if (!wnd.isShowing()) {
								wnd.setVisible(true);
							}
						}
					});
		}
	}

	@Override
	public synchronized void handlePreferences(
			com.apple.eawt.ApplicationEvent arg0) {
		Log.debug("handlePreferences event, filename: " + arg0.getFilename());
	}

	@Override
	public synchronized void handleReOpenApplication(
			com.apple.eawt.ApplicationEvent arg0) {
		Log.debug("handleReOpenApplication event, filename: "
				+ arg0.getFilename());
	}

}
