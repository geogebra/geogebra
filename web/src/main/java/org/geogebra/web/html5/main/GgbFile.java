package org.geogebra.web.html5.main;

import java.util.HashMap;

import org.geogebra.common.io.file.ZipFile;

/**
 * @author Zbynek
 */
public class GgbFile extends HashMap<String, String> implements ZipFile {
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
		for (Entry<String, String> entry : entrySet()) {
			copy.put(entry.getKey(), entry.getValue());
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

}
