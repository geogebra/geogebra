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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.UtilD;

/**
 * Adapted from
 * http://www.jotschi.de/Uncategorized/2011/09/26/jogl2-jogamp-classpathloader
 * -for-native-libraries.html
 *
 */
public class NativeLibClassPathLoader {

	/**
	 * Loads the given library with the libname from the classpath root
	 * 
	 * @param libname
	 *            eg javagiac or javagiac64
	 * @return success
	 */
	public boolean loadLibrary(String libname) {

		String extension, prefix;
		if (AppD.WINDOWS) {
			prefix = "";
			extension = ".dll";
		} else if (AppD.MAC_OS) {
			prefix = "lib";
			extension = ".jnilib";
		} else {
			// assume Linux
			prefix = "lib";
			extension = ".so";
		}

		String filename = prefix + libname + extension;
		String fname = prefix + libname + Math.random() + extension;
		try (InputStream ins = ClassLoader.getSystemResourceAsStream(filename)) {
			if (ins == null) {
				Log.error(filename + " not found");
				return false;
			}

			// Math.random() to avoid problems with 2 instances
			File tmpFile = writeTmpFile(ins, fname);
			System.load(tmpFile.getAbsolutePath());
			UtilD.delete(tmpFile);
		} catch (IOException e) {
			Log.debug(e);
			Log.debug("error loading: " + fname);
			return false;
		}

		return true;
	}

	/**
	 * Write the content of the inputstream into a tempfile with the given
	 * filename
	 * 
	 * @param ins input stream
	 * @param filename filename
	 * @throws FileNotFoundException when file not found
	 * @throws IOException when other I/O problem occurs
	 */
	private static File writeTmpFile(InputStream ins, String filename)
			throws IOException {

		File tmpFile = new File(System.getProperty("java.io.tmpdir"), filename);
		UtilD.delete(tmpFile);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(tmpFile);

			byte[] buffer = new byte[1024];
			int len;
			while ((len = ins.read(buffer)) != -1) {

				fos.write(buffer, 0, len);
			}
		} finally {
			if (ins != null) {

				// need try/catch to be sure fos gets closed
				try {
					ins.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				fos.close();
			}
		}
		return tmpFile;
	}

}