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

package org.geogebra.desktop.cas.giac;

import org.geogebra.common.cas.CASparser;
import org.geogebra.common.jre.cas.giac.CASgiacJre;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.main.AppD;

/**
 * @author michael
 * 
 */
public class CASgiacD extends CASgiacJre {

	private static boolean giacLoaded = false;

	/**
	 * @param casParser
	 *            casParser
	 */
	public CASgiacD(CASparser casParser) {
		super(casParser);
	}

	static {
		try {
			Log.debug("Loading Giac dynamic library");

			String file;

			if (AppD.MAC_OS) {
				if ("aarch64".equals(System.getProperty("os.arch"))) {
					file = "javagiac-arm64";
				} else {
					// Architecture on OSX seems to be x86_64, but let's make sure
					file = "javagiac";
				}
			} else {
				file = "javagiac64";
			}

			Log.debug("Loading Giac dynamic library: " + file);

			// When running from local jars we can load the library files from
			// inside a jar like this
			NativeLibClassPathLoader loader = new NativeLibClassPathLoader();
			giacLoaded = loader.loadLibrary(file);

			if (!giacLoaded) {
				// "classic" method
				// for Webstart, eg loading
				// javagiac.dll from javagiac-win32.jar
				// javagiac64.dll from javagiac-win64.jar
				// libjavagiac.so from javagiac-linux32.jar
				// libjavagiac64.so from javagiac-linux64.jar
				// libjavagiac.jnilib from javagiac-mac.jar

				Log.debug("Trying to load Giac library (alternative method)");
				System.loadLibrary(file);
				giacLoaded = true;

			}

		} catch (Throwable e) {
			Log.debug(e);
		}

		if (giacLoaded) {
			Log.debug("Giac dynamic library loaded");
			App.setCASVersionString("Giac/JNI");
		} else {
			Log.debug("Failed to load Giac dynamic library");
			App.setCASVersionString("Giac");
		}
	}

	@Override
	final protected boolean useThread() {
		return !AppD.LINUX;
	}

}
