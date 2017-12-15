package org.geogebra.web.html5.main;

import java.util.HashMap;

/**
 * @author Zbynek
 */
public class GgbFile extends HashMap<String, String> {

	/** default value */
	private static final long serialVersionUID = 1L;

	public GgbFile duplicate() {
		GgbFile copy = new GgbFile();
		for (Entry<String, String> entry : entrySet()) {
			copy.put(entry.getKey(), entry.getValue());
		}
		return copy;
	}

}
