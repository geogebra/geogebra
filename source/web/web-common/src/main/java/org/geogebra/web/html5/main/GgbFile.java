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

package org.geogebra.web.html5.main;

import java.util.HashMap;

import org.geogebra.common.io.file.ZipFile;
import org.geogebra.web.html5.util.ArchiveEntry;

/**
 * @author Zbynek
 */
public class GgbFile extends HashMap<String, ArchiveEntry> implements ZipFile {
	/** prefix for shared items in slides file */
	public static final String SHARED_PREFIX = "_shared/";
	/** structure of slides file */
	public static final String STRUCTURE_JSON = "structure.json";
	/** prefix for anonymous slide elements */
	public static final String SLIDE_PREFIX = "_slide";

	/** default value */
	private static final long serialVersionUID = 1L;
	private final String id;

	private static int counter = 0;

	/**
	 * New GGB file with automatic ID.
	 */
	public GgbFile() {
		this.id = SLIDE_PREFIX + counter;
		incCounter();
	}

	/**
	 * New GGB file with given ID.
	 * 
	 * @param id
	 *            file ID
	 */
	public GgbFile(String id) {
		this.id = id;
	}

	private static void incCounter() {
		counter++;
	}

	/**
	 * @param copyId
	 *            id of the new file
	 * @return a file with the same content
	 */
	public GgbFile duplicate(String copyId) {
		GgbFile copy = copyId == null ? new GgbFile() : new GgbFile(copyId);
		for (Entry<String, ArchiveEntry> entry : entrySet()) {
			copy.put(entry.getKey(), entry.getValue().copy(entry.getKey()));
		}
		return copy;
	}

	/**
	 * @return file ID
	 */
	public String getID() {
		return id;
	}

	/**
	 * @return file counter; unique within session
	 */
	public static int getCounter() {
		return counter;
	}

	/**
	 * Add archive entry.
	 * @param key file name in the archive
	 * @param val entry content
	 */
	public void put(String key, String val) {
		put(key, new ArchiveEntry(key, val));
	}

}
