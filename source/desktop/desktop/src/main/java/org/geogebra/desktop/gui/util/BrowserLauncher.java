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

package org.geogebra.desktop.gui.util;

import java.awt.Desktop;
import java.lang.reflect.Method;
import java.net.URI;

import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.main.AppD;

/////////////////////////////////////////////////////////
// Bare Bones Browser Launch                          //
// Version 1.5                                        //
// December 10, 2005                                  //
// Supports: Mac OS X, GNU/Linux, Unix, Windows XP    //
// Example Usage:                                     //
//  String url = "http://www.centerkey.com/";       //
//  BareBonesBrowserLaunch.openURL(url);            //
// Public Domain Software -- Free to Use as You Like  //
/////////////////////////////////////////////////////////

/**
 * Utility for launching browser
 *
 */
public class BrowserLauncher {

	/**
	 * @param url
	 *            website URL
	 */
	public static void openURL(String url) {
		Log.debug("opening URL:" + url);
		try {

			if (Desktop.isDesktopSupported()) {
				Desktop desktop = Desktop.getDesktop();
				desktop.browse(new URI(url));
				return;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// fallback
		try {
			if (AppD.MAC_OS) {
				Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
				Method openURL = fileMgr.getDeclaredMethod("openURL",
						new Class[] { String.class });
				openURL.invoke(null, new Object[] { url });
			} else if (AppD.WINDOWS) {
				// replace file:/c:/Program Files/etc
				// by file:///c:\Program Files\etc
				String fixedURL = url;
				if (fixedURL.indexOf("file:") == 0) { // local URL
					fixedURL = fixedURL.replaceAll("file:///", ""); // remove
					// file:///
					// from the start
					fixedURL = fixedURL.replaceAll("file:/", ""); // remove
																	// file:/
																	// from
					// the start

					fixedURL = fixedURL.replaceAll("[/\\\\]+", "\\" + "\\"); // replace
					// slashes
					// with
					// backslashes

					fixedURL = "file:///" + url; // put "file:///" back in
				}

				Runtime.getRuntime().exec(
						"rundll32.exe url.dll,FileProtocolHandler " + fixedURL);
			} else { // assume Unix or Linux
				String[] browsers = { "xdg-open", "firefox", "google-chrome",
						"chromium-browser", "opera", "konqueror", "epiphany",
						"safari", "mozilla", "netscape", "seamonkey" };
				String browser = null;
				for (int count = 0; count < browsers.length
						&& browser == null; count++) {
					if (Runtime.getRuntime()
							.exec(new String[] { "which", browsers[count] })
							.waitFor() == 0) {
						browser = browsers[count];
					}
				}
				if (browser == null) {
					Log.error("Could not find web browser");
					return;

				}
				Log.debug("Using browser " + browser);
				Runtime.getRuntime().exec(new String[] { browser, url });
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
