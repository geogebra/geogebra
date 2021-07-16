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

	/**
	 * @param casParser
	 *            casParser
	 */
	public CASgiacD(CASparser casParser) {
		super(casParser);
	}

	private static boolean giacLoaded = false;

	static {
		try {
			Log.debug("Loading Giac dynamic library");

			String file;

			if (AppD.MAC_OS) {
				// Architecture on OSX seems to be x86_64, but let's make sure
				file = "javagiac";
			} else if ("AMD64".equals(System.getenv("PROCESSOR_ARCHITECTURE"))
					// System.getenv("PROCESSOR_ARCHITECTURE") can return null
					// (seems to
					// happen on linux)
					|| "amd64".equals(System.getProperty("os.arch"))) {
				file = "javagiac64";
			} else {
				file = "javagiac";
			}

			Log.debug("Loading Giac dynamic library: " + file);

			// When running from local jars we can load the library files from
			// inside a jar like this
			MyClassPathLoader loader = new MyClassPathLoader();
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

		} catch (Exception e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
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

	@SuppressWarnings("deprecation")
	@Override
	final protected void stopThread(Thread thread) {
		// thread.interrupt() doesn't seem to stop it, so add this for
		// good measure:
		thread.stop();
	}

}
