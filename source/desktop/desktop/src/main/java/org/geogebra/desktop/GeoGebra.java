/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop;

import java.awt.Frame;
import java.awt.Toolkit;
import java.net.URL;

import org.geogebra.common.main.PreviewFeature;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.gui.app.GeoGebraFrame;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.GeoGebraServer;
import org.geogebra.desktop.util.GuiResourcesD;

public class GeoGebra {

	private static Frame splashFrame = null;

	protected GeoGebra() {
	}

	public static void main(String[] cmdArgs) {
		(new GeoGebra()).doMain(cmdArgs);
	}

	protected void doMain(String[] cmdArgs) {

		CommandLineArguments args = new CommandLineArguments(cmdArgs);

		boolean showSplash = true;
		if (!args.getBooleanValue("showSplash", true)) {
			showSplash = false;
		}
		if (args.containsArg("prerelease")) {
			PreviewFeature.setPreviewFeaturesEnabled(true);
			Log.warn("!!! Running with --prerelease");
		}
		if (args.containsArg("startHttpServer")) {
			new GeoGebraServer().start();
			return;
		}
		if (args.containsArg("help") || args.containsArg("proverhelp")
				|| args.containsArg("v")
				|| args.containsArg("regressionFile")) {
			showSplash = false;
		}

		if (showSplash) {
			// Show splash screen
			URL imageURL = GeoGebra.class.getResource(GuiResourcesD.SPLASH.getFilename());
			if (imageURL != null) {
				splashFrame = SplashWindow.splash(
						Toolkit.getDefaultToolkit().createImage(imageURL));
			} else {
				System.err.println("Splash image not found");
			}
		}

		// Start GeoGebra
		try {
			startGeoGebra(args);
		} catch (Throwable e) {
			Log.debug(e);
			System.err.flush();
			AppD.exit(10);
		}

		// Hide splash screen
		if (splashFrame != null) {
			splashFrame.setVisible(false);
		}
	}

	protected void startGeoGebra(CommandLineArguments args) {
		// create and open first GeoGebra window
		GeoGebraFrame.main(args);
	}

	/**
	 * Hide the splash window
	 */
	public static void hideSplash() {
		if (splashFrame != null) {
			splashFrame.setVisible(false);
		}
	}

}