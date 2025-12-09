/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.desktop.gui.app;

import java.awt.Desktop;
import java.awt.desktop.AboutEvent;
import java.awt.desktop.AboutHandler;
import java.awt.desktop.FilesEvent;
import java.awt.desktop.OpenFilesEvent;
import java.awt.desktop.OpenFilesHandler;
import java.awt.desktop.PrintFilesEvent;
import java.awt.desktop.PrintFilesHandler;
import java.awt.desktop.QuitEvent;
import java.awt.desktop.QuitHandler;
import java.awt.desktop.QuitResponse;
import java.awt.desktop.SystemEventListener;
import java.io.File;

import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.menubar.GeoGebraMenuBar;
import org.geogebra.desktop.main.AppD;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class MacApplicationListener implements QuitHandler, AboutHandler, OpenFilesHandler,
		SystemEventListener, PrintFilesHandler {

	/**
	 * Initialize the listener
	 */
	public void initMacApplicationListener() {
		Desktop desktop = Desktop.getDesktop();
		desktop.setQuitHandler(this);
		desktop.setAboutHandler(this);
		desktop.setOpenFileHandler(this);
		desktop.setPrintFileHandler(this);
		desktop.addAppEventListener(this);
	}

	/**
	 * Gets active instance of GeoGebra window. This method waits until an
	 * active instance was created by GeoGebra.main()
	 * 
	 * @return the frame
	 */
	@SuppressFBWarnings("SWL_SLEEP_WITH_LOCK_HELD")
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
	public void handleQuitRequestWith(QuitEvent e, QuitResponse response) {
		// quit all frames
		AppD app = getGGBInstance().getApplication();
		app.exitAll();
	}

	@Override
	public void handleAbout(AboutEvent e) {
		AppD app = getGGBInstance().getApplication();
		GeoGebraMenuBar.showAboutDialog(app);
	}

	@Override
	public void openFiles(OpenFilesEvent ev) {
		Log.debug("handleOpenFile event, filenames: " + ev.getFiles());

		openFirstFile(ev);

	}

	private void openFirstFile(FilesEvent ev) {
		// open file
		File openFile = ev.getFiles().isEmpty() ? null : ev.getFiles().get(0);

		if (openFile != null && openFile.exists()) {
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

	@Override
	public void printFiles(PrintFilesEvent event) {
		Log.debug("handlePrintFile event, filename: " + event.getFiles());

		openFirstFile(event);
		GeoGebraMenuBar.showPrintPreview(getGGBInstance().getApplication());
	}
}
