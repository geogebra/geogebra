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

package org.geogebra.common.jre.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.geogebra.common.util.debug.Log;

/**
 * Helper for downloading files
 *
 */
public class DownloadManager {

	// size of byte buffer to download / copy files
	private static final int BYTE_BUFFER_SIZE = 65536;

	/**
	 * Copies or downloads url to destination file.
	 * 
	 * @param src
	 *            source file
	 * @param dest
	 *            target file
	 * @throws IOException
	 *             if URL can't be opened or writing fails
	 */
	public static void copyURLToFile(URL src, File dest) throws IOException {

			// open input stream to src URL
			URLConnection srcConnection = src.openConnection();

			if (srcConnection.getContentLength() == 0) {
				// eg running from IDE
				Log.debug(src.getFile() + " not found");
				return;
			}
			// Check if this file has already been downloaded:
			if (srcConnection.getLastModified() <= dest.lastModified()
					&& srcConnection.getContentLength() == dest.length()) {
				// Yes. No extra download is required. :-)
				Log.debug(src.getFile() + " has already been downloaded to "
						+ dest.toString());
				return;
			}

			// Creating a user readable filename (trimming the directory name):
			boolean done = false;
			int i;
			for (i = src.getFile().length() - 1; i > 0 && !done; i--) {
				char c = src.getFile().charAt(i);

				// maybe DIRECTORY_SEPARATOR would be better
				if (c == '/' || c == '\\') {
					done = true;
				}
			}
		try (BufferedInputStream in = new BufferedInputStream(srcConnection.getInputStream());
			 FileOutputStream out = new FileOutputStream(dest)) {
			byte[] buf = new byte[BYTE_BUFFER_SIZE];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			boolean ok = dest.setLastModified(srcConnection.getLastModified());
			if (!ok) {
				Log.warn("Problem downloading " + src);
			}
		}
	}

}
