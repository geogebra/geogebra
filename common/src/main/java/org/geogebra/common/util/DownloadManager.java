/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.geogebra.common.main.App;

public class DownloadManager {

	// size of byte buffer to download / copy files
	private static final int BYTE_BUFFER_SIZE = 65536;

	/**
	 * Copies or downloads url to destination file.
	 */
	public static void copyURLToFile(URL src, File dest)
			throws Exception {
		BufferedInputStream in = null;
		FileOutputStream out = null;
		try {
			// open input stream to src URL
			URLConnection srcConnection = src.openConnection();
			// Application.debug(srcConnection.getLastModified() + " " +
			// dest.lastModified() + " " +
			// srcConnection.getContentLength() + " " + dest.length());

			if (srcConnection.getContentLength() == 0) {
				// eg running from Eclipse
				App.debug(src.getFile() + " not found");
				return;
			}
			// Check if this file has already been downloaded:
			if (srcConnection.getLastModified() <= dest.lastModified()
					&& srcConnection.getContentLength() == dest.length()) {
				// Yes. No extra download is required. :-)
				App.debug(src.getFile() + " has already been downloaded to "
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

			in = new BufferedInputStream(srcConnection.getInputStream());
			// if (in == null)
			// throw new NullPointerException("URL not found: " + src);

			// create output file
			out = new FileOutputStream(dest);

			byte[] buf = new byte[BYTE_BUFFER_SIZE];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			dest.setLastModified(srcConnection.getLastModified());
			in.close();
		} catch (Exception e) {
			try {
				in.close();
				out.close();
			} catch (Exception ex) {
				App.error(ex.toString());
			}
			// dest.delete();

			throw e;
		}
	}

	private static String tempDir = null;

	public static String getTempDir() {

		if (tempDir == null) {
			tempDir = System.getProperty("java.io.tmpdir");

			// Mac OS doesn't add "/" at the end of directory path name
			if (!tempDir.endsWith(File.separator)) {
				tempDir += File.separator;
			}
		}

		return tempDir;

	}

}
