package org.geogebra.web.util.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.geogebra.common.util.Charsets;
import org.geogebra.common.util.debug.Log;

/**
 * Helper class for file operations.
 */
public final class FileIO {

	private static FileIO instance;

	private FileIO() {
	}

	private static FileIO getInstance() {
		if (instance == null) {
			instance = new FileIO();
		}
		return instance;
	}

	/**
	 * Loads the content of a file into String.
	 * @param filename the name of the file to be read
	 * @return the content of the file
	 */
	public static String load(String filename) {
		FileIO fileIO = getInstance();
		InputStream is = null;
		try {
			Path filePath = Paths.get(filename);
			is = Files.newInputStream(filePath);
			return fileIO.load(is);
		} catch (Exception e) {
			Log.error("problem loading " + filename);
		} finally {
			fileIO.tryToClose(is);
		}

		return null;
	}

	private String load(InputStream is) {
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(is, Charsets.getUtf8()));
		StringBuilder sb = new StringBuilder();

		String line;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

	private void tryToClose(InputStream inputStream) {
		try {
			if (inputStream != null) {
				inputStream.close();
			}
		} catch (IOException e) {
			Log.error("problem closing the input stream");
		}
	}
}
