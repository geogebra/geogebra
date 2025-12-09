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

package org.geogebra.desktop;

import java.awt.Frame;
import java.awt.Toolkit;
import java.net.URL;
import java.util.function.Supplier;

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

	/**
	 * Run the app.
	 * @param cmdArgs command line arguments
	 */
	public static void main(String[] cmdArgs) {
		doMain(cmdArgs, GeoGebraFrame::new);
	}

	/**
	 * @param cmdArgs Command line arguments
	 * @param frameFactory frame constructor
	 */
	public static void doMain(String[] cmdArgs, Supplier<GeoGebraFrame> frameFactory) {

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
			GeoGebraFrame.init(args, frameFactory.get());
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

	/**
	 * Hide the splash window
	 */
	public static void hideSplash() {
		if (splashFrame != null) {
			splashFrame.setVisible(false);
		}
	}

}