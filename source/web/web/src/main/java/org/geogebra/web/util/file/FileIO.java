package org.geogebra.web.util.file;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

/**
 * Helper class for file operations.
 */
public final class FileIO {

	private FileIO() {
		// helper class, instantiation not possible
	}

	/**
	 * Loads the content of a file into String.
	 * @param filename the name of the file to be read
	 * @return the content of the file
	 */
	public static String load(String filename) {
		Path filePath = Paths.get(filename);
		try {
			return StringUtil.join("\n",
					Files.readAllLines(filePath, StandardCharsets.UTF_8));
		} catch (Exception e) {
			Log.error("problem loading " + filePath.toAbsolutePath());
		}
		return null;
	}

}
