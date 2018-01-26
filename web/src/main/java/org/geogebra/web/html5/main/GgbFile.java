package org.geogebra.web.html5.main;

import java.util.HashMap;

/**
 * @author Zbynek
 */
public class GgbFile extends HashMap<String, String> {
	/** prefix for shared items in slides file */
	public static final String SHARED_PREFIX = "_shared/";
	/** structure of slides file */
	public static final String STRUCTURE_JSON = "structure.json";
	/** prefix for anonymous slide elements */
	public static final String SLIDE_PREFIX = "_slide";

	/** default value */
	private static final long serialVersionUID = 1L;

	/**
	 * @return a file with the same content
	 */
	public GgbFile duplicate() {
		GgbFile copy = new GgbFile();
		for (Entry<String, String> entry : entrySet()) {
			copy.put(entry.getKey(), entry.getValue());
		}
		return copy;
	}

}
