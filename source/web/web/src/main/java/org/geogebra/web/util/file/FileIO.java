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
